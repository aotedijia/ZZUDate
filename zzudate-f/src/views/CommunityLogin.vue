<template>
  <div class="app">
    <header class="site-header">
      <div class="header-inner">
        <h1 class="site-title">ZZU Community</h1>
      </div>
    </header>

    <main class="main">
      <div class="main-inner">
        <div class="login-card">
          <h2 class="login-title">登录</h2>
          <p class="login-sub">验证即登录，未注册用户将自动创建账号</p>

          <div class="form-group">
            <label>邮箱</label>
            <div class="email-input-group">
              <input v-model="emailPrefix" type="text" placeholder="邮箱用户名" autocomplete="username" />
              <div class="email-domain-row">
                <span class="at-symbol">@</span>
                <select v-model="emailSuffix">
                  <option value="gs.zzu.edu.cn">gs.zzu.edu.cn</option>
                  <option value="zzu.edu.cn">zzu.edu.cn</option>
                  <option value="stu.zzu.edu.cn">stu.zzu.edu.cn</option>
                </select>
              </div>
            </div>
          </div>

          <div class="form-group">
            <label>验证码</label>
            <div class="code-input-group">
              <input v-model="code" type="text" inputmode="numeric" maxlength="6" placeholder="输入6位验证码" />
              <button class="btn-send" @click="sendCode" :disabled="countdown > 0">
                {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
              </button>
            </div>
          </div>

          <button class="login-btn" @click="handleLogin">登录</button>
        </div>
      </div>
    </main>

<footer class="site-footer">
      <div class="footer-inner">
        <p>
          © <span class="mono">2026</span> ZZU Date · 仅供郑州大学在校生使用
          <span style="margin: 0 8px;">|</span>
          <a href="https://beian.miit.gov.cn/" target="_blank" style="color: #aaa; text-decoration: none;">
            豫ICP备<span class="mono">2026018839</span>号-<span class="mono">1</span>
          </a>
        </p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from '../utils/request'

const router = useRouter()
const emailPrefix = ref('')
const emailSuffix = ref('gs.zzu.edu.cn')
const code = ref('')
const countdown = ref(0)

const getFullEmail = () => `${emailPrefix.value}@${emailSuffix.value}`

const sendCode = async () => {
  if (!emailPrefix.value) { alert('请先输入邮箱前缀'); return }
  try {
    const params = new URLSearchParams()
    params.append('email', getFullEmail())
    const res = await axios.post('/api/auth/getemailcode', params)
    alert(res.data.message || '验证码已发送')
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) clearInterval(timer)
    }, 1000)
  } catch (err) {
    alert(err.response?.data?.msg || '发送失败')
  }
}

const handleLogin = async () => {
  if (!emailPrefix.value || !code.value) { alert('请输入完整的邮箱和验证码'); return }
  try {
    const params = new URLSearchParams()
    params.append('email', getFullEmail())
    params.append('emailCode', code.value)
    const res = await axios.post('/api/auth/emailloginautoregister', params)
    if (res.data.code === 200) {
      localStorage.setItem('token', res.data.data.accessToken)
      localStorage.setItem('userId', res.data.data.userId)
      localStorage.setItem('userName', res.data.data.name || '')
      localStorage.setItem('exp', res.data.data.exp || 0)
      const avatarSeeds = ['Sophia','Sawyer','Avery','Liliana','Aiden','Wyatt','Eliza','Amaya']
      localStorage.setItem('avatarSeed', avatarSeeds[Math.floor(Math.random() * avatarSeeds.length)])
      router.push('/community')
    } else {
      alert(res.data.message || '登录失败')
    }
  } catch (err) {
    alert('登录错误：' + (err.response?.data?.msg || err.message))
  }
}
</script>

<style scoped>
* { box-sizing: border-box; margin: 0; padding: 0; }

