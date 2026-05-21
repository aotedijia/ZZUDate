<template>
  <div class="app">
    <header class="site-header">
      <div class="header-inner">
        <h1 class="site-title" @click="handleBack">ZZU Date</h1>
        <nav class="site-nav">
          <a @click="handleBack">退出测试</a>
        </nav>
      </div>
    </header>

    <main class="main">
      <div class="main-inner">
        <!-- 进度条 -->
        <div class="progress-section">
          <div class="progress-info">
            <span class="dimension-tag">{{ currentDimension.name }}</span>
            <span class="step-count">{{ currentIndex + 1 }} / {{ questions.length }}</span>
          </div>
          <div class="progress-bar-bg">
            <div class="progress-bar-fill" :style="{ width: progress + '%' }"></div>
          </div>
        </div>

        <!-- 问题卡片 -->
        <transition name="fade-slide" mode="out-in">
          <div :key="currentIndex" class="question-card">
            <h2 class="question-title" v-html="monoNumbers(questions[currentIndex].title)"></h2>

            <div class="options-list">
              <button
                v-for="(text, key) in questions[currentIndex].options"
                :key="key"
                class="option-item"
                :class="{ active: answers[questions[currentIndex].id] === key }"
                @click="handleSelect(key)"
              >
                <span class="opt-label">{{ key }}</span>
                <span class="opt-content" v-html="monoNumbers(text)"></span>
              </button>
            </div>

            <div class="question-footer">
              <button @click="prevQuestion" :disabled="currentIndex === 0" class="nav-btn">
                上一题
              </button>
              <span class="footer-hint">Be sincere</span>
            </div>
          </div>
        </transition>
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

    <div v-if="isSubmitting" class="loading-overlay">
      <div class="elegant-loader"></div>
      <p class="loading-text">正在描绘您的灵魂画像...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import axios from '../utils/request'
import { useRouter } from 'vue-router'

const router = useRouter()
const currentIndex = ref(0)
const answers = ref({})
const isSubmitting = ref(false)

const monoNumbers = (str) => {
  return str.replace(/(\d+\.?\d*)/g, '<span class="mono">$1</span>')
}

