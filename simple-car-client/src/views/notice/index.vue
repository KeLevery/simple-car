<template>
	<div class="notice-container">
		<van-nav-bar 
			title="消息中心" 
			left-text="返回" 
			left-arrow 
			fixed 
			placeholder 
			@click-left="$router.back()"
		/>

		<van-pull-refresh v-model="refreshing" @refresh="onRefresh">
			<div class="notice-list" v-if="list.length > 0">
				<div 
					v-for="item in list" 
					:key="item.id" 
					class="notice-item"
				>
					<div class="notice-header">
						<div class="type-tag" :class="getTypeClass(item.type)">
							{{ getTypeName(item.type) }}
						</div>
						<div class="time">{{ item.createTime ? item.createTime.substring(0, 16) : '' }}</div>
					</div>
					<div class="notice-body">
						<div class="title">{{ item.title }}</div>
						<div class="content">{{ item.content }}</div>
					</div>
				</div>
			</div>
			
			<div class="empty-box" v-else-if="!loading">
				<van-empty description="暂无消息通知" />
			</div>
		</van-pull-refresh>
	</div>
</template>

<script>
import { noticeList } from '@/api/notice'

export default {
	data() {
		return {
			list: [],
			loading: false,
			refreshing: false
		}
	},
	created() {
		this.onLoad()
	},
	methods: {
		onLoad() {
			this.loading = true
			noticeList().then(res => {
				if (res.code === 200) {
					this.list = res.data
				}
				this.loading = false
				this.refreshing = false
			}).catch(() => {
				this.loading = false
				this.refreshing = false
			})
		},
		onRefresh() {
			this.onLoad()
		},
		getTypeName(type) {
			const names = { 1: '系统', 2: '维保', 3: '充电' }
			return names[type] || '通知'
		},
		getTypeClass(type) {
			const classes = { 1: 'sys', 2: 'service', 3: 'charge' }
			return classes[type] || ''
		}
	}
}
</script>

<style lang="scss" scoped>
.notice-container {
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

.notice-list {
	padding: 15px;
	
	.notice-item {
		background: var(--bg-card);
    border: 1px solid var(--border);
    backdrop-filter: blur(var(--card-blur));
		border-radius: var(--card-radius);
		padding: 15px;
		margin-bottom: 15px;
		
		.notice-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin-bottom: 12px;
			
			.type-tag {
				font-size: 11px;
				padding: 2px 8px;
				border-radius: 4px;
				
				&.sys { background: rgba(0, 212, 255, 0.1); color: var(--accent); border: 1px solid var(--border-accent); }
				&.service { background: rgba(245, 158, 11, 0.1); color: var(--warning); border: 1px solid rgba(245, 158, 11, 0.2); }
				&.charge { background: rgba(34, 197, 94, 0.1); color: var(--success); border: 1px solid rgba(34, 197, 94, 0.2); }
			}
			
			.time {
				font-size: 12px;
				color: var(--text-tertiary);
			}
		}
		
		.notice-body {
			.title {
				font-size: 16px;
				font-weight: 600;
				color: var(--text-primary);
				margin-bottom: 6px;
			}
			.content {
				font-size: 14px;
				color: var(--text-secondary);
				line-height: 1.5;
        font-weight: 300;
			}
		}
	}
}

.empty-box {
	padding-top: 100px;
  :deep(.van-empty__description) { color: var(--text-tertiary); }
}

:deep(.van-pull-refresh) {
  min-height: calc(100vh - 46px);
}
</style>
