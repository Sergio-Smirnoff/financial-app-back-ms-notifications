# ms-notifications — messaging and jobs

CloudEvents 1.0, Kafka binary mode, via `commons-messaging`. Group ID = `notifications-group`. Outbox and DLT conventions: parent `.ai/references/ARCHITECTURE.md` — not repeated here.

## Published
None. ms-notifications is a consumer hub and produces no domain events.

## Consumed

| ce_type | Handler | Listener | Idempotency Key | DLT behaviour |
|---|---|---|---|---|
| `users.user.registered` | `ProcessUserRegisteredUseCase` | `UserEventListener` | `ce_id` via `processed_events` | Retries, then `users.user.registered.DLT` |
| `banks.account.low_balance` | `ProcessLowBalanceUseCase` | `BankEventListener` | `ce_id` via `processed_events` | Retries, then `banks.account.low_balance.DLT` |
| `banks.account.balance_adjusted` | `ProcessBalanceAdjustedUseCase` | `BankEventListener` | `ce_id` via `processed_events` | Retries, then `banks.account.balance_adjusted.DLT` |
| `banks.loan.reminder` | `ProcessLoanReminderUseCase` | `BankEventListener` | `ce_id` via `processed_events` | Retries, then `banks.loan.reminder.DLT` |
| `banks.card.expiring` | `ProcessCardExpiringUseCase` | `BankEventListener` | `ce_id` via `processed_events` | Retries, then `banks.card.expiring.DLT` |
| `banks.card.installment_due` | `ProcessPaymentDueUseCase` | `BankEventListener` | `ce_id` via `processed_events` | Retries, then `banks.card.installment_due.DLT` |
| `investments.threshold.breached` | `ProcessInvestmentThresholdUseCase` | `InvestmentEventListener` | `ce_id` via `processed_events` | Retries, then `investments.threshold.breached.DLT` |

## Scheduled jobs

| Job | Trigger / Cron | What it does |
|---|---|---|
| `MonthlySummaryScheduler` | 1st of month 09:00 (`NOTIFICATION_SCHEDULER_CRON`) | Fetches category summaries from ms-finances via `FinancesClient` and dispatches monthly summary emails |
| `NotificationCleanupScheduler` | Midnight daily (`0 0 0 * * *`) | Purges stale notification records |
| `SseHeartbeatScheduler` | Every 30 s (fixed rate) | Sends heartbeat comment event to open SSE streams |

## Outbound calls

| Target service / Protocol | Target / Method | Purpose |
|---|---|---|
| ms-finances | `GET /api/v1/finances/transactions/summary` (`FinancesClient`) | Fetches category spending totals for monthly email |
| SMTP Relay | `SmtpEmailSender` (Spring Mail + Thymeleaf) | Dispatches email notifications for opted-in categories |
