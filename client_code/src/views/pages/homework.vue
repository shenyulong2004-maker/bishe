<template>
  <div class="homework-container">
    <h2>作业提交</h2>
    
    <div class="form-group">
      <label>作业内容：</label>
      <textarea v-model="form.content" placeholder="请输入作业内容" rows="6"></textarea>
    </div>

    <div class="form-group">
      <label>附件地址：</label>
      <input v-model="form.attachmentUrl" type="text" placeholder="请输入附件地址或URL">
    </div>

    <div class="form-group checkbox">
      <input v-model="form.isPermanent" type="checkbox" id="permanent">
      <label for="permanent">是否永久保存</label>
    </div>

    <button @click="submitHomework" class="btn-submit">提交作业</button>
    <div v-if="message" :class="['message', messageType]">{{ message }}</div>

    <hr>
    <h3>已提交作业</h3>
    <div v-if="homeworkList.length === 0" class="empty">暂无作业</div>
    <div v-else class="homework-list">
      <div v-for="item in homeworkList" :key="item.id" class="homework-item">
        <div class="item-header">
          <span class="time">{{ formatTime(item.createTime) }}</span>
          <span v-if="item.isPermanent" class="badge">永久</span>
        </div>
        <div class="item-content">{{ item.content }}</div>
        <div v-if="item.attachmentUrl" class="item-attachment">
          附件：<a :href="item.attachmentUrl" target="_blank">{{ item.attachmentUrl }}</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Homework',
  data() {
    return {
      form: {
        content: '',
        attachmentUrl: '',
        isPermanent: false
      },
      homeworkList: [],
      message: '',
      messageType: ''
    };
  },
  mounted() {
    this.getHomework();
  },
  methods: {
    async submitHomework() {
      if (!this.form.content.trim()) {
        this.showMessage('请输入作业内容', 'error');
        return;
      }

      try {
        const payload = {
          ...this.form,
          isPermanent: this.form.isPermanent ? 1 : 0
        };
        const response = await fetch(`${process.env.VUE_APP_BASE_API_URL}${process.env.VUE_APP_BASE_API}/homework/saveHomework`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });

        const result = await response.json();
        if (result.success) {
          this.showMessage('作业提交成功', 'success');
          this.form = { content: '', attachmentUrl: '', isPermanent: false };
          this.getHomework();
        } else {
          this.showMessage(result.message || '提交失败', 'error');
        }
      } catch (error) {
        console.error(error);
        this.showMessage('网络错误', 'error');
      }
    },

    async getHomework() {
      try {
        const response = await fetch(`${process.env.VUE_APP_BASE_API_URL}${process.env.VUE_APP_BASE_API}/homework/getHomework`);
        const result = await response.json();
        if (result.success) {
          this.homeworkList = result.data;
        }
      } catch (error) {
        console.error(error);
      }
    },

    formatTime(time) {
      return new Date(time).toLocaleString('zh-CN');
    },

    showMessage(msg, type) {
      this.message = msg;
      this.messageType = type;
      setTimeout(() => {
        this.message = '';
      }, 3000);
    }
  }
};
</script>

<style scoped>
.homework-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 4px;
}

h2, h3 {
  color: #333;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  color: #555;
}

textarea, input[type="text"] {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-family: inherit;
  font-size: 14px;
  box-sizing: border-box;
}

textarea {
  resize: vertical;
}

.form-group.checkbox {
  display: flex;
  align-items: center;
}

.form-group.checkbox input {
  width: auto;
  margin-right: 8px;
}

.form-group.checkbox label {
  margin: 0;
  font-weight: normal;
}

.btn-submit {
  background: #007bff;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-submit:hover {
  background: #0056b3;
}

.message {
  margin-top: 10px;
  padding: 10px;
  border-radius: 4px;
  text-align: center;
}

.message.success {
  background: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.message.error {
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

hr {
  margin: 30px 0;
  border: none;
  border-top: 1px solid #ddd;
}

.empty {
  text-align: center;
  color: #999;
  padding: 20px;
}

.homework-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.homework-item {
  background: white;
  padding: 15px;
  border-radius: 4px;
  border-left: 4px solid #007bff;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 12px;
}

.time {
  color: #999;
}

.badge {
  background: #28a745;
  color: white;
  padding: 2px 8px;
  border-radius: 3px;
  font-size: 12px;
}

.item-content {
  color: #333;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
  margin-bottom: 10px;
}

.item-attachment {
  color: #666;
  font-size: 12px;
}

.item-attachment a {
  color: #007bff;
  text-decoration: none;
}

.item-attachment a:hover {
  text-decoration: underline;
}
</style>
