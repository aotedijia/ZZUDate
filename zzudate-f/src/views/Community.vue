<template>
  <div class="app">
    <header class="site-header">
      <div class="header-inner">
        <h1 class="site-title" @click="refreshPage">ZZU Community</h1>
        <nav class="site-nav">
          <a @click="$router.push('/home')">返回ZZUDate</a>
          <a @click="$router.push('/profile')">个人中心</a>
          <a @click="handleLogout">退出</a>
        </nav>
      </div>
    </header>

    <main class="main">
      <div class="main-inner">
        <!-- 页面标题 -->
        <div class="page-header">
          <h2 class="page-title">社区</h2>
          <button class="publish-link" @click="showPublish = true">+ 写新帖</button>
        </div>

        <!-- 分类 -->
        <div class="categories">
          <a
            v-for="cat in categories"
            :key="cat.key"
            :class="{ active: activeCategory === cat.key }"
            @click="switchCategory(cat.key)"
          >{{ cat.name }}</a>
        </div>

        <!-- 加载 -->
        <div v-if="loading" class="loading">加载中...</div>

        <!-- 空态 -->
        <div v-else-if="infoList.length === 0" class="empty">
          <p>暂无内容。</p>
        </div>

        <!-- 帖子列表 -->
        <div v-else class="post-list">
          <article
            class="post-item"
            v-for="item in infoList"
            :key="item.id"
            @click="openDetail(item)"
          >
            <div class="post-meta">
              <span v-if="activeCategory === '公开讨论区'" class="post-cat">{{ item.category }}</span>
              <span v-if="activeCategory === '公开讨论区'" class="post-dot">·</span>
              <time>{{ formatTime(item.createTime) }}</time>
              <span class="post-dot">·</span>
              <span>{{ item.userName || '匿名' }}</span>
            </div>
            <h3 class="post-title">{{ item.title }}</h3>
            <p class="post-excerpt">{{ item.content }}</p>
          </article>
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

    <!-- 详情弹窗 -->
    <transition name="fade">
      <div class="overlay" v-if="showDetail" @click.self="showDetail = false">
        <div class="detail-panel">
          <button class="close-btn" @click="showDetail = false">关闭</button>
          <div class="detail-meta">
            <time>{{ formatTime(detailItem.createTime) }}</time>
            <span class="post-dot">·</span>
            <span>{{ detailItem.userName || '匿名' }}</span>
          </div>
          <h2 class="detail-title">{{ detailItem.title }}</h2>
          <div class="detail-body">{{ detailItem.content }}</div>
          <div class="detail-actions" v-if="isOwner(detailItem)">
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
              <button class="submit-btn" @click="handleAddComment">发表评论</button>
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
            <textarea v-model="editForm.content" class="form-textarea" rows="6"></textarea>
          </div>
          <button class="submit-btn" @click="handleUpdate">保存</button>
        </div>
      </div>
    </transition>

    <!-- 发布弹窗 -->
    <transition name="fade">
      <div class="overlay" v-if="showPublish" @click.self="showPublish = false">
        <div class="detail-panel">
          <button class="close-btn" @click="showPublish = false">关闭</button>
          <h2 class="detail-title">写新帖</h2>

          <div class="form-group">
            <label>分区</label>
            <div class="cat-select">
              <a
                v-for="cat in categories"
                :key="cat.key"
                :class="{ active: publishForm.category === cat.key }"
                @click="publishForm.category = cat.key"
              >{{ cat.name }}</a>
            </div>
          </div>

          <div class="form-group">
            <label>标题</label>
            <input v-model="publishForm.title" type="text" class="form-input" placeholder="请输入标题" />
          </div>

          <div class="form-group">
            <label>内容</label>
            <textarea v-model="publishForm.content" class="form-textarea" rows="6" placeholder="请输入内容"></textarea>
          </div>

          <button class="submit-btn" @click="handlePublish">发布</button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '../utils/request'
import { formatTime, categories } from '../utils/community'

const router = useRouter()

const refreshPage = () => {
  fetchInfoList()
}

const handleLogout = async () => {
  try {
    await axios.post('/api/auth/logout', {})
  } catch (err) {
    console.error('Logout Error:', err)
  } finally {
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('userName')
    localStorage.removeItem('exp')
    localStorage.removeItem('base_info')
    localStorage.removeItem('lastSyncDate')
    localStorage.removeItem('avatarSeed')
    router.push('/community/login')
  }
}
const loading = ref(false)
const infoList = ref([])
const activeCategory = ref('公开讨论区')
const showPublish = ref(false)

const showDetail = ref(false)
const detailItem = ref({})
const showEdit = ref(false)
const editForm = ref({ id: '', title: '', content: '' })
const comments = ref([])
const commentText = ref('')

const publishForm = ref({ title: '', content: '', category: '' })

