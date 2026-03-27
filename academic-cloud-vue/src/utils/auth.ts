export const AUTH_TOKEN_KEY = 'academic_cloud_token'
export const AUTH_ROLE_KEY = 'academic_cloud_role'
export const AUTH_USERNAME_KEY = 'academic_cloud_username'
export const AUTH_USER_ID_KEY = 'academic_cloud_user_id'

export function getToken() {
  return localStorage.getItem(AUTH_TOKEN_KEY) || ''
}

export function setToken(token: string) {
  localStorage.setItem(AUTH_TOKEN_KEY, token)
}

export function clearToken() {
  localStorage.removeItem(AUTH_TOKEN_KEY)
}

export function getRole() {
  return localStorage.getItem(AUTH_ROLE_KEY) || ''
}

export function setRole(role: string) {
  localStorage.setItem(AUTH_ROLE_KEY, role)
}

export function normalizeRole(rawRole?: string | null) {
  if (!rawRole) {
    return ''
  }
  const role = rawRole.trim().toUpperCase()
  if (role === 'ADMIN' || role === 'ROLE_ADMIN' || role === 'SUPER_ADMIN') {
    return 'ADMIN'
  }
  if (role === 'USER' || role === 'ROLE_USER') {
    return 'USER'
  }
  return role
}

export function getUsername() {
  return localStorage.getItem(AUTH_USERNAME_KEY) || ''
}

export function setUsername(username: string) {
  localStorage.setItem(AUTH_USERNAME_KEY, username)
}

export function getUserId() {
  return Number(localStorage.getItem(AUTH_USER_ID_KEY) || 0)
}

export function setUserId(id: number) {
  localStorage.setItem(AUTH_USER_ID_KEY, String(id))
}

export function clearAuthStorage() {
  localStorage.removeItem(AUTH_TOKEN_KEY)
  localStorage.removeItem(AUTH_ROLE_KEY)
  localStorage.removeItem(AUTH_USERNAME_KEY)
  localStorage.removeItem(AUTH_USER_ID_KEY)
}

export function buildAuthorizationHeader(token: string) {
  if (!token) {
    return ''
  }
  return token.startsWith('Bearer ') ? token : `Bearer ${token}`
}
