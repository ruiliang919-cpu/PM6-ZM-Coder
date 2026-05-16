export default {
  grid: {
    top: '3%',
    left: '0%',
    right: '25%',
    bottom: '3%',
    containLabel: true
  },
  legend: {
    data: [],
    orient: 'vertical',
    align: 'left',
    top: 'center',
    right: '10',
    backgroundColor: '#dadedf',
    borderRadius: 2,
    padding: 20,
    borderWidth: 1,
    borderColor: '#798287',
    formatter(e) {
      let s
      try {
        s = e.split(' ')[1]
      } catch (e) {
        s = e
      }
      return s
    }
  },
  tooltip: {
    trigger: 'axis',
    formatter(e) {
      //console.log(e)
      const i = e.find(item => item.data.name !== 'fake')
      if(i){
        return `${i.data.name} : ${i.data.value}kWh`
      }
      return ""
    }
  },
  xAxis: {
    type: 'category',
    data: [],
    axisLabel:{
      rotate: -45,
      formatter(e) {
        let s
        try {
          s = e.split(' ')[1]
        } catch (e) {
          s = e
        }
        return s
      }
    }

  },
  yAxis: {
    type: 'value'
  },
  series: [
    // {
    //     name: "占位符",
    //     stack: "设备",
    //     data: [168],
    //     type: 'bar'
    // },

  ]
  // formatter(data) {
  //     return {}
  // }
}
