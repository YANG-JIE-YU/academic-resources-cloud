<template>
  <div class="page-container">
    <n-space vertical :size="16">
      <page-header-card title="AI 智能推荐" description="结合场景与关键词，推荐匹配的论文资源。">
        <template #extra>
          <n-button @click="$router.push('/papers')">进入论文库</n-button>
        </template>
      </page-header-card>
      <n-card :bordered="false">
        <n-grid cols="1 m:4" responsive="screen" :x-gap="12" :y-gap="12">
          <n-grid-item>
            <n-select v-model:value="scene" :options="sceneOptions" placeholder="推荐场景" />
          </n-grid-item>
          <n-grid-item :span="2">
            <n-input v-model:value="query" clearable placeholder="输入兴趣关键词（可选）" />
          </n-grid-item>
          <n-grid-item>
            <n-button type="primary" block :loading="loading" @click="load">获取推荐</n-button>
          </n-grid-item>
        </n-grid>
      </n-card>

      <n-card :bordered="false" title="推荐结果">
        <template #header-extra>
          <n-tag v-if="trigger" type="success">触发词：{{ trigger }}</n-tag>
        </template>
        <n-grid cols="1 s:2 l:3" responsive="screen" :x-gap="14" :y-gap="14">
          <n-grid-item v-for="item in list" :key="item.id">
            <paper-card :paper="item" />
          </n-grid-item>
        </n-grid>
        <n-empty v-if="!loading && list.length === 0" description="暂无推荐结果" />
      </n-card>
    </n-space>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { recommendApi } from '@/api/ai'
import type { PaperItem } from '@/types/content'
import PageHeaderCard from '@/components/common/PageHeaderCard.vue'
import PaperCard from '@/components/business/PaperCard.vue'

const loading = ref(false)
const scene = ref('HOME')
const query = ref('')
const trigger = ref('')
const list = ref<PaperItem[]>([])

const sceneOptions = [
  { label: '首页推荐', value: 'HOME' },
  { label: '搜索增强', value: 'SEARCH' },
  { label: '用户兴趣推荐', value: 'INTEREST' }
]

async function load() {
  loading.value = true
  try {
    const res = await recommendApi({
      scene: scene.value,
      query: query.value.trim() || undefined,
      limit: 9
    })
    trigger.value = res.trigger
    list.value = res.papers || []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  load()
})
</script>
