import { NavLink } from 'react-router-dom'
import { useAuth } from '../AuthContext'
import { money } from '../format'

export default function Navbar() {
  const { user, logout } = useAuth()
  return (
    <nav className="navbar">
      <span className="brand">Global Stock Market</span>
      <div className="links">
        <NavLink to="/" end>Stocks</NavLink>
        <NavLink to="/portfolio">Portfolio</NavLink>
        <NavLink to="/transactions">Transactions</NavLink>
      </div>
      <div className="user">
        <span>{user.username} · Cash {money(user.balance)}</span>
        <button className="btn ghost" onClick={logout}>Logout</button>
      </div>
    </nav>
  )
}
