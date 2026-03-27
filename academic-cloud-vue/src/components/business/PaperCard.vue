<template>
  <n-card hoverable class="paper-card" @click="openDetail">
    <n-space vertical :size="10">
      <n-space justify="space-between" align="center">
        <n-tag size="small" type="success">{{ categoryText }}</n-tag>
        <span class="time">{{ formatDateTime(paper.publishTime) }}</span>
      </n-space>
      <h3 class="title">{{ paper.title }}</h3>
      <p class="abstract">{{ ellipsis(paper.abstractText || '', 120) }}</p>
      <n-space justify="space-between" align="center">
        <span class="meta">{{ paper.authorInfo || '未知作者' }}</span>
        <n-space :size="12">
          <span class="meta">浏览 {{ paper.viewCount || 0 }}</span>
          <span class="meta">收藏 {{ paper.favoriteCount || 0 }}</span>
        </n-space>
      </n-space>
    </n-space>
  </n-card>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import type { PaperItem } from '@/types/content'
import { ellipsis, formatDateTime } from '@/utils/format'

const props = defineProps<{
  paper: PaperItem
  categoryName?: string
}>()

const router = useRouter()

const categoryText = computed(() => props.categoryName || `分类 #${props.paper.categoryId}`)

function openDetail() {
  router.push(`/papers/${props.paper.id}`)
}
</script>

<style scoped>
.paper-card {
  border-radius: 12px;
  cursor: pointer;
}

.title {
  margin: 0;
  font-size: 18px;
}

.abstract {
  margin: 0;
  line-height: 1.7;
  color: var(--text-sub);
}

.meta {
  font-size: 13px;
  color: var(--text-sub);
}

.time {
  font-size: 12px;
  color: var(--text-sub);
}
</style>
