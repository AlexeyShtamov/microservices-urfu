# Microservices URFU — API Gateway with Caching

Этот проект демонстрирует слой API Gateway (BFF), который объединяет три простых микросервиса и добавляет к ним кэширование, rate limiting и авторизацию по JWT.

## Архитектура
- **user-service** (порт `8081`): отдаёт пользователей (`/users`, `/users/{id}`).
- **order-service** (порт `8082`): отдаёт заказы пользователя (`/orders/user/{userId}`).
- **product-service** (порт `8083`): отдаёт товары (`/products/{id}`).
- **api-gateway** (порт `8080`): агрегирует профиль пользователя `GET /api/profile/{userId}`.

### Возможности Gateway
- Агрегация: собирает пользователя, его заказы и товары по каждому заказу в единый ответ.
- Кэш Redis на 30 секунд для профилей (`cached: true` в ответе, если данные взяты из кэша).
- Rate limiting: максимум 30 запросов в минуту с одного IP (кроме `/actuator`).
- JWT guard: требуется заголовок `Authorization: Bearer demo-token` (значение можно поменять в `security.jwt.token`).
- Fallback/retry: простые заглушки, когда сервисы недоступны (пустой список заказов, placeholder-пользователь/товар).

## Требования
- Java 21
- Maven Wrapper (входит в репозиторий)
- Redis, доступный на `localhost:6379`
  - Быстрый старт: `docker run -p 6379:6379 redis:7-alpine`

## Настройка
При необходимости обновите сервисные URL и JWT-токен в конфигурации `application.yaml` каждого сервиса (например, если меняете порты или выносите в Docker).

## Запуск
Откройте по окну/вкладке терминала на каждый сервис и выполните команды из корня репозитория:

```bash
# 1. User Service (8081)
cd user-service
./mvnw spring-boot:run
```

```bash
# 2. Order Service (8082)
cd order-service
./mvnw spring-boot:run
```

```bash
# 3. Product Service (8083)
cd product-service
./mvnw spring-boot:run
```

```bash
# 4. API Gateway (8080)
cd api-gateway
./mvnw spring-boot:run
```

Убедитесь, что Redis запущен до старта Gateway.

## Проверка работы
1. Получить профиль пользователя (агрегация):
   ```bash
   curl -H "Authorization: Bearer demo-token" http://localhost:8080/api/profile/1
   ```
   Повторный запрос в течение 30 секунд вернёт `"cached": true`.

2. Индивидуальные сервисы можно дернуть напрямую для отладки:
   - `curl http://localhost:8081/users/1`
   - `curl http://localhost:8082/orders/user/1`
   - `curl http://localhost:8083/products/1`

3. Rate limit: при более чем 30 запросах в минуту с одного IP Gateway вернёт `429 Too Many Requests`.

## Тесты
Во всех сервисах есть базовые модульные тесты Maven. Запуск из корня каждого сервиса:
```bash
cd api-gateway && ./mvnw test
cd user-service && ./mvnw test
cd order-service && ./mvnw test
cd product-service && ./mvnw test
```
