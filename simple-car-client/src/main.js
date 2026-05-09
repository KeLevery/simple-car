import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Vant, {
  closeToast,
  showConfirmDialog,
  showFailToast,
  showLoadingToast,
  showNotify,
  showSuccessToast,
  showToast
} from 'vant'
import 'vant/lib/index.css'
import 'amfe-flexible/index.js'

const toast = Object.assign(showToast, {
  success: showSuccessToast,
  fail: showFailToast,
  loading: showLoadingToast,
  clear: closeToast
})

const app = createApp(App)

app.use(router)
app.use(store)
app.use(Vant)

app.config.globalProperties.$toast = toast
app.config.globalProperties.$notify = showNotify
app.config.globalProperties.$dialog = {
  confirm: showConfirmDialog
}

app.mount('#app')
