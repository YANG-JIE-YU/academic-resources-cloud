<template>
  <n-space vertical :size="16">
    <page-header-card title="论文管理" description="支持论文资源新增、编辑、删除与检索。">
      <template #extra>
        <n-button type="primary" @click="openCreate">新增论文</n-button>
      </template>
    </page-header-card>

    <table-toolbar>
      <template #left>
        <n-input v-model:value="keyword" clearable placeholder="搜索标题/关键词" style="width: 260px" />
        <n-select v-model:value="categoryId" clearable :options="categoryOptions" placeholder="按分类筛选" style="width: 180px" />
        <n-button type="primary" @click="load">查询</n-button>
      </template>
      <template #right>
        <n-button @click="reset">重置</n-button>
      </template>
    </table-toolbar>

    <n-card :bordered="false">
      <n-data-table :columns="columns" :data="rows" :loading="loading" :bordered="false" />
      <n-pagination
        v-model:page="pager.current"
        v-model:page-size="pager.size"
        :item-count="pager.total"
        show-size-picker
        :page-sizes="[10, 20, 30]"
        style="margin-top: 16px"
        @update:page="load"
        @update:page-size="load"
      />
    </n-card>

    <n-drawer v-model:show="drawerVisible" :width="620" :mask-closable="false">
      <n-drawer-content :title="editingId ? '编辑论文' : '新增论文'" closable>
        <n-form ref="formRef" :model="form" :rules="rules" label-placement="top">
          <n-form-item label="论文标题" path="title">
            <n-input v-model:value="form.title" />
          </n-form-item>
          <n-form-item label="论文摘要" path="abstractText">
            <n-input v-model:value="form.abstractText" type="textarea" :rows="3" />
          </n-form-item>
          <n-form-item label="论文内容" path="content">
            <n-input v-model:value="form.content" type="textarea" :rows="7" />
          </n-form-item>
          <n-grid :cols="2" :x-gap="12">
            <n-form-item-gi label="关键词" path="keywords">
              <n-input v-model:value="form.keywords" placeholder="用逗号分隔" />
            </n-form-item-gi>
            <n-form-item-gi label="作者信息" path="authorInfo">
              <n-input v-model:value="form.authorInfo" />
            </n-form-item-gi>
          </n-grid>
          <n-grid :cols="2" :x-gap="12">
            <n-form-item-gi label="分类" path="categoryId">
              <n-select v-model:value="form.categoryId" :options="categoryOptions" />
            </n-form-item-gi>
            <n-form-item-gi label="发布时间" path="publishTime">
              <n-date-picker
                v-model:formatted-value="form.publishTime"
                value-format="yyyy-MM-dd HH:mm:ss"
                type="datetime"
                clearable
              />
            </n-form-item-gi>
          </n-grid>
          <n-form-item label="封面 URL" path="coverUrl">
            <n-input v-model:value="form.coverUrl" />
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
import { computed, h, reactive, ref } from 'vue'
import type { DataTableColumns, FormInst, FormRules } from 'naive-ui'
import { NButton, NPopconfirm, useMessage } from 'naive-ui'
import { adminApi } from '@/api/admin'
import type { PaperCategory, PaperItem, PaperSaveRequest } from '@/types/content'
import { formatDateTime, toDateTimePickerValue } from '@/utils/format'
import PageHeaderCard from '@/components/common/PageHeaderCard.vue'
import TableToolbar from '@/components/common/TableToolbar.vue'

const message = useMessage()

const loading = ref(false)
const saving = ref(false)
const drawerVisible = ref(false)
const editingId = ref<number | null>(null)
const formRef = ref<FormInst | null>(null)

const keyword = ref('')
const categoryId = ref<number | null>(null)
const rows = ref<PaperItem[]>([])
const categories = ref<PaperCategory[]>([])
const pager = ref({
  current: 1,
  size: 10,
  total: 0
})

const form = reactive<PaperSaveRequest>({
  title: '',
  abstractText: '',
  content: '',
  keywords: '',
  authorInfo: '',
  coverUrl: '',
  categoryId: 0,
  publishTime: ''
})

const rules: FormRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  abstractText: [{ required: true, message: '请输入摘要', trigger: 'blur' }],
  content: [{ required: true, message: '请输入正文', trigger: 'blur' }],
  keywords: [{ required: true, message: '请输入关键词', trigger: 'blur' }],
  categoryId: [{ required: true, type: 'number', message: '请选择分类', trigger: 'change' }]
}

const categoryOptions = computed(() =>
  categories.value.map((item) => ({
    label: item.name,
    value: item.id
  }))
)

const categoryMap = computed<Record<number, string>>(() =>
  categories.value.reduce<Record<number, string>>((acc, item) => {
    acc[item.id] = item.name
    return acc
  }, {})
)

const columns: DataTableColumns<PaperItem> = [
  { title: 'ID', key: 'id', width: 70 },
  { title: '标题', key: 'title' },
  { title: '分类', key: 'categoryId', render: (row) => categoryMap.value[row.categoryId] || '-' },
  { title: '作者', key: 'authorInfo', width: 150 },
  { title: '发布时间', key: 'publishTime', width: 170, render: (row) => formatDateTime(row.publishTime) },
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
            default: () => '确定删除该论文吗？'
          }
        )
      ])
  }
]

function initForm() {
  form.title = ''
  form.abstractText = ''
  form.content = ''
  form.keywords = ''
  form.authorInfo = ''
  form.coverUrl = ''
  form.categoryId = categories.value[0]?.id || 0
  form.publishTime = ''
}

function openCreate() {
  editingId.value = null
  initForm()
  drawerVisible.value = true
}

function openEdit(row: PaperItem) {
  editingId.value = row.id
  form.title = row.title
  form.abstractText = row.abstractText || ''
  form.content = row.content || ''
  form.keywords = row.keywords || ''
  form.authorInfo = row.authorInfo || ''
  form.coverUrl = row.coverUrl || ''
  form.categoryId = row.categoryId
  form.publishTime = toDateTimePickerValue(row.publishTime)
  drawerVisible.value = true
}

function submit() {
  formRef.value?.validate(async (errors) => {
    if (errors) {
      return
    }
    saving.value = true
    try {
      const payload: PaperSaveRequest = {
        ...form,
        authorInfo: form.authorInfo?.trim() || undefined,
        coverUrl: form.coverUrl?.trim() || undefined,
        publishTime: form.publishTime?.trim() || undefined
      }
      if (editingId.value) {
        await adminApi.updatePaper(editingId.value, payload)
        message.success('更新成功')
      } else {
        await adminApi.createPaper(payload)
        message.success('创建成功')
      }
      drawerVisible.value = false
      await load()
    } finally {
      saving.value = false
    }
  })
}

async function remove(id: number) {
  await adminApi.deletePaper(id)
  message.success('删除成功')
  await load()
}

async function load() {
  loading.value = true
  try {
    const res = await adminApi.fetchPapers({
      current: pager.value.current,
      size: pager.value.size,
      keyword: keyword.value.trim() || undefined,
      categoryId: categoryId.value || undefined
    })
    rows.value = res.records || []
    pager.value.total = res.total || 0
  } finally {
    loading.value = false
  }
}

function reset() {
  keyword.value = ''
  categoryId.value = null
  pager.value.current = 1
  load()
}

async function loadCategories() {
  categories.value = await adminApi.fetchCategories()
}

loadCategories().then(load)
</script>
