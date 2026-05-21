<template>
  <div class="app">
    <nav class="navbar">
      <div class="nav-inner">
        <span class="site-title" @click="$router.push('/community')">ZZU Community</span>
        <div class="nav-links">
          <a class="nav-link" @click="$router.push('/community')">返回社区</a>
        </div>
      </div>
    </nav>

    <main class="main">
      <div class="content">

        <!-- 个人信息 -->
        <section class="profile-section">
          <h1 class="username">{{ userName || '未设置昵称' }}</h1>
          <div class="meta-line">
            <span class="meta-item">Lv.<span class="mono">{{ levelInfo.level }}</span> {{ levelInfo.title }}</span>
            <span class="meta-sep">·</span>
            <span class="meta-item">经验 <span class="mono">{{ levelInfo.exp }}</span> / <span class="mono">{{ levelInfo.nextExp }}</span></span>
          </div>
        </section>

        <hr class="divider" />

        <!-- 我的帖子 -->
        <section class="posts-section">
          <h2 class="section-heading">我发布的帖子</h2>

          <div v-if="loading" class="loading-state">正在加载...</div>

          <div v-else-if="myPosts.length === 0" class="empty-state">
            <p>你还没有发布过帖子。</p>
            <a class="text-link" @click="$router.push('/community')">去发布 →</a>
          </div>

          <div v-else class="post-list">
            <article class="post-item" v-for="item in myPosts" :key="item.id" @click="openDetail(item)">
              <div class="post-meta">
                <span class="post-category">{{ getCategoryLabel(item.category) }}</span>
                <span class="post-sep">·</span>
                <time class="post-time">{{ formatTime(item.createTime) }}</time>
              </div>
              <h3 class="post-title">{{ item.title }}</h3>
              <p class="post-excerpt">{{ item.content }}</p>
            </article>
          </div>
        </section>

      </div>
    </main>

    <!-- 详情弹窗 -->
    <transition name="fade">
      <div class="overlay" v-if="showDetail" @click.self="showDetail = false">
        <div class="detail-panel">
          <button class="close-btn" @click="showDetail = false">关闭</button>
          <div class="detail-meta">
            <time>{{ formatTime(detailItem.createTime) }}</time>
            <span class="meta-sep">·</span>
            <span>{{ detailItem.userName || '匿名' }}</span>
          </div>
          <h2 class="detail-title">{{ detailItem.title }}</h2>
          <div class="detail-body">{{ detailItem.content }}</div>
          <div class="detail-actions">
            <button class="action-link" @click="handleEdit">编辑</button>
            <button class="action-link danger" @click="handleDelete">删除</button>
          </div>

          <!-- 评论区 -->
          <div class="comment-section">
            <h3 class="comment-heading">评论 (<span class="mono">{{ comments.length }}</span>)</h3>
            <div class="comment-list" v-if="comments.length > 0">
              <div class="comment-item" v-for="c in comments" :key="c.id">
                <span class="comment-author">{{ c.userName || '匿名' }}</span>
                <span class="comment-dot">·</span>
                <time class="comment-time">{{ formatTime(c.createTime) }}</time>
                <p class="comment-text">{{ c.content }}</p>
                <button v-if="isCommentOwner(c)" class="action-link danger comment-del" @click="handleDeleteComment(c)">删除</button>
              </div>
            </div>
            <div v-else class="comment-empty">暂无评论</div>
            <div class="comment-form">
              <textarea v-model="commentText" class="comment-input" rows="2" placeholder="写下你的评论..."></textarea>
              <button class="action-link" @click="handleAddComment">发表</button>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <!-- 编辑弹窗 -->
    <transition name="fade">
      <div class="overlay" v-if="showEdit" @click.self="showEdit = false">
        <div class="detail-panel">
          <button class="close-btn" @click="showEdit = false">关闭</button>
          <h2 class="detail-title">编辑帖子</h2>
          <div class="form-group">
            <label>标题</label>
            <input v-model="editForm.title" type="text" class="form-input" />
          </div>
          <div class="form-group">
            <label>内容</label>
            <textarea v-model="editForm.content" class="form-textarea" rows="5"></textarea>
          </div>
          <button class="action-link" @click="handleUpdate">保存修改</button>
        </div>
      </div>
    </transition>

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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '../utils/request'
import { syncUserInfo } from '../utils/userSync'
import { formatTime, categories } from '../utils/community'

