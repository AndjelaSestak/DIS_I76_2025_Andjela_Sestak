-- Hotel Reservation System - Initial User Data
-- Admin password: admin123 (BCrypt)
-- User passwords: password123 (BCrypt)

INSERT INTO users (username, email, password, first_name, last_name, phone, role, active, created_at, updated_at)
VALUES
    ('admin', 'admin@hotelreservation.com', '$2b$10$pt.6d0FLBl7g7z/fX6egXeLUgdI7RN.xFlxpxlesrFZCnBFc8dKnK', 'Admin', 'System', '+381600000001', 'ROLE_ADMIN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('johndoe', 'john.doe@example.com', '$2b$10$fjwkOuTr/KWJ9UaB3Qg/6eEWDxUtlsNiLxGBGL/ADWiyux0MRsjBm', 'John', 'Doe', '+381601234567', 'ROLE_USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('janedoe', 'jane.doe@example.com', '$2b$10$fjwkOuTr/KWJ9UaB3Qg/6eEWDxUtlsNiLxGBGL/ADWiyux0MRsjBm', 'Jane', 'Doe', '+381607654321', 'ROLE_USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO UPDATE SET
    password = EXCLUDED.password,
    updated_at = CURRENT_TIMESTAMP;
