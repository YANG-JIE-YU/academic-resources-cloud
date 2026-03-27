import type { PaperItem } from '@/types/content'

export interface ChatRequest {
  question: string
}

export interface ChatResponse {
  answer: string
}

export interface TextProcessRequest {
  text: string
}

export interface SummaryResponse {
  summary: string
}

export interface RecommendResponse {
  scene: string
  trigger: string
  papers: PaperItem[]
}

export interface AdminAiLogItem {
  id: number
  userId: number
  logType: string
  triggerText: string
  createTime: string
}
