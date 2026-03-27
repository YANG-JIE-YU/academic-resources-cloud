<template>
  <div class="page-container">
    <n-space vertical :size="16">
      <page-header-card title="论坛交流" description="围绕论文选题、方法与技术栈进行讨论交流。">
        <template #extra>
          <n-button type="primary" @click="openCreatePost">发布帖子</n-button>
        </template>
      </page-header-card>

      <n-card :bordered="false">
        <n-spin :show="loading">
          <n-space v-if="posts.length > 0" vertical :size="12">
            <n-card v-for="post in posts" :key="post.id" embedded>
              <n-space vertical :size="8">
                <n-space justify="space-between" align="center">
                  <h3 class="post-title">{{ post.title || '无标题' }}</h3>
                  <n-space align="center">
                    <n-tag size="small">用户 {{ post.userId }}</n-tag>
                    <span class="time">{{ formatDateTime(post.createTime) }}</span>
                  </n-space>
                </n-space>
                <p class="content">{{ post.content }}</p>
                <n-space>
                  <n-button size="small" secondary @click="loadReplies(post.id)">查看回复</n-button>
                  <n-button size="small" type="primary" ghost @click="openReply(post.id)">回复</n-button>
                </n-space>
                <n-space v-if="replyMap[post.id]?.length" vertical :size="8" class="reply-block">
                  <n-card v-for="reply in replyMap[post.id]" :key="reply.id" size="small" embedded>
                    <n-space vertical :size="6">
                      <span class="reply-user">用户 {{ reply.userId }} 回复：</span>
                      <span>{{ reply.content }}</span>
                    </n-space>
                  </n-card>
                </n-space>
              </n-space>
            </n-card>
          </n-space>
          <n-empty v-else description="暂无论坛帖子" />
        </n-spin>
      </n-card>
    </n-space>

    <n-modal v-model:show="createModalVisible" preset="dialog" title="发布帖子" positive-text="发布" negative-text="取消" @positive-click="submitCreatePost">
      <n-form :model="createForm" label-placement="top">
        <n-form-item label="标题">
          <n-input v-model:value="createForm.title" placeholder="请输入帖子标题" />
        </n-form-item>
        <n-form-item label="内容">
          <n-input v-model:value="createForm.content" type="textarea" :rows="5" placeholder="请输入帖子内容" />
        </n-form-item>
      </n-form>
    </n-modal>

    <n-modal v-model:show="replyModalVisible" preset="dialog" title="回复帖子" positive-text="提交回复" negative-text="取消" @positive-click="submitReply">
      <n-input v-model:value="replyContent" type="textarea" :rows="5" placeholder="请输入回复内容" />
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useMessage } from 'naive-ui'
import { useRouter } from 'vue-router'
import { createForumPost, createForumReply, fetchForumPosts, fetchForumReplies } from '@/api/user'
import type { ForumPostItem } from '@/types/user'
import { formatDateTime } from '@/utils/format'
import { useUserStore } from '@/stores/user'
import PageHeaderCard from '@/components/common/PageHeaderCard.vue'

const router = useRouter()
const message = useMessage()
const userStore = useUserStore()

const loading = ref(false)
const posts = ref<ForumPostItem[]>([])
const replyMap = ref<Record<number, ForumPostItem[]>>({})

const createModalVisible = ref(false)
const replyModalVisible = ref(false)
const createForm = reactive({
  title: '',
  content: ''
})
const replyContent = ref('')
const activePostId = ref(0)

function ensureLogin() {
  if (userStore.isLoggedIn) {
    return true
  }
  message.warning('请先登录后再操作')
  router.push('/auth/login')
  return false
}

async function loadPosts() {
  loading.value = true
  try {
    posts.value = await fetchForumPosts()
  } finally {
    loading.value = false
  }
}

async function loadReplies(postId: number) {
  const replies = await fetchForumReplies(postId)
  replyMap.value = {
    ...replyMap.value,
    [postId]: replies
  }
}

function openCreatePost() {
  if (!ensureLogin()) {
    return
  }
  createModalVisible.value = true
}

function openReply(postId: number) {
  if (!ensureLogin()) {
    return
  }
  activePostId.value = postId
  replyContent.value = ''
  replyModalVisible.value = true
}

async function submitCreatePost() {
  if (!createForm.title.trim() || !createForm.content.trim()) {
    message.warning('标题和内容不能为空')
    return false
  }
  await createForumPost({
    title: createForm.title.trim(),
    content: createForm.content.trim()
  })
  message.success('发帖成功')
  createForm.title = ''
  createForm.content = ''
  await loadPosts()
  return true
}

async function submitReply() {
  if (!replyContent.value.trim()) {
    message.warning('回复内容不能为空')
    return false
  }
  await createForumReply({
    parentId: activePostId.value,
    content: replyContent.value.trim()
  })
  message.success('回复成功')
  await loadReplies(activePostId.value)
  return true
}

onMounted(() => {
  loadPosts()
})
</script>

<style scoped>
.post-title {
  margin: 0;
  font-size: 18px;
}

.time {
  color: var(--text-sub);
  font-size: 12px;
}

.content {
  margin: 0;
  line-height: 1.8;
  white-space: pre-wrap;
}

.reply-block {
  padding: 8px;
  border-radius: 8px;
  background: #f8fbff;
}

.reply-user {
  font-size: 13px;
  color: var(--text-sub);
}
</style>
