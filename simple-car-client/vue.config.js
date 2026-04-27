const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  publicPath: './',
  devServer: {
    port: 4000,
    open: true,
    proxy: {
      '/dev-api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        onProxyReq: (proxyReq, req, res) => {
          // 添加调试日志
          console.log('Proxying request:', req.method, req.url, '->', proxyReq.path)
        }
      }
    }
  }
})
