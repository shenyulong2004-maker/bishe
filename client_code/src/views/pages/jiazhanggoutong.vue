<template>
  <div class="app-contain" :style='{"minHeight":"100vh","padding":"0","margin":"0 auto","overflow":"hidden","borderRadius":"4px","background":"none","width":"1200px","position":"relative"}'>
    <div class="bread_view">
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item class="first_breadcrumb" :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item class="second_breadcrumb">家长-教师沟通</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="chat-layout" v-if="isTeacher">
      <div class="session-panel">
        <div class="session-title">家长会话</div>
        <div class="session-list" v-if="sessionList.length">
          <div
            v-for="s in sessionList"
            :key="s.parentId"
            class="session-item"
            :class="Number(activeParentId)===Number(s.parentId)?'active':''"
            @click="selectSession(s)"
          >
            <div class="row1">
              <span class="name">{{ s.parentName || ('家长#'+s.parentId) }}</span>
              <span class="time">{{ formatTime(s.lastTime) }}</span>
            </div>
            <div class="row2">
              <span class="preview">{{ s.lastContent || '暂无消息' }}</span>
              <span class="badge" v-if="Number(s.unread)>0">{{ s.unread }}</span>
            </div>
          </div>
        </div>
        <div class="empty" v-else>暂无会话</div>
      </div>

      <div class="chat-panel">
        <div class="topbar">
          <span>当前会话：{{ activeParentName || '请选择左侧家长' }}</span>
          <el-button type="primary" link @click="manualRefresh">刷新</el-button>
        </div>

        <div class="chat-list" v-if="conversationList.length">
          <div class="chat-item" v-for="item in conversationList" :key="item.id" :class="item.fromId===myId && item.fromRole===myTable ? 'mine' : 'other'">
            <div class="meta">
              <span>{{ item.fromName || roleName(item.fromRole) }}</span>
              <span>{{ formatTime(item.addtime) }}</span>
            </div>
            <div class="bubble">{{ item.content }}</div>
          </div>
        </div>
        <div class="empty" v-else>暂无沟通记录</div>

        <div class="editor">
          <el-input v-model="content" type="textarea" :rows="3" placeholder="请输入沟通内容" />
          <div class="actions">
            <el-button type="primary" @click="sendMsg">发送</el-button>
          </div>
        </div>
      </div>
    </div>

    <el-form :inline="true" class="list_search" v-else>
      <div class="search_view">
        <div class="search_label">选择教师：</div>
        <div class="search_box">
          <el-select class="search_inp" v-model="selectedTeacherId" placeholder="请选择教师" style="width: 260px" @change="loadParentConversation">
            <el-option v-for="item in teacherList" :key="item.id" :label="item.jiaoshixingming || item.jiaoshigonghao" :value="item.id" />
          </el-select>
        </div>
      </div>
      <div class="search_btn_view">
        <el-button class="search_btn" type="primary" @click="manualRefresh">刷新</el-button>
      </div>
    </el-form>

    <div class="chat-card" v-if="!isTeacher">
      <div class="chat-list" v-if="conversationList.length">
        <div class="chat-item" v-for="item in conversationList" :key="item.id" :class="item.fromId===myId && item.fromRole===myTable ? 'mine' : 'other'">
          <div class="meta">
            <span>{{ item.fromName || roleName(item.fromRole) }}</span>
            <span>{{ formatTime(item.addtime) }}</span>
          </div>
          <div class="bubble">{{ item.content }}</div>
        </div>
      </div>
      <div class="empty" v-else>暂无沟通记录</div>

      <div class="editor">
        <el-input v-model="content" type="textarea" :rows="3" placeholder="请输入沟通内容" />
        <div class="actions">
          <el-button type="primary" @click="sendMsg">发送</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, getCurrentInstance, onMounted, onUnmounted } from 'vue'

const context = getCurrentInstance()?.appContext.config.globalProperties

const myTable = ref('')
const myId = ref(null)
const teacherList = ref([])
const selectedTeacherId = ref(null)

const sessionList = ref([])
const activeParentId = ref(null)
const activeParentName = ref('')
const conversationList = ref([])
const content = ref('')
let timer = null

const isParent = computed(() => myTable.value === 'xuesheng')
const isTeacher = computed(() => myTable.value === 'jiaoshi')

const roleName = (r) => r === 'xuesheng' ? '家长' : (r === 'jiaoshi' ? '教师' : r)

const formatTime = (t) => {
  if (!t) return ''
  return new Date(t).toLocaleString('zh-CN')
}

const getSession = async () => {
  myTable.value = context?.$toolUtil.storageGet('frontSessionTable') || ''
  const res = await context?.$http({
    url: `${myTable.value}/session`,
    method: 'get'
  })
  myId.value = res.data.data.id
}

const loadTeachers = async () => {
  if (!isParent.value) return
  const res = await context?.$http({
    url: 'jiaoshi/list',
    method: 'get',
    params: { page: 1, limit: 500 }
  })
  teacherList.value = res.data.data.list || []
}

