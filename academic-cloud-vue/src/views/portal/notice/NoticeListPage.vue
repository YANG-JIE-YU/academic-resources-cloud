<template>
  <div class="page-container">
    <n-space vertical :size="16">
      <page-header-card title="公告信息" description="了解系统更新、学术通知与专题公告。" />
      <n-grid cols="1 s:2 m:2 l:3" responsive="screen" :x-gap="16" :y-gap="16">
        <n-grid-item v-for="item in pageList" :key="item.id">
          <n-card hoverable class="notice-card" @click="router.push(`/notices/${item.id}`)">
            <n-space vertical :size="10">
              <h3 class="title">{{ item.title }}</h3>
              <p class="summary">{{ item.summary }}</p>
              <span class="time">{{ formatDateTime(item.publishTime) }}</span>
            </n-space>
          </n-card>
        </n-grid-item>
      </n-grid>
      <n-pagination
        v-model:page="pager.current"
        v-model:page-size="pager.size"
        :item-count="notices.length"
        show-size-picker
        :page-sizes="[6, 9, 12]"
      />
    </n-space>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { fetchNoticeList } from '@/api/content'
import type { NoticeItem } from '@/types/content'
import { formatDateTime } from '@/utils/format'
import PageHeaderCard from '@/components/common/PageHeaderCard.vue'

const router = useRouter()
const notices = ref<NoticeItem[]>([])
const pager = reactive({
  current: 1,
  size: 6
})

const pageList = computed(() => {
  const start = (pager.current - 1) * pager.size
  return notices.value.slice(start, start + pager.size)
})

onMounted(async () => {
  notices.value = await fetchNoticeList()
})
</script>

<style scoped>
.notice-card {
  height: 100%;
  border-radius: 12px;
  cursor: pointer;
}

.title {
  margin: 0;
  font-size: 18px;
}

.summary {
  margin: 0;
  color: var(--text-sub);
  line-height: 1.7;
}

.time {
  color: var(--text-sub);
  font-size: 12px;
}
</style>
