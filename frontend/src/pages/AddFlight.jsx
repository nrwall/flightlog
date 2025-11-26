import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../util/api'

export default function AddFlight() {
  const [form, setForm] = useState({
    flightNumber: '',
    origin: '',
    destination: '',
    departureTime: '',
    arrivalTime: '',
    seatClass: '',
    notes: ''
  })
  const [error, setError] = useState(null)
  const navigate = useNavigate()

  function update(k, v) {
    setForm(prev => ({...prev, [k]: v}))
  }

  async function submit(e) {
    e.preventDefault()
    setError(null)
    try {
      // Convert datetime-local to ISO
      const payload = {
        ...form,
        departureTime: new Date(form.departureTime).toISOString(),
        arrivalTime: new Date(form.arrivalTime).toISOString()
      }
      await api.post('/flights', payload)
      navigate('/')
    } catch (err) {
      setError('Failed to save flight. Check your inputs.')
    }
  }

  return (
    <div style={{maxWidth: 600}}>
      <h2>Add Flight</h2>
      <form onSubmit={submit}>
        <div style={{display:'grid', gap:'1rem', gridTemplateColumns:'1fr 1fr'}}>
          <div>
            <label>Flight Number</label>
            <input value={form.flightNumber} onChange={e=>update('flightNumber', e.target.value)} required />
          </div>
          <div>
            <label>Seat Class</label>
            <input value={form.seatClass} onChange={e=>update('seatClass', e.target.value)} placeholder="Economy/Premium/Business/First" />
          </div>
          <div>
            <label>Origin</label>
            <input value={form.origin} onChange={e=>update('origin', e.target.value)} required placeholder="e.g. SFO" />
          </div>
          <div>
            <label>Destination</label>
            <input value={form.destination} onChange={e=>update('destination', e.target.value)} required placeholder="e.g. JFK" />
          </div>
          <div>
            <label>Departure</label>
            <input type="datetime-local" value={form.departureTime} onChange={e=>update('departureTime', e.target.value)} required />
          </div>
          <div>
            <label>Arrival</label>
            <input type="datetime-local" value={form.arrivalTime} onChange={e=>update('arrivalTime', e.target.value)} required />
          </div>
        </div>
        <div style={{marginTop:'1rem'}}>
          <label>Notes</label>
          <textarea value={form.notes} onChange={e=>update('notes', e.target.value)} style={{width:'100%', minHeight:'100px'}} />
        </div>
        {error && <p style={{color:'red'}}>{error}</p>}
        <div style={{marginTop:'1rem'}}>
          <button type="submit">Save Flight</button>
        </div>
      </form>
    </div>
  )
}
