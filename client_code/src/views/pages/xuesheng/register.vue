<template>
	<div>
		<div class="register_view">
			<el-form :model="registerForm" class="register_form">
				<div class="title_view">{{projectName}}注册</div>
				<div class="list_item">
					<div class="list_label">家长账号：</div>
					<input class="list_inp"
					 v-model="registerForm.xuehao" 
					 placeholder="请输入家长账号"
					 type="text"
					 />
				</div>
				<div class="list_item">
					<div class="list_label">密码：</div>
					<input class="list_inp"
					 v-model="registerForm.mima" 
					 placeholder="请输入密码（1-64位）"
					 type="password"
					 />
				</div>
				<div class="list_item">
					<div class="list_label">确认密码：</div>
					<input class="list_inp" v-model="registerForm.mima2" type="password" placeholder="请输入确认密码" />
				</div>
				<div class="list_item">
					<div class="list_label">姓名：</div>
					<input class="list_inp"
					 v-model="registerForm.xingming" 
					 placeholder="请输入姓名"
					 type="text"
					 />
				</div>
				<div class="list_item">
					<div class="list_label">头像：</div>
					<div class="list_file_list">
						<uploads
							action="file/upload" 
							tip="请上传头像" 
							:limit="3" 
							style="width: 100%;text-align: left;"
							:fileUrls="registerForm.touxiang?registerForm.touxiang:''" 
							@change="touxiangUploadSuccess">
						</uploads>
					</div>
				</div>
				<div class="list_item">
					<div class="list_label">性别：</div>
					<el-select 
						class="list_sel" 
						v-model="registerForm.xingbie" 
						placeholder="请选择性别"
						>
						<el-option v-for="item in xueshengxingbieLists" :label="item" :value="item"></el-option>
					</el-select>
				</div>
				<div class="list_item">
					<div class="list_label">联系电话：</div>
					<input class="list_inp"
					 v-model="registerForm.lianxidianhua" 
					 placeholder="请输入联系电话"
					 type="text"
					 />
				</div>
				<div class="list_item">
					<div class="list_label">班级：</div>
					<input class="list_inp"
					 v-model="registerForm.banji" 
					 placeholder="请输入班级"
					 type="text"
					 />
				</div>
				<div class="list_btn">
					<el-button class="register" type="success" @click="handleRegister">注册</el-button>
					<div class="r-login" @click="close">已有账号，直接登录</div>
				</div>
			</el-form>	
		</div>
	</div>
</template>
<script setup>
	import {
		ref,
		getCurrentInstance,
		nextTick,
	} from 'vue';
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	const projectName = context?.$project.projectName
	//获取注册类型
	import { useRoute } from 'vue-router';
	const route = useRoute()
	const tableName = ref('xuesheng')
	
	
	const registerForm = ref({
        xingbie: '',
	})
	const xueshengxingbieLists = ref([])
	const init=()=>{
		xueshengxingbieLists.value = "男,女".split(',')
	}
    const touxiangUploadSuccess=(fileUrls)=> {
        registerForm.value.touxiang = fileUrls;
    }
	// 多级联动参数
	//注册按钮
	const handleRegister = () => {
		let url = tableName.value +"/register";
		if((!registerForm.value.xuehao)){
			context?.$toolUtil.message(`家长账号不能为空`,'error')
			return false
		}
		if((!registerForm.value.mima)){
			context?.$toolUtil.message(`密码不能为空`,'error')
			return false
		}
		if(!isValidPassword(registerForm.value.mima)){
			context?.$toolUtil.message(`密码长度需为1-64位`,'error')
			return false
		}
		if(registerForm.value.mima!=registerForm.value.mima2){
			context?.$toolUtil.message('两次密码输入不一致','error')
			return false
		}
		if((!registerForm.value.xingming)){
			context?.$toolUtil.message(`姓名不能为空`,'error')
			return false
		}
		if(registerForm.value.touxiang!=null){
			registerForm.value.touxiang = registerForm.value.touxiang.replace(new RegExp(context?.$config.url,"g"),"");
		}
		if(registerForm.value.lianxidianhua&&(!context?.$toolUtil.isMobile(registerForm.value.lianxidianhua))){
			context?.$toolUtil.message(`联系电话应输入手机格式`,'error')
			return false
		}
		
		context?.$http({
			url:url,
			method:'post',
			data:registerForm.value
		}).then(res=>{
			context?.$toolUtil.message('注册成功','success', obj=>{
				context?.$router.push({
					path: "/login"
				});
			})
		})
	}
	//公共方法
	const isValidPassword = (pwd) => {
		if (!pwd) return false
		return pwd.length >= 1 && pwd.length <= 64
	}
	const getUUID=()=> {
		return new Date().getTime();
	}
	//返回登录
	const close = () => {
		context?.$router.push({
			path: "/login"
		});
	}
	init()
