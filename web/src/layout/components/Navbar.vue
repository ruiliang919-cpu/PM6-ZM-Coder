<template>
  <div class="navbar">
    <hamburger
      id="hamburger-container"
      :is-active="sidebar.opened"
      class="hamburger-container"
      @toggleClick="toggleSideBar"
    />

    <breadcrumb
      id="breadcrumb-container"
      class="breadcrumb-container"
      v-if="!topNav"
    />
    <top-nav id="topmenu-container" class="topmenu-container" v-if="topNav"/>
    <div class="alert-message">
      <div class="alert" v-show="news" @click="$router.push({path:'/alarm/list'})">
        <!-- //TODO 告警信息 -->
        <span><i class="el-icon-message-solid"></i></span>
        <span>告警信息:{{ news }}</span>
      </div>
    </div>


    <div class="operations" v-if="show">
      <!-- //TODO 告警信息 -->
      <el-select
        style="width: 220px"
        class="select"
        size="small"
        :value="navibarDeviceValue"
        filterable
        @change="handleChange"
      >
        <el-option :value="item.deviceId" :label="item.deviceName" v-for="item in cabinetList" :key="item.deviceId"/>

      </el-select>
    </div>
    <div class="operations">
      <div>
        <template v-if="show">
          <!-- //TODO 告警信息 -->
          <span style="padding: 0 5px"></span>
        </template>
        <el-button type="text" @click="$router.push({path:'/base/baseInstructs'})">
          <span style="color: #a4b9cb" class="btn_text">报文</span>
        </el-button>
        <span style="padding: 0 5px">|</span>
        <el-button type="text" @click="testGetData">
          <span style="color: #a4b9cb" class="btn_text">手动采集设备电量</span>
        </el-button>
        <span style="padding: 0 5px">|</span>


        <!--        <el-button type="text">-->
        <!--          <span style="color: #a4b9cb" class="btn_text">通讯</span>-->
        <!--        </el-button>-->
        <!--        <span style="padding: 0 5px">|</span>-->
        <el-button type="text" @click="setModule(0)" :loading="loadingModule">
          <span style="color: #a4b9cb" class="btn_text">就地</span>
        </el-button>
        <span style="padding: 0 5px">|</span>
        <el-button type="text" @click="setModule(1)" :loading="loadingModule">
          <span style="color: #a4b9cb" class="btn_text">远程</span>
        </el-button>
        <span style="padding: 0 5px">|</span>
        <el-button type="text" @click="handleImport">
          <span style="color: #a4b9cb" class="btn_text">导入故障定义文本</span>
        </el-button>
      </div>
      <div>

        <span style="font-size: 15px">
          当前模式: <span v-if="mode === 0">就地</span><span v-else-if="mode === 1">远程</span><span v-else>未知</span>
        </span>
        <span style="padding-left:10px;font-size: 15px">主机版本:{{ version }}</span>
      </div>
    </div>
    <div class="right-menu">
      <!-- <template v-if="device!=='mobile'">
        <search id="header-search" class="right-menu-item" />

        <el-tooltip content="源码地址" effect="dark" placement="bottom">
          <ruo-yi-git id="ruoyi-git" class="right-menu-item hover-effect" />
        </el-tooltip>

        <el-tooltip content="文档地址" effect="dark" placement="bottom">
          <ruo-yi-doc id="ruoyi-doc" class="right-menu-item hover-effect" />
        </el-tooltip>

        <screenfull id="screenfull" class="right-menu-item hover-effect" />

        <el-tooltip content="布局大小" effect="dark" placement="bottom">
          <size-select id="size-select" class="right-menu-item hover-effect" />
        </el-tooltip>

      </template> -->

      <el-dropdown
        class="avatar-container right-menu-item hover-effect"
        trigger="hover"
      >
        <span class="avatar-logo">{{ name ? name.split('')[0] : 'N' }}</span>
        <!--        <div class="avatar-wrapper">-->
        <!--          -->
        <!--          <img :src="avatar" class="user-avatar" />-->
        <!--           <i class="el-icon-caret-bottom" /> -->
        <!--        </div>-->
        <el-dropdown-menu slot="dropdown">
          <router-link to="/user/profile">
            <el-dropdown-item>个人中心</el-dropdown-item>
          </router-link>
          <el-dropdown-item @click.native="setting = true">
            <span>布局设置</span>
          </el-dropdown-item>
          <el-dropdown-item divided @click.native="logout">
            <span>退出登录</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
    <!-- 遥测导入对话框 -->
    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px" append-to-body>
      <el-upload
        ref="upload"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm" @keydown.enter="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import TopNav from '@/components/TopNav'
