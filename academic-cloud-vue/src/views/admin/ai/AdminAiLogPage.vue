<template>
  <n-space vertical :size="16">
    <page-header-card title="AI 调用日志" description="查看 AI 对话、摘要、关键词与推荐调用记录。" />

    <table-toolbar>
      <template #left>
        <n-select v-model:value="type" :options="typeOptions" style="width: 180px" />
        <n-input-number v-model:value="limit" :min="1" :max="200" style="width: 140px" />
        <n-button type="primary" @click="load">查询</n-button>
      </template>
    </table-toolbar>

    <n-card :bordered="false">
      <n-data-table :columns="columns" :data="rows" :loading="loading" :bordered="false" />
    </n-card>
  </n-space>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { DataTableColumns } from 'naive-ui'
import { fetchAdminAiLogs } from '@/api/ai'
import type { AdminAiLogItem } from '@/types/ai'
import { formatDateTime } from '@/utils/format'
import PageHeaderCard from '@/components/common/PageHeaderCard.vue'
import TableToolbar from '@/components/common/TableToolbar.vue'

const loading = ref(false)
const rows = ref<AdminAiLogItem[]>([])
const type = ref('ALL')
const limit = ref<number | null>(100)

const typeOptions = [
  { label: '全部', value: 'ALL' },
  { label: '对话', value: 'CHAT' },
  { label: '摘要', value: 'SUMMARY' },
  { label: '关键词', value: 'KEYWORD' },
  { label: '推荐', value: 'RECOMMEND' }
]

const columns: DataTableColumns<AdminAiLogItem> = [
  { title: '日志ID', key: 'id', width: 90 },
  { title: '用户ID', key: 'userId', width: 100 },
  { title: '类型', key: 'logType', width: 110 },
  { title: '触发文本', key: 'triggerText' },
  { title: '时间', key: 'createTime', width: 170, render: (row) => formatDateTime(row.createTime) }
]

async function load() {
  loading.value = true
  try {
    rows.value = await fetchAdminAiLogs({
      type: type.value,
      limit: Number(limit.value || 100)
    })
  } finally {
    loading.value = false
  }
}

load()
</script>
