<template>
  <div class="app">
    <header class="site-header">
      <div class="header-inner">
        <h1 class="site-title" @click="$router.push('/home')">ZZU Date</h1>
        <nav class="site-nav">
          <a @click="$router.push('/home')">返回首页</a>
        </nav>
      </div>
    </header>

    <main class="main">
      <div class="main-inner">
        <div class="page-header">
          <h2 class="page-title">匹配结果</h2>
        </div>

        <!-- 加载 -->
        <div v-if="loading" class="loading">
          <div class="minimal-spinner"></div>
          <p class="loading-text">正在获取匹配结果...</p>
        </div>

        <!-- 有匹配 -->
        <div v-else-if="matchInfo && matchInfo.matchId" class="match-render">
          <div class="avatar-section">
            <div class="avatar-ring">
              <img :src="`https://api.dicebear.com/9.x/lorelei-neutral/svg?seed=${matchInfo.matchUserName}`" alt="Avatar" class="dice-avatar" />
            </div>
          </div>

          <div class="score-section">
            <div class="score-line">
              <span class="score-val">{{ matchInfo.score }}</span>
              <span class="score-pct">%</span>
            </div>
            <p class="label-tiny">MATCHING SCORE</p>
          </div>

          <div class="info-section">
            <h2 class="name-heading">{{ matchInfo.matchUserName }}</h2>
            <p class="narrative-text">{{ matchInfo.description }}</p>
          </div>

          <div class="divider"></div>

          <div class="action-section">
            <template v-if="matchInfo.iHaveNumber && matchInfo.iHaveNumber2">
              <div class="reveal-box success">
                <span class="label-tiny">互相坦白了联系方式</span>
                <div class="number-display active">{{ matchInfo.number }}</div>
                <p class="hint-text">缘分已至，开启你们的对话</p>
              </div>
            </template>

            <template v-else-if="matchInfo.iHaveNumber">
              <div class="reveal-box waiting">
                <span class="label-tiny">您已坦白联系方式</span>
                <div class="number-display muted">{{ matchInfo.number }}</div>
                <div class="waiting-indicator">
                  <div class="dot-pulse"></div>
                  <p class="waiting-text">等待对方坦白...</p>
                </div>
              </div>
            </template>

            <template v-else>
              <div class="lock-state">
                <p class="lock-title">真诚是交流的前提</p>
                <p class="lock-hint">坦白您的联系方式后方可查看对方</p>
                <button class="submit-btn" @click="handleConfess">选择坦白</button>
              </div>
            </template>
          </div>
        </div>

        <!-- 无匹配 -->
        <div v-else class="empty-state">
          <div class="logo-mark">Z</div>
          <h2 class="empty-title">暂无共鸣</h2>
          <p class="empty-text">{{ emptyMsg || '本周暂未匹配到契合者，请静待下周' }}</p>
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
import { ref, onMounted } from 'vue';
import axios from '../utils/request';
import { useRouter } from 'vue-router';

const router = useRouter();
const loading = ref(true);
const matchInfo = ref(null);
const emptyMsg = ref('');

const fetchMatchData = async () => {
  loading.value = true;
  try {
    const userId = localStorage.getItem('userId');
    if (!userId) {
      emptyMsg.value = '请先登录';
      loading.value = false;
      return;
    }
    const params = new URLSearchParams();
    params.append('userId', userId);
    const res = await axios.post('/api/match/getmatchresult', params);

    if (res.data.code === 200) {
      const data = res.data.data;
      if (typeof data === 'string') {
        emptyMsg.value = data;
      } else {
        matchInfo.value = data;
      }
    }
  } catch (err) {
    console.error("Fetch Error:", err);
    emptyMsg.value = "无法连接至服务器";
  } finally {
    loading.value = false;
  }
};

const handleConfess = async () => {
  const userId = localStorage.getItem('userId');
  if (!userId) {
    alert('请先登录');
    return;
  }
  try {
    const params = new URLSearchParams();
    params.append('userId', userId);
    const res = await axios.post('/api/match/shownumber', params);
    if (res.data.code === 200) {
      fetchMatchData();
    } else {
      alert(res.data.message);
    }
  } catch (err) {
    console.error("Confess Action Error:", err);
  }
};

onMounted(fetchMatchData);
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

a { cursor: pointer; }

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
  cursor: pointer;
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
  padding: 40px 24px 80px;
}

/* Page Header */
.page-header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 32px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e0e0e0;
}
.page-title {
  font-size: 22px;
  font-weight: 700;
  color: #222;
}

