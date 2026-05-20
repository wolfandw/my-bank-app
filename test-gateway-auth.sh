#!/bin/bash

# Скрипт мгновенно завершится, если какая-то команда упадет с ошибкой
set -e

# ======= ⚙️ НАСТРОЙКИ КОНТУРА (Замените на ваши реальные данные) =======
KEYCLOAK_URL="http://localhost:8080"
REALM="my-bank-app-realm"
CLIENT_ID="front-ui"
CLIENT_SECRET="5eOiAHGLM8KvD2xxMRcCeIogKaFtnN3P" # 👈 Скопируйте секрет из админки Keycloak

USERNAME="user"
PASSWORD="user"

GATEWAY_URL="http://my-bank-app"
# ======================================================================

echo "Шаг 1: Запрашиваем токен доступа (access_token) у Keycloak..."

# Отправляем POST запрос и используем встроенную магию sed/grep, чтобы вырезать токен из JSON без jq
RESPONSE=$(curl -s -X POST "$KEYCLOAK_URL/realms/$REALM/protocol/openid-connect/token" \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "username=$USERNAME" \
     -d "password=$PASSWORD" \
     -d "grant_type=password" \
     -d "client_id=$CLIENT_ID" \
     -d "client_secret=$CLIENT_SECRET" \
     -d "scope=openid")

# Вытаскиваем access_token из сырого JSON
ACCESS_TOKEN=$(echo "$RESPONSE" | grep -o '"access_token":"[^"]*' | grep -o '[^"]*$')

if [ -z "$ACCESS_TOKEN" ]; then
    echo "❌ Ошибка: Не удалось получить токен. Проверьте логин/пароль или Client Secret в Keycloak."
    echo "Ответ сервера: $RESPONSE"
    exit 1
fi

echo "✅ Токен успешно получен!"
echo "------------------------------------------------------------"

echo "Шаг 2: Выполняем тестовый запрос к шлюзу в Kubernetes с токеном..."
echo "Вызов: GET $GATEWAY_URL/api/account"
echo "------------------------------------------------------------"

# Выполняем финальный запрос и выводим полные HTTP-заголовки ответа (-i)
curl -i -X GET "$GATEWAY_URL/api/account" \
     -H "Authorization: Bearer $ACCESS_TOKEN"

echo ""
echo "------------------------------------------------------------"
echo "Тест завершен."
