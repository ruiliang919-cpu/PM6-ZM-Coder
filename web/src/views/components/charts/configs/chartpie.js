export default {

  legend: {
    orient: 'vertical',
    align: 'left',
    top: 'center',
    right: '10',
    backgroundColor: '#dadedf',
    borderRadius: 2,
    padding: 20,
    borderWidth: 1,
    borderColor: '#798287',
    formatter(e){
      let s;
      try {
        s = e.split(" ")[1]
      }catch(e){
        s = e
      }
      return s
    }
  },
  tooltip: {
    trigger: 'item'
  },
  series: []
  // formatter(data) {
  //     return {}
  // }
}
