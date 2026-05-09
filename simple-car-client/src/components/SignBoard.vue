<template>
	<div :style="{height: height, width: width,margin: '15px auto'}">
		<canvas ref="canvas" :height="height" :width="width" style="border: 1px solid"
				@touchstart="handleTouchStart"
				@touchmove="handleTouchMove"></canvas>
		<span @click="handleClear" style="font-size: 20px;padding-right: 2em">重签</span>
		<span @click="handleConfirm" style="font-size: 20px;">确定</span>
	</div>
</template>

<script setup>
import { onMounted, ref, toRefs } from 'vue'
import { useVantCompat } from '@/composables/useVantCompat'

defineOptions({ name: 'signBoard' })
const props = defineProps({
		height: {
			type: String,
			default: '200px'
		},
		width: {
			type: String,
			default: '300px'
		}
	})
const { height, width } = toRefs(props)
const emit = defineEmits(["confirm"])
const { toast, notify, dialog } = useVantCompat()
const canvas = ref(null)
const canvasRect = ref(null)
const ctx = ref(null)
const startX = ref(0)
const startY = ref(0)
const endX = ref(0)
const endY = ref(0)
const isEmpty = ref(true)
function init() {
			const canvasEl = canvas.value;
			canvasRect.value = canvasEl.getBoundingClientRect();
			ctx.value = canvasEl.getContext('2d')
		}
function handleTouchStart(e) {
			e.preventDefault();
			canvasRect.value = canvas.value.getBoundingClientRect();
			startX.value = e.targetTouches[0].clientX - canvasRect.value.left;
			startY.value = e.targetTouches[0].clientY - canvasRect.value.top;
		}
function handleTouchMove(e) {
			e.preventDefault();
			endX.value = e.targetTouches[0].clientX - canvasRect.value.left;
			endY.value = e.targetTouches[0].clientY - canvasRect.value.top;
			draw()
			startX.value = endX.value;
			startY.value = endY.value;
		}
function draw() {
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
			ctx.value.clearRect(0, 0, canvasRect.value.width, canvasRect.value.height);
			isEmpty.value = true;
		}
function handleConfirm() {
			if(isEmpty.value) {
				toast.fail('请签名确认！');
				return
			}
			emit('confirm', {canvas: canvas.value})
		}
onMounted(() => {
		init()
	})
</script>

