# API Documentation

Base path: `/api/v1`. All bodies are JSON. Authenticated endpoints need `Authorization: Bearer <accessToken>`.

## Implemented

### GET /api/v1/health
Public. Returns `{"status":"UP","service":"vibemitra-backend"}`.

### POST /api/v1/auth/register
Public. Creates an account and returns a token.

Request:
```json
{
  "email": "asha@example.com",
  "password": "at-least-8-chars",
  "displayName": "Asha",
  "dateOfBirth": "1999-04-12",
  "city": "DELHI"
}
```
- `city`: `DELHI`, `NOIDA`, `GHAZIABAD`, `GURUGRAM`, `FARIDABAD`
- `password`: 8–72 characters
- `dateOfBirth`: user must be at least 18 years old (server-side check)

Responses:
- `201 Created` → `{"accessToken":"...","userId":"uuid"}`
- `400` validation failure (`fields` lists each invalid field), or under-18
- `409` email already registered

### POST /api/v1/auth/login
Public.

Request: `{"email":"...","password":"..."}`

Responses:
- `200 OK` → `{"accessToken":"...","userId":"uuid"}`
- `401` for unknown email, wrong password or inactive account (same message in all cases)

### GET /api/v1/users/me
Authenticated. Returns the current account (no password hash).

`200 OK` → `{"id","email","displayName","dateOfBirth","city","status","createdAt"}`

## Error format

```json
{
  "timestamp": "2026-11-27T10:00:00Z",
  "status": 400,
  "message": "Validation failed",
  "fields": { "password": "Password must be 8–72 characters" }
}
```

## Planned

| Group | Endpoints |
|---|---|
| Profile | `GET/PUT /profile`, interests, preferences, photos |
| Discovery | `GET /discovery`, `POST /discovery/{userId}/like`, `POST /discovery/{userId}/pass` |
| Matches and chat | `GET /matches`, `GET/POST /matches/{id}/messages` |
| Safety | `POST /blocks/{userId}`, `DELETE /blocks/{userId}`, `POST /reports` |
| Subscriptions | `GET /subscriptions/me`, `POST /subscriptions/google-play/verify`, `POST /webhooks/razorpay` |
| Notifications | `POST /devices` |
| Account | `DELETE /users/me` (account deletion, required by Google Play) |
