-- Hotel Reservation System - Initial User Data
-- Admin password: admin123 (BCrypt)
-- User passwords: password123 (BCrypt)

INSERT INTO users (username, email, password, first_name, last_name, phone, role, active, created_at, updated_at)
VALUES
    ('admin', 'admin@hotelreservation.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin', 'System', '+381600000001', 'ROLE_ADMIN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('johndoe', 'john.doe@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'John', 'Doe', '+381601234567', 'ROLE_USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('janedoe', 'jane.doe@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Jane', 'Doe', '+381607654321', 'ROLE_USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;
