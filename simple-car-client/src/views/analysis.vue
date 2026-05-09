<template>
	<div class="analysis-container">
		<!-- Header -->
		<div class="page-header">
			<div class="header-top">
				<span class="header-label">DATA ANALYSIS</span>
				<span class="update-time">
					{{ updateTime ? updateTime.substring(0, 16) : 'LOADING...' }}
				</span>
			</div>
			<h1 class="page-title">用车报告</h1>
			<p class="page-subtitle">Vehicle Usage Analytics & Insights</p>
		</div>

		<!-- Summary Cards -->
		<div class="summary-row animate-enter stagger-1">
			<div class="summary-card">
				<div class="summary-icon-dot blue"></div>
				<div class="summary-data">
					<div class="summary-val">{{ totalChargeCount }}</div>
					<div class="summary-lbl">充电次数</div>
				</div>
			</div>
			<div class="summary-card">
				<div class="summary-icon-dot green"></div>
				<div class="summary-data">
					<div class="summary-val">{{ totalCharged }}</div>
					<div class="summary-lbl">总充电量</div>
				</div>
			</div>
			<div class="summary-card">
				<div class="summary-icon-dot amber"></div>
				<div class="summary-data">
					<div class="summary-val">{{ totalPay }}</div>
					<div class="summary-lbl">总花费</div>
				</div>
			</div>
		</div>

		<!-- Chart: Monthly Trend -->
		<div class="chart-section animate-enter stagger-2">
			<div class="section-header">
				<span class="section-label">CHARGING TREND</span>
			</div>
			<div class="chart-card">
				<div class="chart-box" ref="firstChartEl"></div>
			</div>
		</div>

		<!-- Chart: Consumption & Mileage -->
		<div class="chart-section animate-enter stagger-3">
			<div class="section-header">
				<span class="section-label">CONSUMPTION & MILEAGE</span>
			</div>
			<div class="chart-card">
				<div class="chart-box" ref="secondChartEl"></div>
			</div>
		</div>
		
		<Tabbar active="/analysis" />
	</div>
</template>

<script setup>
import {
	chargeOrderList,
	mileageList
} from '@/api/analysis';
import { graphic, init as initEcharts } from '@/util/echarts';
import Tabbar from "@/components/Tabbar.vue"
import { nextTick, ref } from 'vue'

const firstChartEl = ref(null)
const secondChartEl = ref(null)
const active = ref('/analysis')
const updateTime = ref('')
const MarchTotal = ref(0)
const MayTotal = ref(0)
const totalChargeCount = ref(0)
const totalCharged = ref('0.0')
const totalPay = ref('0.0')
const option = ref({
				backgroundColor: 'transparent',
				tooltip: {
					trigger: 'axis',
					axisPointer: { type: 'shadow' },
					backgroundColor: '#ffffff',
					borderColor: '#e2e8f0',
					borderWidth: 1,
					textStyle: { color: '#1a202c', fontSize: 11 }
				},
				legend: {
					data: ['充电次数', '充电量', '花费'],
					bottom: 10,
					itemWidth: 8,
					itemHeight: 8,
					textStyle: { fontSize: 10, color: '#718096' }
				},
				color: ['#3b82f6', '#22c55e', '#f59e0b'],
				xAxis: {
					type: 'category',
					data: ['01月', '02月', '03月', '04月', '05月', '06月'],
					axisTick: { show: false },
					axisLine: { lineStyle: { color: '#e2e8f0' } },
					axisLabel: { color: '#718096', fontSize: 10 }
				},
				grid: {
					left: '3%',
					right: '4%',
					bottom: '15%',
					top: '15%',
					containLabel: true
				},
				yAxis: {
					type: 'value',
					splitLine: { lineStyle: { type: 'dashed', color: '#edf2f7' } },
					axisLabel: { color: '#718096', fontSize: 10 }
				},
				series: [
					{
						name: '充电次数', type: 'bar', barWidth: 6, data: [],
						itemStyle: {
							borderRadius: [3, 3, 0, 0],
							color: new graphic.LinearGradient(0, 0, 0, 1, [
								{ offset: 0, color: '#3b82f6' },
								{ offset: 1, color: 'rgba(59, 130, 246, 0.1)' }
							])
						}
					},
					{
						name: '充电量', type: 'bar', barWidth: 6, data: [],
						itemStyle: {
							borderRadius: [3, 3, 0, 0],
							color: new graphic.LinearGradient(0, 0, 0, 1, [
								{ offset: 0, color: '#22c55e' },
								{ offset: 1, color: 'rgba(34, 197, 94, 0.1)' }
							])
						}
					},
					{
						name: '花费', type: 'bar', barWidth: 6, data: [],
						itemStyle: {
							borderRadius: [3, 3, 0, 0],
							color: new graphic.LinearGradient(0, 0, 0, 1, [
								{ offset: 0, color: '#f59e0b' },
								{ offset: 1, color: 'rgba(245, 158, 11, 0.1)' }
							])
						}
					}
				]
			})
