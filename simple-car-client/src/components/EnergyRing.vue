<template>
	<div class="energy-ring-container" ref="container"></div>
</template>

<script setup>
import * as THREE from 'three'
import { onBeforeUnmount, onMounted, ref, toRefs, watch } from 'vue'

defineOptions({ name: 'EnergyRing' })
const props = defineProps({
		percentage: { type: Number, default: 80 },
		color: { type: Number, default: 0x00d4ff }
	})
const { percentage, color } = toRefs(props)
const container = ref(null)
const animationId = ref(null)
const renderer = ref(null)
let scene = null
let camera = null
let baseRing = null
let glowPoints = null
let outerRing = null
let clock = null
let progressRing = null
function initScene() {
			const containerEl = container.value
			if (!containerEl) return

			const size = Math.min(containerEl.clientWidth, containerEl.clientHeight) || 200
			
			renderer.value = new THREE.WebGLRenderer({ antialias: true, alpha: true })
			renderer.value.setSize(size, size)
			renderer.value.setPixelRatio(Math.min(window.devicePixelRatio, 2))
			containerEl.appendChild(renderer.value.domElement)

			scene = new THREE.Scene()
			camera = new THREE.PerspectiveCamera(50, 1, 0.1, 100)
			camera.position.z = 4

			// Base ring (full circle, dim)
			const baseGeometry = new THREE.TorusGeometry(1.2, 0.03, 16, 100)
			const baseMaterial = new THREE.MeshBasicMaterial({
				color: 0x1a2332,
				transparent: true,
				opacity: 0.6
			})
			baseRing = new THREE.Mesh(baseGeometry, baseMaterial)
			scene.add(baseRing)

			// Progress ring
			updateRing()

			// Inner glow particles
			const glowCount = 30
			const glowPos = new Float32Array(glowCount * 3)
			for (let i = 0; i < glowCount; i++) {
				const angle = (i / glowCount) * Math.PI * 2
				const r = 1.2 + (Math.random() - 0.5) * 0.15
				glowPos[i * 3] = Math.cos(angle) * r
				glowPos[i * 3 + 1] = Math.sin(angle) * r
				glowPos[i * 3 + 2] = (Math.random() - 0.5) * 0.1
			}
			const glowGeom = new THREE.BufferGeometry()
			glowGeom.setAttribute('position', new THREE.BufferAttribute(glowPos, 3))
			const glowMat = new THREE.PointsMaterial({
				color: color.value,
				size: 0.04,
				transparent: true,
				opacity: 0.4,
				blending: THREE.AdditiveBlending,
				depthWrite: false
			})
			glowPoints = new THREE.Points(glowGeom, glowMat)
			scene.add(glowPoints)

			// Outer subtle ring
			const outerGeom = new THREE.TorusGeometry(1.45, 0.008, 8, 100)
			const outerMat = new THREE.MeshBasicMaterial({
				color: color.value,
				transparent: true,
				opacity: 0.15
			})
			outerRing = new THREE.Mesh(outerGeom, outerMat)
			scene.add(outerRing)

			clock = new THREE.Clock()
			animate()
		}
function updateRing() {
			if (progressRing) {
				scene.remove(progressRing)
				progressRing.geometry.dispose()
				progressRing.material.dispose()
			}

			const pct = Math.max(0, Math.min(100, percentage.value))
			const arcAngle = (pct / 100) * Math.PI * 2

			const progressGeometry = new THREE.TorusGeometry(1.2, 0.05, 16, 100, arcAngle)
			const progressMaterial = new THREE.MeshBasicMaterial({
				color: color.value,
				transparent: true,
				opacity: 0.9
			})
			progressRing = new THREE.Mesh(progressGeometry, progressMaterial)
			progressRing.rotation.z = Math.PI / 2
			scene.add(progressRing)
		}
function animate() {
			animationId.value = requestAnimationFrame(animate)
			const t = clock.getElapsedTime()

			// Subtle rotation
			if (baseRing) baseRing.rotation.z = t * 0.05
			if (outerRing) outerRing.rotation.z = -t * 0.08
			if (glowPoints) {
				glowPoints.rotation.z = t * 0.1
				glowPoints.material.opacity = 0.3 + Math.sin(t * 2) * 0.15
			}

			renderer.value.render(scene, camera)
		}
watch(percentage, () => {
			updateRing()
		})
onMounted(() => {
		initScene()
	})
onBeforeUnmount(() => {
		if (animationId.value) cancelAnimationFrame(animationId.value)
		if (renderer.value) {
			renderer.value.dispose()
		}
	})
</script>

<style scoped>
.energy-ring-container {
	width: 100%;
	height: 100%;
	display: flex;
	align-items: center;
	justify-content: center;
}
</style>
