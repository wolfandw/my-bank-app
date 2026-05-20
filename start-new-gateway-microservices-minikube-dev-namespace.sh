#!/bin/bash

echo "Запуск локального контура мониторинга, авторизации и фронтенда (Docker Compose)..."
COMPOSE_FILE="front-ui-zpg-elk-docker-compose.yml"
echo "Удаляем локальные образы front-ui, elk..."
sudo docker compose -f "$COMPOSE_FILE" down -v
sleep 20
echo "Cобираем локальные образы front-ui, elk..."
docker compose -f "$COMPOSE_FILE" up -d
echo "Контур Docker Compose (Front UI, Keycloak, ELK, Prometheus, Grafana, Zipkin) успешно запущен!"

echo ""
echo "Стартуем Minikube..."
minikube delete
minikube start --driver=docker --memory=8g --cpus=4
minikube kubectl -- get pods -A
minikube addons enable ingress

echo "Ожидаем полную готовность Ingress-контроллера Nginx..."
minikube kubectl -- wait --namespace ingress-nginx \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=controller \
  --timeout=120s

sleep 5
echo "Ingress-контроллер полностью готов к валидации чартов!"

echo ""
echo "Создаем namespace dev..."
minikube kubectl create namespace dev

eval "$(minikube -p minikube docker-env)"

upgrade_install() {
    local databases=$1
    local services=$2

    helm upgrade --install my-bank-app-dev ./helm/my-bank-app-chart -n dev \
        -f ./helm/my-bank-app-chart/values.yaml \
        -f ./helm/my-bank-app-chart/values-dev.yaml \
        -f ./helm/my-bank-app-chart/values-secrets.yaml \
        --set global.deployDatabases="$databases" \
        --set global.deployServices="$services" \
        --set account.image.pullPolicy=Never \
        --set cash.image.pullPolicy=Never \
        --set gateway.image.pullPolicy=Never \
        --set notification.image.pullPolicy=Never \
        --set transfer.image.pullPolicy=Never \
        --set kafka.enabled=true \
        --set global.deployKafka=true
}

echo ""
echo "Ждём готовности подов Postgres..."
upgrade_install "true" "false"
sleep 5
minikube kubectl -- wait --for=condition=ready pod -l app=notificationsdata -n dev --timeout=300s
minikube kubectl -- wait --for=condition=ready pod -l app=accountsdata -n dev --timeout=300s
minikube kubectl -- wait --for=condition=ready pod -l app=cashdata -n dev --timeout=300s
minikube kubectl -- wait --for=condition=ready pod -l app=transferdata -n dev --timeout=300s
echo "Поды Postgres готовы"

echo ""
echo "Ждем готовности подов сервисов..."
echo "Билдим контейнеры внутренних микросервисов для Minikube..."
docker build . -f ./accounts/Dockerfile -t "accounts-image:0.1.0"
docker build . -f ./cash/Dockerfile -t "cash-image:0.1.0"
docker build . -f ./gateway/Dockerfile -t "gateway-image:0.1.0"
docker build . -f ./notifications/Dockerfile -t "notifications-image:0.1.0"
docker build . -f ./transfer/Dockerfile -t "transfer-image:0.1.0"

URL="http://localhost:8080/realms/master/.well-known/openid-configuration"
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

upgrade_install "true" "true"
sleep 5
eval "$(minikube -p minikube docker-env -u)"

echo "Поды сервисов - ожидание инициализации..."
minikube kubectl -- wait --for=condition=ready pod -l app=notifications-chart -n dev --timeout=300s
minikube kubectl -- wait --for=condition=ready pod -l app=accounts-chart -n dev --timeout=300s
minikube kubectl -- wait --for=condition=ready pod -l app=cash-chart -n dev --timeout=300s
minikube kubectl -- wait --for=condition=ready pod -l app=transfer-chart -n dev --timeout=300s
minikube kubectl -- wait --for=condition=ready pod -l app=gateway-chart -n dev --timeout=300s
echo "Внутренние сервисы Kubernetes готовы и запущены..."

echo ""
echo "Добавление host.docker.internal и my-bank-app in /etc/hosts ..."
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
add_host "172.17.0.1" "host.docker.internal"
add_host "127.0.0.1" "keycloak"
add_host "$MINIKUBE_IP" "my-bank-app"

echo ""
echo "Состояние сервисов в Kubernetes..."
minikube kubectl -- get pods -n dev

echo ""
echo "Запускаем интеграционные helm-тесты..."
helm test my-bank-app-dev -n dev --logs

echo ""
echo "============================================================="
echo "       КОНТУР БАНКА УСПЕШНО РАЗВЕРНУТ И ГОТОВ К РАБОТЕ!      "
echo "============================================================="
echo " - Веб-интерфейс приложения (Front UI): http://localhost:8089"
echo " - API Шлюз (Kubernetes Ingress):       http://my-bank-app   "
echo " - Панель авторизации (Keycloak):       http://localhost:8080"
echo " - Трассировка запросов (Zipkin):       http://localhost:9411"
echo " - Метрики (Prometheus):                http://localhost:9090"
echo " - Метрики и Дашборды (Grafana):        http://localhost:3000"
echo " - Логирование и Поиск (Kibana):        http://localhost:5601"
echo "============================================================="