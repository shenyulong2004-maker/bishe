package com.cl.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.annotation.IgnoreAuth;
import com.cl.entity.HomeworkEntity;
import com.cl.entity.XueshengEntity;
import com.cl.service.HomeworkService;
import com.cl.service.XueshengService;
import com.cl.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 作业
 */
@RestController
@RequestMapping("/homework")
public class HomeworkController {

    @Autowired
    private HomeworkService homeworkService;

    @Autowired
    private XueshengService xueshengService;

    /**
     * 保存作业（兼容原 node 接口）
     */
    @IgnoreAuth
    @RequestMapping("/saveHomework")
    public R saveHomework(@RequestBody HomeworkEntity homework, HttpServletRequest request) {
        return doSave(homework, request);
    }

    /**
     * 保存作业
     */
    @RequestMapping("/save")
    public R save(@RequestBody HomeworkEntity homework, HttpServletRequest request) {
        return doSave(homework, request);
    }

    private R doSave(HomeworkEntity homework, HttpServletRequest request) {
        if (homework == null || StringUtils.isBlank(homework.getContent())) {
            return R.error("作业内容不能为空");
        }

        Long userId = (Long) request.getSession().getAttribute("userId");
        String tableName = (String) request.getSession().getAttribute("tableName");

        if (userId == null || StringUtils.isBlank(tableName)) {
            return R.error(401, "请先登录");
        }

        if (!"xuesheng".equals(tableName)) {
            return R.error("仅学生/家长账号可提交作业");
        }

        XueshengEntity user = xueshengService.selectById(userId);
        if (user == null) {
            return R.error("用户不存在");
        }

        String role = user.getRole();
        if (!"学生".equals(role) && !"家长".equals(role)) {
            return R.error("仅学生/家长角色可提交作业");
        }

        homework.setSubmitterId(userId);
        homework.setSubmitterRole("xuesheng");
        homework.setSubmitterName(StringUtils.isNotBlank(user.getXingming()) ? user.getXingming() : user.getXuehao());

        homework.setId(new Date().getTime() + (long) Math.floor(Math.random() * 1000));
        homework.setCreateTime(new Date());
        homework.setIsPermanent(homework.getIsPermanent() != null && homework.getIsPermanent() == 1 ? 1 : 0);
        homeworkService.insert(homework);
        return R.ok("作业保存成功");
    }

    /**
     * 获取作业列表（兼容原 node 接口）
     */
    @IgnoreAuth
    @RequestMapping("/getHomework")
    public R getHomework(HttpServletRequest request) {
        return doList(request);
    }

    /**
     * 获取作业列表
     */
    @RequestMapping("/list")
    public R list(HttpServletRequest request) {
        return doList(request);
    }

    private R doList(HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String tableName = (String) request.getSession().getAttribute("tableName");

        EntityWrapper<HomeworkEntity> ew = new EntityWrapper<HomeworkEntity>();

        if (userId != null && "xuesheng".equals(tableName)) {
            ew.eq("submitter_id", userId);
        }

        List<HomeworkEntity> list = homeworkService.selectList(ew.orderBy("create_time", false));
        return R.ok().put("data", list);
    }
}
