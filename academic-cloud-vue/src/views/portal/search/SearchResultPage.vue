<template>
  <div class="page-container">
    <n-space vertical :size="16">
      <page-header-card :title="`搜索结果：${keyword || '全部'}`" description="按关键词匹配论文标题与关键词字段。" />
      <n-card :bordered="false">
        <n-space align="center">
          <n-input v-model:value="keyword" clearable placeholder="输入关键词继续检索" @keyup.enter="search" />
          <n-button type="primary" @click="search">搜索</n-button>
        </n-space>
      </n-card>
      <n-spin :show="loading">
        <n-grid cols="1 s:2 l:3" responsive="screen" :x-gap="14" :y-gap="14">
          <n-grid-item v-for="item in list" :key="item.id">
            <paper-card :paper="item" :category-name="categoryMap[item.categoryId] || ''" />
          </n-grid-item>
        </n-grid>
        <n-empty v-if="!loading && list.length === 0" description="没有匹配的论文" />
      </n-spin>
    </n-space>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { fetchPaperCategories, fetchPaperPage } from '@/api/content'
import type { PaperCategory, PaperItem } from '@/types/content'
import PageHeaderCard from '@/components/common/PageHeaderCard.vue'
import PaperCard from '@/components/business/PaperCard.vue'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const keyword = ref('')
const list = ref<PaperItem[]>([])
const categories = ref<PaperCategory[]>([])

const categoryMap = computed<Record<number, string>>(() =>
  categories.value.reduce<Record<number, string>>((acc, item) => {
    acc[item.id] = item.name
    return acc
  }, {})
)

async function load() {
  loading.value = true
  try {
    const res = await fetchPaperPage({
      current: 1,
      size: 18,
      keyword: keyword.value.trim()
    })
    list.value = res.records || []
  } finally {
    loading.value = false
  }
}

function search() {
  router.replace({
    path: '/search',
    query: {
      keyword: keyword.value.trim()
    }
  })
  load()
}

onMounted(async () => {
  keyword.value = typeof route.query.keyword === 'string' ? route.query.keyword : ''
  categories.value = await fetchPaperCategories()
  await load()
})
</script>
