<template>
  <n-space vertical :size="16">
    <page-header-card title="论坛管理" description="查看论坛帖子与回复内容，用于社区运营巡检。" />
    <n-card :bordered="false">
      <n-spin :show="loading">
        <n-space v-if="posts.length > 0" vertical :size="12">
          <n-card v-for="post in posts" :key="post.id" embedded>
            <n-space vertical :size="8">
              <n-space justify="space-between">
                <strong>{{ post.title || '无标题' }}</strong>
                <n-space>
                  <n-tag size="small">用户 {{ post.userId }}</n-tag>
                  <span class="time">{{ formatDateTime(post.createTime) }}</span>
                </n-space>
              </n-space>
              <p class="content">{{ post.content }}</p>
              <n-button size="small" tertiary @click="toggleReplies(post.id)">
                {{ expandedMap[post.id] ? '收起回复' : '查看回复' }}
              </n-button>
              <n-space v-if="expandedMap[post.id] && replyMap[post.id]?.length" vertical :size="8" class="reply-wrapper">
                <n-card v-for="reply in replyMap[post.id]" :key="reply.id" size="small" embedded>
                  <n-space justify="space-between">
                    <span>{{ reply.content }}</span>
                    <span class="time">用户 {{ reply.userId }} · {{ formatDateTime(reply.createTime) }}</span>
                  </n-space>
                </n-card>
              </n-space>
              <n-empty v-else-if="expandedMap[post.id]" description="暂无回复" />
            </n-space>
          </n-card>
        </n-space>
        <n-empty v-else description="暂无论坛数据" />
      </n-spin>
    </n-card>
  </n-space>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { adminApi } from '@/api/admin'
import type { ForumPostItem } from '@/types/user'
import { formatDateTime } from '@/utils/format'
import PageHeaderCard from '@/components/common/PageHeaderCard.vue'

const loading = ref(false)
const posts = ref<ForumPostItem[]>([])
const replyMap = ref<Record<number, ForumPostItem[]>>({})
const expandedMap = ref<Record<number, boolean>>({})

async function loadPosts() {
  loading.value = true
  try {
    posts.value = await adminApi.fetchForumPosts()
  } finally {
    loading.value = false
  }
}

async function toggleReplies(postId: number) {
  const next = !expandedMap.value[postId]
  expandedMap.value = {
    ...expandedMap.value,
    [postId]: next
  }
  if (next && !replyMap.value[postId]) {
    const replies = await adminApi.fetchForumReplies(postId)
    replyMap.value = {
      ...replyMap.value,
      [postId]: replies
    }
  }
}

loadPosts()
</script>

<style scoped>
.content {
  margin: 0;
  line-height: 1.8;
}

.reply-wrapper {
  border-radius: 8px;
  padding: 8px;
  background: #f8fbff;
}

.time {
  color: var(--text-sub);
  font-size: 12px;
}
</style>
