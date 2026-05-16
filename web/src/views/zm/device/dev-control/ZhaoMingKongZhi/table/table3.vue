<template>
  <div>
    <div class="table" v-loading="loading">
      <div
        class="table_item"
        v-for="(item, index) in sceneControl"
        :key="item.sceneId"
        :class="{ active: item.sceneSelect }"
        @click="handleClick(item.sceneId)"
      >
        {{ item.sceneName || '未命名场景' }}
      </div>
    </div>
    <template v-if="!sceneControl.length">
      <div style="border: 1px solid #2280ec;border-top: 0">
        <el-empty description="无数据"></el-empty>
      </div>
    </template>
  </div>

</template>
<script>
import { intoScene } from '@/api/zm/device/dev-control'

export default {
  props: {
    sceneControl: {
      type: Array,
      default() {
        return []
      }
    },
    deviceId: {
      type: Number,
      default: undefined
    }
  },
  data() {
    return {
      sceneId: undefined,
      loading: false
    }
  },
  methods: {
    handleClick(sceneId) {
      this.intoScene(this.deviceId, sceneId)
    },
    intoScene(deviceId, sceneId) {
      this.loading = true
      intoScene({
        deviceId,
        sceneId
      }).then(response => {
        this.$emit('success')
      }).finally(() => {
        this.loading = false
      })
    }
  }
}
</script>
<style scoped lang="scss">
.table {
  display: flex;
  flex-wrap: wrap;
  margin-top: 1px;

  .table_item {
    background-color: #e2dbdb;
    color: #84807f;
    flex-basis: 50%;
    overflow: hidden;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 10px 0;
    border-bottom: #fff 1px solid;

    &:nth-child(2n -1) {
      border-right: #fff 1px solid;
    }

    cursor: pointer;

    &:hover {
      background-color: #4f4f4f5c;
    }
  }

  .active {
    background-color: #2280ec !important;
    color: #fff !important;
  }
}
</style>
