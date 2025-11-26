import React, { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import api from '../util/api'
import { useAuth } from '../state/AuthContext'

export default function Register() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)
  const { setToken } = useAuth()
  const navigate = useNavigate()

  async function handleSubmit(e) {
    e.preventDefault()
    setError(null)
    try {
      const res = await api.post('/auth/register', { username, password })
      setToken(res.data.token)
      navigate('/')
    } catch (err) {
      const msg = err?.response?.data || 'Registration failed'
      setError(typeof msg === 'string' ? msg : 'Registration failed')
    }
  }

  return (
    <div style={{maxWidth: 400, margin:'2rem auto'}}>
      <h2>Register</h2>
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
        <button type="submit">Create account</button>
      </form>
      <p>Already have an account? <Link to="/login">Login</Link></p>
    </div>
  )
}
