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
