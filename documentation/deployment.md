# Deployment Notes

Status: development only. Production hosting is decided in a later milestone.

## Development environment

| Component | How |
|---|---|
| PostgreSQL | Local install, or Supabase free project (pauses after inactivity) |
| Backend | `mvn spring-boot:run` from `backend/` with env vars set |
| Mobile | `npx expo start`; Android emulator or Expo Go on a device |
| Privacy page | Cloudflare Pages (free) once the policy is written |

## Environment separation

- `dev`: local database, test payment keys, Play Billing test accounts.
- `prod`: separate database with backups enabled, live payment keys, production JWT secret.
- Secrets are injected as environment variables by the host. None are stored in the repository.

## Before any production release

1. Apply migrations to an empty database and confirm Flyway reports success.
2. Confirm `JWT_SECRET` is unique to the environment and at least 32 bytes.
3. Enable TLS on the API domain; the mobile app must not allow cleartext traffic in release builds.
4. Turn on database backups and test one restore.
5. Build a signed Android App Bundle (`.aab`) with a release keystore kept outside Git.
6. Complete the Play Console checklist in [security.md](security.md).

## Migration rules

- Never edit an applied migration. Add `V2__...sql`, `V3__...sql`, and so on.
- Hibernate runs with `ddl-auto: validate`; schema changes go through Flyway only.
