<template>
  <n-space vertical :size="16">
    <page-header-card title="评论管理" description="按资源类型与资源 ID 查询评论，并支持管理员回复。" />

    <table-toolbar>
      <template #left>
        <n-select v-model:value="targetType" :options="targetTypeOptions" style="width: 160px" />
        <n-input-number v-model:value="targetId" :min="1" placeholder="目标ID" style="width: 180px" />
        <n-button type="primary" @click="load">查询评论</n-button>
      </template>
    </table-toolbar>

    <n-card :bordered="false">
      <n-data-table :columns="columns" :data="rows" :loading="loading" :bordered="false" />
    </n-card>

    <n-modal v-model:show="showReplyModal" preset="dialog" title="管理员回复" style="width: 640px">
      <n-form ref="replyFormRef" :model="replyForm" :rules="replyRules" label-placement="left" label-width="72">
        <n-form-item label="评论ID">
          <n-input :value="String(replyForm.commentId || '')" disabled />
        </n-form-item>
        <n-form-item path="replyContent" label="回复内容">
          <n-input
            v-model:value="replyForm.replyContent"
            type="textarea"
            :autosize="{ minRows: 4, maxRows: 8 }"
            placeholder="请输入管理员回复内容"
          />
        </n-form-item>
      </n-form>
      <template #action>
        <n-space justify="end">
          <n-button @click="showReplyModal = false">取消</n-button>
          <n-button type="primary" :loading="replySaving" @click="submitReply">保存回复</n-button>
        </n-space>
      </template>
    </n-modal>
  </n-space>
</template>

<script setup lang="ts">
import { h, reactive, ref } from 'vue'
import type { DataTableColumns, FormInst, FormRules } from 'naive-ui'
import { NButton, NSpace, useMessage } from 'naive-ui'
import { adminApi } from '@/api/admin'
import type { CommentItem } from '@/types/user'
import { formatDateTime } from '@/utils/format'
import PageHeaderCard from '@/components/common/PageHeaderCard.vue'
import TableToolbar from '@/components/common/TableToolbar.vue'

const message = useMessage()

const loading = ref(false)
const replySaving = ref(false)
const targetType = ref('PAPER')
const targetId = ref<number | null>(1)
const rows = ref<CommentItem[]>([])

const showReplyModal = ref(false)
const replyFormRef = ref<FormInst | null>(null)
const replyForm = reactive({
  commentId: 0,
  replyContent: ''
})

const replyRules: FormRules = {
  replyContent: [
    { required: true, message: '请输入回复内容', trigger: ['blur', 'input'] },
    { min: 2, max: 2000, message: '回复内容长度需在 2-2000 字符之间', trigger: ['blur', 'input'] }
  ]
}

const targetTypeOptions = [
  { label: '论文', value: 'PAPER' },
  { label: '公告', value: 'NOTICE' },
  { label: '活动', value: 'ACTIVITY' }
]

const columns: DataTableColumns<CommentItem> = [
  { title: '评论ID', key: 'id', width: 90 },
  { title: '用户ID', key: 'userId', width: 90 },
  { title: '评论内容', key: 'content' },
  {
    title: '管理员回复',
    key: 'replyContent',
    render: (row) => row.replyContent || '-'
  },
  {
    title: '评论时间',
    key: 'createTime',
    width: 180,
    render: (row) => formatDateTime(row.createTime)
  },
  {
    title: '操作',
    key: 'actions',
    width: 120,
    render: (row) =>
      h(
        NSpace,
        { size: 8 },
        () => [
          h(
            NButton,
            { size: 'small', type: 'primary', text: true, onClick: () => openReplyModal(row) },
            { default: () => (row.replyContent ? '修改回复' : '回复') }
          )
        ]
      )
  }
]

async function load() {
  if (!targetId.value) {
    message.warning('请输入目标ID')
    return
  }
  loading.value = true
  try {
    rows.value = await adminApi.fetchComments(targetType.value, targetId.value)
  } finally {
    loading.value = false
  }
}

function openReplyModal(row: CommentItem) {
  replyForm.commentId = row.id
  replyForm.replyContent = row.replyContent || ''
  showReplyModal.value = true
}

async function submitReply() {
  try {
    await replyFormRef.value?.validate()
  } catch {
    return
  }
  replySaving.value = true
  try {
    await adminApi.replyComment(replyForm.commentId, { replyContent: replyForm.replyContent.trim() })
    message.success('回复已保存')
    showReplyModal.value = false
    await load()
  } finally {
    replySaving.value = false
  }
}

load()
</script>