const questions = [
  // 消费观 (1-8)
  { id: 1, title: '如果你意外获得 2000 元奖学金，你会？', options: { A: '奖励自己不急需的单品', B: '存入银行作为备用金', C: '规划短途旅行', D: '请客大餐' } },
  { id: 2, title: '在校园恋爱中，你如何看待打车、奶茶等随手的小开支？', options: { A: '细致记账，月底绝对 50/50', B: '模糊平衡，互有请客', C: '条件好的一方多承担', D: '设立共同钱包支取' } },
  { id: 3, title: '购买洗发水、纸巾等生活必需品时，你的决策逻辑是？', options: { A: '品牌忠诚度极高', B: '极致性价比，谁便宜买谁', C: '颜值至上，包装好看优先', D: '随缘购买，不愿耗费脑力' } },
  { id: 4, title: '你认为伴侣送你的理想礼物应该是？', options: { A: '贵重且有品牌保障', B: '纯手工或极具心意', C: '实用主义，正好是我缺的', D: '盲盒或惊喜，瞬间最重要' } },
  { id: 5, title: '当月生活费仅剩 100 元且还有一周时，你会？', options: { A: '顿顿馒头咸菜，绝不借钱', B: '大方向父母或朋友求助', C: '变卖闲置物品回血', D: '依然社交，让朋友先请' } },
  { id: 6, title: '面对社交媒体爆火的"必打卡店"，你的态度是？', options: { A: '积极尝试，保持生活鲜活', B: '偶尔跟风，主要为了拍照', C: '保持冷感，认为是割韭菜', D: '需求且预算充足才考虑' } },
  { id: 7, title: '你如何看待花呗、信用卡或分期付款？', options: { A: '理财手段，分期买是赚了', B: '极度厌恶负债', C: '仅购买生产力工具时接受', D: '随大流，没想过风险' } },
  { id: 8, title: '如果未来月入共 3 万，你认为最大的支出应投向？', options: { A: '孩子的教育和未来保障', B: '提升生活品质', C: '持续投资，早日财务自由', D: '双方的深度爱好' } },
  // 恋爱观 (9-18)
  { id: 9, title: '路边看到形状奇怪的云，你的第一反应是？', options: { A: '拍给对方，不秒回会失落', B: '拍给对方，但不期待回复', C: '自己欣赏或发朋友圈', D: '气氛热烈时顺便分享' } },
  { id: 10, title: '你如何看待伴侣与异性好友单独吃晚饭？', options: { A: '绝对禁止', B: '可以接受但需报备', C: '只要不隐瞒，完全相信', D: '没关系，彼此对等即可' } },
  { id: 11, title: '对方因为忙碌 12 小时未回消息，你会？', options: { A: '反复查看，反思是否说错', B: '感到被冷落而生气', C: '正常做自己的事', D: '故意冷落对方作为博弈' } },
  { id: 12, title: '发生激烈争吵后，你最习惯的应对方式是？', options: { A: '当场说清，拒绝冷战', B: '各自冷静后再理智沟通', C: '习惯性回避，等待消散', D: '寻求第三方调解' } },
  { id: 13, title: '你最能感受到被爱的瞬间是？', options: { A: '对方当众官宣或夸奖', B: '对方做实际的事（送药等）', C: '收到有心意的礼物', D: '深度精神交流的瞬间' } },
  { id: 14, title: '你希望恋爱在校园里的"存在感"是？', options: { A: '高调官宣，并肩而行', B: '适度公开，但不亲昵', C: '低调保护，两人的事', D: '顺其自然，不隐瞒不展示' } },
  { id: 15, title: '你认为"合格的前任"应该？', options: { A: '互不打扰，彻底消失', B: '偶尔点赞的圈友', C: '特定困难时能帮忙', D: '只要现任不介意可联络' } },
  { id: 16, title: '课业压力适中时，你理想的见面频率是？', options: { A: '每天一起吃饭自习', B: '每周 3-4 次，留有空间', C: '仅周末深度约会', D: '随缘，想见就见' } },
  { id: 17, title: '对方在社团受委屈向你抱怨时，你的反应是？', options: { A: '先给情绪价值，一起吐槽', B: '理性分析，给解决方案', C: '转移注意力，带去吃好吃的', D: '静静倾听，不发表意见' } },
  { id: 18, title: '现阶段开启恋情的初衷是？', options: { A: '寻找灵魂伴侣', B: '享受陪伴，调剂生活', C: '共同进步，找战友卷绩点', D: '随缘，刚好遇到合拍的人' } },
  // 生活观 (19-28)
  { id: 19, title: '你最理想的"精力巅峰"在什么时段？', options: { A: '早起鸟：早晨 6-9 点', B: '日间派：跟课表一致', C: '夜猫子：晚上 10 点以后', D: '随缘派：没有固定节律' } },
  { id: 20, title: '你现在的书桌状态最接近？', options: { A: '极简主义，必须归位', B: '乱中有序，能精准找到', C: '自由生长，实在没空才理', D: '依赖他人提醒收拾' } },
  { id: 21, title: '考试周后的首选充电方式是？', options: { A: '物理断联，独自看剧', B: '热闹聚会，火锅蹦迪', C: '深度陪伴，只和最亲近的人', D: '切换赛道，运动周边游' } },
  { id: 22, title: '关于"今天吃什么"，你的模式是？', options: { A: '目标明确，固定好店', B: '随大流，看点评高分', C: '探索者，热爱尝试新店', D: '深度纠结，随便都行' } },
  { id: 23, title: '面对面相处（如吃饭）时看手机的态度？', options: { A: '绝对专注，手机静音', B: '分享导向，好玩的随时看', C: '并行操作，各刷各的', D: '习惯使然，频繁解锁' } },
  { id: 24, title: '布置私人空间，你最愿意在什么上面花钱？', options: { A: '实用工具', B: '氛围好物', C: '整理收纳件', D: '懒人神器' } },
  { id: 25, title: '理想中和伴侣一起"卷"的方式？', options: { A: '连体婴式，互相监督', B: '隔空连线，结束后汇报', C: '同桌但各做各的', D: '拒绝同行，不想展示狼狈' } },
  { id: 26, title: '下午 2 点见，你通常的表现是？', options: { A: '准时提前', B: '刚好踩点出现', C: '由于纠结，延后 15 分', D: '随性派，时间只是参考' } },
  { id: 27, title: '你如何描述自己的爱好？', options: { A: '深度发烧友，极度痴迷', B: '杂食动物，保持新鲜感', C: '极简生活，无特别爱好', D: '养成系，愿随伴侣学习' } },
  { id: 28, title: '处理寝室与恋爱的关系，你倾向于？', options: { A: '重友轻色', B: '见色忘友', C: '利益平衡，常感焦虑', D: '独立隔离，恋爱是隐私' } },
  // 性格与忠诚 (29-40)
  { id: 29, title: '遇到各方面远优于现任且对你有好感的人，你会？', options: { A: '克制本能，保持距离', B: '顺其自然接触，不越底线', C: '陷入对比，考虑换人', D: '隐藏伴侣事实，享受成就感' } },
  { id: 30, title: '你如何看待亲密关系中的"善意谎言"？', options: { A: '绝对透明', B: '适度保留，不让对方担心', C: '战术性谎言，作为润滑剂', D: '隐私保护，无需交代' } },
  { id: 31, title: '共同决定失败时，你的反应是？', options: { A: '共同承担，先安抚情绪', B: '理性归因，分析责任', C: '负罪感沉重，全揽在身上', D: '情绪宣泄，下意识责怪' } },
  { id: 32, title: '哪种行为属于"不可原谅"的背叛？', options: { A: '身体出轨', B: '精神出轨', C: '欺骗与隐瞒', D: '以上皆是' } },
  { id: 33, title: '连续遇到糟糕事时，你会？', options: { A: '寻找树洞，向伴侣倾诉', B: '独自消化，不想示弱', C: '情绪波动，对伴侣发火', D: '寻求补偿，要加倍关爱' } },
  { id: 34, title: '若升学机会需长期异地或分手，你会？', options: { A: '前途优先，感情随缘', B: '共同协商双赢方案', C: '情感优先，愿放弃计划', D: '走一步看一步' } },
  { id: 35, title: '当伴侣指出你性格中的不适点时，你通常？', options: { A: '虚心接受，给出计划', B: '习惯反驳，寻找对方缺点', C: '表面答应，实际不改', D: '感到自卑，冷暴力' } },
  { id: 36, title: '你希望在关系中扮演什么样的角色？', options: { A: '守护引导者', B: '追随被照顾者', C: '绝对平等', D: '独立并线各干各的' } },
  { id: 37, title: '若伴侣家突遭变故，你的态度是？', options: { A: '全力以赴支持度难关', B: '精神支持，量力而行', C: '现实评估，是否被拖累', D: '被动等待，顺势离开' } },
  { id: 38, title: '你对伴侣在他人眼中的评价在意程度？', options: { A: '极其在意', B: '适度在意', C: '完全不在意', D: '虚荣心导向' } },
  { id: 39, title: '你认为"永远在一起"的本质是？', options: { A: '一种信仰，拼尽全力', B: '浪漫修辞，听听就好', C: '阶段性契约', D: '沉重压力，不敢轻诺' } },
  { id: 40, title: '如果只能选一个特质定义伴侣，你选？', options: { A: '忠诚', B: '有趣', C: '强大', D: '善良' } }
]

