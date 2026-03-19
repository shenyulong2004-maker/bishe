START TRANSACTION;

SET @backup_suffix = DATE_FORMAT(NOW(), '%Y%m%d_%H%i%S');

SET @sql = CONCAT('CREATE TABLE IF NOT EXISTS jiazhang_backup_', @backup_suffix, ' AS SELECT * FROM jiazhang');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = CONCAT('CREATE TABLE IF NOT EXISTS xuesheng_backup_', @backup_suffix, ' AS SELECT * FROM xuesheng');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'xuesheng'
    AND COLUMN_NAME = 'jiazhangzhanghao'
);
SET @sql = IF(@col_exists > 0, 'ALTER TABLE xuesheng DROP COLUMN jiazhangzhanghao', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'xuesheng'
    AND COLUMN_NAME = 'jiazhangxingming'
);
SET @sql = IF(@col_exists > 0, 'ALTER TABLE xuesheng DROP COLUMN jiazhangxingming', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

DROP TABLE IF EXISTS jiazhang;

COMMIT;
