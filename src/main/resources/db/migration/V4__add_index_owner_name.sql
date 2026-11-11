-- V4: индекс на owner_name (Task E, шаг 13)

CREATE INDEX idx_accounts_owner_name ON accounts (owner_name);