const currentDimension = computed(() => {
  if (currentIndex.value < 8) return { name: '价值取向' }
  if (currentIndex.value < 18) return { name: '情感共鸣' }
  if (currentIndex.value < 28) return { name: '生活节律' }
  return { name: '真诚本义' }
})

const progress = computed(() => ((currentIndex.value + 1) / questions.length) * 100)

const handleBack = () => {
  if (confirm('确认退出测试吗？当前的进度将不会被保存。')) {
    router.push('/home')
  }
}

const handleSelect = (val) => {
  answers.value[questions[currentIndex.value].id] = val
  setTimeout(() => {
    if (currentIndex.value < questions.length - 1) {
      currentIndex.value++
    } else {
      submitData()
    }
  }, 400)
}

const prevQuestion = () => {
  if (currentIndex.value > 0) currentIndex.value--
}

const submitData = async () => {
  isSubmitting.value = true
  const userId = localStorage.getItem('userId');

  try {
    const payload = {
      userId: userId,
      answers: JSON.stringify(answers.value)
    };
    const res = await axios.post('/api/match/saveuserinfo', payload);
    if (res.data.code === 200) {
      alert('同步成功');
      localStorage.removeItem('base_info');
      router.push('/home');
    } else {
      alert(res.data.message);
    }
  } catch (err) {
    alert('网络异常');
  } finally {
    isSubmitting.value = false
  }
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

/* Progress */
.progress-section {
  margin-bottom: 40px;
  padding-bottom: 24px;
  border-bottom: 1px solid #e0e0e0;
}
.progress-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 1px;
}
.dimension-tag {
  color: #1a0dab;
}
.step-count {
  color: #999;
  font-family: 'SF Mono', Menlo, Consolas, monospace;
}
.progress-bar-bg {
  height: 2px;
  background: #e0e0e0;
  overflow: hidden;
}
.progress-bar-fill {
  height: 100%;
  background: #1a0dab;
  transition: width 0.6s cubic-bezier(0.19, 1, 0.22, 1);
}

