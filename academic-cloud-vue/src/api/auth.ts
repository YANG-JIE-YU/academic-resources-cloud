import { get, post } from '@/utils/request'
import type { LoginRequest, LoginResponse, RegisterRequest } from '@/types/auth'
import type { UserInfo } from '@/types/user'

export function loginApi(data: LoginRequest) {
  return post<LoginResponse>('/api/user/auth/login', data)
}

export function registerApi(data: RegisterRequest) {
  return post<UserInfo>('/api/user/auth/register', data)
}

export function logoutApi() {
  return post<void>('/api/user/auth/logout')
}

export function fetchMyProfileApi() {
  return get<UserInfo>('/api/user/users/me')
}