</script>
<style lang="scss" scoped>
	.register_view {
		background-repeat: no-repeat;
		flex-direction: column;
		background-color: #dce5db;
		background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='20' height='12' viewBox='0 0 20 12'%3E%3Cg fill-rule='evenodd'%3E%3Cg id='charlie-brown' fill='%2392aca6' fill-opacity='0.4'%3E%3Cpath d='M9.8 12L0 2.2V.8l10 10 10-10v1.4L10.2 12h-.4zm-4 0L0 6.2V4.8L7.2 12H5.8zm8.4 0L20 6.2V4.8L12.8 12h1.4zM9.8 0l.2.2.2-.2h-.4zm-4 0L10 4.2 14.2 0h-1.4L10 2.8 7.2 0H5.8z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
		display: flex;
		font-size: 100% 100%;
		min-height: 100vh;
		justify-content: center;
		align-items: center;
		position: relative;
		background-position: center center;
		// 表单盒子
		.register_form {
			border-radius: 12px;
			padding: 40px 60px;
			margin: 0;
			background: #F9FAFB;
			border: 1px solid #D1D5DB;
			box-shadow: 0 10px 40px rgba(0,0,0,0.12);
			display: block;
			width: 600px;
			justify-content: center;
			flex-wrap: wrap;
		}
		// 标题样式
		.title_view {
			padding: 0 0 30px;
			margin: 0;
			color: #111827;
			background: none;
			font-weight: 600;
			width: 100%;
			font-size: 24px;
			text-align: center;
		}
		// item盒子
		.list_item {
			margin: 0 0 20px;
			display: flex;
			width: 100%;
			justify-content: flex-start;
			align-items: center;
			// label
			.list_label {
				color: #374151;
				width: 100px;
				font-size: 14px;
				box-sizing: border-box;
				text-align: right;
				padding-right: 15px;
			}
			// 输入框
			:deep(.list_inp) {
				border: 1px solid #D1D5DB;
				border-radius: 8px;
				padding: 0 12px;
				width: calc(100% - 100px);
				line-height: 40px;
				box-sizing: border-box;
				height: 40px;
				background: #FFFFFF;
				//去掉默认样式
				.el-input__wrapper{
					border: none;
					box-shadow: none;
					background: none;
					border-radius: 0;
					height: 100%;
					padding: 0;
				}
				.is-focus {
					box-shadow: none !important;
				}
				&:hover {
					border-color: #165DFF;
				}
			}
		}
		//下拉框样式
		:deep(.list_sel) {
			border: 1px solid #D1D5DB;
			border-radius: 8px;
			padding: 0 12px;
			background: #fff;
			width: calc(100% - 100px);
			line-height: 40px;
			box-sizing: border-box;
			height: 40px;
			//去掉默认样式
			.select-trigger{
				height: 100%;
				.el-input{
					height: 100%;
					.el-input__wrapper{
						border: none;
						box-shadow: none;
						background: none;
						border-radius: 0;
						height: 100%;
						padding: 0;
					}
					.is-focus {
						box-shadow: none !important;
					}
				}
			}
			&:hover {
				border-color: #165DFF;
			}
		}
		//图片上传样式
		.list_file_list  {
			//提示语
			:deep(.el-upload__tip){
				margin: 7px 0 0;
				color: #9CA3AF;
				display: flex;
				font-size: 14px;
				justify-content: flex-start;
				align-items: center;
			}
			//外部盒子
			:deep(.el-upload--picture-card){
				border: 1px solid #D1D5DB;
				cursor: pointer;
				background-color: #fff;
				border-radius: 8px;
				width: 120px;
				line-height: 70px;
				text-align: center;
				height: 80px;
				//图标
				.el-icon{
					color: #9CA3AF;
					font-size: 24px;
				}
				&:hover {
					border-color: #165DFF;
				}
			}
			:deep(.el-upload-list__item) {
				border: 1px solid #D1D5DB;
				cursor: pointer;
				background-color: #fff;
				border-radius: 8px;
				width: 120px;
				line-height: 70px;
				text-align: center;
				height: 80px;
			}
		}
		//按钮盒子
		.list_btn {
			margin: 30px 0 0;
			display: flex;
			width: 100%;
			justify-content: center;
			align-items: center;
			flex-wrap: wrap;
			//注册按钮
			.register {
					border: none;
					border-radius: 8px;
					margin: 0 0 20px;
					color: #fff;
					background: #165DFF;
					width: 100%;
					font-size: 16px;
					font-weight: 600;
					height: 44px;
					&:hover {
						background: #4080FF;
					}
			}
			//已有账号
			.r-login {
				cursor: pointer;
				padding: 0;
				color: #165DFF;
				width: auto;
				font-size: 14px;
				text-align: center;
				&:hover {
					text-decoration: underline;
				}
			}
		}
	}
</style>
