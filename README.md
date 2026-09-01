# ms-notifications

Centralized notification hub. Consumes domain events from Kafka (ms-users, ms-finances, ms-banks, ms-investments), persists each notification to PostgreSQL, pushes it in real time to connected browsers via SSE, and sends email where configured. A scheduler fires monthly summary emails and a nightly cleanup purges stale records.

**Port:** 8084 | **Schema:** `notifications` | **Group ID:** `notifications-group`

> Full design: `docs/specs/services/ms-notifications.md` (parent workspace).

---

## Tech Stack

Java 21 · Spring Boot 3.4.2 · Apache Kafka · SSE (`SseEmitter`) · Spring Data JPA · Flyway · Thymeleaf (email) · PostgreSQL

---

## File Distribution

```
ms-notifications/src/main/java/com/financialapp/notifications/
├── NotificationsApplication.java
│
├── domain/
│   ├── event/
│   │   ├── BankAlert.java
│   │   ├── InstallmentReminder.java
│   │   ├── InvestmentThreshold.java
│   │   ├── LoanReminder.java
│   │   ├── PaymentDue.java
│   │   └── UserRegistered.java
│   ├── exception/
│   │   ├── BusinessException.java
│   │   ├── ResourceNotFoundException.java
│   │   └── UserNotFoundException.java
│   ├── gateway/
│   │   └── FinancesGateway.java
│   ├── messaging/
│   │   ├── EmailSender.java
│   │   └── InAppNotificationSender.java
│   ├── model/
│   │   ├── category/CategorySummary.java
│   │   ├── notification/
│   │   │   ├── Notification.java
│   │   │   ├── NotificationChannel.java
│   │   │   ├── NotificationType.java
│   │   │   └── UserNotificationPreference.java
│   │   └── pagination/PageResult.java
│   ├── repository/
│   │   ├── NotificationRepository.java
│   │   └── UserNotificationPreferenceRepository.java
│   ├── service/NotificationService.java
│   └── usecase/
│       ├── event/           # ProcessBankEventUseCase, ProcessInstallmentReminderUseCase,
│       │                    # ProcessInvestmentThresholdUseCase, ProcessLoanReminderUseCase,
│       │                    # ProcessPaymentDueUseCase, ProcessUserRegisteredUseCase
│       ├── notification/    # AllAsReadUseCase, CleanupNotificationsUseCase,
│       │                    # GetLatestNotificationsByBankUseCase, GetLatestNotificationsUseCase,
│       │                    # GetNotificationUseCase, GetUnreadCountUseCase,
│       │                    # OneAsReadUsecase, SendMonthlySummariesUseCase
│       └── preference/      # CreatePreferenceIfAbsentUseCase, GetPreferenceUseCase,
│                            # UpdatePreferenceUseCase
│
├── application/
│   ├── service/NotificationServiceImpl.java
│   └── usecase/
│       ├── event/impl/
│       ├── notification/impl/
│       ├── preference/impl/
│       └── scheduler/impl/
│
├── infrastructure/
│   ├── config/
│   │   ├── KafkaConfig.java
│   │   ├── KafkaErrorHandlerConfig.java
│   │   └── OpenApiConfig.java
│   ├── email/SmtpEmailSender.java
│   ├── gateway/impl/FinancesClient.java
│   ├── messaging/
│   │   ├── listener/                        # @KafkaListener<CloudEvent> → IdempotentEventProcessor
│   │   │   ├── BankEventListener.java        # 5 banks.* topics
│   │   │   ├── InvestmentEventListener.java  # investments.threshold.breached
│   │   │   └── UserEventListener.java        # users.user.registered
│   │   ├── mapper/                          # CloudEvent data → domain
│   │   └── payload/                         # CloudEvent data records
│   ├── persistence/
│   │   ├── entity/
│   │   │   ├── NotificationSqlEntity.java
│   │   │   └── UserNotificationPreferenceSqlEntity.java
│   │   ├── mapper/
│   │   └── repository/
│   │       ├── NotificationSqlRepository.java
│   │       ├── SqlNotificationPersistence.java
│   │       ├── SqlUserNotificationPreferencePersistence.java
│   │       └── UserNotificationPreferenceSqlRepository.java
│   ├── scheduler/
│   │   ├── MonthlySummaryScheduler.java
│   │   └── NotificationCleanupScheduler.java
│   └── sse/
│       ├── SseHeartbeatScheduler.java
│       ├── SseInAppNotificationSender.java
│       ├── dto/SseNotificationEntity.java
│       └── mapper/SseNotificationMapper.java
│
└── web/controller/
    ├── config/InternalAuthFilter.java
    ├── dto/
    │   ├── request/NotificationPreferenceRequest.java
    │   └── response/
    │       ├── NotificationPreferenceResponse.java
    │       ├── NotificationResponse.java
    │       └── UnreadCountResponse.java
    ├── exception/GlobalExceptionHandler.java
    ├── mapper/
    │   ├── NotificationMapper.java
    │   └── PageResultMapper.java
    ├── NotificationController.java
    ├── NotificationStreamController.java
    └── PreferenceController.java
```

---

## Endpoints

### NotificationController — `GET | PUT /api/v1/notifications`

