CREATE TABLE IF NOT EXISTS client
(
    id           SERIAL PRIMARY KEY,
    name         TEXT,
    last_name    TEXT,
    email        TEXT      NOT NULL UNIQUE,
    phone_number TEXT      NOT NULL,
    personal_id  TEXT      NOT NULL UNIQUE,
    password     TEXT      NOT NULL,
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS loan
(
    id                SERIAL PRIMARY KEY,
    amount            DECIMAL(10, 2) NOT NULL,
    interest_rate     DECIMAL(6, 4)  NOT NULL,
    interest_amount   DECIMAL(10, 2) NOT NULL,
    loan_term_in_days INTEGER        NOT NULL,
    status            TEXT           NOT NULL DEFAULT 'PENDING',
    due_date          DATE           NOT NULL,
    created_at        TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP,
    ip_address        TEXT           NOT NULL,
    client_id         INTEGER,

    FOREIGN KEY (client_id) REFERENCES client (id)
);

CREATE TABLE IF NOT EXISTS loan_extension
(
    id                           SERIAL PRIMARY KEY,
    additional_loan_term_in_days INTEGER   NOT NULL,
    created_at                   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    loan_id                      INTEGER,

    FOREIGN KEY (loan_id) REFERENCES loan (id)
);
