<template>
	<div class="mine-container">
		<!-- Profile Header -->
		<div class="profile-header animate-fade-in">
			<div class="profile-card">
				<div class="avatar-area">
					<div class="avatar-inner">
						<van-image
							v-if="avatar"
							:src="avatar"
							round
							width="60"
							height="60"
							fit="cover"
							class="avatar-img"
						/>
						<div v-else class="avatar-fallback">
							{{ nickName ? nickName.charAt(0) : 'U' }}
						</div>
					</div>
				</div>
				<div class="profile-info">
					<div class="nick-name">{{ nickName || '用户' }}</div>
					<div class="phone-num">{{ phone }}</div>
				</div>
				<div class="profile-edit" @click="gotoPage('/mine/profile')">
					<van-icon name="edit" size="14" />
				</div>
			</div>
		</div>

		<!-- Stats Row -->
		<div class="stats-row animate-fade-in">
			<div class="stat-card">
				<div class="stat-value">{{ totalMileage }}</div>
				<div class="stat-label">总里程 km</div>
			</div>
			<div class="stat-divider"></div>
			<div class="stat-card">
				<div class="stat-value">{{ totalCharge }}</div>
				<div class="stat-label">充电次数</div>
			</div>
			<div class="stat-divider"></div>
			<div class="stat-card">
				<div class="stat-value">{{ carCount }}</div>
				<div class="stat-label">绑定车辆</div>
			</div>
		</div>

		<!-- Menu Sections -->
		<div class="menu-section animate-fade-in">
			<div class="section-header">
				<span class="section-label">SERVICES</span>
			</div>
			<div class="menu-group">
				<div class="menu-item" @click="gotoPage('/mine/notification')">
					<div class="menu-left">
						<div class="menu-icon blue"><van-icon name="bell" size="16" /></div>
						<span class="menu-name">消息中心</span>
					</div>
					<div class="menu-right">
						<div class="badge-dot" v-if="hasNewMsg"></div>
						<van-icon name="arrow" class="menu-arrow" />
					</div>
				</div>
				<div class="menu-item" @click="gotoPage('/mine/orders')">
					<div class="menu-left">
						<div class="menu-icon green"><van-icon name="orders-o" size="16" /></div>
						<span class="menu-name">我的订单</span>
					</div>
					<van-icon name="arrow" class="menu-arrow" />
				</div>
				<div class="menu-item" @click="gotoPage('/service/reservation')">
					<div class="menu-left">
						<div class="menu-icon orange"><van-icon name="clock-o" size="16" /></div>
						<span class="menu-name">维保预约</span>
					</div>
					<van-icon name="arrow" class="menu-arrow" />
				</div>
				<div class="menu-item" @click="gotoPage('/mine/addCar')">
					<div class="menu-left">
						<div class="menu-icon blue"><van-icon name="add-o" size="16" /></div>
						<span class="menu-name">添加车辆</span>
					</div>
					<van-icon name="arrow" class="menu-arrow" />
				</div>
			</div>
		</div>

		<div class="menu-section animate-fade-in">
			<div class="section-header">
				<span class="section-label">SETTINGS</span>
			</div>
			<div class="menu-group">
				<div class="menu-item" @click="gotoPage('/mine/settings')">
					<div class="menu-left">
						<div class="menu-icon grey"><van-icon name="setting-o" size="16" /></div>
						<span class="menu-name">常用设置</span>
					</div>
					<van-icon name="arrow" class="menu-arrow" />
				</div>
				<div class="menu-item" @click="contactCS">
					<div class="menu-left">
						<div class="menu-icon purple"><van-icon name="service-o" size="16" /></div>
						<span class="menu-name">在线客服</span>
					</div>
					<van-icon name="arrow" class="menu-arrow" />
				</div>
				<div class="menu-item" @click="clearCache">
					<div class="menu-left">
						<div class="menu-icon red"><van-icon name="delete-o" size="16" /></div>
						<span class="menu-name">清除缓存</span>
					</div>
					<van-icon name="arrow" class="menu-arrow" />
				</div>
				<div class="menu-item" @click="gotoPage('/mine/about')">
					<div class="menu-left">
						<div class="menu-icon grey"><van-icon name="info-o" size="16" /></div>
						<span class="menu-name">关于我们</span>
					</div>
					<van-icon name="arrow" class="menu-arrow" />
				</div>
			</div>
		</div>

		<!-- Logout -->
		<div class="logout-section animate-fade-in">
			<div class="logout-btn" @click="logout">
				<span class="logout-text">退出登录</span>
			</div>
		</div>

		<Tabbar active="/mine" />
	</div>
