<template>
	<div class="auth">
		<div class="auth__backdrop" aria-hidden="true"></div>
		<div class="auth__card" role="main">
			<header class="auth__header">
				<div class="auth__brand">家校合作平台</div>
				<h1 class="auth__title">登录</h1>
				<p class="auth__subtitle">学生 / 教师账号均可登录</p>
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
					></el-input>
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
					></el-input>
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

				<div class="auth__row" v-if="loginType==1">
					<el-checkbox v-model="rememberPassword" class="auth__checkbox" :true-label="true" :false-label="false">
						记住密码
					</el-checkbox>
				</div>

				<div class="auth__actions">
					<el-button class="auth__submit" v-if="loginType==1" type="primary" :loading="submitting" @click="handleLogin">
						登录
					</el-button>
				</div>
			</el-form>
		</div>
	</div>
</template>
<script setup>
	import {
		ref,
		getCurrentInstance,
		onMounted,
		computed,
	} from "vue";
	import menu from '@/utils/menu'
	const userList = ref([])
	const menus = ref([])
	const loginForm = ref({
		role: '',
		username: '',
		password: ''
	})
	const tableName = ref('')
	const loginType = ref(1)
	//是否记住密码
	const rememberPassword = ref(true)
	const submitting = ref(false)
	const usernameInputId = 'front-login-username'
	const passwordInputId = 'front-login-password'
	const roleSelectId = 'front-login-role'
	const errors = ref({
		username: '',
		password: '',
		role: '',
	})
	const errorSummary = computed(() => Object.values(errors.value).filter(Boolean))
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	const clearFieldError = (key) => {
		if (errors.value[key]) errors.value[key] = ''
	}
	const validate = () => {
		errors.value = { username: '', password: '', role: '' }
		if (!loginForm.value.username) {
			errors.value.username = '请输入账号'
		}
		if (!loginForm.value.password) {
			errors.value.password = '请输入密码'
		}
		if (userList.value.length > 1 && !loginForm.value.role) {
			errors.value.role = '请选择用户类型'
		}
		return errorSummary.value.length === 0
	}
	const handleLogin = () => {
		if (submitting.value) return
		if (!validate()) {
			context?.$toolUtil.message(errorSummary.value[0], 'error')
			return
		}
		if (!userList.value.length) {
			context?.$toolUtil.message('系统初始化中，请稍后重试', 'error')
			return
		}
		if (userList.value.length > 1) {
			for (let i = 0; i < menus.value.length; i++) {
				if (menus.value[i].roleName == loginForm.value.role) {
					tableName.value = menus.value[i].tableName;
				}
			}
		} else {
			tableName.value = userList.value[0].tableName;
			loginForm.value.role = userList.value[0].roleName;
		}
		login()
	}
	const login = () => {
		submitting.value = true
		context?.$http({
			url: `${tableName.value}/login?username=${loginForm.value.username}&password=${loginForm.value.password}`,
			method: 'post'
		}).then(res => {
			//是否保存当前账号密码至缓存
			if (rememberPassword.value) {
				let loginForm1 = JSON.parse(JSON.stringify(loginForm.value))
				delete loginForm1.code
				context?.$toolUtil.storageSet("frontLoginForm", JSON.stringify(loginForm1));
			} else {
				context?.$toolUtil.storageRemove("frontLoginForm")
			}
			context?.$toolUtil.storageSet("frontToken", res.data.token);
			context?.$toolUtil.storageSet("frontRole", loginForm.value.role);
			context?.$toolUtil.storageSet("frontSessionTable", tableName.value);
			let path = context?.$toolUtil.storageGet('toPath')
			if (path) {
				context?.$router.push(path)
				context?.$toolUtil.storageRemove('toPath')
				return
			}
			context?.$router.push(`/index/${tableName.value}Center`)
		}, () => {}).finally(()=>{
			submitting.value = false
		})
	}
	//获取菜单
	const getMenu=()=> {
		let arr = menu.list()
		menus.value = Array.isArray(arr) ? arr : []
		for (let i = 0; i < menus.value.length; i++) {
			if (menus.value[i].hasFrontLogin=='是' && menus.value[i].tableName != 'jiazhang') {
				userList.value.push(menus.value[i])
			}
		}
    }
	//初始化
	const init = () => {
		getMenu();
		if (!userList.value.length) {
			context?.$http({
				url: "menu/list",
				method: "get",
				params: {
					page: 1,
					limit: 1,
					sort: 'id',
				}
			}).then(res => {
				const raw = res?.data?.data?.list?.[0]?.menujson
				if (raw) {
					context?.$toolUtil.storageSet("menus", raw);
					getMenu()
					if (!loginForm.value.role && userList.value.length) {
						loginForm.value.role = userList.value[0].roleName
					}
				}
			})
		}
		//获取缓存是否有保存的账号密码
		let form = context?.$toolUtil.storageGet('frontLoginForm')
		if (form) {
			loginForm.value = JSON.parse(form)
		}else {
			if (userList.value.length) {
				loginForm.value.role = userList.value[0].roleName
			}
		}
	}
	onMounted(()=>{
		init()
	})
