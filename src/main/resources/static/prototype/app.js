const defaultSections = [
  { id: "backlog", title: "待规划" },
  { id: "todo", title: "待处理" },
  { id: "doing", title: "进行中" },
  { id: "review", title: "验收" },
  { id: "done", title: "完成" }
];

let activeFilter = "all";
let hideDone = false;
let selectedTaskId = null;
let boardSections = [...defaultSections];

const tasks = [
  {
    id: "MK-102",
    title: "梳理移动端任务详情信息架构",
    desc: "确认任务详情页需要展示的字段、评论入口、附件入口和状态流转规则。",
    owner: "林夏",
    due: "06-04",
    status: "backlog",
    priority: "urgent",
    tags: ["产品", "信息架构"],
    blocked: false,
    mine: true
  },
  {
    id: "MK-118",
    title: "补全看板列拖拽交互",
    desc: "支持跨列移动任务，并在卡片详情中同步显示最新状态。",
    owner: "陈予",
    due: "06-05",
    status: "todo",
    priority: "normal",
    tags: ["前端", "交互"],
    blocked: false,
    mine: false
  },
  {
    id: "MK-121",
    title: "定义任务优先级与风险枚举",
    desc: "为后续接口、统计视图和通知规则提供统一数据结构。",
    owner: "周宁",
    due: "06-06",
    status: "todo",
    priority: "normal",
    tags: ["后端", "模型"],
    blocked: false,
    mine: false
  },
  {
    id: "MK-126",
    title: "实现项目容量概览组件",
    desc: "展示本周任务容量、风险项数量、进行中任务和预计交付进度。",
    owner: "陈予",
    due: "06-03",
    status: "doing",
    priority: "urgent",
    tags: ["前端", "统计"],
    blocked: false,
    mine: true
  },
  {
    id: "MK-131",
    title: "修复附件上传限制提示",
    desc: "当前异常提示文案存在编码问题，需要统一为 UTF-8 并补充大小限制说明。",
    owner: "周宁",
    due: "06-07",
    status: "doing",
    priority: "urgent",
    tags: ["后端", "异常"],
    blocked: true,
    mine: false
  },
  {
    id: "MK-136",
    title: "验收搜索与筛选空状态",
    desc: "检查搜索、快捷筛选和隐藏完成任务后的空列展示。",
    owner: "林夏",
    due: "06-08",
    status: "review",
    priority: "normal",
    tags: ["测试", "验收"],
    blocked: false,
    mine: true
  },
  {
    id: "MK-139",
    title: "整理 Sprint 复盘指标",
    desc: "汇总本周完成量、返工率和阻塞时长，为统计页提供样例数据。",
    owner: "林夏",
    due: "06-09",
    status: "done",
    priority: "normal",
    tags: ["数据", "复盘"],
    blocked: false,
    mine: false
  }
];

const board = document.querySelector("#board");
const search = document.querySelector("#taskSearch");
const drawer = document.querySelector("#taskDrawer");
const toggleDone = document.querySelector("#toggleDone");
const appShell = document.querySelector("#appShell");
const sidebarToggle = document.querySelector("#sidebarToggle");
const filterToggle = document.querySelector("#filterToggle");
const filterMenu = document.querySelector("#filterMenu");
const newSectionName = document.querySelector("#newSectionName");
const addSection = document.querySelector("#addSection");
const sectionMenuToggle = document.querySelector("#sectionMenuToggle");
const sectionMenu = document.querySelector("#sectionMenu");
const memberMenuToggle = document.querySelector("#memberMenuToggle");
const memberMenu = document.querySelector("#memberMenu");
const userMenuToggle = document.querySelector("#userMenuToggle");
const userMenu = document.querySelector("#userMenu");

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function getSectionIds() {
  return boardSections.map((section) => section.id);
}

function getStatusLabel(status) {
  return boardSections.find((section) => section.id === status)?.title || status;
}

function createSectionId(title) {
  const base = title
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/^-|-$/g, "");
  const prefix = base || `section-${Date.now()}`;
  let id = prefix;
  let index = 2;

  while (boardSections.some((section) => section.id === id)) {
    id = `${prefix}-${index}`;
    index += 1;
  }

  return id;
}

function saveSections() {
  try {
    localStorage.setItem("meowKanban.sections", JSON.stringify(boardSections));
  } catch (error) {
    return;
  }
}

function loadSections() {
  let loadedSavedSections = false;

  try {
    const saved = JSON.parse(localStorage.getItem("meowKanban.sections") || "[]");
    if (Array.isArray(saved) && saved.length > 0) {
      boardSections = saved
        .filter((section) => section && section.id && section.title)
        .map((section) => ({ id: String(section.id), title: String(section.title) }));
      loadedSavedSections = boardSections.length > 0;
    }
  } catch (error) {
    boardSections = [...defaultSections];
  }

  if (!loadedSavedSections) {
    boardSections = [...defaultSections];
  }

  tasks.forEach((task) => {
    if (!boardSections.some((section) => section.id === task.status)) {
      task.status = boardSections[0].id;
    }
  });
}