const fetchInfoList = async () => {
  loading.value = true
  try {
    let res
    if (activeCategory.value === '公开讨论区') {
      res = await axios.get('/api/Info/list')
    } else {
      res = await axios.get('/api/Info/listByCategory', {
        params: { category: activeCategory.value }
      })
    }
    infoList.value = res.data.code === 200 ? (res.data.data || []) : []
  } catch (err) {
    console.error('获取列表失败:', err)
    infoList.value = []
  } finally {
    loading.value = false
  }
}

const switchCategory = (key) => {
  activeCategory.value = key
  fetchInfoList()
}

const isOwner = (item) => {
  const userId = localStorage.getItem('userId')
  return userId && item.userId === userId
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
      id: editForm.value.id, userId: localStorage.getItem('userId'), title: editForm.value.title, content: editForm.value.content
    })
    if (res.data.code === 200) { alert('修改成功'); showEdit.value = false; fetchInfoList() }
    else { alert('修改失败：' + (res.data.message || '未知错误')) }
  } catch (err) {
    alert('网络异常')
  }
}

const handleDelete = async () => {
  if (!confirm('确定要删除这条帖子吗？')) return
  try {
    const res = await axios.post('/api/Info/deleteInfo', { id: detailItem.value.id, userId: localStorage.getItem('userId') })
    if (res.data.code === 200) { alert('删除成功'); showDetail.value = false; fetchInfoList() }
    else { alert('删除失败') }
  } catch (err) {
    alert('网络异常')
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

const handlePublish = async () => {
  if (!publishForm.value.category) { alert('请选择分区'); return }
  if (!publishForm.value.title.trim()) { alert('请输入标题'); return }
  if (!publishForm.value.content.trim()) { alert('请输入内容'); return }
  try {
    const res = await axios.post('/api/Info/saveInfo', {
      ...publishForm.value,
      userId: localStorage.getItem('userId') || '',
      userName: localStorage.getItem('userName') || ''
    })
    if (res.data.code === 200) {
      if (res.data.data != null) localStorage.setItem('exp', res.data.data)
      alert('发布成功')
      showPublish.value = false
      publishForm.value = { title: '', content: '', category: '' }
      fetchInfoList()
    } else { alert('发布失败：' + (res.data.message || '未知错误')) }
  } catch (err) {
    alert('网络异常')
  }
}

onMounted(() => {
  fetchInfoList()
})

</script>

<style scoped>
/* ========== Reset & Base ========== */
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

/* ========== Header ========== */
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

/* ========== Main ========== */
.main {
  flex: 1;
}
.main-inner {
  max-width: 680px;
  margin: 0 auto;
  padding: 40px 24px 80px;
}

/* ========== Page Header ========== */
.page-header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e0e0e0;
}
.page-title {
  font-size: 22px;
  font-weight: 700;
  color: #222;
}
.publish-link {
  font-size: 14px;
  color: #1a0dab;
  background: none;
  border: none;
  cursor: pointer;
  font-family: inherit;
}
.publish-link:hover {
  color: #600;
}

/* ========== Categories ========== */
.categories {
  display: flex;
  gap: 18px;
  margin-bottom: 32px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e0e0e0;
}
.categories a {
  font-size: 14px;
  color: #666;
  text-decoration: none;
  padding-bottom: 2px;
  border-bottom: 2px solid transparent;
  transition: all 0.15s;
}
.categories a:hover {
  color: #1a0dab;
}
.categories a.active {
  color: #1a0dab;
  border-bottom-color: #1a0dab;
  font-weight: 600;
}

/* ========== Loading / Empty ========== */
.loading, .empty {
  padding: 60px 0;
  text-align: center;
  color: #999;
  font-size: 14px;
}

/* ========== Post List ========== */
.post-list {
  display: flex;
  flex-direction: column;
}

.post-item {
  padding: 24px 0;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background 0.15s;
}
.post-item:hover {
  background: #fafafa;
}
.post-item:last-child {
  border-bottom: none;
}

.post-meta {
  font-family: "SF Mono", "Menlo", "Consolas", "PingFang SC", monospace;
  font-size: 12px;
  color: #999;
  margin-bottom: 6px;
  display: flex;
  align-items: center;
  gap: 6px;
}
.post-dot {
  color: #ccc;
}
.post-cat {
  color: #1a0dab;
}

.post-title {
  font-size: 17px;
  font-weight: 700;
  color: #1a1a1a;
  line-height: 1.5;
  margin-bottom: 6px;
}
.post-item:hover .post-title {
  color: #1a0dab;
}

.post-excerpt {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* ========== Footer ========== */
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

/* ========== Overlay / Modal ========== */
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: flex-start;
  justify-content: center;
  z-index: 999;
  padding-top: 60px;
  overflow-y: auto;
}
.detail-panel {
  background: #fff;
  width: 90%;
  max-width: 600px;
  padding: 40px 36px;
  position: relative;
  margin-bottom: 60px;
}
.close-btn {
  position: absolute;
  top: 16px;
  right: 20px;
  background: none;
  border: none;
  font-size: 14px;
  color: #999;
  cursor: pointer;
  font-family: inherit;
}
.close-btn:hover { color: #333; }

.detail-meta {
  font-family: "SF Mono", "Menlo", "Consolas", "PingFang SC", monospace;
  font-size: 12px;
  color: #999;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.detail-title {
  font-size: 22px;
  font-weight: 700;
  color: #1a1a1a;
  line-height: 1.5;
  margin-bottom: 20px;
}

.detail-body {
  font-size: 15px;
  color: #444;
  line-height: 2;
  white-space: pre-wrap;
  word-break: break-word;
}

.detail-actions {
  margin-top: 28px;
  padding-top: 16px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 20px;
}
.action-link {
  background: none;
  border: none;
  font-size: 14px;
  color: #1a0dab;
  cursor: pointer;
  font-family: inherit;
}
.action-link:hover { color: #600; }
.action-link.danger { color: #c00; }
.action-link.danger:hover { color: #900; }

/* ========== Comment Section ========== */
.comment-section {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #eee;
}
.comment-heading {
  font-size: 16px;
  font-weight: 700;
  color: #222;
  margin-bottom: 16px;
}
.comment-heading .mono {
  font-family: "SF Mono", "Menlo", "Consolas", monospace;
}
.comment-list {
  margin-bottom: 20px;
}
.comment-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  position: relative;
}
.comment-item:last-child { border-bottom: none; }
.comment-author {
  font-size: 13px;
  font-weight: 600;
  color: #1a0dab;
}
.comment-dot { color: #ccc; font-size: 12px; }
.comment-time {
  font-family: "SF Mono", "Menlo", "Consolas", "PingFang SC", monospace;
  font-size: 12px;
  color: #999;
}
.comment-text {
  font-size: 14px;
  color: #444;
  line-height: 1.8;
  margin-top: 4px;
}
.comment-del {
  position: absolute;
  top: 12px; right: 0;
  font-size: 12px;
}
.comment-empty {
  font-size: 13px;
  color: #999;
  padding: 16px 0;
}
.comment-form {
  display: flex;
  gap: 10px;
  align-items: flex-end;
  margin-top: 12px;
}
.comment-input {
  flex: 1;
  border: 1px solid #ddd;
  padding: 8px 10px;
  font-size: 14px;
  font-family: "SF Mono", "Menlo", "Consolas", "PingFang SC", monospace;
  letter-spacing: 0.5px;
  color: #333;
  outline: none;
  resize: vertical;
  min-height: 40px;
}
.comment-input:focus {
  border-color: #1a0dab;
  box-shadow: 0 0 0 1px #1a0dab;
}
.comment-input::placeholder { color: #bbb; }
.comment-form .submit-btn {
  padding: 8px 20px;
  white-space: nowrap;
}

/* ========== Form ========== */
.form-group {
  margin-bottom: 20px;
}
.form-group label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #666;
  margin-bottom: 6px;
}
.form-input, .form-textarea {
  width: 100%;
  border: 1px solid #ddd;
  border-radius: 0;
  padding: 10px 12px;
  font-size: 14px;
  font-family: "SF Mono", "Menlo", "Consolas", "PingFang SC", monospace;
  letter-spacing: 0.5px;
  color: #333;
  outline: none;
  background: #fff;
  resize: vertical;
}
.form-input:focus, .form-textarea:focus {
  border-color: #1a0dab;
  box-shadow: 0 0 0 1px #1a0dab;
}
.form-input::placeholder, .form-textarea::placeholder { color: #bbb; }

.cat-select {
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
}
.cat-select a {
  font-size: 13px;
  color: #666;
  text-decoration: none;
  padding-bottom: 2px;
  border-bottom: 2px solid transparent;
  transition: all 0.15s;
}
.cat-select a:hover { color: #1a0dab; }
.cat-select a.active {
  color: #1a0dab;
  border-bottom-color: #1a0dab;
  font-weight: 600;
}

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

/* ========== Transition ========== */
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

/* ========== Responsive ========== */
@media (max-width: 600px) {
  .header-inner { padding: 16px; }
  .site-title { font-size: 17px; }
  .site-nav { gap: 12px; }
  .site-nav a { font-size: 13px; }
  .main-inner { padding: 24px 16px 60px; }
  .page-title { font-size: 19px; }
  .categories { gap: 12px; }
  .post-title { font-size: 15px; }
  .detail-panel { padding: 28px 20px; }
  .detail-title { font-size: 19px; }
}

@media (max-width: 375px) {
  .site-nav { gap: 10px; }
  .site-nav a { font-size: 12px; }
  .page-title { font-size: 17px; }
  .categories a { font-size: 13px; }
}
</style>
