<template>
  <div class="app">
    <header class="site-header">
      <div class="header-inner">
        <h1 class="site-title">ZZU Date</h1>
        <nav class="site-nav">
          <a @click="showAuth = true">登录 / 注册</a>
        </nav>
      </div>
    </header>

    <main class="main">
      <div class="main-inner">
        <!-- Hero -->
        <section class="hero">
          <div class="badge">仅限郑大邮箱</div>
          <h2 class="hero-title">
            在郑大，<br />遇见真正契合的人
          </h2>
          <p class="hero-sub">
            完成一份关于你的问卷，每周定时匹配一次。<br />
            不刷脸，不打分，只看契合度。
          </p>
          <div class="hero-cta">
            <button class="submit-btn" @click="showAuth = true">开始匹配</button>
            <button class="outline-btn" @click="scrollToHow">了解流程</button>
          </div>
        </section>

        <!-- Match Preview -->
        <section class="match-preview">
          <div class="match-card" v-for="(m, i) in matchPreviews" :key="i">
            <div class="match-avatar">{{ m.letter }}</div>
            <div class="match-info">
              <span class="match-name">{{ m.name }}</span>
              <span class="match-tag">{{ m.tag }}</span>
            </div>
            <span class="match-score">{{ m.score }}%</span>
          </div>
        </section>

        <div class="divider"></div>

        <!-- How -->
        <section class="how" ref="howSection" id="how">
          <h2 class="section-title">三步，开始你的缘分</h2>
          <div class="steps">
            <div class="step" v-for="(s, i) in steps" :key="i">
              <div class="step-num">{{ i + 1 }}</div>
              <div class="step-content">
                <h3 class="step-name">{{ s.title }}</h3>
                <p class="step-desc" v-html="s.desc"></p>
              </div>
            </div>
          </div>
        </section>

        <div class="divider"></div>

        <!-- Features -->
        <section class="features">
          <h2 class="section-title">为什么选择 ZZU Date</h2>
          <div class="feature-list">
            <div class="feature-item" v-for="(f, i) in features" :key="i">
              <div class="feature-icon">{{ f.icon }}</div>
              <div class="feature-text">
                <h3 class="feature-title">{{ f.title }}</h3>
                <p class="feature-desc">{{ f.desc }}</p>
              </div>
            </div>
          </div>
        </section>

        <div class="divider"></div>

        <!-- CTA -->
        <section class="cta-section">
          <h2 class="cta-title">还在等什么？</h2>
          <p class="cta-text">每周三进行匹配，下周三焕新</p>
          <button class="submit-btn" @click="showAuth = true">用学生邮箱注册</button>
        </section>
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

    <!-- 登录弹窗 -->
    <transition name="fade">
      <div class="overlay" v-if="showAuth" @click.self="showAuth = false">
        <div class="modal">
          <button class="modal-close" @click="showAuth = false">✕</button>
          <h2 class="modal-title">身份认证</h2>
          <p class="modal-sub">验证即登录，未注册用户将自动创建账号</p>

          <div class="form-group">
            <label>邮箱</label>
            <div class="email-group">
              <input v-model="authForm.emailPrefix" type="text" placeholder="邮箱用户名" autocomplete="username" />
              <div class="email-domain-row">
                <span class="at-symbol">@</span>
                <select v-model="authForm.emailSuffix">
                  <option value="gs.zzu.edu.cn">gs.zzu.edu.cn</option>
                  <option value="zzu.edu.cn">zzu.edu.cn</option>
                  <option value="stu.zzu.edu.cn">stu.zzu.edu.cn</option>
                </select>
              </div>
            </div>
          </div>

          <div class="form-group">
            <label>验证码</label>
            <div class="code-group">
              <input v-model="authForm.code" type="text" inputmode="numeric" maxlength="6" placeholder="输入6位验证码" />
              <button class="send-btn" @click="sendCode" :disabled="countdown > 0">
                {{ countdown > 0 ? `${countdown}s` : '获取' }}
              </button>
            </div>
          </div>

          <button class="submit-btn btn-full" @click="handleAuth">立即进入</button>
          <p class="modal-note">验证码将发送至您的郑大邮箱</p>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import axios from '../utils/request'

