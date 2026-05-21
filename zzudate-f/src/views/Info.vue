<template>
  <div class="app">
    <header class="site-header">
      <div class="header-inner">
        <h1 class="site-title" @click="router.push('/home')">ZZU Date</h1>
        <nav class="site-nav">
          <a @click="router.push('/home')">返回首页</a>
        </nav>
      </div>
    </header>

    <main class="main">
      <div class="main-inner">
        <div class="page-header">
          <h2 class="page-title">基本信息</h2>
        </div>

        <form class="elegant-form">
          <div class="form-group">
            <label>昵称或姓名</label>
            <input v-model="form.name" type="text" class="form-input mono-input" placeholder="请输入昵称或姓名" />
          </div>

          <div class="form-group">
            <label>年龄</label>
            <input v-model="form.age" type="number" class="form-input mono-input" placeholder="请输入年龄" min="18" max="50" />
          </div>

          <div class="form-group">
            <label>性别标识</label>
            <div class="cat-select">
              <a :class="{ active: form.gender === 1 }" @click="form.gender = 1">男</a>
              <a :class="{ active: form.gender === 0 }" @click="form.gender = 0">女</a>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>身高 / CM</label>
              <input v-model="form.height" type="number" placeholder="170" class="form-input mono-input" />
            </div>
            <div class="form-group">
              <label>联系方式</label>
              <input v-model="form.number" type="text" placeholder="匹配成功后选择性可见" class="form-input mono-input" />
            </div>
          </div>

          <div class="form-group">
            <label>学术归属 / 学院</label>
            <select v-model="form.college" class="form-input">
              <option value="" disabled selected>选择你的学院</option>
              <option v-for="dept in colleges" :key="dept" :value="dept">{{ dept }}</option>
            </select>
          </div>

          <div class="form-group">
            <label>倾向匹配</label>
            <div class="cat-select">
              <a :class="{ active: form.choose === 1 }" @click="form.choose = 1">偏好男</a>
              <a :class="{ active: form.choose === 0 }" @click="form.choose = 0">偏好女</a>
            </div>
          </div>

          <div class="form-group">
            <label>交友意向</label>
            <div class="cat-select">
              <a :class="{ active: form.friendChoose === '寻找长期恋爱伴侣' }" @click="form.friendChoose = '寻找长期恋爱伴侣'">寻找长期恋爱伴侣</a>
              <a :class="{ active: form.friendChoose === '寻找志同道合的朋友' }" @click="form.friendChoose = '寻找志同道合的朋友'">寻找志同道合的朋友</a>
              <a :class="{ active: form.friendChoose === '顺其自然拓宽社交' }" @click="form.friendChoose = '顺其自然拓宽社交'">顺其自然拓宽社交</a>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>常驻校区</label>
              <select v-model="form.campus" class="form-input">
                <option value="" disabled selected>选择校区</option>
                <option v-for="c in ['主校区','北校区','南校区','东校区']" :key="c" :value="c">{{ c }}</option>
              </select>
            </div>
            <div class="form-group">
              <label>就读层次</label>
              <select v-model="form.grade" class="form-input">
                <option :value="null" disabled selected>选择层次</option>
                <option :value="1">本科生</option>
                <option :value="2">硕士研究生</option>
                <option :value="3">博士研究生</option>
              </select>
            </div>
          </div>

          <div class="form-group">
            <label>期望年龄范围</label>
            <div class="range-group">
              <div class="range-labels">
                <span>最小：{{ form.ageRequirementMin || '未设置' }} {{ form.ageRequirementMin ? '岁' : '' }}</span>
                <span>最大：{{ form.ageRequirementMax || '未设置' }} {{ form.ageRequirementMax ? '岁' : '' }}</span>
              </div>
              <div class="range-inputs">
                <div class="range-item">
                  <input v-model.number="form.ageRequirementMin" type="range" min="16" max="30" class="range-slider" />
                </div>
                <div class="range-item">
                  <input v-model.number="form.ageRequirementMax" type="range" min="16" max="30" class="range-slider" />
                </div>
              </div>
            </div>
          </div>

          <div class="form-group">
            <label>期望身高范围</label>
            <div class="range-group">
              <div class="range-labels">
                <span>最小：{{ form.heightRequirementMin || '未设置' }} {{ form.heightRequirementMin ? 'cm' : '' }}</span>
                <span>最大：{{ form.heightRequirementMax || '未设置' }} {{ form.heightRequirementMax ? 'cm' : '' }}</span>
              </div>
              <div class="range-inputs">
                <div class="range-item">
                  <input v-model.number="form.heightRequirementMin" type="range" min="140" max="220" class="range-slider" />
                </div>
                <div class="range-item">
                  <input v-model.number="form.heightRequirementMax" type="range" min="140" max="220" class="range-slider" />
                </div>
              </div>
            </div>
          </div>

          <div class="form-group">
            <label>期望校区（可多选）</label>
            <div class="cat-select">
              <a v-for="c in ['主校区','北校区','南校区','东校区']" :key="c"
                 :class="{ active: form.campusRequirement?.includes(c) }"
                 @click="toggleCampus(c)">
                {{ c }}
              </a>
            </div>
          </div>

          <button class="submit-btn" @click.prevent="startTest">同步信息</button>
        </form>
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '../utils/request'

