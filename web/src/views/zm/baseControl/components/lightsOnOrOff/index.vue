<script>
import {
  brightSet,
  brightStatus,
  clearLoopStatus,
  getLoopStatus,
} from '@/api/zm/baseControl'
import polling from '@/mixins/polling'

export default {
  name: 'index.vue',
  mixins: [polling],
  data() {
    return {
      status: false,
      loading: false,
      pollingConfig: {
        useGetFlag: true
      }
    }
  },
  methods: {
    getFlag() {
      getLoopStatus().then(res => {
        if (res.data) {
          clearLoopStatus().then(res => {
            this._get()
          }).catch(err => {
          })
        }
      })
    },
    handleClick(flag) {
      this.$confirm(`确认${flag ? '全亮' : '全灭'}吗？`, {
        title: '警告',
        type: 'warning'
      }).then(() => {
        return brightSet(flag)
      }).then(response => {
        if (response.code === 200) {
          this.$message.success(response.msg || '操作成功')
        }
        return this.getData()
      }).then(res => {

      }).catch(e => {
        console.log(e)
      })
    },
    getData() {
      this.loading = true
      this._get()
    },
    _get(){
      return brightStatus().then(res => {
        console.log(res)
        this.status = res.data
      }).finally(() => {
        this.loading = false
      })
    }
  },
  mounted() {
    this.getData()
    this.$startPolling()
  }
}
</script>

<template>
  <div class="scenario" v-loading="loading">
    <div
      class="scenario_card"
      :class="{scenario_card_red:status}"
      @click="handleClick(true)"
    >
      <div>
        <i class="el-icon-video-camera-solid"></i>
      </div>
      <div>全亮</div>
    </div>
    <div
      class="scenario_card"
      :class="{scenario_card_red:!status}"
      @click="handleClick(false)"
    >
      <div>
        <i class="el-icon-video-camera-solid"></i>
      </div>
      <div>全灭</div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.scenario {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr 1fr 1fr;
  gap: 100px;
  padding: 50px;

  &_card {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    background-color: #2280ec;
    border-radius: 10px;
    color: #fff;
    padding: 20px;
    cursor: pointer;

    & > div:nth-child(1) {
      margin-bottom: 10px;
      font-size: 80px;
    }

    &:hover {
      background-color: #084e9f;
    }

    &:active {
      background-color: #4d96e9;
    }

    &_red {
      background: #ff4d51 !important;
    }
  }
}

@media screen and (max-width: 1260px) {
  .scenario {
    grid-template-columns: 1fr 1fr 1fr 1fr;
  }
}

@media screen and (max-width: 1180px) {
  .scenario {
    grid-template-columns: 1fr 1fr 1fr;
  }
}

@media screen and (max-width: 840px) {
  .scenario {
    grid-template-columns: 1fr 1fr;
  }
}
</style>
