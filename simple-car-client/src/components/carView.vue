<!-- <template>
	<div>
		<div class="css-1xtnbsj" ref="refContainer"></div>
		<van-overlay :show="showLoading">
			<div class="loadPos">
				<van-loading size="24px" vertical>加载中...</van-loading>
			</div>
		</van-overlay>
	</div>
</template>

<script>
	import {
		getBaseUrl
	} from '@/util/env';
	import * as THREE from "three";
	import {
		TWEEN
	} from "three/examples/jsm/libs/tween.module.min.js";
	import {
		OrbitControls
	} from "three/examples/jsm/controls/OrbitControls";
	import {
		loadGLTFModel
	} from "../lib/model";

	export default {
		data() {
			return {
				showLoading: false,
				GLBs: [{
						name: "EXT",
						path: getBaseUrl() +
							"/profile/avatar/2023/12/23/static/data/lynkco09/model/Lynkco09_EXT_d.glb",
					},
					{
						name: "INT",
						path: getBaseUrl() +
							"/profile/avatar/2023/12/23/static/data/lynkco09/model/Lynkco09_INT_d.glb",
					},
					{
						name: "Sunproof",
						path: getBaseUrl() +
							"/profile/avatar/2023/12/23/static/data/lynkco09/model/Lynkco09_Sunproof_d.glb",
					},
					{
						name: "Trunk",
						path: getBaseUrl() +
							"/profile/avatar/2023/12/23/static/data/lynkco09/model/Lynkco09_Trunk_d.glb",
					},
					{
						name: "Tires",
						path: getBaseUrl() +
							"/profile/avatar/2023/12/23/static/data/lynkco09/model/Lynkco09_Tires_d.glb",
					},
					{
						name: "LBDoor",
						path: getBaseUrl() +
							"/profile/avatar/2023/12/23/static/data/lynkco09/model/Lynkco09_LBDoor_d.glb",
					},
					{
						name: "LFDoor",
						path: getBaseUrl() +
							"/profile/avatar/2023/12/23/static/data/lynkco09/model/Lynkco09_LFDoor_d.glb",
					},
					{
						name: "RFDoor",
						path: getBaseUrl() +
							"/profile/avatar/2023/12/23/static/data/lynkco09/model/Lynkco09_RFDoor_d.glb",
					},
					{
						name: "RBDoor",
						path: getBaseUrl() +
							"/profile/avatar/2023/12/23/static/data/lynkco09/model/Lynkco09_RBDoor_d.glb",
					},
				],
				refRenderer: {
					current: null,
				},
				scene: null,
				camera: null,
				controls: null,
				models: [],
			};
		},
		mounted() {
			this.init();
		},
		methods: {
			init() {
				this.showLoading = true
				const container = document.getElementsByClassName('css-1xtnbsj')[0]

				if (container) {
					const scW = container.clientWidth;
					const scH = container.clientHeight;
					const renderer = new THREE.WebGLRenderer({
						antialias: true,
						alpha: true,
					})
					renderer.setPixelRatio(window.devicePixelRatio)
					//设置渲染器大小
					renderer.setSize(scW, scH)
					renderer.outputEncoding = THREE.sRGBEncoding
					//将渲染器挂载到dom上
					container.appendChild(renderer.domElement)
					this.refRenderer.current = renderer
					//创建3d场景
					this.scene = new THREE.Scene()
					const target = new THREE.Vector3(-0.5, 0.5, 0)
					const initialCameraPosition = new THREE.Vector3(
						5 * Math.sin(0.2 * Math.PI),
						2.5,
						5 * Math.cos(0.2 * Math.PI)
					)
					//创建相机
					this.camera = new THREE.PerspectiveCamera(40, scW / scH, 0.1, 1000)
					this.camera.position.copy(initialCameraPosition)
					this.camera.lookAt(target)
					//创建相机控件
					this.controls = new OrbitControls(this.camera, renderer.domElement)
					this.controls.autoRotate = false;
					this.controls.enableZoom = true
					this.controls.minDistance = 1
					this.controls.maxDistance = 6
					this.controls.target = target

					//创建灯光
					const light1 = new THREE.DirectionalLight(0xffffff, 0.2)
					light1.position.set(0, 0, 10)
					this.scene.add(light1)

					Promise.all(
						this.GLBs.map(item => {
							loadGLTFModel(this.scene, item, this.refRenderer, {
								receiveShadow: false,
								castShadow: false
							})
						})
					).then(res => {
						this.models = res
						animate()
						this.showLoading = false
					})

					let frame = 0
					const animate = () => {
						requestAnimationFrame(animate)
						frame = frame <= 100 ? frame + 1 : frame
						if (frame <= 100) {
							this.camera.lookAt(target)
						} else {
							this.controls.update()
						}
						TWEEN.update()
						renderer.render(this.scene, this.camera)
					}

				} else {
					this.showLoading = false;
				}
			},
		},
	};
</script>

<style scoped>
	.css-1xtnbsj {
		width: 100%;
		height: 220px;
		position: relative;
	}

	.loadPos {
		position: absolute;
		top: 50%;
		left: 50%;
		transform: translate3d(-50%, -50%, 0);
	}
</style> -->