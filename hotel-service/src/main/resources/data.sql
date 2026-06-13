INSERT INTO hotels (id, name, city, address, description, stars, amenities, active)
VALUES
  (1, 'Grand Hotel Belgrade', 'Belgrade', 'Knez Mihailova 10, Belgrade', 'Luxury hotel in the heart of Belgrade', 5, 'WiFi, Pool, Spa, Restaurant, Bar', true),
  (2, 'Hotel Novi Sad', 'Novi Sad', 'Trg Slobode 5, Novi Sad', 'Comfortable hotel near the city center', 4, 'WiFi, Restaurant, Parking', true),
  (3, 'Mountain Resort Kopaonik', 'Kopaonik', 'Ski Centar bb, Kopaonik', 'Ski resort hotel with stunning mountain views', 4, 'WiFi, Ski, Spa, Restaurant', true)
ON CONFLICT (id) DO NOTHING;

INSERT INTO rooms (id, hotel_id, room_number, type, price_per_night, capacity, description, available)
VALUES
  (1,  1, '101', 'SINGLE', 120.00, 1, 'Cozy single room with city view', true),
  (2,  1, '102', 'DOUBLE', 180.00, 2, 'Spacious double room with city view', true),
  (3,  1, '201', 'SUITE',  350.00, 2, 'Luxury suite with panoramic view', true),
  (4,  1, '202', 'DELUXE', 280.00, 2, 'Deluxe room with balcony', true),
  (5,  2, '101', 'SINGLE',  80.00, 1, 'Standard single room', true),
  (6,  2, '102', 'DOUBLE', 130.00, 2, 'Standard double room', true),
  (7,  2, '201', 'TWIN',   140.00, 2, 'Twin room with two beds', true),
  (8,  3, '101', 'SINGLE', 100.00, 1, 'Ski-in ski-out room', true),
  (9,  3, '102', 'DOUBLE', 160.00, 2, 'Mountain view double room', true),
  (10, 3, '201', 'SUITE',  400.00, 4, 'Family suite with mountain view', true)
ON CONFLICT (id) DO NOTHING;

SELECT setval('hotels_id_seq', (SELECT MAX(id) FROM hotels));
SELECT setval('rooms_id_seq', (SELECT MAX(id) FROM rooms));
