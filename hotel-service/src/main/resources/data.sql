INSERT INTO hotels (id, name, city, address, description, stars, amenities, active, created_at)
VALUES
  (1, 'Grand Hotel Belgrade', 'Belgrade', 'Knez Mihailova 10, Belgrade', 'Luxury hotel in the heart of Belgrade', 5, 'WiFi, Pool, Spa, Restaurant, Bar', true, CURRENT_TIMESTAMP),
  (2, 'Hotel Novi Sad', 'Novi Sad', 'Trg Slobode 5, Novi Sad', 'Comfortable hotel near the city center', 4, 'WiFi, Restaurant, Parking', true, CURRENT_TIMESTAMP),
  (3, 'Mountain Resort Kopaonik', 'Kopaonik', 'Ski Centar bb, Kopaonik', 'Ski resort hotel with stunning mountain views', 4, 'WiFi, Ski, Spa, Restaurant', true, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

INSERT INTO rooms (id, hotel_id, room_number, type, price_per_night, capacity, description, available, created_at)
VALUES
  (1,  1, '101', 'SINGLE', 120.00, 1, 'Cozy single room with city view', true, CURRENT_TIMESTAMP),
  (2,  1, '102', 'DOUBLE', 180.00, 2, 'Spacious double room with city view', true, CURRENT_TIMESTAMP),
  (3,  1, '201', 'SUITE',  350.00, 2, 'Luxury suite with panoramic view', true, CURRENT_TIMESTAMP),
  (4,  1, '202', 'DELUXE', 280.00, 2, 'Deluxe room with balcony', true, CURRENT_TIMESTAMP),
  (5,  2, '101', 'SINGLE',  80.00, 1, 'Standard single room', true, CURRENT_TIMESTAMP),
  (6,  2, '102', 'DOUBLE', 130.00, 2, 'Standard double room', true, CURRENT_TIMESTAMP),
  (7,  2, '201', 'TWIN',   140.00, 2, 'Twin room with two beds', true, CURRENT_TIMESTAMP),
  (8,  3, '101', 'SINGLE', 100.00, 1, 'Ski-in ski-out room', true, CURRENT_TIMESTAMP),
  (9,  3, '102', 'DOUBLE', 160.00, 2, 'Mountain view double room', true, CURRENT_TIMESTAMP),
  (10, 3, '201', 'SUITE',  400.00, 4, 'Family suite with mountain view', true, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

SELECT setval('hotels_id_seq', (SELECT MAX(id) FROM hotels));
SELECT setval('rooms_id_seq', (SELECT MAX(id) FROM rooms));
