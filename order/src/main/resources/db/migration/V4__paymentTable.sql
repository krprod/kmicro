CREATE SEQUENCE order_schema.payment_seq
    START WITH 1
    INCREMENT BY 50;

CREATE TABLE if not exists order_schema.payment (
    id                 BIGINT NOT NULL,
    order_id           BIGINT,
    total_amount       DOUBLE PRECISION,
    payment_method     VARCHAR(255),
    payment_status     VARCHAR(255),
    fail_reason        TEXT,
    transaction_id     VARCHAR(255),
    user_id            BIGINT,
    shipping_fee       DOUBLE PRECISION,
    created_at timestamp(6) with time zone NULL,
    updated_at timestamp(6) with time zone NULL,
--     -- Audit columns (Assuming these exist in your BaseEntity)
--     created_at         TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
--     updated_at         TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
--     version            INTEGER DEFAULT 0,

    CONSTRAINT pk_payment PRIMARY KEY (id)
);

-- 3. Optional but recommended: Add an index on frequently queried columns
CREATE INDEX idx_payment_order_id ON order_schema.payment(order_id);
CREATE INDEX idx_payment_user_id ON order_schema.payment(user_id);

-- 1. Create the sequence first
-- Increment must match JPA allocationSize (50) for HiLo optimization
SELECT setval('order_schema.payment_seq', COALESCE((SELECT MAX(id) FROM  order_schema.payment), 0) + 1, false);