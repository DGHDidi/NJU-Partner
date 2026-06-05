export function formatDateTime(value) {
  if (!value) {
    return '-'
  }

  const text = String(value).replace('T', ' ')
  const match = text.match(/^(\d{4})-(\d{2})-(\d{2})\s+(\d{2}):(\d{2})(?::(\d{2}))?/)
  if (!match) {
    return text
  }

  const [, year, month, day, hour, minute, second = '00'] = match
  return `${year}年${month}月${day}日 ${hour}:${minute}:${second}`
}

export function formatRelativeTime(value) {
  if (!value) return '-'

  const date = new Date(String(value).replace(' ', 'T'))
  const timestamp = date.getTime()
  if (Number.isNaN(timestamp)) return formatDateTime(value)

  const diff = Date.now() - timestamp
  if (diff < 0) return formatDateTime(value)

  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour

  if (diff < minute) return '刚刚'
  if (diff < hour) return `${Math.floor(diff / minute)}分钟前`
  if (diff < day) return `${Math.floor(diff / hour)}小时前`
  if (diff < 7 * day) return `${Math.floor(diff / day)}天前`

  return formatDateTime(value)
}
