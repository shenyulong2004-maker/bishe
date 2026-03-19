<template>
	<div class="auth">
		<div class="auth__card" role="main">
			<header class="auth__header">
				<h1 class="auth__title">家校合作平台登录</h1>
				<p class="auth__subtitle">学生 / 教师账号均可登录</p>
			</header>

			<el-form :model="loginForm" class="auth__form" @submit.prevent="handleLogin">
				<div v-if="errorSummary.length" class="auth__alert" role="alert" aria-live="polite">
					<div v-for="(msg, idx) in errorSummary" :key="idx" class="auth__alertLine">{{ msg }}</div>
				</div>

				<div class="field">
					<label class="field__label" :for="usernameInputId">账号：</label>
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

				<div class="field">
					<label class="field__label" :for="passwordInputId">密码：</label>
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
					<label class="field__label">用户类型：</label>
					<div class="role_btn_group">
						<button
							v-for="(item,index) in userList"
							:key="index"
							type="button"
							class="role_btn"
							:class="{ active: loginForm.role === item.roleName }"
							@click="loginForm.role = item.roleName; clearFieldError('role')"
						>
							{{ item.roleName }}
						</button>
					</div>
					<div v-if="errors.role" class="field__error">{{ errors.role }}</div>
				</div>

				<div class="auth__row">
					<el-checkbox v-model="rememberPassword" class="auth__checkbox" :true-label="true" :false-label="false">
						记住密码
					</el-checkbox>
					<div class="register_view">
						<span class="register_link" @click="handleRegister('xuesheng')">注册学生</span>
						<span class="register_link" @click="handleRegister('jiaoshi')">注册教师</span>
					</div>
				</div>

				<div class="auth__actions">
					<el-button class="auth__submit" type="primary" :loading="submitting" @click="handleLogin">
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
	const userList = ref([
		{ roleName: '学生', tableName: 'xuesheng' },
		{ roleName: '教师', tableName: 'jiaoshi' },
	])
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
	// 角色名到tableName的兜底映射
	const roleTableMap = {
		'学生': 'xuesheng',
		'教师': 'jiaoshi',
	}
	const handleLogin = () => {
		if (submitting.value) return
		if (!validate()) {
			context?.$toolUtil.message(errorSummary.value[0], 'error')
			return
		}
		// 从菜单列表中查找tableName
		let found = false
		if (Array.isArray(menus.value) && menus.value.length) {
			for (let i = 0; i < menus.value.length; i++) {
				if (menus.value[i] && menus.value[i].roleName == loginForm.value.role) {
					tableName.value = menus.value[i].tableName;
					found = true
					break;
				}
			}
		}
		// 菜单未加载时，用兜底映射
		if (!found) {
			const mapped = roleTableMap[loginForm.value.role]
			if (mapped) {
				tableName.value = mapped
				found = true
			}
		}
		if (!found || !tableName.value) {
			context?.$toolUtil.message('请选择正确的用户类型', 'error')
			return
		}
		login()
	}
	//注册
	const handleRegister = (tableName) => {
		context?.$router.push(`/${tableName}Register`)
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
	const getMenu = () => {
		let arr = menu.list()
		menus.value = Array.isArray(arr) ? arr : []
		const filtered = []
		for (let i = 0; i < menus.value.length; i++) {
			const m = menus.value[i]
			// 只保留学生和教师角色，排除家长
			if (m && m.tableName && m.tableName !== 'jiazhang' && (m.tableName === 'xuesheng' || m.tableName === 'jiaoshi' || m.hasFrontLogin === '是')) {
				filtered.push(m)
			}
		}
		// 只有从后端取到菜单数据时才覆盖默认值
		if (filtered.length) {
			userList.value = filtered
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
				}
				// 无论是否有菜单数据，都设置默认角色
				if (!loginForm.value.role) {
					if (userList.value.length) {
						loginForm.value.role = userList.value[0].roleName
					} else {
						// 兜底：直接设置默认角色
						loginForm.value.role = '学生'
					}
				}
			}).catch(() => {
				// 接口失败时仍可尝试登录
				if (!loginForm.value.role) {
					loginForm.value.role = '学生'
				}
			})
		} else {
			if (!loginForm.value.role) {
				loginForm.value.role = userList.value[0].roleName
			}
		}
		//获取缓存是否有保存的账号密码
		let form = context?.$toolUtil.storageGet('frontLoginForm')
		if (form) {
			loginForm.value = JSON.parse(form)
		}
	}
	onMounted(()=>{
		init()
	})