const loadSessionList = async () => {
  if (!isTeacher.value) return
  const res = await context?.$http({
    url: 'jiazhangjiaoshigoutong/sessionList',
    method: 'get'
  })
  sessionList.value = res.data.data || []
  if (!activeParentId.value && sessionList.value.length) {
    activeParentId.value = sessionList.value[0].parentId
    activeParentName.value = sessionList.value[0].parentName
    await loadTeacherConversation()
  }
}

const loadTeacherConversation = async () => {
  if (!isTeacher.value || !activeParentId.value) {
    conversationList.value = []
    return
  }
  const res = await context?.$http({
    url: 'jiazhangjiaoshigoutong/conversation',
    method: 'get',
    params: { parentId: activeParentId.value }
  })
  conversationList.value = res.data.data || []
}

const loadParentConversation = async () => {
  if (!isParent.value || !selectedTeacherId.value) {
    conversationList.value = []
    return
  }
  const res = await context?.$http({
    url: 'jiazhangjiaoshigoutong/conversation',
    method: 'get',
    params: { parentId: selectedTeacherId.value }
  })
  conversationList.value = res.data.data || []
}

const selectSession = async (s) => {
  activeParentId.value = s.parentId
  activeParentName.value = s.parentName
  await context?.$http({
    url: 'jiazhangjiaoshigoutong/markRead',
    method: 'post',
    params: { parentId: s.parentId }
  })
  await loadSessionList()
  await loadTeacherConversation()
}

const sendMsg = async () => {
  const msg = (content.value || '').trim()
  if (!msg) {
    context?.$toolUtil.message('请输入沟通内容', 'error')
    return
  }

  let toRole = ''
  let toId = null
  let toName = ''

  if (isParent.value) {
    if (!selectedTeacherId.value) {
      context?.$toolUtil.message('请先选择教师', 'error')
      return
    }
    const t = teacherList.value.find(v => Number(v.id) === Number(selectedTeacherId.value))
    if (!t) {
      context?.$toolUtil.message('教师不存在', 'error')
      return
    }
    toRole = 'jiaoshi'
    toId = t.id
    toName = t.jiaoshixingming || t.jiaoshigonghao
  } else {
    if (!activeParentId.value) {
      context?.$toolUtil.message('请先选择家长会话', 'error')
      return
    }
    toRole = 'xuesheng'
    toId = activeParentId.value
    toName = activeParentName.value
  }

  await context?.$http({
    url: 'jiazhangjiaoshigoutong/save',
    method: 'post',
    data: { toRole, toId, toName, content: msg }
  })
  content.value = ''
  context?.$toolUtil.message('发送成功', 'success')
  await manualRefresh()
}

const manualRefresh = async () => {
  if (isTeacher.value) {
    await loadSessionList()
    await loadTeacherConversation()
  } else {
    await loadParentConversation()
  }
}

const startPolling = () => {
  if (timer) clearInterval(timer)
  timer = setInterval(() => {
    manualRefresh()
  }, 8000)
}

onMounted(async () => {
  await getSession()
  await loadTeachers()
  if (isParent.value && teacherList.value.length) {
    selectedTeacherId.value = teacherList.value[0].id
  }
  await manualRefresh()
  startPolling()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.chat-layout {
  display: flex;
  gap: 14px;
}
.session-panel {
  width: 340px;
  border: 1px solid #eee;
  border-radius: 8px;
  background: #fff;
  padding: 10px;
}
.session-title {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 10px;
}
.session-list {
  max-height: 650px;
  overflow-y: auto;
}
.session-item {
  border: 1px solid #f0f0f0;
  border-radius: 6px;
  padding: 10px;
  margin-bottom: 8px;
  cursor: pointer;
}
.session-item.active {
  border-color: #409eff;
  background: #f5faff;
}
.row1 {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
}
.row1 .name {
  font-weight: 600;
}
.row1 .time {
  color: #888;
  font-size: 12px;
}
.row2 {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}
.row2 .preview {
  color: #666;
  font-size: 12px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.badge {
  min-width: 18px;
  height: 18px;
  line-height: 18px;
  text-align: center;
  border-radius: 9px;
  padding: 0 6px;
  background: #f56c6c;
  color: #fff;
  font-size: 12px;
}
.chat-panel {
  flex: 1;
  border: 1px solid #eee;
  border-radius: 8px;
  background: #fff;
  padding: 12px;
}
.topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.chat-card {
  background: #fff;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 16px;
}
.chat-list {
  max-height: 460px;
  overflow-y: auto;
  margin-bottom: 16px;
}
.chat-item {
  margin-bottom: 12px;
}
.chat-item .meta {
  font-size: 12px;
  color: #888;
  margin-bottom: 4px;
  display: flex;
  gap: 12px;
}
.chat-item .bubble {
  display: inline-block;
  padding: 10px 12px;
  border-radius: 6px;
  background: #f5f7fa;
  max-width: 72%;
  white-space: pre-wrap;
  word-break: break-all;
}
.chat-item.mine {
  text-align: right;
}
.chat-item.mine .bubble {
  background: #e6f7ff;
}
.editor .actions {
  margin-top: 10px;
  text-align: right;
}
.empty {
  color: #999;
  text-align: center;
  padding: 40px 0;
}
</style>
