<template>
  <div class="page-container">
    <n-space vertical :size="16">
      <page-header-card title="学术资源门户首页" description="聚合论文、公告、活动与 AI 功能，面向毕业设计答辩演示。">
        <template #extra>
          <n-space>
            <n-button type="primary" @click="router.push('/papers')">浏览论文库</n-button>
            <n-button tertiary @click="router.push('/ai/chat')">进入 AI 助手</n-button>
          </n-space>
        </template>
      </page-header-card>

      <n-grid cols="1 s:2 m:3" responsive="screen" :x-gap="16" :y-gap="16">
        <n-grid-item>
          <n-card title="公告推荐" :bordered="false">
            <n-space vertical :size="8">
              <div v-for="notice in notices.slice(0, 3)" :key="notice.id" class="link-row" @click="router.push(`/notices/${notice.id}`)">
                <span class="link-title">{{ notice.title }}</span>
              </div>
            </n-space>
          </n-card>
        </n-grid-item>
        <n-grid-item>
          <n-card title="学术活动" :bordered="false">
            <n-space vertical :size="8">
              <div
                v-for="activity in activities.slice(0, 3)"
                :key="activity.id"
                class="link-row"
                @click="router.push(`/activities/${activity.id}`)"
              >
                <span class="link-title">{{ activity.title }}</span>
              </div>
            </n-space>
          </n-card>
        </n-grid-item>
        <n-grid-item>
          <n-card title="智能推荐入口" :bordered="false">
            <n-space vertical :size="10">
              <p class="sub-text">基于搜索词和用户偏好推荐论文资源。</p>
              <n-button block type="success" @click="router.push('/ai/recommend')">查看智能推荐</n-button>
            </n-space>
          </n-card>
        </n-grid-item>
      </n-grid>

      <n-card title="热门论文资源" :bordered="false">
        <n-spin :show="loading">
          <n-grid cols="1 s:2 m:2 l:3" responsive="screen" :x-gap="14" :y-gap="14">
            <n-grid-item v-for="paper in papers" :key="paper.id">
              <paper-card :paper="paper" :category-name="categoryMap[paper.categoryId] || ''" />
            </n-grid-item>
          </n-grid>
        </n-spin>
      </n-card>
    </n-space>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { fetchActivityList, fetchNoticeList, fetchPaperCategories, fetchPaperPage } from '@/api/content'
import type { ActivityItem, NoticeItem, PaperCategory, PaperItem } from '@/types/content'
import PageHeaderCard from '@/components/common/PageHeaderCard.vue'
import PaperCard from '@/components/business/PaperCard.vue'

const router = useRouter()
const loading = ref(false)
const notices = ref<NoticeItem[]>([])
const activities = ref<ActivityItem[]>([])
const papers = ref<PaperItem[]>([])
const categories = ref<PaperCategory[]>([])

const categoryMap = computed<Record<number, string>>(() => {
  return categories.value.reduce<Record<number, string>>((acc, item) => {
    acc[item.id] = item.name
    return acc
  }, {})
})

async function loadData() {
  loading.value = true
  try {
    const [noticeData, activityData, categoryData, paperData] = await Promise.all([
      fetchNoticeList(),
      fetchActivityList(),
      fetchPaperCategories(),
      fetchPaperPage({ current: 1, size: 6 })
    ])
    notices.value = noticeData
    activities.value = activityData
    categories.value = categoryData
    papers.value = paperData.records || []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData().catch(() => {
    // 某个服务未就绪时保持页面稳定
  })
})
</script>

<style scoped>
.link-row {
  padding: 8px 10px;
  border-radius: 8px;
  background: #f6f8fc;
  cursor: pointer;
}

.link-title {
  color: #2a3a54;
  font-size: 14px;
}

.sub-text {
  margin: 0;
  color: var(--text-sub);
}
</style>
