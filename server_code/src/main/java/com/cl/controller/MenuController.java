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

import com.cl.entity.MenuEntity;
import com.cl.entity.view.MenuView;

import com.cl.service.MenuService;
import com.cl.service.TokenService;
import com.cl.utils.PageUtils;
import com.cl.utils.R;
import com.cl.utils.MPUtil;
import com.cl.utils.CommonUtil;
import java.io.IOException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 菜单
 * 后端接口
 * @author 
 * @email 
 * @date 2024-03-05 21:53:17
 */
@RestController
@RequestMapping("/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;



    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,MenuEntity menu,
		HttpServletRequest request){
        EntityWrapper<MenuEntity> ew = new EntityWrapper<MenuEntity>();

		PageUtils page = menuService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, menu), params), params));
		removeJiazhangFromMenu(page);
        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,MenuEntity menu, 
		HttpServletRequest request){
        EntityWrapper<MenuEntity> ew = new EntityWrapper<MenuEntity>();

		PageUtils page = menuService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, menu), params), params));
		removeJiazhangFromMenu(page);
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( MenuEntity menu){
       	EntityWrapper<MenuEntity> ew = new EntityWrapper<MenuEntity>();
      	ew.allEq(MPUtil.allEQMapPre( menu, "menu")); 
        return R.ok().put("data", menuService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(MenuEntity menu){
        EntityWrapper< MenuEntity> ew = new EntityWrapper< MenuEntity>();
 		ew.allEq(MPUtil.allEQMapPre( menu, "menu")); 
		MenuView menuView =  menuService.selectView(ew);
		return R.ok("查询菜单成功").put("data", menuView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        MenuEntity menu = menuService.selectById(id);
		menu = menuService.selectView(new EntityWrapper<MenuEntity>().eq("id", id));
        return R.ok().put("data", menu);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        MenuEntity menu = menuService.selectById(id);
		menu = menuService.selectView(new EntityWrapper<MenuEntity>().eq("id", id));
        return R.ok().put("data", menu);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MenuEntity menu, HttpServletRequest request){
    	menu.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(menu);
        menuService.insert(menu);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody MenuEntity menu, HttpServletRequest request){
    	menu.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(menu);
        menuService.insert(menu);
        return R.ok();
    }



    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    @IgnoreAuth
    public R update(@RequestBody MenuEntity menu, HttpServletRequest request){
        //ValidatorUtils.validateEntity(menu);
        menuService.updateById(menu);//全部更新
        return R.ok();
    }



    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        menuService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
	
	/**
     * 前端智能排序
     */
	@IgnoreAuth
    @RequestMapping("/autoSort")
    public R autoSort(@RequestParam Map<String, Object> params,MenuEntity menu, HttpServletRequest request,String pre){
        EntityWrapper<MenuEntity> ew = new EntityWrapper<MenuEntity>();
        Map<String, Object> newMap = new HashMap<String, Object>();
        Map<String, Object> param = new HashMap<String, Object>();
		Iterator<Map.Entry<String, Object>> it = param.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = it.next();
			String key = entry.getKey();
			String newKey = entry.getKey();
			if (pre.endsWith(".")) {
				newMap.put(pre + newKey, entry.getValue());
			} else if (StringUtils.isEmpty(pre)) {
				newMap.put(newKey, entry.getValue());
			} else {
				newMap.put(pre + "." + newKey, entry.getValue());
			}
		}
		params.put("sort", "clicktime");
        params.put("order", "desc");
		PageUtils page = menuService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, menu), params), params));
        return R.ok().put("data", page);
    }








	private void removeJiazhangFromMenu(PageUtils page) {
		if (page == null || page.getList() == null) return;
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<List<Map<String, Object>>> rolesType = new TypeReference<List<Map<String, Object>>>() {};
		for (Object row : page.getList()) {
			if (!(row instanceof MenuEntity)) continue;
			MenuEntity menuEntity = (MenuEntity) row;
			String menujson = menuEntity.getMenujson();
			if (menujson == null || menujson.trim().isEmpty()) continue;
			try {
				List<Map<String, Object>> roles = mapper.readValue(menujson, rolesType);
				roles.removeIf(role -> "jiazhang".equals(role.get("tableName")) || "家长".equals(role.get("roleName")));
				for (Map<String, Object> role : roles) {
					Object backMenuObj = role.get("backMenu");
					if (backMenuObj instanceof List) {
						List<Map<String, Object>> backMenu = (List<Map<String, Object>>) backMenuObj;
						backMenu.removeIf(section -> {
							Object childObj = section.get("child");
							if (childObj instanceof List) {
								List<Map<String, Object>> child = (List<Map<String, Object>>) childObj;
								child.removeIf(item -> "jiazhang".equals(item.get("tableName")));
								return child.isEmpty();
							}
							return false;
						});
					}
					Object frontMenuObj = role.get("frontMenu");
					if (frontMenuObj instanceof List) {
						List<Map<String, Object>> frontMenu = (List<Map<String, Object>>) frontMenuObj;
						frontMenu.removeIf(section -> {
							Object childObj = section.get("child");
							if (childObj instanceof List) {
								List<Map<String, Object>> child = (List<Map<String, Object>>) childObj;
								child.removeIf(item -> "jiazhang".equals(item.get("tableName")));
								return child.isEmpty();
							}
							return false;
						});
					}
				}
				menuEntity.setMenujson(mapper.writeValueAsString(roles));
			} catch (Exception e) {
			}
		}
	}

}
