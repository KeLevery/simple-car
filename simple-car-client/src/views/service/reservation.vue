<template>
	<div class="reservationWrap">
		<van-nav-bar title="维保预约" right-text="维保历史" left-arrow fixed @click-left="goBack" @click-right="gotoHistory" />
		<div class="formWrapper">
			<van-form validate-first @submit="createOrder">
				<!-- 1 -->
				<van-field required input-align="right" label="维保类型">
					<template #input>
						<van-radio-group v-model="mtType" direction="horizontal">
							<van-radio name="0">常规保养</van-radio>
							<van-radio name="1">车辆维修</van-radio>
						</van-radio-group>
					</template>
				</van-field>

				<!-- 2 -->
				<van-field required readonly clickable input-align="right" label="城市" :value="city" placeholder="请选择"
					:rules="[{required:true,message:'请选择城市'}]" @click="showCity=true"></van-field>

				<!-- 3 -->
				<van-field required readonly clickable input-align="right" label="服务站" :value="station"
					placeholder="请选择" :rules="[{required:true,message:'请选择服务站'}]" @click="chooseStation()"></van-field>


				<!-- 4 -->
				<van-field required readonly clickable input-align="right" label="日期" :value="appointDate"
					placeholder="请选择" :rules="[{required:true,message:'请选择日期'}]" @click="showDate=true"></van-field>

				<!-- 5 -->
				<van-field required readonly clickable input-align="right" label="时间" :value="appointTime"
					placeholder="请选择" :rules="[{required:true,message:'请选择时间'}]" @click="chooseTime"></van-field>

				<!-- 6 -->
				<van-field required readonly clickable input-align="right" label="车辆" :value="car" placeholder="请选择"
					:rules="[{required:true,message:'请选择车辆'}]" @click="showCar=true"></van-field>

				<!-- 7 -->
				<van-field required readonly clickable input-align="right" label="车牌号" v-model="licensetag"
					placeholder="请选择" :rules="[{required:true,message:'请选择车牌'}]"></van-field>

				<van-field required input-align="right" label="联系人">
					<template #input>
						<van-radio-group v-model="contacts" direction="horizontal">
							<van-radio name="1">车主</van-radio>
							<van-radio name="2">其他</van-radio>
						</van-radio-group>
					</template>
				</van-field>

				<van-field v-model="fullName" required label="联系人姓名" maxlength="10" input-align="right"
					placeholder="请输入" :rules="[{ required: true, message: '请输入姓名' }]" />
				<van-field v-model="telephone" required label="联系电话" type="number" input-align="right" placeholder="请输入"
					:rules="[{ validator:verifyPhone,required: true, message: '请输入正确的联系电话' }]" />

				<div class="submitBox">
					<van-button round block type="info" native-type="submit">预约提交</van-button>
				</div>
			</van-form>
		</div>

		<!-- 2 -->
		<van-popup v-model:show="showCity" position="bottom">
			<van-area :area-list="areaList" :columns-num="2" @confirm="cityConfirm" @cancel="showCity=false"></van-area>
		</van-popup>

		<!-- 3 -->
		<van-popup v-model:show="showStation" position="bottom">
			<van-picker show-toolbar :columns="columns" @confirm="stationConfirm"
				@cancel="showStation=false"></van-picker>
		</van-popup>

		<!-- 4 -->
		<van-calendar v-model:show="showDate" :min-date="new Date()" @confirm="dateConfirm" />

		<!-- 5 -->
		<van-popup v-model:show="showTime" position="bottom">
			<van-datetime-picker type="time" :min-hour="minHour" :min-minute="minMinutes" @confirm="timeConfirm"
				@cancel="showTime=false" />
		</van-popup>

		<!-- 6 -->
		<van-popup v-model:show="showCar" position="bottom">
			<van-picker show-toolbar :columns="carList" @confirm="carConfirm" @cancel="showCar=false"></van-picker>
		</van-popup>

		<div class="programme" v-show="mtType=='1'">
			<div class="title">维修方案</div>
			<table class="tabelBox">
				<tr>
					<th>序号</th>
					<th>维修类目</th>
					<th>更换零件</th>
					<th>单项价格</th>
					<th>维修总价</th>
					<th>维修周期</th>
				</tr>
				<tr v-for="(item,index) in randomPlan" :key="index">
					<td>{{index+1}}</td>
					<td>{{item.category}}</td>
					<td>{{item.replacementPart}}</td>
					<td>¥{{item.unitPrice.toFixed(2)}}</td>
					<td>¥{{item.totalPrice.toFixed(2)}}</td>
					<td>{{item.duration}}天</td>
				</tr>
				<tr>
					<td colspan="6" class="totalAmount">共计：¥{{totalAmount.toFixed(2)}}</td>
				</tr>
			</table>
		</div>

		<div class="signature" v-show="mtType=='1'">
			<div class="title">客户签字</div>
			<p class="signTips">注：确认上述方案无误后，请在下方签名，注意不要连笔。</p>
			<img v-if="imgSrc.length>0" :src="imgSrc" class="signedImg" />
			<SignBoard v-else @confirm="cvsCfm" />
		</div>
	</div>
