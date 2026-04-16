<template>
  <div class="app-contain" :style='{"minHeight":"100vh","padding":"0","margin":"0 auto","overflow":"hidden","borderRadius":"4px","background":"none","width":"1200px","position":"relative"}'>
    <div class="bread_view">
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item class="first_breadcrumb" :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item class="second_breadcrumb" :to="{ path: '/parent/meeting' }">在线家长会</el-breadcrumb-item>
        <el-breadcrumb-item class="second_breadcrumb">发起/预约</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <el-form label-width="130px" style="max-width: 760px; margin-top: 24px;">
      <el-form-item label="会议标题">
        <el-input v-model="form.title" placeholder="请输入标题" />
      </el-form-item>

      <el-form-item label="会议时间">
        <el-select v-model="form.meetingTime" placeholder="请选择时间" style="width:100%">
          <el-option v-for="item in slotOptions" :key="item" :label="item" :value="item" />
        </el-select>
      </el-form-item>

      <el-form-item label="会议室ID">
        <el-input v-model="form.roomId" placeholder="可选，留空自动分配" />
      </el-form-item>

      <template v-if="isParent">
        <el-form-item label="选择教师">
          <el-select v-model="form.teacherId" filterable placeholder="请选择教师" style="width:100%">
            <el-option
              v-for="item in teacherOptions"
              :key="item.id"
              :label="(item.jiaoshixingming || item.jiaoshigonghao) + '（ID:' + item.id + '）'"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </template>

      <template v-else>
        <el-form-item label="参会家长">
          <el-select v-model="form.parentIds" multiple filterable placeholder="请选择家长" style="width:100%">
            <el-option
              v-for="item in parentOptions"
              :key="item.id"
              :label="(item.xingming || item.xuehao) + '（ID:' + item.id + '）'"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </template>

      <el-form-item>
        <el-button type="primary" @click="submit">提交</el-button>
        <el-button @click="goBack">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { computed, getCurrentInstance, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const context = getCurrentInstance()?.appContext.config.globalProperties
const router = useRouter()

const sessionTable = computed(() => context?.$toolUtil.storageGet('sessionTable'))
const isParent = computed(() => sessionTable.value === 'xuesheng')

const slotOptions = ref([
  '2026-04-16 19:00:00',
  '2026-04-18 10:00:00',
  '2026-04-20 19:30:00'
])

const parentOptions = ref([])
const teacherOptions = ref([])

const form = ref({
  title: '',
  meetingTime: '',
  roomId: '',
  teacherId: '',
  parentIds: []
})

const loadTeachers = async () => {
  if (!isParent.value) return
  const res = await context?.$http({
    url: 'jiaoshi/list',
    method: 'get',
    params: { page: 1, limit: 500 }
  })
  teacherOptions.value = res?.data?.data?.list || []
}

const loadParents = async () => {
  if (isParent.value) return
  const res = await context?.$http({
    url: 'xuesheng/list',
    method: 'get',
    params: { page: 1, limit: 500 }
  })
  parentOptions.value = res?.data?.data?.list || []
}

const submit = async () => {
  if (!form.value.meetingTime) {
    context?.$toolUtil.message('请选择会议时间', 'error')
    return
  }

  if (isParent.value) {
    if (!form.value.teacherId) {
      context?.$toolUtil.message('请选择教师', 'error')
      return
    }
    await context?.$http({
      url: 'api/parent/meetings/book',
      method: 'post',
      data: {
        title: form.value.title,
        teacherId: Number(form.value.teacherId),
        meetingTime: form.value.meetingTime,
        roomId: form.value.roomId
      }
    })
    context?.$toolUtil.message('预约成功', 'success')
  } else {
    if (!form.value.parentIds || form.value.parentIds.length === 0) {
      context?.$toolUtil.message('请选择参会家长', 'error')
      return
    }
    await context?.$http({
      url: 'api/parent/meetings/group',
      method: 'post',
      data: {
        title: form.value.title,
        meetingTime: form.value.meetingTime,
        roomId: form.value.roomId,
        parentIds: form.value.parentIds
      }
    })
    context?.$toolUtil.message('群体家长会创建成功', 'success')
  }

  router.push('/parent/meeting')
}

const goBack = () => router.push('/parent/meeting')

onMounted(async () => {
  await loadTeachers()
  await loadParents()
})
</script>
