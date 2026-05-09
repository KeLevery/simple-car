<template>
	<div>
		<div class="carBox">
			<div class="carInfo">
				<div class="mileage">
					<div class="mileageNum">{{enduranceMileage}}</div>
					<div class="mileageUnit">
						<div>可续航</div>
						<div class="mileKm">km</div>
					</div>
				</div>

				<div>
					<div class="progress">
						<div class="progress1">
							<van-progress stroke-width="6" :percentage="remainingPower" :show-pivot="false"
								color="#52bb3d"></van-progress>
						</div>
						<div style="margin-left: 10px;">70%</div>
					</div>
					<div class="updateTime">更新于：{{updateTime}}</div>
				</div>
			</div>

			<div class="car">
				<img src="../../assets/logo.png" />
			</div>

			<div class="controlDoor">
				<div class="doorItme" @click="doorTips('车门已解锁',0)">
					<div class="imgBox">
						<img :src="controlAct==0?controlList[0].actSrc:controlList[0].defaultSrc"/>
					</div>
					<h5 :class="controlAct==0?'active':''">车门解锁</h5>
				</div>
				<div class="doorItme">
					<div class="imgBox">
						<img />
					</div>
					<h5>车门解锁</h5>
				</div>
				<div class="doorItme">
					<div class="imgBox">
						<img />
					</div>
					<h5>车门解锁</h5>
				</div>
				<div class="doorItme">
					<div class="imgBox">
						<img />
					</div>
					<h5>车门解锁</h5>
				</div>
			</div>

			<div class="carControl">
				<div class="carItem" @click="gotoPage('/control')">
					<img src="@/assets/control/control.png" />
					<h5>车辆控制</h5>
				</div>
			</div>
		</div>

		<van-tabbar v-model="active" route>
			<van-tabbar-item>
				<span>社区</span>
				<template #icon>
					<img src="@/assets/tabbar/community.png" />
				</template>
			</van-tabbar-item>
			<van-tabbar-item to="/analysis">
				<span>数据分析</span>
				<template #icon>
					<img src="@/assets/tabbar/analysis.png" />
				</template>
			</van-tabbar-item>
			<van-tabbar-item to="/home">
				<template #icon>
					<img src="@/assets/tabbar/home_act.png" />
				</template>
			</van-tabbar-item>
			<van-tabbar-item to="/service">
				<span>服务</span>
				<template #icon>
					<img src="@/assets/tabbar/service.png" />
				</template>
			</van-tabbar-item>
			<van-tabbar-item to="/mine">
				<span>我的</span>
				<template #icon>
					<img src="@/assets/tabbar/mine.png" />
				</template>
			</van-tabbar-item>
		</van-tabbar>
	</div>
</template>

<script setup>
import {
		carInfo
	} from '@/api/carInfo'
import unlockIcon from '@/assets/unlock.png'
import unlockActiveIcon from '@/assets/unlock_act.png'
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useVantCompat } from '@/composables/useVantCompat'

const router = useRouter()
const route = useRoute()
const { toast, notify, dialog } = useVantCompat()
const active = ref('/home')
const carId = ref(0)
const enduranceMileage = ref(0)
const remainingPower = ref(0)
const controlAct = ref(null)
const updateTime = ref('')
const controlList = ref([{
						defaultSrc: unlockIcon,
						actSrc: unlockActiveIcon
					},
				])
function getCarInfo() {
				carInfo(carId.value).then(res => {
					console.log(res);
					enduranceMileage.value = res.data.enduranceMileage
					remainingPower.value = res.data.remainingPower
					updateTime.value = res.data.updateTime
				})
			}
function doorTips(text, index) {
				if(controlAct.value==index){
					controlAct.value=null
				}else{
					notify({type:'success',message:text})
					controlAct.value=index
				}
			}
function gotoPage(url) {
				router.push({path:url})
			}
carId.value = JSON.parse(window.localStorage.getItem('carInfo')).carId;
			getCarInfo()
</script>

<style>
	.carInfo {
		/* border: 1px solid red; */
		margin-top: 30px;
	}

	.mileage {
		display: flex;
		justify-content: center;
	}

	.mileageNum {
		font-size: 40px;
		font-weight: 600;
	}

	.mileageUnit {
		font-size: 14px;
		line-height: 14px;
		box-sizing: border-box;
		padding-top: 10px;
	}

	.mileKm {
		text-align: left;
	}

	.progress {
		width: 44%;
		margin: 0 auto;
		display: flex;
		align-items: center;
	}

	.progress1 {
		flex: auto;
	}

	.updateTime {
		font-size: 12px;
		color: #888;
	}

	.controlDoor {
		padding: 20px 15px;
		display: flex;
		justify-content: space-between;
	}

	.doorItme {
		flex: 1;
	}

	.imgBox {
		width: 56px;
		height: 56px;
		background: #fff;
		border-radius: 50%;
		text-align: center;
		margin: 0 auto;
		box-shadow: 0 0 2px rgba(0, 0, 0, .1);
	}

	.imgBox img {
		width: 26px;
		padding-top: 14px;
	}

	.doorItme h5 {
		margin: 0;
		font-weight: 500;
		padding-top: 10px;
		font-size: 14px;
	}

	.carControl {
		/* border: 1px solid red; */
		margin-top: 15px;
		padding: 0 15px;
	}

	.carItem {
		height: 50px;
		display: flex;
		justify-content: left;
		align-items: center;
		/* padding: 15px 10px; */
		background-color: #fff;
		border-radius: 10px;
		box-shadow: 0 0 2px #cdc9c9;
	}

	.carItem img {
		display: flex;
		width: 30px;
		padding-left: 10px;
		padding-right: 10px;
	}

	.carItem h5 {
		font-size: 16px;
		font-weight: 500;
	}

	.car {
		/* border: 1px solid rebeccapurple; */
		width: 100%;
		height: 150px;
		margin-top: 20px;
		padding: 0;
	}

	.car img {
		width: 100%;
		height: 100%;
	}
	
	.active{
		color: blue;
	}
</style>