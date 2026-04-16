<template>
  <div class="app-contain" :style='{"minHeight":"100vh","padding":"0","margin":"0 auto","overflow":"hidden","borderRadius":"4px","background":"none","width":"1200px","position":"relative"}'>
    <div class="bread_view">
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item class="first_breadcrumb" :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item class="second_breadcrumb" :to="{ path: '/parent/meeting' }">在线家长会</el-breadcrumb-item>
        <el-breadcrumb-item class="second_breadcrumb">详情</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <el-descriptions title="会议详情" :column="1" border style="margin-top: 20px;">
      <el-descriptions-item label="会议ID">{{ detail.id }}</el-descriptions-item>
      <el-descriptions-item label="会议标题">{{ detail.title }}</el-descriptions-item>
      <el-descriptions-item label="会议类型">{{ Number(detail.meetingType) === 2 ? '教师群体会' : '一对一预约' }}</el-descriptions-item>
      <el-descriptions-item label="创建者">{{ detail.creatorRole }} / {{ detail.creatorId }}</el-descriptions-item>
      <el-descriptions-item label="家长ID">{{ detail.parentId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="教师ID">{{ detail.teacherId }}</el-descriptions-item>
      <el-descriptions-item label="会议时间">{{ detail.meetingTime }}</el-descriptions-item>
      <el-descriptions-item label="会议状态">{{ statusText(detail.status) }}</el-descriptions-item>
      <el-descriptions-item label="我的参会">{{ joinText(detail.mineJoinStatus) }}</el-descriptions-item>
      <el-descriptions-item label="会议室">{{ detail.roomId }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
    </el-descriptions>

    <div style="margin-top: 20px;">
      <el-button @click="goBack">返回</el-button>
    </div>
  </div>
</template>

<script setup>
import { getCurrentInstance, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const context = getCurrentInstance()?.appContext.config.globalProperties
const route = useRoute()
const router = useRouter()
const detail = ref({})

const statusText = (s) => {
  const map = { 0: '待确认', 1: '已确认', 2: '已完成', 3: '已取消' }
  return map[Number(s)] || '未知'
}

const joinText = (s) => {
  const map = { 0: '待确认', 1: '已确认', 2: '已拒绝' }
  return map[Number(s)] || '-'
}

const loadDetail = async () => {
  const id = route.params.id
  const res = await context?.$http({
    url: `api/parent/meetings/${id}`,
    method: 'get'
  })
  detail.value = res?.data?.data || {}
}

const goBack = () => router.push('/parent/meeting')

onMounted(loadDetail)
</script>
