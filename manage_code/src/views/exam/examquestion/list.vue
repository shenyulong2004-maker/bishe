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
								<el-input class="search_inp" v-model="searchQuery.papername" placeholder="试卷名称"
									 clearable>
								</el-input>
							</div>
						</div>
						<div class="search_view">
							<div class="search_label">
								试题：
							</div>
							<div class="search_box">
								<el-input class="search_inp" v-model="searchQuery.questionname" placeholder="试题名称"
									 clearable>
								</el-input>
							</div>
						</div>
						<div class="search_btn_view">
							<el-button class="search_btn" type="primary" @click="searchClick()">搜索</el-button>
						</div>
					</el-form>
					<br>
					<div class="btn_view">
						<el-button type="success" @click="addClick"
							v-if="btnAuth('examquestion','新增')">新增</el-button>
						<el-button type="danger" :disabled="selRows.length?false:true"
							@click="delClick(null)" v-if="btnAuth('examquestion','删除')">删除</el-button>
						<el-button type="warning" @click="exportClick" v-if="btnAuth('examquestion','导出')">导出</el-button>
						<el-button type="primary" @click="printClick" v-if="btnAuth('examquestion','打印')">打印</el-button>
					</div>
				</div>
			</div>
			<br>
			<el-table v-loading="listLoading" border :stripe='true' @selection-change="handleSelectionChange" ref="table"
				v-if="btnAuth('exampaper','查看')" :data="list" @row-click="listChange">
				<el-table-column :resizable='true' align="left" header-align="left" type="selection" width="55" />
				<el-table-column label="序号" width="120" :resizable='true' :sortable='true' align="left" header-align="left">
					<template #default="scope">{{ scope.$index + 1}}</template>
				</el-table-column>
				<el-table-column label="试卷名称" :resizable='true' :sortable='true' align="left" header-align="left">
					<template #default="scope">
						{{scope.row.papername}}
					</template>
				</el-table-column>
				<el-table-column label="试题名称" :resizable='true' :sortable='true' align="left" header-align="left">
					<template #default="scope">
						{{scope.row.questionname}}
					</template>
				</el-table-column>
				<el-table-column label="分值" :resizable='true' :sortable='true' align="left" header-align="left">
					<template #default="scope">
						{{scope.row.score}}
					</template>
				</el-table-column>
				<el-table-column label="答案" :resizable='true' :sortable='true' align="left" header-align="left">
					<template #default="scope">
						{{scope.row.answer}}
					</template>
				</el-table-column>
				<el-table-column label="试题类型" :resizable='true' :sortable='true' align="left" header-align="left">
					<template #default="scope">
						<el-tag type="success" v-if="scope.row.type==0">单选题</el-tag>
						<el-tag type="warning" v-if="scope.row.type==1">多选题</el-tag>
						<el-tag type="danger" v-if="scope.row.type==2">判断题</el-tag>
						<el-tag type="info" v-if="scope.row.type==3">填空题</el-tag>
						<el-tag type="primary" v-if="scope.row.type==4">解答题</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="操作" :resizable='true' :sortable='true' align="left" header-align="left">
					<template #default="scope">
						<el-button :type="'primary'" @click="editClick(scope.row.id)" size="small"
							v-if="btnAuth('examquestion','修改')">修改</el-button>
						<el-button :type="'danger'" @click="delClick(scope.row.id)" size="small"
							v-if="btnAuth('examquestion','删除')">删除</el-button>
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
		<formModel ref="formRef" @formModelChange="formModelChange"></formModel>
	</div>
</template>

