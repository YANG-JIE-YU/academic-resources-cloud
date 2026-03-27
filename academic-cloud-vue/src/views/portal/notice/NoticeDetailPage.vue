<template>
  <div class="page-container">
    <n-space vertical :size="16">
      <n-button text @click="$router.push('/notices')">返回公告列表</n-button>
      <n-card :bordered="false">
        <n-space vertical :size="14">
          <h1 class="title">{{ notice?.title }}</h1>
          <n-tag type="info" size="small">{{ formatDateTime(notice?.publishTime) }}</n-tag>
          <p class="content">{{ notice?.content }}</p>
        </n-space>
      </n-card>
    </n-space>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { fetchNoticeDetail } from '@/api/content'
import type { NoticeItem } from '@/types/content'
import { formatDateTime } from '@/utils/format'

const route = useRoute()
const notice = ref<NoticeItem>()

onMounted(async () => {
  const id = Number(route.params.id)
  notice.value = await fetchNoticeDetail(id)
})
</script>

<style scoped>
.title {
  margin: 0;
  font-size: 32px;
}

.content {
  margin: 0;
  line-height: 1.9;
  white-space: pre-wrap;
}
</style>
