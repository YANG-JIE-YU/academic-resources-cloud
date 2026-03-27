<template>
  <n-space vertical :size="16">
    <h2 class="title">账号登录</h2>
    <n-form ref="formRef" :model="form" :rules="rules" label-placement="top">
      <n-form-item path="username" label="用户名">
        <n-input v-model:value="form.username" placeholder="请输入用户名" />
      </n-form-item>
      <n-form-item path="password" label="密码">
        <n-input v-model:value="form.password" type="password" show-password-on="mousedown" placeholder="请输入密码" />
      </n-form-item>
      <n-button type="primary" block :loading="submitting" @click="handleLogin">
        登录
      </n-button>
    </n-form>

<!--    <n-alert type="info" :bordered="false">-->
<!--      可使用你在 user-service 中注册的账号登录。-->
<!--    </n-alert>-->

    <n-space justify="space-between">
      <span class="tip">还没有账号？</span>
      <router-link to="/auth/register">立即注册</router-link>
    </n-space>
  </n-space>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import type { FormInst, FormRules } from 'naive-ui'
import { useMessage } from 'naive-ui'
import { useRoute, useRouter } from 'vue-router'
import { loginApi } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const message = useMessage()
const userStore = useUserStore()

const formRef = ref<FormInst | null>(null)
const submitting = ref(false)
const form = reactive({
  username: '',
  password: ''
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

function resolveRedirect() {
  const redirect = route.query.redirect
  if (typeof redirect === 'string' && redirect) {
    return redirect
  }
  return userStore.isAdmin ? '/admin/dashboard' : '/'
}

function handleLogin() {
  formRef.value?.validate(async (errors) => {
    if (errors) {
      return
    }
    submitting.value = true
    try {
      const response = await loginApi(form)
      userStore.applyLogin(response)
      await userStore.fetchProfile().catch(() => null)
      message.success('登录成功')
      router.replace(resolveRedirect())
    } finally {
      submitting.value = false
    }
  })
}
</script>

<style scoped>
.title {
  margin: 0;
  font-size: 26px;
}

.tip {
  color: var(--text-sub);
}
</style>
