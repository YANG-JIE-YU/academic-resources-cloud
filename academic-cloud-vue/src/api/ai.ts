import { get, post } from '@/utils/request'
import type { AdminAiLogItem, ChatResponse, RecommendResponse, SummaryResponse } from '@/types/ai'
import { buildAuthorizationHeader, getToken } from '@/utils/auth'

export function chatApi(question: string) {
  return post<ChatResponse>('/api/ai/chat', { question })
}

interface ChatStreamOptions {
  signal?: AbortSignal
  onChunk?: (chunk: string) => void
  onDone?: () => void
}

export async function chatStreamApi(question: string, options?: ChatStreamOptions) {
  const token = getToken()
  const headers: Record<string, string> = {
    'Content-Type': 'application/json'
  }
  if (token) {
    headers.Authorization = buildAuthorizationHeader(token)
  }

  const response = await fetch('/api/ai/chat/stream', {
    method: 'POST',
    headers,
    body: JSON.stringify({ question }),
    signal: options?.signal
  })
  if (!response.ok) {
    const errorText = await response.text()
    throw new Error(errorText || `HTTP ${response.status}`)
  }
  if (!response.body) {
    throw new Error('No response stream from server')
  }

  const reader = response.body.getReader()
  const decoder = new TextDecoder('utf-8')
  let buffer = ''

  while (true) {
    const { value, done } = await reader.read()
    if (done) {
      break
    }
    buffer += decoder.decode(value, { stream: true })
    const lines = buffer.split(/\r?\n/)
    buffer = lines.pop() || ''
    for (const line of lines) {
      const text = line.trim()
      if (!text.startsWith('data:')) {
        continue
      }
      const data = text.substring(5).trim()
      if (!data) {
        continue
      }
      if (data === '[DONE]') {
        options?.onDone?.()
        return
      }
      options?.onChunk?.(data)
    }
  }
  options?.onDone?.()
}

export function summaryApi(text: string) {
  return post<SummaryResponse>('/api/ai/paper/summary', { text })
}

export function keywordsApi(text: string) {
  return post<string[]>('/api/ai/paper/keywords', { text })
}

export function recommendApi(params: { scene?: string; query?: string; limit?: number }) {
  return get<RecommendResponse>('/api/ai/recommend', params)
}

export function fetchAdminAiLogs(params?: { type?: string; limit?: number }) {
  return get<AdminAiLogItem[]>('/api/ai/admin/logs', params)
}
