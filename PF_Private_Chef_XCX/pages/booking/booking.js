const api = require('../../utils/api.js')

const BUDGETS = ['500-1000', '1000-2000', '2000-3500', '3500-5000', '5000 以上', '还没定']

Page({
  data: {
    submitting: false,
    /** 可选档期（只展示可约的） */
    slots: [],
    /** 日期选择器的候选项 */
    dateOptions: [],
    dateIndex: 0,
    mealOptions: ['午宴', '晚宴'],
    mealIndex: 0,
    budgetOptions: BUDGETS,
    budgetIndex: 1,

    form: {
      name: '',
      phone: '',
      people: '',
      address: '',
      remark: '',
      source: '',
    },
  },

  async onLoad(query) {
    const slots = (await api.listSlots()).filter((s) => s.status === 'open')

    // 日期去重，保留顺序
    const seen = new Set()
    const dateOptions = []
    slots.forEach((s) => {
      if (!seen.has(s.date)) {
        seen.add(s.date)
        dateOptions.push(s.date)
      }
    })

    let dateIndex = 0
    let mealIndex = 0
    if (query.date) {
      const i = dateOptions.indexOf(query.date)
      if (i >= 0) dateIndex = i
    }
    if (query.meal) {
      const j = this.data.mealOptions.indexOf(decodeURIComponent(query.meal))
      if (j >= 0) mealIndex = j
    }

    this.setData({ slots, dateOptions, dateIndex, mealIndex })
  },

  onInput(e) {
    const key = e.currentTarget.dataset.key
    this.setData({ [`form.${key}`]: e.detail.value })
  },

  onDateChange(e) {
    this.setData({ dateIndex: Number(e.detail.value) })
  },

  onMealChange(e) {
    this.setData({ mealIndex: Number(e.detail.value) })
  },

  onBudgetChange(e) {
    this.setData({ budgetIndex: Number(e.detail.value) })
  },

  /** 提交预约 */
  async onSubmit() {
    if (this.data.submitting) return

    const { form, dateOptions, dateIndex, mealOptions, mealIndex, budgetOptions, budgetIndex } = this.data

    // 档期冲突提醒：选中的日期+餐次若已满，直接拦下
    const date = dateOptions[dateIndex]
    const meal = mealOptions[mealIndex]
    const clash = this.data.slots.find((s) => s.date === date && s.meal === meal)
    if (!clash) {
      wx.showToast({ title: '该档期已约满，换一个吧', icon: 'none' })
      return
    }

    this.setData({ submitting: true })
    try {
      const booking = await api.submitBooking({
        ...form,
        date,
        meal,
        budget: budgetOptions[budgetIndex],
      })
      getApp().globalData.lastBooking = booking

      // 订阅消息：预约成功后立刻引导授权，用于后续「档期确认 / 出发提醒」
      // TODO(上线前)：在微信公众平台申请模板后填入 tmplIds
      // wx.requestSubscribeMessage({ tmplIds: [] })

      wx.redirectTo({ url: '/pages/booking/result/result' })
    } catch (e) {
      wx.showToast({ title: e.message || '提交失败', icon: 'none' })
    } finally {
      this.setData({ submitting: false })
    }
  },
})
