-- Create table: subscription_plans
CREATE TABLE IF NOT EXISTS subscription_plans (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    price NUMERIC(4,2) NOT NULL,
    is_active BOOLEAN NOT NULL,
    description TEXT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);