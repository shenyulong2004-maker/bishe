-- 为 examrecord 表新增解答题附件字段
ALTER TABLE examrecord ADD COLUMN myanswerfiles TEXT COMMENT '解答题作答附件，多个用逗号分隔' AFTER myanswer;
