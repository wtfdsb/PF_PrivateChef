/**
 * 小程序入口
 *
 * 数据来源开关：config.useMock = true 时全部走本地假数据，
 * 打开微信开发者工具即可直接跑，不需要后端、不需要 AppID 资质。
 * 接后端时只改 utils/api.js，页面代码一行不用动。
 */
const config = {
  // 开发期用 true；接上后端后改为 false
  useMock: true,
  // 后端基地址（useMock = false 时生效）
  // 本地联调：http://localhost:8080  （开发者工具里记得勾「不校验合法域名」）
  // 上线：必须换成 https + 已备案域名，并加入小程序 request 合法域名白名单
  apiBase: 'http://localhost:8080',
  // 厨师主体信息（会与远端数据合并，作为兜底）
  brand: {
    name: '私宴到家',
    city: '河南 · 平顶山',
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
  },
})
