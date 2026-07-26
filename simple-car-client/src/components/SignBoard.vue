<template>
	<div :style="{height: height, width: width,margin: '15px auto'}">
		<canvas ref="canvas" :height="height" :width="width" style="border: 1px solid"
				@touchstart="handleTouchStart"
				@touchmove="handleTouchMove"></canvas>
		<span @click="handleClear" style="font-size: 20px;padding-right: 2em">重签</span>
		<span @click="handleConfirm" style="font-size: 20px;">确定</span>
	</div>
</template>

<script setup lang="ts">
import { onMounted, ref, toRefs } from 'vue'
import { useVantCompat } from '@/composables/useVantCompat'

defineOptions({ name: 'signBoard' })
const props = withDefaults(defineProps<{
		height?: string
		width?: string
	}>(), {
		height: '200px',
		width: '300px'
	})
const { height, width } = toRefs(props)
const emit = defineEmits<{
	(e: 'confirm', payload: { canvas: HTMLCanvasElement }): void
}>()
const { toast, notify, dialog } = useVantCompat()
const canvas = ref<HTMLCanvasElement | null>(null)
const canvasRect = ref<DOMRect | null>(null)
const ctx = ref<CanvasRenderingContext2D | null>(null)
const startX = ref(0)
const startY = ref(0)
const endX = ref(0)
const endY = ref(0)
const isEmpty = ref(true)
function init() {
			const canvasEl = canvas.value;
			if (!canvasEl) return;
			canvasRect.value = canvasEl.getBoundingClientRect();
			ctx.value = canvasEl.getContext('2d')
		}
function handleTouchStart(e: TouchEvent) {
			e.preventDefault();
			if (!canvas.value) return;
			canvasRect.value = canvas.value.getBoundingClientRect();
			startX.value = e.targetTouches[0].clientX - canvasRect.value.left;
			startY.value = e.targetTouches[0].clientY - canvasRect.value.top;
		}
function handleTouchMove(e: TouchEvent) {
			e.preventDefault();
			if (!canvasRect.value) return;
			endX.value = e.targetTouches[0].clientX - canvasRect.value.left;
			endY.value = e.targetTouches[0].clientY - canvasRect.value.top;
			draw()
			startX.value = endX.value;
			startY.value = endY.value;
		}
function draw() {
			if (!ctx.value) return;
			ctx.value.beginPath();
			ctx.value.moveTo(startX.value, startY.value);
			ctx.value.lineTo(endX.value, endY.value);
			ctx.value.lineCap = 'round';
			ctx.value.lineJoin = 'round';
			ctx.value.stroke();
			ctx.value.closePath();

			isEmpty.value = false;
		}
function handleClear() {
			if (!ctx.value || !canvasRect.value) return;
			ctx.value.clearRect(0, 0, canvasRect.value.width, canvasRect.value.height);
			isEmpty.value = true;
		}
function handleConfirm() {
			if(isEmpty.value) {
				toast.fail('请签名确认！');
				return
			}
			if (!canvas.value) return;
			emit('confirm', {canvas: canvas.value})
		}
onMounted(() => {
		init()
	})
</script>

