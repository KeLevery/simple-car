<template>
  <div id="app">
    <router-view />
  </div>
</template>

<style lang="scss">
/* ========== 白色简约主题 - 全局变量 ========== */
:root {
  /* 核心色板 */
  --bg-primary: #ffffff;
  --bg-secondary: #f7f8fa;
  --bg-card: #ffffff;
  --bg-page: #f5f6f8;

  /* 强调色 - 主题蓝 */
  --accent: #2b6cb0;
  --accent-light: #ebf4ff;
  --accent-gradient: linear-gradient(135deg, #3182ce, #2b6cb0);

  /* 文字层级 */
  --text-primary: #1a202c;
  --text-secondary: #718096;
  --text-tertiary: #a0aec0;

  /* 边框 / 分隔线 */
  --border: #edf2f7;
  --border-dark: #e2e8f0;

  /* 功能色 */
  --success: #38a169;
  --warning: #d69e2e;
  --danger: #e53e3e;
  --info: #3182ce;

  /* 卡片 */
  --card-radius: 16px;
  --card-shadow: 0 1px 2px rgba(16, 24, 40, 0.04), 0 1px 3px rgba(16, 24, 40, 0.06);
  --card-shadow-md: 0 6px 18px rgba(16, 24, 40, 0.08);

  /* 更偏 Creative Tim 的“面板”风格：细边框 + 更轻阴影 */
  --panel-border: 1px solid rgba(226, 232, 240, 0.9);
  --panel-bg: rgba(255, 255, 255, 0.9);
  --panel-blur: 8px;

  /* 字体 */
  --font-family: -apple-system, 'SF Pro Display', 'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
  --font-mono: 'SF Mono', 'Fira Code', 'JetBrains Mono', monospace;
}

/* ========== 全局重置 ========== */
*, *::before, *::after {
  box-sizing: border-box;
}

body, html {
  margin: 0;
  padding: 0;
  background: var(--bg-page);
  color: var(--text-primary);
  font-family: var(--font-family);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  font-size: 14px;
  line-height: 1.5;
}

#app {
  font-family: var(--font-family);
  color: var(--text-primary);
  min-height: 100vh;
  text-align: left;
}

/* ========== Vant 简约覆写 ========== */

/* NavBar */
.van-nav-bar {
  background: var(--bg-primary) !important;
  border-bottom: 1px solid var(--border) !important;

  .van-nav-bar__title {
    color: var(--text-primary) !important;
    font-weight: 600 !important;
    font-size: 16px;
  }
  .van-nav-bar__left .van-icon,
  .van-nav-bar__right .van-icon,
  .van-nav-bar__text {
    color: var(--text-secondary) !important;
  }
}

/* CellGroup / Cell */
.van-cell-group--inset {
  background: var(--bg-card) !important;
  border-radius: var(--card-radius) !important;
  border: none !important;
  box-shadow: var(--card-shadow);
  overflow: hidden;
}

.van-cell {
  background: transparent !important;
  color: var(--text-primary) !important;
  padding: 14px 16px !important;

  &::after {
    border-color: var(--border) !important;
  }

  .van-cell__title {
    color: var(--text-primary) !important;
  }
  .van-cell__value {
    color: var(--text-secondary) !important;
  }
  .van-cell__right-icon {
    color: var(--text-tertiary) !important;
  }
  .van-cell__left-icon {
    color: var(--accent) !important;
  }
}

/* Button */
.van-button--info {
  background: var(--accent-gradient) !important;
  border: none !important;
  color: #ffffff !important;
  font-weight: 600 !important;
}

.van-button--default {
  background: var(--bg-primary) !important;
  border: 1px solid var(--border-dark) !important;
  color: var(--text-primary) !important;
}

/* Field */
.van-field {
  background: transparent !important;

  .van-field__control {
    color: var(--text-primary) !important;

    &::placeholder {
      color: var(--text-tertiary) !important;
    }
  }

  .van-field__left-icon {
    color: var(--text-tertiary) !important;
  }
}

