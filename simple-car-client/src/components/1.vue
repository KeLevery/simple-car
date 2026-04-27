<template>
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
  import { getBaseUrl } from '@/util/env';
  import * as THREE from "three";
  import { TWEEN } from "three/examples/jsm/libs/tween.module.min.js";
  import { OrbitControls } from "three/examples/jsm/controls/OrbitControls";
  import { loadGLTFModel } from "../lib/model";
  
  export default {
	data() {
	  return {
		showLoading: false,
		GLBs: [
		  {
			name: "EXT",
			path: getBaseUrl()+"/profile/avatar/2023/12/23/static/data/lynkco09/model/Lynkco09_EXT_d.glb",
		  },
		  {
			name: "INT",
			path: getBaseUrl()+"/profile/avatar/2023/12/23/static/data/lynkco09/model/Lynkco09_INT_d.glb",
		  },
		  {
			name: "Sunproof",
			path: getBaseUrl()+"/profile/avatar/2023/12/23/static/data/lynkco09/model/Lynkco09_Sunproof_d.glb",
		  },
		  {
			name: "Trunk",
			path: getBaseUrl()+"/profile/avatar/2023/12/23/static/data/lynkco09/model/Lynkco09_Trunk_d.glb",
		  },
		  {
			name: "Tires",
			path: getBaseUrl()+"/profile/avatar/2023/12/23/static/data/lynkco09/model/Lynkco09_Tires_d.glb",
		  },
		  {
			name: "LBDoor",
			path: getBaseUrl()+"/profile/avatar/2023/12/23/static/data/lynkco09/model/Lynkco09_LBDoor_d.glb",
		  },
		  {
			name: "LFDoor",
			path: getBaseUrl()+"/profile/avatar/2023/12/23/static/data/lynkco09/model/Lynkco09_LFDoor_d.glb",
		  },
		  {
			name: "RFDoor",
			path: getBaseUrl()+"/profile/avatar/2023/12/23/static/data/lynkco09/model/Lynkco09_RFDoor_d.glb",
		  },
		  {
			name: "RBDoor",
			path: getBaseUrl()+"/profile/avatar/2023/12/23/static/data/lynkco09/model/Lynkco09_RBDoor_d.glb",
		  },
		],
		controls: null,
		camera: null,
		scene: null,
		models: [],
		tweenCollection: {
		  LBDoor: {
			tween: null,
			from: {
			  value: null,
			},
			to: {
			  value: null,
			},
		  },
		  RBDoor: {
			tween: null,
			from: {
			  value: null,
			},
			to: {
			  value: null,
			},
		  },
		  LFDoor: {
			tween: null,
			from: {
			  value: null,
			},
			to: {
			  value: null,
			},
		  },
		  RFDoor: {
			tween: null,
			from: {
			  value: null,
			},
			to: {
			  value: null,
			},
		  },
		  Trunk: {
			tween: null,
			from: {
			  value: null,
			},
			to: {
			  value: null,
			},
		  },
		},
		refRenderer: {
		  current: null,
		}
	  };
	},
	mounted() {
	  this.init();
	  window.addEventListener('click', this.pickupObjects, false) // 监听单击拾取对象初始化球体
	},
	methods: {
	  easeOutCirc(x) {
		return Math.sqrt(1 - Math.pow(x - 1, 4));
	  },
	  handleWindowResize() {},
	  // 拾取对象
	  pickupObjects(event) {
		const container = document.getElementsByClassName("css-1xtnbsj")[0];
		if (container) {
		  const scW = container.clientWidth;
		  const scH = container.clientHeight;
		  const offsetLeft = container.offsetLeft;
		  const offsetTop = container.offsetTop;
  
		  let mouse = new THREE.Vector2();
		  mouse.x = ((event.clientX - offsetLeft) / scW) * 2 - 1;
		  mouse.y = -((event.clientY - offsetTop) / scH) * 2 + 1;
		  //创建光线投射对象：从相机所在位置到鼠标点击的位置的连线画一条射线，射线穿过的物体就会被拾取
		  let raycaster = new THREE.Raycaster();
		  raycaster.setFromCamera(mouse, this.camera);
		  // 获取射线交叉的对象
		  let intersects = raycaster.intersectObjects(this.scene.children);
		  
		  if (intersects.length > 0) {
			if (
			  intersects[0].object.name.includes("Door") ||
			  intersects[0].object.name.includes("Trunk")
			) {
			  let doorName = intersects[0].object.name.split("_")[0];
			  let door = this.models.find((item) => item.name === doorName);
			  if (door && door.outer && door.status) {
				this.setupTweenDoor(door, door.status);
			  }
			}
			if (intersects[0].object.name.includes("INT")) {
			  this.controls.autoRotate = false;
			  let INT = this.models.find((item) => item.name === "INT");
			  this.setupTweenCarIn(INT);
			}
		  }
		}
	  },
	  setupTweenCarIn(model) { // 进入车辆内部
		const { x: cx, y: cy, z: cz } = this.camera.position;
		const { x: tocx, y: tocy, z: tocz } = model.carInCameraPosition;
		this.setupTweenCamera(
		  { cx, cy, cz, ox: 0, oy: 0, oz: 0 },
		  { cx: tocx, cy: tocy, cz: tocz, ox: 0, oy: tocy, oz: 0.1 }
		);
	  },
	  // 开门或关门动画
	  setupTweenDoor(door, status) { 
		const { from, to } = door.rotateDirection[status];
		// 模型初始化时默认设置
		// door.rotateDirection = {
		//   rotateAxis: 'y',
		//   status: 'close',
		//   open: {
		//     from: { value: -50 },
		//     to: { value: 0 }
		//   },
		//   close: {
		//     from: { value: 0 },
		//     to: { value: -50 }
		//   }
		// }
		let that = this;
		// 判断开启或关闭状态
		if (status == "open") {
		  door.status = "close";
		}
		if (status == "close") {
		  door.status = "open";
		}
		// TWEEN.removeAll()
		let lastLocation = null;
		if (this.tweenCollection[door.name].tween) {
		  lastLocation = { value: this.tweenCollection[door.name].from.value };
		  this.tweenCollection[door.name].tween.stop();
		} else {
		  lastLocation = { value: from.value };
		}
		this.tweenCollection[door.name].tween = new TWEEN.Tween(lastLocation)
		  .to(to, 1000)
		  .easing(TWEEN.Easing.Cubic.InOut)
		  .onUpdate(function (lastLocation) {
			door.outer.rotation[door.rotateDirection.rotateAxis] =
			  THREE.MathUtils.degToRad(lastLocation.value);
			that.tweenCollection[door.name].from.value = lastLocation.value;
		  })
		  .onComplete(() => {
			that.tweenCollection[door.name] = {
			  tween: null,
			  from: { value: null },
			  to: { value: null },
			};
		  })
		  .start();
	  },
	  // 相机视角位置切换动画
	  setupTweenCamera(source, target) {
			let thats = this;
		const carTween = new TWEEN.Tween(source)
		  .to(target, 2000)
		  .easing(TWEEN.Easing.Quadratic.Out);
			carTween.onUpdate(function (that) {
			  thats.camera.position.set(that.cx, that.cy, that.cz);
			  thats.controls.target.set(that.ox, that.oy, that.oz);
		  });
		  carTween.start();
	  },
	  init() {
		// const container = this.$refs.refContainer
		this.showLoading = true;
		const container = document.getElementsByClassName("css-1xtnbsj")[0];
		
		if (container) {
		  const scW = container.clientWidth;
		  const scH = container.clientHeight;
		  // 实例化渲染器
		  const renderer = new THREE.WebGLRenderer({
			antialias: true,
			alpha: true,
		  });
		  renderer.setPixelRatio(window.devicePixelRatio);
		  // 设置渲染尺寸大小
		  renderer.setSize(scW, scH);
		  renderer.outputEncoding = THREE.sRGBEncoding;
		  console.log(container);
		  // 将渲染器挂载到dom上
		  container.appendChild(renderer.domElement);
		  this.refRenderer.current = renderer;
		  // 创建3D场景
		  this.scene = new THREE.Scene();
  
		  const target = new THREE.Vector3(-0.5, 0.5, 0);
  
		  const initialCameraPosition = new THREE.Vector3(
			5 * Math.sin(0.2 * Math.PI),
			2.5,
			5 * Math.cos(0.2 * Math.PI)
		  );
		  // 创建相机并设置位置和角度
		  this.camera = new THREE.PerspectiveCamera(40, scW / scH, 0.1, 1000);
		  this.camera.position.copy(initialCameraPosition);
		  this.camera.lookAt(target);
		  // 创建相机控件（缩放、平移、旋转）
		  this.controls = new OrbitControls(this.camera, renderer.domElement);
		  this.controls.autoRotate = false;
		  this.controls.enableZoom = true;
		  this.controls.minDistance = 1;
		  this.controls.maxDistance = 6;
		  this.controls.target = target;
  
		  // 添加灯光
		  const light1 = new THREE.DirectionalLight(0xffffff, 0.2);
		  light1.position.set(0, 0, 10);
		  this.scene.add(light1);
		  const light2 = new THREE.DirectionalLight(0xffffff, 0.2);
		  light2.position.set(0, 0, -10);
		  this.scene.add(light2);
		  const light3 = new THREE.DirectionalLight(0xffffff, 0.2);
		  light3.position.set(10, 0, 0);
		  this.scene.add(light3);
		  const light4 = new THREE.DirectionalLight(0xffffff, 0.2);
		  light4.position.set(-10, 0, 0);
		  this.scene.add(light4);
  
		  const light5 = new THREE.DirectionalLight(0xffffff, 0.2);
		  light5.position.set(0, 10, 0);
		  this.scene.add(light5);
		  const light6 = new THREE.DirectionalLight(0xffffff, 0.2);
		  light6.position.set(5, 10, 0);
		  this.scene.add(light6);
		  const light7 = new THREE.DirectionalLight(0xffffff, 0.2);
		  light7.position.set(0, 10, 5);
		  this.scene.add(light7);
		  const light8 = new THREE.DirectionalLight(0xffffff, 0.2);
		  light8.position.set(0, 10, -5);
		  this.scene.add(light8);
		  const light9 = new THREE.DirectionalLight(0xffffff, 0.2);
		  light9.position.set(-5, 10, 0);
		  this.scene.add(light9);
		  
		  Promise.all(
			this.GLBs.map((item) =>
			  loadGLTFModel(this.scene, item, this.refRenderer, {
				receiveShadow: false,
				castShadow: false,
			  })
			)
		  ).then((res) => {
			this.models = res;
			animate();
			this.showLoading = false;
		  });
  
		  let req = null;
		  let frame = 0;
		  const animate = () => {
			req = requestAnimationFrame(animate);
  
			frame = frame <= 100 ? frame + 1 : frame;
  
			if (frame <= 100) {
			  const p = initialCameraPosition;
			  const rotSpeed = -this.easeOutCirc(frame / 120) * Math.PI * 20;
			  this.camera.lookAt(target);
			} else {
			  this.controls.update();
			}
			TWEEN.update();
			renderer.render(this.scene, this.camera);
		  };
  
		  return () => {
			cancelAnimationFrame(req);
			renderer.domElement.remove();
			renderer.dispose();
		  };
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
  .loadPos{
	position: absolute;
	top:50%;
	left:50%;
	transform: translate3d(-50%,-50%,0);
  }
  </style>