| Method | Path | Purpose |
|--------|------|---------|
| `GET` | `/api/v1/notifications` | Paginated list (`page`, `size`; default 0/20) |
| `GET` | `/api/v1/notifications/latest` | Latest notifications; optional `?bankId=` filter |
| `GET` | `/api/v1/notifications/unread-count` | Returns `{ count }` |
| `PUT` | `/api/v1/notifications/{id}/read` | Mark one notification as read |
| `PUT` | `/api/v1/notifications/read-all` | Mark all notifications as read |

### NotificationStreamController — `GET /api/v1/notifications/stream`

| Method | Path | Purpose |
|--------|------|---------|
| `GET` | `/api/v1/notifications/stream` | Open SSE stream (`text/event-stream`) |

### PreferenceController — `GET | PUT /api/v1/notifications/preferences`

| Method | Path | Purpose |
|--------|------|---------|
| `GET` | `/api/v1/notifications/preferences` | Legacy: get user preferences (facade over `SUMMARY` category) |
| `PUT` | `/api/v1/notifications/preferences` | Legacy: update preferences (`monthlyEmailEnabled`, facade over `SUMMARY`) |
| `GET` | `/api/v1/notifications/preferences/by-category` | Get all 7 notification categories with `hasUiToggle` flag |
| `PUT` | `/api/v1/notifications/preferences/{category}` | Update channel preferences (`inAppEnabled`, `emailEnabled`) for a category |

All controllers read `X-User-Id` from the request header (injected by the gateway JWT filter).
Responses use the shared envelope `{ status, title, code, message, data }` from `commons-core`;
`code` appears only on errors with the `DomainError` slug (`resource_not_found`, `user_not_found`,
`business_rule_violation`). Errors are rendered by `GlobalExceptionHandler extends
ApiExceptionHandler` (commons-web); endpoints declare throwable codes with `@ApiErrorCodes`.

---

## Kafka Consumers

All listeners consume `CloudEvent` values (CloudEvents 1.0 Kafka binding, binary mode) on group ID `notifications-group`. The consumer factory, `CloudEventDeserializer`, ce_id dedup (`ProcessedEventGateway` → `processed_event`) and DLQ (`<topic>.DLT`) come from the shared `commons-messaging` module via `IdempotentEventProcessor`.

| Topic (`= ce_type`) | `data` record | Use Case |
|---------------------|---------------|----------|
| `users.user.registered` | `UserRegisteredData` | `ProcessUserRegisteredUseCase` — welcome notification + create preference if absent |
| `banks.account.low_balance` | `LowBalanceData` | `ProcessLowBalanceUseCase` |
| `banks.account.balance_adjusted` | `BalanceAdjustedData` | `ProcessBalanceAdjustedUseCase` |
| `banks.loan.reminder` | `LoanReminderData` | `ProcessLoanReminderUseCase` |
| `banks.card.expiring` | `CardExpiringData` | `ProcessCardExpiringUseCase` |
| `banks.card.installment_due` | `CardInstallmentDueData` | `ProcessPaymentDueUseCase` |
| `investments.threshold.breached` | `InvestmentThresholdData` | `ProcessInvestmentThresholdUseCase` — GAIN or LOSS direction |

> The old `bank-alerts` umbrella was split into the five typed `banks.*` events. Reminders are produced by **ms-banks** (owns loans/cards post-DDD), not ms-finances.

---

## GOTCHA: `UserRegisteredEvent` Has No `timestamp` Field

`UserRegisteredEvent` intentionally carries **no `timestamp` field**. Earlier versions included a bare `Instant timestamp` at the top level. Because Jackson deserializes `Instant` via a custom converter (epoch-seconds + nanoseconds object), any mismatch between the producer's serialization and the consumer's `ObjectMapper` configuration caused Jackson to throw — triggering the `KafkaErrorHandlerConfig` seek-forward strategy — and the message was skipped silently, so the welcome notification was never created.

**Rule:** keep `UserRegisteredEvent` timestamp-free. If a timestamp is ever needed, add it inside the nested `Payload` class as a `String` (ISO-8601) and convert explicitly in the mapper.

---

## Run

```bash
# Recommended (infra + hot-reload via Maven)
./scripts/dev.sh local service-notifications

# Or directly from the service directory
mvn spring-boot:run
```

Swagger UI: http://localhost:8084/swagger-ui.html

---

## Required environment variables

| Variable | Purpose |
|---|---|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL — e.g. `jdbc:postgresql://postgres:5432/financialapp?currentSchema=notifications` |
| `SPRING_DATASOURCE_USERNAME` | Database user |
| `SPRING_DATASOURCE_PASSWORD` | Database password |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka broker — e.g. `kafka:9092` |
| `INTERNAL_AUTH_TOKEN` | Shared secret for `X-Internal-Token` header; service hard-fails at startup without it |
| `MAIL_HOST` | SMTP server hostname |
| `MAIL_PORT` | SMTP server port (e.g. `587`) |
| `MAIL_USERNAME` | SMTP authentication username |
| `MAIL_PASSWORD` | SMTP authentication password |

Copy `.env.example` (workspace root) to `.env` in this directory and fill in the values.

## CI/CD

| Workflow | Trigger | Does |
|---|---|---|
| `ci.yml` | PRs; push to develop/master | tests + docker build via shared `backend-ci.yml` |
| `docker-publish.yml` | push to master; `v*` tags | GHCR publish: `latest`, `sha-*`, semver on tags |
| `release.yml` | manual (bump dropdown) | next `vX.Y.Z` tag + Release + versioned publish |

Reusable workflows live in the root repo `Sergio-Smirnoff/financial-app`.
