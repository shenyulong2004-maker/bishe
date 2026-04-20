package com.cl.service;

import com.baomidou.mybatisplus.service.IService;
import com.cl.entity.ParentMeetingEntity;

import java.util.List;
import java.util.Map;

/**
 * 在线家长会/会议预约
 */
public interface ParentMeetingService extends IService<ParentMeetingEntity> {

    List<ParentMeetingEntity> listByCurrentUser(Long userId, String role, String tableName);

    Map<String, Object> parentBookOneToOne(Long parentId, ParentMeetingEntity body);

    Map<String, Object> teacherCreateGroup(Long teacherId, ParentMeetingEntity body, List<Long> parentIds);

    ParentMeetingEntity getMeetingDetail(Long userId, String role, String tableName, Long id);

    Map<String, Object> confirmByParent(Long parentId, Long id);

    Map<String, Object> confirmByTeacher(Long teacherId, Long id);

    Map<String, Object> finishByTeacher(Long teacherId, Long id);

    Map<String, Object> cancelMeeting(Long userId, String role, String tableName, Long id);
}
