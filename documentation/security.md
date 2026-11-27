# Security and Compliance Checklist

Status column: **Done** = implemented in this repository; **Planned** = required before launch.

## Application security

| Control | Status | Where |
|---|---|---|
| Passwords hashed with BCrypt | Done | `auth/AuthService` |
| JWT signature and expiry validation | Done | `security/JwtService`, `security/JwtAuthenticationFilter` |
| Fail-fast if JWT secret is missing or short (< 32 bytes) | Done | `security/JwtProperties` |
| Generic login error (no account enumeration) | Done | `auth/AuthService` |
| Input validation on all request bodies | Done | Bean Validation annotations on DTOs |
| Parameterized queries only (JPA / prepared statements) | Done | Spring Data repositories |
| Server-side 18+ age check | Done | `auth/AgeRule` |
| Server-side city restriction (Delhi NCR) | Done | `auth/AuthService`, DB `CHECK` constraint |
| Rate limiting on auth endpoints | Planned | Bucket4j or gateway |
| OTP verification | Planned | Provider to be chosen (free-quota first) |
| Secure file uploads (type, size, malware scan) | Planned | Profile photo module |
| Payment webhook signature verification | Planned | `subscription` module |
| Duplicate payment processing prevention (idempotency keys) | Planned | `subscription` module |
| Audit logging of sensitive actions | Planned | `audit_logs` table exists; write path pending |

## Secrets

- Secrets are read from environment variables only: `DB_PASSWORD`, `JWT_SECRET`, AI keys, payment keys, Firebase service account.
- Never commit `.env` files. `.env.example` files contain placeholders only.
- **Rotate any credential that has been written in a planning document, chat, or screenshot.** Passwords and tokens in the planning document should be treated as exposed.

## Google Play policy items

| Item | Status |
|---|---|
| Data Safety form | Planned |
| Account deletion (in-app and web URL) | Planned |
| Block and report features | Planned |
| Minor blocking for matchmaking apps | Planned (age gate in place) |
| Content moderation and prohibited-content policy | Planned |
| Google Play Billing for Android digital subscriptions | Planned |
| Privacy policy URL | Planned (Cloudflare Pages) |
