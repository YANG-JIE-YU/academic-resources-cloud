<template>
  <n-card title="评论互动" :bordered="false">
    <n-space vertical :size="12">
      <n-input
        v-model:value="form.content"
        type="textarea"
        :rows="4"
        placeholder="分享你的看法..."
        maxlength="2000"
        show-count
      />
      <n-space justify="end">
        <n-button type="primary" :loading="submitting" @click="submitComment">发表评论</n-button>
      </n-space>
      <n-divider />
      <n-spin :show="loading">
        <n-space v-if="comments.length > 0" vertical :size="10">
          <n-card v-for="item in comments" :key="item.id" size="small" embedded>
            <n-space vertical :size="6">
              <n-space justify="space-between">
                <n-tag type="info" size="small">{{ item.username || `用户 ${item.userId}` }}</n-tag>
                <span class="time">{{ formatDateTime(item.createTime) }}</span>
              </n-space>
              <div class="content">{{ item.content }}</div>
              <div v-if="item.replyContent" class="reply">
                管理员回复：{{ item.replyContent }}
              </div>
            </n-space>
          </n-card>
        </n-space>
        <n-empty v-else description="还没有评论，来抢沙发吧" />
      </n-spin>
    </n-space>
  </n-card>
</template>

<script setup lang="ts">
import { reactive, ref, watchEffect } from 'vue'
import { useMessage } from 'naive-ui'
import { useRouter } from 'vue-router'
import { createComment, fetchComments } from '@/api/user'
import type { CommentItem } from '@/types/user'
import { formatDateTime } from '@/utils/format'
import { useUserStore } from '@/stores/user'

const props = defineProps<{
  targetType: string
  targetId: number
}>()

const message = useMessage()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const comments = ref<CommentItem[]>([])
const form = reactive({
  content: ''
})

async function loadComments() {
  if (!props.targetId) {
    comments.value = []
    return
  }
  loading.value = true
  try {
    comments.value = await fetchComments({
      targetType: props.targetType,
      targetId: props.targetId
    })
  } finally {
    loading.value = false
  }
}

async function submitComment() {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录后再评论')
    router.push({
      path: '/auth/login',
      query: { redirect: router.currentRoute.value.fullPath }
    })
    return
  }
  if (!form.content.trim()) {
    message.warning('评论内容不能为空')
    return
  }
  submitting.value = true
  try {
    await createComment({
      targetType: props.targetType,
      targetId: props.targetId,
      content: form.content.trim()
    })
    message.success('评论成功')
    form.content = ''
    await loadComments()
  } finally {
    submitting.value = false
  }
}

watchEffect(() => {
  if (props.targetId) {
    loadComments()
  }
})
</script>

<style scoped>
.time {
  font-size: 12px;
  color: var(--text-sub);
}

.content {
  white-space: pre-wrap;
  line-height: 1.7;
}

.reply {
  padding: 8px 10px;
  border-radius: 8px;
  background: #f5f8ff;
  color: #3c4a63;
  font-size: 13px;
}
</style>
