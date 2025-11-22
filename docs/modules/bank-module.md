# Bank Module

Описание изолированного банковского модуля.

---

## 📋 Обзор

Bank Module - это **полностью изолированный модуль** в отдельной папке `bank-module/`, который отвечает за все банковские операции (счета, карты, транзакции). Модуль имеет собственную БД, конфигурацию, логирование и метрики. Это отдельный модуль в структуре проекта, а не часть Backend модуля.

**База данных Bank Module:**
- **PostgreSQL (банковская)** — для Client, Account, Card, Transaction

**Интеграция с Backend:**
- Роутеры Bank Module инициализируются в Backend через `BankRouterConfig`
- Вся остальная логика (Entity, Repository, Service, Handler) находится в `bank-module/`
- В будущем можно легко вынести в отдельный микросервис

---

## 🎯 Основные функции

1. **Управление счетами** - получение балансов, информации о счетах
2. **Управление картами** - получение информации о картах
3. **Транзакции** - выполнение переводов между счетами
4. **Изоляция** - полная автономия от Backend модуля

---

## 📁 Структура пакетов

```
com.finpuls.bank/
├── config/                      # Конфигурация (изолированная)
│   ├── PersistenceConfig.java   # PostgreSQL конфигурация
│   ├── BankLoggingConfig.java   # Собственное логирование
│   ├── BankMetricsConfig.java  # Собственные метрики
│   └── SwaggerConfig.java
│
├── domain/                      # Доменная модель
│   ├── model/                   # JPA Entities
│   │   ├── account/
│   │   │   └── Account.java
│   │   ├── card/
│   │   │   └── Card.java
│   │   └── transaction/
│   │       └── Transaction.java
│   ├── repository/              # JPA Repositories
│   │   ├── AccountRepository.java
│   │   ├── CardRepository.java
│   │   └── TransactionRepository.java
│   └── dto/                     # Data Transfer Objects
│       ├── request/
│       │   ├── GetBalanceRequest.java
│       │   ├── GetCardsRequest.java
│       │   ├── GetAccountsRequest.java
│       │   └── TransferRequest.java
│       └── response/
│           ├── BalanceDTO.java
│           ├── CardDTO.java
│           ├── AccountDTO.java
│           └── TransferResponse.java
│
├── service/                     # Бизнес-логика
│   ├── account/
│   │   ├── IAccountService.java
│   │   └── AccountService.java
│   ├── card/
│   │   ├── ICardService.java
│   │   └── CardService.java
│   └── transfer/
│       ├── ITransferService.java
│       └── TransferService.java
│
├── api/                         # REST API
│   ├── router/                  # Router Functions (WebFlux)
│   │   ├── BankRouter.java      # Определение роутов
│   │   ├── BalanceRouter.java
│   │   ├── CardRouter.java
│   │   ├── AccountRouter.java
│   │   └── TransferRouter.java
│   └── handler/                 # Request Handlers
│       ├── BalanceHandler.java
│       ├── CardHandler.java
│       ├── AccountHandler.java
│       └── TransferHandler.java
│
└── exception/                   # Обработка ошибок
    └── BankExceptionHandler.java
```

---

## 🗄️ База данных

### PostgreSQL (банковская БД)
- **accounts** - банковские счета
- **cards** - банковские карты
- **transactions** - транзакции

**Подробнее:** [Сущности Bank Module](../entities/bank-module.md)

---

## 🔌 API Endpoints

### Балансы
- `GET /api/bank/balances` - получить все балансы пользователя

### Карты
- `GET /api/bank/cards` - получить все карты пользователя

### Счета
- `GET /api/bank/accounts` - получить все счета пользователя

### Переводы
- `POST /api/bank/transfer` - выполнить перевод между счетами

---

## 🔒 Изоляция

### Собственная конфигурация
- `application-bank.properties` - отдельные настройки
- `PersistenceConfig` - отдельное подключение к БД

### Собственное логирование
- `logback-bank.xml` - отдельная конфигурация логирования
- Логи в `logs/bank-combined.log` и `logs/bank-error.log`

### Собственные метрики
- Отдельные метрики Prometheus для Bank Module
- Префикс `bank_` для всех метрик

---

## 🚀 Router Functions

Bank Module использует Spring WebFlux Router Functions для определения API:

```java
@Configuration
public class BankRouter {
    public static RouterFunction<ServerResponse> createRouter() {
        return RouterFunctions.route()
            .GET("/api/bank/balances", balanceHandler::getBalances)
            .GET("/api/bank/cards", cardHandler::getCards)
            .GET("/api/bank/accounts", accountHandler::getAccounts)
            .POST("/api/bank/transfer", transferHandler::transfer)
            .build();
    }
}
```

---

## 🔗 Интеграция с Backend

Bank Module интегрируется с Backend следующим образом:

**Инициализация роутеров в Backend:**
```java
// В Backend: com.finpuls.config.BankRouterConfig
@Configuration
public class BankRouterConfig {
    @Bean
    public RouterFunction<ServerResponse> bankRouter() {
        return BankRouter.createRouter();  // Роутеры из bank-module
    }
}
```

**Распределение компонентов:**
- **В Backend (`backend/`):**
  - `BankRouterConfig` — инициализация роутеров Bank Module
  - Middleware для проверки токенов перед запросами к Bank Module
  
- **В Bank Module (`bank-module/`):**
  - Entity классы (Client, Account, Card, Transaction)
  - Repository интерфейсы
  - Service классы (бизнес-логика)
  - Handler классы (обработчики запросов)
  - Router классы (определение роутов)
  - Конфигурация БД (`PersistenceConfig` для PostgreSQL)

**Важно:** Роутеры Bank Module инициализируются в Backend, но вся остальная логика находится в `bank-module/`. Это позволяет легко вынести Bank Module в отдельный микросервис в будущем.

---

## 📈 Готовность к микросервису

Bank Module спроектирован так, чтобы легко вынести его в отдельный микросервис:

1. Заменить Router Functions на REST контроллеры
2. Добавить HTTP клиент в Backend для вызова Bank Service
3. Настроить отдельный порт и URL
4. Готово!

---

## 📚 Связанные документы

- [Сущности Bank Module](../entities/bank-module.md) - описание Entity классов
- [Архитектура](../ARCHITECTURE.md) - общая архитектура системы
- [Руководство по разработке](../guides/setup/development.md) - настройка и запуск

---

**Последнее обновление:** 2025-01-20