function getSavedSidebarState() {
  try {
    return localStorage.getItem("meowKanban.sidebarCollapsed") === "true";
  } catch (error) {
    return false;
  }
}

function setSidebarCollapsed(collapsed) {
  appShell.classList.toggle("sidebar-collapsed", collapsed);
  document.body.classList.toggle("sidebar-collapsed", collapsed);
  sidebarToggle.setAttribute("aria-expanded", String(!collapsed));

  const label = collapsed ? "显示侧边栏" : "隐藏侧边栏";
  sidebarToggle.setAttribute("aria-label", label);
  sidebarToggle.title = label;

  try {
    localStorage.setItem("meowKanban.sidebarCollapsed", String(collapsed));
  } catch (error) {
    return;
  }
}

function priorityLabel(priority) {
  return priority === "urgent" ? "高优先级" : "普通";
}

function taskMatches(task, query) {
  const text = [task.id, task.title, task.desc, task.owner, ...task.tags].join(" ").toLowerCase();
  const filterMatch =
    activeFilter === "all" ||
    (activeFilter === "urgent" && task.priority === "urgent") ||
    (activeFilter === "blocked" && task.blocked) ||
    (activeFilter === "mine" && task.mine);

  return filterMatch && text.includes(query.toLowerCase()) && !(hideDone && task.status === "done");
}

function renderBoard() {
  const query = search.value.trim();

  board.innerHTML = boardSections.map((section) => {
    const columnTasks = tasks.filter((task) => task.status === section.id && taskMatches(task, query));

    return `
      <section class="column" data-status="${escapeHtml(section.id)}">
        <header>
          <div class="column-title">
            <h2 class="editable-section-title" data-section-id="${escapeHtml(section.id)}" title="双击修改分区名称">${escapeHtml(section.title)}</h2>
            <span class="count">${columnTasks.length}</span>
          </div>
          <div class="column-actions">
            <button class="column-action rename-section" data-section-id="${escapeHtml(section.id)}" type="button" aria-label="重命名${escapeHtml(section.title)}" title="重命名分区">改</button>
            <button class="column-action danger delete-section" data-section-id="${escapeHtml(section.id)}" type="button" aria-label="删除${escapeHtml(section.title)}" title="删除分区">删</button>
          </div>
        </header>
        <div class="task-list" data-dropzone="${escapeHtml(section.id)}">${columnTasks.map(renderTaskCard).join("")}</div>
        <button class="column-add-task" data-section-id="${escapeHtml(section.id)}" type="button">新建任务</button>
      </section>
    `;
  }).join("");

  bindTaskEvents();
  bindDropEvents();
  bindSectionEvents();
  bindColumnTaskEvents();
}

function renderTaskCard(task) {
  const blocked = task.blocked ? '<span class="badge blocked">阻塞</span>' : "";
  const urgentClass = task.priority === "urgent" ? "urgent" : "normal";
  const tagList = task.tags.map((tag) => `<span class="badge">${tag}</span>`).join("");

  return `
    <article class="task-card" draggable="true" data-id="${task.id}">
      <div class="task-meta">
        <span class="badge ${urgentClass}">${priorityLabel(task.priority)}</span>
        ${blocked}
      </div>
      <h3>${task.title}</h3>
      <p>${task.desc}</p>
      <div class="task-meta">${tagList}</div>
      <div class="task-footer">
        <small>${task.owner}</small>
        <small>${task.due}</small>
      </div>
    </article>
  `;
}

function bindTaskEvents() {
  document.querySelectorAll(".task-card").forEach((card) => {
    card.addEventListener("click", () => openDrawer(card.dataset.id));
    card.addEventListener("dragstart", (event) => {
      event.dataTransfer.setData("text/plain", card.dataset.id);
    });
  });
}

function openDrawer(taskId) {
  const task = tasks.find((item) => item.id === taskId);
  if (!task) return;

  selectedTaskId = taskId;
  document.querySelector("#drawerCode").textContent = task.id;
  document.querySelector("#drawerTitle").textContent = task.title;
  document.querySelector("#drawerDesc").textContent = task.desc;
  document.querySelector("#drawerOwner").textContent = task.owner;
  document.querySelector("#drawerDue").textContent = task.due;
  document.querySelector("#drawerPriority").textContent = priorityLabel(task.priority);
  document.querySelector("#drawerStatus").textContent = getStatusLabel(task.status);
  document.querySelector("#drawerTags").innerHTML = task.tags.map((tag) => `<span class="badge">${tag}</span>`).join("");
  drawer.classList.add("open");
  drawer.setAttribute("aria-hidden", "false");
}

