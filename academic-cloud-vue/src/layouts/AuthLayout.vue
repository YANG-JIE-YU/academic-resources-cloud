<template>
  <div class="auth-layout">
    <div class="auth-shell">
      <section class="intro-panel">
        <h1>学术资源门户系统</h1>
        <p>一个统一前端覆盖门户端与后台管理端的毕业设计项目。</p>
        <n-space vertical :size="10">
          <n-tag type="success" size="small">Vue 3 + Vite + TypeScript</n-tag>
          <n-tag type="info" size="small">Naive UI + Pinia + Router + Axios</n-tag>
        </n-space>
      </section>

      <n-card class="form-panel" :bordered="false">
        <router-view />
      </n-card>
    </div>
  </div>
</template>

<script setup lang="ts"></script>

<style scoped>
.auth-layout {
  min-height: 100vh;
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background:
    radial-gradient(circle at 18% 20%, #dff4ec 0%, transparent 36%),
    radial-gradient(circle at 82% 80%, #e6efff 0%, transparent 38%),
    #f6f8fb;
}

.auth-shell {
  width: min(1020px, 100%);
  display: grid;
  grid-template-columns: 1.05fr 420px;
  gap: 18px;
}

.intro-panel {
  border-radius: 16px;
  background: #ffffff;
  padding: 28px;
  box-shadow: 0 10px 28px rgba(15, 30, 64, 0.08);
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.intro-panel h1 {
  margin: 0 0 12px;
  font-size: 34px;
  color: var(--primary);
}

.intro-panel p {
  margin: 0 0 16px;
  color: var(--text-sub);
  line-height: 1.7;
}

.form-panel {
  border-radius: 16px;
  box-shadow: 0 10px 28px rgba(15, 30, 64, 0.08);
}
</style>
