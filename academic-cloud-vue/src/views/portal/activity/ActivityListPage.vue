<template>
  <div class="page-container">
    <n-space vertical :size="16">
      <page-header-card title="学术活动" description="查看近期论坛、讲座与训练营安排。" />
      <n-grid cols="1 s:2 l:3" responsive="screen" :x-gap="16" :y-gap="16">
        <n-grid-item v-for="item in pageList" :key="item.id">
          <n-card hoverable class="activity-card" @click="router.push(`/activities/${item.id}`)">
            <n-space vertical :size="10">
              <h3 class="title">{{ item.title }}</h3>
              <n-space size="small">
                <n-tag type="success" size="small">{{ item.organizer }}</n-tag>
                <n-tag size="small">{{ item.location }}</n-tag>
              </n-space>
              <p class="time">{{ formatDateTime(item.startTime) }} - {{ formatDateTime(item.endTime) }}</p>
              <p class="summary">{{ item.content }}</p>
            </n-space>
          </n-card>
        </n-grid-item>
      </n-grid>
      <n-pagination
        v-model:page="pager.current"
        v-model:page-size="pager.size"
        :item-count="activities.length"
        show-size-picker
        :page-sizes="[6, 9, 12]"
      />
    </n-space>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { fetchActivityList } from '@/api/content'
import type { ActivityItem } from '@/types/content'
import { formatDateTime } from '@/utils/format'
import PageHeaderCard from '@/components/common/PageHeaderCard.vue'

const router = useRouter()
const activities = ref<ActivityItem[]>([])
const pager = reactive({
  current: 1,
  size: 6
})

const pageList = computed(() => {
  const start = (pager.current - 1) * pager.size
  return activities.value.slice(start, start + pager.size)
})

onMounted(async () => {
  activities.value = await fetchActivityList()
})
</script>

<style scoped>
.activity-card {
  border-radius: 12px;
  height: 100%;
  cursor: pointer;
}

.title {
  margin: 0;
  font-size: 18px;
}

.time {
  margin: 0;
  font-size: 13px;
  color: var(--text-sub);
}

.summary {
  margin: 0;
  color: var(--text-sub);
  line-height: 1.7;
}
</style>
