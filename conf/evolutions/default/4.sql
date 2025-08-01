# categories schema

# --- !Ups

CREATE TABLE categories (
    id          UUID PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL,
    created_by   UUID         NOT NULL,
    updated_by   UUID         NOT NULL,
    is_deleted   BOOLEAN      NOT NULL
);

# --- !Downs

DROP TABLE IF EXISTS categories;