<template>
	<div class="add-car-container">
		<van-nav-bar
			title="添加车辆"
			left-arrow
			fixed
			placeholder
			@click-left="$router.back()"
		/>

		<div class="form-section">
			<div class="section-header">
				<span class="section-label">车辆信息</span>
				<span class="section-desc">请填写您要添加的车辆信息</span>
			</div>

			<van-form @submit="onSubmit" validate-first>
				<div class="form-card">
					<van-field
						v-model="form.carName"
						label="品牌名称"
						placeholder="例如：极氪 001"
						required
						:rules="[{ required: true, message: '请输入品牌名称' }]"
						input-align="right"
					/>
					<van-field
						v-model="form.carModels"
						label="车辆型号"
						placeholder="例如：YOU 100kWh"
						required
						:rules="[{ required: true, message: '请输入车辆型号' }]"
						input-align="right"
					/>
					<van-field
						v-model="form.licenseTag"
						label="车牌号"
						placeholder="例如：苏A·12345"
						required
						:rules="[{ required: true, message: '请输入车牌号' }]"
						input-align="right"
					/>
					<van-field
						v-model="form.frameNumber"
						label="车架号(VIN)"
						placeholder="17位车架号"
						required
						maxlength="17"
						:rules="[
							{ required: true, message: '请输入车架号' },
							{ validator: vinValidator, message: '车架号应为17位字母和数字组合' }
						]"
						input-align="right"
					/>
				</div>

				<div class="tips-card">
					<div class="tips-title">
						<van-icon name="info-o" size="14" />
						<span>温馨提示</span>
					</div>
					<ul class="tips-list">
						<li>车架号(VIN)可在行驶证、车辆铭牌或前挡风玻璃处查看</li>
						<li>添加车辆后可在首页切换查看不同车辆信息</li>
						<li>车牌号请按照实际格式输入，包含省份简称</li>
					</ul>
				</div>

				<div class="submit-area">
					<van-button
						round
						block
						type="info"
						native-type="submit"
						:loading="submitting"
						loading-text="添加中..."
					>
						确认添加
					</van-button>
				</div>
			</van-form>
		</div>
	</div>
</template>

<script>
import { addCar, carInfoList } from '@/api/service'

export default {
	data() {
		return {
			submitting: false,
			form: {
				carName: '',
				carModels: '',
				licenseTag: '',
				frameNumber: ''
			}
		}
	},
	methods: {
		vinValidator(val) {
			return /^[A-HJ-NPR-Z0-9]{17}$/i.test(val);
		},
		async onSubmit() {
			this.submitting = true;
			try {
				const userInfoStr = window.localStorage.getItem('userInfo');
				if (!userInfoStr) {
					this.$toast('请先登录');
					this.submitting = false;
					return;
				}
				const userInfo = JSON.parse(userInfoStr);
				const userId = userInfo.userId || userInfo.id;

				const res = await addCar({
					userId: userId,
					carName: this.form.carName,
					carModels: this.form.carModels,
					licenseTag: this.form.licenseTag,
					frameNumber: this.form.frameNumber.toUpperCase()
				});

				if (res.code === 200) {
					// Refresh the carList in localStorage
					try {
						const listRes = await carInfoList(userId);
						if (listRes.data) {
							window.localStorage.setItem('carList', JSON.stringify(listRes.data));
							// If this is the first car, also set it as carInfo
							if (listRes.data.length === 1) {
								const firstCar = listRes.data[0];
								window.localStorage.setItem('carInfo', JSON.stringify({
									carId: firstCar.car.carId,
									carName: firstCar.car.carName,
									licenseTag: firstCar.car.licenseTag
								}));
							}
						}
					} catch (e) {
						console.error('Failed to refresh car list:', e);
					}

					this.$toast.success('车辆添加成功');
					setTimeout(() => {
						this.$router.back();
					}, 1000);
				} else {
					this.$toast.fail(res.msg || '添加失败');
				}
			} catch (e) {
				console.error('Failed to add car:', e);
				this.$toast.fail('网络异常，请稍后重试');
			} finally {
				this.submitting = false;
			}
		}
	}
}
</script>

<style lang="scss" scoped>
.add-car-container {
	min-height: 100vh;
	background: var(--bg-page);

	:deep(.van-nav-bar) {
		background: var(--bg-glass);
		backdrop-filter: blur(var(--card-blur));
		border-bottom: 1px solid var(--border);
		.van-nav-bar__title { color: var(--text-primary); font-weight: 500; }
		.van-icon, .van-nav-bar__text { color: var(--accent); }
	}
}

.form-section {
	padding: 16px;
}

.section-header {
	margin-bottom: 16px;

	.section-label {
		font-size: 18px;
		font-weight: 600;
		color: var(--text-primary);
		display: block;
		margin-bottom: 4px;
	}

	.section-desc {
		font-size: 13px;
		color: var(--text-tertiary);
	}
}

.form-card {
	background: var(--bg-card);
	border-radius: 12px;
	box-shadow: var(--card-shadow);
	overflow: hidden;

	:deep(.van-cell) {
		background: transparent;
		color: var(--text-primary);

		.van-field__label {
			color: var(--text-secondary);
			width: 90px;
		}

		.van-field__control {
			color: var(--text-primary);
		}

		&::after {
			border-color: var(--border);
		}
	}
}

.tips-card {
	margin-top: 16px;
	padding: 16px;
	background: rgba(59, 130, 246, 0.04);
	border-radius: 12px;
	border: 1px solid rgba(59, 130, 246, 0.1);

	.tips-title {
		display: flex;
		align-items: center;
		gap: 6px;
		font-size: 13px;
		font-weight: 500;
		color: var(--accent);
		margin-bottom: 10px;
	}

	.tips-list {
		margin: 0;
		padding-left: 18px;

		li {
			font-size: 12px;
			color: var(--text-tertiary);
			line-height: 1.8;
		}
	}
}

.submit-area {
	margin-top: 32px;
	padding: 0 4px;

	:deep(.van-button--info) {
		background: var(--accent-gradient);
		border: none;
		box-shadow: 0 4px 12px var(--accent-glow);
		font-weight: 500;
	}
}
</style>
