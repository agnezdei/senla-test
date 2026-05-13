#!/bin/bash

set -e
echo "===== Остановка и удаление старых контейнеров (сохраняем данные БД) ====="
docker-compose down || true

echo "===== Сборка проекта (без тестов) ====="
mvn clean package -DskipTests

echo "===== Сборка Docker-образа ====="
docker-compose build

echo "===== Запуск контейнеров в фоновом режиме ====="
docker-compose up -d

echo "===== Ожидание готовности приложения ====="
sleep 5
echo "Проверка статуса контейнеров:"
docker-compose ps

echo "===== Приложение запущено ====="
echo "API доступно по адресу: http://localhost:8080"