.app {
  font-family: Georgia, 'Noto Serif SC', '宋体', serif;
  background: #fff;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* ========== Header ========== */
.site-header {
  border-bottom: 1px solid #ddd;
  background: #fff;
}
.header-inner {
  max-width: 680px;
  margin: 0 auto;
  padding: 16px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.site-title {
  font-size: 18px;
  font-weight: 700;
  color: #1a0dab;
  cursor: pointer;
  letter-spacing: -0.3px;
}
.site-nav {
  display: flex;
  gap: 18px;
}
.site-nav a {
  font-size: 14px;
  color: #1a0dab;
  cursor: pointer;
  text-decoration: none;
}
.site-nav a:hover {
  text-decoration: underline;
}

/* ========== Main ========== */
.main {
  flex: 1;
}
.main-inner {
  max-width: 680px;
  margin: 0 auto;
  padding: 60px 24px;
}

/* ========== Login Card ========== */
.login-card {
  max-width: 380px;
  margin: 0 auto;
}

.login-title {
  font-size: 24px;
  font-weight: 700;
  color: #222;
  margin-bottom: 6px;
}
.login-sub {
  font-size: 13px;
  color: #999;
  margin-bottom: 32px;
  line-height: 1.6;
}

.form-group {
  margin-bottom: 20px;
}
.form-group label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #555;
  margin-bottom: 6px;
  font-family: -apple-system, 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.email-input-group {
  display: flex;
  flex-direction: column;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
  transition: border-color 0.2s;
}
.email-input-group:focus-within {
  border-color: #1a0dab;
}
.email-input-group input {
  border: none;
  outline: none;
  padding: 10px 12px;
  font-size: 14px;
  background: #fff;
  color: #333;
  font-family: -apple-system, 'PingFang SC', 'Microsoft YaHei', sans-serif;
}
.email-domain-row {
  display: flex;
  align-items: center;
  border-top: 1px solid #ddd;
  padding: 0 12px;
  background: #fafafa;
}
.at-symbol {
  color: #999;
  font-weight: 600;
  margin-right: 4px;
  font-size: 13px;
}
.email-input-group select {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  color: #555;
  font-weight: 500;
  padding: 10px 0;
  font-size: 13px;
  -webkit-appearance: none;
  appearance: none;
  cursor: pointer;
  font-family: -apple-system, 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.code-input-group {
  display: flex;
  gap: 10px;
}
.code-input-group input {
  flex: 1;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 10px 12px;
  font-size: 14px;
  outline: none;
  background: #fff;
  color: #333;
  transition: border-color 0.2s;
  font-family: "SF Mono", "Menlo", "Consolas", monospace;
}
.code-input-group input:focus {
  border-color: #1a0dab;
}
.btn-send {
  min-width: 90px;
  border: 1px solid #1a0dab;
  background: #fff;
  color: #1a0dab;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.15s;
  font-family: -apple-system, 'PingFang SC', 'Microsoft YaHei', sans-serif;
}
.btn-send:hover:not(:disabled) {
  background: #1a0dab;
  color: #fff;
}
.btn-send:disabled {
  border-color: #ddd;
  color: #bbb;
  cursor: not-allowed;
}

.login-btn {
  width: 100%;
  padding: 12px;
  background: #1a0dab;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 8px;
  transition: background 0.15s;
  font-family: -apple-system, 'PingFang SC', 'Microsoft YaHei', sans-serif;
}
.login-btn:hover {
  background: #120880;
}

/* ========== Footer ========== */
.site-footer {
  border-top: 1px solid #ddd;
  background: #fff;
  margin-top: auto;
}
.footer-inner {
  max-width: 680px;
  margin: 0 auto;
  padding: 20px 24px;
}
.footer-inner p {
  font-size: 12px;
  color: #aaa;
}
.footer-inner .mono {
  font-family: 'SF Mono', Menlo, Consolas, monospace;
}

@media (max-width: 600px) {
  .main-inner { padding: 40px 16px; }
  .login-card { max-width: 100%; }
}
</style>
