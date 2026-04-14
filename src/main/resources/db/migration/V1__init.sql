-- ═══════════════════════════════════════════════════════════════════
-- V1: Notifications schema — notifications + user preferences
-- ═══════════════════════════════════════════════════════════════════

CREATE TABLE notifications.notifications (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL,
    type            VARCHAR(50)     NOT NULL,
    title           VARCHAR(255)    NOT NULL,
    message         TEXT            NOT NULL,
    channel         VARCHAR(20)     NOT NULL DEFAULT 'IN_APP',
    read            BOOLEAN         NOT NULL DEFAULT FALSE,
    metadata        JSONB,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notif_user_id    ON notifications.notifications (user_id);
CREATE INDEX idx_notif_user_read  ON notifications.notifications (user_id, read);
CREATE INDEX idx_notif_created    ON notifications.notifications (created_at DESC);

CREATE TABLE notifications.user_notification_preferences (
    id                      BIGSERIAL       PRIMARY KEY,
    user_id                 BIGINT          NOT NULL UNIQUE,
    email                   VARCHAR(255)    NOT NULL,
    monthly_email_enabled   BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP       NOT NULL DEFAULT NOW()
);
