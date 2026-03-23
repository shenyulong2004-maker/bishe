USE cl21002101;

-- 添加 images 字段
ALTER TABLE examquestion ADD COLUMN images VARCHAR(1000) DEFAULT NULL COMMENT '题目图片/PDF附件';

-- 验证
DESCRIBE examquestion;
USE cl21002101;

-- 添加 myanswerfiles 字段到 examrecord 表
ALTER TABLE examrecord ADD COLUMN myanswerfiles VARCHAR(500) DEFAULT NULL COMMENT '解答题作答附件';

-- 验证
DESCRIBE examrecord;examquestion