</template>

<script setup>
import {
		areaList as areaData
	} from '@vant/area-data';
import {
		carInfoList,
		stationList,
		planRandomList,
		appointmentAdd,
		commonUpload
	} from '@/api/service'
import SignBoard from '@/components/SignBoard.vue';
import {
		Col
	} from 'vant';
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useVantCompat } from '@/composables/useVantCompat'

const router = useRouter()
const route = useRoute()
const { toast, notify, dialog } = useVantCompat()
const randomPlan = ref([])
const totalAmount = ref(0)
const imgSrc = ref('')
const signImg = ref('')
const mtType = ref("0")
const showCity = ref(false)
const city = ref("")
const areaList = areaData
const cityId = ref(0)
const columns = ref([])
const showStation = ref(false)
const station = ref("")
const stationData = ref([])
const stationId = ref(0)
const contacts = ref("1")
const fullName = ref('')
const telephone = ref('')
const showDate = ref(false)
const appointDate = ref('')
const showTime = ref(false)
const appointTime = ref('')
const licensetag = ref('')
const showCar = ref(false)
const car = ref('')
const carList = ref([])
const carData = ref([])
const carId = ref(0)
const minHour = ref('')
const pickCar = ref('1')
const minMinutes = ref('')
function goBack() {
				router.go(-1);
			}
function gotoHistory() {
				router.push({
					path: '/service/record'
				});
			}
function onSubmit(values) {
				console.log('submit', values);
			}
function typeChange(e) {
				console.log(e);
				mtType.value = e;
			}
function cityConfirm(e) {
				console.log(e);
				city.value = e[1].name;
				cityId.value = e[1].code;
				showCity.value = false;
				getStationList();
			}
function getStationList() {
				station.value = "";
				stationId.value = 0;
				columns.value = [];
				stationList({
					cityId: cityId.value
				}).then(res => {
					console.log(res);
					stationData.value = res.rows;
					let stations = []
					res.rows.forEach(item => {
						stations.push(item.serviceStationName);
					})
					columns.value = stations;
					if (stationData.value.length === 0) {
						toast.fail('该城市暂无服务站，请选择其他城市');
						city.value = '';
						cityId.value = 0;
					}
				})
			}
function chooseStation() {
				if (city.value.length > 0) {
					if (stationData.value.length === 0) {
						toast.fail("没有服务站")
					} else {
						showStation.value = true;
					}
				} else {
					toast("请选择城市")
				}
			}
function stationConfirm(e, index) {
				console.log(e);
				station.value = e;
				stationId.value = stationData.value[index].id;
				showStation.value = false;
			}
function dateConfirm(e) {
				console.log(e);
				appointDate.value = `${e.getFullYear()}-${e.getMonth()+1}-${e.getDate()}`;
				showDate.value = false;
			}
function chooseTime(e) {
				if (appointDate.value == currentTime()) {
					minHour.value = new Date().getHours();
					minMinutes.value = new Date().getMinutes();
				} else {
					minHour.value = 0;
					minMinutes.value = 0;
				}
				showTime.value = true;
			}
function currentTime() {
				let acurrent = new Date();
				let year = acurrent.getFullYear();
				let month = acurrent.getMonth() + 1;
				let date = acurrent.getDate();
				let current = year + "-" + month + "-" + date;
				return current;
			}
function timeConfirm(e) {
				console.log(e);
				appointTime.value = e;
				showTime.value = false;
			}
function getCarList() {
				const userInfoStr = window.localStorage.getItem("userInfo");
				if (!userInfoStr) {
					toast.fail('请先登录');
					return;
				}
				let userId = JSON.parse(userInfoStr).userId;
				carInfoList(userId).then(res => {
					console.log(res);
					carData.value = res.data;
					let columns = [];
					res.data.forEach(item => {
						columns.push(item.car.carName + " " + item.car.carModels);
					})
					carList.value = columns;
				})
			}
function carConfirm(e, index) {
				car.value = e;
				// 兼容 carID 和 carId 两种字段名
				const picked = carData.value[index] || {};
				carId.value = picked.carID || picked.carId || (picked.car && (picked.car.carId || picked.car.carID));
				licensetag.value = picked.car ? picked.car.licenseTag : '';
				showCar.value = false;
			}
function getPlanList() {
				planRandomList().then(res => {
					if (res.code == 200) {
						let total = 0;
						randomPlan.value = res.data;
						res.data.forEach(item => {
							total += item.totalPrice
						});
						totalAmount.value = total;
					}
				})
			}
function verifyPhone(val) {
				let pattern = /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/;
				return pattern.test(val);
			}