export default {
  name: 'ZzuDate',
  data() {
    return {
      showAuth: false,
      countdown: 0,
      authForm: {
        emailPrefix: '',
        emailSuffix: 'gs.zzu.edu.cn',
        code: ''
      },
      matchPreviews: [
        { letter: 'A', name: '小崔 · 文学院', tag: '喜欢爬山 · 读哲学', score: '92' },
        { letter: 'B', name: 'Bob · 计算机与人工智能学院', tag: '喜欢摄影 · 动漫', score: '87' },
        { letter: 'C', name: '小七 · 商学院', tag: '喜欢美食 · 综艺喜剧', score: '84' },
      ],
      steps: [
        { title: '完成问卷', desc: '回答一系列关于性格、兴趣、生活方式和期待的问题，只需 <span class="mono">10</span> 分钟。' },
        { title: '等待匹配', desc: '每周三晚<span class="mono">19</span>点系统根据契合度算法为你生成本周匹配。' },
        { title: '开始聊天', desc: '收到匹配后如果双方都感兴趣可选择互换联系方式，故事就从这里开始。' },
      ],
      features: [
        { icon: '🔒', title: '仅限郑大学生', desc: '需要郑大邮箱验证，保证社区纯净可信。' },
        { icon: '📋', title: '问卷驱动匹配', desc: '不刷颜值，凭真实兴趣与三观建立连接。' },
        { icon: '⏰', title: '每周一次机会', desc: '克制的频率，让每一次匹配都值得认真对待。' },
        { icon: '🤝', title: '双向意愿', desc: '双方都表示感兴趣才会开放联系方式，保护你的隐私。' },
      ],
    }
  },
  methods: {
    scrollToHow() {
      this.$refs.howSection?.scrollIntoView({ behavior: 'smooth' })
    },
    getFullEmail() {
      return `${this.authForm.emailPrefix}@${this.authForm.emailSuffix}`;
    },
    async sendCode() {
      if (!this.authForm.emailPrefix) { alert('请先输入邮箱前缀'); return; }
      try {
        const params = new URLSearchParams();
        params.append('email', this.getFullEmail());
        const res = await axios.post('/api/auth/getemailcode', params);
        alert(res.data.message || '验证码已发送至您的郑大邮箱');
        this.countdown = 60;
        const timer = setInterval(() => {
          this.countdown--;
          if (this.countdown <= 0) clearInterval(timer);
        }, 1000);
      } catch (err) {
        alert(err.response?.data?.msg || '发送失败，请检查网络');
      }
    },
    async handleAuth() {
      if (!this.authForm.code || !this.authForm.emailPrefix) { alert('请输入完整的邮箱和验证码'); return; }
      try {
        const params = new URLSearchParams();
        params.append('email', this.getFullEmail());
        params.append('emailCode', this.authForm.code);
        const res = await axios.post('/api/auth/emailloginautoregister', params);
        if (res.data.code === 200) {
          localStorage.setItem('token', res.data.data.accessToken);
          localStorage.setItem('userId', res.data.data.userId);
          localStorage.setItem('userName', res.data.data.name || '');
          localStorage.setItem('exp', res.data.data.exp || 0);
          const avatarSeeds = ['Sophia','Sawyer','Avery','Liliana','Aiden','Wyatt','Eliza','Amaya'];
          localStorage.setItem('avatarSeed', avatarSeeds[Math.floor(Math.random() * avatarSeeds.length)]);
          alert('认证成功！');
          this.showAuth = false;
          this.$router.push('/home');
        } else {
          alert(res.data.message || '认证失败');
        }
      } catch (err) {
        alert('认证错误：' + (err.response?.data?.msg || err.message));
      }
    }
  },
}
</script>

<style scoped>
* { box-sizing: border-box; margin: 0; padding: 0; }

