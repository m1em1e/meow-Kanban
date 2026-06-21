const { createApp } = Vue;

const accentClasses = ["teal", "violet", "amber", "rose"];
const useMockProfile = window.location.protocol === "file:"
  || window.location.pathname.includes("/prototype/");

const mockUser = {
  id: 1,
  username: "linxia",
  nickname: "林夏",
  email: "linxia@meowkanban.local",
  gender: 0,
  birthday: "1998-08-16",
  avatarResourceId: null,
  joinedTime: "2026-04-18 09:00:00",
  status: 1
};

const mockBoards = [
  {
    id: 1,
    name: "MeowKanban",
    description: "轻量项目看板，覆盖产品、前端、后端和测试任务。",
    ownerId: 1,
    visibility: 0
  },
  {
    id: 2,
    name: "移动端改版",
    description: "注册、登录、任务详情和移动端信息架构整理。",
    ownerId: 1,
    visibility: 0
  },
  {
    id: 3,
    name: "运营需求池",
    description: "活动素材、数据追踪和复盘动作集中管理。",
    ownerId: 2,
    visibility: 1
  }
];

const mockActivities = [
  {
    id: 1,
    boardId: 1,
    boardTitle: "MeowKanban",
    action: "更新看板封面",
    createTime: "2026-06-20 16:20:00"
  },
  {
    id: 2,
    boardId: 1,
    boardTitle: "MeowKanban",
    action: "移动任务",
    createTime: "2026-06-20 14:08:00"
  },
  {
    id: 3,
    boardId: 2,
    boardTitle: "移动端改版",
    action: "新增评论",
    createTime: "2026-06-19 15:32:00"
  }
];

