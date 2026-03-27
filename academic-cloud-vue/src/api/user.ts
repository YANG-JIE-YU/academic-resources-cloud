import { del, get, post, put } from '@/utils/request'
import type {
  AddFavoriteRequest,
  AdminCommentReplyRequest,
  AdminUserCreateRequest,
  AdminUserUpdateRequest,
  CommentItem,
  CreateCommentRequest,
  CreateForumPostRequest,
  CreateForumReplyRequest,
  FavoriteItem,
  ForumPostItem,
  UserInfo
} from '@/types/user'
import type { PageData } from '@/types/common'

export function fetchMyProfile() {
  return get<UserInfo>('/api/user/users/me')
}

export function fetchAdminUsers(params: { current: number; size: number; keyword?: string }) {
  return get<PageData<UserInfo>>('/api/user/users/admin/page', params)
}

export function createAdminUser(data: AdminUserCreateRequest) {
  return post<UserInfo>('/api/user/users/admin', data)
}

export function updateAdminUser(userId: number, data: AdminUserUpdateRequest) {
  return put<UserInfo>(`/api/user/users/admin/${userId}`, data)
}

export function deleteAdminUser(userId: number) {
  return del<void>(`/api/user/users/admin/${userId}`)
}

export function updateAdminUserStatus(userId: number, status: number) {
  return put<UserInfo>(`/api/user/users/admin/${userId}/status`, { status })
}

export function updateAdminUserRole(userId: number, role: 'USER' | 'ADMIN') {
  return put<UserInfo>(`/api/user/users/admin/${userId}/role`, { role })
}

export function fetchMyFavorites() {
  return get<FavoriteItem[]>('/api/user/favorites/mine')
}

export function addFavorite(data: AddFavoriteRequest) {
  return post<FavoriteItem>('/api/user/favorites', data)
}

export function deleteFavorite(favoriteId: number) {
  return del<void>(`/api/user/favorites/${favoriteId}`)
}

export function fetchComments(params: { targetType: string; targetId: number }) {
  return get<CommentItem[]>('/api/user/comments', params)
}

export function createComment(data: CreateCommentRequest) {
  return post<CommentItem>('/api/user/comments', data)
}

export function replyComment(commentId: number, data: AdminCommentReplyRequest) {
  return put<CommentItem>(`/api/user/comments/admin/${commentId}/reply`, data)
}

export function fetchForumPosts() {
  return get<ForumPostItem[]>('/api/user/forum/posts')
}

export function fetchForumReplies(parentId: number) {
  return get<ForumPostItem[]>(`/api/user/forum/posts/${parentId}/replies`)
}

export function createForumPost(data: CreateForumPostRequest) {
  return post<ForumPostItem>('/api/user/forum/posts', data)
}

export function createForumReply(data: CreateForumReplyRequest) {
  return post<ForumPostItem>('/api/user/forum/replies', data)
}
