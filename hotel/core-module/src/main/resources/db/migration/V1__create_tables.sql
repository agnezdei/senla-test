-- 1. Таблица гостей
CREATE TABLE IF NOT EXISTS guest (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    passport_number VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Таблица номеров
CREATE TABLE IF NOT EXISTS room (
    id BIGSERIAL PRIMARY KEY,
    number VARCHAR(10) NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL,  -- STANDARD, DELUXE, LUXURY
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',  -- AVAILABLE, OCCUPIED, UNDER MAINTENANCE
    price DOUBLE PRECISION NOT NULL,
    capacity INTEGER NOT NULL,
    stars INTEGER CHECK (stars BETWEEN 1 AND 5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT price_positive CHECK (price > 0),
    CONSTRAINT capacity_positive CHECK (capacity > 0)
);

-- 3. Таблица услуг
CREATE TABLE IF NOT EXISTS service (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    category VARCHAR(20) NOT NULL,  -- FOOD, CLEANING, COMFORT
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT service_price_non_negative CHECK (price >= 0)
);

-- 4. Таблица бронирований
CREATE TABLE IF NOT EXISTS booking (
    id BIGSERIAL PRIMARY KEY,
    guest_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (guest_id) REFERENCES guest(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES room(id) ON DELETE RESTRICT,
    CONSTRAINT valid_dates CHECK (check_out_date > check_in_date)
);

-- 5. Таблица guest_service
CREATE TABLE IF NOT EXISTS guest_service (
    id BIGSERIAL PRIMARY KEY,
    guest_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    service_date DATE NOT NULL,
    
    FOREIGN KEY (guest_id) REFERENCES guest(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES service(id) ON DELETE RESTRICT,
    
    CONSTRAINT unique_service_per_day UNIQUE(guest_id, service_id, service_date)
);

-- Индексы
CREATE INDEX IF NOT EXISTS idx_room_number ON room(number);
CREATE INDEX IF NOT EXISTS idx_room_status ON room(status);
CREATE INDEX IF NOT EXISTS idx_guest_passport ON guest(passport_number);
CREATE INDEX IF NOT EXISTS idx_booking_dates ON booking(check_in_date, check_out_date);
CREATE INDEX IF NOT EXISTS idx_booking_guest ON booking(guest_id);
CREATE INDEX IF NOT EXISTS idx_booking_room ON booking(room_id);
CREATE INDEX IF NOT EXISTS idx_booking_active ON booking(is_active);
CREATE INDEX IF NOT EXISTS idx_guest_service_guest ON guest_service(guest_id);
CREATE INDEX IF NOT EXISTS idx_guest_service_service ON guest_service(service_id);
CREATE INDEX IF NOT EXISTS idx_guest_service_date ON guest_service(service_date);