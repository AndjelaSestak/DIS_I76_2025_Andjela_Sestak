INSERT INTO notifications (id, type, recipient, subject, message, status, related_entity_id, created_at, sent_at)
VALUES (1, 'EMAIL', 'john.doe@example.com', 'Payment Confirmation - Reservation RES-SAMPLE01',
        'Dear John Doe, Your payment PAY-SAMPLE01 has been COMPLETED. Reservation: RES-SAMPLE01. Thank you for choosing our hotel!',
        'SENT', 'PAY-SAMPLE01', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

SELECT setval('notifications_id_seq', (SELECT MAX(id) FROM notifications));
