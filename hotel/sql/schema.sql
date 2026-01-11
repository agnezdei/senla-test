-- 1. Справочник типов номеров
CREATE TABLE IF NOT EXISTS room_type (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(20) NOT NULL UNIQUE,
    display_name VARCHAR(50) NOT NULL
);

-- 2. Справочник статусов номеров
CREATE TABLE IF NOT EXISTS room_status (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    status VARCHAR(30) NOT NULL UNIQUE,
    display_name VARCHAR(50)
);

-- 3. Справочник категорий услуг
CREATE TABLE IF NOT EXISTS service_category (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(20) NOT NULL UNIQUE,
    display_name VARCHAR(50) NOT NULL
);

-- 4. Таблица гостей
CREATE TABLE IF NOT EXISTS guest (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    passport_number VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. Таблица номеров
CREATE TABLE IF NOT EXISTS room (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    number VARCHAR(10) NOT NULL UNIQUE,
    room_type_id INTEGER NOT NULL,
    room_status_id INTEGER NOT NULL DEFAULT 1,
    price DECIMAL(10, 2) NOT NULL,
    capacity INTEGER NOT NULL,
    stars INTEGER CHECK (stars BETWEEN 1 AND 5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (room_type_id) REFERENCES room_type(id) ON DELETE RESTRICT,
    FOREIGN KEY (room_status_id) REFERENCES room_status(id) ON DELETE RESTRICT,
    
    CHECK (price > 0),
    CHECK (capacity > 0)
);

-- 6. Таблица услуг
CREATE TABLE IF NOT EXISTS service (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    category_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (category_id) REFERENCES service_category(id) ON DELETE RESTRICT,
    
    CHECK (price >= 0)
);

-- 7. Таблица бронирований
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

-- 8. Связующая таблица: услуги в бронировании (Booking.ServiceWithDate)
CREATE TABLE IF NOT EXISTS booking_service (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    booking_id INTEGER NOT NULL,
    service_id INTEGER NOT NULL,
    service_date DATE NOT NULL,
    
    FOREIGN KEY (booking_id) REFERENCES booking(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES service(id) ON DELETE RESTRICT,
    
    UNIQUE(booking_id, service_id, service_date)
);

CREATE INDEX IF NOT EXISTS idx_room_number ON room(number);
CREATE INDEX IF NOT EXISTS idx_room_status ON room(room_status_id);
CREATE INDEX IF NOT EXISTS idx_guest_passport ON guest(passport_number);
CREATE INDEX IF NOT EXISTS idx_booking_dates ON booking(check_in_date, check_out_date);
CREATE INDEX IF NOT EXISTS idx_booking_guest ON booking(guest_id);
CREATE INDEX IF NOT EXISTS idx_booking_room ON booking(room_id);
CREATE INDEX IF NOT EXISTS idx_booking_active ON booking(is_active);