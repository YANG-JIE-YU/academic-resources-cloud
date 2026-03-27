<template>
  <n-space vertical :size="16">
    <page-header-card title="用户管理" description="支持用户新增、编辑、删除、封禁/解封和角色分配。" />

    <table-toolbar>
      <template #left>
        <n-input v-model:value="keyword" clearable placeholder="按用户名或姓名搜索" style="width: 260px" />
        <n-button type="primary" @click="load">查询</n-button>
        <n-button @click="reset">重置</n-button>
      </template>
      <template #right>
        <n-button type="primary" @click="openCreateModal">新增用户</n-button>
      </template>
    </table-toolbar>

    <n-card :bordered="false">
      <n-data-table :columns="columns" :data="rows" :loading="loading" :bordered="false" />
      <n-pagination
        v-model:page="pager.current"
        v-model:page-size="pager.size"
        :item-count="pager.total"
        show-size-picker
        :page-sizes="[10, 20, 30]"
        style="margin-top: 16px"
        @update:page="load"
        @update:page-size="load"
      />
    </n-card>

    <n-modal v-model:show="showModal" preset="dialog" :title="editingId ? '编辑用户' : '新增用户'" style="width: 640px">
      <n-form ref="formRef" :model="form" :rules="rules" label-placement="left" label-width="84">
        <n-grid cols="1 s:2" responsive="screen" :x-gap="14">
          <n-gi>
            <n-form-item path="username" label="用户名">
              <n-input v-model:value="form.username" placeholder="请输入用户名" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item path="password" :label="editingId ? '新密码(可选)' : '登录密码'">
              <n-input
                v-model:value="form.password"
                type="password"
                show-password-on="mousedown"
                :placeholder="editingId ? '不修改请留空' : '请输入登录密码'"
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item path="realName" label="姓名">
              <n-input v-model:value="form.realName" placeholder="请输入姓名" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item path="phone" label="手机号">
              <n-input v-model:value="form.phone" placeholder="请输入手机号" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item path="gender" label="性别">
              <n-select v-model:value="form.gender" :options="genderOptions" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item path="role" label="角色">
              <n-select v-model:value="form.role" :options="roleOptions" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item path="status" label="状态">
              <n-select v-model:value="form.status" :options="statusOptions" />
            </n-form-item>
          </n-gi>
        </n-grid>
      </n-form>

      <template #action>
        <n-space justify="end">
          <n-button @click="showModal = false">取消</n-button>
          <n-button type="primary" :loading="saving" @click="submit">保存</n-button>
        </n-space>
      </template>
    </n-modal>
  </n-space>
</template>

<script setup lang="ts">
import { computed, h, reactive, ref } from 'vue'
import type { DataTableColumns, FormInst, FormRules } from 'naive-ui'
import { NButton, NSpace, NTag, useDialog, useMessage } from 'naive-ui'
import { adminApi } from '@/api/admin'
import type { AdminUserCreateRequest, AdminUserUpdateRequest, UserInfo, UserRoleCode } from '@/types/user'
import { formatDateTime } from '@/utils/format'
import { normalizeRole } from '@/utils/auth'
import PageHeaderCard from '@/components/common/PageHeaderCard.vue'
import TableToolbar from '@/components/common/TableToolbar.vue'

const message = useMessage()
const dialog = useDialog()

const loading = ref(false)
const saving = ref(false)
const rows = ref<UserInfo[]>([])
const keyword = ref('')
const pager = ref({
  current: 1,
  size: 10,
  total: 0
})

const showModal = ref(false)
const editingId = ref<number | null>(null)
const formRef = ref<FormInst | null>(null)
const form = reactive({
  username: '',
  password: '',
  realName: '',
  phone: '',
  gender: '',
  role: 'USER' as UserRoleCode,
  status: 1
})

const roleOptions = [
  { label: '普通用户', value: 'USER' },
  { label: '管理员', value: 'ADMIN' }
]

const statusOptions = [
  { label: '正常', value: 1 },
  { label: '封禁', value: 0 }
]

const genderOptions = [
  { label: '未填写', value: '' },
  { label: '男', value: '男' },
  { label: '女', value: '女' }
]

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: ['blur', 'input'] }],
  password: [
    {
      trigger: ['blur', 'input'],
      validator: () => {
        if (!form.password) {
          return editingId.value ? true : new Error('请输入密码')
        }
        if (form.password.length < 6) {
          return new Error('密码至少 6 位')
        }
        return true
      }
    }
  ],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  status: [{ type: 'number', required: true, message: '请选择状态', trigger: 'change' }]
}

