<template>
	<div>
		<el-dialog v-model="formVisible" :title="formTitle" width="70%" destroy-on-close>
			<el-form class="formModel_form" ref="formRef" :model="form" label-width="120px" :rules="rules">
				<el-row>
					<el-col :span="24">
						<el-form-item label="试卷" prop="paperid">
							<el-select class="list_sel" :disabled="!isAdd" v-model="form.paperid" placeholder="试卷名称" style="width:100%" @change="paperChange">
								<el-option v-for="(item,index) in paperList" :key="index" :label="item.name" :value="item.id"></el-option>
							</el-select>
						</el-form-item>
					</el-col>
					<el-col :span="24">
						<el-form-item label="试题类型" prop="type">
							<el-select class="list_sel" :disabled="!isAdd" v-model="form.type" placeholder="试题类型" style="width:100%" @change="typeChange">
								<el-option v-for="(item,index) in typeList" :key="index" :label="item" :value="index"></el-option>
							</el-select>
						</el-form-item>
					</el-col>
					<el-col :span="24">
						<el-form-item label="试题题目" prop="questionname">
							<el-input :readonly="!isAdd" v-model="form.questionname" placeholder="试题题目" type="textarea"></el-input>
						</el-form-item>
					</el-col>
					<el-col :span="24">
						<el-form-item label="题目附件">
							<div style="width:100%">
								<div style="font-size:12px;color:#999;margin-bottom:8px">支持上传图片（jpg/png）或PDF，可上传多个</div>
								<div v-if="form.imageList&&form.imageList.length" style="display:flex;flex-wrap:wrap;gap:8px;margin-bottom:8px">
									<div v-for="(img,idx) in form.imageList" :key="idx"
										style="position:relative;border:1px solid #ddd;border-radius:4px;padding:4px 28px 4px 8px;background:#fafafa;font-size:12px;max-width:240px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap">
										<a :href="context.$config.url+img" target="_blank" style="color:#165DFF;text-decoration:none">{{ img.split('/').pop() }}</a>
										<span v-if="isAdd" @click="removeImage(idx)" style="position:absolute;right:6px;top:50%;transform:translateY(-50%);cursor:pointer;color:#f00;font-size:16px">x</span>
									</div>
								</div>
								<el-upload v-if="isAdd" :action="uploadActionUrl" :headers="uploadHeaders" :show-file-list="false" :multiple="true" :on-success="imageUploadSuccess" :on-error="imageUploadError" accept="image/*,.pdf" drag style="width:100%">
									<div style="padding:20px 0;font-size:13px;color:#666">拖拽文件到此处或 <em style="color:#165DFF">点击上传</em>（支持 jpg/png/pdf）</div>
								</el-upload>
							</div>
						</el-form-item>
					</el-col>
					<el-col :span="24" v-if="form.type==0">
						<el-form-item label="选项" prop="optionList">
							<el-input class="list_inpGroup" :readonly="!isAdd" v-for="(item,index) in form.optionList" :key="index" v-model="item.text" style="margin-bottom:6px;" :placeholder="`选项${codeChange(index)}`">
								<template #prepend>{{codeChange(index)}}</template>
								<template #append v-if="isAdd"><el-button :icon="Delete" @click.native="delOption(index)" /></template>
							</el-input>
							<div style="width:100%;text-align:center;padding-top:10px;" v-if="form.optionList.length<4&&isAdd"><el-button circle :icon="Plus" type="success" @click.native="addOption"></el-button></div>
						</el-form-item>
					</el-col>
					<el-col :span="24" v-if="form.type==0">
						<el-form-item label="答案" prop="answer">
							<el-select class="list_sel" :disabled="!isAdd" v-model="form.answer" placeholder="答案">
								<el-option v-for="(item,index) in form.optionList" :label="codeChange(index)" :value="codeChange(index)"></el-option>
							</el-select>
						</el-form-item>
					</el-col>
					<el-col :span="24" v-if="form.type==1">
						<el-form-item label="选项" prop="optionList">
							<el-input class="list_inpGroup" :readonly="!isAdd" v-for="(item,index) in form.optionList" :key="index" v-model="item.text" style="margin-bottom:6px;" :placeholder="`选项${codeChange(index)}`">
								<template #prepend>{{codeChange(index)}}</template>
								<template #append v-if="isAdd"><el-button :icon="Delete" @click.native="delOption(index)" /></template>
							</el-input>
							<div style="width:100%;text-align:center;padding-top:10px;" v-if="form.optionList.length<4&&isAdd"><el-button circle :icon="Plus" type="success" @click.native="addOption"></el-button></div>
						</el-form-item>
					</el-col>
					<el-col :span="24" v-if="form.type==1">
						<el-form-item label="答案" prop="answers">
							<el-select class="list_sel" :disabled="!isAdd" v-model="form.answers" placeholder="答案" multiple @change="answerChange">
								<el-option v-for="(item,index) in form.optionList" :label="codeChange(index)" :value="codeChange(index)"></el-option>
							</el-select>
						</el-form-item>
					</el-col>
					<el-col :span="24" v-if="form.type==2">
						<el-form-item label="答案" prop="answers">
							<el-select class="list_sel" :disabled="!isAdd" v-model="form.answer" placeholder="答案">
								<el-option v-for="(item,index) in form.optionList" :label="codeChange1(index)" :value="codeChange1(index)"></el-option>
							</el-select>
						</el-form-item>
					</el-col>
					<el-col :span="24" v-if="form.type==3">
						<el-form-item label="答案" prop="answers">
							<el-input :readonly="!isAdd" v-model="form.answer" type="textarea" placeholder="答案"></el-input>
						</el-form-item>
					</el-col>
					<el-col :span="24" v-if="form.type==4">
						<el-form-item label="答案" prop="answers">
							<el-input :readonly="!isAdd" v-model="form.answer" type="textarea" placeholder="请输入标准答案（支持文本）" rows="4"></el-input>
						</el-form-item>
					</el-col>
					<el-col :span="24">
						<el-form-item label="解析" prop="analysis">
							<el-input :readonly="!isAdd" v-model="form.analysis" placeholder="解析" type="textarea"></el-input>
						</el-form-item>
					</el-col>
					<el-col :span="24">
						<el-form-item label="分值" prop="score">
							<el-input class="list_inp" :readonly="!isAdd" v-model="form.score" placeholder="分值" type="number"></el-input>
						</el-form-item>
					</el-col>
					<el-col :span="24">
						<el-form-item label="排序" prop="sequence">
							<el-input class="list_inp" :readonly="!isAdd" v-model="form.sequence" placeholder="排序" type="number" :min="1" :max="20"></el-input>
						</el-form-item>
					</el-col>
				</el-row>
			</el-form>
			<template #footer v-if="isAdd">
				<span class="formModel_btn_box">
					<el-button class="formModel_cancel" @click="closeClick">取消</el-button>
					<el-button class="formModel_confirm" type="primary" @click="save">提交</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>
