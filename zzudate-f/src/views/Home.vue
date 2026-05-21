<template>
  <div class="home-wrapper">
    <header class="top-nav">
      <!-- 移动端：汉堡菜单按钮 -->
      <button class="hamburger" @click="toggleMobileMenu" :class="{ active: showMobileMenu }">
        <span></span>
        <span></span>
        <span></span>
      </button>

      <!-- 桌面端导航 -->
      <div class="nav-links desktop-only">
        <button class="uiverse-btn" @click="$router.push('/info')">基本信息</button>
        <button class="uiverse-btn" @click="$router.push('/result')">匹配结果</button>
        <button class="uiverse-btn" @click="$router.push('/question')">深度评测</button>
        <button class="uiverse-btn" @click="$router.push('/community')">校园社区</button>
        <button class="uiverse-btn" @click="handleLogout">退出登录</button>
      </div>

      <div class="avatar-box">
        <img :src="`https://api.dicebear.com/9.x/lorelei-neutral/svg?seed=${userId}`" alt="Avatar" />
      </div>
    </header>

    <!-- 移动端全屏菜单 -->
    <transition name="slide-down">
      <div v-if="showMobileMenu" class="mobile-menu" @click.self="showMobileMenu = false">
        <nav class="mobile-nav">
          <button class="mobile-nav-item" @click="navigate('/info')">基本信息</button>
          <button class="mobile-nav-item" @click="navigate('/result')">匹配结果</button>
          <button class="mobile-nav-item" @click="navigate('/question')">深度评测</button>
          <button class="mobile-nav-item" @click="navigate('/community')">校园社区</button>
          <button class="mobile-nav-item logout" @click="handleLogout">退出登录</button>
        </nav>
      </div>
    </transition>

    <main class="main-stage">
      <template v-if="!isResultTime">
        <div class="loader">
          <svg height="0" width="0" viewBox="0 0 64 64" class="absolute">
            <defs>
              <linearGradient gradientUnits="userSpaceOnUse" y2="2" x2="0" y1="62" x1="0" id="b">
                <stop stop-color="#973BED"></stop>
                <stop stop-color="#007CFF" offset="1"></stop>
              </linearGradient>
              <linearGradient gradientUnits="userSpaceOnUse" y2="0" x2="0" y1="64" x1="0" id="c">
                <stop stop-color="#FFC800"></stop>
                <stop stop-color="#F0F" offset="1"></stop>
                <animateTransform
                  repeatCount="indefinite"
                  keySplines=".42,0,.58,1;.42,0,.58,1;.42,0,.58,1;.42,0,.58,1;.42,0,.58,1;.42,0,.58,1;.42,0,.58,1;.42,0,.58,1"
                  keyTimes="0; 0.125; 0.25; 0.375; 0.5; 0.625; 0.75; 0.875; 1"
                  dur="8s"
                  values="0 32 32;-270 32 32;-270 32 32;-540 32 32;-540 32 32;-810 32 32;-810 32 32;-1080 32 32;-1080 32 32"
                  type="rotate"
                  attributeName="gradientTransform">
                </animateTransform>
              </linearGradient>
              <linearGradient gradientUnits="userSpaceOnUse" y2="2" x2="0" y1="62" x1="0" id="d">
                <stop stop-color="#00E0ED"></stop>
                <stop stop-color="#00DA72" offset="1"></stop>
              </linearGradient>
            </defs>
          </svg>
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 64 64" height="64" width="64" class="inline-block">
            <path stroke-linejoin="round" stroke-linecap="round" stroke-width="8" stroke="url(#b)" d="M 54.722656,3.9726563 A 2.0002,2.0002 0 0 0 54.941406,4 h 5.007813 C 58.955121,17.046124 49.099667,27.677057 36.121094,29.580078 a 2.0002,2.0002 0 0 0 -1.708985,1.978516 V 60 H 29.587891 V 31.558594 A 2.0002,2.0002 0 0 0 27.878906,29.580078 C 14.900333,27.677057 5.0448787,17.046124 4.0507812,4 H 9.28125 c 1.231666,11.63657 10.984383,20.554048 22.6875,20.734375 a 2.0002,2.0002 0 0 0 0.02344,0 c 11.806958,0.04283 21.70649,-9.003371 22.730469,-20.7617187 z" class="dash" id="y" pathLength="360"></path>
          </svg>
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 64 64" height="64" width="64" class="inline-block">
            <path stroke-linejoin="round" stroke-linecap="round" stroke-width="10" stroke="url(#c)" d="M 32 32 m 0 -27 a 27 27 0 1 1 0 54 a 27 27 0 1 1 0 -54" class="spin" id="o" pathLength="360"></path>
          </svg>
          <div class="w-2"></div>
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 64 64" height="64" width="64" class="inline-block">
            <path stroke-linejoin="round" stroke-linecap="round" stroke-width="8" stroke="url(#d)" d="M 4,4 h 4.6230469 v 25.919922 c -0.00276,11.916203 9.8364941,21.550422 21.7500001,21.296875 11.616666,-0.240651 21.014356,-9.63894 21.253906,-21.25586 a 2.0002,2.0002 0 0 0 0,-0.04102 V 4 H 56.25 v 25.919922 c 0,14.33873 -11.581192,25.919922 -25.919922,25.919922 a 2.0002,2.0002 0 0 0 -0.0293,0 C 15.812309,56.052941 3.998433,44.409961 4,29.919922 Z" class="dash" id="u" pathLength="360"></path>
          </svg>
        </div>

        <div class="timer-section">
          <p class="label">距离下次匹配开启还有</p>
          <h1 class="time-num">{{ countdownText }}</h1>
        </div>
      </template>

      <template v-else>
        <div class="result-display-section">
          <button class="match-result-btn" @click="$router.push('/result')">
            查看本次匹配结果
          </button>
          <p class="result-hint">匹配已开启，快去看看你的匹配结果吧</p>
        </div>
      </template>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from '../utils/request';

