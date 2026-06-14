INSERT INTO payments (id, payment_number, reservation_number, user_id, amount, status, guest_email, created_at)
VALUES (1, 'PAY-SAMPLE01', 'RES-SAMPLE01', 1, 250.00, 'COMPLETED', 'john.doe@example.com', NOW())
ON CONFLICT (id) DO NOTHING;

SELECT setval('payments_id_seq', (SELECT MAX(id) FROM payments));
