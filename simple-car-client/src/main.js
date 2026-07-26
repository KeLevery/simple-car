import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import {
  ActionSheet,
  Area,
  Button,
  Calendar,
  Cell,
  CellGroup,
  Dialog,
  DropdownItem,
  DropdownMenu,
  Empty,
  Field,
  Form,
  Icon,
  Image as VanImage,
  List,
  Loading,
  NavBar,
  Overlay,
  Picker,
  Popup,
  Progress,
  PullRefresh,
  Radio,
  RadioGroup,
  Search,
  Swipe,
  SwipeItem,
  Switch,
  Tab,
  Tabbar,
  TabbarItem,
  Tabs,
  Tag,
  TimePicker,
  closeToast,
  showConfirmDialog,
  showFailToast,
  showLoadingToast,
  showNotify,
  showSuccessToast,
  showToast
} from 'vant'
import 'vant/es/action-sheet/style/index.mjs'
import 'vant/es/area/style/index.mjs'
import 'vant/es/button/style/index.mjs'
import 'vant/es/calendar/style/index.mjs'
import 'vant/es/cell/style/index.mjs'
import 'vant/es/cell-group/style/index.mjs'
import 'vant/es/dialog/style/index.mjs'
import 'vant/es/dropdown-item/style/index.mjs'
import 'vant/es/dropdown-menu/style/index.mjs'
import 'vant/es/empty/style/index.mjs'
import 'vant/es/field/style/index.mjs'
import 'vant/es/form/style/index.mjs'
import 'vant/es/icon/style/index.mjs'
import 'vant/es/image/style/index.mjs'
import 'vant/es/list/style/index.mjs'
import 'vant/es/loading/style/index.mjs'
import 'vant/es/nav-bar/style/index.mjs'
import 'vant/es/notify/style/index.mjs'
import 'vant/es/overlay/style/index.mjs'
import 'vant/es/picker/style/index.mjs'
import 'vant/es/popup/style/index.mjs'
import 'vant/es/progress/style/index.mjs'
import 'vant/es/pull-refresh/style/index.mjs'
import 'vant/es/radio/style/index.mjs'
import 'vant/es/radio-group/style/index.mjs'
import 'vant/es/search/style/index.mjs'
import 'vant/es/swipe/style/index.mjs'
import 'vant/es/swipe-item/style/index.mjs'
import 'vant/es/switch/style/index.mjs'
import 'vant/es/tab/style/index.mjs'
import 'vant/es/tabbar/style/index.mjs'
import 'vant/es/tabbar-item/style/index.mjs'
import 'vant/es/tabs/style/index.mjs'
import 'vant/es/tag/style/index.mjs'
import 'vant/es/time-picker/style/index.mjs'
import 'vant/es/toast/style/index.mjs'
import 'amfe-flexible/index.js'

const toast = Object.assign(showToast, {
  success: showSuccessToast,
  fail: showFailToast,
  loading: showLoadingToast,
  clear: closeToast
})

const vantComponents = [
  ActionSheet,
  Area,
  Button,
  Calendar,
  Cell,
  CellGroup,
  Dialog,
  DropdownItem,
  DropdownMenu,
  Empty,
  Field,
  Form,
  Icon,
  VanImage,
  List,
  Loading,
  NavBar,
  Overlay,
  Picker,
  Popup,
  Progress,
  PullRefresh,
  Radio,
  RadioGroup,
  Search,
  Swipe,
  SwipeItem,
  Switch,
  Tab,
  Tabbar,
  TabbarItem,
  Tabs,
  Tag,
  TimePicker
]

const app = createApp(App)

app.use(router)
app.use(store)
vantComponents.forEach(component => app.use(component))

app.config.globalProperties.$toast = toast
app.config.globalProperties.$notify = showNotify
app.config.globalProperties.$dialog = {
  confirm: showConfirmDialog
}

app.mount('#app')
