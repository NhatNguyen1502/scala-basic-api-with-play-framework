# Add name, address, phone for users table

# --- !Ups
ALTER TABLE users
ADD COLUMN first_name VARCHAR(50);

ALTER TABLE users
ADD COLUMN last_name VARCHAR(50);

ALTER TABLE users
ADD COLUMN address VARCHAR(500);

ALTER TABLE users
ADD COLUMN phone_number VARCHAR(12);

# --- !Downs

ALTER TABLE users DROP COLUMN phone_number;
ALTER TABLE users DROP COLUMN address;
ALTER TABLE users DROP COLUMN last_name;
ALTER TABLE users DROP COLUMN first_name;