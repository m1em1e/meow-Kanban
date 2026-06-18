const { createApp } = Vue;

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
      boards: [
        {
          id: 1,
          name: "MeowKanban",
          description: "轻量项目看板，覆盖产品、前端、后端和测试任务。",
          visibility: "私有",
          role: "owner",
          updatedAt: "今天 16:20",
          accent: "teal",
          href: "./index.html"
        },
        {
          id: 2,
          name: "移动端改版",
          description: "注册、登录、任务详情和移动端信息架构整理。",
          visibility: "私有",
          role: "admin",
          updatedAt: "昨天 18:05",
          accent: "violet",
          href: "./index.html"
        },
        {
          id: 3,
          name: "运营需求池",
          description: "活动素材、数据追踪和复盘动作集中管理。",
          visibility: "公开",
          role: "member",
          updatedAt: "周二 09:40",
          accent: "amber",
          href: "./index.html"
        },
        {
          id: 4,
          name: "只读归档",
          description: "已完成项目归档，仅用于检索历史任务和结论。",
          visibility: "私有",
          role: "viewer",
          updatedAt: "6 月 12 日",
          accent: "rose",
          href: "./index.html"
        }
      ]
    };
  },
  computed: {
    filteredBoards() {
      const keyword = this.query.toLowerCase();
      return this.boards.filter((board) => {
        const matchesKeyword = !keyword
          || board.name.toLowerCase().includes(keyword)
          || board.description.toLowerCase().includes(keyword);
        const matchesFilter = this.filter === "all"
          || (this.filter === "managed" && ["owner", "admin"].includes(board.role))
          || (this.filter === "viewer" && board.role === "viewer");

        return matchesKeyword && matchesFilter;
      });
    },
    managedCount() {
      return this.boards.filter((board) => ["owner", "admin"].includes(board.role)).length;
    },
    viewerCount() {
      return this.boards.filter((board) => board.role === "viewer").length;
    }
  },
  methods: {
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
}).mount("#boardListApp");