const router = useRouter()
const loading = ref(false)
const myPosts = ref([])
const userName = ref(localStorage.getItem('userName') || '')

const showDetail = ref(false)
const detailItem = ref({})
const showEdit = ref(false)
const editForm = ref({ id: '', title: '', content: '' })
const comments = ref([])
const commentText = ref('')


// 等级系统
const exp = ref(parseInt(localStorage.getItem('exp') || '0', 10))
const levelInfo = computed(() => {
  const lv = Math.floor(exp.value / 100)
  let title = ''
  if (lv < 5) title = '初来乍到'
  else if (lv < 10) title = '常驻居民'
  else if (lv < 15) title = '小有名气'
  else if (lv < 20) title = '校园达人'
  else title = '一代宗师'
  return { level: lv, title, exp: exp.value, nextExp: (lv + 1) * 100 }
})

const getCategoryLabel = (key) => {
  const cat = categories.find(c => c.key === key)
  return cat ? cat.name : key
}


const openDetail = (item) => {
  detailItem.value = item
  showDetail.value = true
  fetchComments(item.id)
}

const handleEdit = () => {
  editForm.value = {
    id: detailItem.value.id,
    title: detailItem.value.title,
    content: detailItem.value.content
  }
  showDetail.value = false
  showEdit.value = true
}

const handleUpdate = async () => {
  if (!editForm.value.title.trim()) { alert('请输入标题'); return }
  if (!editForm.value.content.trim()) { alert('请输入内容'); return }
  try {
    const res = await axios.post('/api/Info/updateInfo', {
      id: editForm.value.id,
      userId: localStorage.getItem('userId'),
      title: editForm.value.title,
      content: editForm.value.content
    })
    if (res.data.code === 200) {
      alert('修改成功')
      showEdit.value = false
      fetchMyPosts()
    } else {
      alert('修改失败：' + (res.data.message || '未知错误'))
    }
  } catch (err) {
    console.error('修改失败:', err)
    alert('网络异常，修改失败')
  }
}

const handleDelete = async () => {
  if (!confirm('确定要删除这条帖子吗？')) return
  try {
    const res = await axios.post('/api/Info/deleteInfo', {
      id: detailItem.value.id,
      userId: localStorage.getItem('userId')
    })
    if (res.data.code === 200) {
      alert('删除成功')
      showDetail.value = false
      fetchMyPosts()
    } else {
      alert('删除失败：' + (res.data.message || '未知错误'))
    }
  } catch (err) {
    console.error('删除失败:', err)
    alert('网络异常，删除失败')
  }
}

const fetchComments = async (infoId) => {
  try {
    const res = await axios.get('/api/Comment/listByInfoId', {
      params: { infoId }
    })
    if (res.data.code === 200) {
      comments.value = res.data.data || []
    } else {
      comments.value = []
    }
  } catch (err) {
    console.error('获取评论失败:', err)
    comments.value = []
  }
}

const isCommentOwner = (c) => {
  const userId = localStorage.getItem('userId')
  return userId && c.userId === userId
}

const handleAddComment = async () => {
  if (!commentText.value.trim()) { alert('请输入评论内容'); return }
  try {
    const res = await axios.post('/api/Comment/save', {
      infoId: detailItem.value.id,
      content: commentText.value.trim(),
      userName: localStorage.getItem('userName') || ''
    })
    if (res.data.code === 200) {
      commentText.value = ''
      fetchComments(detailItem.value.id)
    } else {
      alert('评论失败：' + (res.data.message || '未知错误'))
    }
  } catch (err) {
    alert('网络异常')
  }
}

const handleDeleteComment = async (c) => {
  if (!confirm('确定删除这条评论？')) return
  try {
    const res = await axios.post('/api/Comment/delete', { id: c.id, userId: c.userId })
    if (res.data.code === 200) {
      fetchComments(detailItem.value.id)
    } else {
      alert('删除失败')
    }
  } catch (err) {
    alert('网络异常')
  }
}

