INSERT INTO client (name, last_name, email, phone_number, personal_id, password)
VALUES ('John', 'Doe', 'john.doe@email.com', '+37060123456', '50001010000', 'password'),
       ('Jane', 'Doe', 'jane.doe@email.com', '+37060123456', '50001010001', 'password');

INSERT INTO loan (amount, interest_rate, interest_amount, status, loan_term_in_days, due_date, client_id, ip_address,
                  created_at)
VALUES (1000, 0.129, 129, 'APPROVED', 30, '2024-04-23', 1, '192.168.0.1', '2024-03-22 12:00:00'),
       (2000, 0.114, 228, 'APPROVED', 540, '2025-09-15', 1, '192.168.0.1', '2024-03-22 12:00:00'),
       (3000, 0.099, 297, 'APPROVED', 1080, '2027-03-09', 1, '192.168.0.1', '2024-03-22 12:00:00'),
       (4000, 0.099, 396, 'APPROVED', 720, '2026-03-14', 2, '192.168.0.2', '2024-03-22 12:00:00'),
       (15000, 0.099, 1485, 'APPROVED', 1200, '2027-07-07', 2, '192.168.0.2', '2024-03-22 12:00:00'),
       (15000, 0.099, 1485, 'REJECTED', 1080, '2027-03-09', 1, '192.168.0.1', '2024-03-22 12:00:00');

INSERT INTO loan_extension (loan_id, additional_loan_term_in_days, created_at)
VALUES (1, 7, '2024-03-22 14:00:00'),
       (2, 7, '2024-03-22 14:00:00'),
       (2, 7, '2024-03-22 14:00:00');



