# Архитектура FinPuls - Финансовый Пульсометр

## 📋 Содержание
1. [Общее описание](#общее-описание)
2. [Технологический стек](#технологический-стек)
3. [Архитектура Backend](#архитектура-backend)
4. [Архитектура Frontend](#архитектура-frontend)
5. [Структура проекта](#структура-проекта)
6. [Компоненты системы](#компоненты-системы)
7. [Паттерны проектирования](#паттерны-проектирования)
8. [API Формат ответов](#api-формат-ответов)
9. [База данных](#база-данных)
10. [Docker и развертывание](#docker-и-развертывание)

---

## 🎯 Общее описание

**FinPuls** — мультибанковское приложение для управления личными финансами с проактивным мониторингом финансового здоровья.

### Ключевые особенности:
- **Единый дэшборд** для всех банковских счетов
- **"Сердцебиение финансов"** — индикатор ликвидности в реальном времени
- **Агрегация данных** из нескольких банков
- **Монетизация**: Free версия + Premium подписка (Pro)

---

## 🛠 Технологический стек

### Backend
- **Java 17+**
- **Spring Boot 3.x**
- **Spring Data JPA** — работа с БД
- **H2 Database** — in-memory БД только для хранения токенов доступа к банковским API
- **Spring Security** — безопасность (для будущего OAuth)
- **Spring AOP** — аспекты для обертки ответов
- **MapStruct** — маппинг Entity ↔ DTO
- **Jakarta Bean Validation** — валидация запросов
- **OpenFeign** — HTTP клиенты для банковских API
- **SpringDoc OpenAPI** — Swagger документация
- **Micrometer + Prometheus** — метрики
- **SLF4J + Logback** — логирование

### Frontend
- **React 18+** с TypeScript
- **Vite** — сборка
- **Axios** — HTTP клиент
- **React Query / SWR** — кэширование и синхронизация данных
- **Recharts / Chart.js** — графики и визуализация
- **Tailwind CSS** — стилизация (или Material-UI)

### Инфраструктура
- **Docker** + **Docker Compose**
- **Maven** — сборка backend
- **Prometheus** — сбор метрик (опционально)
- **Nginx** — reverse proxy (опционально)

---

## 🏗 Архитектура Backend

### Структура пакетов

```
com.finpuls/
├── config/                      # Конфигурация
│   ├── JpaConfig.java
│   ├── SwaggerConfig.java
│   ├── PrometheusConfig.java
│   ├── WebMvcConfig.java        # Интерцепторы
│   └── BankProperties.java      # @ConfigurationProperties для банков
│
├── domain/                      # Доменная модель
│   ├── model/                   # JPA Entities
│   │   └── BankToken.java       # Хранит только токены доступа к банковским API
│   ├── repository/              # JPA Repositories
│   │   └── BankTokenRepository.java
│   └── dto/                     # Data Transfer Objects
│       ├── request/
│       │   ├── ConnectBankRequest.java
│       │   └── GetTransactionsRequest.java
│       ├── response/
│       │   ├── BalanceDTO.java (interface)
│       │   ├── TransactionDTO.java (interface)
│       │   ├── FinancialHealthDTO.java (interface)
│       │   ├── BalanceDTOImpl.java
│       │   └── TransactionDTOImpl.java
│       └── internal/
│           └── BankBalanceResponse.java
│
├── service/                     # Бизнес-логика
│   ├── token/
│   │   ├── ITokenService.java  # Интерфейс
│   │   └── TokenService.java
│   ├── bank/
│   │   ├── BankAdapter.java    # Интерфейс адаптера
│   │   ├── BankAdapterFactory.java
│   │   ├── adapter/
│   │   │   ├── VTBAdapter.java
│   │   │   ├── SberAdapter.java
│   │   │   └── TinkoffAdapter.java
│   │   └── BankConnectionService.java
│   ├── aggregation/
│   │   ├── IBankAggregationService.java
│   │   └── BankAggregationFacade.java
│   ├── analytics/
│   │   ├── IFinancialHealthService.java
│   │   ├── FinancialHealthService.java
│   │   └── strategy/
│   │       ├── HealthCalculationStrategy.java
│   │       ├── BasicHealthStrategy.java
│   │       └── AdvancedHealthStrategy.java
│   ├── subscription/
│   │   ├── ISubscriptionService.java
│   │   └── SubscriptionService.java
│   └── ai/
│       ├── IAIService.java
│       └── GigaChatService.java
│
├── api/                         # REST API
│   ├── controller/
│   │   ├── BankController.java
│   │   ├── BalanceController.java
│   │   ├── TransactionController.java
│   │   ├── FinancialHealthController.java
│   │   └── AnalyticsController.java
│   └── response/
│       ├── IApiResponse.java    # Интерфейс единого формата
│       ├── ApiResponse.java     # Реализация
│       ├── ResponseStatus.java  # Enum (SUCCESS/ERROR)
│       └── ErrorDetails.java    # Детали ошибок
│
├── api/exception/               # Обработка ошибок
│   ├── FinPulsException.java    # Базовое исключение
│   ├── BankException.java
│   │   ├── BankApiException.java
│   │   ├── BankConnectionException.java
│   │   └── TokenExpiredException.java
│   ├── ValidationException.java
│   ├── BusinessException.java
│   │   ├── SubscriptionRequiredException.java
│   │   └── BankNotConnectedException.java
│   ├── GlobalExceptionHandler.java
│   └── ValidationExceptionHandler.java
│
├── middleware/                  # Middleware
│   ├── RequestIdFilter.java
│   ├── RequestLoggingInterceptor.java
│   ├── RequestResponseLoggingFilter.java
│   ├── CachedBodyHttpServletRequest.java
│   └── CachedBodyHttpServletResponse.java
│
├── mapper/                      # MapStruct мапперы
│   ├── BankTokenMapper.java
│   ├── TransactionMapper.java
│   └── BalanceMapper.java
│
├── validation/                  # Кастомные валидаторы
│   ├── DateRange.java
│   └── DateRangeValidator.java
│
├── common/                      # Утилиты
│   ├── RequestContextHolder.java
│   └── LoggingHelper.java       # Утилита для работы с MDC
│
└── metrics/                     # Prometheus метрики
    └── BankMetrics.java
```

### Слои приложения

```
┌─────────────────────────────────────────────────┐
│          REST API Controllers                   │  ← API Layer
│  (BankController, BalanceController, etc.)      │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│              Service Layer                      │  ← Business Logic
│  (TokenService, AggregationService, etc.)       │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│          Repository Layer (JPA)                 │  ← Data Access
│  (BankTokenRepository)                          │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│              H2 Database                        │  ← Database
└─────────────────────────────────────────────────┘
```

### Компоненты Backend

#### 1. **Bank Adapter Pattern**
Интерфейс `BankAdapter` для единообразной работы с разными банками:
```java
public interface BankAdapter {
    String getBankName();
    BankBalanceResponse getBalances(String accessToken);
    BankTransactionsResponse getTransactions(String accessToken, LocalDate from, LocalDate to);
    TokenRefreshResponse refreshToken(String refreshToken);
}
```

#### 2. **Token Management**
- Хранение токенов в H2 (in-memory)
- Формат токена: `{access_token, token_type, client_id, expires_in}`
- Проверка срока действия токена
- Все данные (балансы, транзакции) получаются в реальном времени через API запросы к банкам

#### 3. **Financial Health Calculation**
- Расчет метрики "сердцебиения финансов"
- Стратегии: Basic (Free) и Advanced (Pro)
- Учитывает: балансы, транзакции, предстоящие платежи

#### 4. **Subscription Management**
- Проверка подписки (Free/Pro)
- Feature flags для ограничения функционала
- AOP для автоматической проверки доступа

---

## 🎨 Архитектура Frontend

### Структура папок

```
frontend/
├── public/
│   └── index.html
│
├── src/
│   ├── components/              # React компоненты
│   │   ├── common/              # Переиспользуемые
│   │   │   ├── Header.tsx
│   │   │   ├── Footer.tsx
│   │   │   └── LoadingSpinner.tsx
│   │   ├── Dashboard/           # Главная страница
│   │   │   ├── Dashboard.tsx
│   │   │   ├── SummaryCard.tsx
│   │   │   └── QuickActions.tsx
│   │   ├── FinancialHeartbeat/  # Виджет "сердцебиение"
│   │   │   ├── FinancialHeartbeat.tsx
│   │   │   ├── HeartbeatIndicator.tsx
│   │   │   └── HealthMetrics.tsx
│   │   ├── Banks/               # Управление банками
│   │   │   ├── BankList.tsx
│   │   │   ├── BankCard.tsx
│   │   │   └── ConnectBankModal.tsx
│   │   ├── Transactions/        # Транзакции
│   │   │   ├── TransactionList.tsx
│   │   │   ├── TransactionItem.tsx
│   │   │   ├── TransactionFilters.tsx
│   │   │   └── TransactionChart.tsx
│   │   ├── Balances/            # Балансы
│   │   │   ├── BalanceList.tsx
│   │   │   └── BalanceCard.tsx
│   │   └── Analytics/           # Аналитика (Pro)
│   │       ├── Analytics.tsx
│   │       ├── TrendsChart.tsx
│   │       └── InsightsPanel.tsx
│   │
│   ├── pages/                   # Страницы
│   │   ├── HomePage.tsx
│   │   ├── BanksPage.tsx
│   │   ├── TransactionsPage.tsx
│   │   └── AnalyticsPage.tsx
│   │
│   ├── services/                # API клиенты
│   │   ├── api.ts               # Axios instance
│   │   ├── bankService.ts
│   │   ├── balanceService.ts
│   │   ├── transactionService.ts
│   │   └── healthService.ts
│   │
│   ├── hooks/                   # Custom hooks
│   │   ├── useBanks.ts
│   │   ├── useBalances.ts
│   │   ├── useTransactions.ts
│   │   └── useFinancialHealth.ts
│   │
│   ├── store/                   # State management
│   │   ├── userStore.ts         # Zustand / Redux
│   │   └── subscriptionStore.ts
│   │
│   ├── types/                   # TypeScript типы
│   │   ├── api.ts               # Типы для API ответов
│   │   ├── bank.ts
│   │   ├── transaction.ts
│   │   └── user.ts
│   │
│   ├── utils/                   # Утилиты
│   │   ├── formatters.ts        # Форматирование дат, денег
│   │   ├── validators.ts        # Валидация форм
│   │   └── constants.ts         # Константы
│   │
│   ├── App.tsx                  # Главный компонент
│   └── main.tsx                 # Entry point
│
├── package.json
├── tsconfig.json
├── vite.config.ts
└── tailwind.config.js
```

### Компоненты Frontend

#### 1. **Dashboard (Главная страница)**
- Виджет "Сердцебиение финансов" (зеленый/желтый/красный)
- Общий баланс всех счетов
- Быстрые действия (подключить банк, посмотреть транзакции)
- Последние транзакции

#### 2. **Financial Heartbeat Widget**
- Индикатор здоровья финансов
- Метрики: ликвидность, доходы/расходы
- Предупреждения о кассовых разрывах

#### 3. **Bank Management**
- Список подключенных банков
- Подключение нового банка
- Отключение банка

#### 4. **Transactions**
- Список транзакций из всех банков
- Фильтрация по дате, категории, банку
- Группировка по дням/неделям/месяцам
- График расходов (Pro)

#### 5. **Analytics (Pro only)**
- Глубокая аналитика
- Тренды доходов/расходов
- Инвестиционный портфель
- PDF отчеты

---

## 📁 Структура проекта

```
FinPuls/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/finpuls/
│   │   │   │   └── [структура пакетов выше]
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       ├── application-dev.properties
│   │   │       ├── application-prod.properties
│   │   │       ├── logback-spring.xml
│   │   │       └── db/migration/  # Liquibase (опционально)
│   │   └── test/
│   ├── pom.xml
│   └── Dockerfile
│
├── frontend/
│   ├── src/
│   │   └── [структура выше]
│   ├── public/
│   ├── package.json
│   ├── tsconfig.json
│   ├── vite.config.ts
│   └── Dockerfile
│
├── docker-compose.yml
├── .env.example
├── ARCHITECTURE.md
├── PLAN.md
└── README.md
```

---

## 🔧 Компоненты системы

### 1. **Единый формат ответов API**

Все ответы оборачиваются в `IApiResponse<T>`:

```typescript
interface IApiResponse<T> {
  status: "success" | "error";
  data: T;
  message: string;
  error?: {
    code: string;
    message: string;
    context?: Record<string, any>;
  };
  requestId: string;
}
```

### 2. **Request ID**

Каждый запрос получает уникальный `requestId`:
- Генерируется автоматически или из заголовка `X-Request-Id`
- Возвращается в ответе
- Используется в логах для трассировки

### 3. **Валидация**

- Jakarta Bean Validation для всех DTO
- Кастомные валидаторы для сложных сценариев
- Автоматическая обработка ошибок валидации

### 4. **Логирование**

#### Конфигурация
- Уровень логирования настраивается через `logging.level.root` в `application.properties` (по умолчанию INFO)
- Поддерживаемые уровни: **INFO**, **DEBUG**, **WARN**
- Ротация файлов: автоматическое удаление логов старше **2 недель** (14 дней)
- Раздельные логи для банковских API в отдельном файле
- Уровень логирования можно переопределить через переменную окружения `LOG_LEVEL`

#### Структура JSON лога
Все логи пишутся в JSON формате с единой структурой:

```json
{
  "level": "info|debug|warn",
  "message": "Log message text",
  "timestamp": "2025-01-20T10:30:45.123+0300",
  "data": {
    "http": {
      "ip": "192.168.1.1",
      "url": "/api/balances",
      "userAgent": "Mozilla/5.0..."
    },
    "source": {
      "file": "com.finpuls.service.BankService",
      "function": "getBalances",
      "line": 45
    },
    "data": {
      "userId": "user123",
      "bankName": "VTB"
    }
  },
  "error": {
    "code": "TOKEN_EXPIRED",
    "context": {
      "bankName": "VTB",
      "userId": "user123"
    },
    "message": "Token expired for bank: VTB",
    "stackTrace": "..."
  }
}
```

**Примечания:**
- Поле `error` присутствует только при логировании ошибок
- Поле `error.context` заполняется только в DEBUG режиме
- Поле `data.data` содержит кастомные данные конкретного лога
- HTTP контекст (`data.http`) заполняется автоматически через MDC в интерцепторах
- Source информация (`data.source`) заполняется автоматически Logback

#### Файлы логов
- `logs/finpuls.log` — основные логи приложения
- `logs/bank-api.log` — логи банковских API
- Ротация: `logs/finpuls.2025-01-20.log`, `logs/bank-api.2025-01-20.log`
- Хранение: 14 дней, после чего автоматическое удаление

#### Использование
```java
// В RequestLoggingInterceptor
LoggingHelper.setHttpContext(request);

// При логировании с кастомными данными
Map<String, Object> logData = new HashMap<>();
logData.put("userId", userId);
logData.put("bankName", bankName);
LoggingHelper.setData(logData);
log.info("Requesting balances");

// При логировании ошибки
LoggingHelper.setError("TOKEN_EXPIRED", "Token expired", context, isDebugMode);
log.error("Failed to get balances");
```

### 5. **Метрики Prometheus**

- Количество запросов к банковским API
- Время ответа банковских API
- Количество ошибок
- Метрики по типам подписок

---

## 🎯 Паттерны проектирования

### 1. **Repository Pattern**
JPA Repositories для абстракции доступа к данным

### 2. **Service Layer Pattern**
Бизнес-логика в сервисах, контроллеры тонкие

### 3. **Adapter Pattern**
Единообразная работа с разными банковскими API

### 4. **Factory Pattern**
`BankAdapterFactory` для получения нужного адаптера

### 5. **Strategy Pattern**
Разные стратегии расчета финансового здоровья (Basic/Advanced)

### 6. **Facade Pattern**
`BankAggregationFacade` для упрощения сложных операций

### 7. **DTO Pattern**
Отделение Entity от API, использование интерфейсов для типизации

### 8. **AOP Pattern**
Автоматическая обертка ответов, проверка подписок

---

## 📊 API Формат ответов

### Успешный ответ

```json
{
  "status": "success",
  "data": {
    "userId": "user123",
    "bankName": "VTB",
    "accessToken": "encrypted..."
  },
  "message": "Bank connected successfully",
  "error": null,
  "requestId": "a1b2c3d4e5f6g7h8"
}
```

### Ошибка

```json
{
  "status": "error",
  "data": null,
  "message": "Operation failed",
  "error": {
    "code": "TOKEN_EXPIRED",
    "message": "Token expired for bank: VTB",
    "context": {
      "method": "GET",
      "uri": "/api/balances",
      "bankName": "VTB"
    }
  },
  "requestId": "a1b2c3d4e5f6g7h8"
}
```

### Ошибка валидации

```json
{
  "status": "error",
  "data": null,
  "message": "Operation failed",
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Validation failed",
    "context": {
      "validationErrors": {
        "userId": "User ID is required",
        "bankName": "Bank must be VTB, SBER or TINKOFF"
      },
      "method": "POST",
      "uri": "/api/banks/connect"
    }
  },
  "requestId": "a1b2c3d4e5f6g7h8"
}
```

---

## 💾 База данных

### H2 In-Memory Database

**Назначение**: Хранит только токены доступа к банковским API.  
**Все остальные данные** (балансы, транзакции) получаются напрямую через API запросы к банкам.

**Таблица: bank_tokens**
```sql
CREATE TABLE bank_tokens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(100) NOT NULL,
    access_token VARCHAR(500) NOT NULL,
    token_type VARCHAR(20) NOT NULL DEFAULT 'bearer',
    client_id VARCHAR(100) NOT NULL,
    expires_in INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user (user_id)
);
```

**Примечания:**
- `user_id` - идентификатор пользователя из внешней системы
- `access_token` - токен доступа, полученный из банковского API
- `token_type` - тип токена (обычно "bearer")
- `client_id` - ID клиента (например, "team200")
- `expires_in` - время жизни токена в секундах
- Балансы и транзакции **не хранятся** в БД, получаются в реальном времени через API

---

## ⚙️ Конфигурация

### application.properties

```properties
# Server
server.port=8080

# Database (H2)
spring.datasource.url=jdbc:h2:mem:finpulsdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Logging
logging.level.root=${LOG_LEVEL:INFO}
logging.level.com.finpuls=DEBUG
logging.level.com.finpuls.service.bank=INFO
logging.file.name=logs/finpuls.log

# Banks Configuration
bank.vtb.base-url=https://api-vtb.ru
bank.vtb.name=VTB

bank.sber.base-url=https://api-sber.ru
bank.sber.name=SBER

bank.tinkoff.base-url=https://api-tinkoff.ru
bank.tinkoff.name=TINKOFF

# Swagger
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html

# Actuator (Prometheus)
management.endpoints.web.exposure.include=health,info,prometheus
management.endpoint.health.show-details=always
management.metrics.export.prometheus.enabled=true
```

### Переменные окружения

- `LOG_LEVEL` — уровень логирования (INFO, DEBUG, WARN). По умолчанию: INFO
- `SPRING_PROFILES_ACTIVE` — активный профиль (dev, prod)
- `VTB_ACCESS_TOKEN` — токен для VTB API
- `SBER_ACCESS_TOKEN` — токен для SBER API
- `TINKOFF_ACCESS_TOKEN` — токен для Tinkoff API

---

## 🐳 Docker и развертывание

### docker-compose.yml

```yaml
version: '3.8'

services:
  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - LOG_LEVEL=${LOG_LEVEL:INFO}
      - VTB_ACCESS_TOKEN=${VTB_ACCESS_TOKEN}
      - SBER_ACCESS_TOKEN=${SBER_ACCESS_TOKEN}
      - TINKOFF_ACCESS_TOKEN=${TINKOFF_ACCESS_TOKEN}
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
    networks:
      - finpuls-network

  frontend:
    build: ./frontend
    ports:
      - "3000:3000"
    environment:
      - VITE_API_URL=http://localhost:8080/api
    depends_on:
      - backend
    networks:
      - finpuls-network

  prometheus:
    image: prom/prometheus:latest
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
    ports:
      - "9090:9090"
    networks:
      - finpuls-network

networks:
  finpuls-network:
    driver: bridge
```

### Backend Dockerfile

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Frontend Dockerfile

```dockerfile
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 3000
CMD ["nginx", "-g", "daemon off;"]
```

---

## 🔐 Безопасность

### Текущая реализация
- Токены хранятся в БД в зашифрованном виде (Jasypt)
- Маскировка чувствительных данных в логах
- Валидация всех входных данных

### Будущее (OAuth 2.0)
- OAuth 2.0 / OpenID Connect для подключения банков
- Шифрование токенов с использованием Spring Vault
- Refresh token rotation

---

## 📈 Мониторинг и метрики

### Prometheus метрики
- `bank_api_calls_total` — количество запросов к банкам
- `bank_api_errors_total` — количество ошибок
- `bank_api_response_time_seconds` — время ответа
- `http_request_duration_seconds` — время обработки HTTP запросов
- `subscription_active_users` — количество активных пользователей по типам подписки

### Логирование

#### Конфигурация
- Уровень логирования настраивается через `logging.level.root` в `application.properties` (по умолчанию INFO)
- Поддерживаемые уровни: **INFO**, **DEBUG**, **WARN**
- Ротация файлов: автоматическое удаление логов старше **2 недель** (14 дней)
- Раздельные логи для банковских API в отдельном файле
- Уровень логирования можно переопределить через переменную окружения `LOG_LEVEL`

#### Структура JSON лога
Все логи пишутся в JSON формате с единой структурой:

```json
{
  "level": "info|debug|warn",
  "message": "Log message text",
  "timestamp": "2025-01-20T10:30:45.123+0300",
  "data": {
    "http": {
      "ip": "192.168.1.1",
      "url": "/api/balances",
      "userAgent": "Mozilla/5.0..."
    },
    "source": {
      "file": "com.finpuls.service.BankService",
      "function": "getBalances",
      "line": 45
    },
    "data": {
      "userId": "user123",
      "bankName": "VTB"
    }
  },
  "error": {
    "code": "TOKEN_EXPIRED",
    "context": {
      "bankName": "VTB",
      "userId": "user123"
    },
    "message": "Token expired for bank: VTB",
    "stackTrace": "..."
  }
}
```

**Примечания:**
- Поле `error` присутствует только при логировании ошибок
- Поле `error.context` заполняется только в DEBUG режиме
- Поле `data.data` содержит кастомные данные конкретного лога
- HTTP контекст (`data.http`) заполняется автоматически через MDC в интерцепторах
- Source информация (`data.source`) заполняется автоматически Logback

#### Файлы логов
- `logs/finpuls.log` — основные логи приложения
- `logs/bank-api.log` — логи банковских API
- Ротация: `logs/finpuls.2025-01-20.log`, `logs/bank-api.2025-01-20.log`
- Хранение: 14 дней, после чего автоматическое удаление

### Swagger документация
- Доступна по адресу: `http://localhost:8080/swagger-ui.html`
- Автоматически генерируется из аннотаций

---

## 🚀 Точки расширения

1. **Добавление новых банков**: Создать новый `BankAdapter` реализацию
2. **Новые метрики финансового здоровья**: Добавить новую `HealthCalculationStrategy`
3. **Дополнительные типы подписок**: Расширить enum `SubscriptionType`
4. **Миграция на PostgreSQL**: Заменить H2 на PostgreSQL в docker-compose
5. **OAuth интеграция**: Добавить OAuth 2.0 flow в `BankConnectionService`

