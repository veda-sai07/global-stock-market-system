import axios from 'axios'

const base = (import.meta.env.VITE_API_URL || 'http://localhost:8080').replace(/\/$/, '')

const api = axios.create({ baseURL: `${base}/api` })

export function errorMessage(err) {
  const d = err?.response?.data
  if (d) {
    if (typeof d === 'string') return d
    if (d.message) return d.message
    if (d.error) return d.error
    if (typeof d === 'object') {
      const first = Object.values(d)[0]
      if (typeof first === 'string') return first
    }
  }
  if (err?.code === 'ERR_NETWORK') return 'Cannot reach the server. Is the backend running?'
  return 'Something went wrong. Please try again.'
}

export default api
