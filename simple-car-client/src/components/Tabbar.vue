<template>
	<van-tabbar v-model="activePath" route :border="false" placeholder class="custom-tabbar">
		<van-tabbar-item to="/community">
			<span class="tab-label">社区</span>
			<template #icon="props">
				<div class="tab-icon-wrap" :class="{ active: props.active }">
					<van-icon name="chat-o" />
				</div>
			</template>
		</van-tabbar-item>
		<van-tabbar-item to="/analysis">
			<span class="tab-label">分析</span>
			<template #icon="props">
				<div class="tab-icon-wrap" :class="{ active: props.active }">
					<van-icon name="chart-trending-o" />
				</div>
			</template>
		</van-tabbar-item>
		<van-tabbar-item to="/home" class="home-tab-item">
			<template #icon="props">
				<div class="home-btn" :class="{ active: props.active }">
					<van-icon name="wap-home" size="24" />
				</div>
			</template>
		</van-tabbar-item>
		<van-tabbar-item to="/service">
			<span class="tab-label">服务</span>
			<template #icon="props">
				<div class="tab-icon-wrap" :class="{ active: props.active }">
					<van-icon name="service-o" />
				</div>
			</template>
		</van-tabbar-item>
		<van-tabbar-item to="/mine">
			<span class="tab-label">我的</span>
			<template #icon="props">
				<div class="tab-icon-wrap" :class="{ active: props.active }">
					<van-icon name="user-o" />
				</div>
			</template>
		</van-tabbar-item>
	</van-tabbar>
</template>

<script setup>
import { ref, toRefs, watch } from 'vue'

defineOptions({ name: 'Tabbar' })
const props = defineProps({
		active: {
			type: String,
			default: '/home'
		}
	})
const { active } = toRefs(props)
const activePath = ref(active.value)
watch(active, (val) => {
			activePath.value = val;
		})
</script>

<style lang="scss" scoped>
.custom-tabbar {
	height: 56px;
	background: var(--bg-primary) !important;
	border-top: 1px solid var(--border) !important;
	position: fixed !important;
	bottom: 0;
	left: 0;
	right: 0;
	z-index: 100;
	padding-bottom: env(safe-area-inset-bottom, 0);

	:deep(.van-tabbar-item) {
		background-color: transparent;
		color: var(--text-tertiary);
		transition: color 0.25s ease;

		.van-tabbar-item__icon {
			font-size: 22px;
			margin-bottom: 2px;
		}

		.tab-label {
			font-size: 10px;
			font-weight: 400;
			transition: color 0.25s ease;
		}
	}

	:deep(.van-tabbar-item--active) {
		color: var(--accent) !important;

		.tab-label {
			color: var(--accent);
			font-weight: 500;
		}
	}
}

.tab-icon-wrap {
	position: relative;
	display: flex;
	align-items: center;
	justify-content: center;
}

.home-tab-item {
	overflow: visible;
	:deep(.van-tabbar-item__icon) {
		overflow: visible;
	}
}

.home-btn {
	width: 48px;
	height: 48px;
	margin-top: -20px;
	border-radius: 50%;
	background: var(--bg-primary);
	border: 1px solid var(--border);
	display: flex;
	align-items: center;
	justify-content: center;
	color: var(--text-tertiary);
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
	transition: all 0.25s ease;
	z-index: 10;

	&.active {
		background: var(--accent);
		border-color: var(--accent);
		color: #ffffff;
		box-shadow: 0 4px 12px rgba(43, 108, 176, 0.25);
	}
}

:deep(.van-tabbar-placeholder) {
	height: 56px !important;
}
</style>
