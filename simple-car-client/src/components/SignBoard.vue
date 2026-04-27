<template>
	<div :style="{height: height, width: width,margin: '15px auto'}">
		<canvas ref="canvas" :height="height" :width="width" style="border: 1px solid"
				@touchstart="handleTouchStart"
				@touchmove="handleTouchMove"></canvas>
		<span @click="handleClear" style="font-size: 20px;padding-right: 2em">重签</span>
		<span @click="handleConfirm" style="font-size: 20px;">确定</span>
	</div>
</template>

<script>
export default {
	name: 'signBoard',
	props: {
		height: {
			type: String,
			default: '200px'
		},
		width: {
			type: String,
			default: '300px'
		}
	},
	data() {
		return {
			canvasRect: null, // 宽高clientRect数据
			ctx: null,  // 画笔对象
			startX: 0,
			startY: 0,
			endX: 0,
			endY: 0,
			isEmpty: true, // 画板是否为空
		}
	},
	mounted() {
		this.init()
	},
	methods: {
		init () {
			const canvas = this.$refs.canvas;
			this.canvasRect = canvas.getBoundingClientRect();
			this.ctx = canvas.getContext('2d')
		},
		handleTouchStart(e) {
			e.preventDefault();
			this.canvasRect = this.$refs.canvas.getBoundingClientRect();
			this.startX = e.targetTouches[0].clientX - this.canvasRect.left;
			this.startY = e.targetTouches[0].clientY - this.canvasRect.top;
		},
		handleTouchMove(e) {
			e.preventDefault();
			this.endX = e.targetTouches[0].clientX - this.canvasRect.left;
			this.endY = e.targetTouches[0].clientY - this.canvasRect.top;
			this.draw()
			this.startX = this.endX;
			this.startY = this.endY;
		},
		draw () {
			this.ctx.beginPath();
			this.ctx.moveTo(this.startX, this.startY);
			this.ctx.lineTo(this.endX, this.endY);
			this.ctx.lineCap = 'round';
			this.ctx.lineJoin = 'round';
			this.ctx.stroke();
			this.ctx.closePath();

			this.isEmpty = false;
		},
		handleClear() {
			this.ctx.clearRect(0, 0, this.canvasRect.width, this.canvasRect.height);
			this.isEmpty = true;
		},
		handleConfirm() {
			if(this.isEmpty) {
				this.$toast.fail('请签名确认！');
				return
			}
			this.$emit('confirm', {canvas: this.$refs.canvas})
		}
	}
}
</script>