<script setup>
import { ref, getCurrentInstance, nextTick, defineEmits } from 'vue'
import { Delete, Plus } from '@element-plus/icons-vue'
const context = getCurrentInstance()?.appContext.config.globalProperties
const emit = defineEmits(['formModelChange'])
const tableName = 'examquestion'
const uploadActionUrl = context?.$config.name + '/file/upload'
const uploadHeaders = ref({ Token: context?.$toolUtil.storageGet('Token') })
const imageUploadSuccess = (res) => {
  console.log('文件上传响应:', res)
  
  if (!res) {
    console.error('响应为空')
    context?.$toolUtil.message('上传失败：响应为空', 'error')
    return
  }
  
  if (res.code !== 0 && res.code !== '0') {
    console.error('上传失败，错误码:', res.code, '错误信息:', res.msg)
    context?.$toolUtil.message('上传失败：' + (res.msg || '未知错误'), 'error')
    return
  }
  
  let fileName = null
  if (res.data && res.data.file) {
    fileName = res.data.file
  } else if (res.file) {
    fileName = res.file
  }
  
  if (!fileName) {
    console.error('响应中找不到文件名', res)
    context?.$toolUtil.message('上传失败：无法获取文件名', 'error')
    return
  }
  
  if (!form.value.imageList) form.value.imageList = []
  form.value.imageList.push('file/' + fileName)
  form.value.images = form.value.imageList.join(',')
  
  console.log('文件上传成功:', fileName)
  context?.$toolUtil.message('文件上传成功', 'success')
}
const imageUploadError = (err) => {
  console.error('文件上传错误:', err)
  const errMsg = err?.message || err?.response?.data?.msg || '上传失败'
  context?.$toolUtil.message('上传失败：' + errMsg, 'error')
}
const removeImage = (idx) => {
  form.value.imageList.splice(idx, 1)
  form.value.images = form.value.imageList.join(',')
}
const emptyForm = () => ({ papername:'', paperid:'', questionname:'', answer:'', answers:[], analysis:'', score:'', sequence:'', type:0, images:'', imageList:[], optionList:[{text:''},{text:''}] })
const form = ref(emptyForm())
const formVisible = ref(false)
const formTitle = ref('')
const isAdd = ref(true)
const rules = ref({
  paperid: [{required:true,message:'请选择试卷',trigger:'blur'}],
  type: [{required:true,message:'请选择试题类型',trigger:'blur'}],
  questionname: [{required:true,message:'请输入试题题目',trigger:'blur'}],
  answer: [{required:true,message:'请输入答案',trigger:'blur'}],
  analysis: [{required:true,message:'请输入解析',trigger:'blur'}],
  score: [
    {required:true,message:'请输入分值',trigger:'blur'},
    {pattern:/^[0-9]+$/,message:'分值必须是正整数',trigger:'blur'}
  ],
  sequence: [
    {required:true,message:'请输入排序',trigger:'blur'},
    {pattern:/^[0-9]+$/,message:'排序必须是正整数',trigger:'blur'}
  ],
})
const formRef = ref(null)
const id = ref(0)
const resetForm = () => { form.value = emptyForm() }
const getInfo = () => {
  context?.$http({ url:tableName+"/info/"+id.value, method:'get' }).then(res => {
    const d = res.data.data
    d.optionList = d.options ? JSON.parse(d.options) : []
    if (d.type == 1) d.answers = d.answer ? d.answer.split(',') : []
    d.imageList = d.images ? d.images.split(',') : []
    form.value = d
    formVisible.value = true
  })
}
const paperList = ref([])
const getPaperList = () => {
  context?.$http({ url:'exampaper/page', method:'get', params:{page:1,limit:1000} }).then(res => { paperList.value = res.data.data.list })
}
const paperChange = (e) => {
  for (let x in paperList.value) {
    if (paperList.value[x].id == e) { form.value.papername = paperList.value[x].name }
  }
}
const typeList = ref(['单选题', '多选题', '判断题', '填空题', '解答题'])
const typeChange = (e) => {
  const type = Number(e)
  form.value.type = type
  if (type === 3 || type === 4) { 
    form.value.optionList = [] 
  }
  else if (type === 2) { 
    form.value.optionList = [{text:'对'},{text:'错'}] 
  }
  else { 
    form.value.optionList = [{text:''},{text:''}] 
  }
  nextTick(() => { form.value.answer = ''; form.value.answers = [] })
}
const codeChange1 = (type) => ['对','错'][type]
const answerChange = () => { form.value.answer = form.value.answers.join(',') }
const codeChange = (type) => ['A','B','C','D'][type]
const delOption = (index) => { form.value.optionList.splice(index, 1) }
const addOption = () => { form.value.optionList.push({ text:'' }) }
const init = (refid = null, text = null) => {
  formTitle.value = (text || '') + '试题'
  resetForm()
  getPaperList()
  if (refid) {
    id.value = refid
    isAdd.value = (text === '修改' || text === '新增')
    nextTick(() => {
      getInfo()
    })
  } else {
    isAdd.value = true
    nextTick(() => {
      formVisible.value = true
    })
  }
}
defineExpose({ init })
const closeClick = () => { formVisible.value = false }
const save = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      // 数据类型转换
      form.value.score = Number(form.value.score)
      form.value.sequence = Number(form.value.sequence)
      form.value.type = Number(form.value.type)
      
      // 处理选项类型
      if (form.value.type == 0 || form.value.type == 1 || form.value.type == 2) {
        if (form.value.optionList.length > 1) {
          for (let x in form.value.optionList) { form.value.optionList[x].code = codeChange(x) }
          form.value.options = JSON.stringify(form.value.optionList)
        } else { context?.$toolUtil.message('至少输入一个选项', 'error'); return false }
      } else if (form.value.type == 3 || form.value.type == 4) {
        form.value.options = ''
      }
      
      // 多选题处理
      if (form.value.type == 1 && form.value.answers && form.value.answers.length) {
        form.value.answer = form.value.answers.join(',')
      }
      
      // 验证答案
      if (!form.value.answer) { context?.$toolUtil.message('请输入答案', 'error'); return false }
      
      // 提交数据
      context?.$http({
        url: tableName + '/' + (!form.value.id ? 'save' : 'update'),
        method: 'post', 
        data: form.value
      }).then(() => {
        context?.$toolUtil.message('操作成功', 'success', () => {
          formVisible.value = false; emit('formModelChange')
        })
      }).catch(err => {
        console.error('提交失败:', err)
        const errMsg = err.response?.data?.msg || err.message || '请重试'
        context?.$toolUtil.message('提交失败：' + errMsg, 'error')
      })
    }
  })
}
</script>
<style lang="scss" scoped>
.formModel_form {
  border: 1px solid rgba(254,182,203,0.5);
  padding: 40px 30px;
  background: #fff;
}
.formModel_btn_box {
  display:flex; width:100%; justify-content:center; align-items:center;
  .formModel_cancel { border:1px solid #d43f3a;padding:0 24px;margin:0 20px 0 0;color:#fff;background:rgba(217,83,79,0.45);min-width:100px;height:36px; }
  .formModel_confirm { border:1px solid #357ebd;padding:0 24px;margin:0 20px 0 0;color:#fff;background:rgba(66,139,202,0.55);min-width:100px;height:36px; }
}
</style>
