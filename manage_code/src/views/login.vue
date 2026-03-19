<template>
	<div>
    <canvas id="canvas"></canvas>
		<div class="login_view">
			<el-form :model="loginForm" class="login_form">
				<div class="title_view">家校合作平台登录</div>
				<div class="list_item" v-if="loginType==1">
					<div class="list_label">
						账号：
					</div>
					<input class="list_inp" v-model="loginForm.username" placeholder="请输入账号" />
				</div>
				<div class="list_item" v-if="loginType==1">
					<div class="list_label">
						密码：
					</div>
					<input class="list_inp" v-model="loginForm.password" type="password" placeholder="请输入密码" @keydown.enter.native="handleLogin"  />
				</div>
				<div class="list_type" v-if="userList.length>1">
					<div class="list_label">
						用户类型：
					</div>
				  <el-select v-model="loginForm.role" placeholder="请选择用户类型">
				    <el-option v-for="(item,index) in userList" :label="item.roleName" :value="item.roleName"></el-option>
				  </el-select>
				</div>
				<div class="btn_view">
					<el-button class="login" v-if="loginType==1" type="success" @click="handleLogin">登录</el-button>
					<el-button class="register" type="primary" @click="handleRegister('xuesheng')">注册学生</el-button>
					<el-button class="register" type="primary" @click="handleRegister('jiaoshi')">注册教师</el-button>
				</div>
			</el-form>
		</div>
		<Vcode :show="isShow" @success="success" @close="close" @fail='fail'></Vcode>
	</div>
</template>
<script setup>
	import {
		ref,
		getCurrentInstance,
		nextTick,
		onMounted,
		onUnmounted,
	} from "vue";
	const userList = ref([])
	const menus = ref([])
	const loginForm = ref({
		role: '',
		username: '',
		password: ''
	})
	const tableName = ref('')
	const loginType = ref(1)
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	//动态背景
	import canvasBg from "@/assets/js/canvas-bg-2.js";
	import "@/assets/css/canvas-bg-2.css"
	// onUnmounted(()=>{
	// 	nextTick(()=>{
	// 		canvasBg = null
	// 	})
	// })
	//注册
    const handleRegister = (tableName) => {
    	context?.$router.push(`/${tableName}Register`)
    	
    }
	const handleLogin = () => {
		if (!loginForm.value.username) {
			context?.$toolUtil.message('请输入用户名', 'error')
			
			return;
		}
		if (!loginForm.value.password) {
			context?.$toolUtil.message('请输入密码', 'error')
			
			return;
		}
		if (!menus.value || menus.value.length === 0) {
			context?.$toolUtil.message('系统初始化中，请稍后重试', 'error')
			return;
		}
		if (userList.value.length > 1) {
			if (!loginForm.value.role) {
				context?.$toolUtil.message('请选择角色', 'error')
				return;
			}
			for (let i = 0; i < menus.value.length; i++) {
				if (menus.value[i] && menus.value[i].roleName === loginForm.value.role && menus.value[i].tableName) {
					tableName.value = menus.value[i].tableName;
					break;
				}
			}
			if (!tableName.value) {
				context?.$toolUtil.message('系统初始化中，请稍后重试', 'error')
				return;
			}
		} else {
			if (userList.value.length > 0 && userList.value[0] && userList.value[0].tableName) {
				tableName.value = userList.value[0].tableName;
				loginForm.value.role = userList.value[0].roleName;
			} else {
				context?.$toolUtil.message('系统初始化中，请稍后重试', 'error')
				return;
			}
		}
		login()
	}
	const login = () => {
		if (!tableName.value) {
			context?.$toolUtil.message('系统初始化中，请稍后重试', 'error')
			return;
		}
		context?.$http({
			url: `${tableName.value}/login?username=${loginForm.value.username}&password=${loginForm.value.password}`,
			method: 'post'
		}).then(res => {
			context?.$toolUtil.storageSet("Token", res.data.token);
			context?.$toolUtil.storageSet("role", loginForm.value.role);
			context?.$toolUtil.storageSet("sessionTable", tableName.value);
			context?.$toolUtil.storageSet("adminName", loginForm.value.username);
			context?.$toolUtil.message('登录成功', 'success')
			context?.$router.push('/')
		}, err => {
			if (err && err.response && err.response.status === 401) {
				context?.$toolUtil.message('用户名或密码错误', 'error')
			} else if (err && err.response && err.response.status === 404) {
				context?.$toolUtil.message('用户不存在', 'error')
			} else {
				context?.$toolUtil.message('登录失败，请检查网络连接或稍后重试', 'error')
			}
		})
	}
	//获取菜单
	const getMenu=()=> {
      let params = {
        page: 1,
        limit: 1,
        sort: 'id',
      }

      context?.$http({
        url: "menu/list",
        method: "get",
        params: params
      }).then(res => {
          menus.value = JSON.parse(res.data.data.list[0].menujson)
		  menus.value = Array.isArray(menus.value) ? menus.value.filter(item => item && item.tableName != 'jiazhang' && item.roleName != '家长') : []
          for (let i = 0; i < menus.value.length; i++) {
            if (menus.value[i].hasBackLogin=='是' && menus.value[i].tableName != 'jiazhang') {
              userList.value.push(menus.value[i])
            }
          }
			if (userList.value.length) {
				loginForm.value.role = userList.value[0].roleName
			}
          context?.$toolUtil.storageSet("menus", JSON.stringify(menus.value));
      }).catch(err => {
          console.error('获取菜单失败:', err)
          context?.$toolUtil.message('系统初始化失败，请刷新页面重试', 'error')
      })
    }
	//初始化
	const init = () => {
		getMenu();
	}
	onMounted(()=>{
		init()
		
		canvasBg()
	})
