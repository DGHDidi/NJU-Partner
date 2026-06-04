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