const option2 = ref({
				backgroundColor: 'transparent',
				tooltip: {
					trigger: 'axis',
					backgroundColor: '#ffffff',
					borderColor: '#e2e8f0',
					borderWidth: 1,
					textStyle: { color: '#1a202c', fontSize: 11 }
				},
				legend: {
					data: ['耗电量', '行驶里程'],
					bottom: 10,
					itemWidth: 8,
					itemHeight: 8,
					textStyle: { fontSize: 10, color: '#718096' }
				},
				color: ['#3b82f6', '#f59e0b'],
				grid: {
					left: '3%',
					right: '4%',
					bottom: '15%',
					top: '15%',
					containLabel: true
				},
				xAxis: {
					type: 'category',
					boundaryGap: false,
					data: ['3月', '5月'],
					axisTick: { show: false },
					axisLine: { lineStyle: { color: '#e2e8f0' } },
					axisLabel: { color: '#718096', fontSize: 10 }
				},
				yAxis: {
					type: 'value',
					splitLine: { lineStyle: { type: 'dashed', color: '#edf2f7' } },
					axisLabel: { color: '#718096', fontSize: 10 }
				},
				series: [
					{
						name: '耗电量', type: 'line', smooth: true, symbol: 'circle', symbolSize: 6,
						lineStyle: { width: 2 },
						areaStyle: {
							color: new graphic.LinearGradient(0, 0, 0, 1, [
								{ offset: 0, color: 'rgba(59, 130, 246, 0.1)' },
								{ offset: 1, color: 'rgba(59, 130, 246, 0)' }
							])
						},
						data: []
					},
					{
						name: '行驶里程', type: 'line', smooth: true, symbol: 'circle', symbolSize: 6,
						lineStyle: { width: 2 },
						areaStyle: {
							color: new graphic.LinearGradient(0, 0, 0, 1, [
								{ offset: 0, color: 'rgba(245, 158, 11, 0.1)' },
								{ offset: 1, color: 'rgba(245, 158, 11, 0)' }
							])
						},
						data: []
					}
				]
			})
function initData() {
			getMileageList();
		}
function getMileageList() {
			const carInfoLocal = window.localStorage.getItem('carInfo');
			if (!carInfoLocal) return;
			let carId = JSON.parse(carInfoLocal).carId;

			mileageList({ carId }).then(res => {
				if (res.code === 200 && res.data && res.data.length > 0) {
					updateTime.value = res.data[0].createTime;

					// 动态按月聚合里程数据
					const monthlyMileage = {};
					res.data.forEach(item => {
						const month = item.createTime.substring(0, 7); // YYYY-MM
						if (!monthlyMileage[month]) {
							monthlyMileage[month] = [];
						}
						monthlyMileage[month].push(item);
					});

					// 取最近两个有数据的月份用于第二个图表
					const months = Object.keys(monthlyMileage).sort();
					if (months.length >= 2) {
						const month1 = months[months.length - 2];
						const month2 = months[months.length - 1];
						const prevMonth = months[months.length - 3] || month1;

						MarchTotal.value = monthlyMileage[month1].length > 0 && monthlyMileage[prevMonth] && monthlyMileage[prevMonth].length > 0
							? (monthlyMileage[month1][0].carMileage - monthlyMileage[prevMonth][0].carMileage).toFixed(2) : 0;
						MayTotal.value = monthlyMileage[month2].length > 0 && monthlyMileage[month1].length > 0
							? (monthlyMileage[month2][0].carMileage - monthlyMileage[month1][0].carMileage).toFixed(2) : 0;

						option2.value.xAxis.data = [month1.substring(5) + '月', month2.substring(5) + '月'];
						option2.value.series[1].data[0] = MarchTotal.value;
						option2.value.series[1].data[1] = MayTotal.value;
					}

					getOrderList();
				}
			});
		}