const fetchMyPosts = async () => {
  loading.value = true
  try {
    const userId = localStorage.getItem('userId')
    const res = await axios.get('/api/Info/listByUserId', {
      params: { userId }
    })
    if (res.data.code === 200) {
      myPosts.value = res.data.data || []
    }
  } catch (err) {
    console.error('获取我的帖子失败:', err)
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  // 每次进入个人中心都同步用户信息
  await syncUserInfo()
  // 用最新数据更新响应式变量
  userName.value = localStorage.getItem('userName') || ''
  exp.value = parseInt(localStorage.getItem('exp') || '0', 10)
  fetchMyPosts()
})
</script>

<style scoped>
* { box-sizing: border-box; margin: 0; padding: 0; }

.app {
  font-family: 'Noto Serif SC', 'Source Han Serif SC', 'Songti SC', Georgia, 'Times New Roman', serif;
  color: #333;
  background: #fff;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* ── Navbar ── */
.navbar {
  border-bottom: 1px solid #ddd;
  background: #fff;
  position: sticky; top: 0; z-index: 100;
}
.nav-inner {
  max-width: 680px; margin: 0 auto;
  padding: 0 24px; height: 56px;
  display: flex; align-items: center; justify-content: space-between;
}
.site-title {
  font-size: 18px; font-weight: 700; color: #1a0dab;
  cursor: pointer; letter-spacing: -0.3px;
}
.nav-links { display: flex; gap: 20px; }
.nav-link {
  font-size: 14px; color: #1a0dab; cursor: pointer;
  text-decoration: none;
}
.nav-link:hover { text-decoration: underline; }

/* ── Main ── */
.main {
  flex: 1; width: 100%;
}
.content {
  max-width: 680px; margin: 0 auto;
  padding: 48px 24px 80px;
}

/* ── Profile ── */
.profile-section {
  margin-bottom: 0;
}
.username {
  font-size: 26px; font-weight: 700; color: #111;
  line-height: 1.4; margin-bottom: 10px;
  letter-spacing: -0.3px;
}
.meta-line {
  display: flex; align-items: center; gap: 6px;
  font-size: 14px; color: #666;
  flex-wrap: wrap;
}
.meta-sep { color: #ccc; }
.meta-item { white-space: nowrap; }
.meta-item .mono { font-family: 'SF Mono', Menlo, Consolas, monospace; }

.divider {
  border: none; border-top: 1px solid #ddd;
  margin: 32px 0;
}

/* ── Posts ── */
.posts-section {}
.section-heading {
  font-size: 20px; font-weight: 700; color: #111;
  margin-bottom: 28px; letter-spacing: -0.3px;
}

.loading-state {
  text-align: center; padding: 60px 0;
  color: #999; font-size: 14px;
}

.empty-state {
  text-align: center; padding: 60px 0;
  color: #666; font-size: 15px; line-height: 2;
}
.text-link {
  color: #1a0dab; cursor: pointer;
  font-size: 14px;
}
.text-link:hover { text-decoration: underline; }

.post-list {}
.post-item {
  padding: 20px 0;
  border-bottom: 1px solid #eee;
  cursor: pointer;
}
.post-item:hover .post-title { text-decoration: underline; }
.post-item:last-child { border-bottom: none; }

.post-meta {
  font-family: "SF Mono", "Menlo", "Consolas", "PingFang SC", monospace;
  font-size: 13px; color: #999; margin-bottom: 6px;
  display: flex; align-items: center; gap: 6px;
}
.post-category {
  color: #1a0dab;
}
.post-sep { color: #ccc; }
.post-time {}

.post-title {
  font-size: 17px; font-weight: 600; color: #1a0dab;
  line-height: 1.5; margin-bottom: 6px; cursor: pointer;
}
.post-title:hover { text-decoration: underline; }

.post-excerpt {
  font-size: 15px; color: #444; line-height: 1.8;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* ── Detail / Edit Modal ── */
.overlay {
  position: fixed; inset: 0; z-index: 200;
  background: rgba(0,0,0,0.4);
  display: flex; align-items: center; justify-content: center;
  padding: 20px;
}
.detail-panel {
  background: #fff; border: 1px solid #ddd;
  max-width: 600px; width: 100%;
  padding: 36px 32px;
  max-height: 85vh; overflow-y: auto;
  position: relative;
}
.close-btn {
  position: absolute; top: 16px; right: 20px;
  background: none; border: none; cursor: pointer;
  font-size: 14px; color: #1a0dab; font-family: inherit;
}
.close-btn:hover { text-decoration: underline; }
.detail-meta {
  font-size: 13px; color: #999; margin-bottom: 12px;
  display: flex; align-items: center; gap: 6px; flex-wrap: wrap;
}
.detail-meta .post-cat { color: #1a0dab; }
.detail-title {
  font-size: 22px; font-weight: 700; color: #111;
  line-height: 1.4; margin-bottom: 20px;
}
.detail-body {
  font-size: 15px; color: #333; line-height: 1.9;
  white-space: pre-wrap; word-break: break-word;
}
.detail-actions {
  margin-top: 28px; padding-top: 20px;
  border-top: 1px solid #eee;
  display: flex; gap: 20px;
}
.action-link {
  background: none; border: none; cursor: pointer;
  font-size: 14px; color: #1a0dab; font-family: inherit;
  padding: 0;
}
.action-link:hover { text-decoration: underline; }
.action-link.danger { color: #c00; }

/* ── Comment Section ── */
.comment-section {
  margin-top: 32px; padding-top: 24px;
  border-top: 1px solid #eee;
}
.comment-heading {
  font-size: 16px; font-weight: 700; color: #222;
  margin-bottom: 16px;
}
.comment-heading .mono {
  font-family: "SF Mono", "Menlo", "Consolas", monospace;
}
.comment-list { margin-bottom: 20px; }
.comment-item {
  padding: 12px 0; border-bottom: 1px solid #f0f0f0;
  position: relative;
}
.comment-item:last-child { border-bottom: none; }
.comment-author { font-size: 13px; font-weight: 600; color: #1a0dab; }
.comment-dot { color: #ccc; font-size: 12px; }
.comment-time {
  font-family: "SF Mono", "Menlo", "Consolas", "PingFang SC", monospace;
  font-size: 12px; color: #999;
}
.comment-text { font-size: 14px; color: #444; line-height: 1.8; margin-top: 4px; }
.comment-del { position: absolute; top: 12px; right: 0; font-size: 12px; }
.comment-empty { font-size: 13px; color: #999; padding: 16px 0; }
.comment-form {
  display: flex; gap: 10px; align-items: flex-end; margin-top: 12px;
}
.comment-input {
  flex: 1; border: 1px solid #ddd; padding: 8px 10px;
  font-size: 14px; font-family: inherit; color: #333;
  outline: none; resize: vertical; min-height: 40px;
}
.comment-input:focus { border-color: #1a0dab; box-shadow: 0 0 0 1px #1a0dab; }
.comment-input::placeholder { color: #bbb; }
.form-group {
  margin-bottom: 20px;
}
.form-group label {
  display: block; font-size: 14px; font-weight: 600;
  color: #333; margin-bottom: 6px;
}
.form-input {
  width: 100%; padding: 8px 10px;
  border: 1px solid #ccc; font-size: 15px;
  font-family: inherit; color: #333;
  outline: none;
}
.form-input:focus { border-color: #1a0dab; }
.form-textarea {
  width: 100%; padding: 8px 10px;
  border: 1px solid #ccc; font-size: 15px;
  font-family: inherit; color: #333;
  outline: none; resize: vertical;
}
.form-textarea:focus { border-color: #1a0dab; }

.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

/* ── Footer ── */
.footer {
  border-top: 1px solid #ddd;
  background: #fff;
}
.footer-inner {
  max-width: 680px; margin: 0 auto;
  padding: 24px;
  font-size: 12px; color: #999;
  text-align: center;
}
.footer-inner .mono {
  font-family: 'SF Mono', Menlo, Consolas, monospace;
}

/* ── Mobile ── */
@media (max-width: 600px) {
  .nav-inner { padding: 0 16px; height: 50px; }
  .site-title { font-size: 16px; }
  .content { padding: 32px 16px 60px; }
  .username { font-size: 22px; }
  .section-heading { font-size: 18px; margin-bottom: 20px; }
  .post-title { font-size: 16px; }
  .post-excerpt { font-size: 14px; -webkit-line-clamp: 2; }
}
</style>
