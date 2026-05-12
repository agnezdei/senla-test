-- Корневые каталоги
INSERT INTO catalog (name, parent_id) VALUES
                                          ('Художественная литература', NULL),
                                          ('Научная литература', NULL),
                                          ('Детская литература', NULL);

-- Подкаталоги
INSERT INTO catalog (name, parent_id) VALUES
                                          ('Фантастика', (SELECT id FROM catalog WHERE name = 'Художественная литература')),
                                          ('Детектив', (SELECT id FROM catalog WHERE name = 'Художественная литература')),
                                          ('Физика', (SELECT id FROM catalog WHERE name = 'Научная литература')),
                                          ('Математика', (SELECT id FROM catalog WHERE name = 'Научная литература'));

-- Издания книг
INSERT INTO book_edition (title, author, isbn, publication_year, description) VALUES
                                                                                  ('Война и мир', 'Лев Толстой', '978-5-17-118890-9', 1869, 'Роман-эпопея'),
                                                                                  ('Преступление и наказание', 'Фёдор Достоевский', '978-5-17-118891-6', 1866, 'Роман'),
                                                                                  ('Краткие ответы на большие вопросы', 'Стивен Хокинг', '978-5-17-118892-3', 2018, 'Научно-популярная'),
                                                                                  ('Дюна', 'Фрэнк Герберт', '978-5-17-118893-0', 1965, 'Фантастический роман'),
                                                                                  ('Физика для любознательных', 'Эрик Роджерс', '978-5-17-118894-7', 1960, 'Учебник');

-- Экземпляры книг (статус AVAILABLE)
INSERT INTO book_copy (inventory_number, status, edition_id, catalog_id) VALUES
                                                                             ('INV-001', 'AVAILABLE', (SELECT id FROM book_edition WHERE isbn = '978-5-17-118890-9'), (SELECT id FROM catalog WHERE name = 'Художественная литература')),
                                                                             ('INV-002', 'AVAILABLE', (SELECT id FROM book_edition WHERE isbn = '978-5-17-118890-9'), (SELECT id FROM catalog WHERE name = 'Художественная литература')),
                                                                             ('INV-003', 'AVAILABLE', (SELECT id FROM book_edition WHERE isbn = '978-5-17-118891-6'), (SELECT id FROM catalog WHERE name = 'Художественная литература')),
                                                                             ('INV-004', 'AVAILABLE', (SELECT id FROM book_edition WHERE isbn = '978-5-17-118893-0'), (SELECT id FROM catalog WHERE name = 'Фантастика')),
                                                                             ('INV-005', 'AVAILABLE', (SELECT id FROM book_edition WHERE isbn = '978-5-17-118892-3'), (SELECT id FROM catalog WHERE name = 'Физика'));

INSERT INTO book_copy (inventory_number, status, edition_id, catalog_id) VALUES
    ('INV-006', 'BORROWED', (SELECT id FROM book_edition WHERE isbn = '978-5-17-118894-7'), (SELECT id FROM catalog WHERE name = 'Физика'));
