<template>
  <div v-loading="loading">
    <template v-if="list.length">
      <div class="scenario">

        <div v-for="item in list"
             class="scenario_card"
             :class="{scenario_card_red:item.enabled}"
             @click="handleClick(item)"
        >
          <div>
            <i class="el-icon-video-camera-solid"></i>
          </div>
          <div>{{ item.name }}</div>
        </div>
      </div>
    </template>
    <template v-else>
      <el-empty description="无数据"></el-empty>
    </template>
  </div>


</template>
<script>
import { listBaseScene } from '@/api/zm/baseScene'
import { clearSceneStatus, clearZoneStatus, getSceneStatus, getZoneStatus, intoScenes } from '@/api/zm/baseControl'

export default {
  data() {
    return {
      list: [],
      loading: false,
      timer: null,
      flag: false
    }
  },
  methods: {
    startTimer() {
      clearInterval(this.timer)
      this.timer = setInterval(() => {
        this.getFlag()
      }, 5000)
    },
    getFlag() {
      getSceneStatus().then(res => {
        if (res.data) {
          clearSceneStatus().then(res => {
            this._get()
          }).catch(err => {
          })
        }
      })
    },
    /** 查询场景列表 */
    getList() {
      this.loading = true
      this._get()
    },
    _get() {
      listBaseScene().then(response => {
        this.list = response.rows || response.data
        this.total = response.total || 0

      }).finally(() => {
        this.loading = false
      })
    },
    handleClick(item) {
      this.$confirm('确定设置为所有机柜设置该场景吗？', {
        type: 'warning',
        title: item.name
      }).then(async() => {
        return await intoScenes({
          sceneId: item.id
        })
      }).then(res => {
        this.$message.success(res.msg || '操作成功！')
        this.getList()
      })
    }
  },
  mounted() {
    this.getList()
    this.startTimer()
  },
  beforeDestroy() {
   clearInterval(this.timer)
  }
}
</script>
<style lang="scss" scoped>
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
