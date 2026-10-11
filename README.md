# Global Stock Market System

A full-stack stock trading simulator. Users register, get a virtual cash balance, buy and sell stocks from markets around the world, and track their portfolio and trade history.

## Live demo

- **App:** https://global-stock-market-system.vercel.app
- **API:** https://global-stock-market-system.onrender.com/api/stocks

> The backend runs on a free plan and sleeps after about 15 minutes of inactivity. The first request after a quiet period can take around a minute while it wakes up.

## Features

- Register and log in
- Browse stocks from the USA, India, Japan, Germany and the UK, with a country filter
- Buy and sell shares, with the cash balance updating immediately
- Portfolio page with cash, invested value, total value, a holdings table and a pie chart
- Transaction history, newest first
- Validation and clear error messages (for example, selling shares you do not own)

## Tech stack

| Layer | Technology |
|---|---|
| Frontend | React (Vite), React Router, Axios, Recharts |
| Backend | Spring Boot 3.3, Java 21, Spring Data JPA, Bean Validation |
| Database | MySQL (Aiven) in production, H2 in-memory for local development |
| Hosting | Vercel (frontend), Render with Docker (backend) |

## Project structure

```
Global-Stock-Market-System/
├── backend/    Spring Boot API (controllers, services, repositories, DTOs)
└── frontend/   React app (pages, components, auth context)
```

## API endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Create an account |
| POST | `/api/auth/login` | Log in |
| GET | `/api/auth/users/{id}` | Get a user and balance |
| GET | `/api/stocks` | List stocks (optional `?country=India`) |
| GET | `/api/stocks/{symbol}` | Get one stock |
| POST | `/api/users/{userId}/buy` | Buy shares (`{ "symbol": "AAPL", "quantity": 2 }`) |
| POST | `/api/users/{userId}/sell` | Sell shares |
| GET | `/api/users/{userId}/portfolio` | Portfolio summary and holdings |
| GET | `/api/users/{userId}/transactions` | Transaction history |

## Run locally

You need Java 21 and Node.js.

**Backend** (starts on port 8080 with an in-memory H2 database):

```
cd backend
./mvnw spring-boot:run
```

On Windows PowerShell use `.\mvnw spring-boot:run`.

**Frontend** (starts on http://localhost:5173):

```
cd frontend
npm install
npm run dev
```

By default the frontend calls `http://localhost:8080`. To point it elsewhere, copy `frontend/.env.example` to `frontend/.env` and set `VITE_API_URL`.

## Configuration

The backend reads these environment variables. All of them have local defaults, so none are needed for local development.

| Variable | Purpose |
|---|---|
| `DB_URL` | JDBC URL, for example `jdbc:mysql://HOST:PORT/DB?sslMode=REQUIRED&serverTimezone=UTC` |
| `DB_USERNAME` | Database user |
| `DB_PASSWORD` | Database password |
| `CORS_ALLOWED_ORIGINS` | Comma-separated frontend URLs allowed to call the API |
| `PORT` | Server port (default 8080) |

The frontend uses one variable, set at build time:

| Variable | Purpose |
|---|---|
| `VITE_API_URL` | Base URL of the backend, with no trailing slash |

## Deployment

- **Backend:** Render web service using `backend/Dockerfile`, with Root Directory set to `backend`. Set the environment variables above in the Render dashboard.
- **Frontend:** Vercel project with Root Directory set to `frontend`, build command `npm run build` and output directory `dist`. Set `VITE_API_URL` to the Render URL.
- **Database:** Aiven for MySQL (free plan).

Both Render and Vercel redeploy automatically when changes are pushed to `main`.

## Known limitations

- Stock prices are fixed seed data and do not change, so profit and loss always show as 0.
- There are no login tokens. Anyone who knows a user ID could call that user's endpoints, so this is suitable for a demo or learning project but not for real use. Adding Spring Security with JWT would be the next step.
- Transaction times are stored in the server's timezone (UTC on Render).
