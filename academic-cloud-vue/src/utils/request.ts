import axios, { type AxiosRequestConfig } from 'axios'
import { createDiscreteApi } from 'naive-ui'
import type { ApiResult } from '@/types/common'
import { buildAuthorizationHeader, clearAuthStorage, getToken } from '@/utils/auth'

const { message } = createDiscreteApi(['message'])

const requestInstance = axios.create({
  baseURL: '/',
  timeout: 15000
})

requestInstance.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = buildAuthorizationHeader(token)
  }
  return config
})

requestInstance.interceptors.response.use(
  (response) => {
    const payload = response.data as ApiResult<unknown>
    if (payload && typeof payload.code === 'number') {
      if (payload.code === 0) {
        return payload.data
      }
      const msg = payload.msg || '请求失败'
      message.error(msg)
      return Promise.reject(new Error(msg))
    }
    return response.data
  },
  (error) => {
    const status = error?.response?.status as number | undefined
    const msg = error?.response?.data?.msg || error?.message || '网络异常，请稍后重试'

    if (status === 401) {
      clearAuthStorage()
      const redirect = encodeURIComponent(window.location.pathname + window.location.search)
      if (!window.location.pathname.startsWith('/auth/')) {
        window.location.href = `/auth/login?redirect=${redirect}`
      }
      return Promise.reject(error)
    }

    message.error(msg)
    return Promise.reject(error)
  }
)

export function request<T>(config: AxiosRequestConfig) {
  return requestInstance.request<T, T>(config)
}

export function get<T>(url: string, params?: Record<string, unknown>, config?: AxiosRequestConfig) {
  return request<T>({ ...config, url, method: 'GET', params })
}

export function post<T>(url: string, data?: unknown, config?: AxiosRequestConfig) {
  return request<T>({ ...config, url, method: 'POST', data })
}

export function put<T>(url: string, data?: unknown, config?: AxiosRequestConfig) {
  return request<T>({ ...config, url, method: 'PUT', data })
}

export function del<T>(url: string, config?: AxiosRequestConfig) {
  return request<T>({ ...config, url, method: 'DELETE' })
}
