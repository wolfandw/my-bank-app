# Модуль 3. Спринт 11. Микросервисное приложение «Банк» с развёртыванием микросервисов в Kubernetes, с использованием
Helm-чартов и отправкой уведомлений в сервис Notifications через Apache Kafka + Zipkin + Prometheus + Grafana + ELK-стекю

## Описание

Приложение работает во встроенном сервлет-контейнере Netty и доступно по адресу http://localhost:8089/,
использует Postgres базы данных и сервер авторизации Keycloack и Apache Kafka + Zipkin + Prometheus + Grafana + ELK-стек.
Развёртывание микросервисов осуществляется локально в Kubernetes в реализации Minikube в Docker.
В качестве пакетного менеджера и шаблонизатора для развёртывания микросервисов используется Helm.
Отправка уведомлений в сервис Notifications осуществляется через Apache Kafka.
Keycloack + Zipkin + Prometheus + Grafana + ELK-стек и Front UI развёрнуты локально в Docker.
Доступ к микросервисам снаружи кластера Kubernetes осуществляется через сервис Ingress.

## Функциональность
Микросервисное приложение «Банк» — это приложение с веб-интерфейсом (фронт), которое позволяет пользователю (клиенту банка):
- редактировать данные своего аккаунта (фамилию и имя, дату рождения);
- класть виртуальные деньги на счёт своего аккаунта и снимать их;
- переводить виртуальные деньги на счёт другого аккаунта.
Приложение состоит из следующих частей:
- фронт (Front UI);
- микросервис аккаунтов (Accounts);
- микросервис обналичивания денег (Cash);
- микросервис перевода денег на счёт другого аккаунта (Transfer);
- микросервис уведомлений (Notifications).

Все пользователи делятся на авторизованных и анонимных.
Авторизация пользователей (покупателей) осуществляется по логину/паролю. Доступны два предустановленных
пользователя (пароль): admin (admin) и user (user).

Для работы с приложением необходимо клонировать репозиторий проекта, перейти в каталог проекта, собрать,
протестировать и запустить приложение, а так же БД Postgres, сервер авторизации Keycloack и Apache Kafka
+ Zipkin + Prometheus + Grafana + ELK-стек.
Для комплексного запуска всех компонентов, микросервисов в Docker использовать
Docker Compose в корне проекта: ./docker-compose-localhost.yml
Для комплексного запуска всех компонентов в Docker и микросервисов в Kubernetes использовать Minikube,
так же развернутый в Docker, и скрипт в корне проекта: ./start-new-gateway-microservices-minikube-dev-namespace.sh

## Начало
- Клонировать репозиторий продукта
- Перейти в каталог продукта

## Сборка, тестирование
- Как собирать:```mvn clean package -DskipTests=true -Dmaven.test.skip=true```
- Как запускать тесты:```mvn test```

## Как запускать в Docker Compose
- Запустить в терминале```sudo docker compose -f docker-compose-localhost.yml up```
- Проверить наличие образов приложения, Postgres, Keycloack + Zipkin + Prometheus + Grafana + ELK-стек ```sudo docker image ls```
- Запустить в терминале```sudo docker container ls``` и убедиться, что контейнеры запущены
- Остановить контейнеры можно командой```sudo docker compose -f docker-compose-localhost.yml dowm``` в терминале
- Запустить заново продукт можно командой```sudo docker compose -f docker-compose-localhost.yml up``` в терминале

## Как пользоваться в Docker Compose
- Перейти по адресу: http://localhost:8089/ на страницу продукта
- Войти в приложение как user или admin
- Keycloak доступен по адресу: http://localhost:8080
- UI Kafka доступен по адресу: http://localhost:8090

## Как запускать гибрид Docker и Kubernetes
- Выполнить в терминале:```chmod +x start-new-gateway-microservices-minikube-dev-namespace.sh```
- Запустить в терминале скрипт развертывания: старт нового кластера minikube, сборка Docker-образов,
  запуск Helm-чартов ```./start-new-gateway-microservices-minikube-dev-namespace.sh```
- Дождаться загрузки и инициализации сервисов и прохождения helm-тестов
  - Для оценки состояния minikube можно запустить дашборд: ```minikube dashboard```

## Как пользоваться в гибрид Docker и Kubernetes
- Перейти по адресу: http://localhost:8089/ на страницу продукта
- Войти в приложение как user или admin
- Keycloak доступен по адресу:   http://keycloak/
- UI Kafka доступен по адресу:   http://localhost:8090
  - предварительно, необходимо пробросить порты командой в терминале: ```minikube kubectl -- port-forward -n dev svc/kafka-ui-service 8090:8080```
- Трассировка запросов (Zipkin): http://localhost:9411
- Метрики (Prometheus):          http://localhost:9090
- Метрики и Дашборды (Grafana):  http://localhost:3000
- Логирование и Поиск (Kibana):  http://localhost:5601