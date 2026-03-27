<template>
  <div class="page-container">
    <n-space vertical :size="16">
      <n-button text @click="$router.push('/activities')">返回活动列表</n-button>
      <n-card :bordered="false">
        <n-space vertical :size="14">
          <h1 class="title">{{ detail?.title }}</h1>
          <n-space>
            <n-tag type="success">{{ detail?.organizer }}</n-tag>
            <n-tag>{{ detail?.location }}</n-tag>
          </n-space>
          <p class="time">{{ formatDateTime(detail?.startTime) }} - {{ formatDateTime(detail?.endTime) }}</p>
          <p class="content">{{ detail?.content }}</p>
        </n-space>
      </n-card>
    </n-space>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { fetchActivityDetail } from '@/api/content'
import type { ActivityItem } from '@/types/content'
import { formatDateTime } from '@/utils/format'

const route = useRoute()
const detail = ref<ActivityItem>()

onMounted(async () => {
  detail.value = await fetchActivityDetail(Number(route.params.id))
})
</script>

<style scoped>
.title {
  margin: 0;
  font-size: 30px;
}

.time {
  margin: 0;
  color: var(--text-sub);
}

.content {
  margin: 0;
  line-height: 1.9;
}
</style>
