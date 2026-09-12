/**
 * 数据访问层 —— 页面只调这里，不直接碰 wx.request / wx.cloud
 *
 * 切换后端只改本文件：
 *   A. 开发期（默认）        → 本地假数据，零依赖
 *   B. 微信云开发 CloudBase  → 打开 CLOUD 分支（见下方注释）
 *   C. Java / Spring Boot    → 打开 HTTP 分支（见下方注释）
 */

const app = getApp
const mock = require('./mock.js')

/** 取全局配置（页面生命周期外调用要容错） */
function cfg() {
  try {
    return getApp().globalData.config
  } catch (e) {
    return { useMock: true, apiBase: '' }
  }
}

/* ============================================================
 * HTTP 通道（接 Java 后端时启用）
 * ========================================================== */
function request(path, { method = 'GET', data = {}, header = {} } = {}) {
  const c = cfg()
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${c.apiBase}${path}`,
      method,
      data,
      header: { 'content-type': 'application/json', ...header },
      timeout: 15000,
      success(res) {
        if (res.statusCode >= 200 && res.statusCode < 300 && res.data && res.data.code === 0) {
          resolve(res.data.data)
        } else {
          reject(new Error((res.data && res.data.msg) || `请求失败(${res.statusCode})`))
        }
      },
      fail(err) {
        reject(new Error(err.errMsg || '网络异常'))
      },
    })
  })
}

/* ============================================================
 * 云开发通道（接 CloudBase 时启用）
 * ========================================================== */
async function cloudCall(name, data = {}) {
  const res = await wx.cloud.callFunction({ name, data })
  const r = res && res.result
  if (!r || r.code !== 0) throw new Error((r && r.msg) || '云函数调用失败')
  return r.data
}

/* ============================================================
 * 对外 API —— 页面只认这一层
 * ========================================================== */

/** 厨师名片 */
async function getChef() {
  const cached = getApp().globalData.chef
  if (cached) return cached

  let chef
  if (cfg().useMock) {
    chef = mock.chef
  } else {
    // 二选一：
    // chef = await cloudCall('getChef')
    chef = await request('/api/chef')
  }
  getApp().globalData.chef = chef
  return chef
}

/** 作品案例列表 */
async function listCases({ scene = '', limit = 20 } = {}) {
  if (cfg().useMock) {
    let list = mock.cases.slice()
    if (scene) list = list.filter((c) => c.scene === scene)
    return list.slice(0, limit)
  }
  // return cloudCall('listCases', { scene, limit })
  return request('/api/cases', { data: { scene, limit } })
}

/** 作品详情 */
async function getCase(id) {
  if (cfg().useMock) {
    const hit = mock.cases.find((c) => c.id === id)
    if (!hit) throw new Error('作品不存在')
    return hit
  }
  // return cloudCall('getCase', { id })
  return request(`/api/cases/${id}`)
}

/** 参考套餐 */
async function listPackages() {
  if (cfg().useMock) return mock.packages.slice()
  // return cloudCall('listPackages')
  return request('/api/packages')
}

/** 档期（默认可约的未来 30 天） */
async function listSlots({ from, to } = {}) {
  if (cfg().useMock) {
    let list = mock.slots.slice()
    if (from) list = list.filter((s) => s.date >= from)
    if (to) list = list.filter((s) => s.date <= to)
    return list
  }
  // return cloudCall('listSlots', { from, to })
  return request('/api/slots', { data: { from, to } })
}

/** 客户评价 */
async function listReviews() {
  if (cfg().useMock) return mock.reviews.slice()
  // return cloudCall('listReviews')
  return request('/api/reviews')
}

/**
 * 提交预约（核心写操作）
 * @param {object} payload
 * @param {string} payload.name      联系人
 * @param {string} payload.phone     手机号
 * @param {string} payload.date      期望日期 YYYY-MM-DD
 * @param {string} payload.meal      午宴 / 晚宴
 * @param {number} payload.people    人数
 * @param {string} payload.budget    预算区间
 * @param {string} payload.address   场地地址
 * @param {string} payload.remark    忌口 / 过敏 / 其他要求
 * @param {string} payload.source    来源渠道（直接 / 转介绍-某某）
 */
async function submitBooking(payload) {
  // 前端基础校验（后端必须再校验一遍）
  if (!payload.name || !String(payload.name).trim()) throw new Error('请填写称呼')
  if (!/^1[3-9]\d{9}$/.test(String(payload.phone || ''))) throw new Error('请填写正确的手机号')
  if (!payload.date) throw new Error('请选择日期')
  if (!payload.meal) throw new Error('请选择餐次')
  if (!payload.people || payload.people < 1) throw new Error('请填写人数')
  if (!payload.address || !String(payload.address).trim()) throw new Error('请填写场地地址')

  const body = {
    ...payload,
    people: Number(payload.people),
    // 顺手带上来源，用于后续判断转介绍效果
    channel: (getApp().globalData.launchQuery || {}).from || payload.source || 'direct',
    createdAt: Date.now(),
  }

  // 本地记住手机号，让「我的预约」在没有登录态时也能查到自己的单子
  // TODO(上线前): 接上 wx.login 后改为按登录态查询，去掉手机号兜底
  try {
    wx.setStorageSync('my_phone', payload.phone)
  } catch (e) { /* 忽略存储失败 */ }

  if (cfg().useMock) {
    // 开发期：本地存一份，方便在「我的预约」里看到
    const list = wx.getStorageSync('mock_bookings') || []
    const item = { ...body, id: `bk_${Date.now()}`, status: 'pending' }
    list.unshift(item)
    wx.setStorageSync('mock_bookings', list)
    return item
  }
  // return cloudCall('submitBooking', body)
  return request('/api/bookings', { method: 'POST', data: body })
}

/** 我的预约（同一微信 / 同一手机号下的预约记录） */
async function listMyBookings() {
  if (cfg().useMock) return wx.getStorageSync('mock_bookings') || []
  // return cloudCall('listMyBookings')
  // 未接登录前用本机记住的手机号兜底；接上 wx.login 后应改为只认 token
  let phone = ''
  try {
    phone = wx.getStorageSync('my_phone') || ''
  } catch (e) { /* 忽略 */ }
  return request('/api/bookings/mine', { data: { phone } })
}

module.exports = {
  getChef,
  listCases,
  getCase,
  listPackages,
  listSlots,
  listReviews,
  submitBooking,
  listMyBookings,
}
