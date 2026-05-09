<template>
	<div class="about-container">
		<van-nav-bar 
			title="关于我们" 
			left-text="返回" 
			left-arrow 
			fixed 
			placeholder 
			@click-left="$router.back()"
		/>

		<div class="about-logo">
			<div class="logo-glow"></div>
			<img src="../../assets/logo.png" />
			<div class="app-name">极氪车主</div>
			<div class="version">Version 1.0.2</div>
		</div>

		<van-cell-group inset class="mt-15">
			<van-cell title="功能介绍" is-link @click="showContent('feature')" />
			<van-cell title="用户协议" is-link @click="showContent('agreement')" />
			<van-cell title="隐私政策" is-link @click="showContent('privacy')" />
		</van-cell-group>

		<div class="footer-text">
			<div>南京交通 &copy; 2026 Copyright</div>
			<div>All rights reserved.</div>
		</div>

        <van-popup v-model:show="showPopup" position="bottom" round :style="{ maxHeight: '80%' }">
            <div class="content-popup">
                <div class="popup-header">
                    <h3>{{ popupTitle }}</h3>
                </div>
                <div class="popup-body">
                    <p style="white-space: pre-line; line-height: 1.8;">{{ popupContent }}</p>
                </div>
                <div class="popup-footer">
                    <van-button type="default" block round @click="showPopup = false">关闭</van-button>
                </div>
            </div>
        </van-popup>
	</div>
</template>

<script setup>
import { ref } from 'vue'

const showPopup = ref(false)
const popupTitle = ref('')
const popupContent = ref('')
const contents = ref({
                feature: {
                    title: '功能介绍',
                    content: '极氪车主APP是一款专为极氪车主打造的智能出行服务平台。\n\n主要功能包括：\n\n• 远程车控：支持远程锁车/解锁、空调控制、车辆状态查看等\n\n• 智能充电：查找附近充电站、查看实时空闲桩位、充电费用记录\n\n• 维保服务：在线预约维保、查看维保历史、维修方案推荐\n\n• 数据分析：行驶里程统计、充电数据分析、用车报告\n\n• 车主社区：分享用车体验、交流驾驶心得、获取最新资讯\n\n• 违章查询：实时查询车辆违章记录\n\n• 道路救援：一键呼叫道路救援服务'
                },
                agreement: {
                    title: '用户协议',
                    content: '极氪车主用户服务协议\n\n更新日期：2026年1月1日\n生效日期：2026年1月1日\n\n一、总则\n本协议是用户（以下简称"您"）与极氪车主APP（以下简称"本平台"）之间关于使用本平台服务的法律协议。\n\n二、服务内容\n本平台为您提供车辆远程控制、充电服务、维保预约、数据分析、社区交流等服务。\n\n三、用户义务\n1. 您应保证注册信息的真实性和准确性。\n2. 您应妥善保管账号和密码，因保管不善造成的损失由您自行承担。\n3. 您不得利用本平台从事违法违规活动。\n\n四、隐私保护\n我们重视您的隐私保护，详见《隐私政策》。\n\n五、免责声明\n本平台提供的车辆数据仅供参考，具体以车辆实际状态为准。\n\n六、协议修改\n本平台有权根据需要修改本协议，修改后的协议将在平台公布。'
                },
                privacy: {
                    title: '隐私政策',
                    content: '极氪车主隐私政策\n\n更新日期：2026年1月1日\n\n一、信息收集\n我们可能收集以下信息：\n1. 您提供的个人信息（姓名、手机号、车辆信息等）\n2. 车辆运行数据（里程、电量、位置等）\n3. 服务使用记录\n\n二、信息使用\n我们收集的信息用于：\n1. 提供、维护和改进我们的服务\n2. 为您推送相关通知和提醒\n3. 保障账号和交易安全\n\n三、信息保护\n我们采用行业标准的安全措施保护您的个人信息，防止未经授权的访问、使用或泄露。\n\n四、信息共享\n未经您的同意，我们不会向第三方共享您的个人信息，法律法规规定的除外。\n\n五、您的权利\n您有权查询、更正、删除您的个人信息，也可以注销账号。\n\n六、联系我们\n如有疑问，请通过APP内的在线客服联系我们。'
                }
            })
function showContent(type) {
            const content = contents.value[type];
            if (content) {
                popupTitle.value = content.title;
                popupContent.value = content.content;
                showPopup.value = true;
            }
        }
</script>

<style lang="scss" scoped>
.about-container {
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
}

.about-logo {
	padding: 80px 0 60px;
	text-align: center;
	position: relative;
  
  .logo-glow {
    position: absolute;
    top: 120px;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 120px;
    height: 120px;
    background: var(--accent-glow);
    filter: blur(40px);
    border-radius: 50%;
    z-index: 0;
  }
	
	img { 
    width: 80px; 
    height: 80px; 
    border-radius: 20px; 
    box-shadow: 0 0 30px var(--accent-glow); 
    position: relative;
    z-index: 1;
    border: 1px solid var(--border-accent);
  }
	.app-name { 
    font-size: 22px; 
    font-weight: 300; 
    color: var(--text-primary); 
    margin-top: 20px; 
    letter-spacing: 4px;
    text-shadow: 0 0 10px var(--accent-glow);
  }
	.version { font-size: 13px; color: var(--text-tertiary); margin-top: 5px; font-weight: 200; }
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
  .van-cell__title { font-weight: 300; letter-spacing: 1px; }
  .van-icon { color: var(--text-tertiary); }
}

.footer-text {
	position: fixed;
	bottom: 40px;
	left: 0;
	right: 0;
	text-align: center;
	color: var(--text-tertiary);
	font-size: 11px;
	line-height: 1.8;
  font-weight: 200;
  letter-spacing: 1px;
}

.mt-15 { margin-top: 15px; }

.content-popup {
    padding: 20px;
    
    .popup-header {
        text-align: center;
        margin-bottom: 16px;
        padding-bottom: 16px;
        border-bottom: 1px solid var(--border);
        h3 {
            font-size: 16px;
            font-weight: 600;
            color: var(--text-primary);
            margin: 0;
        }
    }
    
    .popup-body {
        max-height: 50vh;
        overflow-y: auto;
        p {
            font-size: 14px;
            color: var(--text-secondary);
            margin: 0;
            font-weight: 300;
        }
    }
    
    .popup-footer {
        margin-top: 20px;
    }
}
</style>
