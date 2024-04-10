INSERT INTO client (name, last_name, email, phone_number, personal_id, password)
VALUES ('John', 'Doe', 'john.doe@gmail.com', '+37061234567', '50101011234',
        '$2a$10$xisJp2RBzbHxltdzeeD.geDP7uEb/beWK/ULQJPAHNyeihFP7Vjwq');

INSERT INTO loan (amount, interest_rate, interest_amount, status, loan_term_in_days, due_date, client_id, ip_address,
                  created_at)
VALUES (1000, 0.129, 129, 'APPROVED', 30, '2024-04-23', 1, '192.168.0.0', '2024-03-22 12:00:00'),
       (2500, 0.114, 228, 'APPROVED', 540, '2025-09-15', 1, '192.168.0.0', '2024-03-22 12:00:00'),
       (5000, 0.099, 495, 'APPROVED', 1080, '2027-03-09', 1, '192.168.0.0', '2024-03-22 12:00:00'),
       (10000, 0.084, 840, 'REJECTED', 2160, '2029-08-31', 1, '192.168.0.0', '2024-03-22 12:00:00');

INSERT INTO loan_extension (loan_id, additional_loan_term_in_days)
VALUES (1, 7);

