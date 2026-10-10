import { useState } from 'react'
import { useAuth } from '../AuthContext'
import { errorMessage } from '../api'

export default function Login() {
  const { login, register } = useAuth()
  const [mode, setMode] = useState('login')
  const [form, setForm] = useState({ username: '', email: '', password: '' })
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value })

  const submit = async (e) => {
    e.preventDefault()
    setError('')
    setBusy(true)
    try {
      if (mode === 'login') await login(form.username, form.password)
      else await register(form.username, form.email, form.password)
    } catch (err) {
      setError(errorMessage(err))
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="card auth-card">
      <h2>{mode === 'login' ? 'Login' : 'Create account'}</h2>
      <form onSubmit={submit}>
        <input placeholder="Username" value={form.username} onChange={set('username')} required />
        {mode === 'register' && (
          <input type="email" placeholder="Email" value={form.email} onChange={set('email')} required />
        )}
        <input type="password" placeholder="Password" value={form.password} onChange={set('password')} required />
        {error && <p className="error">{error}</p>}
        <button className="btn" disabled={busy}>
          {busy ? 'Please wait…' : mode === 'login' ? 'Login' : 'Register'}
        </button>
      </form>
      <p className="muted">
        {mode === 'login' ? 'No account? ' : 'Already registered? '}
        <a href="#" onClick={(e) => { e.preventDefault(); setError(''); setMode(mode === 'login' ? 'register' : 'login') }}>
          {mode === 'login' ? 'Register' : 'Login'}
        </a>
      </p>
    </div>
  )
}
