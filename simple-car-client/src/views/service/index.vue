<template>
	<div class="service-container">
		<!-- Header -->
		<div class="page-header">
			<h1 class="page-title">车主服务</h1>
		</div>

		<!-- Banner Card -->
		<div class="banner-card animate-fade stagger-1">
			<div class="banner-content">
				<div class="banner-text">
					<div class="banner-title">智选维保</div>
					<div class="banner-subtitle">专业技师 · 原厂配件 · 品质保障</div>
					<div class="banner-tag">PREMIUM SERVICE</div>
				</div>
				<div class="banner-icon-area">
					<img src="@/assets/service/appoint.png" class="banner-img" />
				</div>
			</div>
		</div>

		<!-- Core Services Grid -->
		<div class="section animate-fade stagger-2">
			<div class="section-header">
				<span class="section-label">核心服务</span>
			</div>
			<div class="service-grid">
				<div class="service-item" @click="gotoPage('/service/reservation')">
					<div class="service-icon blue">
						<van-icon name="clock-o" />
					</div>
					<div class="service-name">预约维保</div>
					<div class="service-desc">在线预约</div>
				</div>
				<div class="service-item" @click="gotoPage('/service/record')">
					<div class="service-icon orange">
						<van-icon name="description" />
					</div>
					<div class="service-name">维保历史</div>
					<div class="service-desc">查看记录</div>
				</div>
				<div class="service-item" @click="gotoPage('/service/rescue')">
					<div class="service-icon green">
						<van-icon name="aim" />
					</div>
					<div class="service-name">一键救援</div>
					<div class="service-desc">紧急求助</div>
				</div>
				<div class="service-item" @click="gotoPage('/service/purple')">
					<div class="service-icon purple">
						<van-icon name="shield-check-o" />
					</div>
					<div class="service-name">违章查询</div>
					<div class="service-desc">实时查询</div>
				</div>
			</div>
		</div>

		<!-- Charging Section -->
		<div class="section animate-fade stagger-3">
			<div class="section-header">
				<span class="section-label">智能充电</span>
			</div>
			<div class="charging-entry" @click="gotoPage('/service/charging')">
				<div class="charging-left">
					<div class="charging-icon-box">
						<van-icon name="lightning" size="18" />
					</div>
					<div>
						<div class="charging-title">发现充电站</div>
						<div class="charging-desc">附近有 {{ stations.length || 0 }} 个可用充电站</div>
					</div>
				</div>
				<div class="charging-right">
					<span class="charging-cta">查看</span>
					<van-icon name="arrow" size="12" />
				</div>
			</div>
		</div>

		<!-- Station List -->
		<div class="section animate-fade stagger-4" v-if="stations.length > 0">
			<div class="section-header">
				<span class="section-label">附近站点</span>
			</div>
			<div class="station-list">
				<div class="station-item" v-for="(station, index) in stations" :key="index">
					<div class="station-top">
						<div class="station-name">{{ station.stationName }}</div>
						<div class="station-avail">
							<span class="avail-num">{{ station.availablePiles }}</span>
							<span class="avail-total">/{{ station.totalPiles }}</span>
							<span class="avail-label">空闲</span>
						</div>
					</div>
					<div class="station-address">
						<van-icon name="location-o" size="12" />
						<span>{{ station.address }}</span>
					</div>
					<div class="station-tags">
						<span class="s-tag">24H</span>
						<span class="s-tag accent">五星级</span>
					</div>
				</div>
			</div>
		</div>

		<Tabbar active="/service" />
	</div>
</template>

<script setup>
import Tabbar from "@/components/Tabbar.vue"
import { stationList } from '@/api/charging'
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const stations = ref([])
async function fetchStations() {
			const res = await stationList({ cityId: '320100' });
			if (res.code === 200) {
				stations.value = res.data;
			}
		}
function gotoPage(path) {
			router.push({
				path: path
			});
		}
fetchStations();
</script>

<style lang="scss" scoped>
.service-container {
	min-height: 100vh;
	background: var(--bg-page);
	padding-bottom: 100px;
	position: relative;
}

/* ===== Header ===== */
.page-header {
	padding: 20px 20px 0;
	padding-top: calc(env(safe-area-inset-top, 20px) + 20px);
	margin-bottom: 16px;

	.page-title {
		font-size: 22px;
		font-weight: 600;
		color: var(--text-primary);
		margin: 0;
	}
}

