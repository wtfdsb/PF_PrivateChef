const api = require('../../utils/api.js')

const TABS = [
  { code: 'cold', label: '凉菜' },
  { code: 'hot', label: '热菜' },
  { code: 'staple', label: '主食' },
  { code: 'deposit', label: '需预定·押金' },
]

Page({
  data: {
    loading: true,
    error: '',
    tabs: TABS,
    active: 'cold',
    /** 按分类缓存已加载的菜品 */
    cache: {},
    items: [],
  },

  async onLoad(query) {
    if (query.cat && TABS.some((t) => t.code === query.cat)) {
      this.setData({ active: query.cat })
    }
    await this.loadTab(this.data.active)
  },

  async onTab(e) {
    const code = e.currentTarget.dataset.code
    if (code === this.data.active) return
    this.setData({ active: code })
    await this.loadTab(code)
  },

  async loadTab(code) {
    const cached = this.data.cache[code]
    if (cached) {
      this.setData({ items: cached, loading: false, error: '' })
      return
    }
    this.setData({ loading: true, error: '' })
    try {
      const items = await api.listMenuItems(code)
      this.setData({
        items,
        loading: false,
        [`cache.${code}`]: items,
      })
    } catch (e) {
      this.setData({ loading: false, error: e.message || '加载失败' })
    }
  },

  /** 想点这些菜？去预约时在备注里说，或电话聊 */
  goBooking() {
    wx.navigateTo({ url: '/pages/booking/booking' })
  },

  callChef() {
    const app = getApp()
    const phone = (app.globalData.chef && app.globalData.chef.phone) || ''
    if (!phone || phone.indexOf('X') >= 0) {
      wx.showToast({ title: '电话待配置', icon: 'none' })
      return
    }
    wx.makePhoneCall({ phoneNumber: phone })
  },

  onShareAppMessage() {
    return {
      title: '新谷私厨 · 单点菜单，看中哪道打电话说',
      path: '/pages/menu-items/menu-items',
    }
  },
})
