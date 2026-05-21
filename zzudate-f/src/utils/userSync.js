import axios from '../utils/request'

/**
 * 从服务端拉取最新用户信息并同步到 localStorage
 * 返回 true 表示成功，false 表示失败或未登录
 */
export async function syncUserInfo() {
  const token = localStorage.getItem('token')
  if (!token) return false

  try {
    const res = await axios.get('/match/getuserinfo')
    if (res.data.code === 200 && res.data.data) {
      const user = res.data.data
      if (user.name !== undefined) localStorage.setItem('userName', user.name || '')
      if (user.exp !== undefined) localStorage.setItem('exp', user.exp || 0)
      if (user.id) localStorage.setItem('userId', user.id)
      return true
    }
    return false
  } catch (err) {
    console.error('同步用户信息失败:', err)
    return false
  }
}

/**
 * 每日首次访问时同步用户信息
 * 通过 localStorage 记录上次同步日期，跨天则触发
 */
export async function dailySync() {
  const token = localStorage.getItem('token')
  if (!token) return

  const today = new Date().toISOString().slice(0, 10) // 'YYYY-MM-DD'
  const lastSync = localStorage.getItem('lastSyncDate')

  if (lastSync !== today) {
    const success = await syncUserInfo()
    if (success) {
      localStorage.setItem('lastSyncDate', today)
    }
  }
}
