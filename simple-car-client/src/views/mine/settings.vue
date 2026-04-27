<template>
	<div class="settings-container">
		<van-nav-bar 
			title="常用设置" 
			left-text="返回" 
			left-arrow 
			fixed 
			placeholder 
			@click-left="$router.back()"
		/>

		<van-cell-group inset class="mt-15">
			<van-cell title="个人资料" is-link @click="$router.push('/mine/profile')" />
			<van-cell title="账号与安全" is-link @click="$router.push('/mine/security')" />
		</van-cell-group>

		<van-cell-group inset class="mt-15">
			<van-cell title="隐私设置" is-link @click="$router.push('/mine/privacy')" />
			<van-cell title="消息通知" is-link @click="$router.push('/mine/notification')" />
		</van-cell-group>

		<van-cell-group inset class="mt-15">
			<van-cell title="系统版本" value="v1.0.2" />
			<van-cell title="检查更新" is-link @click="$toast('当前已是最新版本')" />
		</van-cell-group>

	</div>
</template>

<style lang="scss" scoped>
.settings-container {
  min-height: 100vh;
  background-color: var(--bg-primary);
  padding-bottom: 20px;

  :deep(.van-nav-bar) {
    background: var(--bg-glass);
    backdrop-filter: blur(var(--card-blur));
    border-bottom: 1px solid var(--border);
    .van-nav-bar__title { color: var(--text-primary); font-weight: 300; letter-spacing: 1px; }
    .van-icon, .van-nav-bar__text { color: var(--accent); }
  }

  :deep(.van-cell-group) {
    background: var(--bg-card);
    border: 1px solid var(--border);
    backdrop-filter: blur(var(--card-blur));
    &::after { border: none; }
  }

  :deep(.van-cell) {
    background: transparent;
    color: var(--text-primary);
    &::after { border-bottom-color: var(--border); }
    .van-cell__title { font-weight: 300; }
    .van-cell__value { color: var(--text-secondary); }
    .van-icon { color: var(--text-tertiary); }
  }
}
.mt-15 { margin-top: 15px; }
</style>
