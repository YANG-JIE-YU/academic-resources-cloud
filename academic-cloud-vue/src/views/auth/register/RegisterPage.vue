<template>
  <n-space vertical :size="16">
    <h2 class="title">创建账号</h2>
    <n-form ref="formRef" :model="form" :rules="rules" label-placement="top">
      <n-form-item path="username" label="用户名">
        <n-input v-model:value="form.username" placeholder="3-32 位用户名" />
      </n-form-item>
      <n-grid :cols="2" :x-gap="12">
        <n-form-item-gi path="realName" label="姓名">
          <n-input v-model:value="form.realName" placeholder="请输入真实姓名" />
        </n-form-item-gi>
        <n-form-item-gi path="gender" label="性别">
          <n-select v-model:value="form.gender" :options="genderOptions" placeholder="请选择性别" />
        </n-form-item-gi>
      </n-grid>
      <n-form-item path="phone" label="手机号">
        <n-input v-model:value="form.phone" placeholder="选填，11 位手机号" />
      </n-form-item>
      <n-form-item path="password" label="密码">
        <n-input v-model:value="form.password" type="password" show-password-on="mousedown" placeholder="请输入密码" />
      </n-form-item>
      <n-form-item path="confirmPassword" label="确认密码">
        <n-input
          v-model:value="form.confirmPassword"
          type="password"
          show-password-on="mousedown"
          placeholder="请再次输入密码"
        />
      </n-form-item>
      <n-button block type="primary" :loading="submitting" @click="handleRegister">
        注册
      </n-button>
    </n-form>

    <n-space justify="space-between">
      <span class="tip">已有账号？</span>
      <router-link to="/auth/login">返回登录</router-link>
    </n-space>
  </n-space>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import type { FormInst, FormRules } from 'naive-ui'
import { useMessage } from 'naive-ui'
import { useRouter } from 'vue-router'
import { registerApi } from '@/api/auth'

const message = useMessage()
const router = useRouter()
const formRef = ref<FormInst | null>(null)
const submitting = ref(false)

const form = reactive({
  username: '',
  realName: '',
  gender: '男',
  phone: '',
  password: '',
  confirmPassword: ''
})

const genderOptions = [
  { label: '男', value: '男' },
  { label: '女', value: '女' }
]

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 32, message: '用户名长度需在 3-32 之间', trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    {
      validator: (_rule, value: string) => !value || /^1\d{10}$/.test(value),
      message: '手机号格式不正确',
      trigger: ['input', 'blur']
    }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 64, message: '密码长度需在 6-64 之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (_rule, value: string) => value === form.password,
      message: '两次密码不一致',
      trigger: ['input', 'blur']
    }
  ]
}

function handleRegister() {
  formRef.value?.validate(async (errors) => {
    if (errors) {
      return
    }
    submitting.value = true
    try {
      await registerApi({
        username: form.username.trim(),
        password: form.password,
        realName: form.realName.trim(),
        phone: form.phone.trim(),
        gender: form.gender
      })
      message.success('注册成功，请登录')
      router.push('/auth/login')
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