/* Loading */
.loading {
  text-align: center;
  padding: 60px 0;
}
.minimal-spinner {
  width: 24px; height: 24px;
  border: 2px solid #e0e0e0;
  border-top-color: #1a0dab;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}
@keyframes spin { to { transform: rotate(360deg); } }
.loading-text {
  font-size: 13px;
  color: #999;
  font-family: "SF Mono", "Menlo", "Consolas", "PingFang SC", monospace;
}

/* Match Render */
.match-render {
  text-align: center;
}

.avatar-section {
  margin-bottom: 28px;
}
.avatar-ring {
  width: 80px; height: 80px;
  margin: 0 auto;
  border: 1px solid #e0e0e0;
  padding: 4px;
  border-radius: 50%;
  background: #fafafa;
}
.dice-avatar {
  width: 100%; height: 100%;
  border-radius: 50%;
  background: #fff;
}

/* Score */
.score-section {
  padding: 24px 0;
  border-bottom: 1px solid #e0e0e0;
  margin-bottom: 24px;
}
.score-line {
  display: inline-flex;
  align-items: baseline;
}
.score-val {
  font-size: 56px;
  font-weight: 300;
  line-height: 1;
  font-family: "SF Mono", "Menlo", "Consolas", monospace;
  color: #222;
}
.score-pct {
  font-size: 20px;
  color: #999;
  margin-left: 2px;
  font-family: "SF Mono", "Menlo", "Consolas", monospace;
}
.label-tiny {
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 3px;
  color: #999;
  font-family: "SF Mono", "Menlo", "Consolas", "PingFang SC", monospace;
}

/* Info */
.info-section {
  text-align: left;
  margin-bottom: 24px;
}
.name-heading {
  font-size: 20px;
  font-weight: 700;
  color: #222;
  margin-bottom: 12px;
}
.narrative-text {
  font-size: 15px;
  color: #555;
  line-height: 1.8;
}

.divider {
  border-top: 1px solid #e0e0e0;
  margin: 8px 0 28px;
}

/* Action */
.action-section {
  text-align: center;
}
.number-display {
  font-size: 22px;
  letter-spacing: 3px;
  margin: 16px 0;
  font-family: "SF Mono", "Menlo", "Consolas", monospace;
}
.number-display.active {
  color: #1a0dab;
  font-weight: 600;
}
.number-display.muted {
  color: #bbb;
  font-style: italic;
}

.hint-text {
  font-size: 13px;
  color: #999;
  margin-top: 4px;
}
.lock-hint {
  font-size: 13px;
  color: #bbb;
  margin-top: 4px;
  margin-bottom: 20px;
}
.lock-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
}

/* Waiting animation */
.dot-pulse {
  width: 6px; height: 6px;
  background: #bbb;
  border-radius: 50%;
  margin: 16px auto 8px;
  animation: pulse 1.5s infinite ease-in-out;
}
@keyframes pulse { 0%, 100% { transform: scale(1); opacity: 0.5; } 50% { transform: scale(1.8); opacity: 1; } }
.waiting-text {
  font-size: 13px;
  color: #999;
  animation: flicker 2s infinite;
}
@keyframes flicker { 0%, 100% { opacity: 1; } 50% { opacity: 0.5; } }

/* Submit btn */
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

/* Empty */
.empty-state {
  text-align: center;
  padding: 60px 0;
}
.logo-mark {
  width: 40px; height: 40px;
  border: 1px solid #e0e0e0;
  margin: 0 auto 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
  color: #1a0dab;
}
.empty-title {
  font-size: 20px;
  font-weight: 700;
  color: #222;
  margin-bottom: 8px;
}
.empty-text {
  font-size: 14px;
  color: #999;
  line-height: 1.6;
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

/* Responsive */
@media (max-width: 600px) {
  .header-inner { padding: 16px; }
  .site-title { font-size: 17px; }
  .site-nav { gap: 12px; }
  .site-nav a { font-size: 13px; }
  .main-inner { padding: 24px 16px 60px; }
  .page-title { font-size: 19px; }
  .score-val { font-size: 44px; }
  .name-heading { font-size: 18px; }
  .narrative-text { font-size: 14px; }
  .number-display { font-size: 18px; }
}

@media (max-width: 375px) {
  .site-nav { gap: 10px; }
  .site-nav a { font-size: 12px; }
  .page-title { font-size: 17px; }
  .score-val { font-size: 38px; }
}
</style>
