#!/bin/bash
docker-compose up -d db
sleep 5
mvn flyway:migrate