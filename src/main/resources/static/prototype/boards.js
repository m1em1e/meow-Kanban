const { createApp } = Vue;

const accentClasses = ["teal", "violet", "amber", "rose"];
const useMockBoards = window.location.protocol === "file:"
  || window.location.pathname.includes("/prototype/");
const boardPageDefaults = Object.freeze({
  pageIndex: 1,
  pageSize: 10
});

const createBoardHref = (id) => {
  const encodedId = encodeURIComponent(id);
  return useMockBoards ? `./index.html?boardId=${encodedId}` : `/detail/${encodedId}`;
};

const mockBoards = [
  {
    id: 1,
    name: "MeowKanban",
    description: "轻量项目看板，覆盖产品、前端、后端和测试任务。",
    visibility: 0,
    favorited: true,
    role: "owner",
    updatedAt: "今天 16:20"
  },
  {
    id: 2,
    name: "移动端改版",
    description: "注册、登录、任务详情和移动端信息架构整理。",
    visibility: 0,
    favorited: false,
    role: "admin",
    updatedAt: "昨天 18:05"
  },
  {
    id: 3,
    name: "运营需求池",
    description: "活动素材、数据追踪和复盘动作集中管理。",
    visibility: 1,
    role: "member",
    updatedAt: "周二 09:40"
  },
  {
    id: 4,
    name: "只读归档",
    description: "已完成项目归档，仅用于检索历史任务和结论。",
    visibility: 0,
    role: "viewer",
    updatedAt: "6 月 12 日"
  },
  {
    id: 5,
    name: "设计系统",
    description: "组件规范、颜色变量、表单状态和图标使用约定。",
    visibility: 1,
    role: "admin",
    updatedAt: "6 月 11 日"
  },
  {
    id: 6,
    name: "后端接口联调",
    description: "认证、看板、任务、附件和活动日志接口联调计划。",
    visibility: 0,
    role: "owner",
    updatedAt: "6 月 10 日"
  },
  {
    id: 7,
    name: "测试回归",
    description: "冒烟用例、回归范围、缺陷复测和发布前检查。",
    visibility: 0,
    role: "member",
    updatedAt: "6 月 9 日"
  },
  {
    id: 8,
    name: "内容排期",
    description: "产品公告、帮助文档、更新日志和运营文章排期。",
    visibility: 1,
    role: "viewer",
    updatedAt: "6 月 8 日"
  },
  {
    id: 9,
    name: "数据看板",
    description: "关键指标、使用趋势、任务吞吐和阻塞统计。",
    visibility: 0,
    role: "admin",
    updatedAt: "6 月 7 日"
  },
  {
    id: 10,
    name: "客户反馈",
    description: "收集试用反馈、优先级判断和需求归类。",
    visibility: 0,
    role: "member",
    updatedAt: "6 月 6 日"
  },
  {
    id: 11,
    name: "安全加固",
    description: "登录策略、权限校验、敏感操作和审计记录。",
    visibility: 0,
    role: "owner",
    updatedAt: "6 月 5 日"
  },
  {
    id: 12,
    name: "发布准备",
    description: "版本清单、数据库脚本、部署检查和回滚方案。",
    visibility: 0,
    role: "admin",
    updatedAt: "6 月 4 日"
  },
  {
    id: 13,
    name: "知识库整理",
    description: "项目术语、常见问题、开发约定和交接资料。",
    visibility: 1,
    role: "viewer",
    updatedAt: "6 月 3 日"
  },
  {
    id: 14,
    name: "集成实验",
    description: "第三方服务、消息通知和导入导出能力验证。",
    visibility: 0,
    role: "member",
    updatedAt: "6 月 2 日"
  }
];

