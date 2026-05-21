/**
 * 社区公共工具函数
 */

export const formatTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

export const categories = [
  { key: '公开讨论区', name: '公开讨论区' },
  { key: '校医院', name: '校医院' },
  { key: '餐厅', name: '餐厅' },
  { key: '学习备考', name: '学习备考' }
]