import Hamburger from '@/components/Hamburger'
import Screenfull from '@/components/Screenfull'
import SizeSelect from '@/components/SizeSelect'
import Search from '@/components/HeaderSearch'
import RuoYiGit from '@/components/RuoYi/Git'
import RuoYiDoc from '@/components/RuoYi/Doc'
import { cabinetList, getModule, getVersion } from '@/api/index'
import { getNews } from '@/api/zm/alarm/list'
import { getToken } from '@/utils/auth'
import { isInElectron } from '@/utils/ruoyi'
import { setModule, testGetData } from '@/api/zm/navbar'
import { delay } from '@/utils'

export default {
  components: {
    Breadcrumb,
    TopNav,
    Hamburger,
    Screenfull,
    SizeSelect,
    Search,
    RuoYiGit,
    RuoYiDoc
  },
  data() {
    return {
      show: false,
      navibarDeviceValue: '',
      cabinetList: [],
      timer: null,
      news: undefined,
      // 导入参数
      upload: {
        // 是否显示弹出层
        open: false,
        // 弹出层标题
        title: '',
        // 是否禁用上传
        isUploading: false,
        // 设置上传的请求头部
        headers: { Authorization: 'Bearer ' + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + '/zm/file/import01Data'
      },
      loadingModule: false,
      mode: '',
      version: ''
    }
  },

  watch: {
    $route: {
      handler(n) {
        this.show = !!(n.fullPath && n.fullPath.startsWith('/device'))
      },
      immediate: true,
      deep: true
    }
  },
  computed: {
    ...mapGetters(['sidebar', 'avatar', 'device', 'name', 'roles']),
    setting: {
      get() {
        return this.$store.state.settings.showSettings
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'showSettings',
          value: val
        })
      }
    },
    topNav: {
      get() {
        return this.$store.state.settings.topNav
      }
    }
  },
  methods: {
    isInElectron,
    /**
     * @description 文字转语音方法
     * @public
     * @param { text, speechRate, lang, volume, pitch } object
     * @param  text 要合成的文字内容，字符串
     * @param  lang 读取文字时的语言
     * @param  volume  读取时声音的音量 0~1  正常1
     * @param  pitch  读取时声音的音高 0~2  正常1
     * @param endEvent
     * @param startEvent
     * @returns SpeechSynthesisUtterance
     */
    speak({ text = '', speechRate = 1, lang = 'zh-CN', volume = 1, pitch = 1 }, endEvent = () => {
    }, startEvent = {}) {
      if (!window.SpeechSynthesisUtterance) {
        console.warn('当前浏览器不支持文字转语音服务')
        return null
      }

      if (!text) {
        return null
      }

      const speechUtterance = new SpeechSynthesisUtterance()
      speechUtterance.text = text
      speechUtterance.rate = speechRate || 1
      speechUtterance.lang = lang || 'zh-CN'
      speechUtterance.volume = volume || 1
      speechUtterance.pitch = pitch || 1
      speechUtterance.onend = function() {
        endEvent && endEvent()
      }
      speechUtterance.onstart = function() {
        startEvent && startEvent()
      }
      speechSynthesis.speak(speechUtterance)

      return speechUtterance
    },
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    async getList() {
      return cabinetList({
        pageNum: 1,
        pageSize: 1000
      }).then(res => {
        // console.log(res)
        this.cabinetList = res.rows || []
        if (this.cabinetList.length) {
          console.log('this.cabinetList[0].id ========>', this.cabinetList[0].deviceId)
          this.handleChange(this.cabinetList[0].deviceId)
        }
      })
    },
    async logout() {
      this.$confirm('确定注销并退出系统吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('LogOut').then(() => {
          // location.href = process.env.VUE_APP_CONTEXT_PATH
          this.$router.replace('/login')
        })
      }).catch(() => {
      })
    },
    handleChange(e) {
      console.log(e)
      this.navibarDeviceValue = e
      console.log(this.$root.$children[0])

      this.$root.$children[0]?.setNavibarDeviceValue(e)
    },
    async getNews() {
      await getNews().then(res => {
        this.news = res.data || undefined
        // this.speak({
        //   text: '告警信息:' + this.news,
        //   speechRate: 1,
        //   lang: 'zh-CN',
        //   volume: 1,
        //   pitch: 1
        // }, null, null)
      })
      await delay(2000)
      await getModule().then(res => {
        this.mode = res.data
      })
      await delay(2000)
      await getVersion().then(res => {
        this.version = res.data
      })
    },

    // 文件上传中处理
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true
    },
    // 文件上传成功处理
    handleFileSuccess(response, file, fileList) {
      this.upload.open = false
      this.upload.isUploading = false
      this.$refs.upload.clearFiles()
      this.$alert(response.msg, '导入结果', { dangerouslyUseHTMLString: true })
      // this.getList();
    },
    //导入遥测协议
    handleImport() {
      this.upload.title = '遥信协议导入'
      this.upload.open = true
    },
    // 提交上传文件
    submitFileForm() {
      this.$refs.upload.submit()
    },
    setModule(module) {
      this.loadingModule = true
      setModule({
        module
      }).then(res => {
        this.$message.success(res.msg || '操作成功')
      }).finally(() => {
        this.loadingModule = false
      })
    },
    testGetData() {
      testGetData().then(res => {
        this.$message.success(res.msg || '操作成功!')
      })
    }
  },
  async created() {
    await this.getList()
    this.timer = setInterval(() => {
      // this.getList()
      this.getNews()
    }, 10 * 1000)
    this.getNews()
  },
  beforeDestroy() {
    clearInterval(this.timer)
  }
}
</script>

