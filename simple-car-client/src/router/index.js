import Vue from 'vue'
import VueRouter from 'vue-router'
import LoginView from '../views/login.vue'

Vue.use(VueRouter)

const routes = [
	{
		path: '/',
		name: 'login',
		component: LoginView
	},
	{
		path: '/register',
		name: 'register',
		component: () => import('@/views/register.vue')
	},
	{
		path: '/home',
		name: 'home',
		component: () => import('@/views/HomeView.vue'),
		meta: { title: '首页', keywords: ['主页'] }
	},
	{
		path: '/mine',
		name: 'mine',
		component: () => import('@/views/mine/index.vue'),
		meta: { title: '我的', keywords: ['个人中心'] }
	},
	{
		path: '/analysis',
		name: 'analysis',
		component: () => import('@/views/analysis.vue'),
		meta: { title: '用车报告', keywords: ['分析', '里程', '充电订单'], pinned: true, pinnedOrder: 50 }
	},
	{
		path: '/service',
		name: 'service',
		component: () => import('@/views/service/index.vue'),
		meta: { title: '车主服务', keywords: ['服务'] }
	},
	{
		path: '/service/reservation',
		name: 'reservation',
		component: () => import('@/views/service/reservation.vue'),
		meta: { title: '预约维保', keywords: ['维保', '保养', '预约'], pinned: true, pinnedOrder: 20 }
	},
	{
		path: '/service/record',
		name: 'record',
		component: () => import('@/views/service/record.vue'),
		meta: { title: '维保历史', keywords: ['维保记录'] }
	},
	{
		path: '/service/charging',
		name: 'service-charging',
		component: () => import('@/views/service/charging.vue'),
		meta: { title: '发现充电站', keywords: ['充电', '充电站'], pinned: true, pinnedOrder: 30 }
	},
	{
		path: '/service/rescue',
		name: 'service-rescue',
		component: () => import('@/views/service/rescue.vue'),
		meta: { title: '一键救援', keywords: ['救援', 'SOS'], pinned: true, pinnedOrder: 10 }
	},
	{
		path: '/control',
		name: 'control',
		component: () => import('@/views/control/control.vue'),
		meta: { title: '远程车辆控制', keywords: ['控制', '启动', '空调'] }
	},
	{
		path: '/pay',
		name: 'pay',
		component: () => import('@/views/pay/pay.vue'),
		meta: { title: '支付', keywords: ['付款'] }
	},
	{
		path: '/notice',
		name: 'notice',
		component: () => import('@/views/notice/index.vue'),
		meta: { title: '公告', keywords: ['通知'] }
	},
	{
		path: '/mine/orders',
		name: 'mine-orders',
		component: () => import('@/views/mine/orders.vue'),
		meta: { title: '我的订单', keywords: ['订单'], pinned: true, pinnedOrder: 40 }
	},
	{
		path: '/mine/settings',
		name: 'mine-settings',
		component: () => import('@/views/mine/settings.vue'),
		meta: { title: '常用设置', keywords: ['设置'] }
	},
	{
		path: '/mine/about',
		name: 'mine-about',
		component: () => import('@/views/mine/about.vue'),
		meta: { title: '关于我们', keywords: ['关于'] }
	},
	{
		path: '/mine/profile',
		name: 'mine-profile',
		component: () => import('@/views/mine/profile.vue'),
		meta: { title: '个人资料', keywords: ['资料', '昵称', '手机号'] }
	},
	{
		path: '/mine/security',
		name: 'mine-security',
		component: () => import('@/views/mine/security.vue'),
		meta: { title: '账号安全', keywords: ['密码', '安全'] }
	},
	{
		path: '/community',
		name: 'community',
		component: () => import('@/views/community/index.vue'),
		meta: { title: '社区', keywords: ['论坛'] }
	},
	{
		path: '/demo',
		name: 'demo',
		component: () => import('@/views/service/demo.vue'),
		meta: { title: '演示', keywords: ['demo'] }
	},
	{
		path: '/service/violation',
		name: 'service-violation',
		component: () => import('@/views/service/violation.vue'),
		meta: { title: '违章查询', keywords: ['违章'] }
	},
	{
		path: '/mine/notification',
		name: 'mine-notification',
		component: () => import('@/views/mine/notification.vue'),
		meta: { title: '消息中心', keywords: ['消息', '通知'] }
	},
	{
		path: '/mine/privacy',
		name: 'mine-privacy',
		component: () => import('@/views/mine/privacy.vue'),
		meta: { title: '隐私政策', keywords: ['隐私'] }
	},
	{
		path: '/mine/addCar',
		name: 'mine-addCar',
		component: () => import('@/views/mine/addCar.vue'),
		meta: { title: '添加车辆', keywords: ['绑定车辆'] }
	}
]

const router = new VueRouter({
	routes
})

// 全局路由守卫
router.beforeEach((to, from, next) => {
	const hasLogin = window.localStorage.getItem('hasLogin')
	const token = window.localStorage.getItem('token')

	// 登录页和注册页直接放行
	if (to.path === '/' || to.path === '/register') {
		next()
		return
	}

	// 其他页面需要登录
	if (!hasLogin || !token) {
		next('/')
		return
	}

	next()
})

export default router
