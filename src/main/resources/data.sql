INSERT INTO mk_user (
  id,
  username,
  password,
  salt,
  nickname,
  email,
  status
) VALUES (
  1,
  'admin',
  '$2a$10$3xPuywCsbpJQzVUa9MYU1OyUxmWOWnUdatZPGxZO1ehs/3CP1BZjO',
  'BCrypt',
  '管理员',
  'admin@meowkanban.local',
  1
) ON CONFLICT(id) DO UPDATE SET
  password = excluded.password,
  salt = excluded.salt
WHERE mk_user.password = 'CHANGE_ME_WHEN_AUTH_IS_IMPLEMENTED';

INSERT INTO mk_role (
  id,
  role_code,
  role_name,
  description,
  sort_order
) VALUES
  (1, 'ROLE_ADMIN', '系统管理员', '拥有系统管理权限和业务操作权限', 10),
  (2, 'ROLE_USER', '普通用户', '拥有基础业务操作权限', 20),
  (3, 'ROLE_VIEWER', '只读用户', '仅拥有基础查看权限', 30)
ON CONFLICT(id) DO NOTHING;

INSERT INTO mk_user_role (
  id,
  user_id,
  role_id,
  created_by
) VALUES (
  1,
  1,
  1,
  1
) ON CONFLICT(id) DO NOTHING;

INSERT INTO mk_board (
  id,
  name,
  description,
  owner_id,
  visibility
) VALUES (
  1,
  'MeowKanban',
  '轻量项目看板',
  1,
  0
) ON CONFLICT(id) DO NOTHING;

INSERT INTO mk_board_member (
  id,
  board_id,
  user_id,
  role
) VALUES (
  1,
  1,
  1,
  'owner'
) ON CONFLICT(id) DO NOTHING;

INSERT INTO mk_board_section (
  id,
  board_id,
  code,
  title,
  sort_order
) VALUES
  (1, 1, 'backlog', '待规划', 10),
  (2, 1, 'todo', '待处理', 20),
  (3, 1, 'doing', '进行中', 30),
  (4, 1, 'review', '验收', 40),
  (5, 1, 'done', '完成', 50)
ON CONFLICT(id) DO NOTHING;

INSERT INTO mk_tag (
  id,
  board_id,
  name,
  color
) VALUES
  (1, 1, '产品', NULL),
  (2, 1, '信息架构', NULL),
  (3, 1, '前端', NULL),
  (4, 1, '交互', NULL),
  (5, 1, '后端', NULL),
  (6, 1, '模型', NULL),
  (7, 1, '统计', NULL),
  (8, 1, '异常', NULL),
  (9, 1, '测试', NULL),
  (10, 1, '验收', NULL),
  (11, 1, '数据', NULL),
  (12, 1, '复盘', NULL)
ON CONFLICT(id) DO NOTHING;

INSERT INTO mk_task (
  id,
  board_id,
  section_id,
  task_no,
  title,
  description,
  owner_id,
  due_date,
  priority,
  blocked,
  sort_order,
  created_by,
  updated_by
) VALUES
  (1, 1, 1, 'MK-102', '梳理移动端任务详情信息架构', '确认任务详情页需要展示的字段、评论入口、附件入口和状态流转规则。', 1, '2026-06-04', 'urgent', 0, 10, 1, 1),
  (2, 1, 2, 'MK-118', '补全看板列拖拽交互', '支持跨列移动任务，并在卡片详情中同步显示最新状态。', 1, '2026-06-05', 'normal', 0, 20, 1, 1),
  (3, 1, 2, 'MK-121', '定义任务优先级与风险枚举', '为后续接口、统计视图和通知规则提供统一数据结构。', 1, '2026-06-06', 'normal', 0, 30, 1, 1),
  (4, 1, 3, 'MK-126', '实现项目容量概览组件', '展示本周任务容量、风险项数量、进行中任务和预计交付进度。', 1, '2026-06-03', 'urgent', 0, 40, 1, 1),
  (5, 1, 3, 'MK-131', '修复附件上传限制提示', '当前异常提示文案存在编码问题，需要统一为 UTF-8 并补充大小限制说明。', 1, '2026-06-07', 'urgent', 1, 50, 1, 1),
  (6, 1, 4, 'MK-136', '验收搜索与筛选空状态', '检查搜索、快捷筛选和隐藏完成任务后的空列展示。', 1, '2026-06-08', 'normal', 0, 60, 1, 1),
  (7, 1, 5, 'MK-139', '整理 Sprint 复盘指标', '汇总本周完成量、返工率和阻塞时长，为统计页提供样例数据。', 1, '2026-06-09', 'normal', 0, 70, 1, 1)
ON CONFLICT(id) DO NOTHING;

INSERT INTO mk_task_tag (
  id,
  task_id,
  tag_id
) VALUES
  (1, 1, 1),
  (2, 1, 2),
  (3, 2, 3),
  (4, 2, 4),
  (5, 3, 5),
  (6, 3, 6),
  (7, 4, 3),
  (8, 4, 7),
  (9, 5, 5),
  (10, 5, 8),
  (11, 6, 9),
  (12, 6, 10),
  (13, 7, 11),
  (14, 7, 12)
ON CONFLICT(id) DO NOTHING;

INSERT INTO mk_user_preference (
  id,
  user_id,
  preference_key,
  preference_value
) VALUES
  (1, 1, 'sidebarCollapsed', 'false'),
  (2, 1, 'hideDone', 'false'),
  (3, 1, 'lastBoardId', '1')
ON CONFLICT(id) DO NOTHING;
