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
					<!-- 单选题 -->
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
					<!-- 多选题 -->
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
					<!-- 判断题 -->
					<el-col :span="24" v-if="form.type==2">
						<el-form-item label="答案" prop="answers">
							<el-select class="list_sel" :disabled="!isAdd" v-model="form.answer" placeholder="答案">
								<el-option v-for="(item,index) in form.optionList" :label="codeChange1(index)" :value="codeChange1(index)"></el-option>
							</el-select>
						</el-form-item>
					</el-col>
					<!-- 填空题 -->
					<el-col :span="24" v-if="form.type==3">
						<el-form-item label="答案" prop="answers">
							<el-input :readonly="!isAdd" v-model="form.answer" type="textarea" placeholder="答案"></el-input>
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