function closeDrawer() {
  drawer.classList.remove("open");
  drawer.setAttribute("aria-hidden", "true");
}

function moveSelected(offset) {
  const task = tasks.find((item) => item.id === selectedTaskId);
  if (!task) return;

  const statusIds = getSectionIds();
  const currentIndex = statusIds.indexOf(task.status);
  const nextIndex = Math.min(statusIds.length - 1, Math.max(0, currentIndex + offset));
  task.status = statusIds[nextIndex];
  renderBoard();
  openDrawer(task.id);
}

function bindDropEvents() {
  document.querySelectorAll(".task-list").forEach((list) => {
    list.addEventListener("dragover", (event) => {
      event.preventDefault();
      list.classList.add("drag-over");
    });

    list.addEventListener("dragleave", () => {
      list.classList.remove("drag-over");
    });

    list.addEventListener("drop", (event) => {
      event.preventDefault();
      const taskId = event.dataTransfer.getData("text/plain");
      const task = tasks.find((item) => item.id === taskId);
      if (task) {
        task.status = list.dataset.dropzone;
        renderBoard();
      }
      list.classList.remove("drag-over");
    });
  });
}

function bindSectionEvents() {
  document.querySelectorAll(".editable-section-title").forEach((title) => {
    title.addEventListener("dblclick", () => {
      startInlineRename(title.dataset.sectionId);
    });
  });

  document.querySelectorAll(".rename-section").forEach((button) => {
    button.addEventListener("click", () => {
      const section = boardSections.find((item) => item.id === button.dataset.sectionId);
      if (!section) return;

      const title = window.prompt("分区名称", section.title);
      if (!title || !title.trim()) return;

      renameSection(section.id, title.trim());
    });
  });

  document.querySelectorAll(".delete-section").forEach((button) => {
    button.addEventListener("click", () => {
      const sectionId = button.dataset.sectionId;
      if (boardSections.length <= 1) {
        window.alert("至少保留一个分区。");
        return;
      }

      const section = boardSections.find((item) => item.id === sectionId);
      if (!section) return;

      const fallbackSection = boardSections.find((item) => item.id !== sectionId);
      const taskCount = tasks.filter((task) => task.status === sectionId).length;
      const message = taskCount > 0
        ? `删除「${section.title}」后，其中 ${taskCount} 个任务会移动到「${fallbackSection.title}」。`
        : `确认删除「${section.title}」？`;

      if (!window.confirm(message)) return;

      tasks.forEach((task) => {
        if (task.status === sectionId) {
          task.status = fallbackSection.id;
        }
      });
      boardSections = boardSections.filter((item) => item.id !== sectionId);
      saveSections();
      renderBoard();
    });
  });
}

function renameSection(sectionId, title) {
  const section = boardSections.find((item) => item.id === sectionId);
  if (!section || !title) return;

  section.title = title;
  saveSections();
  renderBoard();

  if (selectedTaskId) {
    const selectedTask = tasks.find((task) => task.id === selectedTaskId);
    if (selectedTask) openDrawer(selectedTask.id);
  }
}

function startInlineRename(sectionId) {
  const section = boardSections.find((item) => item.id === sectionId);
  const heading = document.querySelector(`.editable-section-title[data-section-id="${CSS.escape(sectionId)}"]`);
  if (!section || !heading) return;

  const input = document.createElement("input");
  input.className = "column-title-input";
  input.value = section.title;
  input.setAttribute("aria-label", "分区名称");
  heading.replaceWith(input);
  input.focus();
  input.select();

  let canceled = false;
  const commit = () => {
    const nextTitle = input.value.trim();
    if (!canceled && nextTitle) {
      renameSection(sectionId, nextTitle);
    } else {
      renderBoard();
    }
  };

  input.addEventListener("keydown", (event) => {
    if (event.key === "Enter") {
      input.blur();
    }

    if (event.key === "Escape") {
      canceled = true;
      input.blur();
    }
  });

  input.addEventListener("blur", commit, { once: true });
}

function createTask(sectionId) {
  const id = `MK-${Math.floor(140 + Math.random() * 30)}`;
  tasks.unshift({
    id,
    title: "新建需求待补充",
    desc: "这是一个用于演示新增流程的占位任务，可继续扩展为表单弹窗。",
    owner: "林夏",
    due: "06-10",
    status: sectionId,
    priority: "normal",
    tags: ["草稿"],
    blocked: false,
    mine: true
  });
  renderBoard();
  openDrawer(id);
}

function bindColumnTaskEvents() {
  document.querySelectorAll(".column-add-task").forEach((button) => {
    button.addEventListener("click", () => {
      createTask(button.dataset.sectionId);
    });
  });
}