</template>

<script setup>
import Tabbar from "@/components/Tabbar.vue"
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useVantCompat } from '@/composables/useVantCompat'

const router = useRouter()
const route = useRoute()
const { toast, notify, dialog } = useVantCompat()
const nickName = ref('')
const phone = ref('')
const avatar = ref('https://img01.yzcdn.cn/vant/cat.jpeg')
const totalMileage = ref('0')
const totalCharge = ref('0')
const carCount = ref('0')
const hasNewMsg = ref(true)
function initProfile() {
			const userInfo = window.localStorage.getItem('userInfo');
			if (userInfo) {
				const user = JSON.parse(userInfo);
				nickName.value = user.nickName || user.userName;
				phone.value = user.phonenumber ? user.phonenumber.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') : '';
				avatar.value = user.avatar || '';
			}
			const carList = window.localStorage.getItem('carList');
			if (carList) {
				const cars = JSON.parse(carList);
				carCount.value = cars.length;
			}
			const carInfo = window.localStorage.getItem('carInfo');
			if (carInfo) {
				const car = JSON.parse(carInfo);
				totalMileage.value = car.totalMileage || '0';
			}
		}
function gotoPage(path) {
			router.push({ path });
		}
function contactCS() {
			toast('客服功能开发中');
		}
function clearCache() {
			dialog.confirm({
				title: '确认',
				message: '确定要清除缓存吗？这将清除本地存储的车辆和用户数据（不影响登录状态）',
				confirmButtonText: '清除',
				confirmButtonColor: '#2b6cb0'
			}).then(() => {
				// 清除业务缓存，保留登录态
				window.localStorage.removeItem('notification_settings');
				toast.success('缓存已清除');
			}).catch(() => {});
		}
function logout() {
			dialog.confirm({
				title: '退出登录',
				message: '确定要退出当前账号吗？',
				confirmButtonText: '确认退出',
				confirmButtonColor: '#2b6cb0'
			}).then(() => {
				window.localStorage.removeItem('token');
				window.localStorage.removeItem('hasLogin');
				window.localStorage.removeItem('userInfo');
				window.localStorage.removeItem('carInfo');
				window.localStorage.removeItem('carList');
				router.push('/');
			}).catch(() => {});
		}
initProfile();
</script>

<style lang="scss" scoped>
.mine-container {
	min-height: 100vh;
	background: var(--bg-page);
	padding-bottom: 80px;
	position: relative;
	overflow-x: hidden;
}

/* ===== Profile Header ===== */
.profile-header {
	padding: 20px 20px 0;
	padding-top: calc(env(safe-area-inset-top, 20px) + 16px);
}

.profile-card {
	display: flex;
	align-items: center;
	gap: 16px;
	padding: 24px;
	background: var(--panel-bg);
	backdrop-filter: blur(var(--panel-blur));
	-webkit-backdrop-filter: blur(var(--panel-blur));
	border: var(--panel-border);
	border-radius: 16px;
	box-shadow: var(--card-shadow);
}

.avatar-area {
	flex-shrink: 0;
}

.avatar-inner {
	width: 60px;
	height: 60px;
	border-radius: 50%;
	overflow: hidden;
	background: rgba(59, 130, 246, 0.08);
	display: flex;
	align-items: center;
	justify-content: center;
}

.avatar-fallback {
	font-size: 24px;
	font-weight: 600;
	color: var(--accent);
}

