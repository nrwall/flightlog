# FlightLog a multi-DB Spring Boot and React Dockerized Web Application

- Users can register/login and record flights. 
- The backend uses two PostgreSQL databases (one for auth/users, one for flights). 
- The React frontend is built and served by Spring Boot.

## Quick start

```bash
# 1) Clone this folder
cp .env.example .env   # then edit .env as needed

# 2) Build and run everything
docker compose up --build
```

Open: http://localhost:8080  (Currently only backend. Interact via postman)

Postgres:
- Users DB on `localhost:5433`
- Flights DB on `localhost:5434`

## API endpoints (high level)

- `POST /api/auth/register` -- { username, password } -> { token, username }
- `POST /api/auth/login` -- { username, password } -> { token, username }
- `GET  /api/auth/me` -- current user info (requires Bearer token)

## Data enrichment

- Planned Data Enrichment includes the following:
	- Compute flight duration
	- Determines Airline from flight letter prefix (ex: DL -> Delta Air Lines)
	- Flags isRedEye if flight is overnight
	- Builds a route string (ex: ATL->SAT)

- Additional Possible Enrichments
	- Queries open-source flight data (if available and not cost prohibitive) to accurately update detailed flight information
		- Actual duration
		- Miles flown
		- Delays?
	
## Security

- JWT (HS256) with configurable secret + expiry (`.env`)
- BCrypt password hashing

## Two databases

- Separate datasources, entity managers and transaction managers
- Users live in the **users DB**, flights in the **flights DB**
- Ownership is enforced by cross-record `userId` (no cross-DB foreign keys)

---