<template>
	<div class="register-page">
		<div class="register-content">
			<!-- Brand -->
			<div class="brand animate-in stagger-1">
				<div class="brand-logo">
					<svg viewBox="0 0 40 40" width="48" height="48">
						<polygon points="20,2 38,20 20,38 2,20" fill="none" stroke="currentColor" stroke-width="1.5" />
						<circle cx="20" cy="20" r="4" fill="currentColor" />
					</svg>
				</div>
				<h1 class="brand-name">SIMPLE CAR</h1>
				<p class="brand-slogan">智慧出行 · 简约生活</p>
			</div>

			<!-- Register Form -->
			<div class="form-card animate-in stagger-2">
				<div class="form-title">注册账号</div>

				<div class="field-group">
					<div class="field-item">
						<van-field v-model="phone" placeholder="请输入手机号" left-icon="phone-o" type="tel" maxlength="11"
							class="clean-field" :error-message="phoneError" />
					</div>
					<div class="field-item">
						<van-field v-model="nickName" placeholder="请输入昵称（选填）" left-icon="user-o" maxlength="20"
							class="clean-field" />
					</div>
					<div class="field-item">
						<van-field v-model="password" placeholder="请输入密码（至少6位）" left-icon="lock" type="password"
							class="clean-field" :error-message="passwordError" />
					</div>
					<div class="field-item">
						<van-field v-model="confirmPassword" placeholder="请再次输入密码" left-icon="lock" type="password"
							class="clean-field" :error-message="confirmError" />
					</div>
				</div>

				<van-button block class="register-btn" :loading="loading" @click="registerSubmit">
					注 册
				</van-button>

				<div class="form-links">
					<span class="link primary" @click="goLogin">已有账号？去登录</span>
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
import { userRegister } from '@/api/user'
import { ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useVantCompat } from '@/composables/useVantCompat'

defineOptions({ name: 'register' })
const router = useRouter()
const route = useRoute()
const { toast, notify, dialog } = useVantCompat()
const phone = ref('')
const nickName = ref('')
const password = ref('')
const confirmPassword = ref('')
const loading = ref(false)
const phoneError = ref('')
const passwordError = ref('')
const confirmError = ref('')
function validate() {
	let valid = true
	if (!/^1\d{10}$/.test(phone.value)) {
		phoneError.value = '请输入正确的11位手机号'
		valid = false
	}
	if (password.value.length < 6) {
		passwordError.value = '密码长度不能少于6位'
		valid = false
	}
	if (confirmPassword.value !== password.value) {
		confirmError.value = '两次输入的密码不一致'
		valid = false
	}
	if (confirmPassword.value.length === 0) {
		confirmError.value = '请再次输入密码'
		valid = false
	}
	return valid
}
function registerSubmit() {
	if (!validate()) return

	loading.value = true
	toast.loading({
		message: '注册中...',
		forbidClick: true
	})

	userRegister({
		username: phone.value,
		password: password.value,
		nickName: nickName.value || '',
		phone: phone.value
	}).then(res => {
		loading.value = false
		if (res.code == 200) {
			toast.success('注册成功，请登录')
			router.push({ path: '/', query: { account: phone.value } })
		} else {
			toast.fail(res.msg || '注册失败')
		}
	}).catch(() => {
		loading.value = false
		toast.fail('注册失败，请稍后重试')
	})
}
function goLogin() {
	router.push('/')
}
watch(phone, () => { phoneError.value = '' })
watch(password, () => { passwordError.value = '' })
watch(confirmPassword, () => { confirmError.value = '' })
</script>

<style scoped lang="scss">
.register-page {
	min-height: 100vh;
	background: linear-gradient(180deg, #f0f4f8 0%, #ffffff 40%);
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 40px 24px;
}

.register-content {
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

.register-btn {
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
	justify-content: center;

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

.stagger-1 {
	animation-delay: 0.1s;
}

.stagger-2 {
	animation-delay: 0.2s;
}

.stagger-3 {
	animation-delay: 0.3s;
}

@keyframes enterUp {
	to {
		opacity: 1;
		transform: translateY(0);
	}
}
</style>
