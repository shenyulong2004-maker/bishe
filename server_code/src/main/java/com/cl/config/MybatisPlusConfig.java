package com.cl.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.baomidou.mybatisplus.plugins.PaginationInterceptor;

/**
 * mybatis-plus配置
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    /**
     * 启动时确保在线家长会相关表与字段存在（兼容旧库）
     */
    @Bean
    public CommandLineRunner initParentMeetingTables(JdbcTemplate jdbcTemplate) {
        return args -> {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `parent_meeting` ("
                    + "`id` bigint(20) NOT NULL COMMENT '主键',"
                    + "`meeting_type` tinyint(2) NOT NULL DEFAULT '1' COMMENT '会议类型：1家长一对一预约 2教师群体家长会',"
                    + "`title` varchar(200) DEFAULT NULL COMMENT '会议标题',"
                    + "`creator_role` varchar(20) NOT NULL COMMENT '创建者角色：parent/teacher',"
                    + "`creator_id` bigint(20) NOT NULL COMMENT '创建者ID',"
                    + "`parent_id` bigint(20) DEFAULT NULL COMMENT '家长ID（单聊）',"
                    + "`teacher_id` bigint(20) NOT NULL COMMENT '教师ID',"
                    + "`meeting_time` datetime NOT NULL COMMENT '会议时间',"
                    + "`status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '状态：0待确认 1已确认 2已完成 3已取消',"
                    + "`room_id` varchar(100) DEFAULT NULL COMMENT '会议室ID',"
                    + "`scope` varchar(20) NOT NULL DEFAULT 'single' COMMENT '范围：single/group',"
                    + "`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',"
                    + "PRIMARY KEY (`id`)"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='在线家长会/会议预约'");

            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `parent_meeting_attendee` ("
                    + "`id` bigint(20) NOT NULL COMMENT '主键',"
                    + "`meeting_id` bigint(20) NOT NULL COMMENT '会议ID',"
                    + "`parent_id` bigint(20) NOT NULL COMMENT '家长ID',"
                    + "`join_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '参会状态：0待确认 1已确认 2已拒绝',"
                    + "`confirm_time` datetime DEFAULT NULL COMMENT '确认时间',"
                    + "`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',"
                    + "PRIMARY KEY (`id`)"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会议参会家长'");

            ensureColumn(jdbcTemplate, "parent_meeting", "meeting_type",
                    "ALTER TABLE `parent_meeting` ADD COLUMN `meeting_type` tinyint(2) NOT NULL DEFAULT '1' COMMENT '会议类型：1家长一对一预约 2教师群体家长会'");
            ensureColumn(jdbcTemplate, "parent_meeting", "title",
                    "ALTER TABLE `parent_meeting` ADD COLUMN `title` varchar(200) DEFAULT NULL COMMENT '会议标题'");
            ensureColumn(jdbcTemplate, "parent_meeting", "creator_role",
                    "ALTER TABLE `parent_meeting` ADD COLUMN `creator_role` varchar(20) DEFAULT 'parent' COMMENT '创建者角色：parent/teacher'");
            ensureColumn(jdbcTemplate, "parent_meeting", "creator_id",
                    "ALTER TABLE `parent_meeting` ADD COLUMN `creator_id` bigint(20) DEFAULT NULL COMMENT '创建者ID'");
            ensureColumn(jdbcTemplate, "parent_meeting", "parent_id",
                    "ALTER TABLE `parent_meeting` ADD COLUMN `parent_id` bigint(20) DEFAULT NULL COMMENT '家长ID（单聊）'");
            ensureColumn(jdbcTemplate, "parent_meeting", "teacher_id",
                    "ALTER TABLE `parent_meeting` ADD COLUMN `teacher_id` bigint(20) DEFAULT NULL COMMENT '教师ID'");
            ensureColumn(jdbcTemplate, "parent_meeting", "meeting_time",
                    "ALTER TABLE `parent_meeting` ADD COLUMN `meeting_time` datetime DEFAULT NULL COMMENT '会议时间'");
            ensureColumn(jdbcTemplate, "parent_meeting", "status",
                    "ALTER TABLE `parent_meeting` ADD COLUMN `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '状态：0待确认 1已确认 2已完成 3已取消'");
            ensureColumn(jdbcTemplate, "parent_meeting", "room_id",
                    "ALTER TABLE `parent_meeting` ADD COLUMN `room_id` varchar(100) DEFAULT NULL COMMENT '会议室ID'");
            ensureColumn(jdbcTemplate, "parent_meeting", "scope",
                    "ALTER TABLE `parent_meeting` ADD COLUMN `scope` varchar(20) NOT NULL DEFAULT 'single' COMMENT '范围：single/group'");
            ensureColumn(jdbcTemplate, "parent_meeting", "create_time",
                    "ALTER TABLE `parent_meeting` ADD COLUMN `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'");

            ensureColumn(jdbcTemplate, "parent_meeting_attendee", "meeting_id",
                    "ALTER TABLE `parent_meeting_attendee` ADD COLUMN `meeting_id` bigint(20) NOT NULL COMMENT '会议ID'");
            ensureColumn(jdbcTemplate, "parent_meeting_attendee", "parent_id",
                    "ALTER TABLE `parent_meeting_attendee` ADD COLUMN `parent_id` bigint(20) NOT NULL COMMENT '家长ID'");
            ensureColumn(jdbcTemplate, "parent_meeting_attendee", "join_status",
                    "ALTER TABLE `parent_meeting_attendee` ADD COLUMN `join_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '参会状态：0待确认 1已确认 2已拒绝'");
            ensureColumn(jdbcTemplate, "parent_meeting_attendee", "confirm_time",
                    "ALTER TABLE `parent_meeting_attendee` ADD COLUMN `confirm_time` datetime DEFAULT NULL COMMENT '确认时间'");
            ensureColumn(jdbcTemplate, "parent_meeting_attendee", "create_time",
                    "ALTER TABLE `parent_meeting_attendee` ADD COLUMN `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'");
        };
    }

    private void ensureColumn(JdbcTemplate jdbcTemplate, String tableName, String columnName, String alterSql) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?",
                Integer.class,
                tableName,
                columnName
        );
        if (count == null || count == 0) {
            jdbcTemplate.execute(alterSql);
        }
    }
}
