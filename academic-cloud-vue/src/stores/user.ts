import { defineStore } from 'pinia'
import type { LoginResponse, UserRole } from '@/types/auth'
import type { UserInfo } from '@/types/user'
import { fetchMyProfileApi, logoutApi } from '@/api/auth'
import {
  clearAuthStorage,
  getRole,
  getToken,
  getUserId,
  getUsername,
  normalizeRole,
  setRole,
  setToken,
  setUserId,
  setUsername
} from '@/utils/auth'

interface UserState {
  token: string
  role: UserRole | ''
  userId: number
  username: string
  profile: UserInfo | null
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    token: getToken(),
    role: (normalizeRole(getRole()) as UserRole | '') || '',
    userId: getUserId(),
    username: getUsername(),
    profile: null
  }),
  getters: {
    isLoggedIn: (state) => Boolean(state.token),
    roleCode: (state) => normalizeRole(state.role),
    isAdmin(): boolean {
      return this.roleCode === 'ADMIN'
    }
  },
  actions: {
    applyLogin(payload: LoginResponse) {
      const normalizedRole = normalizeRole(payload.role)
      this.token = payload.token
      this.role = normalizedRole as UserRole
      this.userId = payload.userId
      this.username = payload.username

      setToken(payload.token)
      setRole(normalizedRole)
      setUserId(payload.userId)
      setUsername(payload.username)
    },
    async fetchProfile() {
      if (!this.token) {
        return null
      }
      const profile = await fetchMyProfileApi()
      this.profile = profile
      this.userId = profile.id
      this.username = profile.username
      if (profile.role) {
        const normalizedRole = normalizeRole(profile.role)
        this.role = normalizedRole as UserRole
        setRole(normalizedRole)
      }
      setUserId(profile.id)
      setUsername(profile.username)
      return profile
    },
    async logout() {
      try {
        if (this.token) {
          await logoutApi()
        }
      } catch {
        // ignore logout errors
      } finally {
        this.clearLogin()
      }
    },
    clearLogin() {
      this.token = ''
      this.role = ''
      this.userId = 0
      this.username = ''
      this.profile = null
      clearAuthStorage()
    }
  }
})
