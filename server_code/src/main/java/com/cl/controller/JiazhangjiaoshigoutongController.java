package com.cl.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.entity.JiaoshiEntity;
import com.cl.entity.JiazhangjiaoshigoutongEntity;
import com.cl.entity.XueshengEntity;
import com.cl.service.JiaoshiService;
import com.cl.service.JiazhangjiaoshigoutongService;
import com.cl.service.XueshengService;
import com.cl.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 家长-教师沟通
 */
@RestController
@RequestMapping("/jiazhangjiaoshigoutong")
public class JiazhangjiaoshigoutongController {

    @Autowired
    private JiazhangjiaoshigoutongService jiazhangjiaoshigoutongService;
    @Autowired
    private XueshengService xueshengService;
    @Autowired
    private JiaoshiService jiaoshiService;

    @RequestMapping("/save")
    public R save(@RequestBody JiazhangjiaoshigoutongEntity body, HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (userId == null || StringUtils.isBlank(tableName)) {
            return R.error(401, "请先登录");
        }
        if (body == null || body.getToId() == null || StringUtils.isBlank(body.getToRole()) || StringUtils.isBlank(body.getContent())) {
            return R.error("参数错误");
        }
        if (!("xuesheng".equals(tableName) || "jiaoshi".equals(tableName))) {
            return R.error("仅家长或教师可发送沟通信息");
        }
        if (!("xuesheng".equals(body.getToRole()) || "jiaoshi".equals(body.getToRole()))) {
            return R.error("目标角色错误");
        }

        JiazhangjiaoshigoutongEntity row = new JiazhangjiaoshigoutongEntity();
        row.setId(new Date().getTime() + (long) Math.floor(Math.random() * 1000));
        row.setAddtime(new Date());
        row.setFromRole(tableName);
        row.setFromId(userId);
        row.setFromName(getDisplayName(tableName, userId));
        row.setToRole(body.getToRole());
        row.setToId(body.getToId());
        row.setToName(StringUtils.isBlank(body.getToName()) ? getDisplayName(body.getToRole(), body.getToId()) : body.getToName());
        row.setContent(body.getContent().trim());

        jiazhangjiaoshigoutongService.insert(row);
        return R.ok();
    }

    @RequestMapping("/list")
    public R list(HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (userId == null || StringUtils.isBlank(tableName)) {
            return R.error(401, "请先登录");
        }

        EntityWrapper<JiazhangjiaoshigoutongEntity> ew = new EntityWrapper<>();
        ew.andNew().eq("from_role", tableName).eq("from_id", userId)
          .orNew().eq("to_role", tableName).eq("to_id", userId)
          .orderBy("addtime", false);

        List<JiazhangjiaoshigoutongEntity> list = jiazhangjiaoshigoutongService.selectList(ew);
        return R.ok().put("data", list);
    }

