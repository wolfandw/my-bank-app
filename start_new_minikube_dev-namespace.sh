#!/bin/bash

echo "Стартуем Minikube..."
minikube delete
minikube start --driver=docker --memory=8g --cpus=4
minikube kubectl -- get pods -A
minikube addons enable ingress

echo ""
echo "Создаем namespace dev..."
minikube kubectl create namespace dev

eval $(minikube -p minikube docker-env)

upgrade_install() {
    local databases=$1
    local keycloak=$2
    local services=$3

    helm upgrade --install my-bank-app-dev ./helm/my-bank-app-chart -n dev \
        -f ./helm/my-bank-app-chart/values.yaml \
        -f ./helm/my-bank-app-chart/values-dev.yaml \
        --set global.deployDatabases="$databases" \
        --set global.deployKeycloak="$keycloak" \
        --set global.deployServices="$services" \
        --set account.image.pullPolicy=Never \
        --set cash.image.pullPolicy=Never \
        --set front-ui.image.pullPolicy=Never \
        --set gateway.image.pullPolicy=Never \
        --set notification.image.pullPolicy=Never \
        --set transfer.image.pullPolicy=Never \
        --set kafka.enabled=true \
        --set global.deployKafka=true
}

echo ""
echo "Ждём готовности подов Postgres..."
upgrade_install "true" "false" "false"
sleep 5
minikube kubectl -- wait --for=condition=ready pod -l app.kubernetes.io/name=postgresql -n dev --timeout=300s
echo ""
echo "postgresql готов"

minikube kubectl -- wait --for=condition=ready pod -l app=notificationsdata -n dev --timeout=300s
echo ""
echo "notificationsdata готов"

minikube kubectl -- wait --for=condition=ready pod -l app=accountsdata -n dev --timeout=300s
echo ""
echo "accountsdata готов"

minikube kubectl -- wait --for=condition=ready pod -l app=cashdata -n dev --timeout=300s
echo ""
echo "cashdata готов"

minikube kubectl -- wait --for=condition=ready pod -l app=transferdata -n dev --timeout=300s
echo ""
echo "transferdata готов"
echo "Поды Postgres готовы"

echo ""
echo "Ждём готовности пода Keycloak (около 5 мин)..."
upgrade_install "true" "true" "false"
sleep 5
minikube kubectl -- wait --for=condition=ready pod -l app.kubernetes.io/component=keycloak -n dev --timeout=360s

URL="http://keycloak/realms/master/.well-known/openid-configuration"
MAX_RETRIES=30
SLEEP_INTERVAL=5
SUCCESS=false
echo ""
echo "Ожидание готовности Keycloak по адресу: $URL"
for ((i=1; i<=$MAX_RETRIES; i++)); do
    # -s: тихий режим, -o /dev/null: не выводить тело ответа, -w: выводить только код ответа
    STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$URL")

    if [ "$STATUS" -eq 200 ]; then
        echo "Успех! Keycloak готов (HTTP 200). Попыток: $i"
        SUCCESS=true
        break
    fi

    echo "Попытка $i/$MAX_RETRIES: Сервер еще не готов (Код: $STATUS). Ждем $SLEEP_INTERVAL сек..."
    sleep $SLEEP_INTERVAL
done

if [ "$SUCCESS" = true ]; then
    echo "Keycloak успешно запущен и инициализирован по адресу: $URL"
else
    echo "Ошибка: Keycloak не ответил за отведенное время."
    exit 1
fi

echo ""
echo "Ждем готовности подов сервисов..."
docker build . -f ./accounts/Dockerfile -t "accounts-image:0.1.0"
docker build . -f ./cash/Dockerfile -t "cash-image:0.1.0"
docker build . -f ./frontui/Dockerfile -t "frontui-image:0.1.0"
docker build . -f ./gateway/Dockerfile -t "gateway-image:0.1.0"
docker build . -f ./notifications/Dockerfile -t "notifications-image:0.1.0"
docker build . -f ./transfer/Dockerfile -t "transfer-image:0.1.0"

upgrade_install "true" "true" "true"
sleep 5
eval $(minikube -p minikube docker-env -u)

echo "Поды сервисов - ожидание инициализации..."
minikube kubectl -- wait --for=condition=ready pod -l app=notifications-chart -n dev --timeout=300s
minikube kubectl -- wait --for=condition=ready pod -l app=accounts-chart -n dev --timeout=300s
minikube kubectl -- wait --for=condition=ready pod -l app=cash-chart -n dev --timeout=300s
minikube kubectl -- wait --for=condition=ready pod -l app=transfer-chart -n dev --timeout=300s
minikube kubectl -- wait --for=condition=ready pod -l app=gateway-chart -n dev --timeout=300s
minikube kubectl -- wait --for=condition=ready pod -l app=frontui-chart -n dev --timeout=300s
echo "Сервисы готовы и запущены..."

echo ""
echo "Добавление keycloak и my-bank-app в /etc/hosts ..."
HOSTS_FILE="/etc/hosts"
MINIKUBE_IP=$(minikube ip)

add_host() {
    local ip=$1
    local domain=$2
    local entry="$ip $domain"

    if grep -q "[[:space:]]$domain$" "$HOSTS_FILE"; then
        echo "Запись для $domain уже существует."
    else
        echo "Добавляю запись: $entry"
        echo "$entry" | sudo tee -a "$HOSTS_FILE" > /dev/null
    fi
}
add_host "$MINIKUBE_IP" "keycloak"
add_host "$MINIKUBE_IP" "my-bank-app"

echo ""
echo "Состояние сервисов ..."
minikube kubectl -- get pods -n dev

echo ""
echo "Запускаем helm-тесты..."
helm test my-bank-app-dev -n dev --logs

echo ""
echo "My-bank-app успешно запущен и инициализирован по адресу: http://my-bank-app"

