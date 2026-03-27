<template>
  <n-space vertical :size="16">
    <page-header-card title="活动管理" description="支持活动新增、编辑、删除和上下线管理。">
      <template #extra>
        <n-button type="primary" @click="openCreate">新增活动</n-button>
      </template>
    </page-header-card>

    <table-toolbar>
      <template #left>
        <n-input v-model:value="keyword" clearable placeholder="搜索活动标题" style="width: 260px" />
        <n-button type="primary" @click="load">查询</n-button>
      </template>
      <template #right>
        <n-button @click="reset">重置</n-button>
      </template>
    </table-toolbar>

    <n-card :bordered="false">
      <n-data-table :columns="columns" :data="rows" :loading="loading" :bordered="false" />
    </n-card>

    <n-drawer v-model:show="drawerVisible" :width="560">
      <n-drawer-content :title="editingId ? '编辑活动' : '新增活动'" closable>
        <n-form ref="formRef" :model="form" :rules="rules" label-placement="top">
          <n-form-item label="活动标题" path="title">
            <n-input v-model:value="form.title" />
          </n-form-item>
          <n-form-item label="举办单位" path="organizer">
            <n-input v-model:value="form.organizer" />
          </n-form-item>
          <n-form-item label="活动地点" path="location">
            <n-input v-model:value="form.location" />
          </n-form-item>
          <n-grid :cols="2" :x-gap="12">
            <n-form-item-gi label="开始时间" path="startTime">
              <n-date-picker v-model:formatted-value="form.startTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" />
            </n-form-item-gi>
            <n-form-item-gi label="结束时间" path="endTime">
              <n-date-picker v-model:formatted-value="form.endTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" />
            </n-form-item-gi>
          </n-grid>
          <n-form-item label="状态" path="status">
            <n-select v-model:value="form.status" :options="statusOptions" />
          </n-form-item>
          <n-form-item label="活动内容" path="content">
            <n-input v-model:value="form.content" type="textarea" :rows="6" />
          </n-form-item>
        </n-form>
        <template #footer>
          <n-space justify="end">
            <n-button @click="drawerVisible = false">取消</n-button>
            <n-button type="primary" :loading="saving" @click="submit">保存</n-button>
          </n-space>
        </template>
      </n-drawer-content>
    </n-drawer>
  </n-space>
</template>

<script setup lang="ts">
import { h, reactive, ref } from 'vue'
import type { DataTableColumns, FormInst, FormRules } from 'naive-ui'
import { NButton, NPopconfirm, NTag, useMessage } from 'naive-ui'
import { adminApi } from '@/api/admin'
import type { ActivityItem, ActivitySaveRequest } from '@/types/content'
import { formatDateTime, toDateTimePickerValue } from '@/utils/format'
import PageHeaderCard from '@/components/common/PageHeaderCard.vue'
import TableToolbar from '@/components/common/TableToolbar.vue'

const message = useMessage()
const loading = ref(false)
const saving = ref(false)
const keyword = ref('')
const rows = ref<ActivityItem[]>([])
const drawerVisible = ref(false)
const editingId = ref<number | null>(null)
const formRef = ref<FormInst | null>(null)

const form = reactive<ActivitySaveRequest>({
  title: '',
  location: '',
  organizer: '',
  startTime: '',
  endTime: '',
  content: '',
  status: 1
})

const statusOptions = [
  { label: '已发布', value: 1 },
  { label: '已下线', value: 0 }
]

const rules: FormRules = {
  title: [{ required: true, message: '请输入活动标题', trigger: ['blur', 'input'] }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  content: [{ required: true, message: '请输入活动内容', trigger: ['blur', 'input'] }]
}

const columns: DataTableColumns<ActivityItem> = [
  { title: 'ID', key: 'id', width: 80 },
  { title: '标题', key: 'title' },
  { title: '举办单位', key: 'organizer', width: 140 },
  { title: '地点', key: 'location', width: 140 },
  {
    title: '状态',
    key: 'status',
    width: 100,
    render: (row) =>
      h(NTag, { type: Number(row.status) === 0 ? 'warning' : 'success', size: 'small' }, { default: () => (Number(row.status) === 0 ? '下线' : '发布') })
  },
  {
    title: '活动时间',
    key: 'startTime',
    width: 220,
    render: (row) => `${formatDateTime(row.startTime)} ~ ${formatDateTime(row.endTime)}`
  },
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
                  type: 'error',
                  quaternary: true
                },
                { default: () => '删除' }
              ),
            default: () => '确认删除该活动吗？'
          }
        )
      ])
  }
]

function openCreate() {
  editingId.value = null
  form.title = ''
  form.location = ''
  form.organizer = ''
  form.startTime = ''
  form.endTime = ''
  form.content = ''
  form.status = 1
  drawerVisible.value = true
}

function openEdit(row: ActivityItem) {
  editingId.value = row.id
  form.title = row.title
  form.location = row.location || ''
  form.organizer = row.organizer || ''
  form.startTime = toDateTimePickerValue(row.startTime)
  form.endTime = toDateTimePickerValue(row.endTime)
  form.content = row.content || ''
  form.status = Number(row.status ?? 1)
  drawerVisible.value = true
}

async function submit() {
  try {
    await formRef.value?.validate()
  } catch {
    return
  }
  if (saving.value) {
    return
  }
  saving.value = true
  try {
    const payload: ActivitySaveRequest = {
      ...form,
      location: form.location?.trim() || undefined,
      organizer: form.organizer?.trim() || undefined,
      startTime: form.startTime?.trim() || '',
      endTime: form.endTime?.trim() || ''
    }
    if (editingId.value) {
      await adminApi.updateActivity(editingId.value, payload)
      message.success('活动更新成功')
    } else {
      await adminApi.createActivity(payload)
      message.success('活动新增成功')
    }
    drawerVisible.value = false
    await load()
  } finally {
    saving.value = false
  }
}

async function remove(id: number) {
  await adminApi.deleteActivity(id)
  message.success('删除成功')
  await load()
}

async function load() {
  loading.value = true
  try {
    rows.value = await adminApi.fetchActivities(keyword.value.trim() || undefined)
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
