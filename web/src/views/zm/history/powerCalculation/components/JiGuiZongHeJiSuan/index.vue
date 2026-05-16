<script>
import CommonContainer from '@/components/CommonContainer/index.vue'
import PowerConsumption from '@/views/zm/history/powerCalculation/components/PowerConsumption/index.vue'

export default {
  components: { PowerConsumption, CommonContainer }

}
</script>

<template>
  <div class="jiGuiZongHeJiSuan">
      <CommonContainer
        title="年耗电量"
        :contentPadding="0"
        :borderWidth="0"
        class="CommonContainer"
      >
        <PowerConsumption :type="4"/>
      </CommonContainer>
      <CommonContainer
        title="季耗电量"
        :contentPadding="0"
        :borderWidth="0"
        class="CommonContainer"
      >
        <PowerConsumption :type="3"/>
      </CommonContainer>
      <CommonContainer
        title="月耗电量"
        :contentPadding="0"
        :borderWidth="0"
        class="CommonContainer"
      >
        <PowerConsumption :type="2"/>
      </CommonContainer>
      <CommonContainer
        title="周耗电量"
        :contentPadding="0"
        :borderWidth="0"
        class="CommonContainer"
      >
        <PowerConsumption :type="1"/>
      </CommonContainer>
      <CommonContainer
        title="日耗电量"
        :contentPadding="0"
        :borderWidth="0"
        class="CommonContainer"
      >
        <PowerConsumption :type="0"/>
      </CommonContainer>
  </div>
</template>

<style scoped lang="scss">
.jiGuiZongHeJiSuan {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 10px;

  .CommonContainer {
    width: 100%;
    overflow: hidden;
  }

  @media screen and (max-width: 1150px) {
    .jiGuiZongHeJiSuan {
      grid-template-columns: 1fr 1fr;
    }

  }

  @media screen and (max-width: 750px) {
    .jiGuiZongHeJiSuan {
      grid-template-columns: 1fr;
    }
  }
}
</style>
