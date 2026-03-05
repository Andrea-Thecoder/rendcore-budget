INSERT INTO budget_status_type (id, description)
VALUES ('CREATED', 'CREATO'),
       ('DRAFT', 'BOZZA'),
       ('IN_REVIEW', 'IN ELABORAZIONE'),
       ('APPROVED', 'APPROVATA'),
       ('REJECTED', 'RIFIUTATA'),
       ('CANCELLED', 'CANCELLATA');


INSERT INTO budget_type (id, description)
VALUES ('STRUCTURAL', 'Spesa strutturale (ricorrente, es. stipendi)'),
       ('OCCASIONAL', 'Spesa occasionale (una tantum, es. evento)'),
       ('PLANNED', 'Spesa programmata (prevista nel piano annuale)');


INSERT INTO budget_status_transition (from_status_id, to_status_id)
VALUES ('DRAFT', 'IN_REVIEW'),
       ('DRAFT', 'CANCELLED'),
       ('IN_REVIEW', 'APPROVED'),
       ('IN_REVIEW', 'REJECTED'),
       ('IN_REVIEW', 'CANCELLED'),
       ('IN_REVIEW', 'DRAFT');

