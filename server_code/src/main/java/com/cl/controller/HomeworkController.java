package com.cl.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.annotation.IgnoreAuth;
import com.cl.entity.HomeworkEntity;
import com.cl.service.HomeworkService;
import com.cl.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * 保存作业（兼容原 node 接口）
     */
    @IgnoreAuth
    @RequestMapping("/saveHomework")
    public R saveHomework(@RequestBody HomeworkEntity homework) {
        return doSave(homework);
    }

    /**
     * 保存作业
     */
    @IgnoreAuth
    @RequestMapping("/save")
    public R save(@RequestBody HomeworkEntity homework) {
        return doSave(homework);
    }

    private R doSave(HomeworkEntity homework) {
        if (homework == null || StringUtils.isBlank(homework.getContent())) {
            return R.error("作业内容不能为空");
        }
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
    public R getHomework() {
        return doList();
    }

    /**
     * 获取作业列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list() {
        return doList();
    }

    private R doList() {
        List<HomeworkEntity> list = homeworkService.selectList(
                new EntityWrapper<HomeworkEntity>().orderBy("create_time", false)
        );
        return R.ok().put("data", list);
    }
}
