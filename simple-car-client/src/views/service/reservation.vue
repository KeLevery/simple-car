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
		<van-popup v-model="showCity" position="bottom">
			<van-area :area-list="areaList" :columns-num="2" @confirm="cityConfirm" @cancel="showCity=false"></van-area>
		</van-popup>

		<!-- 3 -->
		<van-popup v-model="showStation" position="bottom">
			<van-picker show-toolbar :columns="columns" @confirm="stationConfirm"
				@cancel="showStation=false"></van-picker>
		</van-popup>

		<!-- 4 -->
		<van-calendar v-model="showDate" :min-date="new Date()" @confirm="dateConfirm" />

		<!-- 5 -->
		<van-popup v-model="showTime" position="bottom">
			<van-datetime-picker type="time" :min-hour="minHour" :min-minute="minMinutes" @confirm="timeConfirm"
				@cancel="showTime=false" />
		</van-popup>

		<!-- 6 -->
		<van-popup v-model="showCar" position="bottom">
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

<script>
	import {
		areaList
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
	export default {
		components: {
			SignBoard
		},
		data() {
			return {
				randomPlan: [],
				totalAmount: 0,
				imgSrc: '',
				signImg: '',
				mtType: "0",
				showCity: false,
				city: "",
				areaList: areaList,
				cityId: 0,
				columns: [],
				showStation: false,
				station: "",
				stationData: [],
				stationId: 0,
				contacts: "1",
				fullName: '',
				telephone: '',
				showDate: false,
				appointDate: '',
				showTime: false,
				appointTime: '',
				licensetag: '',
				showCar: false,
				car: '',
				carList: [],
				carData: [],
				carId: 0,
				minHour: '',
				pickCar: '1',
				minMinutes: '',
			};
		},
		created() {
			this.getCarList();
			this.getPlanList();
		},
		methods: {
			goBack() {
				this.$router.go(-1);
			},
			gotoHistory() {
				this.$router.push({
					path: '/service/record'
				});
			},
			onSubmit(values) {
				console.log('submit', values);
			},
			typeChange(e) {
				console.log(e);
				this.mtType = e;
			},


			cityConfirm(e) {
				console.log(e);
				this.city = e[1].name;
				this.cityId = e[1].code;
				this.showCity = false;
				this.getStationList();
			},
			getStationList() {
				this.station = "";
				this.stationId = 0;
				this.columns = [];
				stationList({
					cityId: this.cityId
				}).then(res => {
					console.log(res);
					this.stationData = res.rows;
					let stations = []
					res.rows.forEach(item => {
						stations.push(item.serviceStationName);
					})
					this.columns = stations;
					if (this.stationData.length === 0) {
						this.$toast.fail('该城市暂无服务站，请选择其他城市');
						this.city = '';
						this.cityId = 0;
					}
				})
			},

			chooseStation() {
				if (this.city.length > 0) {
					if (this.stationData.length === 0) {
						this.$toast.fail("没有服务站")
					} else {
						this.showStation = true;
					}
				} else {
					this.$toast("请选择城市")
				}
			},
			stationConfirm(e, index) {
				console.log(e);
				this.station = e;
				this.stationId = this.stationData[index].id;
				this.showStation = false;
			},

			//日期的选择
			dateConfirm(e) {
				console.log(e);
				this.appointDate = `${e.getFullYear()}-${e.getMonth()+1}-${e.getDate()}`;
				this.showDate = false;
			},
			//时间的选择
			chooseTime(e) {
				if (this.appointDate == this.currentTime()) {
					this.minHour = new Date().getHours();
					this.minMinutes = new Date().getMinutes();
				} else {
					this.minHour = 0;
					this.minMinutes = 0;
				}
				this.showTime = true;
			},
			currentTime() {
				let acurrent = new Date();
				let year = acurrent.getFullYear();
				let month = acurrent.getMonth() + 1;
				let date = acurrent.getDate();
				let current = year + "-" + month + "-" + date;
				return current;
			},
			timeConfirm(e) {
				console.log(e);
				this.appointTime = e;
				this.showTime = false;
			},

			getCarList() {
				const userInfoStr = window.localStorage.getItem("userInfo");
				if (!userInfoStr) {
					this.$toast.fail('请先登录');
					return;
				}
				let userId = JSON.parse(userInfoStr).userId;
				carInfoList(userId).then(res => {
					console.log(res);
					this.carData = res.data;
					let columns = [];
					res.data.forEach(item => {
						columns.push(item.car.carName + " " + item.car.carModels);
					})
					this.carList = columns;
				})
			},
			carConfirm(e, index) {
				this.car = e;
				// 兼容 carID 和 carId 两种字段名
				const picked = this.carData[index] || {};
				this.carId = picked.carID || picked.carId || (picked.car && (picked.car.carId || picked.car.carID));
				this.licensetag = picked.car ? picked.car.licenseTag : '';
				this.showCar = false;
			},


			getPlanList() {
				planRandomList().then(res => {
					if (res.code == 200) {
						let total = 0;
						this.randomPlan = res.data;
						res.data.forEach(item => {
							total += item.totalPrice
						});
						this.totalAmount = total;
					}
				})
			},

			verifyPhone(val) {
				let pattern = /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/;
				return pattern.test(val);
			},

			createOrder() {
				if (this.mtType == 0) {
					let formInfo = {
						type: this.mtType, //类型
						carId: this.carId, //车辆id
						customerName: this.fullName, //联系人姓名
						customerPhone: this.telephone, //联系人电话
						appointDateStr: this.appointDate, //预约日期
						appointTimeStr: this.appointTime, //预约时间
						maintenanceServiceStationId: this.stationId //维保服务站id
					}

					appointmentAdd(formInfo).then(res => {
						if (res.code == 200) {
							this.$notify({
								type: 'success',
								message: '提交成功！'
							});
							this.$router.push({
								path: '/pay?money=' + res.data.paymentAmount + '&payId=' + res.data.paymentId + '&workNo=' + res.data.workNo + '&orderId=' + res.data.id
							})
						}
					})
				} else if (this.mtType == 1) {
					let formInfo = {
						type: this.mtType,
						carId: this.carId,
						customerName: this.fullName,
						customerPhone: this.telephone,
						appointDateStr: this.appointDate,
						appointTimeStr: this.appointTime,
						maintenanceServiceStationId: this.stationId,
						planList: this.randomPlan,
						customerSignature: this.signImg //客户签名
					}

					if (this.signImg.length > 0) {
						appointmentAdd(formInfo).then(res => {
							if (res.code == 200) {
								this.$notify({
									type: 'success',
									message: '提交成功！'
								});
								this.$router.push({
									path: '/pay?money=' + res.data.paymentAmount + '&payId=' + res.data
										.paymentId + '&workNo=' + res.data.workNo + '&orderId=' + res.data.id
								})
							}
						})

					} else {
						this.$toast.fail('请确认签名！');
					}
				}
			},
			cvsCfm(data) {
				// canvas图像转图片
				this.imgSrc = data.canvas.toDataURL("image/png");

				if (this.imgSrc) {
					//图片解码
					const byteString = window.atob(this.imgSrc.split(',')[1]);
					const mimeString = this.imgSrc.split(',')[0].split(':')[1].split(';')[0];
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
							this.signImg = res.fileName;
							this.$notify({
								type: 'success',
								message: '签名已上传！'
							});
						} else {
							this.imgSrc = '';
							this.signImg = '';
							this.$toast.fail('签名失败，请重试！');
						}
					})
				}
			}

		}
	};
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
