INSERT OR IGNORE INTO room_type (name, display_name) VALUES
    ('STANDARD', 'Стандарт'),
    ('BUSINESS', 'Бизнес'),
    ('LUXURY', 'Люкс');

INSERT OR IGNORE INTO room_status (status, display_name) VALUES
    ('AVAILABLE', 'Доступен'),
    ('OCCUPIED', 'Занят'),
    ('UNDER_MAINTENANCE', 'На ремонте');

INSERT OR IGNORE INTO service_category (name, display_name) VALUES
    ('FOOD', 'Питание'),
    ('CLEANING', 'Обслуживание'),
    ('COMFORT', 'Комфорт');

INSERT OR IGNORE INTO guest (name, passport_number) VALUES
    ('Иван Иванов', '444444'),
    ('Мария Сидорова', '666666'),
    ('Петр Петров', '888888');

INSERT OR IGNORE INTO room (number, room_type_id, room_status_id, price, capacity, stars) VALUES
    ('101', 1, 1, 2500.00, 2, 3),
    ('102', 1, 1, 2500.00, 2, 3),
    ('201', 2, 1, 3500.00, 3, 4),
    ('202', 2, 1, 3500.00, 3, 4),
    ('301', 3, 1, 5000.00, 4, 5),
    ('302', 3, 1, 5000.00, 4, 5),
    ('303', 3, 3, 5000.00, 4, 5);

INSERT OR IGNORE INTO service (name, price, category_id) VALUES
    ('Завтрак шведский стол', 800.00, 1),
    ('Ужин à la carte', 1500.00, 1),
    ('Ежедневная уборка', 500.00, 2),
    ('Смена белья', 300.00, 2),
    ('Мини-бар (стандартный набор)', 1200.00, 3),
    ('SPA-процедуры (базовый пакет)', 2500.00, 3),
    ('Трансфер из/в аэропорт', 2000.00, 3);

INSERT OR IGNORE INTO booking (guest_id, room_id, check_in_date, check_out_date, is_active) VALUES
    (1, 1, '2025-12-15', '2026-12-20', TRUE),
    (2, 3, '2025-12-10', '2025-12-15', FALSE),
    (3, 2, '2025-12-25', '2026-05-01', TRUE);

INSERT OR IGNORE INTO booking_service (booking_id, service_id, service_date) VALUES
    (1, 1, '2025-12-16'),
    (1, 1, '2025-12-17'),
    (1, 1, '2025-12-18'),
    (1, 3, '2025-12-16'),
    (1, 5, '2025-12-15'),
    (2, 1, '2025-12-11'),
    (2, 6, '2025-12-12'),
    (3, 1, '2025-12-26'),
    (3, 7, '2025-12-25');