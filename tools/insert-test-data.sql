INSERT INTO mk_user (
  id,
  username,
  password,
  salt,
  nickname,
  email,
  gender,
  status
) VALUES
  (101, 'chenyu', '$2a$10$3xPuywCsbpJQzVUa9MYU1OyUxmWOWnUdatZPGxZO1ehs/3CP1BZjO', 'BCrypt', '陈予', 'chenyu@meowkanban.local', 1, 1),
  (102, 'linxia', '$2a$10$3xPuywCsbpJQzVUa9MYU1OyUxmWOWnUdatZPGxZO1ehs/3CP1BZjO', 'BCrypt', '林夏', 'linxia@meowkanban.local', 0, 1),
  (103, 'zhouning', '$2a$10$3xPuywCsbpJQzVUa9MYU1OyUxmWOWnUdatZPGxZO1ehs/3CP1BZjO', 'BCrypt', '周宁', 'zhouning@meowkanban.local', 1, 1)
ON CONFLICT(id) DO UPDATE SET
  username = excluded.username,
  nickname = excluded.nickname,
  email = excluded.email,
  gender = excluded.gender,
  status = excluded.status,
  updated_time = CURRENT_TIMESTAMP,
  deleted = 0,
  deleted_time = NULL;

INSERT INTO mk_user_role (
  id,
  user_id,
  role_id,
  creater_id
) VALUES
  (101, 101, 2, 1),
  (102, 102, 2, 1),
  (103, 103, 3, 1)
ON CONFLICT(id) DO UPDATE SET
  user_id = excluded.user_id,
  role_id = excluded.role_id,
  creater_id = excluded.creater_id,
  updated_time = CURRENT_TIMESTAMP,
  deleted = 0,
  deleted_time = NULL;

INSERT INTO mk_board (
  id,
  name,
  description,
  owner_id,
  visibility
) VALUES
  (101, '移动端改版', '注册、登录、任务详情和移动端信息架构整理。', 101, 0),
  (102, '后端接口联调', '认证、看板、任务、附件和活动日志接口联调计划。', 1, 0),
  (103, '运营需求池', '活动素材、数据追踪和复盘动作集中管理。', 102, 1)
ON CONFLICT(id) DO UPDATE SET
  name = excluded.name,
  description = excluded.description,
  owner_id = excluded.owner_id,
  visibility = excluded.visibility,
  updated_time = CURRENT_TIMESTAMP,
  deleted = 0,
  deleted_time = NULL;

INSERT INTO mk_board_member (
  id,
  board_id,
  user_id,
  role
) VALUES
  (101, 101, 101, 'owner'),
  (102, 101, 1, 'admin'),
  (103, 101, 102, 'member'),
  (104, 101, 103, 'viewer'),
  (105, 102, 1, 'owner'),
  (106, 102, 101, 'member'),
  (107, 102, 102, 'admin'),
  (108, 103, 102, 'owner'),
  (109, 103, 1, 'viewer')
ON CONFLICT(id) DO UPDATE SET
  board_id = excluded.board_id,
  user_id = excluded.user_id,
  role = excluded.role,
  updated_time = CURRENT_TIMESTAMP,
  deleted = 0,
  deleted_time = NULL;

INSERT INTO mk_board_recent (
  id,
  board_id,
  user_id,
  last_active_time,
  active_count
) VALUES
  (101, 101, 1, '2026-06-23 15:20:00', 12),
  (102, 102, 1, '2026-06-23 15:10:00', 8),
  (103, 103, 1, '2026-06-23 14:45:00', 3),
  (104, 101, 101, '2026-06-23 15:25:00', 18),
  (105, 102, 101, '2026-06-23 14:55:00', 5),
  (106, 101, 102, '2026-06-23 15:18:00', 7),
  (107, 103, 102, '2026-06-23 15:05:00', 9)
ON CONFLICT(id) DO UPDATE SET
  board_id = excluded.board_id,
  user_id = excluded.user_id,
  last_active_time = excluded.last_active_time,
  active_count = excluded.active_count,
  updated_time = CURRENT_TIMESTAMP,
  deleted = 0,
  deleted_time = NULL;

INSERT INTO mk_board_favorite (
  id,
  board_id,
  user_id
) VALUES
  (101, 101, 1),
  (102, 102, 101),
  (103, 103, 102)
ON CONFLICT(id) DO UPDATE SET
  board_id = excluded.board_id,
  user_id = excluded.user_id,
  updated_time = CURRENT_TIMESTAMP,
  deleted = 0,
  deleted_time = NULL;

INSERT INTO mk_board_section (
  id,
  board_id,
  code,
  title,
  sort_order
) VALUES
  (1011, 101, 'backlog', '待规划', 10),
  (1012, 101, 'todo', '待处理', 20),
  (1013, 101, 'doing', '进行中', 30),
  (1014, 101, 'review', '验收', 40),
  (1015, 101, 'done', '完成', 50),
  (1021, 102, 'backlog', '待规划', 10),
  (1022, 102, 'todo', '待处理', 20),
  (1023, 102, 'doing', '进行中', 30),
  (1024, 102, 'review', '验收', 40),
  (1025, 102, 'done', '完成', 50),
  (1031, 103, 'idea', '需求收集', 10),
  (1032, 103, 'plan', '排期中', 20),
  (1033, 103, 'doing', '执行中', 30),
  (1034, 103, 'review', '复盘中', 40),
  (1035, 103, 'done', '已归档', 50)