const columns: DataTableColumns<UserInfo> = [
  { title: 'ID', key: 'id', width: 80 },
  { title: '用户名', key: 'username' },
  { title: '姓名', key: 'realName' },
  { title: '手机号', key: 'phone' },
  {
    title: '角色',
    key: 'role',
    width: 110,
    render: (row) =>
      h(
        NTag,
        {
          size: 'small',
          type: normalizeRole(row.role) === 'ADMIN' ? 'warning' : 'default'
        },
        { default: () => (normalizeRole(row.role) === 'ADMIN' ? '管理员' : '普通用户') }
      )
  },
  {
    title: '状态',
    key: 'status',
    width: 90,
    render: (row) =>
      h(NTag, { size: 'small', type: row.status === 0 ? 'error' : 'success' }, { default: () => (row.status === 0 ? '封禁' : '正常') })
  },
  {
    title: '注册时间',
    key: 'createTime',
    width: 180,
    render: (row) => formatDateTime(row.createTime)
  },
  {
    title: '操作',
    key: 'actions',
    width: 300,
    render: (row) =>
      h(NSpace, { size: 8 }, () => [
        h(
          NButton,
          { size: 'small', onClick: () => openEditModal(row) },
          { default: () => '编辑' }
        ),
        h(
          NButton,
          {
            size: 'small',
            type: row.status === 0 ? 'success' : 'warning',
            ghost: true,
            onClick: () => toggleStatus(row)
          },
          { default: () => (row.status === 0 ? '解封' : '封禁') }
        ),
        h(
          NButton,
          {
            size: 'small',
            tertiary: true,
            onClick: () => toggleRole(row)
          },
          { default: () => (normalizeRole(row.role) === 'ADMIN' ? '设为普通用户' : '设为管理员') }
        ),
        h(
          NButton,
          {
            size: 'small',
            type: 'error',
            ghost: true,
            onClick: () => removeUser(row)
          },
          { default: () => '删除' }
        )
      ])
  }
]

const submitPayload = computed(() => {
  const role = normalizeRole(form.role) === 'ADMIN' ? 'ADMIN' : 'USER'
  return {
    username: form.username.trim(),
    realName: form.realName.trim() || undefined,
    phone: form.phone.trim() || undefined,
    gender: form.gender || undefined,
    role: role as UserRoleCode,
    status: form.status
  }
})

async function load() {
  loading.value = true
  try {
    const res = await adminApi.fetchUsers({
      current: pager.value.current,
      size: pager.value.size,
      keyword: keyword.value.trim() || undefined
    })
    rows.value = (res.records || []).map((item) => ({
      ...item,
      role: normalizeRole(item.role) || 'USER',
      status: Number(item.status ?? 1)
    }))
    pager.value.total = res.total || 0
  } finally {
    loading.value = false
  }
}

function reset() {
  keyword.value = ''
  pager.value.current = 1
  load()
}

function openCreateModal() {
  editingId.value = null
  form.username = ''
  form.password = ''
  form.realName = ''
  form.phone = ''
  form.gender = ''
  form.role = 'USER'
  form.status = 1
  showModal.value = true
}

function openEditModal(user: UserInfo) {
  editingId.value = user.id
  form.username = user.username || ''
  form.password = ''
  form.realName = user.realName || ''
  form.phone = user.phone || ''
  form.gender = user.gender || ''
  form.role = normalizeRole(user.role) === 'ADMIN' ? 'ADMIN' : 'USER'
  form.status = Number(user.status ?? 1)
  showModal.value = true
}

async function submit() {
  try {
    await formRef.value?.validate()
  } catch {
    return
  }

  saving.value = true
  try {
    if (editingId.value) {
      const data: AdminUserUpdateRequest = {
        ...submitPayload.value,
        password: form.password.trim() || undefined
      }
      await adminApi.updateUser(editingId.value, data)
      message.success('用户更新成功')
    } else {
      const password = form.password.trim()
      const data: AdminUserCreateRequest = {
        ...submitPayload.value,
        password
      }
      await adminApi.createUser(data)
      message.success('用户新增成功')
    }
    showModal.value = false
    await load()
  } finally {
    saving.value = false
  }
}

async function toggleStatus(user: UserInfo) {
  const nextStatus = Number(user.status) === 0 ? 1 : 0
  await adminApi.updateUserStatus(user.id, nextStatus)
  message.success(nextStatus === 1 ? '账号已解封' : '账号已封禁')
  await load()
}

async function toggleRole(user: UserInfo) {
  const nextRole: UserRoleCode = normalizeRole(user.role) === 'ADMIN' ? 'USER' : 'ADMIN'
  await adminApi.updateUserRole(user.id, nextRole)
  message.success(nextRole === 'ADMIN' ? '已设置为管理员' : '已设置为普通用户')
  await load()
}

function removeUser(user: UserInfo) {
  dialog.warning({
    title: '删除确认',
    content: `确定要删除用户「${user.username}」吗？此操作不可恢复。`,
    positiveText: '确认删除',
    negativeText: '取消',
    onPositiveClick: async () => {
      await adminApi.deleteUser(user.id)
      message.success('删除成功')
      if (rows.value.length === 1 && pager.value.current > 1) {
        pager.value.current -= 1
      }
      await load()
    }
  })
}

load()
</script>
