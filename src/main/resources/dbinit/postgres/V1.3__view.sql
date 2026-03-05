CREATE OR REPLACE VIEW v_budget_accounting AS
SELECT
    br.id                     AS request_id,
    br.amount                 AS amount,
    br.currency               AS currency,
    br.transaction_type       AS transaction_type,
    br.budget_type_id         AS budget_type,
    br.budget_status_id          AS status,
    bsc._created_at            AS created_at
FROM budget_request br
         JOIN budget_status_change bsc
              ON bsc.request_id = br.id
WHERE br.budget_status_id = 'APPROVED';