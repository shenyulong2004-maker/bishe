package com.cl.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.dao.HomeworkDao;
import com.cl.entity.HomeworkEntity;
import com.cl.service.HomeworkService;
import org.springframework.stereotype.Service;

/**
 * 作业
 */
@Service("homeworkService")
public class HomeworkServiceImpl extends ServiceImpl<HomeworkDao, HomeworkEntity> implements HomeworkService {
}
