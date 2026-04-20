package com.cl.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.dao.ParentMeetingAttendeeDao;
import com.cl.dao.ParentMeetingDao;
import com.cl.entity.ParentMeetingAttendeeEntity;
import com.cl.entity.ParentMeetingEntity;
import com.cl.service.ParentMeetingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

/**
 * 在线家长会/会议预约
 */
@Service("parentMeetingService")
public class ParentMeetingServiceImpl extends ServiceImpl<ParentMeetingDao, ParentMeetingEntity> implements ParentMeetingService {

    @Autowired
    private ParentMeetingAttendeeDao attendeeDao;

    @Override
    public List<ParentMeetingEntity> listByCurrentUser(Long userId, String role, String tableName) {
        if (isTeacher(role, tableName)) {
            return this.selectList(new EntityWrapper<ParentMeetingEntity>()
                    .eq("teacher_id", userId)
                    .orderBy("meeting_time", true));
        }
        if (isParent(role, tableName)) {
            List<ParentMeetingAttendeeEntity> mine = attendeeDao.selectList(new EntityWrapper<ParentMeetingAttendeeEntity>()
                    .eq("parent_id", userId));
            if (mine == null || mine.isEmpty()) return new ArrayList<>();
            List<Long> ids = new ArrayList<>();
            Map<Long, Integer> joinMap = new HashMap<>();
            for (ParentMeetingAttendeeEntity a : mine) {
                ids.add(a.getMeetingId());
                joinMap.put(a.getMeetingId(), a.getJoinStatus());
            }
            List<ParentMeetingEntity> list = this.selectList(new EntityWrapper<ParentMeetingEntity>()
                    .in("id", ids)
                    .orderBy("meeting_time", true));
            for (ParentMeetingEntity row : list) {
                row.setMineJoinStatus(joinMap.get(row.getId()));
            }
            return list;
        }
        return new ArrayList<>();
    }

    @Override
    public Map<String, Object> parentBookOneToOne(Long parentId, ParentMeetingEntity body) {
        Map<String, Object> ret = new HashMap<>();
        if (body == null || body.getTeacherId() == null || body.getMeetingTime() == null) {
            ret.put("ok", false); ret.put("msg", "参数错误"); return ret;
        }

        try {
            Integer parentConflict = attendeeDao.selectCount(new EntityWrapper<ParentMeetingAttendeeEntity>()
                    .eq("parent_id", parentId)
                    .in("join_status", new Integer[]{0, 1}));
            if (parentConflict != null && parentConflict > 0) {
                List<ParentMeetingAttendeeEntity> mine = attendeeDao.selectList(new EntityWrapper<ParentMeetingAttendeeEntity>()
                        .eq("parent_id", parentId).in("join_status", new Integer[]{0,1}));
                if (mine != null) {
                    for (ParentMeetingAttendeeEntity a : mine) {
                        ParentMeetingEntity m = this.selectById(a.getMeetingId());
                        if (m != null && m.getMeetingTime() != null && m.getMeetingTime().equals(body.getMeetingTime()) && m.getStatus() != null && (m.getStatus() == 0 || m.getStatus() == 1)) {
                            ret.put("ok", false); ret.put("msg", "同一时间段只能预约一位老师"); return ret;
                        }
                    }
                }
            }

            Integer teacherConflict = this.selectCount(new EntityWrapper<ParentMeetingEntity>()
                    .eq("teacher_id", body.getTeacherId())
                    .eq("meeting_time", body.getMeetingTime())
                    .in("status", new Integer[]{0, 1}));
            if (teacherConflict != null && teacherConflict > 0) {
                ret.put("ok", false); ret.put("msg", "该教师该时间段已有会议"); return ret;
            }

            Long meetingId = generateMeetingId();
            ParentMeetingEntity row = new ParentMeetingEntity();
            row.setId(meetingId);
            row.setMeetingType(1);
            row.setTitle(StringUtils.isBlank(body.getTitle()) ? "一对一家长沟通" : body.getTitle().trim());
            row.setCreatorRole("parent");
            row.setCreatorId(parentId);
            row.setParentId(parentId);
            row.setTeacherId(body.getTeacherId());
            row.setMeetingTime(body.getMeetingTime());
            row.setStatus(0);
            row.setRoomId(StringUtils.isBlank(body.getRoomId()) ? ("ROOM-" + row.getId()) : body.getRoomId().trim());
            row.setScope("single");
            row.setCreateTime(new Date());
            boolean inserted = this.insert(row);
            if (!inserted) {
                ret.put("ok", false); ret.put("msg", "预约创建失败"); return ret;
            }

            ParentMeetingAttendeeEntity attendee = new ParentMeetingAttendeeEntity();
            attendee.setId(generateAttendeeId());
            attendee.setMeetingId(meetingId);
            attendee.setParentId(parentId);
            attendee.setJoinStatus(0);
            attendee.setCreateTime(new Date());
            attendeeDao.insert(attendee);

            ret.put("ok", true); ret.put("data", row); return ret;
        } catch (Exception e) {
            ret.put("ok", false);
            ret.put("msg", e.getMessage() == null ? "预约创建失败" : e.getMessage());
            return ret;
        }
    }

