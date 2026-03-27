<template>
  <div class="page-container">
    <n-space vertical :size="16">
      <page-header-card title="我的评论" description="根据你关注的论文资源聚合展示个人评论。">
        <template #extra>
          <n-button @click="$router.push('/profile')">返回个人中心</n-button>
        </template>
      </page-header-card>

      <n-alert type="warning" :bordered="false">
        当前后端暂无“按用户直接查询评论”接口，此页面采用“收藏论文 + 评论聚合”方式展示。
      </n-alert>

      <n-card :bordered="false">
        <n-spin :show="loading">
          <n-space v-if="comments.length > 0" vertical :size="10">
            <n-card v-for="item in comments" :key="item.id" embedded>
              <n-space vertical :size="6">
                <n-space justify="space-between">
                  <strong>资源 {{ item.targetType }} #{{ item.targetId }}</strong>
                  <span class="time">{{ formatDateTime(item.createTime) }}</span>
                </n-space>
                <p class="content">{{ item.content }}</p>
                <n-alert v-if="item.replyContent" type="info" :bordered="false">管理员回复：{{ item.replyContent }}</n-alert>
              </n-space>
            </n-card>
          </n-space>
          <n-empty v-else description="暂无可展示的评论记录" />
        </n-spin>
      </n-card>
    </n-space>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import type { CommentItem, FavoriteItem } from '@/types/user'
import { fetchComments, fetchMyFavorites } from '@/api/user'
import { formatDateTime } from '@/utils/format'
import { useUserStore } from '@/stores/user'
import PageHeaderCard from '@/components/common/PageHeaderCard.vue'

const userStore = useUserStore()
const loading = ref(false)
const comments = ref<CommentItem[]>([])

async function loadCommentsByFavorites(favorites: FavoriteItem[]) {
  const myUserId = userStore.userId
  const targets = favorites.filter((item) => item.targetType.toUpperCase() === 'PAPER').slice(0, 20)
  const commentRows = await Promise.all(
    targets.map((item) =>
      fetchComments({
        targetType: item.targetType,
        targetId: item.targetId
      })
    )
  )

  comments.value = commentRows
    .flat()
    .filter((item) => item.userId === myUserId)
    .sort((a, b) => (a.createTime > b.createTime ? -1 : 1))
}

onMounted(async () => {
  loading.value = true
  try {
    if (!userStore.userId) {
      await userStore.fetchProfile().catch(() => null)
    }
    const favorites = await fetchMyFavorites()
    await loadCommentsByFavorites(favorites)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.time {
  color: var(--text-sub);
  font-size: 12px;
}

.content {
  margin: 0;
  line-height: 1.8;
  white-space: pre-wrap;
}
</style>
