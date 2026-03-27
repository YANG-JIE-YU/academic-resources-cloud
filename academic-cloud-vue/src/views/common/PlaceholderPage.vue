<template>
  <div class="page-container">
    <n-result status="418" :title="String(route.meta.title || '页面建设中')" description="该页面正在开发中。">
      <template #footer>
        <n-button type="primary" @click="$router.back()">返回上一页</n-button>
      </template>
    </n-result>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'

const route = useRoute()
</script>
