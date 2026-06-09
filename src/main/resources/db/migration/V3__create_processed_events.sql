CREATE TABLE notifications.processed_events (
    event_id     VARCHAR(64)  NOT NULL PRIMARY KEY,
    processed_at TIMESTAMP    NOT NULL DEFAULT now()
);
