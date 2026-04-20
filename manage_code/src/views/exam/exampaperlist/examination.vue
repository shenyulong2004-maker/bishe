<template>
	<div class="exam_page">
		<div class="exam_top">
			<div class="examTopLeft">
				{{examDetail.name}} (共{{questionList.length}}题)
			</div>
			<div class="examTopCenter">
				剩余时间：{{times}}
			</div>
			<div class="examTopRight">
				<el-button type="danger" @click="leaveExam">退出考试</el-button>
			</div>
		</div>
		<div class="exam_top_kong"></div>
		<el-card v-if="endType" class="score_view">
			<div class="score_item">
				考试成绩：<span class="score_num">{{scoreChange()}}</span>（{{ getGradeLabel(scoreChange(), totalScore) }}）
			</div>
			<div class="btn_view">
				<el-button type="danger" @click="endExam">结束考试</el-button>
			</div>
		</el-card>
		<el-card class="question_list" v-if="questionList.length&&!endType">
			<div class="question">
				<div class="questionTitle">
					{{currentIndex + 1}}、{{questionList[currentIndex].questionname}}
					<el-tag type="success" v-if="questionList[currentIndex].type==0">单选题</el-tag>
					<el-tag type="warning" v-if="questionList[currentIndex].type==1">多选题</el-tag>
					<el-tag type="danger" v-if="questionList[currentIndex].type==2">判断题</el-tag>
					<el-tag type="info" v-if="questionList[currentIndex].type==3">填空题</el-tag>
				</div>
				<div class="question_images" v-if="questionList[currentIndex].images">
					<div v-for="(img, imgIdx) in questionList[currentIndex].images.split(',')"
						:key="imgIdx" class="question_image_item">
						<img v-if="img.match(/\.(jpg|jpeg|png|gif|webp|bmp)$/i)"
							:src="baseUrl + img"
							alt="题目图片"
							style="max-width:100%;border-radius:4px;margin:8px 0;display:block;cursor:pointer;"
							@click="previewImg(baseUrl + img)"
						/>
						<div v-else-if="img.match(/\.pdf$/i)" class="pdf_attachment">
							<el-button type="primary" size="default" @click="previewPdf(baseUrl + img)">
								📄 查看PDF附件
							</el-button>
							<el-button type="success" size="default" @click="downloadFile(baseUrl + img)">
								⬇️ 下载PDF
							</el-button>
						</div>
					</div>
				</div>
				<div class="optionList"
					v-if="questionList[currentIndex].type==0||questionList[currentIndex].type==1">
					<div class="option" v-for="(items,indexs) in questionList[currentIndex].optionList"
						:key="indexs">
						{{codeChange(indexs)}}：{{items.text}}
					</div>
				</div>
				<div class="answer" v-if="!submitType">
					<el-select
						v-if="questionList[currentIndex].type==2||questionList[currentIndex].type==0"
						v-model="questionList[currentIndex].myanswer" placeholder="输入答案">
						<el-option v-if="questionList[currentIndex].type==0"
							v-for="(items,indexs) in questionList[currentIndex].optionList"
							:label="codeChange(indexs)" :value="codeChange(indexs)"></el-option>
						<el-option v-if="questionList[currentIndex].type==2"
							v-for="(items,indexs) in questionList[currentIndex].optionList"
							:label="codeChange1(indexs)" :value="codeChange1(indexs)"></el-option>
					</el-select>
					<el-select v-else-if="questionList[currentIndex].type==1"
						v-model="questionList[currentIndex].myanswers" placeholder="输入答案" multiple
						@change="(e)=>type1Change(e)">
						<el-option v-for="(items,indexs) in questionList[currentIndex].optionList"
							:label="codeChange(indexs)" :value="codeChange(indexs)"></el-option>
					</el-select>
					<el-input class="answer_inp" v-model="questionList[currentIndex].myanswer" placeholder="输入答案" v-else></el-input>
				</div>
				<div class="answer" v-if="submitType">
					<el-tag type="success"
						v-if="questionList[currentIndex].answer==questionList[currentIndex].myanswer"
						size="large">答案正确</el-tag>
					<el-tag type="danger"
						v-if="questionList[currentIndex].answer!=questionList[currentIndex].myanswer"
						size="large">答案错误</el-tag>
					我的答案：{{questionList[currentIndex].myanswer}}
				</div>
				<el-collapse v-if="submitType" model-value="1" class="analysis_view">
					<el-collapse-item title="查看解析" name="1">
						<div class="right_view">正确答案：{{questionList[currentIndex].answer}}</div>
						<div class="analysis">解析：{{questionList[currentIndex].analysis}}</div>
					</el-collapse-item>
				</el-collapse>
			</div>
			<div class="btn_view">
				<el-button type="success" v-if="!submitType" @click="saveClick">提交</el-button>
				<el-button type="warning" v-if="submitType" @click="nextClick">下一题</el-button>
			</div>
		</el-card>
	</div>
