# Стиль кода и соглашения FinPuls

> **Руководство по стилю кода, именованию и структуре проекта**

Этот документ описывает стандарты кодирования, именования и организации кода в проекте FinPuls.

---

## 📋 Содержание

1. [Именование](#именование)
2. [Структура пакетов](#структура-пакетов)
3. [Классы и интерфейсы](#классы-и-интерфейсы)
4. [Методы](#методы)
5. [Переменные и поля](#переменные-и-поля)
6. [Константы](#константы)
7. [ООП принципы](#ооп-принципы)
8. [Аннотации](#аннотации)
9. [Файлы и папки](#файлы-и-папки)
10. [Комментарии](#комментарии)

---

## 📝 Именование

### Общие правила

- **Используйте английский язык** для всех имен (классы, методы, переменные, комментарии)
- **Используйте camelCase** для переменных, методов, полей
- **Используйте PascalCase** для классов, интерфейсов, enum
- **Используйте UPPER_SNAKE_CASE** для констант
- **Используйте kebab-case** для файлов конфигурации (docker-compose.yml, application.properties)

---

## 🏗 Структура пакетов

### Именование пакетов

**Правила:**
- Используйте **lowercase** для всех пакетов
- Используйте одно слово для имени пакета (если возможно)
- Для многословных пакетов используйте **lowercase** без разделителей или с точкой
- Избегайте аббревиатур в именах пакетов (кроме общепринятых)

**Примеры:**

```java
// ✅ Правильно
package com.finpuls.api.controller;
package com.finpuls.domain.model.user;
package com.finpuls.service.auth;
package com.finpuls.security.jwt;

// ❌ Неправильно
package com.finpuls.API.Controller;        // UPPERCASE и PascalCase
package com.finpuls.domain.model.User;     // PascalCase
package com.finpuls.service.Auth;          // PascalCase
package com.finpuls.security.JWT;          // UPPERCASE
```

### Backend модуль

```
com.finpuls/
├── api/                          # REST API слой
│   ├── controller/              # Контроллеры
│   ├── exception/                # Обработка исключений
│   └── response/                # Форматы ответов
│
├── config/                      # Конфигурация Spring
│
├── domain/                      # Доменная модель
│   ├── model/                   # JPA Entity классы
│   │   ├── user/                # Сущности пользователей
│   │   └── token/               # Сущности токенов
│   ├── repository/              # JPA Repository интерфейсы
│   │   ├── user/
│   │   └── token/
│   └── dto/                     # Data Transfer Objects
│       ├── request/             # DTO для запросов
│       └── response/            # DTO для ответов
│
├── service/                     # Бизнес-логика
│   ├── auth/                    # Сервисы авторизации
│   └── bank/                    # Сервисы банковских операций
│
├── security/                    # Безопасность
│   ├── jwt/                     # JWT токены
│   └── password/                # Хэширование паролей
│
├── middleware/                  # Middleware и фильтры
│
└── common/                      # Общие утилиты
```

### Bank Module

```
com.finpuls.bank/
├── api/                         # REST API слой
│   ├── handler/                 # Обработчики запросов
│   └── router/                 # Роутеры (Router Functions)
│
├── config/                      # Конфигурация
│
├── domain/                      # Доменная модель
│   ├── model/                   # JPA Entity классы
│   │   ├── account/            # Сущности счетов
│   │   ├── card/                # Сущности карт
│   │   └── transaction/        # Сущности транзакций
│   ├── repository/              # JPA Repository интерфейсы
│   └── dto/                     # Data Transfer Objects
│       ├── request/
│       └── response/
│
├── service/                     # Бизнес-логика
│   ├── account/                 # Сервисы счетов
│   ├── card/                    # Сервисы карт
│   └── transfer/               # Сервисы переводов
│
└── exception/                   # Обработка исключений
```

---

## 🎯 Классы и интерфейсы

### Именование классов

**Правила:**
- Используйте **PascalCase**
- Имя должно быть **существительным** или **существительным с прилагательным**
- Избегайте аббревиатур (кроме общепринятых: `DTO`, `API`, `JWT`)

**Примеры:**

```java
// ✅ Правильно
public class User {}
public class UserService {}
public class UserRepository {}
public class LoginRequest {}
public class ApiResponse {}
public class JwtTokenProvider {}

// ❌ Неправильно
public class user {}                    // lowercase
public class UserServiceClass {}        // избыточное "Class"
public class UserSvc {}                 // аббревиатура
```

### Именование интерфейсов

**Правила:**
- Используйте **PascalCase**
- Для интерфейсов сервисов используйте префикс `I` или суффикс `Service` (на выбор)
- Для интерфейсов репозиториев используйте суффикс `Repository`

**Примеры:**

```java
// ✅ Вариант 1: Префикс I
public interface IAuthService {}
public interface IUserService {}

// ✅ Вариант 2: Суффикс Service (предпочтительно для Spring)
public interface AuthService {}
public interface UserService {}

// ✅ Репозитории (всегда суффикс Repository)
public interface UserRepository extends JpaRepository<User, Long> {}
public interface TokenRepository extends JpaRepository<Token, Long> {}
```

**Рекомендация:** В Spring Boot используйте суффикс `Service` без префикса `I`, так как Spring использует интерфейсы для DI.

---

## 🔧 Методы

### Именование методов

**Правила:**
- Используйте **camelCase**
- Имя должно быть **глаголом** или **глаголом с существительным**
- Используйте понятные глаголы: `get`, `set`, `create`, `update`, `delete`, `find`, `validate`, `process`

**Примеры:**

```java
// ✅ Правильно
public User findById(Long id) {}
public void createUser(User user) {}
public boolean validateToken(String token) {}
public List<Account> getAccountsByUserId(Long userId) {}
public void processTransaction(Transaction transaction) {}

// ❌ Неправильно
public User user(Long id) {}              // не глагол
public void userCreate(User user) {}       // неправильный порядок
public boolean check(String token) {}      // неясный глагол (лучше validate)
```

### Геттеры и сеттеры

**Используйте Lombok:**

```java
@Getter
@Setter
public class User {
    private Long id;
    private String username;
}

// Или используйте @Data (включает @Getter, @Setter, @ToString, @EqualsAndHashCode)
@Data
public class User {
    private Long id;
    private String username;
}
```

**Если без Lombok (не рекомендуется):**

```java
public class User {
    private Long id;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
}
```

### Методы-предикаты (boolean)

**Правила:**
- Используйте префикс `is`, `has`, `can`, `should`
- Имя должно быть вопросом, на который можно ответить да/нет

**Примеры:**

```java
// ✅ Правильно
public boolean isActive() {}
public boolean hasPermission() {}
public boolean canTransfer() {}
public boolean shouldRetry() {}
public boolean isValid() {}

// ❌ Неправильно
public boolean active() {}                 // не вопрос
public boolean checkValid() {}            // лучше isValid()
```

---

## 📦 Переменные и поля

### Именование переменных

**Правила:**
- Используйте **camelCase**
- Имя должно быть **существительным** или **существительным с прилагательным**
- Избегайте однобуквенных имен (кроме циклов: `i`, `j`, `k`)
- Используйте полные слова, избегайте аббревиатур

**Примеры:**

```java
// ✅ Правильно
String userName = "john";
Long userId = 123L;
List<User> userList = new ArrayList<>();
User currentUser = getCurrentUser();
boolean isActive = true;

// ❌ Неправильно
String un = "john";                        // аббревиатура
Long id = 123L;                            // неясное имя
List<User> list = new ArrayList<>();      // неясное имя
User u = getCurrentUser();                // однобуквенное
boolean a = true;                          // неясное имя
```

### Именование полей класса

**Правила:**
- Используйте **camelCase**
- Для boolean полей используйте префикс `is` или просто имя без префикса (Lombok сам добавит `is`)

**Примеры:**

```java
// ✅ Правильно
public class User {
    private Long id;
    private String username;
    private String email;
    private boolean isActive;              // или просто active
    private LocalDateTime createdAt;
}

// ❌ Неправильно
public class User {
    private Long ID;                       // UPPERCASE
    private String user_name;              // snake_case
    private String Email;                  // PascalCase
}
```

### Именование параметров методов

**Правила:**
- Используйте **camelCase**
- Имя должно описывать назначение параметра

**Примеры:**

```java
// ✅ Правильно
public User findById(Long userId) {}
public void createUser(String username, String email) {}
public boolean validateToken(String token) {}

// ❌ Неправильно
public User findById(Long id) {}           // неясно, что это userId
public void createUser(String u, String e) {} // аббревиатуры
```

---

## 🔒 Константы

### Именование констант

**Правила:**
- Используйте **UPPER_SNAKE_CASE**
- Имя должно быть существительным
- Используйте `static final`

**Примеры:**

```java
// ✅ Правильно
public static final String DEFAULT_USERNAME = "guest";
public static final int MAX_RETRY_ATTEMPTS = 3;
public static final long TOKEN_EXPIRATION_TIME = 3600L;
public static final String API_BASE_URL = "https://api.example.com";

// ❌ Неправильно
public static final String defaultUsername = "guest";  // camelCase
public static final int maxRetry = 3;                 // camelCase
```

### Enum константы

**Правила:**
- Используйте **UPPER_SNAKE_CASE**
- Имя должно быть существительным

**Примеры:**

```java
// ✅ Правильно
public enum UserStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    DELETED
}

public enum TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    TRANSFER
}

// ❌ Неправильно
public enum UserStatus {
    active,              // lowercase
    Inactive,            // PascalCase
    suspended            // lowercase
}
```

---

## 🎨 ООП принципы

### Инкапсуляция

**Правила:**
- Все поля класса должны быть `private`
- Используйте Lombok `@Getter` и `@Setter` для доступа
- Для сложной логики создавайте методы вместо прямого доступа

**Примеры:**

```java
// ✅ Правильно
@Data
public class User {
    private Long id;
    private String username;
    private String email;
    
    // Бизнес-логика в методах
    public boolean canTransfer(BigDecimal amount) {
        return balance.compareTo(amount) >= 0;
    }
}

// ❌ Неправильно
public class User {
    public Long id;              // public поле
    public String username;      // public поле
}
```

### Наследование

**Правила:**
- Используйте наследование только когда есть реальная связь "is-a"
- Избегайте глубокой иерархии (максимум 2-3 уровня)
- Используйте `@MappedSuperclass` для Entity классов

**Примеры:**

```java
// ✅ Правильно
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}

@Entity
public class User extends BaseEntity {
    private String username;
}

@Entity
public class Account extends BaseEntity {
    private String accountNumber;
}
```

### Полиморфизм

**Правила:**
- Используйте интерфейсы для абстракции
- Программируйте на уровне интерфейсов, а не реализаций

**Примеры:**

```java
// ✅ Правильно
public interface AuthService {
    LoginResponse login(LoginRequest request);
}

@Service
public class AuthServiceImpl implements AuthService {
    @Override
    public LoginResponse login(LoginRequest request) {
        // реализация
    }
}

// Использование
@Autowired
private AuthService authService;  // интерфейс, а не реализация
```

---

## 🏷 Аннотации

### Порядок аннотаций

**Правила:**
1. Spring аннотации (`@Service`, `@Repository`, `@Controller`, `@Component`)
2. JPA аннотации (`@Entity`, `@Table`, `@Id`, `@Column`)
3. Lombok аннотации (`@Getter`, `@Setter`, `@Data`, `@Builder`)
4. Валидация (`@NotNull`, `@Size`, `@Email`)
5. Другие (`@Slf4j`, `@AllArgsConstructor`)

**Примеры:**

```java
// ✅ Правильно
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    @NotNull
    @Size(min = 3, max = 50)
    private String username;
    
    @Email
    private String email;
}

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
}
```

---

## 📁 Файлы и папки

### Именование файлов

**Правила:**
- Классы Java: **PascalCase** (`UserService.java`, `LoginRequest.java`)
- Файлы конфигурации: **kebab-case** (`application.properties`, `docker-compose.yml`)
- Файлы ресурсов: **kebab-case** или **snake_case** (`application.properties`, `logback-spring.xml`)

**Примеры:**

```
✅ Правильно:
- UserService.java
- LoginRequest.java
- application.properties
- docker-compose.yml
- logback-spring.xml

❌ Неправильно:
- userService.java          (camelCase)
- login_request.java         (snake_case для Java классов)
- Application.Properties     (PascalCase для properties)
```

### Структура папок

**Правила:**
- Используйте **lowercase** для папок
- Используйте **kebab-case** для многословных папок (опционально)
- Следуйте структуре пакетов Java

**Примеры:**

```
✅ Правильно:
src/main/java/com/finpuls/
├── api/
│   ├── controller/
│   └── exception/
├── domain/
│   ├── model/
│   └── repository/
└── service/

❌ Неправильно:
src/main/java/com/finpuls/
├── API/                      (UPPERCASE)
├── Domain/                   (PascalCase)
└── Service/                  (PascalCase)
```

---

## 💬 Комментарии

### JavaDoc комментарии

**Правила:**
- Используйте JavaDoc для публичных классов и методов
- Описывайте назначение, параметры, возвращаемое значение, исключения

**Примеры:**

```java
/**
 * Сервис для работы с пользователями.
 * 
 * @author FinPuls Team
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    /**
     * Находит пользователя по ID.
     * 
     * @param userId ID пользователя
     * @return пользователь или null, если не найден
     * @throws IllegalArgumentException если userId равен null
     */
    public User findById(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return userRepository.findById(userId).orElse(null);
    }
}
```

### Inline комментарии

**Правила:**
- Используйте комментарии для объяснения "почему", а не "что"
- Избегайте очевидных комментариев
- Комментарии должны быть на английском языке

**Примеры:**

```java
// ✅ Правильно
// Используем H2 для токенов, т.к. они временные и не требуют персистентности
@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {}

// Кэшируем результат на 5 минут для снижения нагрузки на БД
@Cacheable(value = "users", ttl = 300)
public User findById(Long id) {}

// ❌ Неправильно
// Получаем пользователя по ID
public User findById(Long id) {}  // очевидный комментарий

// Создаем новый объект User
User user = new User();            // очевидный комментарий
```

---

## 📋 Чеклист стиля кода

### Перед коммитом проверь:

- [ ] Все классы используют PascalCase
- [ ] Все методы и переменные используют camelCase
- [ ] Все константы используют UPPER_SNAKE_CASE
- [ ] Все поля класса `private`
- [ ] Используется Lombok для геттеров/сеттеров
- [ ] JavaDoc добавлен для публичных методов
- [ ] Комментарии на английском языке
- [ ] Нет однобуквенных переменных (кроме циклов)
- [ ] Имена методов - глаголы
- [ ] Имена классов - существительные

---

## 🔗 Связанные документы

- [AI Learning Strategy](./ai-learning-strategy.md) - Стратегия обучения
- [Development Guide](./development.md) - Руководство по разработке
- [Architecture](../ARCHITECTURE.md) - Архитектура проекта

---

**Последнее обновление:** 2025-01-20

