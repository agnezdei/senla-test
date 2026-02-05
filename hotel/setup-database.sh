#!/bin/bash

echo "Создаем базу данных hotel..."

# Удаляем старую базу если существует
echo "Удаляем старую базу данных..."
dropdb --if-exists hotel

# Создаем новую базу
echo "Создаем новую базу данных..."
createdb hotel

# Определяем путь к SQL файлам
echo "Поиск SQL файлов..."
if [ -f "sql/schema.sql" ]; then
    SCHEMA="sql/schema.sql"
    DATA="sql/data.sql"
elif [ -f "core-module/src/main/resources/schema.sql" ]; then
    SCHEMA="core-module/src/main/resources/schema.sql"
    DATA="core-module/src/main/resources/data.sql"
elif [ -f "src/main/resources/schema.sql" ]; then
    SCHEMA="src/main/resources/schema.sql"
    DATA="src/main/resources/data.sql"
else
    echo "ОШИБКА: Не найден schema.sql!"
    echo "Ищу в текущей директории: $(pwd)"
    find . -name "schema.sql" 2>/dev/null || echo "Файл не найден"
    exit 1
fi

echo "Используем схему: $SCHEMA"
echo "Используем данные: $DATA"

# Выполняем SQL файлы
echo "Выполняем schema.sql..."
psql -d hotel -f "$SCHEMA"

echo "Выполняем data.sql..."
psql -d hotel -f "$DATA"

echo "Готово! Проверка:"
echo "\dt" | psql hotel