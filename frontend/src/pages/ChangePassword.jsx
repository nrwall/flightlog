import React, { useState } from 'react'
import api from '../util/api'

export default function ChangePassword() {
  const [currentPassword, setCurrentPassword] = useState('')
  const [newPassword, setNewPassword] = useState('')
  const [msg, setMsg] = useState(null)
  const [err, setErr] = useState(null)

  const submit = async (e) => {
    e.preventDefault()
    setErr(null); setMsg(null)
    try {
      const res = await api.post('/auth/changePassword', { currentPassword, newPassword })
      // optional: replace stored token with the fresh one returned by backend
      if (res.data?.token) {
        localStorage.setItem('token', res.data.token)
        api.defaults.headers.common['Authorization'] = `Bearer ${res.data.token}`
      }
      setMsg('Password updated.')
      setCurrentPassword(''); setNewPassword('')
    } catch (e) {
      setErr(e?.response?.data || 'Failed to change password')
    }
  }

  return (
    <div style={{maxWidth: 420, margin:'2rem auto'}}>
      <h2>Change Password</h2>
      <form onSubmit={submit}>
        <div><label>Current password</label>
          <input type="password" value={currentPassword} onChange={e=>setCurrentPassword(e.target.value)} required style={{width:'100%'}} />
        </div>
        <div style={{marginTop:'1rem'}}><label>New password</label>
          <input type="password" value={newPassword} onChange={e=>setNewPassword(e.target.value)} required style={{width:'100%'}} />
        </div>
        {err && <p style={{color:'red'}}>{err}</p>}
        {msg && <p style={{color:'green'}}>{msg}</p>}
        <button style={{marginTop:'1rem'}} type="submit">Update</button>
      </form>
    </div>
  )
}