<script setup>
	import {
		ref,
		nextTick,
		getCurrentInstance
	} from 'vue';
	import {
		useRoute,
		useRouter
	} from 'vue-router'
	import {
		ElMessageBox
	} from 'element-plus'
	import formModel from './formModel.vue'
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	//基础信息
	const tableName = 'examquestion'
	const formName = '试题'
	const route = useRoute()
	//基础信息
	const listLoading = ref(false)
	const list = ref([])
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
	const table = ref(null)
	const listChange = (row) => {
		nextTick(() => {
			table.value.clearSelection()
			table.value.toggleRowSelection(row)
		})
	}
	const listQuery = ref({
		page: 1,
		limit: 20,
		sort: 'id',
		order: 'desc'
	})
	const searchQuery = ref({})
	const selRows = ref([])
	//多选
	const handleSelectionChange = (e) => {
		selRows.value = e
	}
	//列表
	const getList = () => {
		listLoading.value = true
		let params = JSON.parse(JSON.stringify(listQuery.value))
		params['sort'] = 'id'
		params['order'] = 'desc'
		if (searchQuery.value.papername && searchQuery.value.papername != '') {
			params['papername'] = '%' + searchQuery.value.papername + '%'
		}
		if (searchQuery.value.questionname && searchQuery.value.questionname != '') {
			params['questionname'] = '%' + searchQuery.value.questionname + '%'
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
	const formRef = ref(null)
	const formModelChange=()=>{
		searchClick()
	}
	const addClick = ()=>{
		formRef.value.init('','新增')
	}
	const editClick = (id=null)=>{
		console.log(id)
		if(id){
			formRef.value.init(id,'修改')
		}
	}
	//导出数据
	const exportClick = () => {
		import('@/utils/Export2Excel').then(excel => {
			const tHeader = [
				"试卷名称",
				"试题名称",
				"试题类型",
				"选项",
				"分值",
				"正确答案",
				"解析",
			]
			const filterVal = [
				"papername",
				"questionname",
				"typeName",
				"options",
				"score",
				"answer",
				"analysis",
			]
			excel.export_json_to_excel2(tHeader, list.value, filterVal, '试题')
		})
	}
	//打印
	const printClick = () => {
		let data = []
		for (let x in list.value) {
			data.push({
				papername: list.value[x].papername,
				questionname: list.value[x].questionname,
				typeName: list.value[x].typeName,
				options: list.value[x].options,
				score: list.value[x].score,
				answer: list.value[x].answer,
				analysis: list.value[x].analysis,
			})
		}
		printJS({
			printable: data,
			properties: [{
					field: 'papername',
					displayName: '试卷名称',
					columnSize: 1
				},
				{
					field: 'questionname',
					displayName: '试题名称',
					columnSize: 1
				},
				{
					field: 'typeName',
					displayName: '试题类型',
					columnSize: 1
				},
				{
					field: 'options',
					displayName: '选项',
					columnSize: 1
				},
				{
					field: 'score',
					displayName: '分值',
					columnSize: 1
				},
				{
					field: 'answer',
					displayName: '正确答案',
					columnSize: 1
				},
				{
					field: 'analysis',
					displayName: '解析',
					columnSize: 1
				},
			],
			type: 'json',
			header: '试题',
			gridStyle: 'border: 1px solid #3971A5; text-align: center; padding: 6px 10px',
			gridHeaderStyle: 'color: #000; border: 1px solid #3971A5; padding: 6px 10px'
		})
	}
	//删
	const delClick = (id) => {
		let ids = ref([])
		if (id) {
			ids.value = [id]
		} else {
			if (selRows.value.length) {
				for (let x in selRows.value) {
					ids.value.push(selRows.value[x].id)
				}
			} else {
				return false
			}
		}
		ElMessageBox.confirm(`是否删除选中${formName}`, '提示', {
			confirmButtonText: '是',
			cancelButtonText: '否',
			type: 'warning',
		}).then(() => {
			context?.$http({
				url: `${tableName}/delete`,
				method: 'post',
				data: ids.value
			}).then(res => {
				context?.$toolUtil.message('删除成功', 'success',()=>{
					getList()
				})
			})
		})
	}
	const init = () => {
		getList()
	}
	init()
</script>

<style lang="scss" scoped>
	/* 总盒子 */
	.app-contain {
		padding: 20px;
		background: transparent;
		min-height: calc(100vh - 64px);
	}

	/* 搜索和按钮盒子 */
	.list_search_view {
		background: #EFF8FF;
		padding: 20px;
		border-radius: 8px;
		border: 1px solid #E5E7EB;
		box-shadow: 0 2px 8px rgba(0,0,0,0.05);
		margin-bottom: 20px;
		display: flex;
		flex-direction: column;
		gap: 20px;

		/* 搜索表单 */
		.search_form {
			display: flex;
			flex-wrap: wrap;
			align-items: center;
			gap: 15px;

			.search_view {
				display: flex;
				align-items: center;

				.search_label {
					font-size: 14px;
					color: #374151;
					margin-right: 10px;
					white-space: nowrap;
				}

				.search_box {
					width: 200px;

					:deep(.el-input__wrapper) {
						background-color: #ffffff;
						box-shadow: 0 0 0 1px #D1D5DB inset;
						&:hover {
							box-shadow: 0 0 0 1px #165DFF inset;
						}
						&.is-focus {
							box-shadow: 0 0 0 1px #165DFF inset !important;
						}
					}
				}
			}

			.search_btn_view {
				.search_btn {
					background: #165DFF;
					border-color: #165DFF;
					padding: 0 24px;
					height: 36px;
					&:hover {
						background: #4080FF;
						border-color: #4080FF;
					}
				}
			}
		}

		/* 顶部操作按钮 */
		.btn_view {
			display: flex;
			gap: 10px;

			:deep(.el-button) {
				padding: 0 20px;
				height: 36px;
				border-radius: 4px;
			}
			
			:deep(.el-button--success) {
				background: #00B42A;
				border-color: #00B42A;
				&:hover {
					background: #23C343;
					border-color: #23C343;
				}
			}
			
			:deep(.el-button--danger) {
				background: #F53F3F;
				border-color: #F53F3F;
				&:hover {
					background: #F76560;
					border-color: #F76560;
				}
			}
			
			:deep(.el-button--warning) {
				background: #FF7D00;
				border-color: #FF7D00;
				&:hover {
					background: #FF9A2E;
					border-color: #FF9A2E;
				}
			}
			
			:deep(.el-button--primary) {
				background: #165DFF;
				border-color: #165DFF;
				&:hover {
					background: #4080FF;
					border-color: #4080FF;
				}
			}
		}
	}

	/* 表格样式 */
	:deep(.el-table) {
		border-radius: 8px;
		border: 1px solid #E5E7EB;
		background: #ffffff;

		th.el-table__cell {
			background: #FAFAFA !important;
			color: #111827;
			font-weight: 600;
			height: 50px;
		}

		td.el-table__cell {
			color: #374151;
			height: 50px;
		}

		.el-table__row--striped td {
			background: #FAFAFA !important;
		}

		&:hover {
			.el-table__body-wrapper tr:hover td {
				background-color: #F2F3F5 !important;
			}
		}

		/* 操作按钮 */
		.el-button--primary {
			color: #165DFF;
			background: none;
			border: none;
			padding: 0 8px;
			&:hover {
				color: #4080FF;
			}
		}

		.el-button--danger {
			color: #F53F3F;
			background: none;
			border: none;
			padding: 0 8px;
			&:hover {
				color: #F76560;
			}
		}
	}

	/* 分页器 */
	.el-pagination {
		margin-top: 24px;
		justify-content: center;
		
		:deep(.el-pager li) {
			background: #ffffff;
			border: 1px solid #D1D5DB;
			color: #374151;
			margin: 0 4px;
			border-radius: 4px;
			font-weight: 400;

			&:hover {
				color: #165DFF;
				border-color: #165DFF;
			}

			&.is-active {
				background: #165DFF !important;
				border-color: #165DFF;
				color: #ffffff;
			}
		}

		:deep(.btn-prev), :deep(.btn-next) {
			background: #ffffff;
			border: 1px solid #D1D5DB;
			border-radius: 4px;
			&:hover {
				color: #165DFF;
				border-color: #165DFF;
			}
			&:disabled {
				background: #F3F4F6;
				color: #9CA3AF;
			}
		}
	}
</style>