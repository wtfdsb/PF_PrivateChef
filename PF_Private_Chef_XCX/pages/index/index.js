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
    } catch (e) {
      this.setData({ loading: false, error: e.message || '加载失败' })
    }
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
