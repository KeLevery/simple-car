<template>
  <div class="rescue-container">
    <van-nav-bar title="一键救援" left-text="返回" left-arrow fixed placeholder @click-left="$router.back()" />

    <div class="emergency-section">
      <div class="sos-button-wrapper" @click="handleRescue">
        <div class="sos-button">
          <div class="sos-text">SOS</div>
          <div class="sos-desc">一键呼救</div>
        </div>
        <div class="sos-ripple"></div>
      </div>
      <p class="emergency-tip">点击上方按钮，专业救援人员将立即联系您</p>
    </div>

    <div class="form-section">
      <div class="section-title">救援信息</div>
      <van-form @submit="handleRescue">
        <van-cell-group inset>
          <van-field v-model="form.contactName" label="联系人" placeholder="请输入联系人姓名" :rules="[{ required: true, message: '请输入联系人姓名' }]" />
          <van-field v-model="form.contactPhone" label="联系电话" placeholder="请输入联系电话" type="tel" :rules="[{ required: true, message: '请输入联系电话' }]" />
          <van-field v-model="form.location" label="当前位置" placeholder="请输入或点击获取当前位置" :rules="[{ required: true, message: '请输入当前位置' }]">
            <template #button>
              <van-button size="small" type="primary" plain round @click="getLocation">获取定位</van-button>
            </template>
          </van-field>
          <van-field v-model="form.description" label="故障描述" placeholder="请简要描述车辆故障情况" type="textarea" rows="2" autosize />
        </van-cell-group>
        <div style="margin: 20px 16px;">
          <van-button round block type="danger" native-type="submit" :loading="loading" loading-text="正在发送请求...">
            立即发起救援
          </van-button>
        </div>
      </van-form>
    </div>

    <div class="history-section">
      <div class="section-title">救援历史</div>
      <van-empty v-if="history.length === 0" description="暂无救援历史记录" />
      <div v-else class="history-list">
        <div v-for="item in history" :key="item.id" class="history-card">
          <div class="card-header">
            <span class="order-no">救援单号: {{ item.id }}</span>
            <van-tag :type="getStatusType(item.status)">{{ getStatusName(item.status) }}</van-tag>
          </div>
          <div class="card-content">
            <p><van-icon name="location-o" /> {{ item.location }}</p>
            <p><van-icon name="clock-o" /> {{ formatDate(item.createTime) }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { createRescue, rescueList } from '@/api/rescue'
import dayjs from 'dayjs'

export default {
  name: 'RescueService',
  data() {
    return {
      loading: false,
      form: {
        contactName: '',
        contactPhone: '',
        location: '',
        description: ''
      },
      history: []
    }
  },
  created() {
    this.initForm()
    this.fetchHistory()
  },
  methods: {
    initForm() {
      const userInfoStr = localStorage.getItem('userInfo')
      if (userInfoStr) {
        try {
          const userInfo = JSON.parse(userInfoStr)
          this.form.contactName = userInfo.nickname || userInfo.username || ''
          this.form.contactPhone = userInfo.phone || ''
        } catch (e) {
          console.error('解析用户信息失败', e)
        }
      }
    },
    async fetchHistory() {
      const res = await rescueList()
      if (res.code === 200) {
        this.history = res.data
      }
    },
    async handleRescue() {
      if (!this.form.contactName || !this.form.contactPhone || !this.form.location) {
        this.$toast('请填写完整的联系信息和位置')
        return
      }

      this.loading = true
      try {
        const res = await createRescue(this.form)
        if (res.code === 200) {
          this.$toast.success('救援请求已发送，请保持电话畅通')
          this.form.description = '' // 清除故障描述
          this.fetchHistory()
        } else {
          this.$toast.fail(res.message || '发送失败')
        }
      } catch (error) {
        this.$toast.fail('网络错误，请稍后再试')
      } finally {
        this.loading = false
      }
    },
    getLocation() {
      this.$toast.loading({
        message: '获取定位中...',
        forbidClick: true
      })
      
      // 模拟获取定位
      setTimeout(() => {
        this.form.location = '江苏省南京市江宁区秣陵街道苏源大道'
        this.$toast.success('已自动获取位置')
      }, 1000)
    },
    getStatusName(status) {
      const map = {
        0: '待处理',
        1: '救援中',
        2: '已完成',
        3: '已取消'
      }
      return map[status] || '未知'
    },
    getStatusType(status) {
      const map = {
        0: 'warning',
        1: 'primary',
        2: 'success',
        3: 'default'
      }
      return map[status] || 'default'
    },
    formatDate(date) {
      if (!date) return ''
      const d = new Date(date)
      if (isNaN(d.getTime())) return date
      const pad = (n) => String(n).padStart(2, '0')
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
    }
  }
}
</script>

<style lang="scss" scoped>
.rescue-container {
  min-height: 100vh;
  background-color: var(--bg-primary);
  padding-bottom: 30px;

  :deep(.van-nav-bar) {
    background: var(--bg-glass);
    backdrop-filter: blur(var(--card-blur));
    border-bottom: 1px solid var(--border);
    .van-nav-bar__title { color: var(--text-primary); font-weight: 300; }
    .van-icon, .van-nav-bar__text { color: var(--accent); }
  }
}

.emergency-section {
  padding: 40px 0 30px;
  text-align: center;

  .sos-button-wrapper {
    position: relative;
    width: 140px;
    height: 140px;
    margin: 0 auto;
    cursor: pointer;
    
    .sos-button {
      position: relative;
      z-index: 2;
      width: 140px;
      height: 140px;
      background: linear-gradient(135deg, #ff4d4f, #f5222d);
      border-radius: 50%;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      color: #fff;
      box-shadow: 0 0 30px rgba(245, 34, 45, 0.6);
      transition: transform 0.2s;
      
      &:active {
        transform: scale(0.95);
      }

      .sos-text {
        font-size: 36px;
        font-weight: 800;
        letter-spacing: 2px;
        text-shadow: 0 0 15px rgba(255, 255, 255, 0.4);
      }
      
      .sos-desc {
        font-size: 14px;
        opacity: 0.9;
        margin-top: 4px;
      }
    }

    .sos-ripple {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      border-radius: 50%;
      border: 2px solid #ff4d4f;
      animation: ripple 2s infinite ease-out;
      z-index: 1;
      box-shadow: 0 0 20px rgba(245, 34, 45, 0.4);
    }
  }

  .emergency-tip {
    font-size: 13px;
    color: var(--text-secondary);
    margin-top: 25px;
  }
}

@keyframes ripple {
  0% { transform: scale(1); opacity: 0.8; }
  100% { transform: scale(1.6); opacity: 0; }
}

.section-title {
  padding: 0 20px 15px;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  letter-spacing: 1px;
}

.history-section {
  margin-top: 30px;
  
  .history-list {
    padding: 0 15px;
  }

  .history-card {
    background: var(--bg-card);
    border: 1px solid var(--border);
    backdrop-filter: blur(var(--card-blur));
    border-radius: var(--card-radius);
    padding: 15px;
    margin-bottom: 12px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 10px;
      padding-bottom: 10px;
      border-bottom: 1px solid var(--border);

      .order-no {
        font-size: 13px;
        color: var(--text-tertiary);
      }
    }

    .card-content {
      p {
        font-size: 14px;
        color: var(--text-primary);
        margin: 6px 0;
        display: flex;
        align-items: center;
        
        .van-icon {
          margin-right: 6px;
          color: var(--accent);
        }
      }
    }
  }
}

:deep(.van-cell-group) {
  background: var(--bg-card);
  border: 1px solid var(--border);
  backdrop-filter: blur(var(--card-blur));
  &::after { border: none; }
}

:deep(.van-cell) {
  background: transparent;
  color: var(--text-primary);
  &::after { border-bottom-color: var(--border); }
  .van-field__label { color: var(--text-secondary); }
  .van-field__control { color: var(--text-primary); }
}

:deep(.van-button--danger) {
  background: linear-gradient(135deg, #ff4d4f, #f5222d);
  border: none;
  box-shadow: 0 4px 15px rgba(245, 34, 45, 0.4);
}
</style>
