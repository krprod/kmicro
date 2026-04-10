-- Ensure we don't have duplicate slugs before applying,
-- or the migration will fail.
CREATE UNIQUE INDEX IF NOT EXISTS idx_categories_slug
    ON product_schema.categories(slug);