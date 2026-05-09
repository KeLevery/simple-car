# Simple Car 后续任务

## 车主端工程化

本轮只稳定后台管理和服务端，`simple-car-client` 暂不继续扩大重构范围。后续建议单独开一轮处理：

- 将车主端从 JavaScript 逐步迁移到 TypeScript。
- 将 Vuex 替换为 Pinia，统一登录态、车辆态和服务数据流。
- 增加 `vue-tsc` 类型检查和最小构建校验。
- 清理 Vite 迁移后的旧配置残留和页面级乱码风险。