/* Question */
.question-card {
  padding-top: 8px;
}
.question-title {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a1a;
  line-height: 1.6;
  margin-bottom: 32px;
}
.question-title :deep(.mono),
.opt-content :deep(.mono) {
  font-family: "SF Mono", "Menlo", "Consolas", monospace;
}

/* Options */
.options-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.option-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border: 1px solid #e0e0e0;
  background: #fff;
  cursor: pointer;
  transition: all 0.2s;
  text-align: left;
  font-family: inherit;
}
.option-item:hover {
  background: #fafafa;
  border-color: #1a0dab;
}
.option-item.active {
  background: #1a0dab;
  border-color: #1a0dab;
}
.option-item.active .opt-label,
.option-item.active .opt-content {
  color: #fff;
}

.opt-label {
  font-size: 11px;
  font-weight: 700;
  color: #999;
  margin-right: 16px;
  min-width: 16px;
}
.opt-content {
  font-size: 14px;
  color: #444;
  line-height: 1.6;
}

/* Question Footer */
.question-footer {
  margin-top: 32px;
  padding-top: 16px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.nav-btn {
  background: none;
  border: none;
  font-size: 13px;
  font-weight: 600;
  color: #1a0dab;
  cursor: pointer;
  font-family: inherit;
}
.nav-btn:disabled {
  color: #ccc;
  cursor: default;
}
.footer-hint {
  font-size: 12px;
  font-style: italic;
  color: #ccc;
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
.footer-inner :deep(.mono) {
  font-family: 'SF Mono', Menlo, Consolas, monospace;
}

/* Loading Overlay */
.loading-overlay {
  position: fixed;
  inset: 0;
  background: rgba(255, 255, 255, 0.92);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 100;
}
.elegant-loader {
  width: 24px;
  height: 24px;
  border: 2px solid #e0e0e0;
  border-top-color: #1a0dab;
  border-radius: 50%;
  animation: spin 1s infinite linear;
  margin-bottom: 16px;
}
@keyframes spin { to { transform: rotate(360deg); } }
.loading-text {
  font-size: 14px;
  color: #666;
}

/* Transition */
.fade-slide-enter-active, .fade-slide-leave-active { transition: all 0.3s ease; }
.fade-slide-enter-from { opacity: 0; transform: translateY(10px); }
.fade-slide-leave-to { opacity: 0; transform: translateY(-10px); }

/* Responsive */
@media (max-width: 600px) {
  .header-inner { padding: 16px; }
  .site-title { font-size: 17px; }
  .site-nav { gap: 12px; }
  .site-nav a { font-size: 13px; }
  .main-inner { padding: 24px 16px 60px; }
  .question-title { font-size: 18px; }
  .option-item { padding: 14px 16px; }
  .opt-content { font-size: 13px; }
}

@media (max-width: 375px) {
  .site-nav { gap: 10px; }
  .site-nav a { font-size: 12px; }
  .question-title { font-size: 16px; }
  .option-item { padding: 12px 14px; }
  .opt-content { font-size: 12px; }
}
</style>
