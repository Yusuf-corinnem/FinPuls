# 🚀 Руководство по разработке FinPuls

Документация по настройке, запуску и работе с проектом FinPuls.

---

## 📋 Содержание

1. [Быстрый старт](#быстрый-старт)
2. [Запуск через Docker](#запуск-через-docker)
3. [Архитектура Middleware](#архитектура-middleware)
4. [Система авторизации](#система-авторизации)
5. [Система логирования](#система-логирования)
6. [Обработка запросов](#обработка-запросов)
7. [Переменные окружения](#переменные-окружения)
8. [Полезные команды](#полезные-команды)

---

## 🏁 Быстрый старт

### Требования

- Docker и Docker Compose
- Git
- Java 21 (для локальной разработки)
- Maven 3.8+ (для локальной разработки)
- Node.js 18+ (для локальной разработки frontend)

### Первый запуск

```bash
# 1. Клонировать репозиторий (если еще не склонирован)
git clone <repository-url>
cd FinPuls

# 2. Создать файл .env (если его нет)
cp example.env .env
# Отредактировать .env и указать необходимые переменные

# 3. Запустить приложение
docker compose up --build -d

# 4. Проверить статус
docker compose ps

# 5. Посмотреть логи
docker logs -f finpuls-backend
# Примечание: Bank Module логи находятся в том же контейнере backend
```

### Проверка работоспособности

```bash
# Проверка health endpoint Backend
curl http://localhost:8080/actuator/health

# Ожидаемый ответ:
# {"status":"UP"}
```

---

## 🐳 Запуск через Docker

### Команды Docker Compose

```bash
# Сборка и запуск в фоновом режиме
docker compose up --build -d

# Остановка контейнеров
docker compose down

# Перезапуск контейнеров
docker compose restart

# Остановка с удалением volumes
docker compose down -v

# Просмотр логов
docker compose logs -f backend

# Просмотр логов конкретного контейнера
docker logs -f finpuls-backend

# Зайти внутрь контейнера
docker exec -it finpuls-backend bash

# Примечание: Bank Module находится внутри backend контейнера
# Логи Bank Module: logs/bank-module.log (внутри контейнера или на хосте)
```

### Структура Docker

#### Dockerfile (Multi-stage build)

```dockerfile
# Stage 1: Build
- Устанавливает Java 21 JDK и Maven
- Собирает приложение: mvn clean package
- Создает JAR файл

# Stage 2: Runtime
- Устанавливает только Java 21 JRE
- Копирует JAR из Stage 1
- Запускает приложение
```

#### docker-compose.yml

```yaml
services:
  postgres:
    - Порт: 5432 (внешний доступ)
    - Контейнер: finpuls-postgres
    - База данных: finpuls (пользователи, основная БД)
  
  postgres-bank:
    - Порт: 5433 (внешний доступ)
    - Контейнер: finpuls-postgres-bank
    - База данных: bankdb (банковские данные - изолированная БД)
  
  backend:
    - Порт: 8080
    - Healthcheck: /actuator/health
    - Volumes: ./logs:/app/logs (логи на хосте)
    - Environment: переменные из .env файла
    - Примечание: Bank Module находится внутри backend контейнера
  
  frontend:
    - Порт: 3000
    - Environment: VITE_API_URL
```

---

## 🏗️ Архитектура Middleware

### Уровни обработки запроса

```
HTTP Запрос
    ↓
┌─────────────────────────────────────────┐
│ 1. SERVLET FILTER                       │
│    RequestIdFilter (@Order(1))          │
│    - Генерирует Request ID              │
│    - Логирует входящий запрос           │
│    - Работает для ВСЕХ запросов         │
│      (включая /actuator/*)              │
└─────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────┐
│ 2. TOKEN VALIDATION FILTER              │
│    TokenValidationFilter (@Order(2))    │
│    - Проверяет JWT токен                │
│    - Валидирует токен                   │
│    - Устанавливает SecurityContext      │
│    - Пропускает публичные endpoints     │
│      (/api/auth/login, /actuator/*)     │
└─────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────┐
│ 3. SPRING MVC INTERCEPTOR               │
│    RequestLoggingInterceptor            │
│    - Дополнительное логирование         │
│    - Только для MVC контроллеров        │
└─────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────┐
│ 4. SPRING MVC CONTROLLER                │
│    - Обработка бизнес-логики            │
│    - ResponseWrapperAspect              │
│      (автоматическая обертка ответов)   │
└─────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────┐
│ 5. EXCEPTION HANDLER                    │
│    GlobalExceptionHandler               │
│    - Обрабатывает все исключения        │
│    - Возвращает ApiResponse             │
└─────────────────────────────────────────┘
    ↓
HTTP Ответ
```

### Компоненты

#### 1. RequestIdFilter

**Назначение:** Генерация Request ID и логирование всех запросов

**Что делает:**
- Генерирует уникальный Request ID для каждого запроса
- Сохраняет Request ID в ThreadLocal (RequestContextHolder)
- Записывает контекст в MDC для логирования
- Логирует входящий запрос (метод, URI, IP, User-Agent)
- Логирует завершение запроса (статус, время выполнения)
- Добавляет Request ID в заголовок ответа `X-Request-Id`

**Ключевые особенности:**
- Работает на уровне сервлета (самый ранний уровень)
- Обрабатывает ВСЕ запросы, включая Actuator endpoints
- Использует `@Order(1)` для первого выполнения

**Код:**
```java
@Slf4j
@Order(1)
@Component
public class RequestIdFilter extends OncePerRequestFilter {
    // Генерирует Request ID
    // Логирует запросы
    // Сохраняет контекст
}
```

#### 2. TokenValidationFilter

**Назначение:** Проверка JWT токенов и установка SecurityContext

**Что делает:**
- Извлекает токен из заголовка `Authorization: Bearer <token>`
- Валидирует JWT токен через JwtTokenValidator
- Извлекает login из токена
- Загружает User из БД по login
- Устанавливает SecurityContext с данными пользователя
- Пропускает публичные endpoints (login, health, swagger)

**Ключевые особенности:**
- Работает на уровне сервлета (до контроллеров)
- Использует `@Order(2)` для выполнения после RequestIdFilter
- Пропускает публичные endpoints через `shouldNotFilter()`

**Код:**
```java
@Slf4j
@Order(2)
@Component
public class TokenValidationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) {
        String token = extractToken(request);
        if (token != null && jwtTokenValidator.validateToken(token)) {
            String login = jwtTokenProvider.getLoginFromToken(token);
            User user = userService.findByLogin(login);
            SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
            );
        }
        filterChain.doFilter(request, response);
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/login") ||
               path.startsWith("/actuator") ||
               path.startsWith("/swagger") ||
               path.startsWith("/api-docs");
    }
}
```

#### 3. RequestContextHolder

**Назначение:** Хранение Request ID в ThreadLocal

**Что делает:**
- Хранит Request ID для текущего потока
- Каждый HTTP запрос = отдельный поток
- Автоматическая изоляция между запросами

**Почему ThreadLocal:**
- Изоляция данных между потоками
- Не нужно передавать Request ID через параметры
- Доступен из любого места в коде

**Использование:**
```java
// Получить Request ID
String requestId = RequestContextHolder.getRequestId();

// Установить Request ID
RequestContextHolder.setRequestId("abc123");

// Очистить (важно для предотвращения утечек памяти)
RequestContextHolder.clear();
```

#### 4. LoggingHelper (MDC)

**Назначение:** Структурированное логирование через MDC

**Что такое MDC:**
- Mapped Diagnostic Context
- Добавляет поля в каждый лог-запись автоматически
- Все логи в одном потоке содержат одинаковый контекст

**Что сохраняется в MDC:**
- `requestId` - уникальный ID запроса
- `http.method` - HTTP метод (GET, POST, etc.)
- `http.uri` - URI запроса
- `http.ip` - IP адрес клиента
- `userId` - ID пользователя (опционально)

**Пример лога с MDC:**
```
2025-11-02 16:31:55.664 INFO [requestId=abc123, http.method=GET, http.uri=/api/balances, http.ip=172.18.0.1] Входящий запрос: GET /api/balances
```

**Преимущества:**
- Легко фильтровать логи по `requestId`
- Найти все логи одного запроса
- Структурированные логи для анализа

#### 5. RequestLoggingInterceptor

**Назначение:** Дополнительное логирование на уровне Spring MVC

**Особенности:**
- Работает только для MVC контроллеров
- НЕ работает для Actuator endpoints
- Выполняется ДО и ПОСЛЕ контроллера

#### 6. ResponseWrapperAspect

**Назначение:** Автоматическая обертка ответов в ApiResponse

**Что делает:**
- Перехватывает ответы контроллеров
- Автоматически оборачивает в `ApiResponse<T>`
- Если уже `ApiResponse` - возвращает как есть

**Пример:**
```java
// Контроллер возвращает:
return user;

// AOP оборачивает в:
ApiResponse.success(user, requestId)
```

#### 7. GlobalExceptionHandler

**Назначение:** Централизованная обработка всех исключений

**Что обрабатывает:**
- `FinPulsException` и все наследники
- `ValidationException` - ошибки валидации
- `MethodArgumentNotValidException` - ошибки @Valid
- `ConstraintViolationException` - ошибки валидации
- `TokenExpiredException` - истекший токен
- `TokenInvalidException` - невалидный токен
- `InvalidCredentialsException` - неверные credentials
- `Exception` - все остальные исключения

**Формат ответа:**
```json
{
  "status": "error",
  "message": "Операция завершилась с ошибкой",
  "error": {
    "code": "TOKEN_EXPIRED",
    "message": "Token expired",
    "context": {
      "method": "GET",
      "uri": "/api/balances"
    }
  },
  "requestId": "abc123"
}
```

---

## 🔐 Система авторизации

### Процесс авторизации

#### 1. Логин

```bash
POST /api/auth/login
Content-Type: application/json

{
  "login": "user123",
  "password": "password123"
}
```

**Процесс:**
1. Backend получает login и password
2. Находит UserCredentials по login в PostgreSQL
3. Хэширует входящий password через Java Crypto (BCrypt)
4. Сравнивает хэш с сохраненным password
5. Если совпадает - генерирует JWT access token и refresh token
6. Сохраняет refresh token в H2
7. Возвращает access token и refresh token клиенту

**Ответ:**
```json
{
  "status": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "refresh_token_here",
    "expiresIn": 3600
  },
  "message": "Login successful",
  "requestId": "abc123"
}
```

#### 2. Использование токена

```bash
GET /api/balances
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Процесс:**
1. TokenValidationFilter извлекает токен из заголовка
2. Валидирует JWT токен через JwtTokenValidator
3. Извлекает login из токена
4. Загружает User из БД по login
5. Устанавливает SecurityContext
6. Контроллер получает доступ к User через SecurityContext

#### 3. Обновление токена

```bash
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "refresh_token_here"
}
```

**Процесс:**
1. Backend получает refresh token
2. Проверяет refresh token в H2
3. Проверяет срок действия refresh token
4. Генерирует новый access token
5. Возвращает новый access token

**Ответ:**
```json
{
  "status": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 3600
  },
  "message": "Token refreshed successfully",
  "requestId": "abc123"
}
```

#### 4. Выход (Logout)

```bash
POST /api/auth/logout
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Процесс:**
1. Backend получает refresh token из БД по user_id
2. Удаляет refresh token из H2
3. Access token становится невалидным (нельзя обновить)

**Ответ:**
```json
{
  "status": "success",
  "data": null,
  "message": "Logout successful",
  "requestId": "abc123"
}
```

### Хэширование паролей

**Использование Java Crypto:**
```java
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordEncoder {
    private static final String ALGORITHM = "SHA-256";
    private static final SecureRandom random = new SecureRandom();
    
    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] salt = generateSalt();
            digest.update(salt);
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(salt) + ":" + 
                   Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    public boolean verifyPassword(String password, String hash) {
        // Проверка пароля
    }
    
    private byte[] generateSalt() {
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }
}
```

**Альтернатива: BCrypt (рекомендуется)**
```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hashedPassword = encoder.encode(password);
boolean matches = encoder.matches(password, hashedPassword);
```

### JWT Token Provider

**Генерация токена:**
```java
public String generateAccessToken(User user) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtExpiration);
    
    return Jwts.builder()
        .setSubject(user.getCredentials().getLogin())
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();
}
```

**Валидация токена:**
```java
public boolean validateToken(String token) {
    try {
        Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
        return true;
    } catch (JwtException | IllegalArgumentException e) {
        return false;
    }
}
```

---

## 📝 Система логирования

### Конфигурация

**Файл:** `application-logging.properties`

```properties
# Общий уровень логирования
logging.level.root=INFO

# Логи приложения - DEBUG для разработки
logging.level.com.finpuls=DEBUG

# SQL запросы - DEBUG для отладки
logging.level.org.hibernate.SQL=DEBUG

# Технические библиотеки - WARN (только ошибки)
logging.level.org.springframework=WARN
logging.level.org.apache.tomcat=WARN
```

### Формат логов

#### Входящий запрос

```
2025-11-02 16:31:55.664 INFO [requestId=abc123] Входящий запрос: GET /api/balances - IP: 172.18.0.1 - User-Agent: curl/7.68.0
```

#### Завершение запроса

```
2025-11-02 16:31:55.679 INFO [requestId=abc123] Запрос завершен: GET /api/balances - Статус: 200 - Время выполнения: 15ms - IP: 172.18.0.1
```

#### Ошибка

```
2025-11-02 16:31:55.680 ERROR [requestId=abc123] Ошибка обработки запроса: POST /api/auth/login - Время выполнения: 50ms - IP: 172.18.0.1
com.finpuls.api.exception.InvalidCredentialsException: Invalid login or password
```

### Логи в файл

**Путь:** `logs/finpuls.log` (Backend), `logs/bank-service.log` (Bank Service)

**Ротация:**
- Максимальный размер файла: 10MB
- Количество файлов истории: 14
- Формат: `finpuls.log`, `finpuls.log.1`, `finpuls.log.2`, etc.

### MDC (Mapped Diagnostic Context)

**Автоматические поля в логах:**

| Поле | Описание | Пример |
|------|----------|--------|
| `requestId` | Уникальный ID запроса | `abc123def456` |
| `http.method` | HTTP метод | `GET`, `POST` |
| `http.uri` | URI запроса | `/api/balances` |
| `http.ip` | IP адрес клиента | `172.18.0.1` |
| `userId` | ID пользователя | `user123` (опционально) |

**Использование:**
```java
// Автоматически устанавливается в RequestIdFilter
LoggingHelper.setRequestId(requestId);
LoggingHelper.setHttpContext(request);

// Установить User ID (например, после аутентификации)
LoggingHelper.setUserId(userId);

// Очистить после обработки запроса
LoggingHelper.clear();
```

### Фильтрация логов

**По Request ID:**
```bash
docker logs finpuls-backend | grep "requestId=abc123"
```

**По методу:**
```bash
docker logs finpuls-backend | grep "GET /api"
```

**По статусу:**
```bash
docker logs finpuls-backend | grep "Статус: 500"
```

---

## 🔄 Обработка запросов

### Полный поток обработки

#### Пример: `GET /api/balances` (с токеном)

```
1. [RequestIdFilter] doFilterInternal() входит
   ├─ Генерирует Request ID: "abc123"
   ├─ Сохраняет в ThreadLocal
   ├─ Записывает в MDC (requestId, http.method, http.uri, http.ip)
   ├─ Извлекает IP: "172.18.0.1"
   ├─ Извлекает User-Agent: "curl/7.68.0"
   ├─ Логирует: "Входящий запрос: GET /api/balances - IP: 172.18.0.1 - User-Agent: curl/7.68.0"
   └─ Вызывает filterChain.doFilter()

2. [TokenValidationFilter] doFilterInternal() входит
   ├─ Извлекает токен из заголовка: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
   ├─ Валидирует токен через JwtTokenValidator
   ├─ Извлекает login: "user123"
   ├─ Загружает User из БД по login
   ├─ Устанавливает SecurityContext
   └─ Вызывает filterChain.doFilter()

3. [RequestLoggingInterceptor] preHandle() - Дополнительное логирование

4. [BalanceController] getBalances()
   ├─ Получает User из SecurityContext
   ├─ Вызывает BankService.getBalances()
   └─ Возвращает данные

5. [ResponseWrapperAspect] - Обертка ответа в ApiResponse

6. [RequestLoggingInterceptor] afterCompletion() - Логирование завершения

7. [RequestIdFilter] doFilterInternal() продолжается
   ├─ Засекает время: 15ms
   ├─ Логирует: "Запрос завершен: GET /api/balances - Статус: 200 - Время выполнения: 15ms - IP: 172.18.0.1"
   └─ finally: очищает ThreadLocal и MDC

8. Ответ отправляется клиенту
   ├─ Headers: X-Request-Id: abc123
   └─ Body: ApiResponse с данными
```

#### Пример: `POST /api/auth/login` (публичный endpoint)

```
1. [RequestIdFilter] - Генерация Request ID, логирование входа
2. [TokenValidationFilter] - Пропускает (публичный endpoint)
3. [RequestLoggingInterceptor] - Дополнительное логирование
4. [AuthController] - Обработка логина, генерация токенов
5. [ResponseWrapperAspect] - Обертка ответа
6. [RequestLoggingInterceptor] - Логирование завершения
7. [RequestIdFilter] - Логирование завершения, очистка контекста
```

### Обработка исключений

```
1. Исключение выбрасывается в контроллере/сервисе
2. [GlobalExceptionHandler] перехватывает исключение
3. Определяет тип исключения
4. Создает ApiResponse с ошибкой
5. Возвращает правильный HTTP статус:
   - 400 (BAD_REQUEST) - ValidationException
   - 401 (UNAUTHORIZED) - TokenExpiredException, TokenInvalidException, InvalidCredentialsException
   - 404 (NOT_FOUND) - UserNotFoundException
   - 502 (BAD_GATEWAY) - BankServiceException
   - 500 (INTERNAL_SERVER_ERROR) - все остальные
```

---

## 🌍 Переменные окружения

### Файл .env

Создайте файл `.env` в корне проекта (на основе `example.env`):

```bash
# Spring Profile
SPRING_PROFILES_ACTIVE=dev

# Logging Level
LOG_LEVEL=INFO

# Приложение
SERVER_PORT=8080

# Database - PostgreSQL (основная БД)
DB_PG_URL=jdbc:postgresql://postgres:5432/finpuls
DB_PG_DBNAME=finpuls
DB_PG_USERNAME=finpuls
DB_PG_PASSWORD=your_password_here
DB_PG_DRIVER=org.postgresql.Driver
DB_PG_PLATFORM=org.hibernate.dialect.PostgreSQLDialect

# Database - PostgreSQL (банковский модуль)
DB_BANK_PG_URL=jdbc:postgresql://postgres-bank:5432/bankdb
DB_BANK_PG_DBNAME=bankdb
DB_BANK_PG_USERNAME=bank
DB_BANK_PG_PASSWORD=your_bank_password_here
DB_BANK_PG_DRIVER=org.postgresql.Driver
DB_BANK_PG_PLATFORM=org.hibernate.dialect.PostgreSQLDialect

# Database - H2 (токены, in-memory)
DB_H2_URL=jdbc:h2:mem:banktokens;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
DB_H2_DBNAME=bank_tokens
DB_H2_USERNAME=sa
DB_H2_PASSWORD=
DB_H2_DRIVER=org.h2.Driver
DB_H2_PLATFORM=org.hibernate.dialect.H2Dialect

# JWT
JWT_SECRET=your-secret-key-here-min-256-bits
JWT_EXPIRATION=3600
JWT_REFRESH_EXPIRATION=86400

# Bank API base URLs
BANK_VBANK_BASE_URL=https://vbank.open.bankingapi.ru
BANK_ABANK_BASE_URL=https://abank.open.bankingapi.ru
BANK_SBANK_BASE_URL=https://sbank.open.bankingapi.ru

# CORS (опционально)
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173
```

### Значения по умолчанию

Если переменная не указана в `.env`, используются значения из `docker-compose.yml` и `application.properties`:

**Приложение:**
- `SPRING_PROFILES_ACTIVE`: `dev`
- `LOG_LEVEL`: `INFO`
- `SERVER_PORT`: `8080`

**Основная БД (PostgreSQL):**
- `DB_PG_DBNAME`: `finpuls`
- `DB_PG_USERNAME`: `finpuls`
- `DB_PG_PASSWORD`: `finpuls` (в production измените!)

**Банковская БД (PostgreSQL):**
- `DB_BANK_PG_DBNAME`: `bankdb`
- `DB_BANK_PG_USERNAME`: `bank`
- `DB_BANK_PG_PASSWORD`: `bank` (в production измените!)
- `DB_BANK_PG_URL`: `jdbc:postgresql://postgres-bank:5432/bankdb`

**H2 (токены):**
- `DB_H2_USERNAME`: `sa`
- `DB_H2_PASSWORD`: (пусто)

**JWT:**
- `JWT_EXPIRATION`: `3600` (1 час)
- `JWT_REFRESH_EXPIRATION`: `86400` (24 часа)

**Bank API:**
- `BANK_VBANK_BASE_URL`: `https://vbank.open.bankingapi.ru`
- `BANK_ABANK_BASE_URL`: `https://abank.open.bankingapi.ru`
- `BANK_SBANK_BASE_URL`: `https://sbank.open.bankingapi.ru`

**CORS:**
- По умолчанию: `http://localhost:3000,http://localhost:5173`

---

## 🛠️ Полезные команды

### Docker

```bash
# Запуск
docker compose up -d

# С пересборкой
docker compose up --build -d

# Остановка
docker compose down

# Перезапуск
docker compose restart backend
docker compose restart bank-service

# Логи в реальном времени
docker logs -f finpuls-backend
docker logs -f finpuls-bank-service

# Последние 100 строк логов
docker logs --tail 100 finpuls-backend

# Логи с фильтром
docker logs finpuls-backend | grep "ERROR"
docker logs finpuls-backend | grep "requestId=abc123"

# Зайти в контейнер
docker exec -it finpuls-backend bash
docker exec -it finpuls-bank-service bash

# Проверить здоровье
docker compose ps
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
```

### Maven (локально, без Docker)

```bash
# Сборка Backend
cd backend
mvn clean package

# Запуск Backend
mvn spring-boot:run

# Сборка Bank Service
cd bank-service
mvn clean package

# Запуск Bank Service
mvn spring-boot:run

# Тесты
mvn test

# Проверка зависимостей
mvn dependency:tree
```

### API Endpoints

```bash
# Health check Backend
curl http://localhost:8080/actuator/health

# Bank Module endpoints (через Backend)
curl http://localhost:8080/api/bank/balances
curl http://localhost:8080/api/bank/cards
curl http://localhost:8080/api/bank/accounts

# Metrics
curl http://localhost:8080/actuator/metrics

# Prometheus metrics
curl http://localhost:8080/actuator/prometheus

# Swagger UI Backend (включает Bank Module endpoints)
open http://localhost:8080/swagger-ui.html

# API Docs
curl http://localhost:8080/api-docs
```

### Работа с логами

```bash
# Просмотр логов из файла
tail -f logs/combined.log         # Все логи Backend
tail -f logs/error.log            # Только ошибки Backend
tail -f logs/bank-combined.log    # Все логи Bank Module
tail -f logs/bank-error.log       # Только ошибки Bank Module

# Поиск ошибок
grep "ERROR" logs/combined.log
# Или смотреть только файл с ошибками
cat logs/error.log

# Поиск по Request ID
grep "requestId=abc123" logs/combined.log

# Подсчет запросов
grep "Входящий запрос" logs/combined.log | wc -l

# Запросы с ошибками
grep "Статус: [45]" logs/combined.log
# Или смотреть только файл с ошибками
cat logs/error.log
```

---

## 🔍 Отладка

### Проверка Request ID

1. Сделайте запрос с заголовком:
```bash
curl -H "X-Request-Id: my-custom-id" http://localhost:8080/actuator/health
```

2. В логах будет использован ваш Request ID:
```
[requestId=my-custom-id] Входящий запрос: GET /actuator/health
```

### Проверка логирования

1. Установите `LOG_LEVEL=DEBUG` в `.env`
2. Перезапустите контейнер
3. Все DEBUG логи будут видны

### Проверка MDC

В логах каждый запрос содержит MDC поля:
```
[requestId=abc123, http.method=GET, http.uri=/api/balances, http.ip=172.18.0.1]
```

### Проверка авторизации

1. Зарегистрируйте пользователя (или создайте в БД)
2. Выполните логин:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"user123","password":"password123"}'
```

3. Используйте полученный токен:
```bash
curl -H "Authorization: Bearer <token>" http://localhost:8080/api/balances
```

---

## 📚 Дополнительные ресурсы

- [ARCHITECTURE.md](../ARCHITECTURE.md) - Архитектура приложения
- [PLAN.md](../PLAN.md) - План разработки
- [Java Development Guide](./java-development.md) - Руководство по разработке на Java
- [Backend Module](../modules/backend.md) - Описание Backend модуля
- [Bank Module](../modules/bank-module.md) - Описание Bank Module
- [Backend Entities](../entities/backend.md) - Сущности Backend модуля
- [Bank Module Entities](../entities/bank-module.md) - Сущности Bank Module
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT Documentation](https://jwt.io/)
- [SLF4J Documentation](http://www.slf4j.org/manual.html)
- [MDC Documentation](http://www.slf4j.org/api/org/slf4j/MDC.html)

---

## ❓ FAQ

### Почему не видно логов для Actuator endpoints?

Используйте `RequestIdFilter` (уровень фильтра), а не `RequestLoggingInterceptor` (уровень MVC). `RequestIdFilter` обрабатывает все запросы.

### Как найти все логи одного запроса?

Используйте Request ID:
```bash
docker logs finpuls-backend | grep "requestId=abc123"
```

### Где хранятся логи?

1. **В контейнере Backend:**
   - `/app/logs/combined.log` — все логи
   - `/app/logs/error.log` — только ошибки
2. **В контейнере Bank Module:**
   - `/app/logs/bank-combined.log` — все логи
   - `/app/logs/bank-error.log` — только ошибки
3. **На хосте:** `./logs/*.log` (через volume)

### Как изменить уровень логирования?

Измените `LOG_LEVEL` в `.env`:
```bash
LOG_LEVEL=DEBUG  # для детальных логов
LOG_LEVEL=WARN   # только предупреждения и ошибки
```

### Как работает авторизация?

1. Клиент отправляет логин/пароль на `/api/auth/login`
2. Backend проверяет credentials и генерирует JWT токены
3. Клиент использует access token в заголовке `Authorization: Bearer <token>`
4. TokenValidationFilter проверяет токен и устанавливает SecurityContext
5. Контроллер получает доступ к User через SecurityContext

### Как обновить токен?

Отправьте refresh token на `/api/auth/refresh`:
```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"refresh_token_here"}'
```

---

**Последнее обновление:** 2025-01-20
