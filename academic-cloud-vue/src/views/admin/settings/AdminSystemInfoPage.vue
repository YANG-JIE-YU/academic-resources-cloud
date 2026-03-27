<template>
  <n-space vertical :size="16">
    <page-header-card title="系统基础信息" description="展示项目信息、技术栈与微服务架构说明。" />
    <n-grid cols="1 m:2" responsive="screen" :x-gap="16" :y-gap="16">
      <n-grid-item>
        <n-card title="前端技术栈" :bordered="false">
          <n-space vertical>
            <n-tag type="success">Vue 3</n-tag>
            <n-tag type="success">Vite</n-tag>
            <n-tag type="success">Naive UI</n-tag>
            <n-tag type="success">Pinia + Vue Router</n-tag>
            <n-tag type="success">Axios + TypeScript</n-tag>
          </n-space>
        </n-card>
      </n-grid-item>
      <n-grid-item>
        <n-card title="后端微服务" :bordered="false">
          <n-space vertical>
            <n-tag>gateway-service</n-tag>
            <n-tag>user-service</n-tag>
            <n-tag>paper-service(content)</n-tag>
            <n-tag>ai-service</n-tag>
          </n-space>
        </n-card>
      </n-grid-item>
    </n-grid>
    <n-card title="架构说明" :bordered="false">
      <p class="desc">
        本系统采用前后端分离架构。前端通过 /api 统一访问网关服务，再由网关路由到具体微服务模块，
        并通过 Token 鉴权实现统一登录态管理。
      </p>
    </n-card>
  </n-space>
</template>

<script setup lang="ts">
import PageHeaderCard from '@/components/common/PageHeaderCard.vue'
</script>

<style scoped>
.desc {
  margin: 0;
  line-height: 1.9;
  color: #334155;
}
</style>
