<template>
	<div>
		<div class="app-contain">
			<div>
				<div class="list_search_view">
					<el-form :model="searchQuery" class="search_form">
						<div class="search_view">
							<div class="search_label">
								试卷：
							</div>
							<div class="search_box">
								<el-input class="search_inp" v-model="searchQuery.name" placeholder="试卷名称" clearable>
								</el-input>
							</div>
						</div>
						<div class="search_btn_view">
							<el-button class="search_btn" type="primary" @click="searchClick()">搜索</el-button>
						</div>
					</el-form>
				</div>
			</div>
			<br>
			<el-table v-loading="listLoading" border :stripe='true' @selection-change="handleSelectionChange" ref="table"
				v-if="btnAuth('exampaper','查看')" :data="list" @row-click="listChange">
				<el-table-column :resizable='true' align="left" header-align="left" type="selection" width="55" />
				<el-table-column label="序号" width="120" :resizable='true' :sortable='true' align="left" header-align="left">
					<template #default="scope">{{ scope.$index + 1}}</template>
				</el-table-column>
				<el-table-column label="试卷" :resizable='true' :sortable='true' align="left" header-align="left">
					<template #default="scope">
						{{scope.row.name}}
					</template>
				</el-table-column>
				<el-table-column label="考试时长(分钟)" :resizable='true' :sortable='true' align="left" header-align="left">
					<template #default="scope">
						{{scope.row.time}}
					</template>
				</el-table-column>
				<el-table-column label="操作" :resizable='true' :sortable='true' align="left" header-align="left">
					<template #default="scope">
						<el-button type="success" @click="examClick(scope.row.id)">开始考试</el-button>
					</template>
				</el-table-column>
			</el-table>
			<el-pagination
				background
				:layout="layouts.join(',')"
				:total="total" 
				:page-size="listQuery.limit"
				prev-text="Prev"
				next-text="Next"
				:hide-on-single-page="false"
				:style='{"padding":"0","margin":"20px 0 0","whiteSpace":"nowrap","color":"#333","textAlign":"center","width":"100%","fontWeight":"500"}'
				@size-change="sizeChange"
				@current-change="currentChange" 
				@prev-click="prevClick"
				@next-click="nextClick"  />
		</div>
	</div>
</template>
<script setup>
	import {
		reactive,
		ref,
		getCurrentInstance,
		nextTick,
		onMounted,
	} from 'vue'
	import {
		useRoute,
		useRouter
	} from 'vue-router'
	import {
		ElMessageBox
	} from 'element-plus'
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	const router = useRouter()

	//基础信息
	const tableName = 'exampaper'
	const formName = '试卷'
	const route = useRoute()
	//基础信息
	onMounted(() => {})
	//列表数据
	const list = ref(null)
	const table = ref(null)
	const listQuery = ref({
		page: 1,
		limit: 20,
		sort: 'id',
		status: 1
	})
	const searchQuery = ref({})
	const selRows = ref([])
	const listLoading = ref(false)
	const listChange = (row) => {
		nextTick(() => {
			table.value.clearSelection()
			table.value.toggleRowSelection(row)
		})
	}
	//列表
	const getList = () => {
		listLoading.value = true
		let params = JSON.parse(JSON.stringify(listQuery.value))
		params['sort'] = 'id'
		params['order'] = 'desc'
		if (searchQuery.value.name && searchQuery.value.name != '') {
			params['name'] = '%' + searchQuery.value.name + '%'
		}
		context?.$http({
			url: `${tableName}/page`,
			method: 'get',
			params: params
		}).then(res => {
			listLoading.value = false
			list.value = res.data.data.list
			total.value = Number(res.data.data.total)
		})
	}
	//多选
	const handleSelectionChange = (e) => {
		selRows.value = e
	}
	//列表数据
	//分页
	const total = ref(0)
	const layouts = ref(["prev","pager","next"])
	const sizeChange = (size) => {
		listQuery.value.limit = size
		getList()
	}
	const currentChange = (page) => {
		listQuery.value.page = page
		getList()
	}
	const prevClick = () => {
		listQuery.value.page = listQuery.value.page - 1
		getList()
	}
	const nextClick = () => {
		listQuery.value.page = listQuery.value.page + 1
		getList()
	}
	//分页
	//权限验证
	const btnAuth = (e, a) => {
		return context?.$toolUtil.isAuth(e, a)
	}
	//搜索
	const searchClick = () => {
		listQuery.value.page = 1
		getList()
	}
	//表单
	const formRef = ref(null)
	//考试
	const examClick = (id) => {
		if (id) {
			context?.$http({
				url: `examrecord/deleteRecords?userid=${context?.$toolUtil.storageGet('userid')}&&paperid=${id}`,
				method: 'post'
			})
			router.push(`/examination?id=${id}`)
		}
	}
	//表单

	//初始化
	const init = () => {
		getList()
	}
	init()
