
<template>
	<div>
		<div class="app-contain">
			<div class="list_search_view">
				<el-form :model="searchQuery" class="search_form" >
					<div class="search_view">
						<div class="search_label">
							学号：
						</div>
						<div class="search_box">
							<el-input class="search_inp" v-model="searchQuery.xuehao" placeholder="学号"
								clearable>
							</el-input>
						</div>
					</div>
					<div class="search_btn_view">
						<el-button class="search_btn" type="primary" @click="searchClick()" size="small">搜索</el-button>
					</div>
				</el-form>
				<br>
				<div class="btn_view">
					<el-button type="success" @click="addClick" v-if="btnAuth('xuesheng','新增')">新增</el-button>
					<el-button  v-if=" btnAuth('xuesheng','查看')" type="info"  :disabled="selRows.length==1?false:true" @click="infoClick(null)">详情</el-button>
					<el-button type="primary" :disabled="selRows.length==1?false:true" @click="editClick" v-if=" btnAuth('xuesheng','修改')">修改</el-button>
					<el-button type="danger" :disabled="selRows.length?false:true" @click="delClick(null)"  v-if="btnAuth('xuesheng','删除')">删除</el-button>
				</div>
			</div>
			<br>
			<el-table
				v-loading="listLoading"
				border 
				:stripe='true'
				@selection-change="handleSelectionChange" 
				ref="table"
				v-if="btnAuth('xuesheng','查看')"
				:data="list"
				@row-click="listChange">
				<el-table-column :resizable='true' align="left" header-align="left" type="selection" width="55" />
				<el-table-column label="序号" width="70" :resizable='true' :sortable='true' align="left" header-align="left">
					<template #default="scope">{{ scope.$index + 1}}</template>
				</el-table-column>
				<el-table-column
					 :resizable='true' 
					 :sortable='true' 
					 align="left" 
					 header-align="left"
					label="学号">
					<template #default="scope">
						{{scope.row.xuehao}}
					</template>
				</el-table-column>
				<el-table-column
					 :resizable='true' 
					 :sortable='true' 
					 align="left" 
					 header-align="left"
					label="姓名">
					<template #default="scope">
						{{scope.row.xingming}}
					</template>
				</el-table-column>
				<el-table-column label="头像" width="120" :resizable='true' :sortable='true' align="left" header-align="left">
					<template #default="scope">
						<div v-if="scope.row.touxiang">
							<el-image preview-teleported
								:preview-src-list="[getImageSrc(scope.row.touxiang)]"
								:src="getImageSrc(scope.row.touxiang)" style="width:100px;height:100px">
							</el-image>
						</div>
						<div v-else>无图片</div>
					</template>
				</el-table-column>
				<el-table-column
					 :resizable='true' 
					 :sortable='true' 
					 align="left" 
					 header-align="left"
					label="性别">
					<template #default="scope">
						{{scope.row.xingbie}}
					</template>
				</el-table-column>
				<el-table-column
					 :resizable='true' 
					 :sortable='true' 
					 align="left" 
					 header-align="left"
					label="联系电话">
					<template #default="scope">
						{{scope.row.lianxidianhua}}
					</template>
				</el-table-column>
				<el-table-column
					 :resizable='true' 
					 :sortable='true' 
					 align="left" 
					 header-align="left"
					label="班级">
					<template #default="scope">
						{{scope.row.banji}}
					</template>
				</el-table-column>
				<el-table-column
					 :resizable='true' 
					 :sortable='true' 
					 align="left" 
					 header-align="left"
					label="操作">
					<template #default="scope">
						<el-button type="info" v-if=" btnAuth('xuesheng','查看')" @click="infoClick(scope.row.id)">详情</el-button>
						<el-button type="warning" v-if=" btnAuth('xuesheng','修改')" @click="resetPassClick(scope.row)">重置密码</el-button>
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
	import axios from 'axios'
	import {
		reactive,
		ref,
		getCurrentInstance,
		nextTick,
		onMounted,
		watch,
	} from 'vue'
	import {
		useRoute,
		useRouter
	} from 'vue-router'
	import {
		ElMessageBox
	} from 'element-plus'
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	import formModel from './formModel.vue'
	
	//基础信息
	const tableName = 'xuesheng'
	const formName = '学生'
	const route = useRoute()
	//基础信息
	onMounted(()=>{
	})
	//列表数据
	const list = ref(null)
	const table = ref(null)
	const listQuery = ref({
		page: 1,
		limit: 20,
		sort: 'id',
		order: 'desc'
	})
	const searchQuery = ref({})
	const selRows = ref([])
	const listLoading = ref(false)
	const getImageSrc = (raw) => {
		if (!raw) return ''
		let first = String(raw).split(',')[0] || ''
		first = first.trim().replace(/\\/g, '/')
		if (!first) return ''
		if (/^https?:\/\//i.test(first)) return first
		const base = String(context?.$config?.url || '').replace(/\/$/, '')
		if (first.startsWith('file/')) {
			const fileName = first.replace(/^file\//, '')
			return `${base}/file/download?fileName=${encodeURIComponent(fileName)}`
		}
		const path = first.replace(/^\//, '')
		return `${base}/${path}`
	}
	const listChange = (row) =>{
		nextTick(()=>{
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
		if(searchQuery.value.xuehao&&searchQuery.value.xuehao!=''){
			params['xuehao'] = '%' + searchQuery.value.xuehao + '%'
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
	const resetPassClick = (row) => {
		if (!row || !row.xuehao) return
		ElMessageBox.prompt('请输入新密码（留空则随机生成）', '重置密码', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning',
			inputPlaceholder: '至少8位，包含字母和数字（留空随机）',
			inputValidator: (val) => {
				if (!val || !String(val).trim()) return true
				const v = String(val).trim()
				if (v.length < 8 || v.length > 64) return '密码长度需为8-64位'
				if (!/[A-Za-z]/.test(v) || !/[0-9]/.test(v)) return '密码需包含字母和数字'
				return true
			}
		}).then(({ value }) => {
			const nextPassword = value ? String(value).trim() : ''
			context?.$http({
				url: `${tableName}/resetPass`,
				method: 'post',
				params: {
					username: row.xuehao,
					password: nextPassword
				}
			}).then(res => {
				const data = res?.data?.data
				const show = data ? `已重置，新密码：${data}` : '已重置'
				context?.$toolUtil.message(show, 'success')
			})
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
	const btnAuth = (e,a)=>{
		return context?.$toolUtil.isAuth(e,a)
	}
	//搜索
	const searchClick = () => {
		listQuery.value.page = 1
		getList()
	}
	//表单
	const formRef = ref(null)
	const formModelChange=()=>{
		searchClick()
	}
	const addClick = ()=>{
		formRef.value.init()
	}
	const editClick = ()=>{
		if(selRows.value.length){
			formRef.value.init(selRows.value[0].id,'edit')
		}
	}
	
	const infoClick = (id=null)=>{
		if(id){
			formRef.value.init(id,'info')
		}
		else if(selRows.value.length){
			formRef.value.init(selRows.value[0].id,'info')
		}
	}
	// 表单
	// 预览文件
	const preClick = (file) =>{
		if(!file){
			context?.$toolUtil.message('文件不存在','error')
		}
		window.open(context?.$config.url + file)
		// const a = document.createElement('a');
		// a.style.display = 'none';
		// a.setAttribute('target', '_blank');
		// file && a.setAttribute('download', file);
		// a.href = context?.$config.url + file;
		// document.body.appendChild(a);
		// a.click();
		// document.body.removeChild(a);
	}
	// 下载文件
	const download = (file) => {
		if(!file){
			context?.$toolUtil.message('文件不存在','error')
		}
		let arr = file.replace(new RegExp('file/', "g"), "")
		axios.get((location.href.split(context?.$config.name).length>1 ? location.href.split(context?.$config.name)[0] :'') + context?.$config.name + '/file/download?fileName=' + arr, {
			headers: {
				token: context?.$toolUtil.storageGet('Token')
			},
			responseType: "blob"
		}).then(({
			data
		}) => {
			const binaryData = [];
			binaryData.push(data);
			const objectUrl = window.URL.createObjectURL(new Blob(binaryData, {
				type: 'application/pdf;chartset=UTF-8'
			}))
			const a = document.createElement('a')
			a.href = objectUrl
			a.download = arr
			// a.click()
			// 下面这个写法兼容火狐
			a.dispatchEvent(new MouseEvent('click', {
				bubbles: true,
				cancelable: true,
				view: window
			}))
			window.URL.revokeObjectURL(data)
		})
	}


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
		
		td.el-table__cell {
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
