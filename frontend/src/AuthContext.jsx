import { createContext, useContext, useState, useCallback } from 'react'
import api from './api'

const AuthContext = createContext(null)

function loadUser() {
  try {
    return JSON.parse(localStorage.getItem('user'))
  } catch {
    return null
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(loadUser)

  const save = (u) => {
    setUser(u)
    if (u) localStorage.setItem('user', JSON.stringify(u))
    else localStorage.removeItem('user')
  }

  const login = async (username, password) => {
    const { data } = await api.post('/auth/login', { username, password })
    save(data)
  }

  const register = async (username, email, password) => {
    const { data } = await api.post('/auth/register', { username, email, password })
    save(data)
  }

  const logout = () => save(null)

  const refreshUser = useCallback(async () => {
    const id = loadUser()?.id
    if (!id) return
    const { data } = await api.get(`/auth/users/${id}`)
    save(data)
  }, [])

  return (
    <AuthContext.Provider value={{ user, login, register, logout, refreshUser }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
