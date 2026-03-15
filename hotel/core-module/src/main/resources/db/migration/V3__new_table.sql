CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     username VARCHAR(100) UNIQUE NOT NULL,
                                     password VARCHAR(100) NOT NULL,
                                     role VARCHAR(50) NOT NULL,
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users (username, password, role) VALUES
                                              ('Тест', '$2a$12$4ohPAbmnb6XY/5nea0KFye603/4RMGNwcS0njNuuJOp9H2wxkUFpW', 'USER'),
                                              ('Админ', '$2a$12$jAxJOvU7BrPgaRPq1OjGkudUeyw.9OwqpIItb5D.2kZhoONxG.DtS', 'ADMIN');