const router = useRouter()

const form = ref({
  name: '',
  gender: 1,
  choose: 1,
  campus: '',
  grade: null,
  height: '',
  number: '',
  college: '',
  age: '',
  friendChoose: '',
  ageRequirementMin: '',
  ageRequirementMax: '',
  heightRequirementMin: '',
  heightRequirementMax: '',
  campusRequirement: []
})

onMounted(async () => {
  try {
    const res = await axios.get('/api/match/getuserinfo')
    if (res.data.code === 200 && res.data.data) {
      const u = res.data.data
      form.value.name = u.name || ''
      form.value.gender = u.gender === true ? 1 : u.gender === false ? 0 : 1
      form.value.choose = u.choose === '1' ? 1 : u.choose === '0' ? 0 : 1
      form.value.campus = u.campus || ''
      form.value.grade = u.grade || null
      form.value.height = u.height || ''
      form.value.number = u.number || ''
      form.value.college = u.college || ''
      form.value.age = u.age || ''
      form.value.friendChoose = u.friendChoose || ''
      form.value.ageRequirementMin = u.ageRequirementMin || ''
      form.value.ageRequirementMax = u.ageRequirementMax || ''
      form.value.heightRequirementMin = u.heightRequirementMin || ''
      form.value.heightRequirementMax = u.heightRequirementMax || ''
      // campusRequirement 由 JacksonTypeHandler 直接映射为数组
      form.value.campusRequirement = u.campusRequirement || []
    }
  } catch (err) {
    console.error('获取用户信息失败:', err)
  }
})

const colleges = [
  '商学院', '法学院', '政治与公共管理学院', '文学院', '新闻与传播学院',
  '外国语与国际关系学院', '马克思主义学院', '教育学院', '历史学院',
  '考古与文化遗产学院', '信息管理学院', '运动与体育学院', '音乐学院',
  '美术学院', '书法学院', '哲学学院', '数学与统计学院', '化学学院', '药学院',
  '物理学院', '管理学院', '电气与信息工程学院', '计算机与人工智能学院',
  '材料科学与工程学院', '机械与动力工程学院', '水利与交通学院',
  '土木工程学院', '建筑学院', '力学与安全工程学院', '网络空间安全学院',
  '生态与环境学院', '地球科学与技术学院', '生命科学学院',
  '农业与生物制造学院', '继续教育学院', '国际教育学院', '河南医学院'
]

const toggleCampus = (c) => {
  if (!form.value.campusRequirement) {
    form.value.campusRequirement = []
  }
  const idx = form.value.campusRequirement.indexOf(c)
  if (idx === -1) {
    form.value.campusRequirement.push(c)
  } else {
    form.value.campusRequirement.splice(idx, 1)
  }
}