</script>

<style lang="scss" scoped>
	#canvas {
		background: url(@/assets/jiaxiaopingtai_beijing.jpg) no-repeat center center / cover;
	}
	.login_view {
		background: radial-gradient(circle at center, rgba(0,0,0,0) 0%, rgba(0,0,0,0.15) 100%) !important;
		display: flex;
		align-items: center;
		justify-content: center;
		min-height: 100vh;
		position: relative;
		flex-direction: column;
		// 表单盒子
		.login_form {
			border-radius: 12px;
			padding: 2.5vw;
			margin: 0 auto;
			background: #F9FAFB;
			border: 1px solid #D1D5DB;
			box-shadow: 0 10px 40px rgba(0,0,0,0.12);
			display: flex;
			width: 30vw;
			aspect-ratio: 1 / 1.1;
			justify-content: flex-start;
			align-items: center;
			flex-wrap: wrap;
			box-sizing: border-box;
		}
		.title_view {
			padding: 0px;
			margin: 0 auto 1.5vw;
			color: #111827;
			font-weight: 600;
			width: 85%;
			font-size: 1.2vw;
			text-align: center;
		}
		// item盒子
		.list_item {
			margin: 0.6vw auto;
			background: #fff;
			border: 1px solid #D1D5DB;
			border-radius: 6px;
			display: flex;
			width: 85%;
			justify-content: flex-start;
			align-items: center;
			// label
			.list_label {
				color: #374151;
				background: none;
				width: 5.5vw;
				font-size: 0.8vw;
				line-height: 2.2vw;
				text-align: right;
				padding-right: 0.5vw;
			}
			// 输入框
			.list_inp {
				border: 0px;
				border-radius: 0px;
				padding: 0 0.8vw;
				color: #374151;
				background: none;
				width: 100%;
				line-height: 2.2vw;
				height: 2.2vw;
				font-size: 0.8vw;
				&::placeholder {
					color: #9CA3AF;
				}
			}
		}
		.list_type {
			margin: 0.6vw auto;
			background: #fff;
			border: 1px solid #D1D5DB;
			border-radius: 6px;
			display: flex;
			width: 85%;
			justify-content: flex-start;
			align-items: center;
			order: 3;
			.list_label {
				color: #374151;
				background: none;
				width: 5.5vw;
				font-size: 0.8vw;
				line-height: 2.2vw;
				text-align: right;
				padding-right: 0.5vw;
			}
			// 下拉框样式
			:deep(.el-select) {
				border: 0px;
				border-radius: 0px;
				padding: 0 0.8vw;
				color: #374151;
				background: none;
				width: 100%;
				font-size: 0.8vw;
				line-height: 2.2vw;
				box-sizing: border-box;
				height: 2.2vw;
				//去掉默认样式
				.select-trigger{
					height: 100%;
					.el-input{
						height: 100%;
						.el-input__wrapper{
							border: none;
							box-shadow: none !important;
							background: none;
							border-radius: 0;
							height: 100%;
							padding: 0;
							.el-input__inner {
								color: #374151;
								font-size: 0.8vw;
								&::placeholder {
									color: #9CA3AF;
								}
							}
						}
						.is-focus {
							box-shadow: none !important;
						}
					}
				}
			}
		}
		// 按钮盒子
		.btn_view {
			padding: 0;
			margin: 1vw auto 0;
			display: flex;
			width: 85%;
			justify-content: flex-start;
			align-items: center;
			flex-wrap: wrap;
			order: 5;
			// 登录
			.login {
				border: 0;
				cursor: pointer;
				border-radius: 6px;
				padding: 0 1.2vw;
				margin: 0 auto 0.8vw;
				color: #fff;
				background: #165DFF;
				letter-spacing: 0.1vw;
				width: 100%;
				font-size: 0.9vw;
				height: 2.5vw;
				&:hover {
					opacity: 0.9;
				}
			}
			// 注册
			.register {
				border: 1px solid #D1D5DB;
				cursor: pointer;
				border-radius: 6px;
				padding: 0 1vw;
				margin: 0 0px 0.6vw 0;
				color: #374151;
				background: #fff;
				width: 100%;
				font-size: 0.8vw;
				height: 2.2vw;
				&:hover {
					border-color: #165DFF;
					color: #165DFF;
				}
			}
		}
	}

</style>