    @RequestMapping("/sessionList")
    public R sessionList(HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (userId == null || StringUtils.isBlank(tableName)) {
            return R.error(401, "请先登录");
        }
        if (!"jiaoshi".equals(tableName)) {
            return R.error("仅教师可查看会话列表");
        }

        List<JiazhangjiaoshigoutongEntity> list = jiazhangjiaoshigoutongService.selectList(
                new EntityWrapper<JiazhangjiaoshigoutongEntity>()
                        .andNew().eq("from_role", "xuesheng").eq("to_role", "jiaoshi").eq("to_id", userId)
                        .orNew().eq("from_role", "jiaoshi").eq("from_id", userId).eq("to_role", "xuesheng")
                        .orderBy("addtime", false)
        );

        Map<Long, Map<String, Object>> map = new HashMap<>();
        for (JiazhangjiaoshigoutongEntity item : list) {
            Long parentId;
            String parentName;
            if ("xuesheng".equals(item.getFromRole())) {
                parentId = item.getFromId();
                parentName = item.getFromName();
            } else {
                parentId = item.getToId();
                parentName = item.getToName();
            }
            if (parentId == null) continue;

            Map<String, Object> row = map.get(parentId);
            if (row == null) {
                row = new HashMap<>();
                row.put("parentId", parentId);
                row.put("parentName", parentName);
                row.put("lastContent", item.getContent());
                row.put("lastTime", item.getAddtime());
                row.put("unread", 0);
                map.put(parentId, row);
            }

            if ("xuesheng".equals(item.getFromRole()) && "jiaoshi".equals(item.getToRole()) && userId.equals(item.getToId()) && (item.getIsRead() == null || item.getIsRead() == 0)) {
                Integer unread = (Integer) row.get("unread");
                row.put("unread", unread + 1);
            }
        }

        List<Map<String, Object>> data = new ArrayList<>(map.values());
        data.sort((a, b) -> {
            Date ta = (Date) a.get("lastTime");
            Date tb = (Date) b.get("lastTime");
            if (ta == null && tb == null) return 0;
            if (ta == null) return 1;
            if (tb == null) return -1;
            return tb.compareTo(ta);
        });
        return R.ok().put("data", data);
    }

    @RequestMapping("/conversation")
    public R conversation(Long parentId, HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (userId == null || StringUtils.isBlank(tableName)) {
            return R.error(401, "请先登录");
        }
        if (parentId == null) {
            return R.error("参数错误");
        }
        if (!("jiaoshi".equals(tableName) || "xuesheng".equals(tableName))) {
            return R.error("无权限");
        }

        Long realParentId = "xuesheng".equals(tableName) ? userId : parentId;
        Long realTeacherId = "jiaoshi".equals(tableName) ? userId : parentId;

        EntityWrapper<JiazhangjiaoshigoutongEntity> ew = new EntityWrapper<>();
        ew.andNew().eq("from_role", "xuesheng").eq("from_id", realParentId).eq("to_role", "jiaoshi").eq("to_id", realTeacherId)
          .orNew().eq("from_role", "jiaoshi").eq("from_id", realTeacherId).eq("to_role", "xuesheng").eq("to_id", realParentId)
          .orderBy("addtime", true);

        List<JiazhangjiaoshigoutongEntity> list = jiazhangjiaoshigoutongService.selectList(ew);
        return R.ok().put("data", list);
    }

    @RequestMapping("/markRead")
    public R markRead(Long parentId, HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (userId == null || StringUtils.isBlank(tableName)) {
            return R.error(401, "请先登录");
        }
        if (!"jiaoshi".equals(tableName) || parentId == null) {
            return R.error("参数错误");
        }

        try {
            List<JiazhangjiaoshigoutongEntity> unreadList = jiazhangjiaoshigoutongService.selectList(
                    new EntityWrapper<JiazhangjiaoshigoutongEntity>()
                            .eq("from_role", "xuesheng")
                            .eq("from_id", parentId)
                            .eq("to_role", "jiaoshi")
                            .eq("to_id", userId)
                            .eq("is_read", 0)
            );

            for (JiazhangjiaoshigoutongEntity item : unreadList) {
                item.setIsRead(1);
                jiazhangjiaoshigoutongService.updateById(item);
            }
        } catch (Exception ignore) {
            // 兼容旧库未升级 is_read 字段
        }
        return R.ok();
    }

    private String getDisplayName(String tableName, Long userId) {
        if ("xuesheng".equals(tableName)) {
            XueshengEntity x = xueshengService.selectById(userId);
            return x == null ? "" : (StringUtils.isNotBlank(x.getXingming()) ? x.getXingming() : x.getXuehao());
        }
        if ("jiaoshi".equals(tableName)) {
            JiaoshiEntity j = jiaoshiService.selectById(userId);
            return j == null ? "" : (StringUtils.isNotBlank(j.getJiaoshixingming()) ? j.getJiaoshixingming() : j.getJiaoshigonghao());
        }
        return "";
    }
}