/* Tabs */
.van-tabs {
  .van-tabs__nav {
    background: var(--bg-primary) !important;
  }
  .van-tab {
    color: var(--text-secondary) !important;
  }
  .van-tab--active {
    color: var(--accent) !important;
    font-weight: 600;
  }
  .van-tabs__line {
    background: var(--accent) !important;
  }
}

/* Tag */
.van-tag--primary {
  background: var(--accent-light) !important;
  color: var(--accent) !important;
}

.van-tag--success {
  background: #f0fff4 !important;
  color: var(--success) !important;
}

.van-tag--danger {
  background: #fff5f5 !important;
  color: var(--danger) !important;
}

/* Popup */
.van-popup {
  background: var(--bg-primary) !important;
  color: var(--text-primary) !important;
}

/* Dialog */
.van-dialog {
  background: var(--bg-primary) !important;
  border: none !important;
  border-radius: var(--card-radius) !important;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);

  .van-dialog__header {
    color: var(--text-primary) !important;
  }
  .van-dialog__message {
    color: var(--text-secondary) !important;
  }
}

/* Toast */
.van-toast {
  background: rgba(26, 32, 44, 0.88) !important;
  border-radius: 10px;
}

/* ActionSheet */
.van-action-sheet {
  background: var(--bg-primary) !important;
  color: var(--text-primary) !important;

  .van-action-sheet__item {
    background: transparent !important;
    color: var(--text-primary) !important;
  }
  .van-action-sheet__cancel {
    background: var(--bg-secondary) !important;
    color: var(--text-secondary) !important;
  }
  .van-action-sheet__gap {
    background: var(--bg-secondary) !important;
  }
}

/* Switch */
.van-switch--on {
  background: var(--accent) !important;
}

/* Progress */
.van-progress {
  .van-progress__portion {
    background: var(--accent-gradient) !important;
  }
  .van-progress__track {
    background: var(--bg-secondary) !important;
  }
}

/* Empty */
.van-empty__description {
  color: var(--text-secondary) !important;
}

/* Dropdown */
.van-dropdown-menu__bar {
  background: var(--bg-card) !important;
  box-shadow: none !important;
}

.van-dropdown-menu__title {
  color: var(--text-primary) !important;
}

/* Tabbar */
.van-tabbar {
  background: var(--bg-primary) !important;
  border-top: 1px solid var(--border) !important;
}

/* Notify */
.van-notify {
  font-size: 13px;
}

/* Loading */
.van-loading__text {
  color: var(--text-secondary) !important;
}

/* Swipe indicator */
.van-swipe__indicator--active {
  background: var(--accent) !important;
}

/* Image placeholder */
.van-image__error, .van-image__loading {
  background: var(--bg-secondary) !important;
}

/* ========== 通用工具类 ========== */
.card {
  background: var(--bg-card);
  border-radius: var(--card-radius);
  box-shadow: var(--card-shadow);
}

.card-md {
  background: var(--bg-card);
  border-radius: var(--card-radius);
  box-shadow: var(--card-shadow-md);
}

/* 页面通用容器 */
.page-container {
  min-height: 100vh;
  background: var(--bg-page);
}

/* 页面内容区 (有tabbar时) */
.page-content {
  padding: 0 16px 80px;
}

/* 微弱扫光动画 — 保留但简化 */
@keyframes fade-in-up {
  from { opacity: 0; transform: translateY(16px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes fade-in {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* 入场动画序列 */
.stagger-1 { animation-delay: 0.05s; }
.stagger-2 { animation-delay: 0.1s; }
.stagger-3 { animation-delay: 0.15s; }
.stagger-4 { animation-delay: 0.2s; }
.stagger-5 { animation-delay: 0.25s; }
.stagger-6 { animation-delay: 0.3s; }

.animate-fade-in-up {
  animation: fade-in-up 0.5s ease forwards;
  opacity: 0;
}
</style>
