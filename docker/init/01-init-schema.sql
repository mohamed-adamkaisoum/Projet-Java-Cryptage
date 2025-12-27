CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE TABLE IF NOT EXISTS users (
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(50) NOT NULL UNIQUE,
    password_hash   VARCHAR(256) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE TABLE IF NOT EXISTS encryption_keys (
    id              BIGSERIAL PRIMARY KEY,
    owner_user_id   BIGINT REFERENCES users(id) ON DELETE CASCADE,
    algorithm       VARCHAR(50) NOT NULL DEFAULT 'RSA',
    key_size        INTEGER NOT NULL DEFAULT 2048,
    public_key      TEXT NOT NULL,
    private_key     TEXT NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    rotated_at      TIMESTAMPTZ
);
CREATE TABLE IF NOT EXISTS secure_files (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_user_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    key_id           BIGINT REFERENCES encryption_keys(id) ON DELETE SET NULL,
    original_name    TEXT NOT NULL,
    encrypted_name   TEXT NOT NULL UNIQUE,
    storage_path     TEXT NOT NULL,                 
    encrypted_blob   BYTEA,                         
    file_size_bytes  BIGINT NOT NULL CHECK (file_size_bytes >= 0),
    stored_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_secure_files_owner ON secure_files(owner_user_id);
CREATE INDEX IF NOT EXISTS idx_secure_files_key ON secure_files(key_id);


