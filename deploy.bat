@echo off
docker compose down
docker compose up -d --build
docker compose ps
