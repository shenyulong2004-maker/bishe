<template>
	<div>
		<el-dialog v-model="formVisible" :title="formTitle" width="80%" destroy-on-close :fullscreen='false'>
			<el-form class="formModel_form" ref="formRef" :model="form" label-width="$template2.back.add.form.base.labelWidth" :rules="rules">
				<el-row>
					<el-col :span="24">
						<el-form-item label="试卷名称" prop="name">
							<el-input class="list_inp" v-model="form.name" placeholder="试卷名称"
								 type="text" :readonly="!isAdd||disabledForm.name?true:false" />
						</el-form-item>
					</el-col>

					<el-col :span="24">
						<el-form-item label="提交方式" prop="tijiaofangshi">
							<el-select
								class="list_sel"
								:disabled="!isAdd||disabledForm.tijiaofangshi?true:false"
								v-model="form.tijiaofangshi" 
								placeholder="请选择提交方式"
								@change="tijiaofangshiChange"
								>
								<el-option v-for="(item,index) in tijiaofangshiLists" :key="index" :label="item"
									:value="item"
									>
								</el-option>
							</el-select>
						</el-form-item>
					</el-col>

					<el-col :span="24" v-if="form.tijiaofangshi !== '线下提交'">
						<el-form-item label="考试时长(分钟)" prop="time">
							<el-input class="list_inp" v-model.number="form.time" placeholder="考试时长(分钟)"
								 type="text" :readonly="!isAdd||disabledForm.time?true:false" />
						</el-form-item>
					</el-col>

					<el-col :span="24" v-if="form.tijiaofangshi==='pdf附件'">
						<el-form-item label="PDF附件">
							<el-alert type="info" :closable="false" show-icon>
								<template #title>PDF附件由学生答题时上传，教师发布试卷时无需上传</template>
							</el-alert>
						</el-form-item>
					</el-col>

					<el-col :span="24" v-if="form.tijiaofangshi==='线下提交'">
						<el-form-item label="线下说明" prop="xianxiashuoming">
							<el-input v-model="form.xianxiashuoming" placeholder="请输入线下提交说明（地点/方式/截止时间等）" type="textarea"
							:readonly="!isAdd||disabledForm.xianxiashuoming?true:false"
							/>
						</el-form-item>
					</el-col>

					<el-col :span="24">
						<el-form-item label="试卷状态" prop="status">
							<el-select
								class="list_sel"
								:disabled="!isAdd||disabledForm.status?true:false"
								v-model="form.status" 
								placeholder="请选择试卷状态"
								>
								<el-option v-for="(item,index) in statusLists" :key="index" :label="item"
									:value="String(index)"
									>
								</el-option>
							</el-select>
						</el-form-item>
					</el-col>
				</el-row>
			</el-form>
			<template #footer v-if="isAdd||type=='logistics'||type=='reply'">
				<span class="formModel_btn_box">
					<el-button class="formModel_cancel" @click="closeClick">取消</el-button>
					<el-button class="formModel_confirm" type="primary" @click="save">
						提交
					</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>
