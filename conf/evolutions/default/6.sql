# add is_verified column to users table to indicate if a user has verified their account

# --- !Ups
ALTER TABLE users ADD COLUMN is_verified BOOLEAN DEFAULT FALSE;

# --- !Downs
ALTER TABLE users DROP COLUMN IF EXISTS is_verified;