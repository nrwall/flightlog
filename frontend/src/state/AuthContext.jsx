import React, { createContext, useContext, useEffect, useState } from 'react'
import api from '../util/api'

const AuthCtx = createContext(null)

export function AuthProvider({ children }) {
  const [token, setToken] = useState(localStorage.getItem('token') || null)
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (token) {
      localStorage.setItem('token', token)
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`
      setLoading(true)
      api.get('/auth/me').then(res => setUser(res.data)).catch(() => {
        setToken(null); localStorage.removeItem('token'); delete api.defaults.headers.common['Authorization']
      }).finally(() => setLoading(false))
    } else {
      localStorage.removeItem('token')
      delete api.defaults.headers.common['Authorization']
      setUser(null)
    }
  }, [token])

  const value = {
    token, setToken,
    user, setUser,
    loading
  }
  return <AuthCtx.Provider value={value}>{children}</AuthCtx.Provider>
}

export function useAuth() {
  return useContext(AuthCtx)
}
