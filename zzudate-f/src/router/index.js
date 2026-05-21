import { createRouter, createWebHistory } from 'vue-router'
import Welcome from '../views/Welcome.vue'
import Info from '../views/Info.vue'
import Question from '../views/Question.vue'
import Home from '../views/Home.vue'
import Result from '../views/Result.vue'
import Community from '../views/Community.vue'
import CommunityLogin from '../views/CommunityLogin.vue'
import Profile from '../views/Profile.vue'

const routes = [
  { path: '/', redirect: '/welcome' },
  { path: '/welcome', component: Welcome },
  { path: '/info', component: Info }, // 基础资料页
  { path: '/question', component: Question },
  { path: '/home', component: Home},
  { path: '/result', component: Result},
  { path: '/community', component: Community},
  { path: '/community/login', component: CommunityLogin},
  { path: '/profile', component: Profile}
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 不需要登录的页面
const whiteList = ['/welcome', '/community/login']

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (token) {
    next()
  } else if (whiteList.includes(to.path)) {
    next()
  } else if (to.path.startsWith('/community')) {
    next('/community/login')
  } else {
    next('/welcome')
  }
})

export default router