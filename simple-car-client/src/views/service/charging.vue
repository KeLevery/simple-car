<template>
  <div class="charging-container">
    <van-nav-bar title="充电站" left-text="返回" left-arrow fixed placeholder @click-left="$router.back()" />
    
    <div class="filter-section">
      <van-dropdown-menu>
        <van-dropdown-item v-model="cityValue" :options="cityOptions" @change="onCityChange" />
        <van-dropdown-item v-model="statusValue" :options="statusOptions" @change="onCityChange" />
      </van-dropdown-menu>
    </div>

    <div class="station-list">
      <div v-for="station in stations" :key="station.id" class="station-card">
        <div class="station-header">
          <div class="station-name">{{ station.stationName }}</div>
          <van-tag :type="station.availablePiles > 0 ? 'success' : 'danger'" plain>
            {{ station.availablePiles > 0 ? '可用' : '满桩' }}
          </van-tag>
        </div>
        <div class="station-address">
          <van-icon name="location-o" />
          <span>{{ station.address }}</span>
        </div>
        <div class="station-piles">
          <div class="piles-info">
            <span class="available">{{ station.availablePiles }}</span>
            <span class="total"> / {{ station.totalPiles }} 空闲</span>
          </div>
          <van-progress 
            :percentage="station.totalPiles > 0 ? (station.availablePiles / station.totalPiles * 100) : 0" 
            stroke-width="4" 
            :show-pivot="false" 
            :color="station.availablePiles > 0 ? '#22c55e' : '#ee0a24'" 
            track-color="#ebedf0" 
          />
        </div>
        <div class="station-actions">
          <div class="distance">距离 1.2km</div>
          <van-button size="small" type="info" round plain icon="guide-o">导航</van-button>
        </div>
      </div>
      <van-empty v-if="stations.length === 0" description="暂无充电站数据" />
    </div>
  </div>
</template>

<script setup>
import { stationList } from '@/api/charging'
import { ref } from 'vue'

defineOptions({ name: 'ChargingStation' })
const stations = ref([])
const cityValue = ref('320100')
const statusValue = ref(0)
const cityOptions = ref([
        { text: '南京市', value: '320100' },
        { text: '苏州市', value: '320500' },
        { text: '无锡市', value: '320200' },
      ])
const statusOptions = ref([
        { text: '全部状态', value: 0 },
        { text: '有空闲', value: 1 },
      ])
async function fetchStations() {
      const res = await stationList({
        cityId: cityValue.value,
        availableOnly: statusValue.value === 1
      })
      if (res.code === 200) {
        stations.value = res.data
      }
    }
function onCityChange() {
      fetchStations()
    }
fetchStations()
</script>

<style lang="scss" scoped>
.charging-container {
  min-height: 100vh;
  background-color: var(--bg-primary);
  padding-bottom: 20px;

  :deep(.van-nav-bar) {
    background: var(--bg-glass);
    backdrop-filter: blur(var(--card-blur));
    border-bottom: 1px solid var(--border);
    .van-nav-bar__title { color: var(--text-primary); font-weight: 300; }
    .van-icon, .van-nav-bar__text { color: var(--accent); }
  }
}

.filter-section {
  :deep(.van-dropdown-menu__bar) {
    background: var(--bg-glass);
    border-bottom: 1px solid var(--border);
    box-shadow: none;
  }
  :deep(.van-dropdown-menu__title) {
    color: var(--text-primary);
  }
  :deep(.van-dropdown-item__content) {
    background: var(--bg-card);
    backdrop-filter: blur(var(--card-blur));
  }
  :deep(.van-cell) {
    background: transparent;
    color: var(--text-primary);
  }
}

.station-list {
  padding: 15px;
}

.station-card {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--card-radius);
  backdrop-filter: blur(var(--card-blur));
  padding: 18px;
  margin-bottom: 15px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);

  .station-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;

    .station-name {
      font-size: 16px;
      font-weight: 600;
      color: var(--text-primary);
    }
  }

  .station-address {
    display: flex;
    align-items: flex-start;
    font-size: 13px;
    color: var(--text-secondary);
    margin-bottom: 15px;
    
    .van-icon {
      margin-top: 2px;
      margin-right: 4px;
      color: var(--accent);
    }
    
    span {
      line-height: 1.4;
    }
  }

  .station-piles {
    margin-bottom: 15px;

    .piles-info {
      font-size: 12px;
      color: var(--text-secondary);
      margin-bottom: 6px;

      .available {
        font-size: 16px;
        font-weight: 700;
        color: var(--accent);
        text-shadow: 0 0 10px var(--accent-glow);
      }
    }
    :deep(.van-progress) {
      background: var(--border);
    }
  }

  .station-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 12px;
    border-top: 1px solid var(--border);

    .distance {
      font-size: 13px;
      color: var(--text-secondary);
    }

    :deep(.van-button) {
      padding: 0 20px;
      border-color: var(--border-accent);
      color: var(--accent);
    }
  }
}
</style>
