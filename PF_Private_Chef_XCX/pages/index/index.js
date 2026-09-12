const api = require('../../utils/api.js')

Page({
  data: {
    loading: true,
    error: '',
    chef: null,
    cases: [],
    packages: [],
    reviews: [],
    /** 最近可约的几个档期，用于首页快捷展示 */
    nextSlots: [],
    /** Hero 数据条滚动数字 */
    heroNums: { banquets: 0, rating: '0.0', cuisines: 0, years: 0 },
    /** 案例图加载失败记录（缺图自动回退到渐变占位） */
    caseImgErr: {},
    avatarErr: false,
  },

  onLoad() {
    this.load()
  },

  onPullDownRefresh() {
    this.load().finally(() => wx.stopPullDownRefresh())
  },

  async load() {
    this.setData({ loading: true, error: '' })
    try {
      const [chef, cases, packages, slots, reviews] = await Promise.all([
        api.getChef(),
        api.listCases({ limit: 6 }),
        api.listPackages(),
        api.listSlots(),
        api.listReviews(),
      ])
      this.setData({
        chef,
        cases,
        packages,
        reviews,
        nextSlots: slots.filter((s) => s.status === 'open').slice(0, 6),
        loading: false,
      })
      this.countUp()
    } catch (e) {
      this.setData({ loading: false, error: e.message || '加载失败' })
    }
  },

  /** Hero 数字滚动（约 0.9s，easeOutCubic） */
  countUp() {
    const chef = this.data.chef
    if (!chef) return
    const targets = {
      banquets: chef.stats.banquets || 0,
      rating: chef.stats.rating || 0,
      cuisines: chef.stats.cuisines || 0,
      years: chef.years || 0,
    }
    const start = Date.now()
    const dur = 900
    const tick = () => {
      const p = Math.min(1, (Date.now() - start) / dur)
      const e = 1 - Math.pow(1 - p, 3) // easeOutCubic
      const s = {
        banquets: Math.round(targets.banquets * e),
        rating: (targets.rating * e).toFixed(1),
        cuisines: Math.round(targets.cuisines * e),
        years: Math.round(targets.years * e),
      }
      this.setData({ heroNums: s })
      if (p < 1) setTimeout(tick, 16)
    }
    tick()
  },

  /** 头像/案例图加载失败 → 回退渐变占位 */
  onAvatarError() {
    this.setData({ avatarErr: true })
  },
  onCaseImgError(e) {
    const id = e.currentTarget.dataset.id
    this.setData({ [`caseImgErr.${id}`]: true })
  },

  /** 去预约页，可带上预选档期 */
  goBooking(e) {
    const { date = '', meal = '' } = (e && e.currentTarget && e.currentTarget.dataset) || {}
    const q = []
    if (date) q.push(`date=${date}`)
    if (meal) q.push(`meal=${encodeURIComponent(meal)}`)
    wx.navigateTo({ url: `/pages/booking/booking${q.length ? '?' + q.join('&') : ''}` })
  },

  goCase(e) {
    wx.navigateTo({ url: `/pages/case/detail/detail?id=${e.currentTarget.dataset.id}` })
  },

  goMenu() {
    wx.navigateTo({ url: '/pages/menu/menu' })
  },

  goAbout() {
    wx.navigateTo({ url: '/pages/about/about' })
  },

  goMy() {
    wx.navigateTo({ url: '/pages/my/my' })
  },

  /** 打电话（接单前最直接的沟通方式） */
  callChef() {
    const phone = (this.data.chef && this.data.chef.phone) || ''
    if (!phone) {
      wx.showToast({ title: '电话待配置', icon: 'none' })
      return
    }
    wx.makePhoneCall({ phoneNumber: phone })
  },

  /** 在线客服（open-type="contact" 走官方客服会话） */
  onContact() {
    // open-type="contact" 由按钮本身触发，这里留空作埋点占位
  },

  onShareAppMessage() {
    const name = (this.data.chef && this.data.chef.name) || '私厨'
    return {
      title: `${name} · 上门做宴席，平顶山全市`,
      path: '/pages/index/index?from=share',
    }
  },

  onShareTimeline() {
    return { title: '上门做宴席 · 平顶山全市' }
  },
})
