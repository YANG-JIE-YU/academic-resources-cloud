<template>
  <n-card :bordered="false">
    <n-space justify="space-between" align="center" wrap>
      <n-space align="center">
        <slot name="left" />
      </n-space>
      <n-space align="center">
        <slot name="right" />
      </n-space>
    </n-space>
  </n-card>
</template>

<script setup lang="ts"></script>
