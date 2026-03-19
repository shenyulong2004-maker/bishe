<template>
	<div class="auth">
		<canvas id="canvas" class="auth__canvas" style="background:url(http://clfile.zggen.cn/20231120/5c406dea56f94e7a8862ce254a053b09.jpg) no-repeat center center / cover"></canvas>
		<main class="auth__card">
			<header class="auth__header">
				<div class="auth__brand">家校合作平台</div>
				<h1 class="auth__title">管理端登录</h1>
				<p class="auth__subtitle">管理员 / 学生 / 教师登录入口</p>
			</header>

			<el-form :model="loginForm" class="auth__form" @submit.prevent="handleLogin">
				<div v-if="errorSummary.length" class="auth__alert" role="alert" aria-live="polite">
					<div v-for="(msg, idx) in errorSummary" :key="idx" class="auth__alertLine">{{ msg }}</div>
				</div>

				<div class="field" v-if="loginType==1">
					<label class="field__label" :for="usernameInputId">账号</label>
					<el-input
						v-model="loginForm.username"
						:input-id="usernameInputId"
						class="field__control"
						placeholder="请输入账号"
						autocomplete="username"
						clearable
						@input="clearFieldError('username')"
					/>
					<div v-if="errors.username" class="field__error">{{ errors.username }}</div>
				</div>

				<div class="field" v-if="loginType==1">
					<label class="field__label" :for="passwordInputId">密码</label>
					<el-input
						v-model="loginForm.password"
						:input-id="passwordInputId"
						class="field__control"
						type="password"
						show-password
						placeholder="请输入密码"
						autocomplete="current-password"
						@keyup.enter="handleLogin"
						@input="clearFieldError('password')"
					/>
					<div v-if="errors.password" class="field__error">{{ errors.password }}</div>
				</div>

				<div class="field" v-if="userList.length>1">
					<label class="field__label" :for="roleSelectId">用户类型</label>
					<el-select
						v-model="loginForm.role"
						:teleported="false"
						:input-id="roleSelectId"
						class="field__control"
						placeholder="请选择用户类型"
						@change="clearFieldError('role')"
					>
						<el-option v-for="(item,index) in userList" :key="index" :label="item.roleName" :value="item.roleName"></el-option>
					</el-select>
					<div v-if="errors.role" class="field__error">{{ errors.role }}</div>
				</div>

				<div class="auth__actions">
					<el-button class="auth__submit" v-if="loginType==1" type="primary" :loading="submitting" @click="handleLogin">
						登录
					</el-button>
					<div class="auth__links">
						<el-button class="auth__linkBtn" text type="primary" @click="handleRegister('xuesheng')">注册学生</el-button>
						<el-button class="auth__linkBtn" text type="primary" @click="handleRegister('jiaoshi')">注册教师</el-button>
					</div>
				</div>
			</el-form>
		</main>

		<Vcode :show="isShow" @success="onCaptchaSuccess" @close="onCaptchaClose" @fail="onCaptchaFail"></Vcode>
	</div>
