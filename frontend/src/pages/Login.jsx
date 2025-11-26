import React, { useState } from 'react'
import { useNavigate, useLocation, Link } from 'react-router-dom'
import api from '../util/api'
import { useAuth } from '../state/AuthContext'

export default function Login() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)
  const { setToken } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const from = location.state?.from?.pathname || '/'

  async function handleSubmit(e) {
    e.preventDefault()
    setError(null)
    try {
      const res = await api.post('/auth/login', { username, password })
      setToken(res.data.token)
      navigate(from, { replace: true })
    } catch (err) {
      setError('Invalid username or password')
    }
  }

  return (
    <div style={{maxWidth: 400, margin:'2rem auto'}}>
      <h2>Login</h2>
      <form onSubmit={handleSubmit}>
        <div style={{marginBottom: '1rem'}}>
          <label>Username</label>
          <input value={username} onChange={e=>setUsername(e.target.value)} required style={{width:'100%'}} />
        </div>
        <div style={{marginBottom: '1rem'}}>
          <label>Password</label>
          <input type="password" value={password} onChange={e=>setPassword(e.target.value)} required style={{width:'100%'}} />
        </div>
        {error && <p style={{color:'red'}}>{error}</p>}
        <button type="submit">Login</button>
      </form>
      <p>Need an account? <Link to="/register">Register</Link></p>
    </div>
  )
}
