<template>
	<div class="energy-ring-container" ref="container"></div>
</template>

<script>
import * as THREE from 'three'

export default {
	name: 'EnergyRing',
	props: {
		percentage: { type: Number, default: 80 },
		color: { type: Number, default: 0x00d4ff }
	},
	data() {
		return {
			animationId: null,
			renderer: null
		}
	},
	mounted() {
		this.initScene()
	},
	beforeDestroy() {
		if (this.animationId) cancelAnimationFrame(this.animationId)
		if (this.renderer) {
			this.renderer.dispose()
		}
	},
	watch: {
		percentage() {
			this.updateRing()
		}
	},
	methods: {
		initScene() {
			const container = this.$refs.container
			if (!container) return

			const size = Math.min(container.clientWidth, container.clientHeight) || 200
			
			this.renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true })
			this.renderer.setSize(size, size)
			this.renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
			container.appendChild(this.renderer.domElement)

			this.scene = new THREE.Scene()
			this.camera = new THREE.PerspectiveCamera(50, 1, 0.1, 100)
			this.camera.position.z = 4

			// Base ring (full circle, dim)
			const baseGeometry = new THREE.TorusGeometry(1.2, 0.03, 16, 100)
			const baseMaterial = new THREE.MeshBasicMaterial({
				color: 0x1a2332,
				transparent: true,
				opacity: 0.6
			})
			this.baseRing = new THREE.Mesh(baseGeometry, baseMaterial)
			this.scene.add(this.baseRing)

			// Progress ring
			this.updateRing()

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
				color: this.color,
				size: 0.04,
				transparent: true,
				opacity: 0.4,
				blending: THREE.AdditiveBlending,
				depthWrite: false
			})
			this.glowPoints = new THREE.Points(glowGeom, glowMat)
			this.scene.add(this.glowPoints)

			// Outer subtle ring
			const outerGeom = new THREE.TorusGeometry(1.45, 0.008, 8, 100)
			const outerMat = new THREE.MeshBasicMaterial({
				color: this.color,
				transparent: true,
				opacity: 0.15
			})
			this.outerRing = new THREE.Mesh(outerGeom, outerMat)
			this.scene.add(this.outerRing)

			this.clock = new THREE.Clock()
			this.animate()
		},
		updateRing() {
			if (this.progressRing) {
				this.scene.remove(this.progressRing)
				this.progressRing.geometry.dispose()
				this.progressRing.material.dispose()
			}

			const pct = Math.max(0, Math.min(100, this.percentage))
			const arcAngle = (pct / 100) * Math.PI * 2

			const progressGeometry = new THREE.TorusGeometry(1.2, 0.05, 16, 100, arcAngle)
			const progressMaterial = new THREE.MeshBasicMaterial({
				color: this.color,
				transparent: true,
				opacity: 0.9
			})
			this.progressRing = new THREE.Mesh(progressGeometry, progressMaterial)
			this.progressRing.rotation.z = Math.PI / 2
			this.scene.add(this.progressRing)
		},
		animate() {
			this.animationId = requestAnimationFrame(this.animate)
			const t = this.clock.getElapsedTime()

			// Subtle rotation
			if (this.baseRing) this.baseRing.rotation.z = t * 0.05
			if (this.outerRing) this.outerRing.rotation.z = -t * 0.08
			if (this.glowPoints) {
				this.glowPoints.rotation.z = t * 0.1
				this.glowPoints.material.opacity = 0.3 + Math.sin(t * 2) * 0.15
			}

			this.renderer.render(this.scene, this.camera)
		}
	}
}
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
