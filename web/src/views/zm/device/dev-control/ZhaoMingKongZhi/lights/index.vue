<script>
import CommonContainer from '@/components/CommonContainer/index.vue'

export default {
  components: { CommonContainer },
  props: {
    loopControl: {
      type: Array,
      default() {
        return []
      }
    }
  },
  data() {
    return {
      checkedData: []
    }
  },
  methods: {
    selectAll() {
      this.checkedData = this.loopControl.map(item => item.no)
    },
    clearAll() {
      this.checkedData = []
    }
  }
}
</script>

<template>
  <div>
    <CommonContainer
      :border-width="0"
      :content-padding="0"
      title="回路控制"
    >
      <el-checkbox-group v-model="checkedData">
        <div class="container">
          <div class="lights_box">
            <div class="light_group" v-for="item in loopControl" :key="item.id">
              <div class="light">
                <div class="icon">
                  <i class="el-icon-s-opportunity"></i>
                </div>
<!--                <div class="number">{{ item.lux }}</div>-->
              </div>
              <div>
                <el-checkbox :label="item.no">
                  <span style="color: #ffff;font-size: 12px">{{ item.name }}</span>
                </el-checkbox>
              </div>
            </div>

          </div>
          <template v-if="!loopControl.length">
            <div>
              <el-empty description="无数据"></el-empty>
            </div>
          </template>
        </div>
      </el-checkbox-group>

    </CommonContainer>
  </div>
</template>

<style scoped lang="scss">
.container {
  height: 470px;
  overflow: hidden;
  overflow-y: auto;
  background-color: #3673bb;
  &::-webkit-scrollbar-track {
    background-color: #3673bb;
  }
}

.lights_box {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr 1fr 1fr 1fr;


  padding-bottom: 50px;

  .light_group {
    //height: 80px;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
  }

  .light {
    position: relative;
    width: 45px;
    height: 70px;
    display: flex;
    justify-content: center;
    align-items: center;

    .icon {
      font-size: 35px;
      color: #dd9012;
    }

    .number {
      position: absolute;
      bottom: 10px;
      right: 0;
      font-size: 13px;
      color: #fff;
    }
  }
}
</style>