<script setup>
	import {
		ref,
		getCurrentInstance,
		defineEmits
	} from 'vue'
	const context = getCurrentInstance()?.appContext.config.globalProperties;	
	const emit = defineEmits(['formModelChange'])
	const tableName = 'exampaper'
	const formName = '试卷管理'
	const form = ref({})
	const disabledForm = ref({
		name : false,
		tijiaofangshi : false,
		time : false,
		status : false,
		pdfurl : false,
		xianxiashuoming : false,
	})
	const formVisible = ref(false)
	const isAdd = ref(false)
	const formTitle = ref('')

	const validateIntNumber = (rule, value, callback) => {
		if (!value && value !== 0) {
			callback();
		} else if (!context?.$toolUtil.isIntNumer(value)) {
			callback(new Error("请输入整数"));
		} else {
			callback();
		}
	}

	const rules = ref({
		name: [
			{required: true,message: '请输入',trigger: 'blur'}, 
		],
		tijiaofangshi: [
			{required: true,message: '请选择',trigger: 'change'}, 
		],
		time: [
			{ validator: validateIntNumber, trigger: 'blur' },
		],
		status: [],
		pdfurl: [],
		xianxiashuoming: [],
	})

	const formRef = ref(null)
	const id = ref(0)
	const type = ref('')
	const statusLists = ref([])
	const tijiaofangshiLists = ref([])

	const resetForm = () => {
		form.value = {
			name: '',
			tijiaofangshi: '在线答题',
			time: '',
			status: '1',
			pdfurl: '',
			xianxiashuoming: '',
		}
		disabledForm.value = {
			name : false,
			tijiaofangshi : false,
			time : false,
			status : false,
			pdfurl : false,
			xianxiashuoming : false,
		}
	}

	const getInfo = ()=>{
		context?.$http({
			url: `${tableName}/info/${id.value}`,
			method: 'get'
		}).then(res => {
			form.value = {
				...res.data.data,
				status: res.data.data?.status != null ? String(res.data.data.status) : '1'
			}
			formVisible.value = true
		}).catch(err => {
			context?.$toolUtil.message(err?.response?.data?.msg || '获取详情失败','error')
		})
	}
	const crossRow = ref('')
	const crossTable = ref('')
	const crossTips = ref('')
	const crossColumnName = ref('')
	const crossColumnValue = ref('')

	const init=(formId=null,formType='add',formNames='',row=null,table=null,statusColumnName=null,tips=null,statusColumnValue=null)=>{
		resetForm()
		id.value = formId || 0
		type.value = formType
		if(formType == 'add'){
			isAdd.value = true
			formTitle.value = '新增' + formName
			formVisible.value = true
		}else if(formType == 'info'){
			isAdd.value = false
			formTitle.value = '查看' + formName
			getInfo()
		}else if(formType == 'edit'){
			isAdd.value = true
			formTitle.value = '修改' + formName
			getInfo()
		}
		else if(formType == 'cross'){
			isAdd.value = true
			formTitle.value = formNames
			for(let x in row){
				if(x=='name'){
					form.value.name = row[x];
					disabledForm.value.name = true;
					continue;
				}
				if(x=='time'){
					form.value.time = row[x];
					disabledForm.value.time = true;
					continue;
				}
				if(x=='status'){
					form.value.status = String(row[x]);
					disabledForm.value.status = true;
					continue;
				}
			}
			if(row){
				crossRow.value = row
			}
			if(table){
				crossTable.value = table
			}
			if(tips){
				crossTips.value = tips
			}
			if(statusColumnName){
				crossColumnName.value = statusColumnName
			}
			if(statusColumnValue){
				crossColumnValue.value = statusColumnValue
			}
			formVisible.value = true
		}

		statusLists.value = "禁用,启用".split(',')
		tijiaofangshiLists.value = "在线答题,pdf附件,线下提交".split(',')
	}

	defineExpose({
		init
	})

	const closeClick = () => {
		formVisible.value = false
	}

	const tijiaofangshiChange = (val) => {
		if (val !== 'pdf附件') {
			form.value.pdfurl = ''
		}
		if (val !== '线下提交') {
			form.value.xianxiashuoming = ''
		} else {
			form.value.time = 0
		}
	}

	const saveRequest = () => {
		return context?.$http({
			url: `${tableName}/${!form.value.id ? "save" : "update"}`,
			method: 'post', 
			data: form.value 
		})
	}

	const save=()=>{
		var objcross = JSON.parse(JSON.stringify(crossRow.value || {}))
		let crossUserId = ''
		let crossRefId = ''
		let crossOptNum = ''
		if(type.value == 'cross'){
			if(crossColumnName.value!=''){
				if(!crossColumnName.value.startsWith('[')){
					for(let o in objcross){
						if(o == crossColumnName.value){
							objcross[o] = crossColumnValue.value
						}
					}
					changeCrossData(objcross)
				}else{
					crossUserId = context?.$toolUtil.storageGet('userid')
					crossRefId = objcross['id']
					crossOptNum = crossColumnName.value.replace(/\[/,"").replace(/\]/,"")
				}
			}
		}
		formRef.value.validate((valid)=>{
			if(valid){
				if (!form.value.tijiaofangshi) {
					form.value.tijiaofangshi = '在线答题'
				}
				if (form.value.status === '' || form.value.status === null || form.value.status === undefined) {
					form.value.status = '1'
				} else {
					form.value.status = String(form.value.status)
				}
				if (form.value.tijiaofangshi === '在线答题' && (form.value.time === '' || form.value.time === null || form.value.time === undefined)) {
					context?.$toolUtil.message('请输入考试时长(分钟)','error')
					return false
				}
				if (form.value.tijiaofangshi === '线下提交') {
					form.value.time = 0
				}
				if (form.value.tijiaofangshi === '线下提交' && (!form.value.xianxiashuoming || !String(form.value.xianxiashuoming).trim())) {
					context?.$toolUtil.message('请输入线下提交说明','error')
					return false
				}
				if(crossUserId&&crossRefId){
					form.value.crossuserid = crossUserId
					form.value.crossrefid = crossRefId
					let params = {
						page: 1,
						limit: 1000, 
						crossuserid:form.value.crossuserid,
						crossrefid:form.value.crossrefid,
					}
					context?.$http({
						url: `${tableName}/page`,
						method: 'get', 
						params: params 
					}).then(res=>{
						if(res.data.data.total>=crossOptNum){
							context?.$toolUtil.message(`${crossTips.value}`,'error')
							return false
						}else{
							saveRequest().then(()=>{
								context?.$toolUtil.message(`操作成功`,'success',()=>{
									formVisible.value = false
									emit('formModelChange')
								})
							}).catch(err=>{
								context?.$toolUtil.message(err?.response?.data?.msg || '保存失败','error')
							})
						}
					}).catch(err=>{
						context?.$toolUtil.message(err?.response?.data?.msg || '校验失败','error')
					})
				}else{
					saveRequest().then(()=>{
						context?.$toolUtil.message(`操作成功`,'success',()=>{
							formVisible.value = false
							emit('formModelChange')
						})
					}).catch(err=>{
						context?.$toolUtil.message(err?.response?.data?.msg || '保存失败','error')
					})
				}
			}
		})
	}

	const changeCrossData=(row)=>{
		context?.$http({
			url: `${crossTable.value}/update`,
			method: 'post',
			data: row
		}).catch(()=>{})
	}
