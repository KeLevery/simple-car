<template>
  <div ref="chartEl" style="height:100%"></div>
</template>
<script setup>
import { init as initEcharts } from '@/util/echarts'
import { onMounted, ref, toRefs, watch } from 'vue'

defineOptions({ name: 'ChartBlock' })
const props = defineProps({
    option: {
      type: Object,
      default: () => {}
    }
  })
const { option } = toRefs(props)
const chartEl = ref(null)
const chart = ref(null)
watch(option, (newValue, oldValue) => {
        if (!chart.value) return
        chart.value.setOption(newValue)
      }, { deep: true })
onMounted(() => {
    chart.value = initEcharts(chartEl.value)
    chart.value.setOption(option.value)
  })
</script>
