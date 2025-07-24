# --- !Ups
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Insert default data
INSERT INTO roles (name, description)
VALUES
    ('USER', 'Default role for normal users'),
    ('ADMIN', 'Administrator with full permissions');

ALTER TABLE users ADD COLUMN role_id BIGINT DEFAULT 1 NOT NULL;

ALTER TABLE users
ADD CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES roles(id);

# --- !Downs
ALTER TABLE users DROP CONSTRAINT fk_user_role;
ALTER TABLE users DROP COLUMN role_id;

DROP TABLE IF EXISTS  roles;
