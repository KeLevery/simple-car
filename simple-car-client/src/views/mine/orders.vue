<template>
	<div class="orders-container">
		<van-nav-bar 
			title="我的订单" 
			left-text="返回" 
			left-arrow 
			fixed 
			placeholder 
			@click-left="$router.back()"
		/>

		<van-tabs v-model:active="activeTab" sticky offset-top="46" color="#00d4ff" background="#111827">
			<van-tab title="全部订单">
				<div class="order-list" v-if="orders.length > 0">
					<div v-for="order in orders" :key="order.uid || order.id" class="order-card">
						<div class="order-header">
							<span class="type">{{ order.type }}</span>
							<span class="status" :class="getStatusClass(order.status)">{{ order.status }}</span>
						</div>
						<div class="order-body">
							<div class="detail">{{ order.detail }}</div>
							<div class="amount">￥{{ order.amount }}</div>
						</div>
						<div class="order-footer">
							<span class="time">{{ formatTime(order.time) }}</span>
							<van-button size="mini" round plain type="info" @click="showDetail(order)">查看详情</van-button>
						</div>
					</div>
				</div>
				<van-empty v-else description="暂无订单记录" />
			</van-tab>
			<van-tab title="进行中">
				<van-empty description="暂无进行中的订单" />
			</van-tab>
			<van-tab title="已完成">
				<div class="order-list">
					<div v-for="order in completedOrders" :key="order.uid || order.id" class="order-card">
						<div class="order-header">
							<span class="type">{{ order.type }}</span>
							<span class="status success">{{ order.status }}</span>
						</div>
						<div class="order-body">
							<div class="detail">{{ order.detail }}</div>
							<div class="amount">￥{{ order.amount }}</div>
						</div>
						<div class="order-footer">
							<span class="time">{{ formatTime(order.time) }}</span>
							<van-button size="mini" round plain type="info" @click="showDetail(order)">查看详情</van-button>
						</div>
					</div>
				</div>
			</van-tab>
		</van-tabs>
		
		<van-popup v-model:show="showDetailPopup" position="bottom" round :style="{ maxHeight: '70%' }">
			<div class="detail-popup" v-if="currentOrder">
				<div class="detail-header">
					<h3>订单详情</h3>
				</div>
				<div class="detail-content">
					<div class="detail-row">
						<span class="detail-label">订单类型</span>
						<span class="detail-value">{{ currentOrder.type }}</span>
					</div>
					<div class="detail-row">
						<span class="detail-label">订单状态</span>
						<span class="detail-value" :class="getStatusClass(currentOrder.status)">{{ currentOrder.status }}</span>
					</div>
					<div class="detail-row">
						<span class="detail-label">订单详情</span>
						<span class="detail-value">{{ currentOrder.detail }}</span>
					</div>
					<div class="detail-row">
						<span class="detail-label">订单金额</span>
						<span class="detail-value amount">￥{{ currentOrder.amount }}</span>
					</div>
					<div class="detail-row">
						<span class="detail-label">下单时间</span>
						<span class="detail-value">{{ formatTime(currentOrder.time) }}</span>
					</div>
				</div>
				<div class="detail-footer">
					<van-button type="default" block round @click="showDetailPopup = false">关闭</van-button>
				</div>
			</div>
		</van-popup>
	</div>
</template>

<script setup>
import { orderList } from '@/api/order'
import { computed, ref } from 'vue'

const activeTab = ref(0)
const orders = ref([])
const currentOrder = ref(null)
const showDetailPopup = ref(false)
const completedOrders = computed(() => {
			return orders.value.filter(o => o.status === '已支付' || o.status === '已完成');
		})
async function fetchOrders() {
			const res = await orderList();
			if (res.code === 200) {
				orders.value = res.data;
			}
		}
function showDetail(order) {
			currentOrder.value = order;
			showDetailPopup.value = true;
		}
function getStatusClass(status) {
			if (status === '已支付' || status === '已完成') return 'success';
			if (status === '待支付') return 'warning';
			return 'info';
		}
function formatTime(time) {
			return time ? time.replace('T', ' ').substring(0, 16) : '';
		}
fetchOrders();
</script>

<style lang="scss" scoped>
.orders-container {
	min-height: 100vh;
	background-color: var(--bg-primary);

  :deep(.van-nav-bar) {
    background: var(--bg-glass);
    backdrop-filter: blur(var(--card-blur));
    border-bottom: 1px solid var(--border);
    .van-nav-bar__title { color: var(--text-primary); font-weight: 300; }
    .van-icon, .van-nav-bar__text { color: var(--accent); }
  }

  :deep(.van-tabs__nav) {
    background-color: var(--bg-secondary);
  }
  :deep(.van-tab) {
    color: var(--text-secondary);
  }
  :deep(.van-tab--active) {
    color: var(--accent);
    font-weight: 600;
  }
  :deep(.van-tabs__line) {
    background-color: var(--accent);
    box-shadow: 0 0 8px var(--accent-glow);
  }
}

.order-list {
	padding: 15px;
	
	.order-card {
		background: var(--bg-card);
    border: 1px solid var(--border);
    backdrop-filter: blur(var(--card-blur));
		border-radius: var(--card-radius);
		padding: 15px;
		margin-bottom: 15px;
		
		.order-header {
			display: flex;
			justify-content: space-between;
			margin-bottom: 12px;
			.type { font-weight: 600; color: var(--text-primary); }
			.status { 
				font-size: 12px;
				&.success { color: var(--success); }
				&.warning { color: var(--warning); }
				&.info { color: var(--text-tertiary); }
			}
		}
		
		.order-body {
			display: flex;
			justify-content: space-between;
			align-items: flex-end;
			margin-bottom: 15px;
			.detail { color: var(--text-secondary); font-size: 14px; font-weight: 300; }
			.amount { font-size: 18px; font-weight: 700; color: var(--accent); text-shadow: 0 0 5px var(--accent-glow); }
		}
		
		.order-footer {
			display: flex;
			justify-content: space-between;
			align-items: center;
			border-top: 1px solid var(--border);
			padding-top: 12px;
			.time { font-size: 12px; color: var(--text-tertiary); }
      :deep(.van-button) {
        border-color: var(--border-accent);
        color: var(--accent);
      }
		}
	}
}

:deep(.van-empty__description) { color: var(--text-tertiary); }

.detail-popup {
    padding: 20px;
    background: var(--bg-secondary);
    
    .detail-header {
        text-align: center;
        margin-bottom: 20px;
        h3 {
            font-size: 16px;
            font-weight: 600;
            color: var(--text-primary);
            margin: 0;
        }
    }
    
    .detail-content {
        .detail-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 14px 0;
            border-bottom: 1px solid var(--border);
            
            .detail-label {
                font-size: 14px;
                color: var(--text-secondary);
            }
            .detail-value {
                font-size: 14px;
                color: var(--text-primary);
                font-weight: 500;
                
                &.success { color: var(--success); }
                &.warning { color: var(--warning); }
                &.amount { color: var(--accent); font-weight: 700; }
            }
        }
    }
    
    .detail-footer {
        margin-top: 20px;
        
        :deep(.van-button) {
            background: var(--bg-card);
            border-color: var(--border);
            color: var(--text-secondary);
        }
    }
}
</style>
