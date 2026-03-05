CREATE EXTENSION IF NOT EXISTS pg_trgm;


CREATE INDEX idx_budget_request_title_trgm
ON budget_request
USING gin (title gin_trgm_ops);
