export type UserRoleCode = 'USER' | 'ADMIN'

export interface UserInfo {
  id: number
  username: string
  realName?: string
  phone?: string
  gender?: string
  avatar?: string
  status?: number
  role?: string
  createTime?: string
  updateTime?: string
}

export interface FavoriteItem {
  id: number
  userId: number
  targetType: string
  targetId: number
  targetTitle: string
  targetCover?: string
  createTime: string
}

export interface AddFavoriteRequest {
  targetType: string
  targetId: number
  targetTitle: string
  targetCover?: string
}

export interface CommentItem {
  id: number
  userId: number
  username?: string
  targetType: string
  targetId: number
  content: string
  replyContent?: string
  createTime: string
}

export interface CreateCommentRequest {
  targetType: string
  targetId: number
  content: string
}

export interface ForumPostItem {
  id: number
  parentId?: number
  userId: number
  title?: string
  content: string
  isTop?: number
  status?: string
  createTime: string
}

export interface CreateForumPostRequest {
  title: string
  content: string
}

export interface CreateForumReplyRequest {
  parentId: number
  content: string
}

export interface AdminUserCreateRequest {
  username: string
  password: string
  realName?: string
  phone?: string
  gender?: string
  status: number
  role: UserRoleCode
}

export interface AdminUserUpdateRequest {
  username: string
  password?: string
  realName?: string
  phone?: string
  gender?: string
  status: number
  role: UserRoleCode
}

export interface AdminCommentReplyRequest {
  replyContent: string
}
