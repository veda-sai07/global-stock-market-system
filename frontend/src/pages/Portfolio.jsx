import { useEffect, useState } from 'react'
import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from 'recharts'
import api, { errorMessage } from '../api'
import { useAuth } from '../AuthContext'
import { money } from '../format'

const COLORS = ['#2563eb', '#16a34a', '#f59e0b', '#dc2626', '#7c3aed', '#0891b2', '#db2777', '#65a30d']

export default function Portfolio() {
  const { user } = useAuth()
  const [p, setP] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    api.get(`/users/${user.id}/portfolio`)
      .then(({ data }) => setP(data))
      .catch((e) => setError(errorMessage(e)))
  }, [user.id, user.balance])

  if (error) return <p className="error">{error}</p>
  if (!p) return <p>Loading…</p>

  const chart = p.holdings.map((h) => ({ name: h.symbol, value: Number(h.marketValue) }))

  return (
    <>
      <div className="stats">
        <div className="card stat"><span>Cash</span><b>{money(p.cashBalance)}</b></div>
        <div className="card stat"><span>Invested</span><b>{money(p.investedValue)}</b></div>
        <div className="card stat"><span>Total value</span><b>{money(p.totalValue)}</b></div>
      </div>

      <div className="card">
        <h2>Holdings</h2>
        {p.holdings.length === 0 ? <p>You don't own any stocks yet. Go to the Stocks page to buy some.</p> : (
          <>
            <div style={{ height: 280 }}>
              <ResponsiveContainer>
                <PieChart>
                  <Pie data={chart} dataKey="value" nameKey="name" outerRadius={100} label>
                    {chart.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
                  </Pie>
                  <Tooltip formatter={(v) => money(v)} />
                  <Legend />
                </PieChart>
              </ResponsiveContainer>
            </div>
            <div className="table-wrap">
              <table>
                <thead>
                  <tr><th>Symbol</th><th>Company</th><th className="num">Qty</th><th className="num">Avg price</th>
                    <th className="num">Current</th><th className="num">Value</th><th className="num">P/L</th></tr>
                </thead>
                <tbody>
                  {p.holdings.map((h) => (
                    <tr key={h.symbol}>
                      <td><b>{h.symbol}</b></td><td>{h.companyName}</td>
                      <td className="num">{h.quantity}</td>
                      <td className="num">{money(h.averagePrice)}</td>
                      <td className="num">{money(h.currentPrice)}</td>
                      <td className="num">{money(h.marketValue)}</td>
                      <td className={`num ${Number(h.profitLoss) >= 0 ? 'pos' : 'neg'}`}>{money(h.profitLoss)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </>
        )}
      </div>
    </>
  )
}