</script>

<style lang="scss" scoped>
	.auth {
		--auth-bg: radial-gradient(1200px 800px at 20% 10%, rgba(59, 130, 246, 0.22), rgba(0, 0, 0, 0)),
			radial-gradient(900px 700px at 80% 30%, rgba(236, 72, 153, 0.18), rgba(0, 0, 0, 0)),
			linear-gradient(180deg, #f8fafc, #eef2ff);
		--auth-surface: rgba(255, 255, 255, 0.78);
		--auth-border: rgba(15, 23, 42, 0.12);
		--auth-text: #0f172a;
		--auth-muted: rgba(15, 23, 42, 0.7);
		--auth-brand: #2563eb;
		--auth-danger: #b91c1c;
		--auth-focus: rgba(37, 99, 235, 0.35);
		min-height: 100vh;
		display: grid;
		place-items: center;
		padding: 24px;
		background: var(--auth-bg);
		color: var(--auth-text);
	}

	@media (prefers-color-scheme: dark) {
		.auth {
			--auth-bg: radial-gradient(1100px 750px at 20% 10%, rgba(59, 130, 246, 0.22), rgba(0, 0, 0, 0)),
				radial-gradient(900px 700px at 80% 30%, rgba(236, 72, 153, 0.18), rgba(0, 0, 0, 0)),
				linear-gradient(180deg, #0b1220, #0f172a);
			--auth-surface: rgba(2, 6, 23, 0.7);
			--auth-border: rgba(148, 163, 184, 0.25);
			--auth-text: #e2e8f0;
			--auth-muted: rgba(226, 232, 240, 0.72);
			--auth-brand: #60a5fa;
			color-scheme: dark;
		}
	}

	.auth__backdrop {
		position: fixed;
		inset: 0;
		background-image: url(http://clfile.zggen.cn/20240301/cb59505e774a42899501d8d7f1360b75.jpg);
		background-size: cover;
		background-position: center;
		filter: saturate(0.9) contrast(1.02);
		opacity: 0.18;
		pointer-events: none;
	}

	.auth__card {
		position: relative;
		width: min(440px, 100%);
		border: 1px solid var(--auth-border);
		border-radius: 16px;
		background: var(--auth-surface);
		backdrop-filter: blur(14px);
		-webkit-backdrop-filter: blur(14px);
		box-shadow: 0 18px 60px rgba(2, 6, 23, 0.14);
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
		border: 1px solid rgba(185, 28, 28, 0.35);
		background: rgba(185, 28, 28, 0.08);
		color: var(--auth-text);
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
		background: rgba(255, 255, 255, 0.55);
	}

	@media (prefers-color-scheme: dark) {
		:deep(.el-input__wrapper),
		:deep(.el-select__wrapper) {
			background: rgba(2, 6, 23, 0.35);
		}
	}

	:deep(.el-input__wrapper.is-focus),
	:deep(.el-select__wrapper.is-focused) {
		border-color: var(--auth-brand);
		box-shadow: 0 0 0 4px var(--auth-focus) !important;
	}

	.auth__row {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin: 10px 0 6px;
	}

	.auth__checkbox {
		:deep(.el-checkbox__label) {
			color: var(--auth-muted);
		}
	}

	.auth__actions {
		display: grid;
		margin-top: 12px;
	}

	.auth__submit {
		width: 100%;
		border-radius: 12px;
		height: 44px;
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