function getOrderList() {
			const carInfoLocal = window.localStorage.getItem('carInfo');
			if (!carInfoLocal) return;
			let carId = JSON.parse(carInfoLocal).carId;

			chargeOrderList({ carId, chargeOrderList: 3 }).then(res => {
				if (res.code === 200 && res.data) {
					// 动态按月聚合充电数据
					const monthlyData = {};
					res.data.forEach(item => {
						if (item.createTime) {
							const month = item.createTime.substring(0, 7); // YYYY-MM
							if (!monthlyData[month]) {
								monthlyData[month] = { count: 0, quantity: 0, pay: 0 };
							}
							monthlyData[month].count += 1;
							monthlyData[month].quantity += item.chargedQuantity;
							monthlyData[month].pay += item.actualPaymentAmount;
						}
					});

					// 取最近6个月数据
					const months = Object.keys(monthlyData).sort();
					const recentMonths = months.slice(-6);
					const chartData = recentMonths.map(m => monthlyData[m] || { count: 0, quantity: 0, pay: 0 });

					// 更新X轴标签
					option.value.xAxis.data = recentMonths.map(m => m.substring(5) + '月');

					const times = chartData.map(item => item.count);
					const chargedQuantity = chartData.map(item => item.quantity.toFixed(1));
					const actualPay = chartData.map(item => item.pay.toFixed(1));

					// Summary stats
					totalChargeCount.value = times.reduce((a, b) => a + b, 0);
					totalCharged.value = chartData.reduce((a, b) => a + b.quantity, 0).toFixed(1);
					totalPay.value = chartData.reduce((a, b) => a + b.pay, 0).toFixed(1);

					option.value.series[0].data = times;
					option.value.series[1].data = chargedQuantity;
					option.value.series[2].data = actualPay;

					// 第二个图表取最近两个月的耗电量
					if (recentMonths.length >= 2) {
						option2.value.series[0].data = [
							chartData[chartData.length - 2].quantity.toFixed(1),
							chartData[chartData.length - 1].quantity.toFixed(1)
						];
					}

					nextTick(() => {
						initCharts();
					});
				}
			});
		}
function initCharts() {
			initChart('firstChartEl', option.value);
			initChart('secondChartEl', option2.value);
		}
function initChart(refName, option) {
			const chartEl = refName === 'firstChartEl' ? firstChartEl.value : secondChartEl.value;
			if (chartEl) {
				const chart = initEcharts(chartEl);
				chart.setOption(option);
			}
		}
initData();
</script>

<style scoped lang="scss">
.analysis-container {
	min-height: 100vh;
	background: var(--bg-page, #f8fafc);
	padding-bottom: 80px;
	position: relative;
}

/* ===== Header ===== */
.page-header {
	position: relative;
	padding: 24px 20px 0;
	padding-top: calc(env(safe-area-inset-top, 20px) + 24px);

	.header-top {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 12px;
	}

	.header-label {
		font-size: 11px;
		color: var(--text-tertiary, #94a3b8);
		font-weight: 500;
	}

	.update-time {
		font-size: 11px;
		color: var(--text-tertiary, #94a3b8);
	}

	.page-title {
		font-size: 24px;
		font-weight: 600;
		color: var(--text-primary, #1e293b);
		margin: 0;
	}

	.page-subtitle {
		font-size: 13px;
		color: var(--text-secondary, #64748b);
		margin: 4px 0 0;
	}
}

/* ===== Summary Cards ===== */
.summary-row {
	display: flex;
	gap: 12px;
	padding: 24px 20px 0;
}

.summary-card {
	flex: 1;
	background: var(--bg-card, #ffffff);
	border-radius: 12px;
	padding: 16px 12px;
	display: flex;
	align-items: center;
	gap: 10px;
	box-shadow: var(--card-shadow, 0 4px 6px -1px rgba(0, 0, 0, 0.1));

	.summary-icon-dot {
		width: 8px;
		height: 8px;
		border-radius: 50%;
		flex-shrink: 0;

		&.blue { background: #3b82f6; }
		&.green { background: #22c55e; }
		&.amber { background: #f59e0b; }
	}

	.summary-val {
		font-size: 18px;
		font-weight: 600;
		color: var(--text-primary, #1e293b);
		line-height: 1.2;
	}

	.summary-lbl {
		font-size: 12px;
		color: var(--text-secondary, #64748b);
		margin-top: 2px;
	}
}

/* ===== Chart Sections ===== */
.chart-section {
	padding: 0 20px;
	margin-top: 32px;
}

.section-header {
	margin-bottom: 12px;

	.section-label {
		font-size: 16px;
		font-weight: 600;
		color: var(--text-primary, #1e293b);
	}
}

.chart-card {
	background: var(--bg-card, #ffffff);
	border-radius: 12px;
	padding: 20px 12px;
	box-shadow: var(--card-shadow, 0 4px 6px -1px rgba(0, 0, 0, 0.1));
	border: 1px solid var(--border, #e2e8f0);
	
	.chart-box {
		height: 260px;
		width: 100%;
	}
}

/* ===== Animations ===== */
.animate-enter {
	opacity: 0;
	transform: translateY(10px);
	animation: enterUp 0.6s forwards ease-out;
}

.stagger-1 { animation-delay: 0.1s; }
.stagger-2 { animation-delay: 0.2s; }
.stagger-3 { animation-delay: 0.3s; }

@keyframes enterUp {
	to { opacity: 1; transform: translateY(0); }
}
</style>
