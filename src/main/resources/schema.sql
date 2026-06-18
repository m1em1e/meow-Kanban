CREATE TABLE IF NOT EXISTS mk_user (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(255) NOT NULL,
  salt VARCHAR(100) NOT NULL,
  nickname VARCHAR(50) NOT NULL,
  email VARCHAR(120),
  avatar_resource_id INTEGER,
  status VARCHAR(20) NOT NULL DEFAULT 'active',
  last_login_time DATETIME,
  created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted INTEGER NOT NULL DEFAULT 0,
  deleted_time DATETIME
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_mk_user_username ON mk_user(username);
CREATE UNIQUE INDEX IF NOT EXISTS uk_mk_user_email ON mk_user(email);

CREATE TABLE IF NOT EXISTS mk_role (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  role_code VARCHAR(50) NOT NULL,
  role_name VARCHAR(50) NOT NULL,
  description VARCHAR(255),
  status VARCHAR(20) NOT NULL DEFAULT 'active',
  sort_order INTEGER NOT NULL DEFAULT 0,
  created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted INTEGER NOT NULL DEFAULT 0,
  deleted_time DATETIME
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_mk_role_code ON mk_role(role_code);
CREATE INDEX IF NOT EXISTS idx_mk_role_status ON mk_role(status);

CREATE TABLE IF NOT EXISTS mk_user_role (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  user_id INTEGER NOT NULL,
  role_id INTEGER NOT NULL,
  created_by INTEGER,
  created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted INTEGER NOT NULL DEFAULT 0,
  deleted_time DATETIME
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_mk_user_role_user_role ON mk_user_role(user_id, role_id);
CREATE INDEX IF NOT EXISTS idx_mk_user_role_role_id ON mk_user_role(role_id);

CREATE TABLE IF NOT EXISTS mk_file_resource (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  file_name VARCHAR(255) NOT NULL,
  storage_path VARCHAR(500) NOT NULL,
  content_type VARCHAR(100),
  file_size INTEGER NOT NULL DEFAULT 0,
  uploaded_by INTEGER,
  created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted INTEGER NOT NULL DEFAULT 0,
  deleted_time DATETIME
);

CREATE INDEX IF NOT EXISTS idx_mk_file_resource_uploaded_by ON mk_file_resource(uploaded_by);

CREATE TABLE IF NOT EXISTS mk_board (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(500),
  cover_resource_id INTEGER,
  owner_id INTEGER NOT NULL,
  visibility INTEGER NOT NULL DEFAULT 0,
  created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted INTEGER NOT NULL DEFAULT 0,
  deleted_time DATETIME
);

CREATE INDEX IF NOT EXISTS idx_mk_board_owner_id ON mk_board(owner_id);
CREATE INDEX IF NOT EXISTS idx_mk_board_deleted ON mk_board(deleted);

CREATE TABLE IF NOT EXISTS mk_board_member (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  board_id INTEGER NOT NULL,
  user_id INTEGER NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'member',
  joined_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted INTEGER NOT NULL DEFAULT 0,
  deleted_time DATETIME
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_mk_board_member_board_user ON mk_board_member(board_id, user_id);
CREATE INDEX IF NOT EXISTS idx_mk_board_member_user_id ON mk_board_member(user_id);

CREATE TABLE IF NOT EXISTS mk_board_favorite (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  board_id INTEGER NOT NULL,
  user_id INTEGER NOT NULL,
  created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted INTEGER NOT NULL DEFAULT 0,
  deleted_time DATETIME
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_mk_board_favorite_user_board ON mk_board_favorite(user_id, board_id);
CREATE INDEX IF NOT EXISTS idx_mk_board_favorite_board_id ON mk_board_favorite(board_id);

CREATE TABLE IF NOT EXISTS mk_board_recent (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  board_id INTEGER NOT NULL,
  user_id INTEGER NOT NULL,
  last_active_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  active_count INTEGER NOT NULL DEFAULT 1,
  created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted INTEGER NOT NULL DEFAULT 0,
  deleted_time DATETIME
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_mk_board_recent_user_board ON mk_board_recent(user_id, board_id);
CREATE INDEX IF NOT EXISTS idx_mk_board_recent_user_active_time ON mk_board_recent(user_id, last_active_time);
CREATE INDEX IF NOT EXISTS idx_mk_board_recent_board_id ON mk_board_recent(board_id);

CREATE TABLE IF NOT EXISTS mk_board_section (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  board_id INTEGER NOT NULL,
  code VARCHAR(50) NOT NULL,
  title VARCHAR(50) NOT NULL,
  sort_order INTEGER NOT NULL DEFAULT 0,
  created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted INTEGER NOT NULL DEFAULT 0,
  deleted_time DATETIME
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_mk_board_section_board_code ON mk_board_section(board_id, code);
CREATE INDEX IF NOT EXISTS idx_mk_board_section_board_sort ON mk_board_section(board_id, sort_order);

CREATE TABLE IF NOT EXISTS mk_task (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  board_id INTEGER NOT NULL,
  section_id INTEGER NOT NULL,
  task_no VARCHAR(30) NOT NULL,
  title VARCHAR(200) NOT NULL,
  description TEXT,
  owner_id INTEGER,
  due_date DATE,
  priority VARCHAR(20) NOT NULL DEFAULT 'normal',
  blocked INTEGER NOT NULL DEFAULT 0,
  sort_order INTEGER NOT NULL DEFAULT 0,
  created_by INTEGER,
  updated_by INTEGER,
  created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted INTEGER NOT NULL DEFAULT 0,
  deleted_time DATETIME
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_mk_task_board_task_no ON mk_task(board_id, task_no);
CREATE INDEX IF NOT EXISTS idx_mk_task_board_section_sort ON mk_task(board_id, section_id, sort_order);
CREATE INDEX IF NOT EXISTS idx_mk_task_owner_id ON mk_task(owner_id);
CREATE INDEX IF NOT EXISTS idx_mk_task_due_date ON mk_task(due_date);
CREATE INDEX IF NOT EXISTS idx_mk_task_priority ON mk_task(priority);
CREATE INDEX IF NOT EXISTS idx_mk_task_blocked ON mk_task(blocked);

CREATE TABLE IF NOT EXISTS mk_tag (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  board_id INTEGER NOT NULL,
  name VARCHAR(30) NOT NULL,
  color VARCHAR(30),
  created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted INTEGER NOT NULL DEFAULT 0,
  deleted_time DATETIME
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_mk_tag_board_name ON mk_tag(board_id, name);

CREATE TABLE IF NOT EXISTS mk_task_tag (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  task_id INTEGER NOT NULL,
  tag_id INTEGER NOT NULL,
  created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_mk_task_tag_task_tag ON mk_task_tag(task_id, tag_id);
CREATE INDEX IF NOT EXISTS idx_mk_task_tag_tag_id ON mk_task_tag(tag_id);

CREATE TABLE IF NOT EXISTS mk_task_comment (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  task_id INTEGER NOT NULL,
  user_id INTEGER NOT NULL,
  content TEXT NOT NULL,
  created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted INTEGER NOT NULL DEFAULT 0,
  deleted_time DATETIME
);

CREATE INDEX IF NOT EXISTS idx_mk_task_comment_task_id ON mk_task_comment(task_id);
CREATE INDEX IF NOT EXISTS idx_mk_task_comment_user_id ON mk_task_comment(user_id);

CREATE TABLE IF NOT EXISTS mk_task_attachment (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  task_id INTEGER NOT NULL,
  file_name VARCHAR(255) NOT NULL,
  storage_path VARCHAR(500) NOT NULL,
  content_type VARCHAR(100),
  file_size INTEGER NOT NULL DEFAULT 0,
  uploaded_by INTEGER NOT NULL,
  created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted INTEGER NOT NULL DEFAULT 0,
  deleted_time DATETIME
);

CREATE INDEX IF NOT EXISTS idx_mk_task_attachment_task_id ON mk_task_attachment(task_id);
CREATE INDEX IF NOT EXISTS idx_mk_task_attachment_uploaded_by ON mk_task_attachment(uploaded_by);

CREATE TABLE IF NOT EXISTS mk_task_activity (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  task_id INTEGER NOT NULL,
  actor_id INTEGER,
  action VARCHAR(30) NOT NULL,
  before_value TEXT,
  after_value TEXT,
  remark VARCHAR(500),
  created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_mk_task_activity_task_id ON mk_task_activity(task_id);
CREATE INDEX IF NOT EXISTS idx_mk_task_activity_actor_id ON mk_task_activity(actor_id);
CREATE INDEX IF NOT EXISTS idx_mk_task_activity_created_time ON mk_task_activity(created_time);

CREATE TABLE IF NOT EXISTS mk_user_preference (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  user_id INTEGER NOT NULL,
  preference_key VARCHAR(100) NOT NULL,
  preference_value TEXT,
  created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_mk_user_preference_user_key ON mk_user_preference(user_id, preference_key);
