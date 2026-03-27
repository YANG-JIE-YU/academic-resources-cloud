<template>
  <div class="page-container">
    <n-space vertical :size="16">
      <n-button text @click="$router.push('/papers')">返回论文列表</n-button>

      <n-card :bordered="false">
        <n-space vertical :size="14">
          <h1 class="title">{{ detail?.title }}</h1>
          <n-space>
            <n-tag type="success">{{ categoryName }}</n-tag>
            <n-tag>{{ detail?.authorInfo || '未知作者' }}</n-tag>
            <n-tag>{{ formatDateTime(detail?.publishTime) }}</n-tag>
          </n-space>
          <p class="abstract"><strong>摘要：</strong>{{ detail?.abstractText }}</p>
          <p class="keywords"><strong>关键词：</strong>{{ detail?.keywords }}</p>

          <n-space>
            <n-button type="primary" :loading="favoriteLoading" @click="favoriteCurrentPaper">收藏论文</n-button>
            <n-button tertiary @click="$router.push('/ai/summary')">AI 摘要</n-button>
            <n-button tertiary @click="$router.push('/ai/keywords')">AI 关键词</n-button>
          </n-space>

          <n-divider />
          <div class="content">{{ detail?.content }}</div>
        </n-space>
      </n-card>

      <comment-panel v-if="detail" target-type="PAPER" :target-id="detail.id" />

      <n-card title="相关推荐" :bordered="false">
        <n-grid cols="1 s:2 l:3" responsive="screen" :x-gap="14" :y-gap="14">
          <n-grid-item v-for="item in relatedList" :key="item.id">
            <paper-card :paper="item" :category-name="categoryMap[item.categoryId] || ''" />
          </n-grid-item>
        </n-grid>
      </n-card>
    </n-space>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useMessage } from 'naive-ui'
import { useRoute, useRouter } from 'vue-router'
import { addFavorite } from '@/api/user'
import { fetchPaperCategories, fetchPaperDetail, fetchPaperPage } from '@/api/content'
import type { PaperCategory, PaperItem } from '@/types/content'
import { formatDateTime } from '@/utils/format'
import { useUserStore } from '@/stores/user'
import PaperCard from '@/components/business/PaperCard.vue'
import CommentPanel from '@/components/business/CommentPanel.vue'

const route = useRoute()
const router = useRouter()
const message = useMessage()
const userStore = useUserStore()

const detail = ref<PaperItem>()
const relatedList = ref<PaperItem[]>([])
const categories = ref<PaperCategory[]>([])
const favoriteLoading = ref(false)

const categoryMap = computed<Record<number, string>>(() =>
  categories.value.reduce<Record<number, string>>((acc, item) => {
    acc[item.id] = item.name
    return acc
  }, {})
)

const categoryName = computed(() => {
  if (!detail.value) {
    return '未分类'
  }
  return categoryMap.value[detail.value.categoryId] || `分类 #${detail.value.categoryId}`
})

async function loadDetail() {
  const id = Number(route.params.id)
  detail.value = await fetchPaperDetail(id)
}

async function loadRelated() {
  if (!detail.value) {
    relatedList.value = []
    return
  }
  const data = await fetchPaperPage({
    current: 1,
    size: 6,
    categoryId: detail.value.categoryId
  })
  relatedList.value = (data.records || []).filter((item) => item.id !== detail.value?.id).slice(0, 3)
}

async function favoriteCurrentPaper() {
  if (!detail.value) {
    return
  }
  if (!userStore.isLoggedIn) {
    message.warning('请先登录后再收藏')
    router.push('/auth/login')
    return
  }
  favoriteLoading.value = true
  try {
    await addFavorite({
      targetType: 'PAPER',
      targetId: detail.value.id,
      targetTitle: detail.value.title,
      targetCover: detail.value.coverUrl
    })
    message.success('收藏成功')
  } finally {
    favoriteLoading.value = false
  }
}

onMounted(async () => {
  categories.value = await fetchPaperCategories()
  await loadDetail()
  await loadRelated()
})
</script>

<style scoped>
.title {
  margin: 0;
  font-size: 32px;
}

.abstract,
.keywords {
  margin: 0;
  line-height: 1.8;
  color: #334155;
}

.content {
  line-height: 1.9;
  white-space: pre-wrap;
  color: #1f2a37;
}
</style>
