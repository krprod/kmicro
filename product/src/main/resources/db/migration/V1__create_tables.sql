CREATE SEQUENCE product_schema.product_seq
    START WITH 1
    INCREMENT BY 50;
CREATE TABLE IF NOT EXISTS product_schema.products (
  category character varying(255)  NULL,
  price double precision NULL,
  description text null ,
  in_stock boolean NOT NULL,
  stock_quantity integer NULL,
  rating double precision NULL,
  review_count bigint NULL,
  id bigint NOT NULL,
  name character varying(255) NULL,
  url character varying(255) NULL
);

ALTER TABLE IF EXISTS  product_schema.products
ADD CONSTRAINT products_pkey PRIMARY KEY (id);
SELECT setval('product_schema.product_seq', COALESCE((SELECT MAX(id) FROM  product_schema.products), 0) + 1, false);

CREATE SEQUENCE product_schema.category_seq START WITH 1 INCREMENT BY 50;
CREATE TABLE IF NOT EXISTS product_schema.categories (
    is_active boolean NOT NULL,
    category_id bigint,
    created_at timestamp(6) without time zone NULL,
    name character varying(255) NULL,
    slug character varying(255) NULL
);

ALTER TABLE IF EXISTS  product_schema.categories
ADD CONSTRAINT categories_pkey PRIMARY KEY (category_id);
SELECT setval('product_schema.category_seq', COALESCE((SELECT MAX(category_id) FROM  product_schema.categories), 0) + 1, false);

CREATE TABLE product_schema.shedlock (
                                      name character varying(64) NOT NULL,
                                      lock_until timestamp without time zone NOT NULL,
                                      locked_at timestamp without time zone NOT NULL,
                                      locked_by character varying(255) NOT NULL
);

ALTER TABLE product_schema.shedlock
    ADD CONSTRAINT shedlock_pkey PRIMARY KEY (name);