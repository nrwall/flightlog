import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../util/api'

export default function Flights() {
  const [flights, setFlights] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    setLoading(true)
    api.get('/flights').then(res => setFlights(res.data)).catch(() => setError('Failed to load flights')).finally(() => setLoading(false))
  }, [])

  if (loading) return <p>Loading...</p>
  if (error) return <p style={{color:'red'}}>{error}</p>

  return (
    <div>
      <div style={{display:'flex', alignItems:'center', gap:'1rem'}}>
        <h2>Your Flights</h2>
        <Link to="/add"><button>Add Flight</button></Link>
      </div>
      {flights.length === 0 ? (
        <p>No flights yet. Add your first one!</p>
      ) : (
        <table style={{width:'100%', borderCollapse:'collapse'}}>
          <thead>
            <tr>
              <th style={{textAlign:'left'}}>When</th>
              <th>Route</th>
              <th>Flight</th>
              <th>Airline</th>
              <th>Duration</th>
              <th>Seat</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {flights.map(f => (
              <tr key={f.id} style={{borderTop:'1px solid #eee'}}>
                <td style={{padding:'0.5rem 0'}}>{new Date(f.departureTime).toLocaleString()}</td>
                <td>{f.route}</td>
                <td>{f.flightNumber}</td>
                <td>{f.airline || '-'}</td>
                <td>{f.durationMinutes ? `${f.durationMinutes}m` : '-'}</td>
                <td>{f.seatClass || '-'}</td>
                <td>
                  <button onClick={async () => {
                    if (!confirm('Delete this flight?')) return
                    await api.delete(`/flights/${f.id}`)
                    setFlights(prev => prev.filter(x => x.id !== f.id))
                  }}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}