.app {
  font-family: "Noto Serif SC", "Source Han Serif CN", "Songti SC", Georgia, "Times New Roman", serif;
  color: #333;
  background: #fff;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

a, button { cursor: pointer; }

/* Header */
.site-header {
  border-bottom: 1px solid #e0e0e0;
  background: #fff;
}
.header-inner {
  max-width: 680px;
  margin: 0 auto;
  padding: 20px 24px;
  display: flex;
  align-items: baseline;
  justify-content: space-between;
}
.site-title {
  font-size: 20px;
  font-weight: 700;
  color: #1a0dab;
  letter-spacing: -0.5px;
}
.site-nav {
  display: flex;
  gap: 20px;
}
.site-nav a {
  font-size: 14px;
  color: #1a0dab;
  text-decoration: none;
  transition: color 0.15s;
}
.site-nav a:hover {
  color: #600;
}

/* Main */
.main { flex: 1; }
.main-inner {
  max-width: 680px;
  margin: 0 auto;
  padding: 0 24px 80px;
}

/* Hero */
.hero {
  padding: 48px 0 36px;
}
.badge {
  display: inline-block;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 2px;
  color: #999;
  font-family: "SF Mono", "Menlo", "Consolas", "PingFang SC", monospace;
  margin-bottom: 16px;
  text-transform: uppercase;
}
.hero-title {
  font-size: 36px;
  font-weight: 700;
  color: #222;
  line-height: 1.3;
  letter-spacing: -0.5px;
  margin-bottom: 16px;
}
.hero-sub {
  font-size: 15px;
  color: #666;
  line-height: 1.85;
  margin-bottom: 28px;
}
.hero-cta {
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
}

/* Buttons */
.submit-btn {
  background: #1a0dab;
  color: #fff;
  border: none;
  padding: 10px 28px;
  font-size: 14px;
  font-family: inherit;
  cursor: pointer;
  transition: background 0.15s;
}
.submit-btn:hover { background: #120880; }
.outline-btn {
  background: none;
  border: 1px solid #1a0dab;
  color: #1a0dab;
  padding: 10px 28px;
  font-size: 14px;
  font-family: inherit;
  cursor: pointer;
  transition: all 0.15s;
}
.outline-btn:hover {
  background: #1a0dab;
  color: #fff;
}

/* Match Preview */
.match-preview {
  padding: 20px 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.match-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  border: 1px solid #e0e0e0;
  transition: border-color 0.15s;
}
.match-card:hover {
  border-color: #1a0dab;
}
.match-avatar {
  width: 40px;
  height: 40px;
  border: 1px solid #e0e0e0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 700;
  color: #1a0dab;
  flex-shrink: 0;
  background: #fafafa;
}
.match-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}
.match-name {
  font-size: 14px;
  font-weight: 600;
  color: #222;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.match-tag {
  font-size: 12px;
  color: #999;
  font-family: "SF Mono", "Menlo", "Consolas", "PingFang SC", monospace;
}
.match-score {
  font-size: 16px;
  font-weight: 700;
  color: #1a0dab;
  font-family: "SF Mono", "Menlo", "Consolas", monospace;
  flex-shrink: 0;
}

/* Divider */
.divider {
  border-top: 1px solid #e0e0e0;
  margin: 32px 0;
}

/* Section Title */
.section-title {
  font-size: 20px;
  font-weight: 700;
  color: #222;
  margin-bottom: 28px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e0e0e0;
}

/* Steps */
.steps {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.step {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}
.step-num {
  width: 36px;
  height: 36px;
  border: 1px solid #1a0dab;
  color: #1a0dab;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 700;
  flex-shrink: 0;
  font-family: "SF Mono", "Menlo", "Consolas", monospace;
}
.step-content { flex: 1; }
.step-name {
  font-size: 15px;
  font-weight: 600;
  color: #222;
  margin-bottom: 4px;
}
.step-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.7;
}
.step-desc :deep(.mono) {
  font-family: "SF Mono", "Menlo", "Consolas", monospace;
}

/* Features */
.feature-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}
.feature-item {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding: 16px;
  border: 1px solid #e0e0e0;
  transition: border-color 0.15s;
}
.feature-item:hover {
  border-color: #1a0dab;
}
.feature-icon {
  font-size: 22px;
  flex-shrink: 0;
  margin-top: 2px;
}
.feature-text { flex: 1; }
.feature-title {
  font-size: 14px;
  font-weight: 600;
  color: #222;
  margin-bottom: 4px;
}
.feature-desc {
  font-size: 13px;
  color: #666;
  line-height: 1.6;
}

/* CTA */
.cta-section {
  text-align: center;
  padding: 24px 0;
}
.cta-title {
  font-size: 22px;
  font-weight: 700;
  color: #222;
  margin-bottom: 8px;
}
.cta-text {
  font-size: 14px;
  color: #999;
  margin-bottom: 24px;
}