</script>

<style lang="scss" scoped>
	.auth {
		min-height: 100vh;
		display: grid;
		place-items: center;
		padding: 24px;
		background-color: #dce5db;
		background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='20' height='12' viewBox='0 0 20 12'%3E%3Cg fill-rule='evenodd'%3E%3Cg id='charlie-brown' fill='%2392aca6' fill-opacity='0.4'%3E%3Cpath d='M9.8 12L0 2.2V.8l10 10 10-10v1.4L10.2 12h-.4zm-4 0L0 6.2V4.8L7.2 12H5.8zm8.4 0L20 6.2V4.8L12.8 12h1.4zM9.8 0l.2.2.2-.2h-.4zm-4 0L10 4.2 14.2 0h-1.4L10 2.8 7.2 0H5.8z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
		color: #111827;
	}

	.auth__card {
		position: relative;
		width: min(440px, 100%);
		border: 1px solid #D1D5DB;
		border-radius: 12px;
		background: #F9FAFB;
		box-shadow: 0 10px 40px rgba(0,0,0,0.12);
		padding: 40px;
	}

	.auth__header {
		display: grid;
		gap: 8px;
		margin-bottom: 30px;
		text-align: center;
	}

	.auth__title {
		font-size: 24px;
		font-weight: 600;
		color: #111827;
		margin: 0;
	}

	.auth__subtitle {
		margin: 0;
		font-size: 14px;
		color: #9CA3AF;
	}

	.auth__alert {
		border: 1px solid #F53F3F;
		background: rgba(245, 63, 63, 0.05);
		color: #F53F3F;
		border-radius: 8px;
		padding: 12px;
		margin-bottom: 20px;
		text-align: center;
	}

	.auth__alertLine {
		font-size: 13px;
	}

	.field {
		display: grid;
		gap: 8px;
		margin-bottom: 20px;
	}

	.field__label {
		font-size: 14px;
		font-weight: 500;
		color: #374151;
	}

	.field__error {
		font-size: 12px;
		color: #F53F3F;
	}

	.field__control {
		width: 100%;
	}

	.role_btn_group {
		display: flex;
		gap: 12px;
		width: 100%;

		.role_btn {
			flex: 1;
			height: 40px;
			display: grid;
			place-items: center;
			background: #FFFFFF;
			border: 1px solid #D1D5DB;
			border-radius: 8px;
			color: #374151;
			font-size: 14px;
			cursor: pointer;
			transition: all 0.2s ease;

			&:hover {
				border-color: #165DFF;
				color: #165DFF;
			}

			&.active {
				background: #165DFF;
				border-color: #165DFF;
				color: #FFFFFF;
				font-weight: 600;
				box-shadow: 0 4px 12px rgba(22, 93, 255, 0.2);
			}
		}
	}

	:deep(.el-input__wrapper),
	:deep(.el-select__wrapper) {
		border-radius: 8px;
		box-shadow: none !important;
		border: 1px solid #D1D5DB;
		background: #FFFFFF;
		height: 40px;
		&:hover {
			border-color: #165DFF;
		}
		&.is-focus, &.is-focused {
			border-color: #165DFF !important;
			box-shadow: 0 0 0 2px rgba(22, 93, 255, 0.1) !important;
		}
	}

	.auth__row {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin: 10px 0 20px;
		
		.register_view {
			display: flex;
			gap: 15px;
			.register_link {
				font-size: 13px;
				color: #165DFF;
				cursor: pointer;
				&:hover {
					text-decoration: underline;
					color: #4080FF;
				}
			}
		}
	}

	.auth__checkbox {
		:deep(.el-checkbox__label) {
			color: #374151;
			font-size: 14px;
		}
		:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
			background-color: #165DFF;
			border-color: #165DFF;
		}
	}

	.auth__actions {
		display: grid;
		margin-top: 10px;
	}

	.auth__submit {
		width: 100%;
		border-radius: 8px;
		height: 44px;
		font-weight: 600;
		background: #165DFF;
		border-color: #165DFF;
		font-size: 16px;
		&:hover {
			background: #4080FF;
			border-color: #4080FF;
		}
	}

	@media (max-width: 480px) {
		.auth {
			padding: 16px;
		}
		.auth__card {
			padding: 24px;
		}
		.auth__title {
			font-size: 20px;
		}
	}
</style>

