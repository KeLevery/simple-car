<template>
  <div ref="chartEl" style="height:100%"></div>
</template>
<script>
import * as echarts from 'echarts'
const ChartBlock = {
  name: 'ChartBlock',
  props: {
    option: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    return {
      chart: null
    }
  },
  watch: {
    option: {
      handler(newValue, oldValue) {
        this.chart.setOption(newValue)
      },
      deep: true
    }
  },
  mounted() {
    this.chart = echarts.init(this.$refs.chartEl)
    this.chart.setOption(this.option)
    // setInterval(this.changeOption, 2000)
  },
  methods: {
   
  }
}

/* 注册组件的方法 */
ChartBlock.install = (Vue) => {
  Vue.component(ChartBlock.name, ChartBlock)
}
export default ChartBlock
</script>