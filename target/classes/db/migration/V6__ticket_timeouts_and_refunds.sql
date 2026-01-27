ALTER TABLE ticket
    ADD COLUMN reserved_until DATETIME NULL,
    ADD COLUMN paid_at DATETIME NULL,
    ADD COLUMN refunded_at DATETIME NULL;

UPDATE ticket SET status = UPPER(status) WHERE status IS NOT NULL;
