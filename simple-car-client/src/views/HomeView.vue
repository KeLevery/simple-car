<template>
	<div class="home-page">
		<!-- Header -->
		<div class="home-header">
			<div class="header-left">
				<div class="greeting">{{ greeting }}</div>
				<div class="car-name" @click="showCarPicker = true">
					{{ carInfos.carName || '我的车辆' }}
					<van-icon name="arrow-down" size="12" class="picker-arrow" />
				</div>
			</div>
			<div class="header-right" @click="gotoPage('/notice')">
				<van-icon name="bell" size="20" color="var(--text-secondary)" />
			</div>
		</div>

		<!-- Vehicle Status Card -->
		<div class="status-card animate-in stagger-1">
			<div class="battery-area">
				<div class="battery-ring">
					<svg viewBox="0 0 120 120" width="120" height="120">
						<circle cx="60" cy="60" r="52" fill="none" stroke="#edf2f7" stroke-width="6"/>
						<circle cx="60" cy="60" r="52" fill="none" stroke="var(--accent)" stroke-width="6"
							stroke-linecap="round"
							:stroke-dasharray="dashArray"
							:stroke-dashoffset="dashOffset"
							transform="rotate(-90 60 60)"
							style="transition: stroke-dashoffset 1s ease"
						/>
					</svg>
					<div class="battery-center">
						<div class="battery-num">{{ batteryPer }}</div>
						<div class="battery-unit">%</div>
					</div>
				</div>
			</div>
			<div class="stats-area">
				<div class="stat-item">
					<div class="stat-val">{{ enduranceMileage }}<span class="stat-u">km</span></div>
					<div class="stat-lbl">续航里程</div>
				</div>
				<div class="stat-sep"></div>
				<div class="stat-item">
					<div class="stat-val">{{ carInfos.totalMileage || 0 }}<span class="stat-u">km</span></div>
					<div class="stat-lbl">总里程</div>
				</div>
				<div class="stat-sep"></div>
				<div class="stat-item">
					<div class="stat-val">{{ carInfos.interiorTemp || '--' }}<span class="stat-u">°C</span></div>
					<div class="stat-lbl">车内温度</div>
				</div>
			</div>
		</div>

		<!-- Quick Controls -->
		<div class="section animate-in stagger-2">
			<div class="section-title">快捷控制</div>
			<div class="control-grid">
				<div 
					v-for="(item, index) in controlList" 
					:key="index"
					class="control-item"
					:class="{ active: controlAct === index }"
					@click="doorTips(item.label + '成功', index)"
				>
					<div class="control-icon">
						<img :src="controlAct === index ? item.actSrc : item.defaultSrc" />
					</div>
					<div class="control-label">{{ item.label }}</div>
				</div>
			</div>
		</div>

		<!-- Function Entry -->
		<div class="section animate-in stagger-3">
			<div class="section-title">车辆服务</div>
			<div class="func-card" @click="gotoPage('/control')">
				<div class="func-info">
					<div class="func-icon-box">
						<img src="@/assets/control/control.png" />
					</div>
					<div class="func-text">
						<div class="func-name">远程车辆控制</div>
						<div class="func-desc">引擎启动 · 空调预冷 · 车辆寻找</div>
					</div>
				</div>
				<van-icon name="arrow" color="var(--text-tertiary)" />
			</div>
		</div>

		<!-- 车辆选择弹窗 -->
		<van-action-sheet
			v-model="showCarPicker"
			:actions="carActions"
			cancel-text="取消"
			close-on-click-action
			@select="onSelectCar"
		/>

		<SmartAssistant />
		<Tabbar active="/home" />
	</div>
</template>

<script>
import Tabbar from "@/components/Tabbar.vue"
import SmartAssistant from "@/components/SmartAssistant.vue"
import { carInfo } from '@/api/carInfo'