function closeFilterMenu() {
  filterMenu.classList.remove("open");
  filterMenu.setAttribute("aria-hidden", "true");
  filterToggle.setAttribute("aria-expanded", "false");
}

function openFilterMenu() {
  filterMenu.classList.add("open");
  filterMenu.setAttribute("aria-hidden", "false");
  filterToggle.setAttribute("aria-expanded", "true");
}

function closeSectionMenu() {
  sectionMenu.classList.remove("open");
  sectionMenu.setAttribute("aria-hidden", "true");
  sectionMenuToggle.setAttribute("aria-expanded", "false");
}

function openSectionMenu() {
  sectionMenu.classList.add("open");
  sectionMenu.setAttribute("aria-hidden", "false");
  sectionMenuToggle.setAttribute("aria-expanded", "true");
  newSectionName.focus();
}

function closeMemberMenu() {
  memberMenu.classList.remove("open");
  memberMenu.setAttribute("aria-hidden", "true");
  memberMenuToggle.setAttribute("aria-expanded", "false");
}

function openMemberMenu() {
  memberMenu.classList.add("open");
  memberMenu.setAttribute("aria-hidden", "false");
  memberMenuToggle.setAttribute("aria-expanded", "true");
}

function closeUserMenu() {
  userMenu.classList.remove("open");
  userMenu.setAttribute("aria-hidden", "true");
  userMenuToggle.setAttribute("aria-expanded", "false");
}

function openUserMenu() {
  userMenu.classList.add("open");
  userMenu.setAttribute("aria-hidden", "false");
  userMenuToggle.setAttribute("aria-expanded", "true");
}

document.querySelectorAll(".filter-option").forEach((option) => {
  option.addEventListener("click", () => {
    document.querySelectorAll(".filter-option").forEach((item) => item.classList.remove("active"));
    option.classList.add("active");
    activeFilter = option.dataset.filter;
    filterToggle.title = `当前筛选：${option.textContent}`;
    closeFilterMenu();
    renderBoard();
  });
});

search.addEventListener("input", renderBoard);

filterToggle.addEventListener("click", () => {
  if (filterMenu.classList.contains("open")) {
    closeFilterMenu();
  } else {
    closeSectionMenu();
    closeMemberMenu();
    closeUserMenu();
    openFilterMenu();
  }
});

sectionMenuToggle.addEventListener("click", () => {
  if (sectionMenu.classList.contains("open")) {
    closeSectionMenu();
  } else {
    closeFilterMenu();
    closeMemberMenu();
    closeUserMenu();
    openSectionMenu();
  }
});

memberMenuToggle.addEventListener("click", () => {
  if (memberMenu.classList.contains("open")) {
    closeMemberMenu();
  } else {
    closeFilterMenu();
    closeSectionMenu();
    closeUserMenu();
    openMemberMenu();
  }
});

userMenuToggle.addEventListener("click", () => {
  if (userMenu.classList.contains("open")) {
    closeUserMenu();
  } else {
    closeFilterMenu();
    closeSectionMenu();
    closeMemberMenu();
    openUserMenu();
  }
});

document.addEventListener("click", (event) => {
  if (!filterMenu.contains(event.target) && event.target !== filterToggle) {
    closeFilterMenu();
  }

  if (!sectionMenu.contains(event.target) && event.target !== sectionMenuToggle) {
    closeSectionMenu();
  }

  if (!memberMenu.contains(event.target) && event.target !== memberMenuToggle && !memberMenuToggle.contains(event.target)) {
    closeMemberMenu();
  }

  if (!userMenu.contains(event.target) && event.target !== userMenuToggle && !userMenuToggle.contains(event.target)) {
    closeUserMenu();
  }
});

sidebarToggle.addEventListener("click", () => {
  setSidebarCollapsed(!appShell.classList.contains("sidebar-collapsed"));
});

toggleDone.addEventListener("change", () => {
  hideDone = toggleDone.checked;
  renderBoard();
});

addSection.addEventListener("click", () => {
  const title = newSectionName.value.trim();
  if (!title) return;

  boardSections.push({
    id: createSectionId(title),
    title
  });
  newSectionName.value = "";
  saveSections();
  renderBoard();
  closeSectionMenu();
});

newSectionName.addEventListener("keydown", (event) => {
  if (event.key === "Enter") {
    addSection.click();
  }
});

document.querySelector("#closeDrawer").addEventListener("click", closeDrawer);
document.querySelector("#movePrev").addEventListener("click", () => moveSelected(-1));
document.querySelector("#moveNext").addEventListener("click", () => moveSelected(1));

drawer.addEventListener("click", (event) => {
  if (event.target === drawer) closeDrawer();
});

loadSections();
setSidebarCollapsed(getSavedSidebarState());
renderBoard();
