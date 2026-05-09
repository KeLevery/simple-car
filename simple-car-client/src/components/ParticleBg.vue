<template>
	<div class="particle-bg" ref="container"></div>
</template>

<script setup>
import * as THREE from 'three'
import { onBeforeUnmount, onMounted, ref, toRefs } from 'vue'

defineOptions({ name: 'ParticleBg' })
const props = defineProps({
		particleCount: { type: Number, default: 60 },
		connectionDistance: { type: Number, default: 2.5 },
		particleColor: { type: Number, default: 0x00d4ff },
		speed: { type: Number, default: 0.002 },
		opacity: { type: Number, default: 0.5 },
		depth: { type: Number, default: 8 }
	})
const { particleCount, connectionDistance, particleColor, speed, opacity, depth } = toRefs(props)
const container = ref(null)
const animationId = ref(null)
const renderer = ref(null)
const scene = ref(null)
const camera = ref(null)
const mouse = ref({ x: 0, y: 0 })
let geometry = null
let material = null
let lineGeometry = null
let lineMaterial = null
let particlesData = null
let points = null
let lines = null
function initScene() {
			const containerEl = container.value
			if (!containerEl) return

			const w = containerEl.clientWidth
			const h = containerEl.clientHeight

			// Renderer
			renderer.value = new THREE.WebGLRenderer({ antialias: true, alpha: true })
			renderer.value.setSize(w, h)
			renderer.value.setPixelRatio(Math.min(window.devicePixelRatio, 2))
			containerEl.appendChild(renderer.value.domElement)

			// Scene & Camera
			scene.value = new THREE.Scene()
			camera.value = new THREE.PerspectiveCamera(60, w / h, 0.1, 1000)
			camera.value.position.z = 5

			// Particles
			const count = particleCount.value
			const d = depth.value
			const positions = new Float32Array(count * 3)
			particlesData = []

			for (let i = 0; i < count; i++) {
				positions[i * 3] = (Math.random() - 0.5) * d
				positions[i * 3 + 1] = (Math.random() - 0.5) * d
				positions[i * 3 + 2] = (Math.random() - 0.5) * d
				particlesData.push({
					velocity: new THREE.Vector3(
						(Math.random() - 0.5) * speed.value,
						(Math.random() - 0.5) * speed.value,
						(Math.random() - 0.5) * speed.value
					)
				})
			}

			geometry = new THREE.BufferGeometry()
			geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3))

			material = new THREE.PointsMaterial({
				color: particleColor.value,
				size: 0.04,
				transparent: true,
				opacity: opacity.value,
				sizeAttenuation: true,
				blending: THREE.AdditiveBlending,
				depthWrite: false
			})

			points = new THREE.Points(geometry, material)
			scene.value.add(points)

			// Connection Lines
			lineGeometry = new THREE.BufferGeometry()
			lineMaterial = new THREE.LineBasicMaterial({
				color: particleColor.value,
				transparent: true,
				opacity: 0.12,
				blending: THREE.AdditiveBlending,
				depthWrite: false
			})
			lines = new THREE.LineSegments(lineGeometry, lineMaterial)
			scene.value.add(lines)

			animate()
		}
function animate() {
			animationId.value = requestAnimationFrame(animate)

			const count = particleCount.value
			const half = depth.value / 2
			const posArr = geometry.attributes.position.array
			const linePositions = []
			const connDist = connectionDistance.value

			for (let i = 0; i < count; i++) {
				const pData = particlesData[i]
				posArr[i * 3] += pData.velocity.x
				posArr[i * 3 + 1] += pData.velocity.y
				posArr[i * 3 + 2] += pData.velocity.z

				if (Math.abs(posArr[i * 3]) > half) pData.velocity.x *= -1
				if (Math.abs(posArr[i * 3 + 1]) > half) pData.velocity.y *= -1
				if (Math.abs(posArr[i * 3 + 2]) > half) pData.velocity.z *= -1

				for (let j = i + 1; j < count; j++) {
					const dx = posArr[i * 3] - posArr[j * 3]
					const dy = posArr[i * 3 + 1] - posArr[j * 3 + 1]
					const dz = posArr[i * 3 + 2] - posArr[j * 3 + 2]
					const dist = Math.sqrt(dx * dx + dy * dy + dz * dz)
					if (dist < connDist) {
						linePositions.push(
							posArr[i * 3], posArr[i * 3 + 1], posArr[i * 3 + 2],
							posArr[j * 3], posArr[j * 3 + 1], posArr[j * 3 + 2]
						)
					}
				}
			}

			geometry.attributes.position.needsUpdate = true
			lineGeometry.setAttribute('position', new THREE.Float32BufferAttribute(linePositions, 3))

			// Subtle rotation following mouse
			points.rotation.y += 0.0003
			lines.rotation.y += 0.0003
			points.rotation.x = mouse.value.y * 0.1
			lines.rotation.x = mouse.value.y * 0.1

			renderer.value.render(scene.value, camera.value)
		}
function onResize() {
			if (!renderer.value || !container.value) return
			const w = container.value.clientWidth
			const h = container.value.clientHeight
			camera.value.aspect = w / h
			camera.value.updateProjectionMatrix()
			renderer.value.setSize(w, h)
		}
function onMouseMove(e) {
			mouse.value.x = (e.clientX / window.innerWidth - 0.5) * 2
			mouse.value.y = (e.clientY / window.innerHeight - 0.5) * 2
		}
onMounted(() => {
		initScene()
		window.addEventListener('resize', onResize)
		window.addEventListener('mousemove', onMouseMove)
	})
onBeforeUnmount(() => {
		window.removeEventListener('resize', onResize)
		window.removeEventListener('mousemove', onMouseMove)
		if (animationId.value) cancelAnimationFrame(animationId.value)
		if (renderer.value) {
			renderer.value.dispose()
			if (geometry) geometry.dispose()
			if (material) material.dispose()
			if (lineGeometry) lineGeometry.dispose()
			if (lineMaterial) lineMaterial.dispose()
		}
	})
</script>

<style scoped>
.particle-bg {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	z-index: 0;
	pointer-events: none;
}
</style>
