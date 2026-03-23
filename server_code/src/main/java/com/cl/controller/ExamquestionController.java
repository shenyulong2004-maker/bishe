package com.cl.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.cl.utils.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.cl.annotation.IgnoreAuth;

import com.cl.entity.ExamquestionEntity;
import com.cl.entity.view.ExamquestionView;

import com.cl.service.ExamquestionService;
import com.cl.service.TokenService;
import com.cl.utils.PageUtils;
import com.cl.utils.R;
import com.cl.utils.MPUtil;
import com.cl.utils.CommonUtil;
import java.io.IOException;

/**
 * 试题管理
 * 后端接口
 * @author 
 * @email 
 * @date 2024-03-05 21:53:17
 */
@RestController
@RequestMapping("/examquestion")
public class ExamquestionController {
    @Autowired
    private ExamquestionService examquestionService;



    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,ExamquestionEntity examquestion,
		HttpServletRequest request){
        EntityWrapper<ExamquestionEntity> ew = new EntityWrapper<ExamquestionEntity>();

		PageUtils page = examquestionService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, examquestion), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,ExamquestionEntity examquestion, 
		HttpServletRequest request){
        EntityWrapper<ExamquestionEntity> ew = new EntityWrapper<ExamquestionEntity>();

		PageUtils page = examquestionService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, examquestion), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( ExamquestionEntity examquestion){
       	EntityWrapper<ExamquestionEntity> ew = new EntityWrapper<ExamquestionEntity>();
      	ew.allEq(MPUtil.allEQMapPre( examquestion, "examquestion")); 
        return R.ok().put("data", examquestionService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(ExamquestionEntity examquestion){
        EntityWrapper< ExamquestionEntity> ew = new EntityWrapper< ExamquestionEntity>();
 		ew.allEq(MPUtil.allEQMapPre( examquestion, "examquestion")); 
		ExamquestionView examquestionView =  examquestionService.selectView(ew);
		return R.ok("查询试题管理成功").put("data", examquestionView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        ExamquestionEntity examquestion = examquestionService.selectById(id);
		examquestion = examquestionService.selectView(new EntityWrapper<ExamquestionEntity>().eq("id", id));
        return R.ok().put("data", examquestion);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        ExamquestionEntity examquestion = examquestionService.selectById(id);
		examquestion = examquestionService.selectView(new EntityWrapper<ExamquestionEntity>().eq("id", id));
        return R.ok().put("data", examquestion);
    }
    



    /**
     * 后端保存 - 修复持久化问题
     * 添加@Transactional确保事务一致性
     */
    @RequestMapping("/save")
    @Transactional
    public R save(@RequestBody ExamquestionEntity examquestion, HttpServletRequest request){
    	try {
    		if (examquestion.getPaperid() == null || examquestion.getPaperid() == 0) {
    			return R.error("试卷不能为空");
    		}
    		if (examquestion.getType() == null) {
    			return R.error("试题类型不能为空");
    		}
    		if (StringUtils.isBlank(examquestion.getQuestionname())) {
    			return R.error("试题名称不能为空");
    		}
    		if (StringUtils.isBlank(examquestion.getAnswer())) {
    			return R.error("答案不能为空");
    		}
    		if (examquestion.getScore() == null || examquestion.getScore() <= 0) {
    			return R.error("分值必须大于0");
    		}
    		if (examquestion.getSequence() == null) {
    			return R.error("排序不能为空");
    		}
    		
    		Long type = examquestion.getType();
    		if (type == 0 || type == 1 || type == 2) {
    			if (StringUtils.isBlank(examquestion.getOptions())) {
    				return R.error("选择题必须有选项");
    			}
    		} else if (type == 3 || type == 4) {
    			examquestion.setOptions("");
    		}
    		
    		// 生成唯一ID（时间戳+随机数）
    		Long uniqueId = new Date().getTime() + new Double(Math.floor(Math.random()*1000)).longValue();
    		examquestion.setId(uniqueId);
    		
    		// 插入数据库
    		boolean success = examquestionService.insert(examquestion);
    		if (!success) {
    			return R.error("保存失败：数据库插入失败");
    		}
    		
    		System.out.println("试题保存成功: ID=" + examquestion.getId() + ", 名称=" + examquestion.getQuestionname());
    		return R.ok("保存成功");
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		System.err.println("试题保存失败: " + e.getMessage());
    		return R.error("保存失败：" + e.getMessage());
    	}
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody ExamquestionEntity examquestion, HttpServletRequest request){
    	examquestion.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(examquestion);
        examquestionService.insert(examquestion);
        return R.ok();
    }



    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody ExamquestionEntity examquestion, HttpServletRequest request){
        //ValidatorUtils.validateEntity(examquestion);
        examquestionService.updateById(examquestion);//全部更新
        return R.ok();
    }



    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        examquestionService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
}
