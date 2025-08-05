# product schema

# --- !Ups
CREATE TABLE products (
    id          UUID PRIMARY KEY,
    name        VARCHAR(200) NOT NULL,
    description TEXT,
    image_url   VARCHAR(255),
    price       DECIMAL(10, 2) NOT NULL,
    quantity    INTEGER      NOT NULL,
    is_featured BOOLEAN      NOT NULL,
    category_id UUID         NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL,
    created_by   UUID         NOT NULL,
    updated_by   UUID         NOT NULL,
    is_deleted   BOOLEAN      NOT NULL,
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

# --- !Downs
DROP TABLE IF EXISTS products;