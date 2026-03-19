
package com.cl.controller;


import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cl.annotation.IgnoreAuth;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.entity.UsersEntity;
import com.cl.service.TokenService;
import com.cl.service.UsersService;
import com.cl.utils.EncryptUtil;
import com.cl.utils.MPUtil;
import com.cl.utils.PageUtils;
import com.cl.utils.R;

/**
 * 登录相关
 */
@RequestMapping("users")
@RestController
public class UsersController{
	
	@Autowired
	private UsersService userService;
	
	@Autowired
	private TokenService tokenService;

	/**
	 * 登录
	 */
	@IgnoreAuth
	@RequestMapping(value = "/login")
	public R login(String username, String password, String captcha, HttpServletRequest request) {
		UsersEntity user = userService.selectOne(new EntityWrapper<UsersEntity>().eq("username", username));
		if(user==null || !EncryptUtil.verifyPassword(password, user.getPassword())) {
			return R.error("账号或密码不正确");
		}
		if (!EncryptUtil.isPasswordHashed(user.getPassword())) {
			user.setPassword(EncryptUtil.hashPassword(password));
			userService.updateById(user);
		}
		String token = tokenService.generateToken(user.getId(),username, "users", user.getRole());
		return R.ok().put("token", token);
	}
	
	/**
	 * 注册
	 */
	@IgnoreAuth
	@PostMapping(value = "/register")
	public R register(@RequestBody UsersEntity user){
//    	ValidatorUtils.validateEntity(user);
    	if(userService.selectOne(new EntityWrapper<UsersEntity>().eq("username", user.getUsername())) !=null) {
    		return R.error("用户已存在");
    	}
		if (!EncryptUtil.isValidPassword(user.getPassword())) {
			return R.error("密码格式不符合要求");
		}
		user.setPassword(EncryptUtil.hashPassword(user.getPassword()));
        userService.insert(user);
        return R.ok();
    }

	/**
	 * 退出
	 */
	@GetMapping(value = "logout")
	public R logout(HttpServletRequest request) {
		request.getSession().invalidate();
		return R.ok("退出成功");
	}
	
	/**
     * 密码重置
     */
	@RequestMapping(value = "/resetPass")
    public R resetPass(String username, @RequestParam(required = false) String password, HttpServletRequest request){
		Object roleObj = request.getSession().getAttribute("role");
		if (roleObj == null || !"管理员".equals(String.valueOf(roleObj))) {
			return R.error(403, "无权限");
		}
    	UsersEntity user = userService.selectOne(new EntityWrapper<UsersEntity>().eq("username", username));
    	if(user==null) {
    		return R.error("账号不存在");
    	}
		String nextPassword = (password == null || password.trim().isEmpty()) ? EncryptUtil.generateRandomPassword() : password.trim();
		if (!EncryptUtil.isValidPassword(nextPassword)) {
			return R.error("密码格式不符合要求");
		}
    	user.setPassword(EncryptUtil.hashPassword(nextPassword));
        userService.updateById(user);
        return R.ok().put("data", nextPassword);
    }

	@RequestMapping(value = "/updatePassword")
	public R updatePassword(@RequestBody Map<String, Object> body, HttpServletRequest request) {
		Long id = (Long) request.getSession().getAttribute("userId");
		if (id == null) return R.error(401, "请先登录");
		String oldPassword = body == null ? null : (String) body.get("oldPassword");
		String newPassword = body == null ? null : (String) body.get("newPassword");
		if (oldPassword == null || newPassword == null) return R.error("参数错误");
		if (!EncryptUtil.isValidPassword(newPassword)) return R.error("密码格式不符合要求");
		UsersEntity user = userService.selectById(id);
		if (user == null) return R.error("账号不存在");
		if (!EncryptUtil.verifyPassword(oldPassword, user.getPassword())) return R.error("原密码不正确");
		user.setPassword(EncryptUtil.hashPassword(newPassword));
		userService.updateById(user);
		return R.ok();
	}
	
	/**
     * 列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,UsersEntity user){
        EntityWrapper<UsersEntity> ew = new EntityWrapper<UsersEntity>();
    	PageUtils page = userService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.allLike(ew, user), params), params));
		sanitizePage(page);
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/list")
    public R list( UsersEntity user){
       	EntityWrapper<UsersEntity> ew = new EntityWrapper<UsersEntity>();
      	ew.allEq(MPUtil.allEQMapPre( user, "user")); 
		Object data = userService.selectListView(ew);
		sanitizeList(data);
        return R.ok().put("data", data);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") String id){
        UsersEntity user = userService.selectById(id);
		sanitizeUser(user);
        return R.ok().put("data", user);
    }
    
    /**
     * 获取用户的session用户信息
     */
    @RequestMapping("/session")
    public R getCurrUser(HttpServletRequest request){
    	Long id = (Long)request.getSession().getAttribute("userId");
        UsersEntity user = userService.selectById(id);
		sanitizeUser(user);
        return R.ok().put("data", user);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public R save(@RequestBody UsersEntity user){
//    	ValidatorUtils.validateEntity(user);
    	if(userService.selectOne(new EntityWrapper<UsersEntity>().eq("username", user.getUsername())) !=null) {
    		return R.error("用户已存在");
    	}
		if (!EncryptUtil.isValidPassword(user.getPassword())) {
			return R.error("密码格式不符合要求");
		}
		user.setPassword(EncryptUtil.hashPassword(user.getPassword()));
        userService.insert(user);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody UsersEntity user){
//        ValidatorUtils.validateEntity(user);
    	UsersEntity u = userService.selectOne(new EntityWrapper<UsersEntity>().eq("username", user.getUsername()));
    	if(u!=null && u.getId()!=user.getId() && u.getUsername().equals(user.getUsername())) {
    		return R.error("用户名已存在。");
    	}
		if (user.getPassword() != null && !user.getPassword().trim().isEmpty() && !EncryptUtil.isPasswordHashed(user.getPassword())) {
			if (!EncryptUtil.isValidPassword(user.getPassword())) return R.error("密码格式不符合要求");
			user.setPassword(EncryptUtil.hashPassword(user.getPassword()));
		}
        userService.updateById(user);//全部更新
        return R.ok();
    }

	private void sanitizeUser(UsersEntity user) {
		if (user == null) return;
		user.setPassword(null);
	}

	@SuppressWarnings("unchecked")
	private void sanitizePage(PageUtils page) {
		if (page == null || page.getList() == null) return;
		for (Object row : page.getList()) {
			if (row instanceof UsersEntity) sanitizeUser((UsersEntity) row);
		}
	}

	@SuppressWarnings("unchecked")
	private void sanitizeList(Object data) {
		if (!(data instanceof java.util.List)) return;
		for (Object row : (java.util.List<Object>) data) {
			if (row instanceof UsersEntity) sanitizeUser((UsersEntity) row);
		}
	}

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        userService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
}
