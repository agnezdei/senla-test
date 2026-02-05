-- Гости
INSERT INTO guest (name, passport_number) VALUES
    ('Иван Иванов', '444444'),
    ('Мария Сидорова', '666666'),
    ('Петр Петров', '888888')
ON CONFLICT (passport_number) DO NOTHING;

-- Комнаты
INSERT INTO room (number, type, status, price, capacity, stars) VALUES
    ('101', 'STANDARD', 'AVAILABLE', 2500.00, 2, 3),
    ('102', 'STANDARD', 'AVAILABLE', 2500.00, 2, 3),
    ('201', 'BUSINESS', 'AVAILABLE', 3500.00, 3, 4),
    ('202', 'BUSINESS', 'AVAILABLE', 3500.00, 3, 4),
    ('301', 'LUXURY', 'AVAILABLE', 5000.00, 4, 5),
    ('302', 'LUXURY', 'AVAILABLE', 5000.00, 4, 5),
    ('303', 'LUXURY', 'UNDER_MAINTENANCE', 5000.00, 4, 5)
ON CONFLICT (number) DO NOTHING;

-- Услуги
INSERT INTO service (name, price, category) VALUES
    ('Завтрак шведский стол', 800.00, 'FOOD'),
    ('Ужин à la carte', 1500.00, 'FOOD'),
    ('Ежедневная уборка', 500.00, 'CLEANING'),
    ('Смена белья', 300.00, 'CLEANING'),
    ('Мини-бар (стандартный набор)', 1200.00, 'COMFORT'),
    ('SPA-процедуры (базовый пакет)', 2500.00, 'COMFORT'),
    ('Трансфер из/в аэропорт', 2000.00, 'TRANSPORT')
ON CONFLICT DO NOTHING;

-- Бронирования
INSERT INTO booking (guest_id, room_id, check_in_date, check_out_date, is_active) VALUES
    (1, 1, '2025-12-15', '2026-12-20', TRUE),
    (2, 3, '2025-12-10', '2025-12-15', FALSE),
    (3, 2, '2025-12-25', '2026-05-01', TRUE)
ON CONFLICT DO NOTHING;

-- Связи гостей и услуг
INSERT INTO guest_service (guest_id, service_id, service_date) VALUES
    (1, 1, '2025-12-16'),
    (1, 1, '2025-12-17'),
    (1, 1, '2025-12-18'),
    (1, 3, '2025-12-16'),
    (1, 5, '2025-12-15'),
    (2, 1, '2025-12-11'),
    (2, 6, '2025-12-12'),
    (3, 1, '2025-12-26'),
    (3, 7, '2025-12-25')
ON CONFLICT (guest_id, service_id, service_date) DO NOTHING;