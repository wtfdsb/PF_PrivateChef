/**
 * 小程序入口
 *
 * 数据来源开关：config.useMock = true 时全部走本地假数据，
 * 打开微信开发者工具即可直接跑，不需要后端、不需要 AppID 资质。
 * 接后端时只改 utils/api.js，页面代码一行不用动。
 */
const config = {
  // 线上环境：直连微信云托管（云端 7×24，本地不用跑后端）
  // 想回本地假数据调试：改成 true
  useMock: false,
  // 请求通道：cloud = 微信官方云调用（推荐，免域名免备案）；http = 公网/本地地址
  mode: 'cloud',
  // 云托管环境与服务（mode=cloud 时生效）
  cloudEnv: 'prod-d4g54iuor98846114',
  serviceName: 'springboot-8oo4',
  // 后端基地址（mode=http 时生效；本地联调 http://localhost:8080 需勾「不校验合法域名」）
  apiBase: 'https://springboot-8oo4-313156-9-1487241248.sh.run.tcloudbase.com',
  // 厨师主体信息（会与远端数据合并，作为兜底）
  brand: {
    name: '新谷私厨',
    city: '河南 · 平顶山',
    slogan: '把星级宴席搬进您家',
  },
}

App({
  globalData: {
    config,
    /** @type {object|null} 厨师资料缓存 */
    chef: null,
    /** @type {object|null} 最近一次提交的预约 */
    lastBooking: null,
  },

  onLaunch() {
    // 记录来源渠道，用于判断转介绍效果（后续接后端时随预约一起上报）
    try {
      const scene = wx.getLaunchOptionsSync()
      this.globalData.launchScene = scene.scene
      this.globalData.launchQuery = scene.query || {}
    } catch (e) {
      this.globalData.launchQuery = {}
    }
    // 云调用通道初始化（失败静默，请求时会再兜底）
    try {
      if (config.mode === 'cloud' && config.cloudEnv && wx.cloud) {
        wx.cloud.init({ env: config.cloudEnv, traceUser: false })
      }
    } catch (e) { /* 忽略 */ }
  },
})