createApp({
  data() {
    return {
      userId: null,
      currentUserId: null,
      user: {
        id: null,
        initial: "我",
        avatarResourceId: null,
        avatarUrl: "",
        name: "",
        gender: -1,
        birthday: "",
        username: "",
        email: "",
        joinedAt: "-"
      },
      profileLoading: true,
      boardsLoading: true,
      activitiesLoading: true,
      avatarUploading: false,
      avatarPreviewUrl: "",
      profileError: "",
      boardsError: "",
      activitiesError: "",
      activeDialog: null,
      profileForm: {
        name: "",
        gender: -1,
        birthday: ""
      },
      settingsForm: {
        currentPassword: "",
        newPassword: "",
        notification: "important",
        weeklyDigest: true
      },
      boards: [],
      activityFilter: "all",
      activityFilters: [
        { label: "全部", value: "all" },
        { label: "看板", value: "board" },
        { label: "任务", value: "task" }
      ],
      activities: []
    };
  },
  computed: {
    profileFields() {
      return [
        { label: "账号", value: this.user.username || "-" },
        { label: "邮箱", value: this.user.email || "-" },
        { label: "性别", value: this.genderLabel(this.user.gender) },
        { label: "生日", value: this.user.birthday || "-" },
        { label: "加入时间", value: this.user.joinedAt || "-" }
      ];
    },
    stats() {
      return [
        { label: "管理看板", value: this.boards.filter((board) => board.role === "owner").length },
        { label: "参与看板", value: this.boards.length },
        { label: "近期操作", value: this.activities.length }
      ];
    },
    filteredActivities() {
      if (this.activityFilter === "all") {
        return this.activities;
      }
      return this.activities.filter((activity) => activity.type === this.activityFilter);
    },
    canEditAvatar() {
      return String(this.userId) === String(this.currentUserId);
    }
  },
  mounted() {
    const root = document.getElementById("profileApp");
    this.userId = String(root?.dataset.userId || "1");
    this.currentUserId = String(root?.dataset.currentUserId || (useMockProfile ? this.userId : ""));
    if (!useMockProfile) {
      window.MeowKanbanAuth?.requireToken();
    }
    this.loadProfile();
    this.loadBoards();
    this.loadActivities();
  },
  beforeUnmount() {
    if (this.avatarPreviewUrl) {
      URL.revokeObjectURL(this.avatarPreviewUrl);
    }
  },
  methods: {
    async loadProfile() {
      this.profileLoading = true;
      this.profileError = "";

      try {
        const profile = useMockProfile
          ? mockUser
          : await this.fetchApi(`/api/v1/user/profile?id=${encodeURIComponent(this.userId)}`);
        this.user = this.normalizeUser(profile);
      } catch (error) {
        this.profileError = error.message || "用户信息加载失败";
      } finally {
        this.profileLoading = false;
      }
    },
    async loadBoards() {
      this.boardsLoading = true;
      this.boardsError = "";

      try {
        const boards = useMockProfile
          ? mockBoards
          : await this.fetchApi(`/api/v1/board/recent?id=${encodeURIComponent(this.userId)}`);
        this.boards = boards.map(this.normalizeBoard);
      } catch (error) {
        this.boardsError = error.message || "我的看板加载失败";
      } finally {
        this.boardsLoading = false;
      }
    },
    async loadActivities() {
      this.activitiesLoading = true;
      this.activitiesError = "";

      try {
        const pageResult = useMockProfile
          ? { records: mockActivities }
          : await this.fetchApi(`/api/v1/activity/recent-activity?id=${encodeURIComponent(this.userId)}&pageIndex=1&pageSize=10`);
        const records = Array.isArray(pageResult.records) ? pageResult.records : [];
        this.activities = records.map(this.normalizeActivity);
      } catch (error) {
        this.activitiesError = error.message || "操作记录加载失败";
      } finally {
        this.activitiesLoading = false;
      }
    },
    async fetchApi(url, options = {}) {
      const { headers = {}, ...requestOptions } = options;
      const apiFetch = window.MeowKanbanAuth?.fetch || fetch;
      const response = await apiFetch(url, {
        ...requestOptions,
        headers: {
          Accept: "application/json",
          ...headers
        }
      });

      let result = null;
      try {
        result = await response.json();
      } catch (error) {
        result = null;
      }

      if (!response.ok || !result || result.code !== 1) {
        throw new Error(result?.msg || "请求失败");
      }
      return result.data;
    },
    normalizeUser(profile) {
      const name = profile.nickname || profile.username || "用户";
      return {
        id: profile.id,
        initial: this.createShortName(name),
        avatarResourceId: profile.avatarResourceId || null,
        avatarUrl: profile.avatarResourceId ? `/api/v1/resource/${encodeURIComponent(profile.avatarResourceId)}` : "",
        name,
        gender: profile.gender ?? -1,
        birthday: profile.birthday || "",
        username: profile.username || "",
        email: profile.email || "",
        joinedAt: this.formatTime(profile.joinedTime || profile.createdTime)
      };
    },
    normalizeBoard(board) {
      const id = String(board.id);
      const name = board.name || "未命名看板";
      const isOwner = String(board.ownerId) === String(this.userId);
      const visibility = Number(board.visibility) === 1 ? "公开" : "私有";
      const coverUrl = board.coverResourceId ? `/api/v1/resource/${encodeURIComponent(board.coverResourceId)}` : "";
      return {
        id,
        name,
        shortName: this.createShortName(name),
        coverUrl,
        description: board.description || "",
        role: isOwner ? "owner" : "member",
        status: visibility,
        statusClass: visibility === "公开" ? "normal" : "blocked",
        updatedAt: "最近参与",
        accent: accentClasses[this.accentIndex(id)],
        href: useMockProfile ? `./index.html?boardId=${encodeURIComponent(id)}` : `/detail/${encodeURIComponent(id)}`
      };
    },
    normalizeActivity(activity) {
      const boardTitle = activity.boardTitle || "看板";
      return {
        id: activity.id,
        type: "task",
        action: this.activityActionLabel(activity.action),
        detail: `在 ${boardTitle} 中产生了任务动态。`,
        time: this.formatTime(activity.createTime)
      };
    },
    activityActionLabel(action) {
      const labels = {
        create: "创建任务",
        update: "更新任务",
        move: "移动任务",
        comment: "新增评论",
        attach: "上传附件",
        delete: "删除任务"
      };
      return labels[action] || action || "任务操作";
    },
    formatTime(value) {
      if (!value) {
        return "-";
      }

      const normalized = String(value).replace("T", " ");
      return normalized.length > 16 ? normalized.slice(0, 16) : normalized;
    },
    createShortName(name) {
      return Array.from(String(name).trim() || "用户").slice(0, 2).join("").toUpperCase();
    },
    accentIndex(id) {
      const value = String(id || "0");
      const lastDigits = value.match(/\d{1,6}$/)?.[0] || "0";
      return Number(lastDigits) % accentClasses.length;
    },
    openProfileDialog() {
      this.profileForm = {
        name: this.user.name,
        gender: this.user.gender,
        birthday: this.user.birthday
      };
      this.activeDialog = "profile";
    },
    openSettingsDialog() {
      this.settingsForm = {
        ...this.settingsForm,
        currentPassword: "",
        newPassword: ""
      };
      this.activeDialog = "settings";
    },
    closeDialog() {
      this.activeDialog = null;
    },
    openAvatarPicker() {
      if (!this.canEditAvatar || this.avatarUploading) {
        return;
      }
      this.$refs.avatarInput?.click();
    },
    async handleAvatarChange(event) {
      const file = event.target.files?.[0];
      event.target.value = "";

      if (!file) {
        return;
      }
      if (!file.type || !file.type.startsWith("image/")) {
        this.profileError = "头像文件必须是图片";
        return;
      }

      this.avatarUploading = true;
      this.profileError = "";

      try {
        if (useMockProfile) {
          if (this.avatarPreviewUrl) {
            URL.revokeObjectURL(this.avatarPreviewUrl);
          }
          this.avatarPreviewUrl = URL.createObjectURL(file);
          this.user = {
            ...this.user,
            avatarUrl: this.avatarPreviewUrl
          };
          return;
        }

        const formData = new FormData();
        formData.append("file", file);
        const resource = await this.fetchApi("/api/v1/resource/upload", {
          method: "POST",
          body: formData
        });
        const profile = await this.fetchApi("/api/v1/user/profile", {
          method: "PUT",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify({
            id: this.userId,
            nickname: this.user.name,
            gender: this.user.gender,
            birthday: this.user.birthday || null,
            avatarResourceId: resource.id
          })
        });
        this.user = this.normalizeUser(profile);
      } catch (error) {
        this.profileError = error.message || "头像上传失败";
      } finally {
        this.avatarUploading = false;
      }
    },
    async saveProfile() {
      if (useMockProfile) {
        const name = this.profileForm.name || this.user.name;
        this.user = {
          ...this.user,
          initial: this.createShortName(name),
          name,
          gender: this.profileForm.gender,
          birthday: this.profileForm.birthday
        };
        this.closeDialog();
        return;
      }

      try {
        const profile = await this.fetchApi("/api/v1/user/profile", {
          method: "PUT",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify({
            id: this.userId,
            nickname: this.profileForm.name,
            gender: this.profileForm.gender,
            birthday: this.profileForm.birthday || null
          })
        });
        this.user = this.normalizeUser(profile);
        this.closeDialog();
      } catch (error) {
        this.profileError = error.message || "保存资料失败";
      }
    },
    saveSettings() {
      this.settingsForm.currentPassword = "";
      this.settingsForm.newPassword = "";
      this.closeDialog();
    },
    genderLabel(gender) {
      if (gender === 1) {
        return "男";
      }
      if (gender === 0) {
        return "女";
      }
      return "未知";
    },
    roleLabel(role) {
      const labels = {
        owner: "拥有者",
        admin: "管理员",
        member: "成员",
        viewer: "只读"
      };
      return labels[role] || role;
    },
    roleClass(role) {
      const classes = {
        owner: "normal",
        admin: "urgent",
        member: "",
        viewer: "blocked"
      };
      return classes[role] || "";
    }
  }
}).mount("#profileApp");
