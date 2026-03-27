<template>
  <div class="page-container">
    <n-result status="404" title="页面不存在" description="你访问的页面路径不存在。">
      <template #footer>
        <n-button type="primary" @click="$router.push('/')">返回首页</n-button>
      </template>
    </n-result>
  </div>
</template>

<script setup lang="ts"></script>
