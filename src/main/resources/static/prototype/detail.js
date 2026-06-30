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
      sectionRenameOpenId: null,
      sectionRenameName: "",
      selectedTaskId: null,
      draggedTaskId: null,
      dragOverSection: null,
      draggedSectionId: null,
      sectionDragOverId: null,
      loading: false,
      loadError: "",
      memberInfoById: {},
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
      this.sectionRenameOpenId = this.isClickInside(target, ".section-rename-manager") ? this.sectionRenameOpenId : null;
      this.memberMenuOpen = this.isClickInside(target, ".member-menu") ? this.memberMenuOpen : false;
      this.userMenuOpen = this.isClickInside(target, ".user-menu") ? this.userMenuOpen : false;
    },
    isClickInside(target, selector) {
      return target instanceof Element && Boolean(target.closest(selector));
    },
    tasksBySection(sectionId) {
      return this.sortTasks(this.tasks.filter((task) => task.status === sectionId && this.matchesTask(task)));
    },
    sortTasks(tasks) {
      return [...tasks].sort((left, right) => {
        const leftSort = Number(left.sort) || 0;
        const rightSort = Number(right.sort) || 0;
        if (leftSort !== rightSort) {
          return leftSort - rightSort;
        }
        return this.compareTaskId(left.id, right.id);
      });
    },
    compareTaskId(leftId, rightId) {
      const leftNumber = Number(leftId);
      const rightNumber = Number(rightId);
      if (Number.isFinite(leftNumber) && Number.isFinite(rightNumber)) {
        return leftNumber - rightNumber;
      }
      return String(leftId).localeCompare(String(rightId));
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
        await this.applyBoardDetail(result.data);
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
    async applyBoardDetail(detail) {
      if (!detail) {
        throw new Error("看板详情加载失败");
      }

      this.boardName = detail.boardTitle || `看板 #${this.boardId}`;
      const memberIds = this.normalizeIdList(detail.memberIds);
      const memberInfos = await this.fetchMemberInfoList(memberIds);
      this.memberInfoById = memberInfos.reduce((lookup, member) => {
        lookup[String(member.id)] = member;
        return lookup;
      }, {});
      this.members = memberIds.map((id) => this.normalizeMemberInfo(
        this.memberInfoById[String(id)] || { id }
      ));
      const sectionVOS = Array.isArray(detail.sectionVOS) ? detail.sectionVOS : [];
      this.sections = sectionVOS.map((section) => ({
        id: String(section.taskSectionId),
        name: section.sectionTitle || "未命名分区",
        sortOrder: Number(section.sortOrder)
      }));
      this.tasks = sectionVOS.flatMap((section) => {
        const sectionId = String(section.taskSectionId);
        const tasks = Array.isArray(section.tasks) ? section.tasks : [];
        return tasks.map((task) => this.normalizeRemoteTask(task, sectionId));
      });
      this.selectedTaskId = null;
    },
    async fetchMemberInfoList(memberIds) {
      if (!memberIds.length) {
        return [];
      }

      const apiFetch = window.MeowKanbanAuth?.fetch || fetch;
      const params = new URLSearchParams();
      params.set("boardId", this.boardId);
      memberIds.forEach((id) => params.append("userIds", id));
      const response = await apiFetch(`/api/v1/user/info-list?${params.toString()}`, {
        headers: { Accept: "application/json" }
      });
      const result = await this.readApiResult(response, "成员信息加载失败");
      return Array.isArray(result.data) ? result.data : [];
    },
    normalizeIdList(ids) {
      if (!Array.isArray(ids)) {
        return [];
      }

      return Array.from(new Set(ids
        .map((id) => Number(id))
        .filter((id) => Number.isFinite(id))));
    },
    normalizeMemberInfo(member) {
      const id = member?.id;
      const name = member?.nickname || `成员 #${id}`;
      return {
        id,
        name,
        shortName: this.createShortName(name),
        role: this.boardRoleLabel(member?.boardRoleCode || member?.BoardRoleCode),
        activeAt: "已加入",
        accent: this.avatarAccent(id || name),
        avatarResourceId: member?.avatarResourceId || null,
        avatarUrl: this.resourceUrl(member?.avatarResourceId)
      };
    },
    boardRoleLabel(roleCode) {
      const labels = {
        owner: "拥有者",
        admin: "管理员",
        member: "成员",
        viewer: "只读"
      };
      return labels[String(roleCode || "").toLowerCase()] || "成员";
    },
    memberNameById(userId) {
      return this.normalizeMemberInfo(
        this.memberInfoById[String(userId)] || { id: userId }
      ).name;
    },
    memberAvatarUrlById(userId) {
      return this.normalizeMemberInfo(
        this.memberInfoById[String(userId)] || { id: userId }
      ).avatarUrl;
    },
    resourceUrl(resourceId) {
      return resourceId ? `/api/v1/resource/${encodeURIComponent(resourceId)}` : "";
    },
    normalizeRemoteTask(task, sectionId) {
      const referUserIds = Array.isArray(task.referUserIds) ? task.referUserIds : [];
      const ownerId = referUserIds[0];
      const owner = referUserIds.length > 0 ? this.memberNameById(ownerId) : "未指派";
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
        ownerAvatarUrl: ownerId ? this.memberAvatarUrlById(ownerId) : "",
        due: this.formatDate(task.dueDate),
        tags: Array.isArray(task.tags) ? task.tags : [],
        sort: Number(task.sort) || 0
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
    async dropTask(sectionId, event) {
      if (this.draggedSectionId && !this.draggedTaskId) {
        event?.stopPropagation();
        this.dropSection(sectionId, event);
        return;
      }
      event?.preventDefault();
      event?.stopPropagation();
      const task = this.tasks.find((item) => item.id === this.draggedTaskId);
      if (!task) {
        this.endTaskDrag();
        return;
      }

      const previousSectionId = task.status;
      const previousSort = task.sort;
      const previousTasks = this.tasks.map((item) => ({ ...item, tags: [...(item.tags || [])] }));
      const nextSort = this.taskDropSort(sectionId, event);
      if (previousSectionId === sectionId && Number(previousSort) === nextSort) {
        this.endTaskDrag();
        return;
      }

      if (useMockBoardDetail) {
        this.applyTaskDrop(task.id, sectionId, nextSort);
        this.endTaskDrag();
        return;
      }

      this.applyTaskDrop(task.id, sectionId, nextSort);
      try {
        await this.modifyTaskCard({
          id: Number(task.id),
          sectionId: Number(sectionId),
          sort: nextSort
        });
      } catch (error) {
        this.tasks = previousTasks;
        window.alert(error.message || "任务移动失败");
      } finally {
        this.endTaskDrag();
      }
    },
    taskDropSort(sectionId, event) {
      const orderedTasks = this.sortTasks(this.tasks.filter((task) => task.status === sectionId && task.id !== this.draggedTaskId));
      const targetCard = event?.target instanceof Element ? event.target.closest(".task-card") : null;
      const listElement = event?.currentTarget instanceof Element ? event.currentTarget : null;
      const targetTaskId = targetCard && listElement?.contains(targetCard) ? targetCard.dataset.id : null;

      if (targetTaskId === this.draggedTaskId) {
        const task = this.tasks.find((item) => item.id === this.draggedTaskId);
        return Number(task?.sort) || 0;
      }

      if (!targetTaskId) {
        return this.nextTaskSort(sectionId, this.draggedTaskId);
      }

      const targetIndex = orderedTasks.findIndex((item) => item.id === targetTaskId);
      if (targetIndex === -1) {
        return this.nextTaskSort(sectionId, this.draggedTaskId);
      }

      const rect = targetCard.getBoundingClientRect();
      const insertBeforeTarget = event.clientY < rect.top + rect.height / 2;
      const insertIndex = insertBeforeTarget ? targetIndex : targetIndex + 1;
      const nextTask = orderedTasks[insertIndex];

      if (!nextTask) {
        return this.nextTaskSort(sectionId, this.draggedTaskId);
      }
      return Number(nextTask.sort) || 0;
    },
    applyTaskDrop(taskId, sectionId, nextSort) {
      const task = this.tasks.find((item) => item.id === taskId);
      if (!task) {
        return;
      }
      this.tasks.forEach((item) => {
        if (item.id !== taskId && item.status === sectionId && (Number(item.sort) || 0) >= nextSort) {
          item.sort = (Number(item.sort) || 0) + 1;
        }
      });
      task.status = sectionId;
      task.sort = nextSort;
    },
    endTaskDrag() {
      this.draggedTaskId = null;
      this.dragOverSection = null;
    },
    startSectionDrag(sectionId, event) {
      if (event?.target instanceof Element && event.target.closest("button, input, textarea, select, .section-menu")) {
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
    async dropSection(targetSectionId, event) {
      event?.preventDefault();
      if (!this.draggedSectionId || this.draggedTaskId) {
        return;
      }

      const fromIndex = this.sections.findIndex((section) => section.id === this.draggedSectionId);
      const toIndex = this.sections.findIndex((section) => section.id === targetSectionId);
      if (fromIndex >= 0 && toIndex >= 0 && fromIndex !== toIndex) {
        const sourceSort = this.sectionSortAt(fromIndex);
        const targetSort = this.sectionSortAt(toIndex);
        const previousSections = this.sections.map((section) => ({ ...section }));
        const [movedSection] = this.sections.splice(fromIndex, 1);
        this.sections.splice(toIndex, 0, movedSection);
        this.refreshSectionSortOrders();

        try {
          await this.modifySectionSort(sourceSort, targetSort);
        } catch (error) {
          this.sections = previousSections;
          window.alert(error.message || "分区顺序保存失败");
        }
      }
      this.endSectionDrag();
    },
    endSectionDrag() {
      this.draggedSectionId = null;
      this.sectionDragOverId = null;
    },
    sectionSortAt(index) {
      const sortOrder = Number(this.sections[index]?.sortOrder);
      return Number.isFinite(sortOrder) ? sortOrder : (index + 1) * 10;
    },
    refreshSectionSortOrders() {
      this.sections.forEach((section, index) => {
        section.sortOrder = (index + 1) * 10;
      });
    },
    async modifySectionSort(sourceSort, targetSort) {
      if (useMockBoardDetail) {
        return;
      }

      const apiFetch = window.MeowKanbanAuth?.fetch || fetch;
      const response = await apiFetch("/api/v1/board/modify-section-card", {
        method: "PUT",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          boardId: Number(this.boardId),
          sourceSort,
          targetSort
        })
      });
      await this.readApiResult(response, "分区顺序保存失败");
    },
    nextSectionSort() {
      const maxSortOrder = this.sections.reduce((max, section, index) => {
        const sortOrder = Number(section.sortOrder);
        return Math.max(max, Number.isFinite(sortOrder) ? sortOrder : (index + 1) * 10);
      }, 0);
      return maxSortOrder + 10;
    },
    nextTaskSort(sectionId, excludeTaskId = null) {
      return this.tasks
        .filter((task) => task.status === sectionId && task.id !== excludeTaskId)
        .reduce((max, task) => Math.max(max, Number(task.sort) || 0), 0) + 10;
    },
    async addSection() {
      const name = this.newSectionName.trim();
      if (!name) {
        return;
      }

      const sortOrder = this.nextSectionSort();
      if (!useMockBoardDetail) {
        try {
          await this.addSectionCard(name, sortOrder);
          this.newSectionName = "";
          this.sectionMenuOpen = false;
          await this.loadBoardDetail();
        } catch (error) {
          window.alert(error.message || "分区创建失败");
        }
        return;
      }

      this.sections.push({
        id: this.createSectionId(name),
        name,
        sortOrder
      });
      this.newSectionName = "";
      this.sectionMenuOpen = false;
    },
    openRenameSectionMenu(sectionId) {
      const section = this.sections.find((item) => item.id === sectionId);
      if (!section) {
        return;
      }
      if (this.sectionRenameOpenId === sectionId) {
        this.sectionRenameOpenId = null;
        return;
      }
      this.sectionMenuOpen = false;
      this.sectionRenameOpenId = sectionId;
      this.sectionRenameName = section.name;
    },
    async submitRenameSection(sectionId) {
      const section = this.sections.find((item) => item.id === sectionId);
      const nextName = this.sectionRenameName.trim();
      if (!section || !nextName) {
        return;
      }

      if (nextName === section.name) {
        this.sectionRenameOpenId = null;
        return;
      }

      if (!useMockBoardDetail) {
        try {
          await this.renameSectionCard(section.id, nextName);
          this.sectionRenameOpenId = null;
          this.sectionRenameName = "";
          await this.loadBoardDetail();
        } catch (error) {
          window.alert(error.message || "分区重命名失败");
        }
        return;
      }

      section.name = nextName;
      this.sectionRenameOpenId = null;
      this.sectionRenameName = "";
    },
    async addSectionCard(boardName, sort) {
      const apiFetch = window.MeowKanbanAuth?.fetch || fetch;
      const response = await apiFetch("/api/v1/board/add-section-card", {
        method: "POST",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          boardId: Number(this.boardId),
          boardName,
          sort
        })
      });
      await this.readApiResult(response, "分区创建失败");
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
    async deleteSection(sectionId) {
      if (this.sections.length <= 1) {
        window.alert("至少保留一个分区");
        return;
      }

      const section = this.sections.find((item) => item.id === sectionId);
      if (!section || !window.confirm(`删除「${section.name}」分区？该分区下任务会移动到第一个分区。`)) {
        return;
      }

      const fallbackId = this.sections.find((item) => item.id !== sectionId)?.id;
      if (!useMockBoardDetail) {
        try {
          await this.moveTasksBeforeDeletingSection(sectionId, fallbackId);
          await this.deleteSectionCard(sectionId);
          await this.loadBoardDetail();
        } catch (error) {
          window.alert(error.message || "分区删除失败");
        }
        return;
      }

      this.tasks.forEach((task) => {
        if (task.status === sectionId) {
          task.status = fallbackId;
        }
      });
      this.sections = this.sections.filter((item) => item.id !== sectionId);
    },
    async createTask(sectionId) {
      const title = window.prompt("任务标题");
      if (!title || !title.trim()) {
        return;
      }

      if (!useMockBoardDetail) {
        try {
          await this.addTaskCard(sectionId, title.trim());
          await this.loadBoardDetail();
        } catch (error) {
          window.alert(error.message || "任务创建失败");
        }
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
    async addTaskCard(sectionId, title) {
      const apiFetch = window.MeowKanbanAuth?.fetch || fetch;
      const response = await apiFetch("/api/v1/task/add-task-card", {
        method: "POST",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          boardId: Number(this.boardId),
          sectionId: Number(sectionId),
          title,
          sort: this.nextTaskSort(sectionId)
        })
      });
      await this.readApiResult(response, "任务创建失败");
    },
    async modifyTaskCard(payload) {
      const apiFetch = window.MeowKanbanAuth?.fetch || fetch;
      const response = await apiFetch("/api/v1/task/modify-task-card", {
        method: "PUT",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
      });
      await this.readApiResult(response, "任务保存失败");
    },
    async deleteTask(taskId) {
      const task = this.tasks.find((item) => item.id === taskId);
      if (!task || !window.confirm(`删除「${task.title}」任务？`)) {
        return;
      }

      if (useMockBoardDetail) {
        this.tasks = this.tasks.filter((item) => item.id !== taskId);
        this.closeDrawer();
        return;
      }

      try {
        const apiFetch = window.MeowKanbanAuth?.fetch || fetch;
        const response = await apiFetch("/api/v1/task/del-task-card", {
          method: "DELETE",
          headers: {
            Accept: "application/json",
            "Content-Type": "application/json"
          },
          body: JSON.stringify({ id: Number(taskId) })
        });
        await this.readApiResult(response, "任务删除失败");
        this.closeDrawer();
        await this.loadBoardDetail();
      } catch (error) {
        window.alert(error.message || "任务删除失败");
      }
    },
    async renameSectionCard(sectionId, title) {
      const apiFetch = window.MeowKanbanAuth?.fetch || fetch;
      const response = await apiFetch("/api/v1/board/rename-section-card", {
        method: "PUT",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          id: Number(sectionId),
          title
        })
      });
      await this.readApiResult(response, "分区重命名失败");
    },
    async deleteSectionCard(sectionId) {
      const apiFetch = window.MeowKanbanAuth?.fetch || fetch;
      const response = await apiFetch("/api/v1/board/del-section-card", {
        method: "DELETE",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ id: Number(sectionId) })
      });
      await this.readApiResult(response, "分区删除失败");
    },
    async moveTasksBeforeDeletingSection(sectionId, fallbackId) {
      const tasksToMove = this.tasks.filter((task) => task.status === sectionId);
      for (let index = 0; index < tasksToMove.length; index += 1) {
        const task = tasksToMove[index];
        await this.modifyTaskCard({
          id: Number(task.id),
          sectionId: Number(fallbackId),
          sort: this.nextTaskSort(fallbackId) + index * 10
        });
      }
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
