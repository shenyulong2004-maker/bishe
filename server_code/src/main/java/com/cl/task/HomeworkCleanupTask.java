package com.cl.task;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.entity.HomeworkEntity;
import com.cl.service.HomeworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

/**
 * 作业清理任务
 */
@Component
public class HomeworkCleanupTask {

    @Autowired
    private HomeworkService homeworkService;

    /**
     * 每小时清理一次：删除3个月前且非永久作业
     */
    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredHomework() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -3);
        Date threshold = calendar.getTime();

        homeworkService.delete(
                new EntityWrapper<HomeworkEntity>()
                        .eq("is_permanent", 0)
                        .lt("create_time", threshold)
        );
    }
}
