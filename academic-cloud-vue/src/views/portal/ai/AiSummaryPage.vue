<template>
  <div class="page-container">
    <n-space vertical :size="16">
      <page-header-card title="AI 摘要生成" description="输入论文正文或长文本，快速生成摘要。">
        <template #extra>
          <n-button @click="$router.push('/ai/chat')">返回对话</n-button>
        </template>
      </page-header-card>
      <n-card :bordered="false">
        <n-space vertical :size="12">
          <n-input
            v-model:value="text"
            type="textarea"
            :rows="10"
            maxlength="100000"
            show-count
            placeholder="粘贴论文正文或章节内容..."
          />
          <n-space justify="end">
            <n-button type="primary" :loading="loading" @click="generate">生成摘要</n-button>
          </n-space>
          <n-divider />
          <n-input v-model:value="summary" type="textarea" :rows="8" readonly placeholder="生成结果将显示在这里" />
        </n-space>
      </n-card>
    </n-space>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useMessage } from 'naive-ui'
import { summaryApi } from '@/api/ai'
import PageHeaderCard from '@/components/common/PageHeaderCard.vue'

const message = useMessage()
const loading = ref(false)
const text = ref('')
const summary = ref('')

async function generate() {
  if (!text.value.trim()) {
    message.warning('请输入待处理文本')
    return
  }
  loading.value = true
  try {
    const res = await summaryApi(text.value.trim())
    summary.value = res.summary
  } finally {
    loading.value = false
  }
}
</script>
