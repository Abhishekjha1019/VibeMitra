# VibeMitra Architecture

Status: Foundation (Phase 1–2 of the development plan). Source of truth for decisions: the blueprint document kept outside Git.

## Stack

| Layer | Choice | Notes |
|---|---|---|
| Mobile | React Native + Expo + TypeScript | Android first; iOS later from the same code |
| Backend | Java 25 LTS + Spring Boot 3.5 | Business logic, auth, entitlements, payment verification |
| Database | PostgreSQL 16 | Schema managed by Flyway migrations in `database/migrations` |
| Auth | JWT (HS256) + BCrypt password hashing | Stateless; role-based authorization planned |
| Notifications | Firebase Cloud Messaging | Device tokens stored per user; not yet wired |
| AI | Provider-agnostic service interface | Implementation to be chosen; keys server-side only |
| Payments | Google Play Billing (Android), Razorpay (web) | Entitlements verified on backend |
| Hosting (dev) | Local PostgreSQL; Supabase free tier optional | Free-tier pauses are acceptable for development only |

## Repository layout

```
VibeMitra/
├── backend/            Spring Boot API (Maven)
├── mobile-app/         Expo / React Native app (TypeScript)
├── database/
│   └── migrations/     Flyway SQL migrations (V1__core_schema.sql, ...)
├── documentation/      Architecture, API, security, deployment
└── README.md
```

## Backend modules

Package root: `com.vibemitra`

| Package | Responsibility | Status |
|---|---|---|
| `auth` | Registration, login, token issuance | Implemented |
| `user` | Account entity and repository, current-user lookup | Implemented |
| `security` | JWT service, authentication filter, security config | Implemented |
| `common` | Shared errors and API exception mapping | Implemented |
| `profile` | Profiles, interests, preferences | Planned |
| `matching` | Discovery, compatibility scoring, AI explanations | Planned |
| `chat` | Mutual-match messaging | Planned |
| `safety` | Block, report, moderation queue | Planned |
| `subscription` | Plans, Play Billing verification, Razorpay webhooks | Planned |
| `notification` | FCM device registration and sending | Planned |

## Data model (V1)

`users`, `profiles`, `interests`, `user_interests`, `matches`, `messages`, `blocks`, `reports`, `subscriptions`, `device_tokens`, `audit_logs`.

Design rules:
- UUID primary keys for user-facing entities; `bigserial` for reference/audit tables.
- Pairs (`matches`) are stored in canonical order (`user_low_id < user_high_id`) so each pair exists once.
- City is constrained to the Delhi NCR launch list. Expansion means extending the constraint or moving cities into a table; no redesign of other tables.
- Subscription entitlements are read from `subscriptions`; the client never decides premium access.

## Launch scope

- Cities: Delhi, Noida, Ghaziabad, Gurugram, Faridabad.
- Minimum age: 18 (enforced at registration on the server).
- Free tier stays available; ₹299 (Premium) and ₹399 (Premium Plus) are paid entitlements.

## Request flow (authenticated)

1. Mobile app stores the JWT in `expo-secure-store`.
2. Each request sends `Authorization: Bearer <token>`.
3. `JwtAuthenticationFilter` validates the signature and expiry, then sets the authenticated user ID.
4. Controllers read the current user from the security context, never from request parameters.
