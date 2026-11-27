# VibeMitra

It's a dating companion app where, in metro cities, girls and boys can spend evenings on coffee dates or casual dates and find weekend partners, all with the help of AI matchmaking. AI will suggest who is the perfect fit for you. And singles can make out on weekends. Also, they can meet for dinner in a secure place. And so many things.

AI-assisted social discovery and matchmaking for Delhi NCR, Android first.

Categories: dating, friendship, serious relationships, roommates, activity partners, weekend partners, travel partners.

## Repository layout

```
backend/              Spring Boot 3.5 / Java 25 REST API (Maven)
mobile-app/           Expo + React Native + TypeScript app
database/migrations/  Flyway SQL migrations (PostgreSQL)
documentation/        Architecture, API, security and deployment notes
```

See [documentation/architecture.md](documentation/architecture.md) and [documentation/security.md](documentation/security.md).

## Status

| Area | Status |
|---|---|
| Database schema V1 | Done (not yet applied to a live PostgreSQL in this environment) |
| Registration, login (JWT), current user, 18+ check, Delhi NCR city check | Done |
| Backend unit tests (JWT, age rule) | Passing (`mvn test`) |
| Mobile login, registration, session storage, home screen | Done (type-checked; not yet run on a device) |
| Profiles, discovery, matching, chat, safety, subscriptions, notifications, OTP | Not started (planned milestones) |

## Prerequisites

- Java 25 LTS (JDK)
- Maven 3.9+
- Node.js 20+ and npm
- PostgreSQL 16 (local, or Supabase free project)

## Run the backend

1. Create a local database:
   ```sql
   CREATE USER vibemitra WITH PASSWORD 'choose-a-local-password';
   CREATE DATABASE vibemitra OWNER vibemitra;
   ```
2. Set environment variables (PowerShell example; never commit them):
   ```powershell
   $env:DB_PASSWORD = "choose-a-local-password"
   $env:JWT_SECRET  = "a-random-string-of-at-least-32-characters"
   ```
   Generate a secret with: `node -e "console.log(require('crypto').randomBytes(48).toString('base64'))"`
3. Start from the `backend/` folder (Flyway resolves `../database/migrations` relative to it):
   ```bash
   cd backend
   mvn spring-boot:run
   ```
4. Check: `GET http://localhost:8080/api/v1/health`

Run tests: `cd backend && mvn test`

## Run the mobile app

```bash
cd mobile-app
cp .env.example .env        # edit EXPO_PUBLIC_API_URL if needed
npm install
npm run start               # then press "a" for Android emulator
```

Android emulator uses `http://10.0.2.2:8080` to reach the backend on your PC.

Type check: `npm run typecheck`

## Configuration

| Variable | Required | Purpose |
|---|---|---|
| `DB_URL` | No | JDBC URL (default `jdbc:postgresql://localhost:5432/vibemitra`) |
| `DB_USERNAME` | No | Default `vibemitra` |
| `DB_PASSWORD` | Yes | Database password |
| `JWT_SECRET` | Yes | HMAC key, at least 32 bytes. The app refuses to start without it |
| `JWT_EXPIRATION_MINUTES` | No | Default 60 |
| `PORT` | No | Default 8080 |
| `EXPO_PUBLIC_API_URL` | Mobile | Backend base URL |

## Secrets

Credentials from the planning document must not be placed in this repository. The planning document is excluded by `.gitignore`. Rotate any password or token that has been shared in plain text.

## Next milestones

1. Profiles, interests and profile photos
2. Matching engine (rule-based scoring first, AI explanations behind a provider interface)
3. Mutual matches, chat, block and report
4. Subscriptions: Google Play Billing with backend verification; Razorpay for web
5. Phone/OTP provider, FCM notifications, account deletion, Play Store readiness