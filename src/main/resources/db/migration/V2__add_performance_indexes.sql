-- Fast unread count per user
CREATE INDEX IF NOT EXISTS idx_notifications_user_unread
    ON notifications.notifications (user_id, created_at DESC)
    WHERE read = false;

-- General user queries
CREATE INDEX IF NOT EXISTS idx_notifications_user_created
    ON notifications.notifications (user_id, created_at DESC);

-- Partial index for unread notifications only
CREATE INDEX IF NOT EXISTS idx_notifications_unread
    ON notifications.notifications (read)
    WHERE read = false;
