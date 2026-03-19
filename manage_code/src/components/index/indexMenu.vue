<template>
	<div>
		<el-scrollbar wrap-class="scrollbar-wrapper" class="menu_scrollbar">
			<el-menu :default-openeds="[]" :unique-opened="false" default-active="0" class="menu_view"
				:collapse="collapse">
				<el-sub-menu :index="0" @click="menuHandler('')">
					<template #title>
						<i class="iconfont icon-zhuye2" v-if="collapse?true:true"></i>
						<span>首页</span>
					</template>
				</el-sub-menu>
				<el-sub-menu v-for=" (menu,index) in menuList.backMenu" :key="menu.menu" :index="index+2+''">
					<template #title>
						<i class="iconfont" :class="menu.fontClass" v-if="collapse?true:true"></i>
						<span>{{ menu.menu }}</span>
					</template>
					<el-menu-item class="menu_item_view" v-for=" (child,sort) in menu.child" :key="sort"
						:index="(index+2)+'-'+sort" @click="menuHandler(child.tableName,child.menuJump)">{{ child.menu }}
					</el-menu-item>
				</el-sub-menu>
			</el-menu>
		</el-scrollbar>
	</div>
</template>

<script setup>
	import menu from '@/utils/menu'
	import {
		ref,
		toRefs,
		getCurrentInstance,
		nextTick
	} from 'vue';
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	//props
	const props = defineProps({
		collapse: Boolean
	})
	const {
		collapse
	} = toRefs(props)
	//data
	const menuList = ref([])
	const role = ref('')
	const styleChange = () => {
		nextTick(() => {
			document.querySelectorAll('.el-menu-vertical-demo .el-sub-menu .el-menu').forEach(el => {
				el.removeAttribute('style')
				const icon = {
					"border": "none",
					"padding": "0",
					"margin": "10px auto 0",
					"borderRadius": "0px",
					"background": "none",
					"display": "none",
					"width": "100%"
				}
				Object.keys(icon).forEach((key) => {
					el.style[key] = icon[key]
				})
			})
		})
	}
	//权限验证
	const btnAuth = (e,a)=>{
		return context?.$toolUtil.isAuth(e,a)
	}
	const init = () => {
		const menus = menu.list()
		if (menus) {
			menuList.value = menus
		}
		role.value = context?.$toolUtil.storageGet('role')

		for (let i = 0; i < menuList.value.length; i++) {
			if (menuList.value[i].roleName == role.value) {
				menuList.value = menuList.value[i];
				break;
			}
		}
		// styleChange()
	}
	const menuHandler = (name,menuJump) => {
		if(name == 'center'){
			name = `${role.value}Center`
		}
		if(name == 'storeup'){
			name = `storeup?type=${menuJump}`
		}
		if(name == 'exampaper' && menuJump == '12'){
			name = 'exampaperlist'
		}
		if(name == 'examrecord' && menuJump == '22'){
			name = 'examfailrecord'
		}
		let router = context?.$router
		name = '/' + name
		router.push(name)
	}
	init()
</script>

<style lang="scss" scoped>
	// 总盒子
	:deep(.menu_scrollbar) {

		// 菜单盒子-展开样式
		.menu_view {
			border: 0;
			padding: 0px 0 60px;
			color: #374151;
			background: transparent;
			height: 100%;

			// 无二级菜单
			.el-menu-item {
				padding: 0 20px;
				color: #374151;
				background: none;
				line-height: 50px;
				height: 50px;
				.iconfont {
					margin: 0 10px 0 0;
					color: inherit;
					width: 20px;
					vertical-align: middle;
					font-size: 18px;
					text-align: center;
				}
			}

			// 无二级悬浮
			.el-menu-item:hover {
				color: #165DFF;
				background: #F3F4F6;
			}

			// 无二级选中
			.el-menu-item.is-active {
				color: #165DFF;
				background: #E8F3FF;
				font-weight: 600;
			}

			// 有二级盒子
			.el-sub-menu {
				cursor: pointer;
				padding: 0;
				color: #374151;
				white-space: nowrap;
				background: none;
				position: relative;

				// 有二级item
				.el-sub-menu__title {
					padding: 0 20px;
					color: #374151;
					background: none;
					line-height: 50px;
					height: 50px;
					.iconfont {
						margin: 0 10px 0 0;
						color: inherit;
						width: 20px;
						vertical-align: middle;
						font-size: 18px;
						text-align: center;
					}
					.el-sub-menu__icon-arrow{
						margin: 0;
						color: #9CA3AF;
						font-size: 12px;
					}
				}

				// 有二级item悬浮
				.el-sub-menu__title:hover {
					color: #165DFF;
					background: #F3F4F6;
				}
			}
			//二级选中
			.is-active {
				.el-sub-menu__title {
					color: #165DFF;
				}
			}
			// 二级盒子
			.el-menu--inline {
				border: none;
				padding: 0px;
				background: #F9FAFB;
				// 二级菜单
				.menu_item_view {
					padding: 0 20px 0 50px;
					color: #374151;
					background: none;
					line-height: 40px;
					height: 40px;
					font-size: 14px;
				}
				// 二级悬浮
				.menu_item_view:hover {
					color: #165DFF;
					background: #F3F4F6;
				}
				// 二级选中
				.is-active.menu_item_view {
					color: #165DFF;
					background: #E8F3FF;
					font-weight: 600;
				}
			}
		}
		// 菜单盒子-关闭样式
		.el-menu--collapse {
			padding: 0;
			background: transparent;
			height: 100%;

			.el-menu-item, .el-sub-menu__title {
				padding: 0 !important;
				display: flex;
				justify-content: center;
				align-items: center;
				color: #374151;
				.iconfont {
					margin: 0;
					font-size: 20px;
				}
			}

			.el-menu-item:hover, .el-sub-menu__title:hover {
				background: #F3F4F6;
				color: #165DFF;
			}

			.el-menu-item.is-active {
				background: #E8F3FF;
				color: #165DFF;
			}
		}
	}
</style>
<style lang="scss">
	.el-popper{
		.el-menu--popup-container {
			.el-menu--popup{
				border: 1px solid #E5E7EB;
				padding: 4px;
				background: #FFFFFF;
				box-shadow: 0 2px 12px rgba(0,0,0,0.1);
				// 二级菜单
				.menu_item_view {
					padding: 0 20px;
					color: #374151;
					background: none;
					line-height: 40px;
					height: 40px;
					font-size: 14px;
					border-radius: 4px;
				}
				// 二级悬浮
				.menu_item_view:hover {
					color: #165DFF;
					background: #F3F4F6;
				}
				// 二级选中
				.is-active.menu_item_view {
					color: #165DFF;
					background: #E8F3FF;
				}
			}
		}
	}
</style>