</template>

<script setup>
	import {
		ref,
		nextTick,
		getCurrentInstance,
		computed
	} from 'vue';
	import {
		useRoute,
		useRouter
	} from 'vue-router'
	import {
		ElMessageBox
	} from 'element-plus'
	const route = useRoute()
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	const baseUrl = context?.$config.url || ''
	const id = ref(0)
	const init = () => {
		if (route.query.id) {
			id.value = route.query.id
			getSession()
			getExamination()
			getQuestionList()
		}
	}
	const user = ref({})
	const getSession = () => {
		context?.$http({
			url: `${context?.$toolUtil.storageGet('sessionTable')}/session`,
			method: 'get'
		}).then(res => {
			user.value = res.data.data
		})
	}
	const examDetail = ref({})
	const examTime = ref(0)
	const timeInter = ref(null)
	const getExamination = () => {
		context?.$http({
			url: `exampaper/info/${id.value}`,
			method: 'get'
		}).then(res => {
			examDetail.value = res.data.data
			examTime.value = Number(res.data.data.time * 60)
			timeInter.value = setInterval(() => {
				examTime.value--
			}, 1000)
		})
	}
	const times = computed(() => {
		var time = examTime.value
		if (null != time && "" != time) {
			if (time > 60 && time < 60 * 60) {
				time = parseInt(time / 60.0) + "分钟" + parseInt((parseFloat(time / 60.0) - parseInt(time / 60.0)) * 60) + "秒";
			} else if (time >= 60 * 60 && time < 60 * 60 * 24) {
				time = parseInt(time / 3600.0) + "小时" + parseInt((parseFloat(time / 3600.0) - parseInt(time / 3600.0)) * 60) + "分钟" + parseInt((parseFloat((parseFloat(time / 3600.0) - parseInt(time / 3600.0)) * 60) - parseInt((parseFloat(time / 3600.0) - parseInt(time / 3600.0)) * 60)) * 60) + "秒";
			} else if (time >= 60 * 60 * 24) {
				time = parseInt(time / 3600.0 / 24) + "天" + parseInt((parseFloat(time / 3600.0 / 24) - parseInt(time / 3600.0 / 24)) * 24) + "小时" + parseInt((parseFloat(time / 3600.0) - parseInt(time / 3600.0)) * 60) + "分钟" + parseInt((parseFloat((parseFloat(time / 3600.0) - parseInt(time / 3600.0)) * 60) - parseInt((parseFloat(time / 3600.0) - parseInt(time / 3600.0)) * 60)) * 60) + "秒";
			} else {
				if (parseInt(time) <= 0) {
					time = "0秒";
				} else {
					time = parseInt(time) + "秒";
				}
			}
		}
		return time;
	})
	const submitType = ref(false)
	const endType = ref(false)
	const questionList = ref([])
	const getQuestionList = () => {
		context?.$http({
			url: 'examquestion/page',
			method: 'get',
			params: {
				page: 1,
				limit: 1000,
				sort: 'sequence',
				paperid: id.value
			}
		}).then(res => {
			for (let x in res.data.data.list) {
				if (res.data.data.list[x].options) {
					res.data.data.list[x].optionList = JSON.parse(res.data.data.list[x].options)
				} else {
					res.data.data.list[x].optionList = []
				}
			}
			questionList.value = res.data.data.list
		})
	}
	const codeChange = (type) => {
		let arr = ['A', 'B', 'C', 'D']
		return arr[type]
	}
	const codeChange1 = (type) => {
		let arr = ['对', '错']
		return arr[type]
	}
	const type1Change = (e) => {
		const sorted = [...e].sort()
		questionList.value[currentIndex.value].myanswers = sorted
		questionList.value[currentIndex.value].myanswer = sorted.join(',')
	}
	const previewImg = (url) => {
		window.open(url)
	}
	const previewPdf = (url) => {
		window.open(url, '_blank')
	}
	const downloadFile = (url) => {
		const link = document.createElement('a')
		link.href = url
		link.download = ''
		document.body.appendChild(link)
		link.click()
		document.body.removeChild(link)
	}
	const leaveExam = () => {
		ElMessageBox.confirm(`是否退出考试？`, '提示', {
			confirmButtonText: '是',
			cancelButtonText: '否',
			type: 'warning',
		}).then(() => {
			history.back()
		})
	}
	const finishSyncing = ref(false)
	const finishExamSync = () => {
		if (finishSyncing.value || !examDetail.value?.id) return
		finishSyncing.value = true
		context?.$http({
			url: 'examrecord/finishExam',
			method: 'post',
			params: { paperid: examDetail.value.id }
		}).finally(() => {
			finishSyncing.value = false
		})
	}
	const endExam = () => {
		history.back()
	}
	const totalScore = computed(() => {
		return questionList.value.reduce((sum, item) => sum + (Number(item.score) || 0), 0)
	})
	const scoreChange = () => {
		let score = 0
		for (let x in questionList.value) {
			if (questionList.value[x].myanswer == questionList.value[x].answer) {
				questionList.value[x].myscore = questionList.value[x].score
			} else {
				questionList.value[x].myscore = 0
			}
			score += questionList.value[x].myscore
		}
		return score
	}
	const getGradeLabel = (score, fullScore = 100) => {
		const s = Number(score)
		const total = Number(fullScore)
		if (!Number.isFinite(s) || !Number.isFinite(total) || total <= 0) return ''
		const percent = (s / total) * 100
		if (percent >= 90) return '优'
		if (percent >= 80) return '良'
		if (percent >= 70) return '中'
		if (percent >= 60) return '及格'
		return '不及格'
	}
	const saverecord = (row) => {
		let arr = {
			userid: user.value.id,
			username: user.value.xingming,
			paperid: examDetail.value.id,
			papername: examDetail.value.name,
			questionid: parseInt(row.id),
			questionname: row.questionname,
			options: row.options ? row.options : '[]',
			score: row.score,
			answer: row.answer,
			analysis: row.analysis,
			myanswer: row.myanswer,
			myscore: row.myanswer == row.answer ? row.score : 0,
		}
		context?.$http({
			url: 'examrecord/save',
			method: 'POST',
			data: arr
		}).then(res => {})
	}
	const currentIndex = ref(0)
	const saveClick = () => {
		if (!questionList.value[currentIndex.value].myanswer) {
			context?.$toolUtil.message('请输入答案', 'error')
			return false
		}
		saverecord(questionList.value[currentIndex.value])
		submitType.value = true
	}
	const nextClick = () => {
		if (currentIndex.value == questionList.value.length - 1) {
			clearInterval(timeInter.value)
			finishExamSync()
			endType.value = true
		} else {
			submitType.value = false
			currentIndex.value++
		}
	}
	init()
