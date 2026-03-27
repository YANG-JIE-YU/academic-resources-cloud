<template>
  <n-space vertical :size="16">
    <page-header-card title="公告管理" description="支持公告新增、编辑、删除和上下线管理。">
      <template #extra>
        <n-button type="primary" @click="openCreate">新增公告</n-button>
      </template>
    </page-header-card>

    <table-toolbar>
      <template #left>
        <n-input v-model:value="keyword" clearable placeholder="搜索公告标题" style="width: 260px" />
        <n-button type="primary" @click="load">查询</n-button>
      </template>
      <template #right>
        <n-button @click="reset">重置</n-button>
      </template>
    </table-toolbar>

    <n-card :bordered="false">
      <n-data-table :columns="columns" :data="rows" :loading="loading" :bordered="false" />
    </n-card>

    <n-modal
      v-model:show="modalVisible"
      preset="dialog"
      :title="editingId ? '编辑公告' : '新增公告'"
      positive-text="保存"
      negative-text="取消"
      @positive-click="submit"
    >
      <n-form ref="formRef" :model="form" :rules="rules" label-placement="top">
        <n-form-item label="标题" path="title">
          <n-input v-model:value="form.title" />
        </n-form-item>
        <n-form-item label="摘要" path="summary">
          <n-input v-model:value="form.summary" />
        </n-form-item>
        <n-form-item label="发布时间" path="publishTime">
          <n-date-picker v-model:formatted-value="form.publishTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" />
        </n-form-item>
        <n-form-item label="状态" path="status">
          <n-select v-model:value="form.status" :options="statusOptions" />
        </n-form-item>
        <n-form-item label="内容" path="content">
          <n-input v-model:value="form.content" type="textarea" :rows="6" />
        </n-form-item>
      </n-form>
    </n-modal>
  </n-space>
</template>

<script setup lang="ts">
import { h, reactive, ref } from 'vue'
import type { DataTableColumns, FormInst, FormRules } from 'naive-ui'
import { NButton, NPopconfirm, NTag, useMessage } from 'naive-ui'
import { adminApi } from '@/api/admin'
import type { NoticeItem, NoticeSaveRequest } from '@/types/content'
import { formatDateTime, toDateTimePickerValue } from '@/utils/format'
import PageHeaderCard from '@/components/common/PageHeaderCard.vue'
import TableToolbar from '@/components/common/TableToolbar.vue'

const message = useMessage()
const loading = ref(false)
const saving = ref(false)
const keyword = ref('')
const rows = ref<NoticeItem[]>([])
const modalVisible = ref(false)
const editingId = ref<number | null>(null)
const formRef = ref<FormInst | null>(null)

const form = reactive<NoticeSaveRequest>({
  title: '',
  summary: '',
  content: '',
  publishTime: '',
  status: 1
})

const statusOptions = [
  { label: '已发布', value: 1 },
  { label: '已下线', value: 0 }
]

const rules: FormRules = {
  title: [{ required: true, message: '请输入标题', trigger: ['blur', 'input'] }],
  content: [{ required: true, message: '请输入内容', trigger: ['blur', 'input'] }]
}

const columns: DataTableColumns<NoticeItem> = [
  { title: 'ID', key: 'id', width: 80 },
  { title: '标题', key: 'title' },
  { title: '摘要', key: 'summary' },
  {
    title: '状态',
    key: 'status',
    width: 100,
    render: (row) =>
      h(NTag, { type: Number(row.status) === 0 ? 'warning' : 'success', size: 'small' }, { default: () => (Number(row.status) === 0 ? '下线' : '发布') })
  },
  { title: '发布时间', key: 'publishTime', width: 170, render: (row) => formatDateTime(row.publishTime) },
  {
    title: '操作',
    key: 'actions',
    width: 170,
    render: (row) =>
      h('div', { style: 'display:flex;gap:8px;' }, [
        h(
          NButton,
          {
            size: 'small',
            quaternary: true,
            onClick: () => openEdit(row)
          },
          { default: () => '编辑' }
        ),
        h(
          NPopconfirm,
          {
            onPositiveClick: () => remove(row.id)
          },
          {
            trigger: () =>
              h(
                NButton,
                {
                  size: 'small',
                  quaternary: true,
                  type: 'error'
                },
                { default: () => '删除' }
              ),
            default: () => '确认删除该公告吗？'
          }
        )
      ])
  }
]

function openCreate() {
  editingId.value = null
  form.title = ''
  form.summary = ''
  form.content = ''
  form.publishTime = ''
  form.status = 1
  modalVisible.value = true
}

function openEdit(row: NoticeItem) {
  editingId.value = row.id
  form.title = row.title
  form.summary = row.summary || ''
  form.content = row.content || ''
  form.publishTime = toDateTimePickerValue(row.publishTime)
  form.status = Number(row.status ?? 1)
  modalVisible.value = true
}

async function submit() {
  try {
    await formRef.value?.validate()
  } catch {
    return false
  }
  if (saving.value) {
    return false
  }

  saving.value = true
  try {
    const payload: NoticeSaveRequest = {
      ...form,
      summary: form.summary?.trim() || undefined,
      publishTime: form.publishTime?.trim() || undefined
    }
    if (editingId.value) {
      await adminApi.updateNotice(editingId.value, payload)
      message.success('公告更新成功')
    } else {
      await adminApi.createNotice(payload)
      message.success('公告新增成功')
    }
    await load()
    return true
  } finally {
    saving.value = false
  }
}

async function remove(id: number) {
  await adminApi.deleteNotice(id)
  message.success('删除成功')
  await load()
}

async function load() {
  loading.value = true
  try {
    rows.value = await adminApi.fetchNotices(keyword.value.trim() || undefined)
  } finally {
    loading.value = false
  }
}

function reset() {
  keyword.value = ''
  load()
}

load()
</script>