    @Override
    public Map<String, Object> teacherCreateGroup(Long teacherId, ParentMeetingEntity body, List<Long> parentIds) {
        Map<String, Object> ret = new HashMap<>();
        if (body == null || body.getMeetingTime() == null || parentIds == null || parentIds.isEmpty()) {
            ret.put("ok", false); ret.put("msg", "参数错误"); return ret;
        }
        Integer teacherConflict = this.selectCount(new EntityWrapper<ParentMeetingEntity>()
                .eq("teacher_id", teacherId)
                .eq("meeting_time", body.getMeetingTime())
                .in("status", new Integer[]{0, 1}));
        if (teacherConflict != null && teacherConflict > 0) {
            ret.put("ok", false); ret.put("msg", "该时间段已有会议"); return ret;
        }

        Long meetingId = new Date().getTime() + (long) Math.floor(Math.random() * 1000);
        ParentMeetingEntity row = new ParentMeetingEntity();
        row.setId(meetingId);
        row.setMeetingType(2);
        row.setTitle(StringUtils.isBlank(body.getTitle()) ? "家长会" : body.getTitle().trim());
        row.setCreatorRole("teacher");
        row.setCreatorId(teacherId);
        row.setParentId(null);
        row.setTeacherId(teacherId);
        row.setMeetingTime(body.getMeetingTime());
        row.setStatus(0);
        row.setRoomId(StringUtils.isBlank(body.getRoomId()) ? ("ROOM-" + row.getId()) : body.getRoomId().trim());
        row.setScope("group");
        row.setCreateTime(new Date());
        this.insert(row);

        for (Long pid : parentIds) {
            ParentMeetingAttendeeEntity attendee = new ParentMeetingAttendeeEntity();
            attendee.setId(new Date().getTime() + (long) Math.floor(Math.random() * 1000));
            attendee.setMeetingId(meetingId);
            attendee.setParentId(pid);
            attendee.setJoinStatus(0);
            attendee.setCreateTime(new Date());
            attendeeDao.insert(attendee);
        }

        ret.put("ok", true); ret.put("data", row); return ret;
    }