export default {
	name: 'home',
	components: { Tabbar, SmartAssistant },
	data() {
		return {
			carInfos: {},
			carId: 0,
			carList: [],
			showCarPicker: false,
			controlAct: null,
			controlList: [
				{ label: '车门解锁', defaultSrc: require('../assets/unlock.png'), actSrc: require('../assets/unlock_act.png') },
				{ label: '车门上锁', defaultSrc: require('../assets/locked.png'), actSrc: require('../assets/locked_act.png') },
				{ label: '关闭车窗', defaultSrc: require('../assets/closeWindow.png'), actSrc: require('../assets/closeWindow_act.png') },
				{ label: '关闭后备箱', defaultSrc: require('../assets/closeTrunk.png'), actSrc: require('../assets/closeTrunk_act.png') },
			],
			enduranceMileage: 0,
			batteryPer: 0,
		}
	},
	computed: {
		greeting() {
			const h = new Date().getHours();
			if (h < 6) return '夜深了';
			if (h < 12) return '早上好';
			if (h < 14) return '中午好';
			if (h < 18) return '下午好';
			return '晚上好';
		},
		carActions() {
			const list = this.carList.map(car => ({
				name: car.carName + ' (' + car.licenseTag + ')',
				carId: car.carId,
				color: car.carId === this.carId ? 'var(--accent)' : undefined
			}));
			list.push({ name: '＋ 添加新车辆', carId: '__ADD__', color: 'var(--accent)' });
			return list;
		},
		dashArray() {
			return 2 * Math.PI * 52;
		},
		dashOffset() {
			const pct = Math.max(0, Math.min(100, this.batteryPer));
			return this.dashArray * (1 - pct / 100);
		}
	},
	created() {
		this.initCarData();
		this.checkLogin();
	},
	activated() {
		// Refresh car list when returning (e.g. after adding a car)
		this.initCarData();
	},
	methods: {
		initCarData() {
			const carInfoLocal = window.localStorage.getItem('carInfo');
			if (carInfoLocal) {
				this.carId = JSON.parse(carInfoLocal).carId;
			}
			const carListLocal = window.localStorage.getItem('carList');
			if (carListLocal) {
				this.carList = JSON.parse(carListLocal);
			}
		},
		onSelectCar(item) {
			if (item.carId === '__ADD__') {
				this.$router.push('/mine/addCar');
				return;
			}
			this.carId = item.carId;
			const selectedCar = this.carList.find(c => c.carId === item.carId);
			window.localStorage.setItem('carInfo', JSON.stringify(selectedCar));
			this.getCarInfo();
			this.$toast.success('已切换至 ' + selectedCar.carName);
		},
		gotoPage(path) {
			this.$router.push({ path })
		},
		doorTips(text, num) {
			if (this.controlAct === num) {
				this.controlAct = null;
			} else {
				this.$notify({ type: 'success', message: text });
				this.controlAct = num;
			}
		},
		checkLogin() {
			if (!window.localStorage.getItem('hasLogin')) {
				this.$router.push({ path: '/' })
			} else {
				this.getCarInfo();
			}
		},
		getCarInfo() {
			if (!this.carId) return;
			carInfo(this.carId).then(res => {
				if (res.code == 200) {
					this.carInfos = res.data;
					this.enduranceMileage = this.carInfos.enduranceMileage || 0;
					this.batteryPer = res.data.remainingPower || 0;
				}
			});
		},
	}
};
</script>

<style lang="scss" scoped>
.home-page {
	min-height: 100vh;
	background: var(--bg-page);
	padding-bottom: 72px;
}

/* Header */
.home-header {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	padding: 16px 20px;
	padding-top: calc(env(safe-area-inset-top, 20px) + 12px);
	background: var(--bg-primary);

	.greeting {
		font-size: 13px;
		color: var(--text-tertiary);
		margin-bottom: 4px;
	}

	.car-name {
		font-size: 20px;
		font-weight: 600;
		color: var(--text-primary);
		display: flex;
		align-items: center;
		gap: 6px;
		cursor: pointer;

		.picker-arrow {
			color: var(--text-tertiary);
		}
	}

	.header-right {
		padding: 8px;
	}
}

