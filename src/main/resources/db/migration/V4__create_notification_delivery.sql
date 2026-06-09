CREATE TABLE notifications.notification_delivery (
    id              BIGSERIAL       PRIMARY KEY,
    notification_id BIGINT          NOT NULL REFERENCES notifications.notifications(id),
    channel         VARCHAR(20)     NOT NULL,
    status          VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    attempts        INT             NOT NULL DEFAULT 0,
    last_error      TEXT,
    next_retry_at   TIMESTAMP,
    created_at      TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT now()
);

CREATE INDEX idx_delivery_notif_id ON notifications.notification_delivery (notification_id);
CREATE INDEX idx_delivery_status   ON notifications.notification_delivery (status);
CREATE INDEX idx_delivery_retry    ON notifications.notification_delivery (status, next_retry_at) WHERE status = 'FAILED';
