<template>
  <n-space vertical :size="16">
    <page-header-card title="管理工作台" description="集中查看系统运行概况与模块入口。" />

    <n-grid cols="1 s:2 m:4" responsive="screen" :x-gap="16" :y-gap="16">
      <n-grid-item>
        <n-card :bordered="false">
          <n-statistic label="用户总数" :value="stats.userCount" />
        </n-card>
      </n-grid-item>
      <n-grid-item>
        <n-card :bordered="false">
          <n-statistic label="论文总数" :value="stats.paperCount" />
        </n-card>
      </n-grid-item>
      <n-grid-item>
        <n-card :bordered="false">
          <n-statistic label="公告总数" :value="stats.noticeCount" />
        </n-card>
      </n-grid-item>
      <n-grid-item>
        <n-card :bordered="false">
          <n-statistic label="活动总数" :value="stats.activityCount" />
        </n-card>
      </n-grid-item>
    </n-grid>

    <n-grid cols="1 m:2" responsive="screen" :x-gap="16" :y-gap="16">
      <n-grid-item>
        <n-card title="系统说明" :bordered="false">
          <n-space vertical>
            <p class="info">后端由 user-service、paper-service、ai-service 组成，通过 gateway 统一入口。</p>
            <p class="info">前端单仓库承载门户端和管理端，便于毕业设计演示与答辩。</p>
          </n-space>
        </n-card>
      </n-grid-item>
      <n-grid-item>
        <n-card title="快捷操作" :bordered="false">
          <n-space vertical>
            <n-button block @click="$router.push('/admin/users')">进入用户管理</n-button>
            <n-button block @click="$router.push('/admin/papers')">进入论文管理</n-button>
            <n-button block @click="$router.push('/admin/categories')">进入分类管理</n-button>
          </n-space>
        </n-card>
      </n-grid-item>
    </n-grid>
  </n-space>
</template>

<script setup lang="ts">
import { onMounted, reactive } from 'vue'
import { adminApi } from '@/api/admin'
import PageHeaderCard from '@/components/common/PageHeaderCard.vue'

const stats = reactive({
  userCount: 0,
  paperCount: 0,
  noticeCount: 0,
  activityCount: 0
})

onMounted(async () => {
  const [users, papers, notices, activities] = await Promise.all([
    adminApi.fetchUsers({ current: 1, size: 1 }).catch(() => ({ total: 0 })),
    adminApi.fetchPapers({ current: 1, size: 1 }).catch(() => ({ total: 0 })),
    adminApi.fetchNotices().catch(() => []),
    adminApi.fetchActivities().catch(() => [])
  ])

  stats.userCount = users.total || 0
  stats.paperCount = papers.total || 0
  stats.noticeCount = notices.length || 0
  stats.activityCount = activities.length || 0
})
</script>

<style scoped>
.info {
  margin: 0;
  line-height: 1.8;
  color: var(--text-sub);
}
</style>