/* Footer */
.site-footer {
  border-top: 1px solid #e0e0e0;
  background: #fafafa;
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

/* Modal Overlay */
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  z-index: 999;
}
@media (min-width: 601px) {
  .overlay { align-items: center; }
}

.modal {
  background: #fff;
  padding: 32px 24px;
  width: 100%;
  max-width: 440px;
  position: relative;
  border: 1px solid #e0e0e0;
}
@media (max-width: 600px) {
  .modal { border: none; border-top: 1px solid #e0e0e0; }
}
.modal-close {
  position: absolute;
  top: 12px;
  right: 14px;
  background: none;
  border: none;
  font-size: 16px;
  color: #999;
  cursor: pointer;
  padding: 4px;
}
.modal-close:hover { color: #333; }

.modal-title {
  font-size: 22px;
  font-weight: 700;
  color: #222;
  margin-bottom: 6px;
}
.modal-sub {
  font-size: 13px;
  color: #999;
  margin-bottom: 24px;
}

/* Form */
.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 16px;
}
.form-group label {
  font-size: 13px;
  font-weight: 600;
  color: #666;
}

.email-group {
  border: 1px solid #ddd;
  overflow: hidden;
}
.email-group input {
  width: 100%;
  border: none;
  outline: none;
  padding: 10px 12px;
  font-size: 14px;
  font-family: "SF Mono", "Menlo", "Consolas", "PingFang SC", monospace;
  color: #333;
  background: #fff;
}
.email-domain-row {
  display: flex;
  align-items: center;
  border-top: 1px solid #e0e0e0;
  padding: 0 12px;
  background: #fafafa;
}
.at-symbol {
  color: #1a0dab;
  font-weight: 700;
  margin-right: 6px;
  font-size: 14px;
}
.email-group select {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  color: #333;
  font-weight: 600;
  padding: 10px 0;
  font-size: 13px;
  font-family: "SF Mono", "Menlo", "Consolas", monospace;
  -webkit-appearance: none;
  appearance: none;
  cursor: pointer;
}

@media (min-width: 601px) {
  .email-group {
    display: flex;
    align-items: center;
    padding: 0 12px;
  }
  .email-group input {
    padding: 10px 0;
    flex: 1;
  }
  .email-domain-row {
    border-top: none;
    background: transparent;
    padding: 0;
  }
}

.code-group {
  display: flex;
  gap: 10px;
}
.code-group input {
  flex: 1;
  border: 1px solid #ddd;
  padding: 10px 12px;
  font-size: 14px;
  font-family: "SF Mono", "Menlo", "Consolas", monospace;
  color: #333;
  outline: none;
  background: #fff;
}
.code-group input:focus {
  border-color: #1a0dab;
  box-shadow: 0 0 0 1px #1a0dab;
}
.send-btn {
  min-width: 64px;
  border: 1px solid #1a0dab;
  background: #fff;
  color: #1a0dab;
  padding: 10px 14px;
  font-size: 13px;
  font-weight: 600;
  font-family: "SF Mono", "Menlo", "Consolas", monospace;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.15s;
}
.send-btn:hover:not(:disabled) {
  background: #1a0dab;
  color: #fff;
}
.send-btn:disabled {
  border-color: #ddd;
  color: #bbb;
  cursor: not-allowed;
}

.btn-full {
  width: 100%;
  margin-top: 8px;
  padding: 12px;
  font-size: 15px;
}

.modal-note {
  text-align: center;
  color: #bbb;
  font-size: 12px;
  margin-top: 16px;
}

/* Transition */
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

/* Responsive */
@media (max-width: 600px) {
  .header-inner { padding: 16px; }
  .site-title { font-size: 17px; }
  .site-nav { gap: 12px; }
  .site-nav a { font-size: 13px; }
  .main-inner { padding: 0 16px 60px; }
  .hero { padding: 36px 0 28px; }
  .hero-title { font-size: 28px; }
  .hero-sub { font-size: 14px; }
  .hero-cta { flex-direction: column; }
  .hero-cta .submit-btn,
  .hero-cta .outline-btn { width: 100%; text-align: center; }
  .section-title { font-size: 18px; }
}

@media (max-width: 375px) {
  .hero-title { font-size: 24px; }
  .site-nav { gap: 10px; }
  .site-nav a { font-size: 12px; }
}
</style>
