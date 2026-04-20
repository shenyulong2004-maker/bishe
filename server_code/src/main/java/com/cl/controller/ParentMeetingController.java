package com.cl.controller;

import com.cl.entity.ParentMeetingEntity;
import com.cl.service.ParentMeetingService;
import com.cl.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 在线家长会/会议预约
 */
@RestController
@RequestMapping("/api/parent/meetings")
public class ParentMeetingController {

    @Autowired
    private ParentMeetingService parentMeetingService;

    @GetMapping
    public R list(HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String role = (String) request.getSession().getAttribute("role");
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (userId == null) return R.error(401, "请先登录");

        List<ParentMeetingEntity> list = parentMeetingService.listByCurrentUser(userId, role, tableName);
        return R.ok().put("data", list);
    }

    /** 家长发起一对一预约 */
    @PostMapping("/book")
    public R parentBook(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String role = (String) request.getSession().getAttribute("role");
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!isParent(role, tableName) || userId == null) return R.error(403, "仅家长可预约");

        try {
            ParentMeetingEntity meeting = new ParentMeetingEntity();
            meeting.setMeetingTime(parseMeetingTime(body.get("meetingTime")));
            meeting.setTitle(body.get("title") == null ? null : String.valueOf(body.get("title")));
            meeting.setRoomId(body.get("roomId") == null ? null : String.valueOf(body.get("roomId")));
            meeting.setTeacherId(parseTeacherId(body.get("teacherId")));

            Map<String, Object> result = parentMeetingService.parentBookOneToOne(userId, meeting);
            if (!(Boolean) result.get("ok")) return R.error(String.valueOf(result.get("msg")));
            return R.ok().put("data", result.get("data"));
        } catch (Exception e) {
            String msg = e.getMessage() == null ? "参数错误" : e.getMessage();
            return R.error(msg);
        }
    }

    /** 教师发起群体家长会 */
    @PostMapping("/group")
    public R teacherGroup(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String role = (String) request.getSession().getAttribute("role");
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!isTeacher(role, tableName) || userId == null) return R.error(403, "仅教师可发起家长会");

        try {
            ParentMeetingEntity meeting = new ParentMeetingEntity();
            String meetingTimeStr = body.get("meetingTime") == null ? null : String.valueOf(body.get("meetingTime"));
            if (meetingTimeStr != null && !meetingTimeStr.trim().isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                meeting.setMeetingTime(sdf.parse(meetingTimeStr));
            }
            meeting.setTitle(body.get("title") == null ? null : String.valueOf(body.get("title")));
            meeting.setRoomId(body.get("roomId") == null ? null : String.valueOf(body.get("roomId")));

            @SuppressWarnings("unchecked")
            List<Object> raw = (List<Object>) body.get("parentIds");
            List<Long> parentIds = new ArrayList<>();
            if (raw != null) {
                for (Object v : raw) {
                    if (v == null) continue;
                    parentIds.add(Long.valueOf(String.valueOf(v)));
                }
            }

            Map<String, Object> result = parentMeetingService.teacherCreateGroup(userId, meeting, parentIds);
            if (!(Boolean) result.get("ok")) return R.error(String.valueOf(result.get("msg")));
            return R.ok().put("data", result.get("data"));
        } catch (Exception e) {
            return R.error("参数错误");
        }
    }

    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String role = (String) request.getSession().getAttribute("role");
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (userId == null) return R.error(401, "请先登录");

        ParentMeetingEntity row = parentMeetingService.getMeetingDetail(userId, role, tableName, id);
        if (row == null) return R.error(404, "会议不存在");
        return R.ok().put("data", row);
    }

    /** 会议确认（家长/教师均可） */
    @PutMapping("/{id}/confirm")
    public R confirm(@PathVariable("id") Long id, HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String role = (String) request.getSession().getAttribute("role");
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (userId == null) return R.error(401, "请先登录");

        Map<String, Object> result;
        if (isParent(role, tableName)) {
            result = parentMeetingService.confirmByParent(userId, id);
        } else if (isTeacher(role, tableName)) {
            result = parentMeetingService.confirmByTeacher(userId, id);
        } else {
            return R.error(403, "无权限");
        }

        if (!(Boolean) result.get("ok")) return R.error(String.valueOf(result.get("msg")));
        return R.ok();
    }

    /** 教师标记完成 */
    @PutMapping("/{id}/finish")
    public R finish(@PathVariable("id") Long id, HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String role = (String) request.getSession().getAttribute("role");
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!isTeacher(role, tableName) || userId == null) return R.error(403, "仅教师可操作");

        Map<String, Object> result = parentMeetingService.finishByTeacher(userId, id);
        if (!(Boolean) result.get("ok")) return R.error(String.valueOf(result.get("msg")));
        return R.ok();
    }

    @PutMapping("/{id}/cancel")
    public R cancel(@PathVariable("id") Long id, HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String role = (String) request.getSession().getAttribute("role");
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (userId == null) return R.error(401, "请先登录");

        Map<String, Object> result = parentMeetingService.cancelMeeting(userId, role, tableName, id);
        if (!(Boolean) result.get("ok")) return R.error(String.valueOf(result.get("msg")));
        return R.ok();
    }

    private boolean isParent(String role, String tableName) {
        if ("ROLE_PARENT".equals(role) || "家长".equals(role)) return true;
        return "xuesheng".equals(tableName);
    }

    private Date parseMeetingTime(Object value) throws ParseException {
        if (value == null) {
            throw new ParseException("请选择会议时间", 0);
        }
        String raw = String.valueOf(value).trim();
        if (raw.isEmpty()) {
            throw new ParseException("请选择会议时间", 0);
        }
        String normalized = raw.replace('T', ' ');
        String[] patterns = new String[]{
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm",
                "yyyy/MM/dd HH:mm:ss",
                "yyyy/MM/dd HH:mm"
        };
        for (String pattern : patterns) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                sdf.setLenient(false);
                return sdf.parse(normalized);
            } catch (ParseException ignore) {
            }
        }
        throw new ParseException("会议时间格式错误", 0);
    }

    private Long parseTeacherId(Object value) {
        if (value == null) return null;
        String raw = String.valueOf(value).trim();
        if (raw.isEmpty()) return null;
        if (raw.endsWith(".0")) {
            raw = raw.substring(0, raw.length() - 2);
        }
        return Long.valueOf(raw);
    }

    private boolean isTeacher(String role, String tableName) {
        if ("ROLE_TEACHER".equals(role) || "教师".equals(role) || "管理员".equals(role)) return true;
        return "jiaoshi".equals(tableName);
    }
}
