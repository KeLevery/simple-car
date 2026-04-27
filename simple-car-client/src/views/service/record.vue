<template>
	<div class="record-container">
		<van-nav-bar title="维保历史" fixed left-arrow placeholder @click-left="goBack" />

		<div class="car-selector" v-if="carList.length > 0">
			<div class="selector-card" @click="showCarPicker = true">
				<div class="car-info">
					<div class="car-name">{{ carList[chooseIndex].car.carName || '我的车辆' }}</div>
					<div class="car-detail">{{ carList[chooseIndex].car.licenseTag }} · {{ carList[chooseIndex].car.frameNumber }}</div>
				</div>
				<van-icon name="arrow-down" size="14" color="var(--text-tertiary)" />
			</div>
		</div>

		<div class="recordWrap" v-if="carList.length > 0">
			<div class="recordTotal" v-if="historyTotal > 0">共 {{ historyTotal }} 条维保记录</div>
			<van-list v-model="loading" :finished="finished" finished-text="没有更多了" @load="getRecordList">
				<div class="recordCard" v-for="(item, index) in historyArr" :key="index">
					<div class="card-header">
						<div class="work-no">{{ item.workNo }}</div>
						<div class="status-tag" :class="statusClass(item.status)">
							{{ item.status == 0 ? '待支付' : item.status == 1 ? '维保中' : '已完成' }}
						</div>
					</div>
					<div class="card-body">
						<div class="info-row">
							<span class="info-label">维保类型</span>
							<span class="info-value">{{ item.type == 0 ? '常规保养' : '车辆维修' }}</span>
						</div>
						<div class="info-row" v-if="item.deliveryTime">
							<span class="info-label">送修时间</span>
							<span class="info-value">{{ item.deliveryTime }}</span>
						</div>
						<div class="info-row" v-if="item.maintenanceTime">
							<span class="info-label">维保日期</span>
							<span class="info-value">{{ item.maintenanceTime }}</span>
						</div>
						<div class="info-row" v-if="item.appointDate">
							<span class="info-label">预约日期</span>
							<span class="info-value">{{ item.appointDate }} {{ item.appointTime }}</span>
						</div>
						<div class="info-row" v-if="item.payment != null">
							<span class="info-label">维保费用</span>
							<span class="info-value amount">￥{{ item.payment.price.toFixed(2) }}</span>
						</div>
						<div class="info-row" v-else-if="item.totalAmount != null">
							<span class="info-label">维保费用</span>
							<span class="info-value amount">￥{{ Number(item.totalAmount).toFixed(2) }}</span>
						</div>
					</div>
				</div>
			</van-list>
		</div>

		<van-empty v-else image="search" description="暂无车辆维保数据" />

		<!-- 车辆选择弹窗 -->
		<van-action-sheet
			v-model="showCarPicker"
			:actions="carActions"
			cancel-text="取消"
			close-on-click-action
			@select="onSelectCar"
		/>
	</div>
</template>

