# financial-app-notifications

Notifications microservice — consumes Kafka events, sends email and browser notifications via SSE. Scheduled jobs check for upcoming payments and expirations.

## Port: 8084

## Database Schema: `notifications`

## Endpoints
```
GET    /api/v1/notifications
PUT    /api/v1/notifications/{id}/read
PUT    /api/v1/notifications/read-all
GET    /api/v1/notifications/stream      ← SSE
GET    /api/v1/notifications/rules
POST   /api/v1/notifications/rules
PUT    /api/v1/notifications/rules/{id}
```

## Kafka — Consumes
- `payment.due`
- `card.expiring`
- `card.statement.uploaded`
- `loan.reminder`
- `installment.reminder`

## Environment Variables
See `.env.example`.

## Local Development

```bash
cd ../financial-app-parent && mvn install -N
cd ../financial-app-notifications
cp .env.example .env
mvn spring-boot:run
```

## Swagger
`http://localhost:8084/swagger-ui.html`

## CI/CD

| Workflow | Trigger | Does |
|---|---|---|
| `ci.yml` | PRs; push to develop/master | tests + docker build via shared `backend-ci.yml` |
| `docker-publish.yml` | push to master; `v*` tags | GHCR publish: `latest`, `sha-*`, semver on tags |
| `release.yml` | manual (bump dropdown) | next `vX.Y.Z` tag + Release + versioned publish |

Reusable workflows live in the root repo `Sergio-Smirnoff/financial-app`.