const startTest = async () => {
  // 验证所有字段都已填写
  const errors = []
  
  if (!form.value.name.trim()) errors.push('请填写昵称或姓名')
  if (form.value.gender === null) errors.push('请选择性别标识')
  if (form.value.choose === null) errors.push('请选择倾向匹配')
  if (!form.value.campus) errors.push('请选择常驻校区')
  if (!form.value.college) errors.push('请选择学院')
  if (!form.value.grade) errors.push('请选择就读层次')
  if (!form.value.number.trim()) errors.push('请填写联系方式')
  if (!form.value.age) errors.push('请填写年龄')
  if (!form.value.height) errors.push('请填写身高')
  if (!form.value.friendChoose) errors.push('请选择交友意向')
  
  // 验证年龄范围
  if (!form.value.ageRequirementMin || !form.value.ageRequirementMax) {
    errors.push('请设置期望年龄范围')
  } else if (parseInt(form.value.ageRequirementMin) > parseInt(form.value.ageRequirementMax)) {
    errors.push('年龄最小值不能大于最大值')
  }
  
  // 验证身高范围
  if (!form.value.heightRequirementMin || !form.value.heightRequirementMax) {
    errors.push('请设置期望身高范围')
  } else if (parseInt(form.value.heightRequirementMin) > parseInt(form.value.heightRequirementMax)) {
    errors.push('身高最小值不能大于最大值')
  }
  
  // 验证期望校区
  if (!form.value.campusRequirement || form.value.campusRequirement.length === 0) {
    errors.push('请至少选择一个期望校区')
  }
  
  if (errors.length > 0) {
    alert(errors[0])  // 只显示第一个错误
    return
  }

  try {
    const postData = {
      ...form.value,
      gender: form.value.gender === 1,
      choose: form.value.choose.toString()
    }
    const response = await axios.post('/api/match/savebaseinfo', postData)
    if (response.data.code === 200 || response.data.msg === "同步成功") {
      alert('画像同步成功')
      localStorage.setItem('base_info', JSON.stringify(form.value))
      localStorage.setItem('userName', form.value.name)
      router.push('/home')
    } else {
      alert('同步失败：' + (response.data.msg || '未知错误'))
    }
  } catch (error) {
    console.error('同步过程中发生异常:', error)
    alert('网络异常')
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

/* Form */
.elegant-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.form-group { margin-bottom: 0; }
.form-group label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #666;
  margin-bottom: 6px;
}
.form-input, select.form-input {
  width: 100%;
  border: 1px solid #ddd;
  border-radius: 0;
  padding: 10px 12px;
  font-size: 14px;
  font-family: inherit;
  color: #333;
  outline: none;
  background: #fff;
  resize: vertical;
  -webkit-appearance: none;
  box-shadow: none;
}
input[type="number"].form-input,
input.mono-input {
  font-family: "SF Mono", "Menlo", "Consolas", "PingFang SC", monospace;
  letter-spacing: 0.5px;
}
.form-input:focus,
.form-input:focus-visible,
.form-input:active {
  border-color: #1a0dab;
  box-shadow: none;
  outline: none;
}
.form-input::placeholder { color: #bbb; }

/* Selector */
.cat-select {
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
}
.cat-select a {
  font-size: 13px;
  color: #666;
  text-decoration: none;
  padding: 6px 16px;
  border: 1px solid #ddd;
  transition: all 0.15s;
}
.cat-select a:hover { color: #1a0dab; border-color: #1a0dab; }
.cat-select a.active {
  color: #fff;
  background: #1a0dab;
  border-color: #1a0dab;
  font-weight: 600;
}

/* Submit */
.submit-btn {
  background: #1a0dab;
  color: #fff;
  border: none;
  padding: 10px 28px;
  font-size: 14px;
  font-family: inherit;
  cursor: pointer;
  transition: background 0.15s;
  margin-top: 8px;
}
.submit-btn:hover { background: #120880; }

/* Range Slider */
.range-group {
  margin-top: 8px;
}
.range-labels {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #1a0dab;
  font-weight: 600;
  margin-bottom: 12px;
  font-family: "SF Mono", "Menlo", "Consolas", "PingFang SC", monospace;
}
.range-labels span {
  font-family: "SF Mono", "Menlo", "Consolas", "PingFang SC", monospace;
  letter-spacing: 0.5px;
}
.range-inputs {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}
.range-item {
  display: flex;
  align-items: center;
}
.range-slider {
  width: 100%;
  -webkit-appearance: none;
  appearance: none;
  height: 4px;
  background: #ddd;
  outline: none;
  border-radius: 2px;
}
.range-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: #1a0dab;
  cursor: pointer;
  border: 2px solid #fff;
  box-shadow: 0 0 0 1px #1a0dab;
}
.range-slider::-moz-range-thumb {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: #1a0dab;
  cursor: pointer;
  border: 2px solid #fff;
  box-shadow: 0 0 0 1px #1a0dab;
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
  .form-row { grid-template-columns: 1fr; gap: 16px; }
}

@media (max-width: 375px) {
  .site-nav { gap: 10px; }
  .site-nav a { font-size: 12px; }
  .page-title { font-size: 17px; }
}
</style>