/* ===== Banner Card ===== */
.banner-card {
	margin: 0 20px;
	border-radius: 16px;
	overflow: hidden;
	background: #fff;
	background: linear-gradient(135deg, rgba(59, 130, 246, 0.05) 0%, #ffffff 100%);
	box-shadow: var(--card-shadow);
	border: 1px solid var(--border);

	.banner-content {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 24px;
	}

	.banner-text {
		.banner-title {
			font-size: 20px;
			font-weight: 600;
			color: var(--text-primary);
			margin-bottom: 4px;
		}
		.banner-subtitle {
			font-size: 12px;
			color: var(--text-secondary);
		}
		.banner-tag {
			margin-top: 12px;
			font-size: 10px;
			color: var(--accent);
			background: rgba(59, 130, 246, 0.08);
			border-radius: 4px;
			padding: 4px 8px;
			display: inline-block;
			font-weight: 500;
		}
	}

	.banner-icon-area {
		width: 64px;
		height: 64px;
		display: flex;
		align-items: center;
		justify-content: center;

		.banner-img {
			width: 100%;
			height: auto;
			object-fit: contain;
		}
	}
}

/* ===== Section ===== */
.section {
	padding: 0 20px;
	margin-top: 24px;
}

.section-header {
	margin-bottom: 12px;

	.section-label {
		font-size: 15px;
		font-weight: 600;
		color: var(--text-primary);
	}
}

/* ===== Service Grid ===== */
.service-grid {
	display: grid;
	grid-template-columns: 1fr 1fr;
	gap: 12px;
}

.service-item {
	background: var(--bg-card);
	border-radius: 12px;
	padding: 20px 16px;
	text-align: center;
	box-shadow: var(--card-shadow);
	border: 1px solid var(--border);
	transition: transform 0.2s;

	&:active {
		transform: scale(0.96);
	}

	.service-icon {
		width: 44px;
		height: 44px;
		margin: 0 auto 12px;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 20px;

		&.blue { background: rgba(59, 130, 246, 0.08); color: #3b82f6; }
		&.orange { background: rgba(245, 158, 11, 0.08); color: #f59e0b; }
		&.green { background: rgba(16, 185, 129, 0.08); color: #10b981; }
		&.purple { background: rgba(139, 92, 246, 0.08); color: #8b5cf6; }
	}

	.service-name {
		font-size: 14px;
		font-weight: 500;
		color: var(--text-primary);
		margin-bottom: 4px;
	}

	.service-desc {
		font-size: 11px;
		color: var(--text-tertiary);
	}
}

/* ===== Charging Entry ===== */
.charging-entry {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 16px;
	background: var(--bg-card);
	border-radius: 12px;
	box-shadow: var(--card-shadow);
	border: 1px solid var(--border);
	transition: transform 0.2s;

	&:active {
		transform: scale(0.98);
	}

	.charging-left {
		display: flex;
		align-items: center;
		gap: 12px;
	}

	.charging-icon-box {
		width: 40px;
		height: 40px;
		border-radius: 10px;
		background: rgba(59, 130, 246, 0.08);
		display: flex;
		align-items: center;
		justify-content: center;
		color: var(--accent);
	}

	.charging-title {
		font-size: 15px;
		font-weight: 500;
		color: var(--text-primary);
	}

	.charging-desc {
		font-size: 12px;
		color: var(--text-tertiary);
		margin-top: 2px;
	}

	.charging-right {
		display: flex;
		align-items: center;
		gap: 4px;
		color: var(--text-tertiary);

		.charging-cta {
			font-size: 12px;
		}
	}
}

/* ===== Station List ===== */
.station-list {
	background: var(--bg-card);
	border-radius: 12px;
	box-shadow: var(--card-shadow);
	overflow: hidden;
}

.station-item {
	padding: 16px;

	&:not(:last-child) {
		border-bottom: 1px solid var(--border);
	}

	.station-top {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 6px;
	}

	.station-name {
		font-size: 14px;
		font-weight: 500;
		color: var(--text-primary);
	}

	.station-avail {
		.avail-num {
			font-size: 16px;
			color: var(--accent);
			font-weight: 600;
		}
		.avail-total {
			font-size: 12px;
			color: var(--text-tertiary);
		}
		.avail-label {
			font-size: 11px;
			color: var(--text-tertiary);
			margin-left: 4px;
		}
	}

	.station-address {
		display: flex;
		align-items: center;
		gap: 4px;
		font-size: 12px;
		color: var(--text-tertiary);
		margin-bottom: 10px;
	}

	.station-tags {
		display: flex;
		gap: 8px;
	}

	.s-tag {
		font-size: 10px;
		padding: 2px 6px;
		border: 1px solid var(--border);
		border-radius: 4px;
		color: var(--text-secondary);

		&.accent {
			border-color: rgba(59, 130, 246, 0.2);
			background: rgba(59, 130, 246, 0.04);
			color: var(--accent);
		}
	}
}

/* ===== Animations ===== */
.animate-fade {
	opacity: 0;
	transform: translateY(10px);
	animation: fadeIn 0.5s forwards ease-out;
}

.stagger-1 { animation-delay: 0.1s; }
.stagger-2 { animation-delay: 0.2s; }
.stagger-3 { animation-delay: 0.3s; }
.stagger-4 { animation-delay: 0.4s; }

@keyframes fadeIn {
	to { opacity: 1; transform: translateY(0); }
}
</style>