</script>
<style lang="scss" scoped>
	.app-contain {
		background: transparent;
		padding: 24px;
		border-radius: 8px;
		min-height: calc(100vh - 120px);
	}
	// 操作盒子
	.list_search_view {
		margin: 0 0 24px;
		padding: 20px;
		background: #EFF8FF;
		border-radius: 8px;
		border: 1px solid #E5E7EB;
		display: flex;
		justify-content: space-between;
		align-items: center;
		flex-wrap: wrap;
		box-shadow: 0 2px 8px rgba(0,0,0,0.05);
		// 搜索盒子
		.search_form {
			display: flex;
			align-items: center;
			order: 1;
			// 子盒子
			.search_view {
				margin: 0 16px 0 0;
				display: flex;
				align-items: center;
				// 搜索label
				.search_label {
					margin: 0 8px 0 0;
					color: #111827;
					background: none;
					font-weight: 500;
					display: inline-block;
					width: auto;
					font-size: 14px;
					line-height: 36px;
					text-align: right;
					height: 36px;
				}
				// 搜索item
				.search_box {
					display: inline-block;
					width: auto;
					// 输入框
					:deep(.search_inp) {
						.el-input__wrapper{
							border: 1px solid #D1D5DB;
							box-shadow: none !important;
							background: #FFFFFF;
							border-radius: 4px;
							height: 36px;
							padding: 0 12px;
							.el-input__inner {
								color: #374151;
								&::placeholder {
									color: #9CA3AF;
								}
							}
						}
						.is-focus {
							border-color: #165DFF;
						}
					}
				}
			}
			// 搜索按钮盒子
			.search_btn_view {
				display: flex;
				padding: 0;
				// 搜索按钮
				.search_btn {
					border: 0;
					cursor: pointer;
					border-radius: 4px;
					padding: 0 20px;
					color: #fff;
					background: #165DFF;
					width: auto;
					font-size: 14px;
					height: 36px;
					&:hover {
						opacity: 0.9;
					}
				}
			}
		}
		//头部按钮盒子
		.btn_view {
			margin: 0;
			display: flex;
			order: 2;
			// 通用按钮
			:deep(.el-button){
				border: 0;
				border-radius: 4px;
				padding: 0 16px;
				margin: 0 8px 0 0;
				height: 32px;
				font-size: 13px;
			}
			// 新增
			:deep(.el-button--success){
				background: #165DFF;
				color: #fff;
			}
			// 修改
			:deep(.el-button--primary){
				background: #165DFF;
				color: #fff;
				opacity: 0.85;
			}
			// 详情
			:deep(.el-button--info){
				background: #FFFFFF;
				border: 1px solid #E5E7EB;
				color: #374151;
				&:hover {
					border-color: #165DFF;
					color: #165DFF;
				}
			}
			// 删除
			:deep(.el-button--danger){
				background: #F53F3F;
				color: #fff;
			}
		}
	}
	// 表格样式
	:deep(.el-table) {
		border-radius: 8px;
		background: #FFFFFF;
		box-shadow: 0 2px 8px rgba(0,0,0,0.05);
		border: 1px solid #E5E7EB;
		
		th.el-table__cell {
			background: #FAFAFA !important;
			color: #111827;
			font-weight: 600;
			height: 48px;
		}
		
		d.el-table__cell {
			color: #374151;
			height: 54px;
		}
		
		.el-table__row--striped td.el-table__cell {
			background: #FAFAFA;
		}
		
		.el-table__row:hover td.el-table__cell {
			background: #EFF8FF !important;
		}

		.el-button {
			padding: 4px 12px;
			font-size: 12px;
			border-radius: 4px;
		}
		.el-button--info {
			background: #FFFFFF;
			border: 1px solid #E5E7EB;
			color: #374151;
			&:hover {
				border-color: #165DFF;
				color: #165DFF;
			}
		}
	}

	:deep(.el-pagination) {
		margin-top: 24px !important;
		justify-content: center;
		.el-pager li.is-active {
			background-color: #165DFF !important;
		}
		.btn-prev, .btn-next {
			background-color: #FFFFFF !important;
			border: 1px solid #E5E7EB;
			&:hover {
				color: #165DFF;
			}
		}
	}
</style>