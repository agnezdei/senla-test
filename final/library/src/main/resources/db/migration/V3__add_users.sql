-- пароль: admin
INSERT INTO users (username, password_hash, role) VALUES
    ('admin', '$2a$10$H6MpgugJfweaYjhV7iZJfesr7oYjB.hjei/Slq6tWKcSvsinXHJXy', 'ADMIN');

-- пароль: 123456
INSERT INTO users (username, password_hash, role) VALUES
                                                      ('alice', '$2a$10$1GWX0B8eutv4YGyL0J8SuuGUtB7F.APwjr3XbqHrF.k.5aBWHJnDu', 'USER'),
                                                      ('bob',   '$2a$10$1GWX0B8eutv4YGyL0J8SuuGUtB7F.APwjr3XbqHrF.k.5aBWHJnDu', 'USER'),
                                                      ('carol', '$2a$10$1GWX0B8eutv4YGyL0J8SuuGUtB7F.APwjr3XbqHrF.k.5aBWHJnDu', 'USER');

INSERT INTO book_edition (title, author, isbn, publication_year, description) VALUES
    ('Мастер и Маргарита', 'Михаил Булгаков', '978-5-17-118895-4', 1967, 'Роман-мистерия');

INSERT INTO book_copy (inventory_number, status, edition_id, catalog_id) VALUES
    ('INV-007', 'AVAILABLE',
     (SELECT id FROM book_edition WHERE isbn = '978-5-17-118895-4'),
     (SELECT id FROM catalog WHERE name = 'Художественная литература'));

INSERT INTO book_copy (inventory_number, status, edition_id, catalog_id) VALUES
    ('INV-008', 'AVAILABLE',
     (SELECT id FROM book_edition WHERE isbn = '978-5-17-118890-9'),
     (SELECT id FROM catalog WHERE name = 'Художественная литература'));

INSERT INTO book_copy (inventory_number, status, edition_id, catalog_id) VALUES
    ('INV-009', 'AVAILABLE',
     (SELECT id FROM book_edition WHERE isbn = '978-5-17-118891-6'),
     (SELECT id FROM catalog WHERE name = 'Художественная литература'));

INSERT INTO book_copy (inventory_number, status, edition_id, catalog_id) VALUES
    ('INV-010', 'AVAILABLE',
     (SELECT id FROM book_edition WHERE isbn = '978-5-17-118893-0'),
     (SELECT id FROM catalog WHERE name = 'Фантастика'));

-- alice взяла INV-003 (Преступление и наказание) на 14 дней с сегодня
INSERT INTO borrow_record (user_id, copy_id, borrowed_at, due_date, returned_at)
VALUES (
           (SELECT id FROM users WHERE username = 'alice'),
           (SELECT id FROM book_copy WHERE inventory_number = 'INV-003'),
           NOW(),
           NOW() + INTERVAL '14 days',
           NULL
       );

-- bob взял INV-006 (Физика для любознательных), уже просрочена на 5 дней
INSERT INTO borrow_record (user_id, copy_id, borrowed_at, due_date, returned_at)
VALUES (
           (SELECT id FROM users WHERE username = 'bob'),
           (SELECT id FROM book_copy WHERE inventory_number = 'INV-006'),
           NOW() - INTERVAL '20 days',
           NOW() - INTERVAL '5 days',   -- due_date = 5 дней назад
           NULL
       );

-- carol вернула INV-001 (Война и мир)
INSERT INTO borrow_record (user_id, copy_id, borrowed_at, due_date, returned_at)
VALUES (
           (SELECT id FROM users WHERE username = 'carol'),
           (SELECT id FROM book_copy WHERE inventory_number = 'INV-001'),
           NOW() - INTERVAL '30 days',
           NOW() - INTERVAL '16 days',
           NOW() - INTERVAL '15 days'
       );

-- alice вернула INV-004 (Дюна)
INSERT INTO borrow_record (user_id, copy_id, borrowed_at, due_date, returned_at)
VALUES (
           (SELECT id FROM users WHERE username = 'alice'),
           (SELECT id FROM book_copy WHERE inventory_number = 'INV-004'),
           NOW() - INTERVAL '10 days',
           NOW() - INTERVAL '3 days',
           NOW() - INTERVAL '2 days'
       );

-- carol взяла INV-007 (Мастер и Маргарита) на 7 дней
INSERT INTO borrow_record (user_id, copy_id, borrowed_at, due_date, returned_at)
VALUES (
           (SELECT id FROM users WHERE username = 'carol'),
           (SELECT id FROM book_copy WHERE inventory_number = 'INV-007'),
           NOW(),
           NOW() + INTERVAL '7 days',
           NULL
       );