/* Status Panel */
.status-card {
	margin: 0 16px;
	background: var(--panel-bg);
	backdrop-filter: blur(var(--panel-blur));
	-webkit-backdrop-filter: blur(var(--panel-blur));
	border: var(--panel-border);
	border-radius: 16px;
	box-shadow: var(--card-shadow-md);
	padding: 24px;
	display: flex;
	align-items: center;
	gap: 24px;
}

.battery-area {
	flex-shrink: 0;
}

.battery-ring {
	position: relative;
	width: 120px;
	height: 120px;

	.battery-center {
		position: absolute;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
		text-align: center;
		display: flex;
		align-items: baseline;
		gap: 1px;

		.battery-num {
			font-size: 32px;
			font-weight: 300;
			color: var(--text-primary);
			font-family: var(--font-mono);
			line-height: 1;
		}

		.battery-unit {
			font-size: 14px;
			color: var(--text-tertiary);
		}
	}
}

.stats-area {
	flex: 1;
	display: flex;
	flex-direction: column;
	gap: 4px;
}

.stat-item {
	padding: 6px 0;

	.stat-val {
		font-size: 18px;
		font-weight: 500;
		color: var(--text-primary);
		line-height: 1.3;

		.stat-u {
			font-size: 11px;
			color: var(--text-tertiary);
			margin-left: 2px;
			font-weight: 400;
		}
	}

	.stat-lbl {
		font-size: 11px;
		color: var(--text-tertiary);
		margin-top: 1px;
	}
}

.stat-sep {
	height: 1px;
	background: var(--border);
}

/* Section */
.section {
	padding: 0 16px;
	margin-top: 20px;
}

.section-title {
	font-size: 16px;
	font-weight: 600;
	color: var(--text-primary);
	margin-bottom: 12px;
	padding-left: 4px;
}

/* Controls */
.control-grid {
	display: flex;
	gap: 10px;
}

.control-item {
	flex: 1;
	background: var(--panel-bg);
	backdrop-filter: blur(var(--panel-blur));
	-webkit-backdrop-filter: blur(var(--panel-blur));
	border: var(--panel-border);
	border-radius: 12px;
	padding: 16px 8px;
	text-align: center;
	box-shadow: var(--card-shadow);
	transition: all 0.2s ease;
	cursor: pointer;

	&:active {
		transform: scale(0.97);
	}

	&.active {
		background: var(--accent-light);
		box-shadow: none;

		.control-label {
			color: var(--accent);
		}
	}

	.control-icon {
		margin-bottom: 8px;

		img {
			width: 24px;
			height: 24px;
		}
	}

	.control-label {
		font-size: 11px;
		color: var(--text-secondary);
		font-weight: 400;
	}
}

/* Function Card */
.func-card {
	display: flex;
	justify-content: space-between;
	align-items: center;
	background: var(--panel-bg);
	backdrop-filter: blur(var(--panel-blur));
	-webkit-backdrop-filter: blur(var(--panel-blur));
	border: var(--panel-border);
	border-radius: 12px;
	padding: 16px;
	box-shadow: var(--card-shadow);
	cursor: pointer;
	transition: all 0.2s;

	&:active {
		transform: scale(0.99);
	}

	.func-info {
		display: flex;
		align-items: center;
		gap: 14px;
	}

	.func-icon-box {
		width: 44px;
		height: 44px;
		border-radius: 10px;
		background: var(--accent-light);
		display: flex;
		align-items: center;
		justify-content: center;

		img {
			width: 22px;
		}
	}

	.func-name {
		font-size: 15px;
		font-weight: 500;
		color: var(--text-primary);
		margin-bottom: 3px;
	}

	.func-desc {
		font-size: 12px;
		color: var(--text-tertiary);
	}
}

/* Animation */
.animate-in {
	opacity: 0;
	transform: translateY(12px);
	animation: enterUp 0.5s ease forwards;
}

.stagger-1 { animation-delay: 0.05s; }
.stagger-2 { animation-delay: 0.1s; }
.stagger-3 { animation-delay: 0.15s; }

@keyframes enterUp {
	to { opacity: 1; transform: translateY(0); }
}
</style>
