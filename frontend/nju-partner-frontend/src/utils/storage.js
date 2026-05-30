export function getStoredJson(key, fallback = null) {
  try {
    const raw = localStorage.getItem(key)
    if (raw === null || raw === '' || raw === 'undefined') {
      return fallback
    }
    return JSON.parse(raw)
  } catch {
    localStorage.removeItem(key)
    return fallback
  }
}
