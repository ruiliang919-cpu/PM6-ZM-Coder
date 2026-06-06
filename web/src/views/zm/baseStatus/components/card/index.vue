<template>
  <div class="card">
    <div class="title_box">
      <div class="deviceNo">机柜名称：{{ data.deviceName }}</div>
      <div class="region">所属区域：{{ data.regionName }}</div>
    </div>
    <div class="card_body_box">
      <div class="total_box">
        <div class="left">直流回路</div>
        <div class="right">
          <span class="a">回路总数:{{ data.dcModuleNum }}</span>
          <span class="b">开灯回路数:{{ data.dcTurnOnNum }}</span>
          <span class="c">关灯回路数:{{ data.dcTurnOffNum }}</span>
        </div>
      </div>
      <div class="lights_box">
        <div class="light_group" v-for="item in data.dcList" :key="item.id">
          <div class="light">
            <div class="icon" :class="{'icon_turn_off':!item.switchFeedback}">
              <i class="el-icon-s-opportunity"></i>
            </div>
            <div class="number" v-if="item.type === 1">{{ item.brightnessFeedback }}</div>
          </div>
        </div>

      </div>

      <div class="empty_box" v-if="!data.dcList || !data.dcList.length">
        <el-empty description="无数据" style="padding: 0" ></el-empty>
      </div>
    </div>
    <div class="card_body_box">
      <div class="total_box">
        <div class="left">交流回路</div>
        <div class="right">
          <span class="a">回路总数:{{ data.acModuleNum }}</span>
          <span class="b">开灯回路数:{{ data.acTurnOnNum }}</span>
          <span class="c">关灯回路数:{{ data.acTurnOffNum }}</span>
        </div>
      </div>
      <div class="lights_box">
        <div class="light_group" v-for="item in data.acList" :key="item.id">
          <div class="light">
            <div class="icon" :class="{'icon_turn_off':!item.switchFeedback}">
              <i class="el-icon-s-opportunity"></i>
            </div>
<!--            <div class="number">{{ item.brightnessSetting }}</div>-->
          </div>
        </div>
      </div>
      <div class="empty_box" v-if="!data.acList || !data.acList.length">
        <el-empty description="无数据" style="padding: 0" ></el-empty>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    data: {
      type: Object,
      default() {
        return {
          // 机柜名称
          deviceName: 'deviceName',
          // 机柜设备ID
          deviceNo: 0,
          // 机柜区域名称
          regionName: 'regionName',
          // 直流模块数量
          dcModuleNum: 0,
          // 直流开灯回路数
          dcTurnOnNum: 0,
          // 直流关灯回路数
          dcTurnOffNum: 0,
          // 直流回路列表
          dcList: [
            // {
            //   'id': 11,
            //   // 直流回路的亮度设定值
            //   brightnessSetting: 0,
            //   // 直流回路的亮度反馈值
            //   brightnessFeedback: 0,
            //   // 直流回路的开关设定值 0关 1开
            //   switchSetting: 0,
            //   // 直流回路的开关反馈值 0关 1开
            //   switchFeedback: 0
            // }
          ],
          // 机柜的交流模块总数量
          acModuleNum: 0,
          // 机柜的交流回路开灯数
          acTurnOnNum: 0,
          // 机柜的交流回路关灯数
          acTurnOffNum: 0,
          // 机柜的交流回路列表
          acList: [
            // {
            //   // 交流回路的开关设定值 0关 1开
            //   switchSetting: 0,
            //   // 交流回路的开关反馈值 0关 1开
            //   switchFeedback: 0
            // }
          ]
        }
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.card {
  width: 100%;
  height: 100%;
  border-radius: 10px;
  overflow: hidden;
  background-color: #3673bc;
  color: #fff;
  display: flex;
  flex-direction: column;
  .title_box {
    background-color: #2280ec;
    font-size: 18px;
    padding: 10px;

    .deviceNo {
      margin-bottom: 10px;
    }
  }

  .card_body_box {
    padding: 10px;
    width: 100%;
    flex: 1;
    display: flex;
    flex-direction: column;
    .total_box {
      display: flex;
      padding: 20px 0;

      .left {
        font-size: 18px;
      }

      .right {
        font-size: 13px;
        padding-left: 20px;
        flex: 1;
        display: flex;
        justify-content: space-between;
        align-items: flex-end;
        overflow: hidden;

        span {
          padding: 0px 2px;
        }
      }
    }
    .empty_box{
      flex: 1;
      display: flex;
      justify-content: center;
      align-items: center;
    }
    .lights_box {
      display: grid;
      grid-template-columns: 1fr 1fr 1fr 1fr 1fr 1fr;

      .light_group {
        overflow: hidden;
        display: flex;
        justify-content: center;
        align-items: center;
      }

      .light {
        position: relative;
        //width: 20px;
        //height: 50px;
        display: flex;
        justify-content: center;
        align-items: center;

        .icon {
          padding: 5px;
          font-size: 25px;
          color: #dd9012;
        }

        .icon_turn_off {
          color: #bfbfbf;
        }

        .number {
          position: absolute;
          bottom: 5px;
          right: 0;
          font-size: 13px;
        }
      }
    }
  }
}
</style>