const router = useRouter();
const avatarSeed = localStorage.getItem('avatarSeed') || 'Sophia';
const countdownText = ref('00天 : 00 : 00 : 00');
const isResultTime = ref(false);
const showMobileMenu = ref(false);
let timer = null;

const toggleMobileMenu = () => { showMobileMenu.value = !showMobileMenu.value; };

const navigate = (path) => {
  showMobileMenu.value = false;
  router.push(path);
};

const handleLogout = async () => {
  try {
    await axios.post('/api/auth/logout', {});
  } catch (err) {
    console.error("Logout Error:", err);
  } finally {
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem('userName');
    localStorage.removeItem('exp');
    localStorage.removeItem('base_info');
    localStorage.removeItem('lastSyncDate');
    localStorage.removeItem('avatarSeed');
    router.push('/welcome');
  }
};

const updateStatus = () => {
  const now = new Date();
  const day = now.getDay();
  const hour = now.getHours();

  const isAfterWed7PM = (day === 3 && hour >= 19);
  const isBeforeThu7PM = (day === 4 && hour < 19);

  if (isAfterWed7PM || isBeforeThu7PM) {
    isResultTime.value = true;
  } else {
    isResultTime.value = false;

    const targetDay = 3;
    const targetHour = 19;
    let nextMatch = new Date(now);
    let daysUntilTarget = (targetDay - day + 7) % 7;

    if (daysUntilTarget === 0 && hour >= targetHour) {
      daysUntilTarget = 7;
    }

    nextMatch.setDate(now.getDate() + daysUntilTarget);
    nextMatch.setHours(targetHour, 0, 0, 0);

    const diff = nextMatch - now;

    const d = Math.floor(diff / (1000 * 60 * 60 * 24));
    const h = Math.floor((diff / (1000 * 60 * 60)) % 24);
    const m = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    const s = Math.floor((diff % (1000 * 60)) / 1000);

    const dStr = d.toString().padStart(2, '0');
    const hStr = h.toString().padStart(2, '0');
    const mStr = m.toString().padStart(2, '0');
    const sStr = s.toString().padStart(2, '0');

    countdownText.value = `${dStr}天 : ${hStr} : ${mStr} : ${sStr}`;
  }
};

onMounted(() => {
  updateStatus();
  timer = setInterval(updateStatus, 1000);
});

onUnmounted(() => {
  if (timer) clearInterval(timer);
});
</script>

