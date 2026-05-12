-- Таблица пользователей
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(100) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'USER'))
);

-- Таблица каталога
CREATE TABLE catalog (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         parent_id BIGINT,
                         CONSTRAINT fk_catalog_parent FOREIGN KEY (parent_id) REFERENCES catalog(id) ON DELETE RESTRICT
);

-- Таблица изданий книг
CREATE TABLE book_edition (
                              id BIGSERIAL PRIMARY KEY,
                              title VARCHAR(500) NOT NULL,
                              author VARCHAR(255),
                              isbn VARCHAR(20) UNIQUE,
                              publication_year INT,
                              description TEXT
);

-- Таблица экземпляров книг
CREATE TABLE book_copy (
                           id BIGSERIAL PRIMARY KEY,
                           inventory_number VARCHAR(100) NOT NULL UNIQUE,
                           status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'BORROWED')),
                           edition_id BIGINT NOT NULL,
                           catalog_id BIGINT NOT NULL,
                           CONSTRAINT fk_copy_edition FOREIGN KEY (edition_id) REFERENCES book_edition(id) ON DELETE RESTRICT,
                           CONSTRAINT fk_copy_catalog FOREIGN KEY (catalog_id) REFERENCES catalog(id) ON DELETE RESTRICT
);

-- Таблица истории выдач/возвратов
CREATE TABLE borrow_record (
                               id BIGSERIAL PRIMARY KEY,
                               user_id BIGINT NOT NULL,
                               copy_id BIGINT NOT NULL,
                               borrowed_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                               due_date DATE NOT NULL,
                               returned_at TIMESTAMPTZ,
                               CONSTRAINT fk_borrow_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
                               CONSTRAINT fk_borrow_copy FOREIGN KEY (copy_id) REFERENCES book_copy(id) ON DELETE RESTRICT
);

-- Индексы для производительности
CREATE INDEX idx_catalog_parent ON catalog(parent_id);
CREATE INDEX idx_copy_edition ON book_copy(edition_id);
CREATE INDEX idx_copy_catalog ON book_copy(catalog_id);
CREATE INDEX idx_copy_status ON book_copy(status);
CREATE INDEX idx_borrow_user ON borrow_record(user_id);
CREATE INDEX idx_borrow_copy ON borrow_record(copy_id);
CREATE INDEX idx_borrow_due_not_returned ON borrow_record(due_date) WHERE returned_at IS NULL;