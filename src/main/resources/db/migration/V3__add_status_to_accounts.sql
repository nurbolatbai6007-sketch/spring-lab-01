-- V3: добавляем столбец status

ALTER TABLE accounts
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';