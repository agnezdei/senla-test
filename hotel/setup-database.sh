#!/bin/bash
# Простой скрипт создания базы данных

echo "Создаем базу данных hotel.db..."

# Удаляем старый файл если существует
if [ -f "hotel.db" ]; then
    echo "Удаляем старую базу данных..."
    rm hotel.db
fi

# Определяем путь к SQL файлам
if [ -f "sql/schema.sql" ]; then
    SCHEMA="sql/schema.sql"
    DATA="sql/data.sql"
elif [ -f "core-module/resources/schema.sql" ]; then
    SCHEMA="core-module/resources/schema.sql"
    DATA="core-module/resources/data.sql"
else
    echo "ОШИБКА: Не найден schema.sql!"
    exit 1
fi

echo "Используем схему: $SCHEMA"
echo "Используем данные: $DATA"

# Создаем базу
echo "Выполняем schema.sql..."
sqlite3 hotel.db ".read $SCHEMA"

echo "Выполняем data.sql..."
sqlite3 hotel.db ".read $DATA"

echo "Готово! Проверка:"
echo ".tables" | sqlite3 hotel.db