ON CONFLICT(id) DO UPDATE SET
  board_id = excluded.board_id,
  code = excluded.code,
  title = excluded.title,
  sort_order = excluded.sort_order,
  updated_time = CURRENT_TIMESTAMP,
  deleted = 0,
  deleted_time = NULL;

INSERT INTO mk_tag (
  id,
  board_id,
  name,
  color
) VALUES
  (101, 101, '移动端', '#4F46E5'),
  (102, 101, '登录', '#0F766E'),
  (103, 101, '详情页', '#B45309'),
  (104, 101, '交互', '#BE123C'),
  (105, 101, '验收', '#475569'),
  (111, 102, '接口', '#2563EB'),
  (112, 102, '安全', '#B91C1C'),
  (113, 102, '数据库', '#047857'),
  (114, 102, '测试', '#7C3AED'),
  (121, 103, '运营', '#C2410C'),
  (122, 103, '数据', '#0369A1'),
  (123, 103, '内容', '#65A30D')
ON CONFLICT(id) DO UPDATE SET
  board_id = excluded.board_id,
  name = excluded.name,
  color = excluded.color,
  updated_time = CURRENT_TIMESTAMP,
  deleted = 0,
  deleted_time = NULL;

INSERT INTO mk_task (
  id,
  board_id,
  section_id,
  task_no,
  title,
  description,
  due_date,
  priority,
  blocked,
  sort_order,
  creater_id,
  updater_id
) VALUES
  (10101, 101, 1011, 'MOB-001', '梳理登录页移动端布局', '确认输入区域、验证码按钮、错误提示和底部协议在小屏下的排列。', '2026-06-25', 2, 0, 10, 101, 102),
  (10102, 101, 1012, 'MOB-002', '接入登录成功后的看板跳转', '登录成功后跳转到看板列表，并保留 token 续期逻辑。', '2026-06-26', 3, 1, 20, 101, 1),
  (10103, 101, 1012, 'MOB-003', '补充注册表单校验提示', '覆盖用户名、邮箱、验证码和密码强度的提示状态。', '2026-06-28', 1, 0, 30, 102, 101),
  (10104, 101, 1013, 'MOB-004', '优化任务详情抽屉触控区域', '移动端抽屉需要支持大面积关闭、滚动和状态展示。', '2026-06-29', 2, 0, 40, 102, 102),
  (10105, 101, 1013, 'MOB-005', '设计分区拖拽降级方案', '小屏无法可靠拖拽时提供菜单式流转。', '2026-07-01', 0, 0, 50, 103, 101),
  (10106, 101, 1014, 'MOB-006', '验收移动端空状态', '检查筛选无结果、无任务列和加载失败三种状态。', '2026-07-02', 1, 0, 60, 101, 103),
  (10107, 101, 1015, 'MOB-007', '完成移动端导航回归', '确认返回看板列表、个人菜单和登出入口可用。', '2026-06-21', 1, 0, 70, 101, 101),
  (10201, 102, 1021, 'API-001', '定义看板详情响应结构', '确认 board、sectionVOS、tasks、tags、referUserIds 的字段命名。', '2026-06-24', 3, 0, 10, 1, 101),
  (10202, 102, 1022, 'API-002', '实现任务创建接口参数校验', '校验分区、优先级、标题和引用用户合法性。', '2026-06-27', 2, 0, 20, 1, 101),
  (10203, 102, 1022, 'API-003', '补充任务移动接口', '支持拖拽和详情抽屉上一步下一步流转。', '2026-06-30', 3, 1, 30, 101, 1),
  (10204, 102, 1023, 'API-004', '完善 XML 联查映射测试', '验证看板详情的嵌套集合映射稳定。', '2026-06-23', 2, 0, 40, 1, 1),
  (10205, 102, 1024, 'API-005', '接口错误码回归', '检查 400、401、403、404、409 的返回结构。', '2026-07-03', 1, 0, 50, 102, 1),
  (10206, 102, 1025, 'API-006', '归档旧任务 owner_id 迁移', '确认任务负责人迁移为 mk_task_refer_user 后数据完整。', '2026-06-20', 0, 0, 60, 1, 1),
  (10301, 103, 1031, 'OPS-001', '收集七月运营活动需求', '汇总活动目标、目标人群、资源位和数据指标。', '2026-06-30', 1, 0, 10, 102, 102),
  (10302, 103, 1032, 'OPS-002', '制定新用户引导文案', '完成注册后首屏引导和看板空状态文案。', '2026-07-04', 2, 0, 20, 102, 101),
  (10303, 103, 1033, 'OPS-003', '跟进数据埋点清单', '确认登录、创建看板、创建任务和移动任务的事件名。', '2026-07-05', 3, 1, 30, 101, 102),
  (10304, 103, 1034, 'OPS-004', '复盘邮件打开率', '按用户来源和时间段整理邮件触达效果。', '2026-07-06', 1, 0, 40, 103, 102),
  (10305, 103, 1035, 'OPS-005', '归档六月内容排期', '整理已发布内容和后续复用素材。', '2026-06-22', 0, 0, 50, 102, 102)
