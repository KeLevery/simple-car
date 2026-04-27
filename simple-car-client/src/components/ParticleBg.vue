<template>
	<div class="particle-bg" ref="container"></div>
</template>

<script>
import * as THREE from 'three'

export default {
	name: 'ParticleBg',
	props: {
		particleCount: { type: Number, default: 60 },
		connectionDistance: { type: Number, default: 2.5 },
		particleColor: { type: Number, default: 0x00d4ff },
		speed: { type: Number, default: 0.002 },
		opacity: { type: Number, default: 0.5 },
		depth: { type: Number, default: 8 }
	},
	data() {
		return {
			animationId: null,
			renderer: null,
			scene: null,
			camera: null,
			mouse: { x: 0, y: 0 }
		}
	},
	mounted() {
		this.initScene()
		window.addEventListener('resize', this.onResize)
		window.addEventListener('mousemove', this.onMouseMove)
	},
	beforeDestroy() {
		window.removeEventListener('resize', this.onResize)
		window.removeEventListener('mousemove', this.onMouseMove)
		if (this.animationId) cancelAnimationFrame(this.animationId)
		if (this.renderer) {
			this.renderer.dispose()
			if (this.geometry) this.geometry.dispose()
			if (this.material) this.material.dispose()
			if (this.lineGeometry) this.lineGeometry.dispose()
			if (this.lineMaterial) this.lineMaterial.dispose()
		}
	},
	methods: {
		initScene() {
			const container = this.$refs.container
			if (!container) return

			const w = container.clientWidth
			const h = container.clientHeight

			// Renderer
			this.renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true })
			this.renderer.setSize(w, h)
			this.renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
			container.appendChild(this.renderer.domElement)

			// Scene & Camera
			this.scene = new THREE.Scene()
			this.camera = new THREE.PerspectiveCamera(60, w / h, 0.1, 1000)
			this.camera.position.z = 5

			// Particles
			const count = this.particleCount
			const d = this.depth
			const positions = new Float32Array(count * 3)
			this.particlesData = []

			for (let i = 0; i < count; i++) {
				positions[i * 3] = (Math.random() - 0.5) * d
				positions[i * 3 + 1] = (Math.random() - 0.5) * d
				positions[i * 3 + 2] = (Math.random() - 0.5) * d
				this.particlesData.push({
					velocity: new THREE.Vector3(
						(Math.random() - 0.5) * this.speed,
						(Math.random() - 0.5) * this.speed,
						(Math.random() - 0.5) * this.speed
					)
				})
			}

			this.geometry = new THREE.BufferGeometry()
			this.geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3))

			this.material = new THREE.PointsMaterial({
				color: this.particleColor,
				size: 0.04,
				transparent: true,
				opacity: this.opacity,
				sizeAttenuation: true,
				blending: THREE.AdditiveBlending,
				depthWrite: false
			})

			this.points = new THREE.Points(this.geometry, this.material)
			this.scene.add(this.points)

			// Connection Lines
			this.lineGeometry = new THREE.BufferGeometry()
			this.lineMaterial = new THREE.LineBasicMaterial({
				color: this.particleColor,
				transparent: true,
				opacity: 0.12,
				blending: THREE.AdditiveBlending,
				depthWrite: false
			})
			this.lines = new THREE.LineSegments(this.lineGeometry, this.lineMaterial)
			this.scene.add(this.lines)

			this.animate()
		},
		animate() {
			this.animationId = requestAnimationFrame(this.animate)

			const count = this.particleCount
			const half = this.depth / 2
			const posArr = this.geometry.attributes.position.array
			const linePositions = []
			const connDist = this.connectionDistance

			for (let i = 0; i < count; i++) {
				const pData = this.particlesData[i]
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

			this.geometry.attributes.position.needsUpdate = true
			this.lineGeometry.setAttribute('position', new THREE.Float32BufferAttribute(linePositions, 3))

			// Subtle rotation following mouse
			this.points.rotation.y += 0.0003
			this.lines.rotation.y += 0.0003
			this.points.rotation.x = this.mouse.y * 0.1
			this.lines.rotation.x = this.mouse.y * 0.1

			this.renderer.render(this.scene, this.camera)
		},
		onResize() {
			if (!this.renderer || !this.$refs.container) return
			const w = this.$refs.container.clientWidth
			const h = this.$refs.container.clientHeight
			this.camera.aspect = w / h
			this.camera.updateProjectionMatrix()
			this.renderer.setSize(w, h)
		},
		onMouseMove(e) {
			this.mouse.x = (e.clientX / window.innerWidth - 0.5) * 2
			this.mouse.y = (e.clientY / window.innerHeight - 0.5) * 2
		}
	}
}
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
