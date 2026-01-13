-- 1. Таблица гостей
CREATE TABLE IF NOT EXISTS guest (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    passport_number VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Таблица номеров
CREATE TABLE IF NOT EXISTS room (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    number VARCHAR(10) NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL,  -- STANDARD, DELUXE, LUXURY
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',  -- AVAILABLE, OCCUPIED, UNDER MAINTENANCE
    price DECIMAL(10, 2) NOT NULL,
    capacity INTEGER NOT NULL,
    stars INTEGER CHECK (stars BETWEEN 1 AND 5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (price > 0),
    CHECK (capacity > 0)
);

-- 3. Таблица услуг
CREATE TABLE IF NOT EXISTS service (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(20) NOT NULL,  -- FOOD, CLEANING, COMFORT
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (price >= 0)
);

-- 4. Таблица бронирований
CREATE TABLE IF NOT EXISTS booking (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    guest_id INTEGER NOT NULL,
    room_id INTEGER NOT NULL,
    check_in_date TEXT NOT NULL,
    check_out_date TEXT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (guest_id) REFERENCES guest(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES room(id) ON DELETE RESTRICT,
    CHECK (check_out_date > check_in_date)
);

-- 5. Связующая таблица: услуги в бронировании
CREATE TABLE IF NOT EXISTS booking_service (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    booking_id INTEGER NOT NULL,
    service_id INTEGER NOT NULL,
    service_date TEXT NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES booking(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES service(id) ON DELETE RESTRICT,
    UNIQUE(booking_id, service_id, service_date)
);

-- Индексы
CREATE INDEX IF NOT EXISTS idx_room_number ON room(number);
CREATE INDEX IF NOT EXISTS idx_room_status ON room(status);
CREATE INDEX IF NOT EXISTS idx_guest_passport ON guest(passport_number);
CREATE INDEX IF NOT EXISTS idx_booking_dates ON booking(check_in_date, check_out_date);
CREATE INDEX IF NOT EXISTS idx_booking_guest ON booking(guest_id);
CREATE INDEX IF NOT EXISTS idx_booking_room ON booking(room_id);
CREATE INDEX IF NOT EXISTS idx_booking_active ON booking(is_active);