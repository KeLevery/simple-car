<template>
	<div class="pay-container">
        <van-nav-bar
            title="维保支付"
            fixed
            placeholder
        />
		
        <van-popup
            v-model="show"
            closeable
            :close-on-click-overlay="false"
            @click-close-icon="noPay"
            position="bottom"
            round>
            <div class="popup-content">
                <div class="popUpTitle">付款详情</div>
                <div class="payTitle">维保费用</div>
                <div class="amount">￥{{price.toFixed(2)}}</div>
                <div class="infoItem">
                    <div class="itemLeft">订单信息</div>
                    <div class="itemRight">维保项目</div>
                </div>
                <div class="infoItem">
                    <div class="itemLeft">交易方式</div>
                    <div class="itemRight">银行卡[1127]</div>
                </div>
                <div class="payBtn">
                    <van-button @click="payCmf" round block type="info">确认交易</van-button>
                </div>
            </div>
        </van-popup>

        <van-dialog 
            v-model="dialogShow" 
            cancelButtonText="继续支付" 
            confirmButtonText="放弃" 
            @confirm="giveUp"
            @cancel="show = true"
            show-cancel-button>
            <p class="dialog-msg">是否放弃本次支付？</p>
        </van-dialog>
	</div>
</template>

<script>
import { paymentUpdate } from '@/api/payment'
export default {
    data() {
        return {
            show: true,
            price: 1189,
            radio: '1',
            dialogShow: false,
            historyArr: [],
            orderId: 0,
            payId: 0
        }
    },
    created() {
        this.payId = parseInt(this.$route.query.payId)
        this.orderId = parseInt(this.$route.query.orderId)
        this.price = parseFloat(this.$route.query.money)
    },
    methods: {
        goBack() {
            this.$router.go(-1);
        },
        payCmf() {
            paymentUpdate({
                id: this.payId,
                price: this.price,
                maintenanceAppointmentId: this.orderId,
                status: 1
            }).then(res => {
                if(res.code == 200) {
                    this.$notify({type: 'success',message: '支付成功！'});
                    this.$router.push({path: '/home'})
                } else {
                    this.$toast.fail('支付失败，请重试！');
                }
            })            
        },
        noPay() {
            this.dialogShow = true;
        },
        giveUp() {
            this.$router.push({path: '/home'})
        }
    }
}
</script>

<style lang="scss" scoped>
.pay-container {
  min-height: 100vh;
  background-color: var(--bg-primary);

  :deep(.van-nav-bar) {
    background: var(--bg-glass);
    backdrop-filter: blur(var(--card-blur));
    border-bottom: 1px solid var(--border);
    .van-nav-bar__title { color: var(--text-primary); font-weight: 300; }
    .van-icon, .van-nav-bar__text { color: var(--accent); }
  }
}

:deep(.van-popup) {
  background: var(--bg-card);
  backdrop-filter: blur(var(--card-blur));
  border: 1px solid var(--border);
}

.popup-content {
  padding-bottom: 30px;
}

.popUpTitle {
  font-size: 16px;
  line-height: 30px;
  text-align: center;
  padding: 15px 0;
  color: var(--text-primary);
  font-weight: 300;
  border-bottom: 1px solid var(--border);
}

.payTitle {
  font-size: 14px;
  text-align: center;
  padding-top: 30px;
  color: var(--text-secondary);
  font-weight: 200;
}

.amount {
  font-size: 36px;
  font-weight: 700;
  line-height: 1.5;
  color: var(--accent);
  text-align: center;
  margin-bottom: 30px;
  text-shadow: 0 0 15px var(--accent-glow);
  font-family: 'PingFang SC', sans-serif;
}

.infoItem {
  box-sizing: border-box;
  padding: 0 40px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  line-height: 40px;
  color: var(--text-secondary);
  border-bottom: 1px solid var(--border);
  margin: 0 20px;

  .itemRight {
    color: var(--text-primary);
    font-weight: 300;
  }
}

.payBtn {
  margin: 40px 0 20px 0;
  box-sizing: border-box;
  padding: 0 30px;

  :deep(.van-button--info) {
    background: var(--accent-gradient);
    border: none;
    font-size: 16px;
    font-weight: 300;
    letter-spacing: 4px;
    box-shadow: 0 4px 20px var(--accent-glow);
    height: 48px;
  }
}

:deep(.van-dialog) {
  background: var(--bg-card);
  backdrop-filter: blur(var(--card-blur));
  .van-dialog__content { padding: 40px 20px; }
  .dialog-msg { text-align: center; color: var(--text-primary); font-size: 16px; margin: 0; }
  .van-button { background: transparent; color: var(--text-secondary); }
  .van-dialog__confirm { color: var(--danger); }
  .van-dialog__cancel { color: var(--accent); }
}
</style>