createApp({
  data() {
    return {
      query: "",
      filter: "all",
      filters: [
        { label: "全部", value: "all" },
        { label: "我管理", value: "managed" },
        { label: "只读", value: "viewer" }
      ],
      boards: [],
      pageIndex: boardPageDefaults.pageIndex,
      pageSize: boardPageDefaults.pageSize,
      total: 0,
      pages: 0,
      sortTarget: 2,
      sortType: 0,
      loading: false,
      loadError: "",
      createDialogOpen: false,
      createSaving: false,
      createError: "",
      createForm: {
        name: "",
        description: "",
        visibility: 0,
        coverFile: null,
        coverPreviewUrl: ""
      },
      favoriteSavingIds: [],
      searchTimer: null
    };
  },
  computed: {
    filteredBoards() {
      return this.boards.filter((board) => {
        return this.filter === "all"
          || (this.filter === "managed" && ["owner", "admin"].includes(board.role))
          || (this.filter === "viewer" && board.role === "viewer");
      });
    },
    managedCount() {
      return this.boards.filter((board) => ["owner", "admin"].includes(board.role)).length;
    },
    viewerCount() {
      return this.boards.filter((board) => board.role === "viewer").length;
    },
    canPrevPage() {
      return this.pageIndex > 1 && !this.loading;
    },
    canNextPage() {
      return this.pageIndex < this.pages && !this.loading;
    }
  },
  watch: {
    query() {
      window.clearTimeout(this.searchTimer);
      this.searchTimer = window.setTimeout(() => {
        this.goToPage(1);
      }, 260);
    },
    filter() {
      this.goToPage(1);
    }
  },
  mounted() {
    if (!useMockBoards) {
      window.MeowKanbanAuth?.requireToken();
    }
    this.loadBoards();
  },
  beforeUnmount() {
    window.clearTimeout(this.searchTimer);
    this.revokeCreateCoverPreview();
  },
  methods: {
    async loadBoards() {
      if (this.loading) {
        return;
      }

      this.loading = true;
      this.loadError = "";

      try {
        const pageResult = useMockBoards
          ? this.loadMockBoards()
          : await this.fetchBoards();

        this.boards = pageResult.records.map(this.normalizeBoard);
        this.total = Number(pageResult.total) || 0;
        this.pageIndex = Number(pageResult.pageIndex) || 1;
        this.pageSize = Number(pageResult.pageSize) || 10;
        this.pages = Number(pageResult.pages) || 0;
      } catch (error) {
        this.loadError = error.message || "看板加载失败";
      } finally {
        this.loading = false;
      }
    },
    loadMockBoards() {
      const keyword = this.query.trim().toLowerCase();
      const filtered = mockBoards.filter((board) => {
        const matchesKeyword = !keyword
          || board.name.toLowerCase().includes(keyword)
          || board.description.toLowerCase().includes(keyword);
        const matchesFilter = this.filter === "all"
          || (this.filter === "managed" && ["owner", "admin"].includes(board.role))
          || (this.filter === "viewer" && board.role === "viewer");
        return matchesKeyword && matchesFilter;
      });
      const start = (this.pageIndex - 1) * this.pageSize;
      const records = filtered.slice(start, start + this.pageSize);

      return {
        records,
        total: filtered.length,
        pageIndex: this.pageIndex,
        pageSize: this.pageSize,
        pages: Math.ceil(filtered.length / this.pageSize)
      };
    },
    async fetchBoards() {
      const params = new URLSearchParams({
        pageIndex: String(this.pageIndex),
        pageSize: String(this.pageSize),
        sortTarget: String(this.sortTarget),
        sortType: String(this.sortType)
      });

      const keyword = this.query.trim();
      if (keyword) {
        params.set("keyword", keyword);
      }

      const apiFetch = window.MeowKanbanAuth?.fetch || fetch;
      const response = await apiFetch(`/api/v1/board/list?${params.toString()}`, {
        headers: { Accept: "application/json" }
      });

      const result = await this.readApiResult(response, "看板加载失败");
      if (!result.data || !Array.isArray(result.data.records)) {
        throw new Error(result.msg || "看板加载失败");
      }

      return result.data;
    },
    async readApiResult(response, fallbackMessage) {
      let result = null;
      let text = "";
      try {
        text = await response.text();
        result = text ? JSON.parse(text) : null;
      } catch (error) {
        result = null;
      }

      if (!response.ok || !result || result.code !== 1) {
        throw new Error(result?.msg || fallbackMessage);
      }

      Object.defineProperty(result, "_rawText", {
        value: text,
        enumerable: false
      });
      return result;
    },
    openCreateBoardDialog() {
      this.createError = "";
      this.createDialogOpen = true;
    },
    closeCreateBoardDialog() {
      if (this.createSaving) {
        return;
      }
      this.createDialogOpen = false;
      this.resetCreateForm();
    },
    resetCreateForm() {
      this.revokeCreateCoverPreview();
      this.createError = "";
      this.createForm = {
        name: "",
        description: "",
        visibility: 0,
        coverFile: null,
        coverPreviewUrl: ""
      };
      if (this.$refs.createCoverInput) {
        this.$refs.createCoverInput.value = "";
      }
    },
    handleCreateCoverChange(event) {
      const file = event.target.files?.[0] || null;
      this.createError = "";

      if (!file) {
        this.clearCreateCover();
        return;
      }

      if (!file.type || !file.type.startsWith("image/")) {
        this.createError = "封面文件必须是图片";
        event.target.value = "";
        this.clearCreateCover();
        return;
      }

      this.revokeCreateCoverPreview();
      this.createForm.coverFile = file;
      this.createForm.coverPreviewUrl = URL.createObjectURL(file);
    },
    clearCreateCover() {
      this.revokeCreateCoverPreview();
      this.createForm.coverFile = null;
      this.createForm.coverPreviewUrl = "";
      if (this.$refs.createCoverInput) {
        this.$refs.createCoverInput.value = "";
      }
    },
    revokeCreateCoverPreview() {
      if (this.createForm.coverPreviewUrl) {
        URL.revokeObjectURL(this.createForm.coverPreviewUrl);
      }
    },
    async submitCreateBoard() {
      const name = this.createForm.name.trim();
      const description = this.createForm.description.trim();
      if (!name) {
        this.createError = "请输入看板名称";
        return;
      }

      this.createSaving = true;
      this.createError = "";

      try {
        if (useMockBoards) {
          this.createMockBoard(name, description);
        } else {
          const userId = await this.fetchCurrentUserId();
          const coverResourceId = await this.uploadCreateCover();
          await this.createRemoteBoard(name, description, coverResourceId, userId);
        }

        this.query = "";
        this.filter = "all";
        this.pageIndex = 1;
        this.createDialogOpen = false;
        this.resetCreateForm();
        await this.loadBoards();
      } catch (error) {
        this.createError = error.message || "看板创建失败";
      } finally {
        this.createSaving = false;
      }
    },
    createMockBoard(name, description) {
      mockBoards.unshift({
        id: `mock-${Date.now()}`,
        name,
        description: description || "新的协作看板。",
        coverPreviewUrl: this.createForm.coverPreviewUrl,
        visibility: Number(this.createForm.visibility),
        role: "owner",
        updatedAt: "刚刚"
      });
    },
    async uploadCreateCover() {
      if (!this.createForm.coverFile) {
        return null;
      }

      const formData = new FormData();
      formData.append("file", this.createForm.coverFile);

      const apiFetch = window.MeowKanbanAuth?.fetch || fetch;
      const response = await apiFetch("/api/v1/resource/upload", {
        method: "POST",
        headers: {
          Accept: "application/json"
        },
        body: formData
      });
      const result = await this.readApiResult(response, "封面上传失败");
      const resourceId = this.extractExactDataId(result);
      if (!resourceId) {
        throw new Error("封面上传失败");
      }

      return resourceId;
    },
    extractExactDataId(result) {
      const rawText = result?._rawText || "";
      const match = rawText.match(/"data"\s*:\s*\{[\s\S]*?"id"\s*:\s*"?([0-9]+)"?/);
      if (match) {
        return match[1];
      }

      return result?.data?.id == null ? "" : String(result.data.id);
    },
    async fetchCurrentUserId() {
      const apiFetch = window.MeowKanbanAuth?.fetch || fetch;
      const response = await apiFetch("/api/v1/user/profile", {
        headers: { Accept: "application/json" }
      });
      const result = await this.readApiResult(response, "获取当前用户失败");
      const userId = this.extractExactDataId(result);
      if (!userId) {
        throw new Error("获取当前用户失败");
      }

      return userId;
    },
    async createRemoteBoard(name, description, coverResourceId, userId) {
      const apiFetch = window.MeowKanbanAuth?.fetch || fetch;
      const response = await apiFetch("/api/v1/board/new", {
        method: "POST",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          name,
          description,
          userId,
          coverResourceId,
          visibility: Number(this.createForm.visibility)
        })
      });

      if (response.status === 404) {
        throw new Error("创建看板接口尚未接入");
      }

      await this.readApiResult(response, "看板创建失败");
    },
    goToPage(pageIndex) {
      const nextPage = Math.max(1, Math.min(pageIndex, this.pages || 1));
      if (nextPage === this.pageIndex && this.boards.length > 0) {
        return;
      }
      this.pageIndex = nextPage;
      this.loadBoards();
    },
    normalizeBoard(board) {
      const id = String(board.id);
      const name = board.name || "未命名看板";
      const description = board.description || "";
      const visibility = Number(board.visibility) === 1 ? "公开" : "私有";
      const role = board.role || "owner";
      const coverUrl = this.createCoverUrl(board);

      return {
        id,
        name,
        description,
        coverUrl,
        visibility,
        role,
        favorited: Boolean(board.favorited),
        updatedAt: board.updatedAt || "最近活动",
        accent: accentClasses[this.accentIndex(id)],
        href: createBoardHref(id),
        shortName: this.createShortName(name)
      };
    },
    createShortName(name) {
      return Array.from(name.trim() || "看板").slice(0, 2).join("").toUpperCase();
    },
    createCoverUrl(board) {
      if (board.coverPreviewUrl) {
        return board.coverPreviewUrl;
      }
      if (board.coverResourceId) {
        return `/api/v1/resource/${encodeURIComponent(board.coverResourceId)}`;
      }
      return "";
    },
    accentIndex(id) {
      const value = String(id || "0");
      const lastDigits = value.match(/\d{1,6}$/)?.[0] || "0";
      return Number(lastDigits) % accentClasses.length;
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
    },
    isFavoriteSaving(boardId) {
      return this.favoriteSavingIds.includes(String(boardId));
    },
    async toggleFavorite(board) {
      if (this.isFavoriteSaving(board.id)) {
        return;
      }

      const nextFavorited = !board.favorited;
      if (useMockBoards) {
        board.favorited = nextFavorited;
        const target = mockBoards.find((item) => String(item.id) === String(board.id));
        if (target) {
          target.favorited = nextFavorited;
        }
        return;
      }

      this.favoriteSavingIds.push(String(board.id));
      board.favorited = nextFavorited;
      try {
        const apiFetch = window.MeowKanbanAuth?.fetch || fetch;
        const response = await apiFetch(nextFavorited ? "/api/v1/favorite/add-favorite" : "/api/v1/favorite/del-favorite", {
          method: nextFavorited ? "POST" : "DELETE",
          headers: {
            Accept: "application/json",
            "Content-Type": "application/json"
          },
          body: JSON.stringify({ id: Number(board.id) })
        });
        await this.readApiResult(response, nextFavorited ? "收藏失败" : "取消收藏失败");
      } catch (error) {
        board.favorited = !nextFavorited;
        this.loadError = error.message || (nextFavorited ? "收藏失败" : "取消收藏失败");
      } finally {
        this.favoriteSavingIds = this.favoriteSavingIds.filter((id) => id !== String(board.id));
      }
    }
  }
}).mount("#boardListApp");
