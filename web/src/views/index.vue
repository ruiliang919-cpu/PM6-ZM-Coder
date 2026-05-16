<template>
  <div class="app-container home">

    <el-row :gutter="10">
      <el-col :span="12">
        <CommonContainer title="机柜总耗电量" v-loading="loading0">
          <ChartView :options="chartBar"/>
        </CommonContainer>
      </el-col>
      <el-col :span="12">
        <CommonContainer title="机柜当天电量" v-loading="loading1">
          <ChartView :options="chartPie"/>
        </CommonContainer>
      </el-col>
    </el-row>

    <el-row style="margin-top: 20px">
      <el-col :span="24">
        <CommonContainer
          title="机柜综合信息"
          :contentPadding="0"
          :borderWidth="0"
        >
          <IndexTable/>
        </CommonContainer>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import CommonContainer from '@/components/CommonContainer/index.vue'
import ChartView from '@/views/components/charts/ChartView/index.vue'
import IndexTable from '@/views/components/table/IndexTable/index.vue'
import chartBar from './components/charts/configs/chartbar'
import chartPie from './components/charts/configs/chartpie'
import { getModule, getVersion, powersByAll, powersByDay } from '../api/index/index'
import { delay } from '@/utils'

export default {
  name: 'Index',
  components: {
    CommonContainer,
    ChartView,
    IndexTable
  },
  data() {
    return {
      // 版本号
      version: '0.0.1',
      chartBar: chartBar,
      chartPie: chartPie,
      loading0: false,
      loading1: false,
      timer: null,
      mode: -1
    }
  },
  methods: {
    goTarget(href) {
      window.open(href, '_blank')
    },
    getPowersByAll() {
      this.loading0 = true
      this._getPowersByAll()
    },
    _getPowersByAll() {
      return powersByAll().then((res) => {
        console.log(res)
        //TODO 获取每一个机柜的总耗电量
        this.formatPowerByAllData(res)
      }).finally(() => {
        this.loading0 = false
      })

    },
    getPowersByDay() {
      this.loading1 = true
      this._getPowersByDay()
    },
    _getPowersByDay() {
      return powersByDay()
        .then((res) => {
          console.log(res)
          //TODO 获取每一个机柜的日耗电量
          this.formatPowerByDay(res)
        })
        .finally(() => {
          this.loading1 = false
        })
    },
    formatPowerByDay(res) {
      const dataArray = res.data || []

      const _clone = JSON.parse(JSON.stringify(this.chartPie))

      if (dataArray.length) {
        _clone.series = []
        _clone.series.push({
          // name: 'Access From',
          // grid: {
          //     top: '20%',
          //     left: '0%',
          //     right: '40%',
          //     bottom: '20%',
          // },
          right: '25%',
          bottom: '10%',
          top: '10%',
          left: '3%',
          type: 'pie',
          radius: ['10%', '95%'],
          itemStyle: {
            borderRadius: 5,
            borderColor: '#fff',
            borderWidth: 3
          },
          // roseType: 'radius',
          data: [
            // { value: 500, name: 'Search Engine' },
            // { value: 510, name: 'Direct' },
            // { value: 520, name: 'Email' },
            // { value: 500, name: 'Union Ads' },
            // { value: 510, name: 'Video Ads' }
          ],
          tooltip: {
            // formatter(e){
            //   return `${e.data.name}  耗电:${e.data.value}kWh`
            // },
          },
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        })

        dataArray.forEach((item, index) => {
          _clone.series[0].data.push({ value: item.power, name: item.name })
        })
      }

      this.chartPie = _clone
    },
    formatPowerByAllData(res) {
      const dataArray = res.data || []
      const len = dataArray.length

      const names = []
      const series = []

      const _clone = JSON.parse(JSON.stringify(this.chartBar))
      _clone.series = []
      _clone.legend.data = []
      _clone.xAxis.data = []
      dataArray.forEach((item, index) => {
        names.push(item.name || '')
        const template = []
        for (let i = 0; i < len; i++) {
          template.push({
            name: 'fake',
            value: 0
          })
        }
        template[index] = {
          name: item.name,
          value: item.power
        }
        series.push({
          name: item.name,
          stack: '设备',
          data: template,
          type: 'bar',
          barMaxWidth: 30
        })
      })
      console.log(names)
      _clone.legend.data = names
      _clone.xAxis.data = names
      _clone.series = series
      this.chartBar = _clone
    }
  },
  mounted() {
    this.getPowersByAll()
    this.getPowersByDay()

    this.timer = setInterval(async() => {
      await this._getPowersByAll()
      await delay(2000)
      await this._getPowersByDay()
    }, 10000)
  },
  beforeDestroy() {
    clearInterval(this.timer)
  }
}
</script>

<style scoped lang="scss">
.home {
  blockquote {
    padding: 10px 20px;
    margin: 0 0 20px;
    font-size: 17.5px;
    border-left: 5px solid #eee;
  }

  hr {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 0;
    border-top: 1px solid #eee;
  }

  .col-item {
    margin-bottom: 20px;
  }

  ul {
    padding: 0;
    margin: 0;
  }

  font-family: "open sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
  font-size: 13px;
  color: #676a6c;
  overflow-x: hidden;

  ul {
    list-style-type: none;
  }

  h4 {
    margin-top: 0px;
  }

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
  }

  p {
    margin-top: 10px;

    b {
      font-weight: 700;
    }
  }

  .update-log {
    ol {
      display: block;
      list-style-type: decimal;
      margin-block-start: 1em;
      margin-block-end: 1em;
      margin-inline-start: 0;
      margin-inline-end: 0;
      padding-inline-start: 40px;
    }
  }
}
</style>
