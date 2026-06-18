INSERT INTO reservations (id, reservation_number, user_id, room_id, hotel_id, guest_name, guest_email, check_in_date, check_out_date, total_price, status, created_at)
VALUES
  (1, 'RES-SAMPLE01', 1, 2, 1, 'John Doe', 'john@example.com', CURRENT_DATE + 10, CURRENT_DATE + 13, 540.00, 'CONFIRMED', CURRENT_TIMESTAMP),
  (2, 'RES-SAMPLE02', 2, 6, 2, 'Jane Doe', 'jane@example.com', CURRENT_DATE + 5,  CURRENT_DATE + 8,  390.00, 'CONFIRMED', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

SELECT setval('reservations_id_seq', (SELECT MAX(id) FROM reservations));
