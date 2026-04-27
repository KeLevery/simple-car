<template>
  <div class="violation-page">
    <van-nav-bar
      title="违章查询"
      left-arrow
      fixed
      placeholder
      @click-left="$router.back()"
    />

    <div class="summary-container">
      <div class="summary-grid">
        <div class="summary-card">
          <div class="label">违章记录</div>
          <div class="value">{{ summary.total || 0 }}</div>
        </div>
        <div class="summary-card">
          <div class="label">未处理</div>
          <div class="value highlight">{{ summary.untreated || 0 }}</div>
        </div>
        <div class="summary-card">
          <div class="label">累计罚款</div>
          <div class="value">￥{{ summary.totalFine || 0 }}</div>
        </div>
        <div class="summary-card">
          <div class="label">累计扣分</div>
          <div class="value">{{ summary.totalPoints || 0 }}</div>
        </div>
      </div>
    </div>

    <div class="list-container">
      <template v-if="list.length > 0">
        <div v-for="item in list" :key="item.id" class="violation-card">
          <div class="card-header">
            <span class="type">{{ item.violationType }}</span>
            <van-tag :type="item.status === 0 ? 'danger' : 'success'" round>
              {{ item.status === 0 ? '未处理' : '已处理' }}
            </van-tag>
          </div>
          <div class="card-body">
            <div class="info-item">
              <van-icon name="location-o" />
              <span>{{ item.location }}</span>
            </div>
            <div class="info-item">
              <van-icon name="clock-o" />
              <span>{{ item.violationTime }}</span>
            </div>
            <div class="fine-info">
              <span class="fine">罚款：￥{{ item.fineAmount }}</span>
              <span class="points">扣分：{{ item.deductPoints }}分</span>
            </div>
          </div>
        </div>
      </template>
      <van-empty v-else description="恭喜，暂无违章记录" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { violationList } from '@/api/violation'

const list = ref([])
const summary = ref({})

const fetchData = async () => {
  try {
    const res = await violationList()
    if (res.code === 200) {
      list.value = res.data.list
      summary.value = {
        total: res.data.total,
        untreated: res.data.untreated,
        totalFine: res.data.totalFine,
        totalPoints: res.data.totalPoints
      }
    }
  } catch (error) {
    console.error('Failed to fetch violations:', error)
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.violation-page {
  min-height: 100vh;
  background-color: var(--bg-primary);
  padding-bottom: 20px;

  :deep(.van-nav-bar) {
    background: var(--bg-glass);
    backdrop-filter: blur(var(--card-blur));
    border-bottom: 1px solid var(--border);
    .van-nav-bar__title { color: var(--text-primary); font-weight: 300; }
    .van-icon { color: var(--accent); }
  }
}

.summary-container {
  padding: 16px;
}

.summary-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.summary-card {
  background: var(--bg-card);
  border: 1px solid var(--border);
  backdrop-filter: blur(var(--card-blur));
  padding: 16px;
  border-radius: var(--card-radius);
  text-align: center;
}

.summary-card .label {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.summary-card .value {
  font-size: 18px;
  font-weight: bold;
  color: var(--text-primary);
}

.summary-card .value.highlight {
  color: var(--accent);
  text-shadow: 0 0 10px var(--accent-glow);
}

.list-container {
  padding: 0 16px;
}

.violation-card {
  background: var(--bg-card);
  border: 1px solid var(--border);
  backdrop-filter: blur(var(--card-blur));
  border-radius: var(--card-radius);
  padding: 16px;
  margin-bottom: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border);
}

.card-header .type {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.info-item {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.info-item .van-icon {
  margin-right: 6px;
  color: var(--accent);
}

.fine-info {
  margin-top: 12px;
  display: flex;
  gap: 20px;
  font-size: 14px;
}

.fine-info .fine {
  color: var(--danger);
  font-weight: 500;
}

.fine-info .points {
  color: var(--warning);
  font-weight: 500;
}
</style>
