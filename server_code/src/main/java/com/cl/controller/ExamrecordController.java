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

import com.cl.entity.ExamrecordEntity;
import com.cl.entity.XueshengEntity;
import com.cl.entity.XueshengchengjiEntity;
import com.cl.entity.view.ExamrecordView;

import com.cl.service.ExamrecordService;
import com.cl.service.XueshengService;
import com.cl.service.XueshengchengjiService;
import com.cl.service.TokenService;
import com.cl.utils.PageUtils;
import com.cl.utils.R;
import com.cl.utils.MPUtil;
import com.cl.utils.CommonUtil;
import java.io.IOException;

/**
 * 考试记录
 * 后端接口
 * @author 
 * @email 
 * @date 2024-03-05 21:53:17
 */
@RestController
@RequestMapping("/examrecord")
public class ExamrecordController {
    @Autowired
    private ExamrecordService examrecordService;
    @Autowired
    private XueshengService xueshengService;
    @Autowired
    private XueshengchengjiService xueshengchengjiService;



    

   	    /**
     * 考试记录接口
     */
    @RequestMapping("/groupby")
    public R page2(@RequestParam Map<String, Object> params,ExamrecordEntity examrecord, HttpServletRequest request){
    	if(!request.getSession().getAttribute("role").toString().equals("管理员")) {
    		examrecord.setUserid((Long)request.getSession().getAttribute("userId"));
    	}

        EntityWrapper<ExamrecordEntity> ew = new EntityWrapper<ExamrecordEntity>();
		PageUtils page = examrecordService.queryPageGroupBy(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, examrecord), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,ExamrecordEntity examrecord,
		HttpServletRequest request){
    	if(!request.getSession().getAttribute("role").toString().equals("管理员")) {
    		examrecord.setUserid((Long)request.getSession().getAttribute("userId"));
    	}
        EntityWrapper<ExamrecordEntity> ew = new EntityWrapper<ExamrecordEntity>();

		PageUtils page = examrecordService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, examrecord), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,ExamrecordEntity examrecord, 
		HttpServletRequest request){
    	if(!request.getSession().getAttribute("role").toString().equals("管理员")) {
    		examrecord.setUserid((Long)request.getSession().getAttribute("userId"));
    	}
        EntityWrapper<ExamrecordEntity> ew = new EntityWrapper<ExamrecordEntity>();

		PageUtils page = examrecordService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, examrecord), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( ExamrecordEntity examrecord){
       	EntityWrapper<ExamrecordEntity> ew = new EntityWrapper<ExamrecordEntity>();
      	ew.allEq(MPUtil.allEQMapPre( examrecord, "examrecord")); 
        return R.ok().put("data", examrecordService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(ExamrecordEntity examrecord){
        EntityWrapper< ExamrecordEntity> ew = new EntityWrapper< ExamrecordEntity>();
 		ew.allEq(MPUtil.allEQMapPre( examrecord, "examrecord")); 
		ExamrecordView examrecordView =  examrecordService.selectView(ew);
		return R.ok("查询考试记录成功").put("data", examrecordView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        ExamrecordEntity examrecord = examrecordService.selectById(id);
		examrecord = examrecordService.selectView(new EntityWrapper<ExamrecordEntity>().eq("id", id));
        return R.ok().put("data", examrecord);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        ExamrecordEntity examrecord = examrecordService.selectById(id);
		examrecord = examrecordService.selectView(new EntityWrapper<ExamrecordEntity>().eq("id", id));
        return R.ok().put("data", examrecord);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody ExamrecordEntity examrecord, HttpServletRequest request){
    	examrecord.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(examrecord);
    	examrecord.setUserid((Long)request.getSession().getAttribute("userId"));
        examrecordService.insert(examrecord);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody ExamrecordEntity examrecord, HttpServletRequest request){
    	examrecord.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(examrecord);
    	examrecord.setUserid((Long)request.getSession().getAttribute("userId"));
        examrecordService.insert(examrecord);
        return R.ok();
    }



    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody ExamrecordEntity examrecord, HttpServletRequest request){
        //ValidatorUtils.validateEntity(examrecord);
        examrecordService.updateById(examrecord);//全部更新
        return R.ok();
    }



    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        examrecordService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
	


    /**
     * 当重新考试时，删除考生的某个试卷的所有考试记录
     */
    @RequestMapping("/deleteRecords")
    public R deleteRecords(@RequestParam Long userid,@RequestParam Long paperid){
    	examrecordService.delete(new EntityWrapper<ExamrecordEntity>().eq("paperid", paperid).eq("userid", userid));
        return R.ok();
    }

    /**
     * 交卷后同步学生成绩（按试卷汇总）
     */
    @RequestMapping("/finishExam")
    @Transactional
    public R finishExam(@RequestParam Long paperid, HttpServletRequest request){
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            return R.error(401, "请先登录");
        }
        Object tableNameObj = request.getSession().getAttribute("tableName");
        if (tableNameObj == null || !"xuesheng".equals(String.valueOf(tableNameObj))) {
            return R.ok();
        }

        XueshengEntity xuesheng = xueshengService.selectById(userId);
        if (xuesheng == null) {
            return R.error("学生信息不存在");
        }

        List<ExamrecordEntity> records = examrecordService.selectList(
                new EntityWrapper<ExamrecordEntity>()
                        .eq("userid", userId)
                        .eq("paperid", paperid)
        );
        if (records == null || records.isEmpty()) {
            return R.error("未找到考试记录");
        }

        double totalScore = 0D;
        for (ExamrecordEntity row : records) {
            if (row != null && row.getMyscore() != null) {
                totalScore += row.getMyscore();
            }
        }

        EntityWrapper<XueshengchengjiEntity> ew = new EntityWrapper<XueshengchengjiEntity>()
                .eq("xuehao", xuesheng.getXuehao());
        XueshengchengjiEntity scoreRow = xueshengchengjiService.selectOne(ew);
        if (scoreRow == null) {
            scoreRow = new XueshengchengjiEntity();
            scoreRow.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
        }
        scoreRow.setKaoshichengji(totalScore);
        scoreRow.setXuehao(xuesheng.getXuehao());
        scoreRow.setXingming(xuesheng.getXingming());
        scoreRow.setBanji(xuesheng.getBanji());
        scoreRow.setTianjiariqi(new Date());

        if (scoreRow.getId() == null) {
            scoreRow.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
            xueshengchengjiService.insert(scoreRow);
        } else if (xueshengchengjiService.selectById(scoreRow.getId()) == null) {
            xueshengchengjiService.insert(scoreRow);
        } else {
            xueshengchengjiService.updateById(scoreRow);
        }
        return R.ok().put("data", scoreRow);
    }






}
