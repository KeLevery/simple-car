<template>
	<div class="login-page">
		<div class="login-content">
			<!-- Brand -->
			<div class="brand animate-in stagger-1">
				<div class="brand-logo">
					<svg viewBox="0 0 40 40" width="48" height="48">
						<polygon points="20,2 38,20 20,38 2,20" fill="none" stroke="currentColor" stroke-width="1.5"/>
						<circle cx="20" cy="20" r="4" fill="currentColor"/>
					</svg>
				</div>
				<h1 class="brand-name">SIMPLE CAR</h1>
				<p class="brand-slogan">智慧出行 · 简约生活</p>
			</div>

			<!-- Login Form -->
			<div class="form-card animate-in stagger-2">
				<div class="form-title">登录账号</div>

				<div class="field-group">
					<div class="field-item">
						<van-field
							v-model="account"
							placeholder="请输入手机号"
							left-icon="phone-o"
							type="tel"
							maxlength="11"
							class="clean-field"
						/>
					</div>
					<div class="field-item">
						<van-field
							v-model="password"
							placeholder="请输入密码"
							left-icon="lock"
							type="password"
							class="clean-field"
						/>
					</div>
				</div>

				<van-button block class="login-btn" @click="loginSubmit">
					登 录
				</van-button>

				<div class="form-links">
					<span class="link" @click="onForgotPassword">忘记密码？</span>
					<span class="link primary" @click="onRegister">注册账号</span>
				</div>
			</div>

			<!-- Footer -->
			<div class="footer animate-in stagger-3">
				<p>© 2026 Simple Car Technology</p>
			</div>
		</div>
	</div>
</template>

<script setup>
import {
	userLogin,
	userInfo
} from '@/api/user'
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useVantCompat } from '@/composables/useVantCompat'

defineOptions({ name: 'login' })
const router = useRouter()
const route = useRoute()
const { toast, notify, dialog } = useVantCompat()
const account = ref('')
const password = ref('')
function checkLogin() {
			if (window.localStorage.getItem('hasLogin')) {
				router.push('/home')
			}
		}
function loginSubmit() {
			if (account.value.length == 0) {
				toast.fail('请输入手机号！');
			} else if (password.value.length == 0) {
				toast.fail('请输入密码！');
			} else {
				toast.loading({
					message: '登录中...',
					forbidClick: true,
				});
				userLogin({
					username: account.value,
					password: password.value
				}).then(res => {
					if (res.code == 200) {
						// 后端返回：{ code, msg, data: { token } }
							const token = res && res.data ? res.data.token : '';
							if (token) {
								window.localStorage.setItem('token', token);
							}

						getUserInfo();
					} else {
						toast.fail(res.msg || '登录失败');
					}
				})
			}
		}
function onRegister() {
			router.push('/register')
		}
function onForgotPassword() {
			dialog.alert({
				title: '无法在线重置',
				message: '为保护账号安全，请联系管理员重置密码；已登录用户可在“我的-账号安全”中修改密码。',
				confirmButtonText: '知道了'
			});
		}
function getUserInfo() {
			userInfo().then(res => {
				if (res.code == 200) {
					const data = res.data || {}
					const cars = Array.isArray(data.cars) ? data.cars : []
					const user = data.user || {}

					if (cars.length > 0) {
						window.localStorage.setItem('carInfo', JSON.stringify(cars[0]));
						window.localStorage.setItem('carList', JSON.stringify(cars));
					}
					window.localStorage.setItem('hasLogin', true);
					window.localStorage.setItem('userInfo', JSON.stringify(user));
					toast.success('登录成功');
					router.push('/home')
				} else {
					toast.fail(res.msg || '获取用户信息失败');
				}
			}).catch((err) => {
				console.error('getUserInfo error:', err);
				toast.fail('获取用户信息失败，请重试');
			})
		}
checkLogin()
		if (route.query.account) {
			account.value = route.query.account
		}
</script>

<style scoped lang="scss">
.login-page {
	min-height: 100vh;
	background: linear-gradient(180deg, #f0f4f8 0%, #ffffff 40%);
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 40px 24px;
}

.login-content {
	width: 100%;
	max-width: 380px;
	display: flex;
	flex-direction: column;
	align-items: center;
}

/* Brand */
.brand {
	text-align: center;
	margin-bottom: 40px;

	.brand-logo {
		color: var(--accent);
		margin-bottom: 16px;
	}

	.brand-name {
		font-size: 28px;
		font-weight: 300;
		letter-spacing: 6px;
		color: var(--text-primary);
		margin: 0 0 8px;
	}

	.brand-slogan {
		font-size: 13px;
		color: var(--text-tertiary);
		letter-spacing: 2px;
		margin: 0;
	}
}

/* Form Card */
.form-card {
	width: 100%;
	background: var(--bg-card);
	border-radius: 16px;
	padding: 32px 24px;
	box-shadow: 0 2px 16px rgba(0, 0, 0, 0.06);

	.form-title {
		font-size: 20px;
		font-weight: 600;
		color: var(--text-primary);
		margin-bottom: 28px;
	}
}

.field-group {
	margin-bottom: 24px;
}

.field-item {
	margin-bottom: 14px;

	.clean-field {
		background: var(--bg-secondary) !important;
		border-radius: 10px;
		padding: 12px 14px;

		&::after {
			display: none;
		}

		:deep(.van-field__control) {
			color: var(--text-primary);
			font-size: 15px;
			&::placeholder {
				color: var(--text-tertiary);
			}
		}

		:deep(.van-field__left-icon) {
			color: var(--text-tertiary);
			margin-right: 8px;
		}

		&:focus-within {
			box-shadow: 0 0 0 2px rgba(43, 108, 176, 0.15);
		}
	}
}

.login-btn {
	height: 48px;
	background: var(--accent-gradient) !important;
	border: none !important;
	border-radius: 10px;
	color: #ffffff !important;
	font-weight: 600;
	font-size: 16px;
	letter-spacing: 4px;
	margin-bottom: 20px;

	&:active {
		opacity: 0.9;
	}
}

.form-links {
	display: flex;
	justify-content: space-between;

	.link {
		font-size: 13px;
		color: var(--text-tertiary);
		cursor: pointer;

		&.primary {
			color: var(--accent);
		}
	}
}

/* Footer */
.footer {
	margin-top: 40px;
	text-align: center;

	p {
		font-size: 11px;
		color: var(--text-tertiary);
		letter-spacing: 1px;
		margin: 0;
	}
}

/* Animation */
.animate-in {
	opacity: 0;
	transform: translateY(16px);
	animation: enterUp 0.6s ease forwards;
}

.stagger-1 { animation-delay: 0.1s; }
.stagger-2 { animation-delay: 0.2s; }
.stagger-3 { animation-delay: 0.3s; }

@keyframes enterUp {
	to { opacity: 1; transform: translateY(0); }
}
</style>