    @Override
    public ParentMeetingEntity getMeetingDetail(Long userId, String role, String tableName, Long id) {
        if (id == null) return null;
        ParentMeetingEntity row = this.selectById(id);
        if (row == null) return null;
        if (isTeacher(role, tableName) && userId.equals(row.getTeacherId())) return row;
        if (isParent(role, tableName)) {
            List<ParentMeetingAttendeeEntity> attendees = attendeeDao.selectList(new EntityWrapper<ParentMeetingAttendeeEntity>()
                    .eq("meeting_id", id)
                    .eq("parent_id", userId));
            ParentMeetingAttendeeEntity attendee = (attendees == null || attendees.isEmpty()) ? null : attendees.get(0);
            if (attendee != null) {
                row.setMineJoinStatus(attendee.getJoinStatus());
                return row;
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> confirmByParent(Long parentId, Long id) {
        Map<String, Object> ret = new HashMap<>();
        List<ParentMeetingAttendeeEntity> attendees = attendeeDao.selectList(new EntityWrapper<ParentMeetingAttendeeEntity>()
                .eq("meeting_id", id).eq("parent_id", parentId));
        ParentMeetingAttendeeEntity attendee = (attendees == null || attendees.isEmpty()) ? null : attendees.get(0);
        if (attendee == null) { ret.put("ok", false); ret.put("msg", "会议不存在"); return ret; }
        attendee.setJoinStatus(1);
        attendee.setConfirmTime(new Date());
        attendeeDao.updateById(attendee);

        ParentMeetingEntity meeting = this.selectById(id);
        if (meeting != null && (meeting.getStatus() == null || meeting.getStatus() == 0)) {
            meeting.setStatus(1);
            this.updateById(meeting);
        }
        ret.put("ok", true); return ret;
    }

    @Override
    public Map<String, Object> confirmByTeacher(Long teacherId, Long id) {
        Map<String, Object> ret = new HashMap<>();
        ParentMeetingEntity meeting = this.selectById(id);
        if (meeting == null || !teacherId.equals(meeting.getTeacherId())) {
            ret.put("ok", false); ret.put("msg", "无权限"); return ret;
        }
        if (meeting.getStatus() != null && meeting.getStatus() == 3) {
            ret.put("ok", false); ret.put("msg", "会议已取消"); return ret;
        }
        if (meeting.getStatus() == null || meeting.getStatus() == 0) {
            meeting.setStatus(1);
            this.updateById(meeting);
        }
        ret.put("ok", true); return ret;
    }

    @Override
    public Map<String, Object> finishByTeacher(Long teacherId, Long id) {
        Map<String, Object> ret = new HashMap<>();
        ParentMeetingEntity meeting = this.selectById(id);
        if (meeting == null || !teacherId.equals(meeting.getTeacherId())) {
            ret.put("ok", false); ret.put("msg", "无权限"); return ret;
        }
        meeting.setStatus(2);
        this.updateById(meeting);
        ret.put("ok", true); return ret;
    }

    @Override
    public Map<String, Object> cancelMeeting(Long userId, String role, String tableName, Long id) {
        Map<String, Object> ret = new HashMap<>();
        ParentMeetingEntity row = this.selectById(id);
        if (row == null) { ret.put("ok", false); ret.put("msg", "预约不存在"); return ret; }

        if (isTeacher(role, tableName) && userId.equals(row.getTeacherId())) {
            row.setStatus(3);
            this.updateById(row);
            ret.put("ok", true);
            return ret;
        }

        if (isParent(role, tableName)) {
            List<ParentMeetingAttendeeEntity> attendees = attendeeDao.selectList(new EntityWrapper<ParentMeetingAttendeeEntity>()
                    .eq("meeting_id", id).eq("parent_id", userId));
            ParentMeetingAttendeeEntity attendee = (attendees == null || attendees.isEmpty()) ? null : attendees.get(0);
            if (attendee == null) { ret.put("ok", false); ret.put("msg", "无权限"); return ret; }

            if (row.getMeetingType() != null && row.getMeetingType() == 2) {
                attendee.setJoinStatus(2);
                attendee.setConfirmTime(new Date());
                attendeeDao.updateById(attendee);
                ret.put("ok", true);
                return ret;
            }

            row.setStatus(3);
            this.updateById(row);
            ret.put("ok", true);
            return ret;
        }

        ret.put("ok", false);
        ret.put("msg", "无权限");
        return ret;
    }

    private boolean isParent(String role, String tableName) {
        if ("ROLE_PARENT".equals(role) || "家长".equals(role)) return true;
        return "xuesheng".equals(tableName);
    }

    private Long generateMeetingId() {
        for (int i = 0; i < 10; i++) {
            long id = System.currentTimeMillis() * 1000 + (long) (Math.random() * 1000);
            if (this.selectById(id) == null) return id;
        }
        return System.currentTimeMillis() * 1000 + (long) (Math.random() * 1000);
    }

    private Long generateAttendeeId() {
        for (int i = 0; i < 10; i++) {
            long id = System.currentTimeMillis() * 1000 + (long) (Math.random() * 1000);
            if (attendeeDao.selectById(id) == null) return id;
        }
        return System.currentTimeMillis() * 1000 + (long) (Math.random() * 1000);
    }

    private boolean isTeacher(String role, String tableName) {
        if ("ROLE_TEACHER".equals(role) || "教师".equals(role) || "管理员".equals(role)) return true;
        return "jiaoshi".equals(tableName);
    }
}
