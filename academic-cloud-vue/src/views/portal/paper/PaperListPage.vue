<template>
  <div class="page-container">
    <n-space vertical :size="16">
      <page-header-card title="论文资源库" description="支持关键词检索、分类筛选、排序与分页浏览。">
        <template #extra>
          <n-button tertiary @click="resetFilters">重置筛选</n-button>
        </template>
      </page-header-card>

      <n-card :bordered="false">
        <n-grid cols="1 m:4" responsive="screen" :x-gap="12" :y-gap="12">
          <n-grid-item>
            <n-input v-model:value="query.keyword" clearable placeholder="输入论文标题或关键词" @keyup.enter="loadPapers" />
          </n-grid-item>
          <n-grid-item>
            <n-select
              v-model:value="query.categoryId"
              clearable
              :options="categoryOptions"
              placeholder="按分类筛选"
              @update:value="loadPapers"
            />
          </n-grid-item>
          <n-grid-item>
            <n-select
              v-model:value="query.sort"
              :options="sortOptions"
              placeholder="排序方式"
              @update:value="loadPapers"
            />
          </n-grid-item>
          <n-grid-item>
            <n-button type="primary" block @click="loadPapers">查询</n-button>
          </n-grid-item>
        </n-grid>
      </n-card>

      <n-spin :show="loading">
        <n-grid cols="1 s:2 l:3" responsive="screen" :x-gap="14" :y-gap="14">
          <n-grid-item v-for="paper in renderedList" :key="paper.id">
            <paper-card :paper="paper" :category-name="categoryMap[paper.categoryId] || ''" />
          </n-grid-item>
        </n-grid>
        <n-empty v-if="!loading && renderedList.length === 0" description="暂无数据" />
      </n-spin>

      <n-pagination
        v-model:page="query.current"
        v-model:page-size="query.size"
        :item-count="total"
        show-size-picker
        :page-sizes="[6, 9, 12]"
        @update:page="loadPapers"
        @update:page-size="loadPapers"
      />
    </n-space>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { fetchPaperCategories, fetchPaperPage } from '@/api/content'
import type { PaperCategory, PaperItem } from '@/types/content'
import PageHeaderCard from '@/components/common/PageHeaderCard.vue'
import PaperCard from '@/components/business/PaperCard.vue'

const route = useRoute()

const loading = ref(false)
const total = ref(0)
const rawList = ref<PaperItem[]>([])
const categories = ref<PaperCategory[]>([])

const query = reactive({
  current: 1,
  size: 9,
  keyword: '',
  categoryId: null as number | null,
  sort: 'latest'
})

const sortOptions = [
  { label: '最新发布', value: 'latest' },
  { label: '浏览量优先', value: 'view' },
  { label: '收藏量优先', value: 'favorite' }
]

const categoryOptions = computed(() =>
  categories.value.map((item) => ({
    label: item.name,
    value: item.id
  }))
)

const categoryMap = computed<Record<number, string>>(() =>
  categories.value.reduce<Record<number, string>>((acc, item) => {
    acc[item.id] = item.name
    return acc
  }, {})
)

const renderedList = computed(() => {
  const list = [...rawList.value]
  if (query.sort === 'view') {
    return list.sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0))
  }
  if (query.sort === 'favorite') {
    return list.sort((a, b) => (b.favoriteCount || 0) - (a.favoriteCount || 0))
  }
  return list
})

function resetFilters() {
  query.current = 1
  query.keyword = ''
  query.categoryId = null
  query.sort = 'latest'
  loadPapers()
}

async function loadCategories() {
  categories.value = await fetchPaperCategories()
}

async function loadPapers() {
  loading.value = true
  try {
    const page = await fetchPaperPage({
      current: query.current,
      size: query.size,
      keyword: query.keyword.trim(),
      categoryId: query.categoryId ?? undefined
    })
    rawList.value = page.records || []
    total.value = page.total || 0
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  if (typeof route.query.keyword === 'string') {
    query.keyword = route.query.keyword
  }
  await Promise.all([loadCategories(), loadPapers()])
})

watch(
  () => route.query.keyword,
  (value) => {
    if (typeof value === 'string') {
      query.keyword = value
      query.current = 1
      loadPapers()
    }
  }
)
</script>