.profile-info {
	flex: 1;

	.nick-name {
		font-size: 18px;
		font-weight: 600;
		color: var(--text-primary);
		margin-bottom: 4px;
	}

	.phone-num {
		font-size: 13px;
		color: var(--text-tertiary);
	}
}

.profile-edit {
	width: 32px;
	height: 32px;
	display: flex;
	align-items: center;
	justify-content: center;
	color: var(--text-tertiary);
	background: var(--bg-page);
	border-radius: 50%;
	cursor: pointer;
}

/* ===== Stats Row ===== */
.stats-row {
	margin: 20px 20px 0;
	display: flex;
	align-items: center;
	padding: 20px;
	background: var(--panel-bg);
	backdrop-filter: blur(var(--panel-blur));
	-webkit-backdrop-filter: blur(var(--panel-blur));
	border: var(--panel-border);
	border-radius: 16px;
	box-shadow: var(--card-shadow);
}

.stat-card {
	flex: 1;
	text-align: center;

	.stat-value {
		font-size: 20px;
		font-weight: 600;
		color: var(--text-primary);
		line-height: 1.2;
	}

	.stat-label {
		font-size: 11px;
		color: var(--text-tertiary);
		margin-top: 4px;
	}
}

.stat-divider {
	width: 1px;
	height: 24px;
	background: var(--border);
}

/* ===== Menu Sections ===== */
.menu-section {
	padding: 0 20px;
	margin-top: 24px;

	.section-header {
		margin-bottom: 12px;

		.section-label {
			font-size: 14px;
			font-weight: 600;
			color: var(--text-primary);
			letter-spacing: 0.5px;
		}
	}
}

.menu-group {
	background: var(--panel-bg);
	backdrop-filter: blur(var(--panel-blur));
	-webkit-backdrop-filter: blur(var(--panel-blur));
	border: var(--panel-border);
	border-radius: 16px;
	box-shadow: var(--card-shadow);
	overflow: hidden;
}

.menu-item {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 16px;
	transition: background 0.2s;
	cursor: pointer;

	&:not(:last-child) {
		border-bottom: 1px solid var(--border);
	}

	&:active {
		background: var(--bg-page);
	}

	.menu-left {
		display: flex;
		align-items: center;
		gap: 12px;
	}

	.menu-icon {
		width: 32px;
		height: 32px;
		border-radius: 8px;
		display: flex;
		align-items: center;
		justify-content: center;

		&.blue { color: #3b82f6; background: rgba(59, 130, 246, 0.08); }
		&.green { color: #22c55e; background: rgba(34, 197, 94, 0.08); }
		&.orange { color: #f59e0b; background: rgba(245, 158, 11, 0.08); }
		&.purple { color: #8b5cf6; background: rgba(139, 92, 246, 0.08); }
		&.red { color: #ef4444; background: rgba(239, 68, 68, 0.08); }
		&.grey { color: var(--text-secondary); background: rgba(0, 0, 0, 0.04); }
	}

	.menu-name {
		font-size: 14px;
		color: var(--text-primary);
	}

	.menu-right {
		display: flex;
		align-items: center;
		gap: 8px;
	}

	.badge-dot {
		width: 6px;
		height: 6px;
		border-radius: 50%;
		background: #ef4444;
	}

	.menu-arrow {
		color: var(--text-tertiary);
		font-size: 14px;
	}
}

/* ===== Logout ===== */
.logout-section {
	padding: 30px 20px 0;
}

.logout-btn {
	width: 100%;
	padding: 14px;
	text-align: center;
	border: 1px solid rgba(239, 68, 68, 0.2);
	border-radius: 12px;
	background: transparent;
	cursor: pointer;
	transition: all 0.2s;

	&:active {
		background: rgba(239, 68, 68, 0.05);
	}

	.logout-text {
		font-size: 14px;
		font-weight: 500;
		color: #ef4444;
	}
}

/* ===== Animations ===== */
.animate-fade-in {
	opacity: 0;
	animation: fadeIn 0.6s forwards ease-out;
}

@keyframes fadeIn {
	to { opacity: 1; }
}
</style>
