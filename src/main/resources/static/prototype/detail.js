const { createApp } = Vue;

const defaultSections = [
  { id: "todo", name: "待处理" },
  { id: "doing", name: "进行中" },
  { id: "review", name: "评审" },
  { id: "done", name: "已完成" }
];

const defaultTasks = [
  {
    id: 101,
    code: "MK-101",
    title: "完善登录后的看板入口",
    description: "把登录成功后的默认页面调整到看板列表，并补充未登录跳转策略。",
    status: "todo",
    priority: 3,
    blocked: false,
    owner: "林夏",
    ownerShortName: "林",
    ownerAccent: "teal",
    due: "今天",
    tags: ["认证", "页面"]
  },
  {
    id: 102,
    code: "MK-102",
    title: "任务详情抽屉字段整理",
    description: "统一任务详情中的负责人、截止时间、优先级和标签展示方式。",
    status: "doing",
    priority: 1,
    blocked: false,
    owner: "陈予",
    ownerShortName: "陈",
    ownerAccent: "violet",
    due: "明天",
    tags: ["前端", "交互"]
  },
  {
    id: 103,
    code: "MK-103",
    title: "资源上传接口联调",
    description: "验证头像、封面和附件上传后的资源 id 能正确回填到业务表。",
    status: "review",
    priority: 1,
    blocked: false,
    owner: "周宁",
    ownerShortName: "周",
    ownerAccent: "amber",
    due: "6 月 22 日",
    tags: ["资源", "接口"]
  },
  {
    id: 104,
    code: "MK-104",
    title: "补充最近参与看板查询",
    description: "近期看板优先展示本人创建的看板，其次展示最近操作过的看板。",
    status: "done",
    priority: 0,
    blocked: false,
    owner: "林夏",
    ownerShortName: "林",
    ownerAccent: "teal",
    due: "昨天",
    tags: ["看板", "SQL"]
  },
  {
    id: 105,
    code: "MK-105",
    title: "处理 RoleMapper XML 迁移",
    description: "把角色编码查询从注解 SQL 迁移到 XML，保持 Mapper 方法签名不变。",
    status: "done",
    priority: 1,
    blocked: false,
    owner: "周宁",
    ownerShortName: "周",
    ownerAccent: "amber",
    due: "6 月 18 日",
    tags: ["权限", "MyBatis"]
  },
  {
    id: 106,
    code: "MK-106",
    title: "确认邮箱编辑验证流程",
    description: "邮箱变更需要独立验证，不在个人资料普通编辑表单中直接修改。",
    status: "todo",
    priority: 3,
    blocked: true,
    owner: "陈予",
    ownerShortName: "陈",
    ownerAccent: "violet",
    due: "本周",
    tags: ["用户", "安全"]
  }
];

const useMockBoardDetail = window.location.protocol === "file:"
  || window.location.pathname.includes("/prototype/");

