CREATE TABLE player_account (
    id UUID PRIMARY KEY,
    handle VARCHAR(40) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
