CREATE TABLE notifications.monthly_summary_sent (
    id            BIGSERIAL  PRIMARY KEY,
    user_id       BIGINT     NOT NULL,
    summary_month VARCHAR(7) NOT NULL,
    sent_at       TIMESTAMP  NOT NULL DEFAULT now(),
    CONSTRAINT uq_monthly_summary_sent_user_month UNIQUE (user_id, summary_month)
);
