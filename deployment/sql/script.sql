-- Enable UUID generation (PostgreSQL >= 9.4)
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =========================
--  Table: Role
-- =========================
CREATE TABLE role (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- UniqueID
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

COMMENT ON TABLE role IS 'Roles catalog';
COMMENT ON COLUMN role.id IS 'UUID identifier of the role';
COMMENT ON COLUMN role.name IS 'Unique name of the role';

-- =========================
--  Table: User
-- =========================
CREATE TABLE app_user (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    document_id   VARCHAR(50)  NOT NULL,
    phone         VARCHAR(30),
    role_id       UUID NOT NULL,
    basic_salary   NUMERIC(12,2) NOT NULL CHECK (basic_salary >= 0),

    CONSTRAINT uq_user_email UNIQUE (email),
    CONSTRAINT uq_user_doc   UNIQUE (document_id),

    CONSTRAINT fk_user_role FOREIGN KEY (role_id)
        REFERENCES role(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

COMMENT ON TABLE app_user IS 'System users';
COMMENT ON COLUMN app_user.id          IS 'UUID identifier of the user';
COMMENT ON COLUMN app_user.document_id IS 'Unique document';
COMMENT ON COLUMN app_user.base_salary IS 'Base salary (>= 0)';
COMMENT ON CONSTRAINT fk_user_role ON app_user IS 'Each user belongs to a role';

-- Index for searches by role
CREATE INDEX ix_user_role_id ON app_user(role_id);

-- Optional: case-insensitive unique email
-- CREATE UNIQUE INDEX uq_user_email_lower ON app_user ((lower(email)));

-- Quick test
SELECT * FROM app_user;