<style lang="scss" scoped>
.avatar-logo {
  display: flex;
  height: 45px;
  width: 45px;
  border-radius: 50%;
  background: gray;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.navbar {
  min-height: 50px;
  overflow: hidden;
  position: relative;
  background: #304157;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.5);
  display: flex;
  flex-wrap: wrap;
  align-items: center;

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    //float: left;
    cursor: pointer;
    transition: background 0.3s;
    -webkit-tap-highlight-color: transparent;

    &:hover {
      background: rgba(0, 0, 0, 0.025);
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .topmenu-container {
    position: absolute;
    left: 50px;
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .alert {
    //float: left;
    padding-left: 100px;
    display: flex;
    height: 100%;
    align-items: center;
    cursor: pointer;
  }

  .operations {
    //float: right;
    // padding-left: 100px;
    display: flex;
    flex-direction: column;
    //flex: 1;
    justify-content: center;
    align-items: flex-end;
    color: #a4b9cb;
  }

  .right-menu {
    //float: right;
    height: 100%;
    //line-height: 50px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background 0.3s;

        &:hover {
          background: rgba(0, 0, 0, 0.025);
        }
      }
    }

    .avatar-container {
      margin: 0 10px;

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }

  .btn_text:hover {
    color: #ececec !important;
  }

  .btn_text:active {
    color: #007adc !important;
  }

  .alert-message {
    flex: 1;
    min-width: 200px;
    overflow: hidden;
    padding: 0 10px;
    display: flex;
    justify-content: flex-start;
  }

  .alert {
    margin-left: 10px;
    //display: flex;
    //align-items: center;
    padding: 5px;
    border-radius: 15px;
    color: white;
    background-color: #ff4d51;
    //border: 0.8px solid #fff;
    font-size: 12px;

    &:hover {
      background: #b9383c;
    }

    //&:active{
    //  background: #641f22;
    //}

    & > span:nth-child(1) {
      padding: 0 5px;
    }

    & > span:nth-child(2) {
      padding-right: 5px;
      display: inline-block;
      overflow: hidden;
      text-overflow: ellipsis;
      word-break: break-all;
      word-wrap: break-word;
    }
  }
}

::v-deep .select {
  .el-input {
    .el-input__inner {
      font-size: 12px;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }
}
</style>