</template>
<script setup>
	import {
		ref,
		getCurrentInstance,
		onMounted,
		computed,
	} from "vue";
	import Vcode from "vue3-puzzle-vcode";
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
	const submitting = ref(false)
	const isShow = ref(false)
	const usernameInputId = 'manage-login-username'
	const passwordInputId = 'manage-login-password'
	const roleSelectId = 'manage-login-role'
	const errors = ref({
		username: '',
		password: '',
		role: '',
	})
	const errorSummary = computed(() => Object.values(errors.value).filter(Boolean))
	const clearFieldError = (key) => {
		if (errors.value[key]) errors.value[key] = ''
	}
	const validate = () => {
		errors.value = { username: '', password: '', role: '' }
		if (!loginForm.value.username) errors.value.username = '请输入账号'
		if (!loginForm.value.password) errors.value.password = '请输入密码'
		if (userList.value.length > 1 && !loginForm.value.role) errors.value.role = '请选择用户类型'
		return errorSummary.value.length === 0
	}
	//动态背景
	import canvasBg from "@/assets/js/canvas-bg-2.js";
	import "@/assets/css/canvas-bg-2.css"
	//注册
    const handleRegister = (tableName) => {
    	context?.$router.push(`/${tableName}Register`)
    	
    }
	const handleLogin = () => {
		if (submitting.value) return
		if (!validate()) {
			context?.$toolUtil.message(errorSummary.value[0], 'error')
			return
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
		isShow.value = true
	}
	const onCaptchaSuccess = () => {
		isShow.value = false
		login()
	}
	const onCaptchaClose = () => {
		isShow.value = false
	}
	const onCaptchaFail = () => {
	}
	const login = () => {
		if (!tableName.value) {
			context?.$toolUtil.message('系统初始化中，请稍后重试', 'error')
			return;
		}
		submitting.value = true
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
		}).finally(()=>{
			submitting.value = false
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
	.auth {
		--auth-surface: rgba(2, 6, 23, 0.68);
		--auth-border: rgba(148, 163, 184, 0.25);
		--auth-text: #e2e8f0;
		--auth-muted: rgba(226, 232, 240, 0.72);
		--auth-brand: #60a5fa;
		--auth-danger: #fca5a5;
		--auth-focus: rgba(96, 165, 250, 0.35);
		min-height: 100vh;
		display: grid;
		place-items: center;
		padding: 24px;
		color: var(--auth-text);
		color-scheme: dark;
		position: relative;
		overflow: hidden;
	}

	.auth__canvas {
		position: fixed;
		inset: 0;
		width: 100%;
		height: 100%;
		z-index: 0;
	}

	.auth__card {
		position: relative;
		z-index: 1;
		width: min(460px, 100%);
		border: 1px solid var(--auth-border);
		border-radius: 16px;
		background: var(--auth-surface);
		backdrop-filter: blur(14px);
		-webkit-backdrop-filter: blur(14px);
		box-shadow: 0 18px 60px rgba(2, 6, 23, 0.35);
		padding: 24px;
	}

	.auth__header {
		display: grid;
		gap: 6px;
		margin-bottom: 18px;
	}

	.auth__brand {
		font-size: 12px;
		letter-spacing: 0.12em;
		text-transform: uppercase;
		color: var(--auth-muted);
	}

	.auth__title {
		font-size: 24px;
		line-height: 1.2;
		margin: 0;
	}

	.auth__subtitle {
		margin: 0;
		font-size: 13px;
		color: var(--auth-muted);
	}

	.auth__alert {
		border: 1px solid rgba(248, 113, 113, 0.35);
		background: rgba(248, 113, 113, 0.08);
		border-radius: 12px;
		padding: 10px 12px;
		margin-bottom: 12px;
	}

	.auth__alertLine {
		font-size: 13px;
	}

	.field {
		display: grid;
		gap: 8px;
		margin-bottom: 14px;
	}

	.field__label {
		font-size: 13px;
		color: var(--auth-muted);
	}

	.field__error {
		font-size: 12px;
		color: var(--auth-danger);
	}

	.field__control {
		width: 100%;
	}

	:deep(.el-input__wrapper),
	:deep(.el-select__wrapper) {
		border-radius: 12px;
		box-shadow: none !important;
		border: 1px solid var(--auth-border);
		background: rgba(2, 6, 23, 0.35);
	}

	:deep(.el-input__wrapper.is-focus),
	:deep(.el-select__wrapper.is-focused) {
		border-color: var(--auth-brand);
		box-shadow: 0 0 0 4px var(--auth-focus) !important;
	}

	.auth__actions {
		display: grid;
		margin-top: 12px;
		gap: 10px;
	}

	.auth__submit {
		width: 100%;
		border-radius: 12px;
		height: 44px;
		font-weight: 600;
	}

	.auth__links {
		display: flex;
		gap: 8px;
		justify-content: center;
		flex-wrap: wrap;
	}

	.auth__linkBtn {
		font-weight: 600;
	}

	@media (max-width: 420px) {
		.auth {
			padding: 16px;
		}
		.auth__card {
			padding: 18px;
			border-radius: 14px;
		}
		.auth__title {
			font-size: 22px;
		}
	}

</style>
