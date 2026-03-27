<template>
  <n-card :bordered="false" class="header-card">
    <n-space justify="space-between" align="center" wrap>
      <div>
        <h2 class="title">{{ title }}</h2>
        <p v-if="description" class="description">{{ description }}</p>
      </div>
      <n-space align="center">
        <slot name="extra" />
      </n-space>
    </n-space>
  </n-card>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  description?: string
}>()
</script>

<style scoped>
.header-card {
  border-radius: 12px;
}

.title {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
}

.description {
  margin: 8px 0 0;
  color: var(--text-sub);
}
</style>
