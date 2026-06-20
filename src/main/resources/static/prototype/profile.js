const { createApp } = Vue;

createApp({
  data() {
    return {
      user: {
        initial: "我",
        name: "林夏",
        gender: 0,
        birthday: "1998-08-16",
        username: "linxia",
        email: "linxia@meowkanban.local",
        joinedAt: "2026-04-18"
      },
      activeDialog: null,
      profileForm: {
        initial: "",
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
      stats: [
        { label: "管理看板", value: 6 },
        { label: "参与任务", value: 38 },
        { label: "本周操作", value: 24 }
      ],
      boards: [
        {
          id: 1,
          name: "MeowKanban",
          shortName: "MK",
          description: "轻量项目看板，覆盖产品、前端、后端和测试任务。",
          role: "owner",
          status: "私有",
          statusClass: "blocked",
          updatedAt: "今天 16:20",
          accent: "teal",
          href: "./index.html?boardId=1"
        },
        {
          id: 2,
          name: "移动端改版",
          shortName: "移动",
          description: "注册、登录、任务详情和移动端信息架构整理。",
          role: "admin",
          status: "私有",
          statusClass: "blocked",
          updatedAt: "昨天 18:05",
          accent: "violet",
          href: "./index.html?boardId=2"
        },
        {
          id: 3,
          name: "运营需求池",
          shortName: "运营",
          description: "活动素材、数据追踪和复盘动作集中管理。",
          role: "member",
          status: "公开",
          statusClass: "normal",
          updatedAt: "周二 09:40",
          accent: "amber",
          href: "./index.html?boardId=3"
        }
      ],
      activityFilter: "all",
      activityFilters: [
        { label: "全部", value: "all" },
        { label: "看板", value: "board" },
        { label: "任务", value: "task" }
      ],
      activities: [
        {
          id: 1,
          type: "board",
          action: "更新看板封面",
          detail: "在 MeowKanban 中替换了看板封面资源。",
          time: "今天 16:20"
        },
        {
          id: 2,
          type: "task",
          action: "移动任务",
          detail: "将 MK-126 从进行中移动到验收。",
          time: "今天 14:08"
        },
        {
          id: 3,
          type: "board",
          action: "收藏看板",
          detail: "收藏了移动端改版看板，便于快速访问。",
          time: "昨天 18:05"
        },
        {
          id: 4,
          type: "task",
          action: "新增评论",
          detail: "在 MK-118 中补充了拖拽交互验收标准。",
          time: "昨天 15:32"
        },
        {
          id: 5,
          type: "board",
          action: "邀请成员",
          detail: "邀请周宁加入运营需求池看板。",
          time: "周二 09:40"
        }
      ]
    };
  },
  computed: {
    profileFields() {
      return [
        { label: "账号", value: this.user.username },
        { label: "邮箱", value: this.user.email },
        { label: "性别", value: this.genderLabel(this.user.gender) },
        { label: "生日", value: this.user.birthday },
        { label: "加入时间", value: this.user.joinedAt }
      ];
    },
    filteredActivities() {
      if (this.activityFilter === "all") {
        return this.activities;
      }
      return this.activities.filter((activity) => activity.type === this.activityFilter);
    }
  },
  methods: {
    openProfileDialog() {
      this.profileForm = {
        initial: this.user.initial,
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
    saveProfile() {
      this.user = {
        ...this.user,
        initial: this.profileForm.initial || "我",
        name: this.profileForm.name || this.user.name,
        gender: this.profileForm.gender,
        birthday: this.profileForm.birthday
      };
      this.closeDialog();
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
      if (gender === -1) {
        return "未知";
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
