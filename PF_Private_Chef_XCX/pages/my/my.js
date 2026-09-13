const api = require('../../utils/api.js')

const STATUS_TEXT = {
  pending: '待确认',
  confirmed: '已定档',
  making: '制作中',
  done: '已完成',
  canceled: '已取消',
}

/** 进度时间线的四步 */
const STEP_ORDER = ['pending', 'confirmed', 'making', 'done']
const STEP_TEXT = {
  pending: '已提交',
  confirmed: '已确认',
  making: '制作中',
  done: '已完成',
}

/** 给每单计算进度条状态 */
function decorate(list) {
  return list.map((b) => {
    const idx = STEP_ORDER.indexOf(b.status)
    const canceled = b.status === 'canceled'
    const steps = STEP_ORDER.map((s, i) => ({
      name: STEP_TEXT[s],
      state: canceled ? 'skip' : i < idx ? 'done' : i === idx ? 'on' : '',
    }))
    return { ...b, steps, canceled }
  })
}

Page({
  data: {
    loading: true,
    list: [],
    statusText: STATUS_TEXT,
    chef: null,
  },

  onShow() {
    this.load()
  },

  onPullDownRefresh() {
    this.load().finally(() => wx.stopPullDownRefresh())
  },

  async load() {
    try {
      const [list, chef] = await Promise.all([api.listMyBookings(), api.getChef()])
      this.setData({ list: decorate(list), chef, loading: false })
    } catch (e) {
      this.setData({ loading: false })
      wx.showToast({ title: e.message || '加载失败', icon: 'none' })
    }
  },

  callChef(e) {
    const phone = (e && e.currentTarget.dataset.phone) || (this.data.chef && this.data.chef.phone) || ''
    if (!phone || phone.indexOf('X') >= 0) {
      wx.showToast({ title: '电话待配置', icon: 'none' })
      return
    }
    wx.makePhoneCall({ phoneNumber: phone })
  },

  goBooking() {
    wx.navigateTo({ url: '/pages/booking/booking' })
  },
})
