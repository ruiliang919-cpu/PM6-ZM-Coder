<template>
  <div class="chart-view">
    <div ref="chart" class="chart"></div>
    <div class="empty" v-show="options && options.series && !options.series.length">
      <el-empty description="无数据" :image-size="60" style="height: 300px"/>
    </div>
  </div>
</template>
<script>
import * as echarts from 'echarts'

export default {
  props: {
    options: {
      type: Object,
      default() {
        return {
          series: []
        }
      }
    }
  },
  data() {
    return {
      chart: null
    }
  },
  methods: {
    handleResize() {
      console.log('resize')

      if (this.chart) {
        this.chart.resize()
      }
    }
  },
  watch: {
    options: {
      handler(n, o) {
        console.log(n)
        if (!this.chart) return
        this.chart.setOption(n)
      },
      deep: true,
      immediate: true
    }
  },
  mounted() {
    this.chart = echarts.init(this.$refs.chart, 'chart-bar')
    this.chart.setOption(this.options)
    window.addEventListener('resize', this.handleResize, false)
  },
  beforeDestory() {
    console.log('destroy')

    window.removeEventListener('resize', this.handleResize, false)
  }
}
</script>
<style scoped lang="scss">
.chart-view {
  position: relative;
  overflow: hidden;
  .empty{
    position: absolute;
    inset: 0;
    z-index: 1;
    background-color: #fff;
    transform: scale(1.2);
  }
}

.chart {
  position: relative;
  width: 100%;
  height: 300px;
  z-index: 0;
}
</style>