createApp({
  data() {
    return {
      boardId: "",
      boardName: "看板详情",
      query: "",
      activeFilter: "all",
      hideDone: false,
      filterOpen: false,
      sectionMenuOpen: false,
      memberMenuOpen: false,
      userMenuOpen: false,
      sidebarCollapsed: false,
      newSectionName: "",
      selectedTaskId: null,
      draggedTaskId: null,
      dragOverSection: null,
      draggedSectionId: null,
      sectionDragOverId: null,
      loading: false,
      loadError: "",
      sections: defaultSections.map((section) => ({ ...section })),
      tasks: defaultTasks.map((task) => ({ ...task, tags: [...task.tags] })),
      filters: [
        { label: "全部任务", value: "all" },
        { label: "紧急", value: "urgent" },
        { label: "阻塞", value: "blocked" },
        { label: "我负责", value: "mine" }
      ],
      members: [
        { name: "林夏", shortName: "林", role: "产品", activeAt: "刚刚", accent: "teal" },
        { name: "陈予", shortName: "陈", role: "前端", activeAt: "12 分钟前", accent: "violet" },
        { name: "周宁", shortName: "周", role: "后端", activeAt: "45 分钟前", accent: "amber" }
      ]
    };
  },
  created() {
    this.handleDocumentClick = (event) => {
      this.closeMenusOnOutsideClick(event);
    };
  },
  computed: {
    storageKey() {
      return `meow-kanban-detail:${this.boardId || "demo"}`;
    },
    selectedTask() {
      return this.tasks.find((task) => task.id === this.selectedTaskId) || null;
    },
    todayTodoCount() {
      return this.tasks.filter((task) => task.status !== "done" && task.due === "今天").length;
    },
    doingCount() {
      return this.tasks.filter((task) => task.status === "doing").length;
    },
    riskCount() {
      return this.tasks.filter((task) => task.blocked || this.normalizePriority(task.priority) === 3).length;
    },
    deliveryRate() {
      if (this.tasks.length === 0) {
        return 0;
      }
      return Math.round((this.tasks.filter((task) => task.status === "done").length / this.tasks.length) * 100);
    }
  },
  watch: {
    sections: {
      deep: true,
      handler() {
        this.saveState();
      }
    },
    tasks: {
      deep: true,
      handler() {
        this.saveState();
      }
    },
    sidebarCollapsed(value) {
      window.localStorage.setItem("meow-kanban-sidebar-collapsed", String(value));
      document.body.classList.toggle("sidebar-collapsed", value);
    }
  },
  mounted() {
    const root = document.getElementById("detailApp");
    this.boardId = root?.dataset.boardId || new URLSearchParams(window.location.search).get("boardId") || "demo";
    this.boardName = `看板 #${this.boardId}`;
    if (!useMockBoardDetail) {
      window.MeowKanbanAuth?.requireToken();
    }
    this.loadBoardDetail();
    this.sidebarCollapsed = window.localStorage.getItem("meow-kanban-sidebar-collapsed") === "true";
    document.body.classList.toggle("sidebar-collapsed", this.sidebarCollapsed);
    document.addEventListener("click", this.handleDocumentClick);
  },
  beforeUnmount() {
    document.removeEventListener("click", this.handleDocumentClick);
    document.body.classList.remove("sidebar-collapsed");
  },
  methods: {
    closeMenusOnOutsideClick(event) {
      const target = event.target;
      this.filterOpen = this.isClickInside(target, ".search-filter") ? this.filterOpen : false;
      this.sectionMenuOpen = this.isClickInside(target, ".section-manager") ? this.sectionMenuOpen : false;
      this.memberMenuOpen = this.isClickInside(target, ".member-menu") ? this.memberMenuOpen : false;
      this.userMenuOpen = this.isClickInside(target, ".user-menu") ? this.userMenuOpen : false;
    },
    isClickInside(target, selector) {
      return target instanceof Element && Boolean(target.closest(selector));
    },
    tasksBySection(sectionId) {
      return this.tasks.filter((task) => task.status === sectionId && this.matchesTask(task));
    },
    async loadBoardDetail() {
      if (useMockBoardDetail) {
        this.loadState();
        return;
      }

      this.loading = true;
      this.loadError = "";

      try {
        const apiFetch = window.MeowKanbanAuth?.fetch || fetch;
        const response = await apiFetch(`/api/v1/board/detail?id=${encodeURIComponent(this.boardId)}`, {
          headers: { Accept: "application/json" }
        });
        const result = await this.readApiResult(response, "看板详情加载失败");
        this.applyBoardDetail(result.data);
      } catch (error) {
        this.loadError = error.message || "看板详情加载失败";
        this.sections = [];
        this.tasks = [];
      } finally {
        this.loading = false;
      }
    },
    async readApiResult(response, fallbackMessage) {
      let result = null;
      try {
        const text = await response.text();
        result = text ? JSON.parse(text) : null;
      } catch (error) {
        result = null;
      }

      if (!response.ok || !result || result.code !== 1) {
        throw new Error(result?.msg || fallbackMessage);
      }
      return result;
    },
    applyBoardDetail(detail) {
      if (!detail) {
        throw new Error("看板详情加载失败");
      }

      this.boardName = detail.boardTitle || `看板 #${this.boardId}`;
      const memberIds = Array.isArray(detail.memberIds) ? detail.memberIds : [];
      this.members = memberIds.map((id) => {
        const name = `成员 #${id}`;
        return {
          name,
          shortName: this.createShortName(name),
          role: "成员",
          activeAt: "已加入",
          accent: this.avatarAccent(name)
        };
      });
      const sectionVOS = Array.isArray(detail.sectionVOS) ? detail.sectionVOS : [];
      this.sections = sectionVOS.map((section) => ({
        id: String(section.taskSectionId),
        name: section.sectionTitle || "未命名分区"
      }));
      this.tasks = sectionVOS.flatMap((section) => {
        const sectionId = String(section.taskSectionId);
        const tasks = Array.isArray(section.tasks) ? section.tasks : [];
        return tasks.map((task) => this.normalizeRemoteTask(task, sectionId));
      });
      this.selectedTaskId = null;
    },
    normalizeRemoteTask(task, sectionId) {
      const referUserIds = Array.isArray(task.referUserIds) ? task.referUserIds : [];
      const owner = referUserIds.length > 0 ? `成员 #${referUserIds[0]}` : "未指派";
      return {
        id: String(task.taskId),
        code: task.taskNo || `TASK-${task.taskId}`,
        title: task.title || "未命名任务",
        description: task.description || "",
        status: sectionId,
        priority: this.normalizePriority(task.priority),
        blocked: Boolean(task.blocked),
        owner,
        ownerShortName: this.createShortName(owner),
        ownerAccent: this.avatarAccent(owner),
        due: this.formatDate(task.dueDate),
        tags: Array.isArray(task.tags) ? task.tags : []
      };
    },
    createShortName(name) {
      return Array.from(String(name || "未").trim()).slice(0, 1).join("") || "未";
    },
    avatarAccent(value) {
      const accents = ["teal", "violet", "amber", "rose"];
      const text = String(value || "0");
      let sum = 0;
      for (const char of text) {
        sum += char.charCodeAt(0);
      }
      return accents[sum % accents.length];
    },
    formatDate(value) {
      if (!value) {
        return "待定";
      }
      return String(value).slice(0, 10);
    },
    matchesTask(task) {
      if (this.hideDone && task.status === "done") {
        return false;
      }

      if (this.activeFilter === "urgent" && this.normalizePriority(task.priority) !== 3) {
        return false;
      }

      if (this.activeFilter === "blocked" && !task.blocked) {
        return false;
      }

      if (this.activeFilter === "mine" && task.owner !== "林夏") {
        return false;
      }

      const keyword = this.query.trim().toLowerCase();
      if (!keyword) {
        return true;
      }

      return [
        task.code,
        task.title,
        task.description,
        task.owner,
        ...task.tags
      ].some((value) => String(value).toLowerCase().includes(keyword));
    },
    setFilter(value) {
      this.activeFilter = value;
      this.filterOpen = false;
    },
    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed;
    },
    priorityLabel(priority) {
      const labels = {
        0: "长期",
        1: "普通",
        2: "优先",
        3: "紧急"
      };
      return labels[this.normalizePriority(priority)] || "普通";
    },
    normalizePriority(priority) {
      const legacyValues = {
        low: 0,
        normal: 1,
        high: 2,
        urgent: 3
      };
      if (Object.prototype.hasOwnProperty.call(legacyValues, priority)) {
        return legacyValues[priority];
      }

      const value = Number(priority);
      return [0, 1, 2, 3].includes(value) ? value : 1;
    },
    priorityClass(priority) {
      const classes = {
        0: "priority-long",
        1: "priority-normal",
        2: "priority-high",
        3: "priority-urgent"
      };
      return classes[this.normalizePriority(priority)] || "priority-normal";
    },
    sectionName(sectionId) {
      return this.sections.find((section) => section.id === sectionId)?.name || "未分区";
    },
    openDrawer(taskId) {
      this.selectedTaskId = taskId;
    },
    closeDrawer() {
      this.selectedTaskId = null;
    },
    startDrag(taskId, event) {
      this.draggedTaskId = taskId;
      this.draggedSectionId = null;
      this.sectionDragOverId = null;
      if (event?.dataTransfer) {
        event.dataTransfer.effectAllowed = "move";
        event.dataTransfer.setData("text/plain", `task:${taskId}`);
      }
    },
    dragOverTaskList(sectionId, event) {
      if (this.draggedSectionId && !this.draggedTaskId) {
        this.dragOverSectionColumn(sectionId, event);
        return;
      }
      if (!this.draggedTaskId) {
        return;
      }
      event?.stopPropagation();
      this.dragOverSection = sectionId;
      if (event?.dataTransfer) {
        event.dataTransfer.dropEffect = "move";
      }
    },
    leaveTaskList(sectionId, event) {
      if (this.draggedSectionId && !this.draggedTaskId) {
        this.leaveSectionColumn(sectionId, event);
        return;
      }
      event?.stopPropagation();
      if (event?.currentTarget instanceof Element && event.currentTarget.contains(event.relatedTarget)) {
        return;
      }
      this.dragOverSection = null;
    },
    dropTask(sectionId, event) {
      if (this.draggedSectionId && !this.draggedTaskId) {
        event?.stopPropagation();
        this.dropSection(sectionId, event);
        return;
      }
      event?.preventDefault();
      event?.stopPropagation();
      const task = this.tasks.find((item) => item.id === this.draggedTaskId);
      if (task) {
        task.status = sectionId;
      }
      this.endTaskDrag();
    },
    endTaskDrag() {
      this.draggedTaskId = null;
      this.dragOverSection = null;
    },
    startSectionDrag(sectionId, event) {
      if (event?.target instanceof Element && event.target.closest("button")) {
        event.preventDefault();
        return;
      }
      this.draggedSectionId = sectionId;
      this.sectionDragOverId = sectionId;
      this.draggedTaskId = null;
      this.dragOverSection = null;
      if (event?.dataTransfer) {
        event.dataTransfer.effectAllowed = "move";
        event.dataTransfer.setData("text/plain", `section:${sectionId}`);
      }
    },
    dragOverSectionColumn(sectionId, event) {
      if (!this.draggedSectionId || this.draggedTaskId) {
        return;
      }
      this.sectionDragOverId = sectionId;
      if (event?.dataTransfer) {
        event.dataTransfer.dropEffect = "move";
      }
    },
    leaveSectionColumn(sectionId, event) {
      if (event?.currentTarget instanceof Element && event.currentTarget.contains(event.relatedTarget)) {
        return;
      }
      if (this.sectionDragOverId === sectionId) {
        this.sectionDragOverId = null;
      }
    },
    dropSection(targetSectionId, event) {
      event?.preventDefault();
      if (!this.draggedSectionId || this.draggedTaskId) {
        return;
      }

      const fromIndex = this.sections.findIndex((section) => section.id === this.draggedSectionId);
      const toIndex = this.sections.findIndex((section) => section.id === targetSectionId);
      if (fromIndex >= 0 && toIndex >= 0 && fromIndex !== toIndex) {
        const [movedSection] = this.sections.splice(fromIndex, 1);
        this.sections.splice(toIndex, 0, movedSection);
      }
      this.endSectionDrag();
    },
    endSectionDrag() {
      this.draggedSectionId = null;
      this.sectionDragOverId = null;
    },
    addSection() {
      const name = this.newSectionName.trim();
      if (!name) {
        return;
      }

      this.sections.push({
        id: this.createSectionId(name),
        name
      });
      this.newSectionName = "";
      this.sectionMenuOpen = false;
    },
    createSectionId(name) {
      const base = name
        .toLowerCase()
        .replace(/[^a-z0-9\u4e00-\u9fa5]+/g, "-")
        .replace(/^-+|-+$/g, "") || "section";
      let candidate = base;
      let index = 1;
      while (this.sections.some((section) => section.id === candidate)) {
        candidate = `${base}-${index}`;
        index += 1;
      }
      return candidate;
    },
    renameSection(sectionId) {
      const section = this.sections.find((item) => item.id === sectionId);
      if (!section) {
        return;
      }

      const nextName = window.prompt("分区名称", section.name);
      if (nextName && nextName.trim()) {
        section.name = nextName.trim();
      }
    },
    deleteSection(sectionId) {
      if (this.sections.length <= 1) {
        window.alert("至少保留一个分区");
        return;
      }

      const section = this.sections.find((item) => item.id === sectionId);
      if (!section || !window.confirm(`删除「${section.name}」分区？该分区下任务会移动到第一个分区。`)) {
        return;
      }

      const fallbackId = this.sections.find((item) => item.id !== sectionId)?.id;
      this.tasks.forEach((task) => {
        if (task.status === sectionId) {
          task.status = fallbackId;
        }
      });
      this.sections = this.sections.filter((item) => item.id !== sectionId);
    },
    createTask(sectionId) {
      const title = window.prompt("任务标题");
      if (!title || !title.trim()) {
        return;
      }

      const id = Date.now();
      this.tasks.push({
        id,
        code: `MK-${String(id).slice(-4)}`,
        title: title.trim(),
        description: "新建任务，点击卡片可查看详情。",
        status: sectionId,
        priority: 1,
        blocked: false,
        owner: "林夏",
        ownerShortName: "林",
        ownerAccent: "teal",
        due: "待定",
        tags: ["新任务"]
      });
    },
    saveState() {
      if (!useMockBoardDetail || !this.boardId) {
        return;
      }

      window.localStorage.setItem(this.storageKey, JSON.stringify({
        sections: this.sections,
        tasks: this.tasks
      }));
    },
    loadState() {
      const raw = window.localStorage.getItem(this.storageKey);
      if (!raw) {
        return;
      }

      try {
        const parsed = JSON.parse(raw);
        if (Array.isArray(parsed.sections) && parsed.sections.length > 0) {
          this.sections = parsed.sections;
        }
        if (Array.isArray(parsed.tasks)) {
          this.tasks = parsed.tasks.map((task) => ({
            ...task,
            priority: this.normalizePriority(task.priority)
          }));
        }
      } catch (error) {
        window.localStorage.removeItem(this.storageKey);
      }
    },
    async logout() {
      await (window.MeowKanbanAuth?.fetch || fetch)("/api/v1/auth/logout", { method: "POST" });
      window.MeowKanbanAuth?.clearToken();
      window.location.href = "/login";
    }
  }
}).mount("#detailApp");
