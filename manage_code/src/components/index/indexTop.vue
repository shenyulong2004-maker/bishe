<template>
	<div class="top_view">
		<div class="top_left_view">
			<div class="fold_view" @click="toggleClick">
				<el-icon class="icons">
					<Fold v-if="!collapse" />
					<Expand v-else />
				</el-icon>
			</div>
		</div>

		<div class="projectTitle">家校合作平台</div>
		<div class="top_right_view">
			<el-dropdown class="avatar-container right-menu-item" trigger="hover">
				<div class="avatar-wrapper">
					<div class="nickname">欢迎 {{$toolUtil.storageGet('adminName')}}</div>
					<img class="user-avatar" src="@/assets/img/avatar.png">
					<el-icon class="el-icon--right">
						<arrow-down />
					</el-icon>
				</div>
				<template #dropdown>
					<el-dropdown-menu slot="dropdown">
						<el-dropdown-item @click="centerClick" v-if="roleName!='管理员'">
							个人中心
						</el-dropdown-item>
						<el-dropdown-item @click="updatepasswordClick">
							修改密码
						</el-dropdown-item>
						<el-dropdown-item v-if="roleName!='管理员'">
							<span style="display:block;" @click="frontClick">系统前台</span>
						</el-dropdown-item>
						<el-dropdown-item>
							<span style="display:block;" @click="onLogout">退出登录</span>
						</el-dropdown-item>
					</el-dropdown-menu>
				</template>
			</el-dropdown>
		</div>
	</div>
</template>

<script setup>
	import axios from 'axios'
	import {
		ElMessageBox
	} from 'element-plus'
	import {
		toRefs,
		defineEmits,
		getCurrentInstance,
		ref,
		onBeforeUnmount
	} from 'vue';
	import { useStore } from 'vuex'
	const store = useStore()
	import {
		useRouter
	} from 'vue-router';
	const props = defineProps({
		collapse: Boolean
	})
	const {
		collapse,
		
	} = toRefs(props)
	
	const router = useRouter()
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	const emit = defineEmits(['collapseChange'])
	const role = context?.$toolUtil.storageGet('sessionTable')
	const roleName = context?.$toolUtil.storageGet('role')
	const toggleClick = () => {
		emit('collapseChange')
	}
	const getSession = () => {
		context?.$http({
			url: `${context?.$toolUtil.storageGet('sessionTable')}/session`,
			method: 'get'
		}).then(res=>{
			context?.$toolUtil.storageSet('userid',res.data.data.id)
		})
	}
	// 退出登录
	const onLogout = () => {
		let toolUtil = context?.$toolUtil
		store.dispatch('delAllCachedViews')
		store.dispatch('delAllVisitedViews')
		toolUtil.storageClear()
		router.replace({
			name: "login"
		});
	}
	// 跳转前台
	const frontClick = () => {
		window.location.href = `${context?.$config.indexUrl}`
	}
	// 个人中心
	const centerClick = () => {
		router.push(`/${role}Center`)
	}
	// 修改密码
	const updatepasswordClick = () => {
		router.push(`/updatepassword`)
	}
	getSession()
</script>

<style lang="scss" scoped>
	// 总盒子
	.top_view {
		z-index: 9;
		display: flex;
		border-color: #E5E7EB;
		top: 0;
		left: 0;
		background: transparent;
		width: 100%;
		justify-content: flex-start;
		border-width: 0 0 1px;
		align-items: center;
		position: fixed;
		border-style: solid;
		height: 60px;
		box-shadow: 0 1px 4px rgba(0,0,0,0.02);
		// 左边盒子
		.top_left_view {
			display: flex;
			width: auto;
			align-items: center;
			height: 100%;
			// 折叠按钮盒子
			.fold_view {
				padding: 0 15px;
				display: flex;
				cursor: pointer;
				// 图标
				.icons {
					color: #374151;
					font-size: 20px;
				}
				&:hover {
					background: #F3F4F6;
				}
			}
		}
		// 标题
		.projectTitle{
			padding: 0 20px;
			margin: 0 0 0 10px;
			color: #111827;
			font-weight: 600;
			width: auto;
			font-size: 20px;
			line-height: 100%;
		}
		// 右部盒子
		.top_right_view{
			display: flex;
			width: auto;
			justify-content: flex-end;
			position: absolute;
			right: 10px;
			height: 100%;
			order: 3;
			// 头像盒子
			.avatar-container {
				cursor: pointer;
				margin: 0 20px 0 0;
				color: #374151;
				display: flex;
				align-items: center;
				height: 100%;
				.avatar-wrapper {
					display: flex;
					position: relative;
					align-items: center;
					// 昵称
					.nickname {
						cursor: pointer;
						margin: 0 10px 0 0;
						color: #374151;
						font-size: 14px;
					}
					// 头像
					.user-avatar {
						cursor: pointer;
						border-radius: 50%;
						margin: 0 4px 0 0;
						width: 32px;
						height: 32px;
						border: 1px solid #E5E7EB;
					}
					// 图标
					.el-icon--right {
						color: #9CA3AF;
						font-size: 12px;
					}
				}
			}
		}
	}
	// 下拉盒子
	.el-dropdown-menu{
		background: #fff;
		border: 1px solid #E5E7EB;
		padding: 4px 0;
		// 下拉盒子itme
		:deep(.el-dropdown-menu__item){
			color: #374151;
			font-size: 14px;
			padding: 8px 16px;
		}
		// item悬浮
		:deep(.el-dropdown-menu__item:hover){
			color: #165DFF;
			background: #F3F4F6;
		}
	}
	
</style>
