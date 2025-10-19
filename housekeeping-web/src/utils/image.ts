const BASE64_PREFIX = /^data:(.*?);base64,/i

const decodeBase64 = (value: string): string | null => {
  if (typeof globalThis !== 'undefined' && typeof globalThis.atob === 'function') {
    return globalThis.atob(value)
  }
  return null
}

export function createObjectUrlFromDataUrl(dataUrl: string): string {
  if (!dataUrl || typeof dataUrl !== 'string') {
    return dataUrl
  }
  const match = dataUrl.match(BASE64_PREFIX)
  if (!match) {
    return dataUrl
  }
  const mimeType = match[1] || 'application/octet-stream'
  const base64Part = dataUrl.slice(match[0].length)
  const binary = decodeBase64(base64Part)
  if (binary == null) {
    return dataUrl
  }
  try {
    const length = binary.length
    const bytes = new Uint8Array(length)
    for (let i = 0; i < length; i += 1) {
      bytes[i] = binary.charCodeAt(i)
    }
    const blob = new Blob([bytes], { type: mimeType })
    return URL.createObjectURL(blob)
  } catch (error) {
    console.error('无法转换头像数据为对象 URL', error)
    return dataUrl
  }
}

export function revokeObjectUrl(url: string | null | undefined) {
  if (url && url.startsWith('blob:')) {
    URL.revokeObjectURL(url)
  }
}
