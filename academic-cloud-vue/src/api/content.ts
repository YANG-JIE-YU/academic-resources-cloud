import { del, get, post, put } from '@/utils/request'
import type {
  ActivityItem,
  ActivitySaveRequest,
  CategorySaveRequest,
  NoticeItem,
  NoticeSaveRequest,
  PaperCategory,
  PaperItem,
  PaperPageData,
  PaperQuery,
  PaperSaveRequest
} from '@/types/content'

export function fetchPaperPage(params: PaperQuery) {
  return get<PaperPageData>('/api/paper/papers/page', params as Record<string, unknown>)
}

export function fetchPaperDetail(id: number) {
  return get<PaperItem>(`/api/paper/papers/${id}`)
}

export function fetchPaperCategories() {
  return get<PaperCategory[]>('/api/paper/categories')
}

export function fetchAdminPaperCategories() {
  return get<PaperCategory[]>('/api/paper/categories/admin/list')
}

export function createPaper(data: PaperSaveRequest) {
  return post<PaperItem>('/api/paper/papers/admin', data)
}

export function updatePaper(id: number, data: PaperSaveRequest) {
  return put<PaperItem>(`/api/paper/papers/admin/${id}`, data)
}

export function deletePaper(id: number) {
  return del<void>(`/api/paper/papers/admin/${id}`)
}

export function createPaperCategory(data: CategorySaveRequest) {
  return post<PaperCategory>('/api/paper/categories/admin', data)
}

export function updatePaperCategory(id: number, data: CategorySaveRequest) {
  return put<PaperCategory>(`/api/paper/categories/admin/${id}`, data)
}

export function deletePaperCategory(id: number) {
  return del<void>(`/api/paper/categories/admin/${id}`)
}

export function fetchNoticeList(keyword?: string) {
  return get<NoticeItem[]>('/api/paper/notices', keyword ? { keyword } : undefined)
}

export function fetchNoticeDetail(id: number) {
  return get<NoticeItem>(`/api/paper/notices/${id}`)
}

export function fetchAdminNoticeList(keyword?: string) {
  return get<NoticeItem[]>('/api/paper/notices/admin/list', keyword ? { keyword } : undefined)
}

export function createNotice(data: NoticeSaveRequest) {
  return post<NoticeItem>('/api/paper/notices/admin', data)
}

export function updateNotice(id: number, data: NoticeSaveRequest) {
  return put<NoticeItem>(`/api/paper/notices/admin/${id}`, data)
}

export function deleteNotice(id: number) {
  return del<void>(`/api/paper/notices/admin/${id}`)
}

export function fetchActivityList(keyword?: string) {
  return get<ActivityItem[]>('/api/paper/activities', keyword ? { keyword } : undefined)
}

export function fetchActivityDetail(id: number) {
  return get<ActivityItem>(`/api/paper/activities/${id}`)
}

export function fetchAdminActivityList(keyword?: string) {
  return get<ActivityItem[]>('/api/paper/activities/admin/list', keyword ? { keyword } : undefined)
}

export function createActivity(data: ActivitySaveRequest) {
  return post<ActivityItem>('/api/paper/activities/admin', data)
}

export function updateActivity(id: number, data: ActivitySaveRequest) {
  return put<ActivityItem>(`/api/paper/activities/admin/${id}`, data)
}

export function deleteActivity(id: number) {
  return del<void>(`/api/paper/activities/admin/${id}`)
}
