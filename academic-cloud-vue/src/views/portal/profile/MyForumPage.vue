<template>
  <div class="page-container">
    <n-space vertical :size="16">
      <page-header-card title="我的论坛记录" description="查看你发布的帖子和回复内容。">
        <template #extra>
          <n-space>
            <n-button @click="$router.push('/forum')">前往论坛</n-button>
            <n-button @click="$router.push('/profile')">返回个人中心</n-button>
          </n-space>
        </template>
      </page-header-card>

      <n-grid cols="1 m:2" responsive="screen" :x-gap="16" :y-gap="16">
        <n-grid-item>
          <n-card title="我发布的帖子" :bordered="false">
            <n-spin :show="loading">
              <n-space v-if="myPosts.length > 0" vertical :size="10">
                <n-card v-for="item in myPosts" :key="item.id" embedded>
                  <n-space vertical :size="6">
                    <strong>{{ item.title || '无标题' }}</strong>
                    <p class="content">{{ item.content }}</p>
                    <span class="time">{{ formatDateTime(item.createTime) }}</span>
                  </n-space>
                </n-card>
              </n-space>
              <n-empty v-else description="暂无发帖记录" />
            </n-spin>
          </n-card>
        </n-grid-item>

        <n-grid-item>
          <n-card title="我的回复" :bordered="false">
            <n-spin :show="loading">
              <n-space v-if="myReplies.length > 0" vertical :size="10">
                <n-card v-for="item in myReplies" :key="item.id" embedded>
                  <n-space vertical :size="6">
                    <p class="content">{{ item.content }}</p>
                    <span class="time">{{ formatDateTime(item.createTime) }}</span>
                  </n-space>
                </n-card>
              </n-space>
              <n-empty v-else description="暂无回复记录" />
            </n-spin>
          </n-card>
        </n-grid-item>
      </n-grid>
    </n-space>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import type { ForumPostItem } from '@/types/user'
import { fetchForumPosts, fetchForumReplies } from '@/api/user'
import { useUserStore } from '@/stores/user'
import { formatDateTime } from '@/utils/format'
import PageHeaderCard from '@/components/common/PageHeaderCard.vue'

const userStore = useUserStore()
const loading = ref(false)
const myPosts = ref<ForumPostItem[]>([])
const myReplies = ref<ForumPostItem[]>([])

onMounted(async () => {
  loading.value = true
  try {
    if (!userStore.userId) {
      await userStore.fetchProfile().catch(() => null)
    }
    const userId = userStore.userId
    const posts = await fetchForumPosts()
    myPosts.value = posts.filter((item) => item.userId === userId)

    const repliesGroup = await Promise.all(posts.slice(0, 30).map((item) => fetchForumReplies(item.id)))
    myReplies.value = repliesGroup.flat().filter((item) => item.userId === userId)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.content {
  margin: 0;
  line-height: 1.7;
  white-space: pre-wrap;
}

.time {
  font-size: 12px;
  color: var(--text-sub);
}
</style>
