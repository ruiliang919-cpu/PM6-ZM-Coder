<template>
  <div>
    <div class="QiTaMoShi">
      <div>
        <CommonContainer
          title="机柜信息"
          :contentPadding="0"
          :borderWidth="0"
          style="flex: 1; overflow: hidden"
        >
          <Table1/>
        </CommonContainer>
      </div>
      <div>
        <CommonContainer
          title="通讯参数"
          :contentPadding="0"
          :borderWidth="0"
          style="flex: 1; overflow: hidden"
        >
          <Table2/>
        </CommonContainer>
      </div>
    </div>
    <div class="QiTaMoShi">
      <div style="margin-top: 30px">
        <CommonContainer
          title="对时"
          :contentPadding="0"
          :borderWidth="0"
          style="flex: 1; overflow: hidden"
        >
          <Table3/>
        </CommonContainer>
      </div>
    </div>
  </div>
</template>
<script>
import CommonContainer from '@/components/CommonContainer/index.vue'
import Table1 from './table/table1.vue'
import Table2 from './table/table2.vue'
import Table3 from './table/table3.vue'

export default {
  components: {
    CommonContainer,
    Table1,
    Table2,
    Table3
  }
}
</script>
<style lang="scss" scoped>
.QiTaMoShi {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;

  & > div {
    width: 100%;
    overflow: hidden;
  }
}

@media screen and (max-width: 780px) {
  .QiTaMoShi {
    grid-template-columns:1fr;
  }
}
</style>
