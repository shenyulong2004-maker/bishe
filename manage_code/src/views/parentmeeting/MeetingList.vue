<template>
  <div class="app-contain" :style='{"minHeight":"100vh","padding":"0","margin":"0 auto","overflow":"hidden","borderRadius":"4px","background":"none","width":"1200px","position":"relative"}'>
    <div class="bread_view">
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item class="first_breadcrumb" :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item class="second_breadcrumb">在线家长会</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <el-form :inline="true" class="list_search">
      <div class="search_btn_view">
        <el-button type="primary" @click="loadList">刷新</el-button>
        <el-button type="success" @click="goBook">发起/预约会议</el-button>
      </div>
    </el-form>

    <div class="list_bottom">
      <el-table :data="list" border style="width:100%">
        <el-table-column prop="id" label="ID" width="120" />
        <el-table-column prop="title" label="标题" min-width="160" />
        <el-table-column label="类型" width="130">
          <template #default="scope">
            {{ Number(scope.row.meetingType) === 2 ? '教师群体会' : '一对一预约' }}
          </template>
        </el-table-column>
        <el-table-column prop="teacherId" label="教师ID" width="120" />
        <el-table-column prop="parentId" label="家长ID" width="120" />
        <el-table-column prop="meetingTime" label="会议时间" min-width="180" />
        <el-table-column label="状态" width="120">
          <template #default="scope">
            <el-tag :type="statusTag(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="我的参会" width="120" v-if="isParent">
          <template #default="scope">
            <el-tag :type="joinTag(scope.row.mineJoinStatus)">{{ joinText(scope.row.mineJoinStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="roomId" label="会议室" min-width="140" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="goDetail(scope.row.id)">详情</el-button>

            <el-button
              link
              type="success"
              v-if="isParent && Number(scope.row.status) !== 3 && Number(scope.row.mineJoinStatus) === 0"
              @click="confirmMeeting(scope.row.id)"
            >确认参会</el-button>

            <el-button
              link
              type="warning"
              v-if="isTeacher && Number(scope.row.status) === 1"
              @click="finishMeeting(scope.row.id)"
            >标记完成</el-button>

            <el-button
              link
              type="danger"
              v-if="Number(scope.row.status) === 0 || Number(scope.row.status) === 1"
              @click="cancelMeeting(scope.row.id)"
            >取消</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { computed, getCurrentInstance, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const context = getCurrentInstance()?.appContext.config.globalProperties
const router = useRouter()
const list = ref([])

const sessionTable = computed(() => context?.$toolUtil.storageGet('sessionTable'))
const isParent = computed(() => sessionTable.value === 'xuesheng')
const isTeacher = computed(() => sessionTable.value === 'jiaoshi')

const statusText = (s) => {
  const map = { 0: '待确认', 1: '已确认', 2: '已完成', 3: '已取消' }
  return map[Number(s)] || '未知'
}
const statusTag = (s) => {
  const n = Number(s)
  if (n === 0) return 'warning'
  if (n === 1) return 'success'
  if (n === 2) return ''
  return 'info'
}

const joinText = (s) => {
  const map = { 0: '待确认', 1: '已确认', 2: '已拒绝' }
  return map[Number(s)] || '-'
}
const joinTag = (s) => {
  const n = Number(s)
  if (n === 1) return 'success'
  if (n === 2) return 'danger'
  return 'warning'
}

const loadList = async () => {
  const res = await context?.$http({
    url: 'api/parent/meetings',
    method: 'get'
  })
  list.value = res?.data?.data || []
}

const goBook = () => router.push('/parent/meeting/book')
const goDetail = (id) => router.push(`/parent/meeting/${id}`)

const confirmMeeting = async (id) => {
  await context?.$http({ url: `api/parent/meetings/${id}/confirm`, method: 'put' })
  context?.$toolUtil.message('确认成功', 'success')
  await loadList()
}

const finishMeeting = async (id) => {
  await context?.$http({ url: `api/parent/meetings/${id}/finish`, method: 'put' })
  context?.$toolUtil.message('已标记完成', 'success')
  await loadList()
}

const cancelMeeting = async (id) => {
  await context?.$http({ url: `api/parent/meetings/${id}/cancel`, method: 'put' })
  context?.$toolUtil.message('取消成功', 'success')
  await loadList()
}

onMounted(loadList)
</script>
