-- 创建数据库
CREATE DATABASE IF NOT EXISTS homework_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE homework_db;

-- 创建作业表
CREATE TABLE IF NOT EXISTS homework (
  id INT AUTO_INCREMENT PRIMARY KEY,
  content LONGTEXT NOT NULL COMMENT '作业内容',
  isPermanent TINYINT(1) DEFAULT 0 COMMENT '是否永久保存',
  attachmentUrl VARCHAR(500) COMMENT '附件地址',
  createTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_createTime (createTime),
  INDEX idx_isPermanent (isPermanent)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
