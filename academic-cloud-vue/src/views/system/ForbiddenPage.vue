<template>
  <div class="page-container">
    <n-result status="403" title="无权限访问" description="当前账号没有访问该页面的权限。">
      <template #footer>
        <n-space>
          <n-button @click="$router.push('/')">返回首页</n-button>
          <n-button type="primary" @click="$router.push('/auth/login')">重新登录</n-button>
        </n-space>
      </template>
    </n-result>
  </div>
</template>

<script setup lang="ts"></script>
