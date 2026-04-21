<template>
	<div>
		<div class="register_view">
			<el-form :model="registerForm" class="register_form">
				<div class="title_view">{{projectName}}注册</div>
				<div class="list_item">
					<div class="list_label">账号：</div>
					<input class="list_inp"
					 v-model="registerForm.xuehao" 
					 placeholder="请输入账号"
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
				<div class="list_item">
					<div class="list_label">角色：</div>
					<el-radio-group v-model="registerForm.role" class="role_radio_group">
						<el-radio label="学生">学生</el-radio>
						<el-radio label="家长">家长</el-radio>
					</el-radio-group>
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
	import { useRoute } from 'vue-router';
	const route = useRoute()
	const tableName = ref('xuesheng')
	
	const registerForm = ref({
        xingbie: '',
        role: '学生',
	})
	const xueshengxingbieLists = ref([])
	const init=()=>{
		xueshengxingbieLists.value = "男,女".split(',')
		const registerRole = context?.$toolUtil.storageGet('registerRole')
		if (registerRole === '学生' || registerRole === '家长') {
			registerForm.value.role = registerRole
		}
	}
    const touxiangUploadSuccess=(fileUrls)=> {
        registerForm.value.touxiang = fileUrls;
    }
	const handleRegister = () => {
		let url = tableName.value +"/register";
		if((!registerForm.value.xuehao)){
			context?.$toolUtil.message(`账号不能为空`,'error')
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
			context?.$toolUtil.storageRemove('registerRole')
			context?.$toolUtil.message('注册成功','success', obj=>{
				context?.$router.push({
					path: "/login"
				});
			})
		})
	}
	const isValidPassword = (pwd) => {
		if (!pwd) return false
		return pwd.length >= 1 && pwd.length <= 64
	}
	const getUUID=()=> {
		return new Date().getTime();
	}
	const close = () => {
		context?.$toolUtil.storageRemove('registerRole')
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
		.list_item {
			margin: 0 0 20px;
			display: flex;
			width: 100%;
			justify-content: flex-start;
			align-items: center;
			.list_label {
				color: #374151;
				width: 100px;
				font-size: 14px;
				box-sizing: border-box;
				text-align: right;
				padding-right: 15px;
			}
		}
	}
</style>