</script>
<style lang="scss" scoped>
	.formModel_form{
		border: 1px solid rgba(254, 182, 203, 0.5);
		border-radius: 0px;
		padding: 40px 30px;
		margin: 0;
		background: #fff;
		:deep(.el-form-item) {
			margin: 0 150px 20px 0;
			background: none;
			display: flex;
			.el-form-item__label {
			 color: #666;
			 background: none;
			 font-weight: 500;
			 display: block;
			 width: 150px;
			 text-align: right;
			 min-width: 150px;
			}
			.el-form-item__content {
				display: flex;
				width: calc(100% - 120px);
				justify-content: flex-start;
				align-items: center;
				flex-wrap: wrap;
				.list_inp {
					border: 0px solid rgba(255, 255, 255, 0.25);
					border-radius: 0px;
					padding: 0 10px;
					color: #666;
					background: rgba(0, 0, 0, 0.1);
					width: auto;
					line-height: 36px;
					box-sizing: border-box;
					height: 36px;
					.el-input__wrapper{
						border: none;
						box-shadow: none;
						background: none;
						border-radius: 0;
						height: 100%;
						padding: 0;
					}
					.is-focus {
						box-shadow: none !important;
					}
				}
				.list_sel {
					border: 1px solid rgba(255, 255, 255, 0.25);
					border-radius: 0px;
					padding: 0 10px;
					color: #666;
					background: rgba(0, 0, 0, 0.1);
					width: auto;
					line-height: 36px;
					box-sizing: border-box;
					.select-trigger{
						height: 100%;
						.el-input{
							height: 100%;
							.el-input__wrapper{
								border: none;
								box-shadow: none;
								background: none;
								border-radius: 0;
								height: 100%;
								padding: 0;
							}
							.is-focus {
								box-shadow: none !important;
							}
						}
					}
				}
			}
		}
	}
	.formModel_btn_box {
		display: flex;
		width: 100%;
		justify-content: center;
		align-items: center;
		.formModel_cancel {
			border: 1px solid #d43f3a;
			cursor: pointer;
			border-radius: 3px;
			padding: 0 24px;
			margin: 0 20px 0 0;
			outline: none;
			color: #fff;
			background: rgba(217, 83, 79, 0.45);
			width: auto;
			font-size: 14px;
			min-width: 100px;
			height: 36px;
		}
		.formModel_confirm {
			border: 1px solid #357ebd;
			cursor: pointer;
			border-radius: 3px;
			padding: 0 24px;
			margin: 0 20px 0 0;
			outline: none;
			color: #fff;
			background: rgba(66, 139, 202, 0.55);
			width: auto;
			font-size: 14px;
			min-width: 100px;
			height: 36px;
		}
	}
</style>
