import { useEffect, useState } from 'react'
import api, { errorMessage } from '../api'
import { useAuth } from '../AuthContext'
import { money } from '../format'

export default function Transactions() {
  const { user } = useAuth()
  const [rows, setRows] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    api.get(`/users/${user.id}/transactions`)
      .then(({ data }) => setRows(data))
      .catch((e) => setError(errorMessage(e)))
  }, [user.id])

  if (error) return <p className="error">{error}</p>
  if (!rows) return <p>Loading…</p>

  return (
    <div className="card">
      <h2>Transaction history</h2>
      <div className="table-wrap">
        <table>
          <thead>
            <tr><th>Date</th><th>Type</th><th>Symbol</th><th className="num">Qty</th><th className="num">Price</th><th className="num">Total</th></tr>
          </thead>
          <tbody>
            {[...rows].sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp)).map((t) => (
              <tr key={t.id}>
                <td>{new Date(t.timestamp).toLocaleString()}</td>
                <td className={String(t.type).toUpperCase() === 'BUY' ? 'pos' : 'neg'}>{t.type}</td>
                <td><b>{t.symbol}</b></td>
                <td className="num">{t.quantity}</td>
                <td className="num">{money(t.price)}</td>
                <td className="num">{money(t.total)}</td>
              </tr>
            ))}
            {rows.length === 0 && <tr><td colSpan="6">No transactions yet.</td></tr>}
          </tbody>
        </table>
      </div>
    </div>
  )
}
