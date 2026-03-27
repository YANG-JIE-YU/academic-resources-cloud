export function formatDateTime(value?: string) {
  if (!value) {
    return '-'
  }
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return value
  }
  return new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date)
}

/**
 * 将后端返回的时间字符串转换为 Naive UI DatePicker 的 formatted-value 格式：
 * yyyy-MM-dd HH:mm:ss
 */
export function toDateTimePickerValue(value?: string | null) {
  if (!value) {
    return ''
  }
  const raw = value.trim()
  if (!raw) {
    return ''
  }

  const withSpace = raw.replace('T', ' ')
  const stripped = withSpace
    .replace(/\.\d{1,9}/, '')
    .replace(/Z$/, '')
    .replace(/[+-]\d{2}:\d{2}$/, '')

  const fullMatch = stripped.match(/^(\d{4}-\d{2}-\d{2} \d{2}:\d{2}):(\d{2})$/)
  if (fullMatch) {
    return `${fullMatch[1]}:${fullMatch[2]}`
  }

  const minuteMatch = stripped.match(/^(\d{4}-\d{2}-\d{2} \d{2}:\d{2})$/)
  if (minuteMatch) {
    return `${minuteMatch[1]}:00`
  }

  return stripped
}

export function ellipsis(text: string, max = 80) {
  if (!text) {
    return ''
  }
  if (text.length <= max) {
    return text
  }
  return `${text.slice(0, max)}...`
}
