package com.cl.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cl.annotation.IgnoreAuth;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.entity.ConfigEntity;
import com.cl.entity.EIException;
import com.cl.service.ConfigService;
import com.cl.utils.R;

/**
 * 上传文件映射表
 */
@RestController
@RequestMapping("file")
@SuppressWarnings({"unchecked","rawtypes"})
public class FileController{
	@Autowired
    private ConfigService configService;
	/**
	 * 上传文件
	 */
	@RequestMapping("/upload")
    @IgnoreAuth
	public R upload(@RequestParam("file") MultipartFile file,String type) throws Exception {
		try {
			if (file.isEmpty()) {
				return R.error("上传文件不能为空");
			}
			
			String originalFilename = file.getOriginalFilename();
			if (originalFilename == null) {
				return R.error("文件名为空");
			}
			
			String fileExt = originalFilename.substring(originalFilename.lastIndexOf(".")+1).toLowerCase();
			List<String> allowedExt = Arrays.asList("jpg", "jpeg", "png", "pdf", "gif", "bmp");
			if (!allowedExt.contains(fileExt)) {
				return R.error("不支持的文件格式：" + fileExt);
			}
			
			if (file.getSize() > 50 * 1024 * 1024) {
				return R.error("文件大小不能超过 50MB");
			}
			
			File path = new File(ResourceUtils.getURL("classpath:static").getPath());
			if(!path.exists()) {
			    path = new File("");
			}
			File upload = new File(path.getAbsolutePath(),"/file/");
			if(!upload.exists()) {
			    upload.mkdirs();
			}
			String fileName = new Date().getTime()+"."+fileExt;
	        if(StringUtils.isNotBlank(type) && type.contains("_template")) {
	            fileName = type + "."+fileExt;
	            new File(upload.getAbsolutePath()+"/"+fileName).deleteOnExit();
	        }
			File dest = new File(upload.getAbsolutePath()+"/"+fileName);
			file.transferTo(dest);
			
			if(StringUtils.isNotBlank(type) && type.equals("1")) {
				ConfigEntity configEntity = configService.selectOne(new EntityWrapper<ConfigEntity>().eq("name", "faceFile"));
				if(configEntity==null) {
					configEntity = new ConfigEntity();
					configEntity.setName("faceFile");
					configEntity.setValue(fileName);
				} else {
					configEntity.setValue(fileName);
				}
				configService.insertOrUpdate(configEntity);
			}
			System.out.println("文件上传成功: " + fileName);
			return R.ok("上传成功").put("file", fileName);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("文件上传失败: " + e.getMessage());
			return R.error("上传失败：" + e.getMessage());
		}
	}
	
	/**
	 * 下载文件
	 */
	@IgnoreAuth
	@RequestMapping("/download")
	public ResponseEntity<byte[]> download(@RequestParam String fileName) {
		try {
			File path = new File(ResourceUtils.getURL("classpath:static").getPath());
			if(!path.exists()) {
			    path = new File("");
			}
			File upload = new File(path.getAbsolutePath(),"/file/");
			if(!upload.exists()) {
			    upload.mkdirs();
			}
			File file = new File(upload.getAbsolutePath()+"/"+fileName);
			if(file.exists()){
				/*if(!fileService.canRead(file, SessionManager.getSessionUser())){
					getResponse().sendError(403);
				}*/
				HttpHeaders headers = new HttpHeaders();
			    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);    
			    headers.setContentDispositionFormData("attachment", fileName);    
			    return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),headers, HttpStatus.CREATED);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}
