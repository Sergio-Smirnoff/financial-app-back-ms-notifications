CREATE TABLE notifications.notification_preferences (
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT      NOT NULL,
    category       VARCHAR(30) NOT NULL,
    in_app_enabled BOOLEAN     NOT NULL DEFAULT true,
    email_enabled  BOOLEAN     NOT NULL DEFAULT false,
    created_at     TIMESTAMP   NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP   NOT NULL DEFAULT now(),
    UNIQUE (user_id, category)
);

INSERT INTO notifications.notification_preferences (user_id, category, in_app_enabled, email_enabled)
SELECT user_id, 'SUMMARY', true, monthly_email_enabled
FROM notifications.user_notification_preferences;