</script>

<style lang="scss" scoped>
	.exam_page {
		min-height: 100vh;
		background: url(http://clfile.zggen.cn/20231120/3003369554f34c828841c05292b6461a.jpg) fixed no-repeat center top / cover, #ac699c;
	}

	.exam_top {
		padding: 0 8%;
		z-index: 9;
		color: #fff;
		display: flex;
		box-sizing: border-box;
		top: 0;
		left: 0;
		background: url(http://clfile.zggen.cn/20231120/3003369554f34c828841c05292b6461a.jpg) fixed no-repeat left top / cover,#ac699c;
		width: 100%;
		justify-content: space-between;
		align-items: center;
		position: fixed;
		height: 60px;
		.examTopLeft {
			width: calc(100% / 3);
			font-size: 16px;
			text-align: left;
		}
		.examTopCenter {
			width: calc(100% / 3);
			font-size: 16px;
			text-align: center;
		}
		.examTopRight {
			width: calc(100% / 3);
			text-align: right;
			:deep(.el-button--danger) {
				border: 0;
				cursor: pointer;
				border-radius: 3px;
				padding: 0 10px;
				margin: 0 10px 0 0;
				color: #fff;
				background: rgba(255,255,255,.1);
				width: auto;
				font-size: 14px;
				height: 32px;
			}
			:deep(.el-button--danger:hover) {
				color: #ff0;
			}
		}
	}

	.question_list {
		border: 0px solid #19a97b;
		border-radius: 0px;
		box-shadow: none;
		padding: 60px 7% 80px;
		margin: 0 auto;
		background: transparent;
		width: 100%;
		min-height: calc(100vh - 60px);
		text-align: left;
	}

	.score_view {
		border: 0px solid #000;
		border-radius: 0;
		box-shadow: none;
		padding: 0px 0;
		margin: 0 auto;
		background: rgba(0, 0, 0, 0.9);
		bottom: 0;
		width: 100%;
		position: fixed;
		.score_item {
			padding: 0px 0;
			color: #ccc;
			font-size: 22px;
			text-align: center;
		}
		.score_num {
			color: #f00;
			font-weight: 600;
			font-size: 24px;
		}
	}
</style>
