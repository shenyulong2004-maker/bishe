-- 修复试题持久化问题
-- 问题：examquestion 表主键为 AUTO_INCREMENT，但后端使用自定义ID，导致数据丢失
-- 解决：修改主键配置为非自增，允许用户输入ID

USE cl21002101;

-- 1. 备份原表数据
CREATE TABLE examquestion_backup AS SELECT * FROM examquestion;

-- 2. 删除原表
DROP TABLE examquestion;

-- 3. 重建表，主键改为非自增
CREATE TABLE `examquestion` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `paperid` bigint(20) NOT NULL COMMENT '试卷id（外键）',
  `papername` varchar(200) NOT NULL COMMENT '试卷名称',
  `questionname` varchar(200) NOT NULL COMMENT '试题名称',
  `options` longtext COMMENT '选项，json字符串',
  `score` bigint(20) DEFAULT NULL COMMENT '分值',
  `answer` varchar(200) DEFAULT NULL COMMENT '正确答案',
  `analysis` longtext COMMENT '答案解析',
  `type` bigint(20) DEFAULT NULL COMMENT '试题类型',
  `sequence` bigint(20) DEFAULT NULL COMMENT '试题排序，值越大排越前面',
  `images` varchar(1000) COMMENT '题目图片/PDF附件（逗号分隔多个文件路径）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='试题管理';

-- 4. 恢复数据
INSERT INTO examquestion SELECT * FROM examquestion_backup;

-- 5. 验证
SELECT COUNT(*) as total_questions FROM examquestion;
DESCRIBE examquestion;