<style scoped>
/* ========== 基础布局 ========== */
.home-wrapper {
  position: fixed;
  top: 0; left: 0;
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background: #050507;
}

/* ========== 顶部导航 ========== */
.top-nav {
  position: absolute;
  top: 40px;
  right: 40px;
  display: flex;
  align-items: center;
  gap: 30px;
  z-index: 200;
}

.nav-links {
  display: flex;
  gap: 25px;
}

/* ========== 桌面导航按钮 ========== */
.uiverse-btn {
  font-size: 16px;
  color: #e1e1e1;
  font-family: inherit;
  font-weight: 800;
  cursor: pointer;
  position: relative;
  border: none;
  background: none;
  text-transform: uppercase;
  transition: color 400ms cubic-bezier(0.25, 0.8, 0.25, 1);
  padding: 8px 0;
}

.uiverse-btn:hover { color: #fff; }

.uiverse-btn:after {
  content: "";
  position: absolute;
  bottom: -2px;
  left: 50%;
  width: 0%;
  height: 2px;
  background-color: #007CFF;
  transition: all 400ms cubic-bezier(0.25, 0.8, 0.25, 1);
}

.uiverse-btn:hover:after {
  width: 100%;
  left: 0%;
}

/* ========== 头像 ========== */
.avatar-box {
  position: relative;
}

.avatar-box img {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  border: 2px solid #007CFF;
  object-fit: cover;
}


/* ========== 汉堡菜单按钮（仅移动端） ========== */
.hamburger {
  display: none;
  flex-direction: column;
  justify-content: space-between;
  width: 28px;
  height: 20px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  z-index: 300;
}

.hamburger span {
  display: block;
  width: 100%;
  height: 2px;
  background: #e1e1e1;
  border-radius: 2px;
  transition: all 0.35s cubic-bezier(0.25, 0.8, 0.25, 1);
  transform-origin: center;
}

/* 汉堡变 X 动画 */
.hamburger.active span:nth-child(1) {
  transform: translateY(9px) rotate(45deg);
}
.hamburger.active span:nth-child(2) {
  opacity: 0;
  transform: scaleX(0);
}
.hamburger.active span:nth-child(3) {
  transform: translateY(-9px) rotate(-45deg);
}

/* ========== 移动端全屏菜单 ========== */
.mobile-menu {
  position: fixed;
  top: 0; left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(5, 5, 7, 0.97);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  z-index: 150;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mobile-nav {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  width: 100%;
  padding: 0 40px;
}

.mobile-nav-item {
  width: 100%;
  max-width: 320px;
  padding: 18px 0;
  font-size: 18px;
  font-family: inherit;
  font-weight: 700;
  letter-spacing: 4px;
  text-transform: uppercase;
  color: #c0c0c0;
  background: none;
  border: none;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  cursor: pointer;
  text-align: center;
  transition: color 0.3s ease, letter-spacing 0.3s ease;
}

.mobile-nav-item:first-child {
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.mobile-nav-item:hover,
.mobile-nav-item:active {
  color: #fff;
  letter-spacing: 6px;
}

.mobile-nav-item.logout {
  color: #555;
  margin-top: 20px;
  border: none;
  font-size: 14px;
  letter-spacing: 3px;
}

.mobile-nav-item.logout:hover {
  color: #888;
  letter-spacing: 4px;
}

/* 滑入动画 */
.slide-down-enter-active {
  animation: slideDownIn 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
}
.slide-down-leave-active {
  animation: slideDownIn 0.3s cubic-bezier(0.25, 0.8, 0.25, 1) reverse;
}

@keyframes slideDownIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ========== Fade 过渡 ========== */
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

/* ========== 主内容区 ========== */
.main-stage {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 10;
  width: 100%;
  padding: 0 20px;
  box-sizing: border-box;
}

/* ========== SVG 动画 ========== */
.loader {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.absolute { position: absolute; }
.inline-block { display: inline-block; }
.w-2 { width: 10px; }

.dash {
  animation: dashArray 2s ease-in-out infinite, dashOffset 2s linear infinite;
}

.spin {
  animation: spinDashArray 2s ease-in-out infinite, spin 8s ease-in-out infinite, dashOffset 2s linear infinite;
  transform-origin: center;
}

@keyframes dashArray {
  0% { stroke-dasharray: 0 1 359 0; }
  50% { stroke-dasharray: 0 359 1 0; }
  100% { stroke-dasharray: 359 1 0 0; }
}

@keyframes spinDashArray {
  0% { stroke-dasharray: 270 90; }
  50% { stroke-dasharray: 0 360; }
  100% { stroke-dasharray: 270 90; }
}

@keyframes dashOffset {
  0% { stroke-dashoffset: 365; }
  100% { stroke-dashoffset: 5; }
}

@keyframes spin {
  0% { rotate: 0deg; }
  12.5%, 25% { rotate: 270deg; }
  37.5%, 50% { rotate: 540deg; }
  62.5%, 75% { rotate: 810deg; }
  87.5%, 100% { rotate: 1080deg; }
}

/* ========== 倒计时 ========== */
.timer-section {
  text-align: center;
  margin-top: 50px;
}

.label {
  color: #666;
  letter-spacing: 6px;
  font-size: 14px;
  margin-bottom: 15px;
}

.time-num {
  font-size: 3.5rem;
  color: #fff;
  font-weight: 200;
  font-family: "SF Mono", "Menlo", "Consolas", monospace;
  font-variant-numeric: tabular-nums;
  text-shadow: 0 0 25px rgba(0, 124, 255, 0.4);
}

/* ========== 匹配结果按钮 ========== */
.match-result-btn {
  padding: 22px 50px;
  font-size: 1.6rem;
  font-weight: 800;
  color: #fff;
  background: linear-gradient(45deg, #007CFF, #973BED);
  border: none;
  border-radius: 60px;
  cursor: pointer;
  box-shadow: 0 0 40px rgba(0, 124, 255, 0.5);
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.match-result-btn:hover {
  transform: scale(1.05) translateY(-5px);
  box-shadow: 0 15px 50px rgba(151, 59, 237, 0.6);
}

.result-display-section { text-align: center; }

.result-hint {
  margin-top: 25px;
  color: #888;
  letter-spacing: 3px;
  font-size: 14px;
}

/* ========== 辅助类 ========== */
.desktop-only {
  display: flex;
}

/* ========================================
   移动端响应式适配 (≤ 768px)
   ======================================== */
@media (max-width: 768px) {
  /* 导航栏调整 */
  .top-nav {
    top: 20px;
    right: 20px;
    gap: 16px;
  }

  /* 显示汉堡，隐藏桌面导航 */
  .hamburger {
    display: flex;
  }

  .desktop-only {
    display: none;
  }

  /* 头像缩小 */
  .avatar-box img {
    width: 40px;
    height: 40px;
  }

  /* SVG 动画缩小 */
  .loader svg:not(.absolute) {
    width: 48px;
    height: 48px;
  }

  .w-2 {
    width: 6px;
  }

  /* 倒计时字体缩小 */
  .timer-section {
    margin-top: 36px;
  }

  .label {
    font-size: 12px;
    letter-spacing: 3px;
  }

  .time-num {
    font-size: 2rem;
  }

  /* 结果按钮适配 */
  .match-result-btn {
    padding: 18px 32px;
    font-size: 1.2rem;
    border-radius: 50px;
    width: 100%;
    max-width: 320px;
  }

  .result-hint {
    font-size: 12px;
    letter-spacing: 2px;
    margin-top: 20px;
  }
}

/* 超小屏（≤ 375px） */
@media (max-width: 375px) {
  .time-num {
    font-size: 1.6rem;
  }

  .label {
    font-size: 11px;
    letter-spacing: 2px;
  }

  .match-result-btn {
    font-size: 1rem;
    padding: 16px 24px;
  }
}

/* 横屏模式 */
@media (max-width: 768px) and (orientation: landscape) {
  .loader {
    margin-bottom: 10px;
  }

  .loader svg:not(.absolute) {
    width: 36px;
    height: 36px;
  }

  .timer-section {
    margin-top: 16px;
  }

  .time-num {
    font-size: 1.8rem;
  }

  .label {
    margin-bottom: 8px;
  }
}
</style>