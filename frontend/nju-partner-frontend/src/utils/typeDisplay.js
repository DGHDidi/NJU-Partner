const TYPE_EMOJI_RULES = [
  { keywords: ['运动', '羽毛球', '跑步', '健身', '球'], emoji: '🏃' },
  { keywords: ['竞赛', '比赛', '组队'], emoji: '🏆' },
  { keywords: ['自习', '学习', '课程'], emoji: '📚' },
  { keywords: ['饭', '拼单'], emoji: '🍜' },
  { keywords: ['讲座'], emoji: '🎤' },
  { keywords: ['出行', '同行', '校区'], emoji: '🚇' },
  { keywords: ['社团', '活动'], emoji: '✨' },
]

export function getTypeEmoji(type = '') {
  const matched = TYPE_EMOJI_RULES.find((rule) =>
    rule.keywords.some((keyword) => type.includes(keyword)),
  )
  return matched?.emoji || '📝'
}

export function formatTypeLabel(type = '') {
  if (!type) return '-'
  return `${getTypeEmoji(type)} ${type}`
}