function createOrder() {
				if (mtType.value == 0) {
					let formInfo = {
						type: mtType.value, //类型
						carId: carId.value, //车辆id
						customerName: fullName.value, //联系人姓名
						customerPhone: telephone.value, //联系人电话
						appointDateStr: appointDate.value, //预约日期
						appointTimeStr: appointTime.value, //预约时间
						maintenanceServiceStationId: stationId.value //维保服务站id
					}

					appointmentAdd(formInfo).then(res => {
						if (res.code == 200) {
							notify({
								type: 'success',
								message: '提交成功！'
							});
							router.push({
								path: '/pay?money=' + res.data.paymentAmount + '&payId=' + res.data.paymentId + '&workNo=' + res.data.workNo + '&orderId=' + res.data.id
							})
						}
					})
				} else if (mtType.value == 1) {
					let formInfo = {
						type: mtType.value,
						carId: carId.value,
						customerName: fullName.value,
						customerPhone: telephone.value,
						appointDateStr: appointDate.value,
						appointTimeStr: appointTime.value,
						maintenanceServiceStationId: stationId.value,
						planList: randomPlan.value,
						customerSignature: signImg.value //客户签名
					}

					if (signImg.value.length > 0) {
						appointmentAdd(formInfo).then(res => {
							if (res.code == 200) {
								notify({
									type: 'success',
									message: '提交成功！'
								});
								router.push({
									path: '/pay?money=' + res.data.paymentAmount + '&payId=' + res.data
										.paymentId + '&workNo=' + res.data.workNo + '&orderId=' + res.data.id
								})
							}
						})

					} else {
						toast.fail('请确认签名！');
					}
				}
			}
function cvsCfm(data) {
				// canvas图像转图片
				imgSrc.value = data.canvas.toDataURL("image/png");

				if (imgSrc.value) {
					//图片解码
					const byteString = window.atob(imgSrc.value.split(',')[1]);
					const mimeString = imgSrc.value.split(',')[0].split(':')[1].split(';')[0];
					const ab = new ArrayBuffer(byteString.length);
					const ia = new Uint8Array(ab);
					for (let i = 0; i < byteString.length; i++) {
						ia[i] = byteString.charCodeAt(i);
					}
					const blob = new Blob([ab], {
						type: mimeString
					});
					let formData = new FormData();
					formData.append('file', blob, `sign${new Date().getTime()}.png`)
					commonUpload(formData).then(res => {
						if (res.code == 200) {
							signImg.value = res.fileName;
							notify({
								type: 'success',
								message: '签名已上传！'
							});
						} else {
							imgSrc.value = '';
							signImg.value = '';
							toast.fail('签名失败，请重试！');
						}
					})
				}
			}
getCarList();
			getPlanList();
</script>

<style lang="scss" scoped>
	.reservationWrap {
		padding-bottom: 65px;
		background-color: var(--bg-primary);
    min-height: 100vh;

    :deep(.van-nav-bar) {
      background: var(--bg-glass);
      backdrop-filter: blur(var(--card-blur));
      border-bottom: 1px solid var(--border);
      .van-nav-bar__title { color: var(--text-primary); font-weight: 300; }
      .van-icon, .van-nav-bar__text { color: var(--accent); }
    }

		.formWrapper {
			margin-top: 46px;
      :deep(.van-cell-group) { background: transparent; }
      :deep(.van-cell) {
        background: var(--bg-card);
        margin-bottom: 1px;
        color: var(--text-primary);
        .van-field__label { color: var(--text-secondary); }
        .van-field__control { color: var(--text-primary); }
      }
      :deep(.van-radio__label) { color: var(--text-primary); }
      :deep(.van-radio__icon--checked .van-icon) { background-color: var(--accent); border-color: var(--accent); }
		}

		.programme,
		.signature {
			box-sizing: border-box;
			padding: 20px 15px;

			.title {
				font-size: 16px;
				font-weight: 300;
				text-align: left;
				line-height: 40px;
        color: var(--accent);
        letter-spacing: 1px;
			}

			.tabelBox {
				border-collapse: collapse;
        width: 100%;
        background: var(--bg-card);
        border: 1px solid var(--border);

				th,
				td {
					font-size: 12px;
					border: 1px var(--border) solid;
					padding: 8px 5px;
          color: var(--text-secondary);
          text-align: center;
				}
        th { color: var(--text-primary); background: rgba(255,255,255,0.05); }
        .totalAmount {
          text-align: right;
          padding-right: 15px;
          color: var(--accent);
          font-weight: 600;
        }
			}
		}

		.signature {
			padding: 0 15px 40px 15px;

			.signTips {
				font-size: 12px;
				padding-top: 5px;
				margin-bottom: 10px;
				text-align: left;
        color: var(--text-tertiary);
			}
      .signedImg {
        width: 100%;
        background: var(--bg-card);
        border: 1px solid var(--border);
        border-radius: 8px;
      }
		}
	}

	.submitBox {
		width: 100%;
		position: fixed;
		left: 0;
		bottom: 0;
		z-index: 9;
		box-sizing: border-box;
		padding: 10px 15px;
		border-top: 1px var(--border) solid;
		background-color: var(--bg-glass);
    backdrop-filter: blur(var(--card-blur));

    :deep(.van-button--info) {
      background: var(--accent-gradient);
      border: none;
      box-shadow: 0 4px 12px var(--accent-glow);
    }
	}
</style>
