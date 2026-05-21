import axios from 'axios'
import router from '../router'

// 请求拦截器：自动注入 token
axios.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = token
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器：统一处理 401
axios.interceptors.response.use(
  response => response,
  error => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
      localStorage.removeItem('userName')
      localStorage.removeItem('exp')
      localStorage.removeItem('base_info')
      localStorage.removeItem('lastSyncDate')

      const currentPath = router.currentRoute.value.path
      if (currentPath.startsWith('/community')) {
        router.push('/community/login')
      } else {
        router.push('/welcome')
      }
    }
    return Promise.reject(error)
  }
)

export default axios
