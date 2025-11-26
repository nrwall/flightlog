# FlightLog – Multi-DB Spring Boot + React (Dockerized)

A capstone-ready template: users can register/login and record flights. 
The backend uses **two PostgreSQL databases** (one for auth/users, one for flights). 
The React frontend is built and **served by Spring Boot** as static files.

## Quick start

```bash
# 1) Clone this folder or unzip the provided archive
cp .env.example .env   # then edit .env as needed

# 2) Build and run everything
docker compose up --build
```

Open: http://localhost:8080  (frontend, served by the backend)

Postgres:
- Users DB on `localhost:5433`
- Flights DB on `localhost:5434`

## Development workflow (Ubuntu + Eclipse)

- **Backend:** Import `backend` as a Maven project into Eclipse (Eclipse IDE for Enterprise Java + Spring Tools recommended). Run `FlightLogApplication`.
- **Frontend:** In `frontend/`, run `npm install && npm run dev` to start Vite at `http://localhost:5173`. During dev, API calls proxy to `http://localhost:8080` (see `vite.config.js`).
- For containers, use `docker compose up --build`.

## API endpoints (high level)

- `POST /api/auth/register` – { username, password } → { token, username }
- `POST /api/auth/login` – { username, password } → { token, username }
- `GET  /api/auth/me` – current user info (requires Bearer token)

- `POST /api/flights` – create flight (requires token)
- `GET  /api/flights` – list your flights
- `GET  /api/flights/{id}` – one flight (must be yours)
- `DELETE /api/flights/{id}` – delete a flight (must be yours)

## Data enrichment

When you submit a flight, the backend enriches it:
- Computes `durationMinutes` from `departureTime`/`arrivalTime`
- Infers `airline` from flight number prefix (simple map)
- Flags `isRedEye` (overnight)
- Builds a `route` string like `SFO→JFK`

You can expand enrichment with external APIs later.

## Security

- JWT (HS256) with configurable secret + expiry (`.env`)
- BCrypt password hashing
- CORS allows your dev origin via `FRONTEND_ORIGIN`, but in containers the frontend is same-origin.

## Two databases

- Separate datasources, entity managers and transaction managers
- Users live in the **users DB**, flights in the **flights DB**
- Ownership is enforced by cross-record `userId` (no cross-DB foreign keys)

---

### Production-ish notes

- This template uses Hibernate `ddl-auto=update` to keep schema simple. For a stronger capstone, consider adding **Flyway** for per-DB migrations.
- Add proper logging, metrics, and integration tests for greatness.
