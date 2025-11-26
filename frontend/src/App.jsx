import React from 'react'
import { Routes, Route, Link, Navigate, useLocation } from 'react-router-dom'
import Login from './pages/Login'
import Register from './pages/Register'
import Flights from './pages/Flights'
import AddFlight from './pages/AddFlight'
import ChangePassword from './pages/ChangePassword'
import { useAuth } from './state/AuthContext'

function RequireAuth({ children }) {
  const { token, loading } = useAuth()
  const location = useLocation()
  if (loading) return <p>Loading...</p>
  if (!token) return <Navigate to="/login" state={{ from: location }} replace />
  return children
}

function Nav() {
  const { token, user, setToken } = useAuth()
  return (
    <nav style={{display:'flex', gap:'1rem', padding:'1rem', borderBottom:'1px solid #ddd'}}>
      <Link to="/">Flights</Link>
      {token ? (
        <>
		  <Link to="/changePassword">Change Password</Link>
          <span style={{marginLeft:'auto'}}>Logged in as <strong>{user?.username}</strong></span>
          <button onClick={() => setToken(null)}>Logout</button>
        </>
      ) : (
        <span style={{marginLeft:'auto'}}>
          <Link to="/login">Login</Link> | <Link to="/register">Register</Link>
        </span>
      )}
    </nav>
  )
}

export default function App() {
  return (
    <div>
      <Nav />
      <div style={{padding:'1rem'}}>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/add" element={<RequireAuth><AddFlight /></RequireAuth>} />
          <Route path="/" element={<RequireAuth><Flights /></RequireAuth>} />
		  <Route path="/changePassword" element={<RequireAuth><ChangePassword /></RequireAuth>} />
        </Routes>
      </div>
    </div>
  )
}
