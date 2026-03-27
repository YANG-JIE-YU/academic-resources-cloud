<template>
  <n-space vertical :size="16">
    <page-header-card title="分类标签管理" description="支持分类新增、编辑、删除和状态管理。" />

    <n-card :bordered="false">
      <n-grid cols="1 m:5" :x-gap="12">
        <n-grid-item :span="2">
          <n-input v-model:value="form.name" placeholder="分类名称" />
        </n-grid-item>
        <n-grid-item>
          <n-input-number v-model:value="form.sortNo" :min="0" placeholder="排序" style="width: 100%" />
        </n-grid-item>
        <n-grid-item>
          <n-select v-model:value="form.status" :options="statusOptions" />
        </n-grid-item>
        <n-grid-item>
          <n-space style="width: 100%" justify="end">
            <n-button @click="resetForm">重置</n-button>
            <n-button type="primary" block :loading="saving" @click="submit">{{ editingId ? '更新分类' : '新增分类' }}</n-button>
          </n-space>
        </n-grid-item>
      </n-grid>
    </n-card>

    <n-card :bordered="false">
      <n-data-table :columns="columns" :data="rows" :loading="loading" :bordered="false" />
    </n-card>
  </n-space>
</template>

<script setup lang="ts">
import { h, onMounted, reactive, ref } from 'vue'
import type { DataTableColumns } from 'naive-ui'
import { NButton, NPopconfirm, NTag, useMessage } from 'naive-ui'
import { adminApi } from '@/api/admin'
import type { PaperCategory } from '@/types/content'
import { formatDateTime } from '@/utils/format'
import PageHeaderCard from '@/components/common/PageHeaderCard.vue'

const message = useMessage()
const loading = ref(false)
const saving = ref(false)
const rows = ref<PaperCategory[]>([])
const editingId = ref<number | null>(null)

const form = reactive({
  name: '',
  sortNo: 0,
  status: 1
})

const statusOptions = [
  { label: '启用', value: 1 },
  { label: '停用', value: 0 }
]

const columns: DataTableColumns<PaperCategory> = [
  { title: 'ID', key: 'id', width: 80 },
  { title: '分类名称', key: 'name' },
  { title: '排序', key: 'sortNo', width: 100 },
  {
    title: '状态',
    key: 'status',
    width: 100,
    render: (row) =>
      h(
        NTag,
        { type: Number(row.status) === 0 ? 'warning' : 'success', size: 'small' },
        { default: () => (Number(row.status) === 0 ? '停用' : '启用') }
      )
  },
  {
    title: '创建时间',
    key: 'createTime',
    width: 180,
    render: (row) => formatDateTime(row.createTime)
  },
  {
    title: '操作',
    key: 'actions',
    width: 180,
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
          { onPositiveClick: () => remove(row.id) },
          {
            trigger: () =>
              h(
                NButton,
                {
                  size: 'small',
                  type: 'error',
                  quaternary: true
                },
                { default: () => '删除' }
              ),
            default: () => '确认删除该分类吗？'
          }
        )
      ])
  }
]

async function load() {
  loading.value = true
  try {
    rows.value = await adminApi.fetchCategories()
  } finally {
    loading.value = false
  }
}

function openEdit(row: PaperCategory) {
  editingId.value = row.id
  form.name = row.name
  form.sortNo = Number(row.sortNo ?? 0)
  form.status = Number(row.status ?? 1)
}

async function submit() {
  if (!form.name.trim()) {
    message.warning('请输入分类名称')
    return
  }
  saving.value = true
  try {
    if (editingId.value) {
      await adminApi.updateCategory(editingId.value, {
        name: form.name.trim(),
        sortNo: form.sortNo,
        status: form.status
      })
      message.success('分类更新成功')
    } else {
      await adminApi.createCategory({
        name: form.name.trim(),
        sortNo: form.sortNo,
        status: form.status
      })
      message.success('分类新增成功')
    }
    resetForm()
    await load()
  } finally {
    saving.value = false
  }
}

async function remove(id: number) {
  await adminApi.deleteCategory(id)
  message.success('删除成功')
  await load()
}

function resetForm() {
  editingId.value = null
  form.name = ''
  form.sortNo = 0
  form.status = 1
}

onMounted(() => {
  load()
})
</script>
