# Backend модуль

Описание основного Backend модуля (API Gateway).

---

## 📋 Обзор

Backend модуль выступает в роли **API Gateway** между клиентом (Frontend) и банковским модулем. Он обеспечивает авторизацию, валидацию запросов и проксирование запросов к Bank Module.

**Базы данных Backend:**
- **PostgreSQL (основная)** — для User, UserCredentials, UserBankClient
- **H2 (in-memory)** — для Token (refresh tokens)

---

## 🎯 Основные функции

1. **Авторизация** - JWT токены (access + refresh)
2. **Валидация** - проверка входных данных
3. **Middleware** - фильтры для проверки токенов
4. **Проксирование** - передача запросов в Bank Module
5. **Обработка ошибок** - единый формат ответов

---

## 📁 Структура пакетов

```
com.finpuls/
├── config/                      # Конфигурация
│   ├── PersistenceConfigMain.java      # PostgreSQL конфигурация
│   ├── PersistenceConfigTokenH2.java   # H2 конфигурация для токенов
│   ├── SecurityConfig.java             # Spring Security + JWT
│   ├── SwaggerConfig.java
│   ├── WebMvcConfig.java               # Интерцепторы
│   └── BankRouterConfig.java           # Инициализация Bank Module роутера
│
├── domain/                      # Доменная модель
│   ├── model/                   # JPA Entities
│   │   ├── user/
│   │   │   ├── User.java
│   │   │   └── UserCredentials.java
│   │   └── token/
│   │       └── Token.java
│   ├── repository/              # JPA Repositories
│   │   ├── user/
│   │   │   ├── UserRepository.java
│   │   │   └── UserCredentialsRepository.java
│   │   └── token/
│   │       └── TokenRepository.java
│   └── dto/                     # Data Transfer Objects
│       ├── request/
│       │   └── auth/
│       │       ├── LoginRequest.java
│       │       └── RefreshTokenRequest.java
│       └── response/
│           └── auth/
│               ├── LoginResponse.java
│               └── TokenResponse.java
│
├── service/                     # Бизнес-логика
│   ├── auth/
│   │   ├── IAuthService.java
│   │   ├── AuthService.java
│   │   ├── ITokenService.java
│   │   └── TokenService.java
│   └── bank/
│       ├── IBankService.java
│       └── BankService.java     # Прокси к Bank Module
│
├── api/                         # REST API
│   ├── controller/
│   │   └── AuthController.java
│   └── response/
│       ├── IApiResponse.java
│       ├── ApiResponse.java
│       └── ResponseStatus.java
│
├── security/                    # Безопасность
│   ├── jwt/
│   │   └── JwtTokenProvider.java
│   └── password/
│       └── PasswordEncoder.java
│
├── middleware/                  # Middleware
│   ├── RequestIdFilter.java
│   ├── TokenValidationFilter.java
│   └── RequestLoggingInterceptor.java
│
└── exception/                   # Обработка ошибок
    ├── GlobalExceptionHandler.java
    └── [кастомные исключения]
```

---

## 🗄️ Базы данных

### PostgreSQL (основная БД)
- **users** - информация о пользователях
- **user_credentials** - логины и пароли

### H2 (токены)
- **tokens** - refresh tokens для JWT

**Подробнее:** [Сущности Backend](../entities/backend.md)

---

## 🔐 Авторизация

### JWT токены
- **Access Token** - короткоживущий (1 час)
- **Refresh Token** - долгоживущий (24 часа), хранится в H2

### Endpoints
- `POST /api/auth/login` - вход
- `POST /api/auth/refresh` - обновление токена
- `POST /api/auth/logout` - выход

---

## 🛡️ Middleware

### RequestIdFilter
- Генерирует уникальный `requestId` для каждого запроса
- Передает через `ThreadLocal`

### TokenValidationFilter
- Проверяет JWT токен в заголовке `Authorization`
- Устанавливает `SecurityContext`

### RequestLoggingInterceptor
- Логирует все запросы и ответы
- Маскирует чувствительные данные

---

## 📝 Обработка ошибок

Все ошибки обрабатываются через `GlobalExceptionHandler` и возвращаются в едином формате:

```json
{
  "status": "error",
  "message": "Operation failed",
  "error": {
    "code": "ERROR_CODE",
    "message": "Error message",
    "context": {}
  },
  "requestId": "a1b2c3d4"
}
```

---

## 🔗 Интеграция с Bank Module

Backend инициализирует роутер Bank Module через `BankRouterConfig`:

```java
@Configuration
public class BankRouterConfig {
    @Bean
    public RouterFunction<ServerResponse> bankRouter() {
        return BankRouter.createRouter();
    }
}
```

---

## 📚 Связанные документы

- [Сущности Backend](../entities/backend.md) - описание Entity классов
- [Архитектура](../ARCHITECTURE.md) - общая архитектура системы
- [Руководство по разработке](../guides/development.md) - настройка и запуск

---

**Последнее обновление:** 2025-01-20

