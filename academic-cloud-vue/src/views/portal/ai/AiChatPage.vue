<template>
  <div class="page-container">
    <n-space vertical :size="16">
      <page-header-card
        title="AI 对话助手"
        description="用于论文选题、研究方法、文献阅读和写作思路讨论。"
      >
        <template #extra>
          <n-space>
            <n-button @click="$router.push('/ai/summary')">摘要生成</n-button>
            <n-button @click="$router.push('/ai/keywords')">关键词提取</n-button>
            <n-button type="primary" ghost @click="$router.push('/ai/recommend')">智能推荐</n-button>
          </n-space>
        </template>
      </page-header-card>

      <n-card :bordered="false">
        <n-space vertical :size="12">
          <div class="chat-list">
            <n-space v-if="messages.length > 0" vertical :size="12">
              <n-card v-for="(item, index) in messages" :key="index" embedded>
                <n-space vertical :size="8">
                  <div class="q">Q: {{ item.question }}</div>
                  <div class="a">A: {{ item.answer }}</div>
                </n-space>
              </n-card>
            </n-space>
            <n-empty v-else description="输入问题开始对话" />
          </div>
          <n-input
            v-model:value="question"
            type="textarea"
            :rows="4"
            maxlength="2000"
            show-count
            placeholder="例如：请给我一份计算机视觉方向毕业论文开题思路"
          />
          <n-space justify="end">
            <n-button v-if="!loading" type="primary" @click="send">发送</n-button>
            <n-button v-else type="error" ghost @click="stop">停止</n-button>
          </n-space>
        </n-space>
      </n-card>
    </n-space>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useMessage } from 'naive-ui'
import { chatApi, chatStreamApi } from '@/api/ai'
import PageHeaderCard from '@/components/common/PageHeaderCard.vue'

interface ChatMessage {
  question: string
  answer: string
}

const message = useMessage()
const loading = ref(false)
const question = ref('')
const messages = ref<ChatMessage[]>([])
const streamAbortController = ref<AbortController | null>(null)

async function send() {
  const text = question.value.trim()
  if (!text) {
    message.warning('请输入问题内容')
    return
  }
  if (loading.value) {
    return
  }

  messages.value.unshift({
    question: text,
    answer: ''
  })
  const currentMessage = messages.value[0]
  question.value = ''

  const controller = new AbortController()
  streamAbortController.value = controller
  loading.value = true
  try {
    await chatStreamApi(text, {
      signal: controller.signal,
      onChunk: (chunk) => {
        currentMessage.answer += chunk
      }
    })
    if (!currentMessage.answer.trim()) {
      const res = await chatApi(text)
      currentMessage.answer = res.answer
    }
  } catch {
    if (controller.signal.aborted) {
      if (!currentMessage.answer.trim()) {
        currentMessage.answer = '已停止生成。'
      }
      return
    }
    try {
      const res = await chatApi(text)
      currentMessage.answer = res.answer
    } catch {
      message.error('对话失败，请稍后重试')
      if (!currentMessage.answer.trim()) {
        currentMessage.answer = '对话失败，请稍后重试。'
      }
    }
  } finally {
    loading.value = false
    streamAbortController.value = null
  }
}

function stop() {
  if (streamAbortController.value) {
    streamAbortController.value.abort()
  }
}
</script>

<style scoped>
.chat-list {
  min-height: 180px;
}

.q {
  color: #1f2a37;
  font-weight: 600;
}

.a {
  color: #334155;
  line-height: 1.8;
  white-space: pre-wrap;
}
</style>
