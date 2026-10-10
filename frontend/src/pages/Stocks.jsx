import { useEffect, useState } from 'react'
import api, { errorMessage } from '../api'
import { useAuth } from '../AuthContext'
import { money } from '../format'

// Tolerant of different field names on the Stock model.
const priceOf = (s) => s.price ?? s.currentPrice ?? s.lastPrice ?? 0
const nameOf = (s) => s.companyName ?? s.name ?? ''

export default function Stocks() {
  const { user, refreshUser } = useAuth()
  const [stocks, setStocks] = useState([])
  const [countries, setCountries] = useState([])
  const [country, setCountry] = useState('')
  const [qty, setQty] = useState({})
  const [msg, setMsg] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.get('/stocks').then(({ data }) => {
      setCountries([...new Set(data.map((s) => s.country).filter(Boolean))].sort())
    }).catch(() => {})
  }, [])

  useEffect(() => {
    setLoading(true)
    api.get('/stocks', { params: country ? { country } : {} })
      .then(({ data }) => setStocks(data))
      .catch((e) => setMsg({ type: 'error', text: errorMessage(e) }))
      .finally(() => setLoading(false))
  }, [country])

  const trade = async (side, symbol) => {
    const quantity = parseInt(qty[symbol] || 1, 10)
    setMsg(null)
    try {
      const { data } = await api.post(`/users/${user.id}/${side}`, { symbol, quantity })
      setMsg({ type: 'ok', text: `${side === 'buy' ? 'Bought' : 'Sold'} ${quantity} × ${symbol} at ${money(data.price)} (total ${money(data.total)})` })
      await refreshUser()
    } catch (e) {
      setMsg({ type: 'error', text: errorMessage(e) })
    }
  }

  return (
    <div className="card">
      <div className="row">
        <h2>Stocks</h2>
        <select value={country} onChange={(e) => setCountry(e.target.value)}>
          <option value="">All countries</option>
          {countries.map((c) => <option key={c}>{c}</option>)}
        </select>
      </div>
      {msg && <p className={msg.type === 'ok' ? 'success' : 'error'}>{msg.text}</p>}
      {loading ? <p>Loading…</p> : (
        <div className="table-wrap">
          <table>
            <thead>
              <tr><th>Symbol</th><th>Company</th><th>Country</th><th className="num">Price</th><th>Qty</th><th></th></tr>
            </thead>
            <tbody>
              {stocks.map((s) => (
                <tr key={s.symbol}>
                  <td><b>{s.symbol}</b></td>
                  <td>{nameOf(s)}</td>
                  <td>{s.country}</td>
                  <td className="num">{money(priceOf(s))}</td>
                  <td>
                    <input className="qty" type="number" min="1" value={qty[s.symbol] ?? 1}
                      onChange={(e) => setQty({ ...qty, [s.symbol]: e.target.value })} />
                  </td>
                  <td className="actions">
                    <button className="btn buy" onClick={() => trade('buy', s.symbol)}>Buy</button>
                    <button className="btn sell" onClick={() => trade('sell', s.symbol)}>Sell</button>
                  </td>
                </tr>
              ))}
              {stocks.length === 0 && <tr><td colSpan="6">No stocks found.</td></tr>}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