ON CONFLICT(id) DO UPDATE SET
  board_id = excluded.board_id,
  section_id = excluded.section_id,
  task_no = excluded.task_no,
  title = excluded.title,
  description = excluded.description,
  due_date = excluded.due_date,
  priority = excluded.priority,
  blocked = excluded.blocked,
  sort_order = excluded.sort_order,
  creater_id = excluded.creater_id,
  updater_id = excluded.updater_id,
  updated_time = CURRENT_TIMESTAMP,
  deleted = 0,
  deleted_time = NULL;

INSERT INTO mk_task_tag (
  id,
  task_id,
  tag_id
) VALUES
  (10101, 10101, 101),
  (10102, 10101, 102),
  (10103, 10102, 102),
  (10104, 10102, 104),
  (10105, 10103, 102),
  (10106, 10104, 103),
  (10107, 10104, 104),
  (10108, 10105, 101),
  (10109, 10106, 105),
  (10110, 10107, 105),
  (10201, 10201, 111),
  (10202, 10201, 113),
  (10203, 10202, 111),
  (10204, 10203, 111),
  (10205, 10203, 114),
  (10206, 10204, 113),
  (10207, 10204, 114),
  (10208, 10205, 112),
  (10209, 10206, 113),
  (10301, 10301, 121),
  (10302, 10302, 123),
  (10303, 10303, 122),
  (10304, 10304, 122),
  (10305, 10305, 123)
ON CONFLICT(id) DO UPDATE SET
  task_id = excluded.task_id,
  tag_id = excluded.tag_id;

INSERT INTO mk_task_refer_user (
  id,
  task_id,
  user_id
) VALUES
  (10101, 10101, 101),
  (10102, 10101, 102),
  (10103, 10102, 1),
  (10104, 10102, 101),
  (10105, 10103, 102),
  (10106, 10104, 102),
  (10107, 10105, 103),
  (10108, 10106, 101),
  (10109, 10107, 101),
  (10201, 10201, 1),
  (10202, 10201, 101),
  (10203, 10202, 101),
  (10204, 10203, 1),
  (10205, 10203, 102),
  (10206, 10204, 1),
  (10207, 10205, 102),
  (10208, 10206, 1),
  (10301, 10301, 102),
  (10302, 10302, 101),
  (10303, 10303, 101),
  (10304, 10303, 102),
  (10305, 10304, 103),
  (10306, 10305, 102)
ON CONFLICT(id) DO UPDATE SET
  task_id = excluded.task_id,
  user_id = excluded.user_id;

INSERT INTO mk_task_comment (
  id,
  task_id,
  user_id,
  content
) VALUES
  (10101, 10102, 1, '这里先按紧急任务处理，联调时重点看登录后的跳转链路。'),
  (10102, 10104, 102, '抽屉滚动和底部操作区需要在小屏真机上再确认一次。'),
  (10201, 10203, 101, '任务移动接口要兼容拖拽和按钮流转两个入口。'),
  (10202, 10204, 1, 'XML 映射测试已覆盖 sectionVOS、tags 和 referUserIds。'),
  (10301, 10303, 102, '埋点命名需要在接口联调前冻结。')
ON CONFLICT(id) DO UPDATE SET
  task_id = excluded.task_id,
  user_id = excluded.user_id,
  content = excluded.content,
  updated_time = CURRENT_TIMESTAMP,
  deleted = 0,
  deleted_time = NULL;

INSERT INTO mk_task_activity (
  id,
  task_id,
  actor_id,
  action,
  before_value,
  after_value,
  remark
) VALUES
  (10101, 10102, 1, 'move', '{"section":"backlog"}', '{"section":"todo"}', '登录跳转任务进入待处理。'),
  (10102, 10104, 102, 'update', '{"priority":1}', '{"priority":2}', '任务详情抽屉提升为优先级。'),
  (10201, 10203, 101, 'update', '{"blocked":false}', '{"blocked":true}', '任务移动接口依赖权限校验，暂时标记阻塞。'),
  (10202, 10204, 1, 'create', NULL, '{"section":"doing"}', '新增 XML 联查映射测试任务。'),
  (10301, 10303, 102, 'comment', NULL, '{"comment":"埋点命名需要冻结"}', '补充运营埋点备注。')
ON CONFLICT(id) DO UPDATE SET
  task_id = excluded.task_id,
  actor_id = excluded.actor_id,
  action = excluded.action,
  before_value = excluded.before_value,
  after_value = excluded.after_value,
  remark = excluded.remark;
