<template>
  <div class="page-container">
    <n-space vertical :size="16">
      <page-header-card title="AI 关键词提取" description="自动从论文文本提取关键术语。">
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
            placeholder="粘贴论文内容..."
          />
          <n-space justify="end">
            <n-button type="primary" :loading="loading" @click="extract">提取关键词</n-button>
          </n-space>
          <n-divider />
          <n-space>
            <n-tag v-for="item in keywords" :key="item" type="success">{{ item }}</n-tag>
          </n-space>
        </n-space>
      </n-card>
    </n-space>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useMessage } from 'naive-ui'
import { keywordsApi } from '@/api/ai'
import PageHeaderCard from '@/components/common/PageHeaderCard.vue'

const message = useMessage()
const loading = ref(false)
const text = ref('')
const keywords = ref<string[]>([])

async function extract() {
  if (!text.value.trim()) {
    message.warning('请输入待处理文本')
    return
  }
  loading.value = true
  try {
    keywords.value = await keywordsApi(text.value.trim())
  } finally {
    loading.value = false
  }
}
</script>
