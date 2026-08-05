# ms-notifications — API

Endpoints and error codes. Envelope shape: parent `.ai/references/APP_STRUCTURE.md` — not repeated here.

## Endpoints

| Method | Path | Purpose | Error codes |
|---|---|---|---|
| GET | `/api/v1/notifications` | List user notifications (paginated `?page=&size=`) | — |
| GET | `/api/v1/notifications/latest` | List latest notifications (optional `?bankId=`) | — |
| GET | `/api/v1/notifications/unread-count` | Get unread notification count `{ count }` | — |
| PUT | `/api/v1/notifications/{id}/read` | Mark a single notification as read | `resource_not_found` |
| PUT | `/api/v1/notifications/read-all` | Mark all user notifications as read | — |
| GET | `/api/v1/notifications/stream` | Open real-time SSE stream (`text/event-stream`) | — |
| GET | `/api/v1/notifications/preferences` | Legacy: get user's preferences (facade over `SUMMARY`) | `user_not_found` |
| PUT | `/api/v1/notifications/preferences` | Legacy: update preferences (`monthlyEmailEnabled`) | `user_not_found` |
| GET | `/api/v1/notifications/preferences/by-category` | Get preferences for all 7 categories | — |
| PUT | `/api/v1/notifications/preferences/{category}` | Update channel toggles (`inAppEnabled`, `emailEnabled`) for a category | `business_rule_violation` |

## SSE Stream Behavior (`/stream`)

- Produces `text/event-stream`.
- Gateway routes with `response-timeout: -1`.
- Max 3 active emitters per user (oldest evicted). Emitter timeout: 5 minutes (300 s).
- `SseHeartbeatScheduler` sends comment heartbeat every 30 s.
- SSE event format: `event: notification`, `data: { id, type, title, message, channel, read, metadata, createdAt }`.

## DomainError catalog

| Slug | HTTP status | When it is thrown |
|---|---|---|
| `resource_not_found` | 404 | Notification ID lookup found no match for user |
| `user_not_found` | 404 | User preferences lookup returned no match |
| `business_rule_violation` | 422 | Invalid category preference update |
| `internal_error` | 500 | Unmapped failure |