<script>
	import {
		carInfoList,
		appointmentList
	} from '@/api/service'
	export default {
		data() {
			return {
				chooseIndex: 0,
				historyArr: [],
				historyTotal: 0,
				carList: [],
				carId: 0,
				loading: false,
				finished: false,
				pageNum: 1,
				showCarPicker: false
			}
		},
		computed: {
			carActions() {
				return this.carList.map((item, index) => ({
					name: item.car.carName + ' (' + item.car.licenseTag + ')',
					index: index,
					color: index === this.chooseIndex ? 'var(--accent)' : undefined
				}));
			}
		},
		created() {
			this.loadCarList();
		},
		methods: {
			loadCarList() {
				try {
					const userInfoStr = window.localStorage.getItem('userInfo');
					if (!userInfoStr) {
						this.$toast('请先登录');
						this.finished = true;
						return;
					}
					const userInfo = JSON.parse(userInfoStr);
					const userId = userInfo.userId || userInfo.id;
					if (!userId) {
						this.$toast('用户信息异常');
						this.finished = true;
						return;
					}
					carInfoList(userId).then(res => {
						if (res.data && res.data.length > 0) {
							this.carList = res.data;
							// Use the first car's carId by default
							this.carId = res.data[0].car.carId || res.data[0].carID;
							this.chooseIndex = 0;
							// Trigger the list loading
							this.getRecordList();
						} else {
							this.finished = true;
						}
					}).catch(err => {
						console.error('Failed to load car list:', err);
						this.$toast('加载车辆信息失败');
						this.finished = true;
					});
				} catch (e) {
					console.error('Failed to load car info:', e);
					this.$toast('加载车辆信息失败');
					this.finished = true;
				}
			},
			onSelectCar(item) {
				this.chooseIndex = item.index;
				this.carId = this.carList[item.index].car.carId || this.carList[item.index].carID;
				// Reset and reload
				this.historyArr = [];
				this.historyTotal = 0;
				this.pageNum = 1;
				this.finished = false;
				this.loading = false;
				this.getRecordList();
			},
			getRecordList() {
				if (!this.carId) {
					this.finished = true;
					return;
				}
				appointmentList(this.carId, this.pageNum).then(res => {
					if (res.code == 200) {
						if (res.total > 0) {
							this.historyTotal = res.total;
							this.historyArr = this.historyArr.concat(res.rows);
							this.pageNum++;
						}
						this.loading = false;
						if (this.historyArr.length >= res.total) {
							this.finished = true;
						}
					} else {
						this.loading = false;
						this.finished = true;
					}
				}).catch(err => {
					console.error('Failed to load records:', err);
					this.loading = false;
					this.finished = true;
				});
			},
			statusClass(status) {
				if (status == 0) return 'pending';
				if (status == 1) return 'processing';
				return 'done';
			},
			goBack() {
				this.$router.go(-1);
			}
		}
	}
</script>

<style lang="scss" scoped>
.record-container {
	min-height: 100vh;
	background-color: var(--bg-page);

	:deep(.van-nav-bar) {
		background: var(--bg-glass);
		backdrop-filter: blur(var(--card-blur));
		border-bottom: 1px solid var(--border);
		.van-nav-bar__title { color: var(--text-primary); font-weight: 500; }
		.van-icon, .van-nav-bar__text { color: var(--accent); }
	}
}

.car-selector {
	padding: 16px 16px 0;

	.selector-card {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 16px 20px;
		background: var(--bg-card);
		border-radius: 12px;
		box-shadow: var(--card-shadow);
		cursor: pointer;
		transition: all 0.2s;

		&:active {
			transform: scale(0.98);
		}

		.car-info {
			.car-name {
				font-size: 16px;
				font-weight: 600;
				color: var(--text-primary);
				margin-bottom: 4px;
			}
			.car-detail {
				font-size: 12px;
				color: var(--text-tertiary);
			}
		}
	}
}

.recordWrap {
	padding: 16px;

	.recordTotal {
		font-size: 13px;
		font-weight: 400;
		text-align: left;
		padding-bottom: 12px;
		color: var(--text-tertiary);
	}

	.recordCard {
		background-color: var(--bg-card);
		border-radius: 12px;
		box-shadow: var(--card-shadow);
		margin-bottom: 12px;
		overflow: hidden;

		.card-header {
			display: flex;
			align-items: center;
			justify-content: space-between;
			padding: 14px 16px;
			border-bottom: 1px solid var(--border);

			.work-no {
				font-size: 14px;
				font-weight: 500;
				color: var(--text-primary);
			}

			.status-tag {
				font-size: 11px;
				font-weight: 500;
				padding: 3px 10px;
				border-radius: 20px;

				&.pending {
					color: #f59e0b;
					background: rgba(245, 158, 11, 0.1);
				}
				&.processing {
					color: #3b82f6;
					background: rgba(59, 130, 246, 0.1);
				}
				&.done {
					color: #22c55e;
					background: rgba(34, 197, 94, 0.1);
				}
			}
		}

		.card-body {
			padding: 12px 16px;

			.info-row {
				display: flex;
				justify-content: space-between;
				align-items: center;
				padding: 6px 0;

				.info-label {
					font-size: 13px;
					color: var(--text-tertiary);
				}

				.info-value {
					font-size: 13px;
					color: var(--text-primary);
					font-weight: 400;

					&.amount {
						color: var(--accent);
						font-weight: 600;
					}
				}
			}
		}
	}
}
</style>
