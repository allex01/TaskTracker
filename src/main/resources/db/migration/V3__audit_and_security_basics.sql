-- Users: add lockout and audit-related fields
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS failed_attempts INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS locked_until TIMESTAMPTZ NULL,
    ADD COLUMN IF NOT EXISTS last_login_at TIMESTAMPTZ NULL;

-- Audit events table
CREATE TABLE IF NOT EXISTS audit_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    event_type VARCHAR(64) NOT NULL,
    user_id UUID NULL REFERENCES users(id) ON DELETE SET NULL,
    app_user VARCHAR(128) NULL, -- username at event time (for deleted users)
    os_user VARCHAR(128) NULL,
    workstation VARCHAR(128) NULL,
    details JSONB NULL
);

CREATE INDEX IF NOT EXISTS idx_audit_events_type ON audit_events(event_type);
CREATE INDEX IF NOT EXISTS idx_audit_events_time ON audit_events(occurred_at);
CREATE INDEX IF NOT EXISTS idx_audit_events_user ON audit_events(user_id);

-- Security settings (single row)
CREATE TABLE IF NOT EXISTS security_settings (
    id SMALLINT PRIMARY KEY DEFAULT 1,
    max_failed_attempts INTEGER NOT NULL DEFAULT 3,
    max_failed_attempts_cap INTEGER NOT NULL DEFAULT 10,
    lock_minutes INTEGER NOT NULL DEFAULT 15,
    session_idle_minutes INTEGER NOT NULL DEFAULT 15,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

INSERT INTO security_settings (id)
    VALUES (1)
    ON CONFLICT (id) DO NOTHING;


