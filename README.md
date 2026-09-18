# auth-backend

> **Phase 2 — Backend Only**
> Standalone Spring Boot JWT Authentication Backend.
> Frontend integration will be added in Phase 3.

---

## Project Purpose

A production-ready RESTful backend for user registration and JWT-based authentication, built to integrate with the `auth-frontend` React application in Phase 3.

---

## Technology Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Language |
| Spring Boot | 3.2.5 | Framework |
| Maven | 3.x | Build tool |
| Spring Web | — | REST controllers |
| Spring Data JPA | — | ORM / DB access |
| Spring Security 6 | — | Auth filter chain |
| MySQL | 8.x | Database |
| Hibernate | — | JPA implementation |
| BCrypt | — | Password hashing |
| JJWT | 0.12.6 | JWT generation & validation |
| Lombok | — | Boilerplate reduction |

---

## Requirements

- Java 17+
- Maven 3.8+
- MySQL 8.x running on `localhost:3306`

---

## MySQL Setup

```sql
-- Option 1: Let Spring Boot auto-create the database (recommended for dev)
-- Set createDatabaseIfNotExist=true in application.properties (already configured)

-- Option 2: Create manually
CREATE DATABASE IF NOT EXISTS authentication_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

Tables (`users` and `jwt_tokens`) are created **automatically** by Hibernate on first startup (`ddl-auto=update`).

---

## Configuration

### Environment Variables

Set these before running the application:

| Variable | Default (dev only) | Description |
|---|---|---|
| `DB_USERNAME` | `root` | MySQL username |
| `DB_PASSWORD` | *(empty)* | MySQL password |
| `JWT_SECRET` | `dev-only-secret-...` | JWT signing secret (min 32 chars) |

**PowerShell (Windows):**
```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "yourpassword"
$env:JWT_SECRET  = "your-strong-secret-32-chars-minimum!!"
```

**Linux/macOS:**
```bash
export DB_USERNAME=root
export DB_PASSWORD=yourpassword
export JWT_SECRET=your-strong-secret-32-chars-minimum!!
```

> ⚠️ **Never commit real credentials or production JWT secrets to source control.**

### JWT Configuration

| Property | Default | Description |
|---|---|---|
| `jwt.expiration.ms` | `86400000` | Token lifetime: **24 hours** |

---

## How to Run

```bash
# 1. Clone the repository
git clone https://github.com/<your-username>/auth-backend.git
cd auth-backend

# 2. Set environment variables (see above)

# 3. Build
mvn clean package -DskipTests

# 4. Run
mvn spring-boot:run
# OR
java -jar target/auth-backend-1.0.0.jar
```

The server starts on **http://localhost:8080**

---

## API Endpoints

### POST `/api/auth/register` — Public

Register a new user.

**Request:**
```json
{
  "username": "vilas",
  "password": "password123",
  "email": "vilas@gmail.com",
  "phone": "9876543210"
}
```

**Success (201 Created):**
```json
{
  "message": "Registration successful"
}
```

**Errors:**
- `400` — validation failure (missing/invalid fields)
- `409` — username or email already exists

---

### POST `/api/auth/login` — Public

Authenticate and receive a JWT.

**Request:**
```json
{
  "username": "vilas",
  "password": "password123"
}
```

**Success (200 OK):**
```json
{
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "vilas"
}
```

**Errors:**
- `400` — validation failure
- `401` — `Invalid credentials`

---

### GET `/api/users/me` — Protected (JWT required)

Get the currently authenticated user's profile.

**Request header:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Success (200 OK):**
```json
{
  "id": 1,
  "username": "vilas",
  "email": "vilas@gmail.com",
  "phone": "9876543210"
}
```

**Errors:**
- `401` — missing, invalid, or expired JWT

---

## Authentication & JWT

- Algorithm: **HMAC-SHA256** (`HS256`)
- Claims: `userId`, `username`, `iat` (issued-at), `exp` (expiration)
- Expiry: **24 hours** (configurable via `jwt.expiration.ms`)
- Token is stored in the `jwt_tokens` table after each successful login
- Password and password hash are **never** included in the JWT or any API response

---

## Database Schema

### `users`
| Column | Type | Notes |
|---|---|---|
| `id` | BIGINT PK | Auto-increment |
| `username` | VARCHAR(50) | Unique |
| `password` | VARCHAR(255) | BCrypt hash only |
| `email` | VARCHAR(100) | Unique |
| `phone` | VARCHAR(20) | — |
| `created_at` | DATETIME | Auto-set |
| `updated_at` | DATETIME | Auto-updated |

### `jwt_tokens`
| Column | Type | Notes |
|---|---|---|
| `tid` | BIGINT PK | Auto-increment |
| `uid` | BIGINT FK | → `users.id` |
| `token` | TEXT | JWT string |
| `cat` | VARCHAR(20) | e.g. `ACCESS` |
| `expiry` | DATETIME | Token expiry |
| `created_at` | DATETIME | Auto-set |

---

## Postman Testing

1. Import or create a Postman collection
2. **Register**: `POST http://localhost:8080/api/auth/register` with JSON body
3. **Login**: `POST http://localhost:8080/api/auth/login` → copy the `token` from the response
4. **Current user**: `GET http://localhost:8080/api/users/me`
   - Header: `Authorization: Bearer <token>`

---

## Project Structure

```
auth-backend/
├── src/
│   ├── main/
│   │   ├── java/com/auth/app/
│   │   │   ├── controller/      AuthController, UserController
│   │   │   ├── service/         AuthService, UserService
│   │   │   ├── repository/      UserRepository, JwtTokenRepository
│   │   │   ├── entity/          User, JwtToken
│   │   │   ├── dto/             RegisterRequest, LoginRequest, LoginResponse, UserResponse
│   │   │   ├── security/        JwtService, JwtAuthenticationFilter, SecurityConfig
│   │   │   ├── exception/       GlobalExceptionHandler, UserAlreadyExistsException, InvalidCredentialsException
│   │   │   └── AuthBackendApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/auth/app/
│           └── AuthBackendApplicationTests.java
├── pom.xml
├── README.md
└── .gitignore
```

---

## Phase Roadmap

| Phase | Status | Description |
|---|---|---|
| Phase 1 | ✅ Complete | React + Vite frontend (mock auth) |
| Phase 2 | ✅ Complete | Spring Boot backend (JWT auth) |
| Phase 3 | 🔜 Pending | Frontend + Backend integration |
| Phase 4 | 🔜 Pending | JWT + End-to-end testing |
