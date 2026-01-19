# JWT Security - Spring Boot Authentication Strategies

A comprehensive Spring Boot demonstration project showcasing three different authentication and authorization approaches using Spring Security. This repository serves as a reference implementation for understanding stateless JWT authentication, database-backed basic auth, and in-memory user management.

## Project Overview

This repository demonstrates production-ready authentication patterns in Spring Boot 3.x applications. Each module is independently runnable and showcases a distinct authentication strategy, allowing developers to compare implementations and choose the approach that best fits their use case.

**Key Features:**
- Stateless JWT authentication with custom filters
- Role-based access control (RBAC)
- PostgreSQL integration with JPA/Hibernate
- OpenAPI 3.0 documentation with Swagger UI
- Secure password encoding with BCrypt
- Docker-based database setup

## Modules

### 1. **jwt-token** (Main Module)
**Purpose:** Full-featured JWT-based stateless authentication

**When to use:**
- Building REST APIs consumed by multiple clients (web, mobile, third-party)
- Microservices architecture requiring distributed authentication
- Applications needing scalable, stateless authentication
- Single Sign-On (SSO) implementations

**Key Components:**
- Custom `JwtAuthFilter` intercepting requests
- Token generation and validation via `JwtService`
- 2-minute token expiration (configurable)
- Database-backed user storage

---

### 2. **basic-auto**
**Purpose:** Traditional HTTP Basic Authentication with database persistence

**When to use:**
- Internal tools and admin panels
- Simple authentication requirements
- Legacy system integration
- Rapid prototyping

**Key Components:**
- Spring Security's built-in Basic Auth
- Session-based authentication
- PostgreSQL user storage
- Simplified security configuration

---

### 3. **in-memory**
**Purpose:** In-memory authentication without database dependency

**When to use:**
- Local development and testing
- Proof-of-concepts
- Educational purposes
- Temporary demos

**Key Components:**
- Hardcoded user credentials in configuration
- No external dependencies
- Fastest startup time

## Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17+ | Runtime environment |
| Spring Boot | 3.4.4 | Application framework |
| Spring Security | 6.x | Authentication/Authorization |
| PostgreSQL | 15 | Relational database |
| JJWT | 0.11.5 | JWT creation and parsing |
| Lombok | Latest | Boilerplate reduction |
| SpringDoc OpenAPI | 2.5.0 | API documentation |
| Docker | Latest | Database containerization |
| Maven | 3.8+ | Build tool |

## Prerequisites

- **JDK 17** or newer
- **Maven 3.8+**
- **Docker** and Docker Compose (for PostgreSQL)
- **Git**
- An IDE (IntelliJ IDEA, Eclipse, VS Code)

## Quick Start

Get the JWT module running in under 2 minutes:

```bash
# 1. Clone the repository
git clone https://github.com/yourusername/jwt-security.git
cd jwt-security

# 2. Start PostgreSQL
docker-compose up -d

# 3. Build and run
mvn clean install
mvn spring-boot:run

# 4. Verify
curl http://localhost:8080/auth/welcome
```

Access Swagger UI: **http://localhost:8080/swagger-ui/index.html**

## Build & Run Commands

### JWT Token Module (Default Port: 8080)

```bash
# From project root
mvn clean install               # Build
mvn spring-boot:run            # Run
mvn test                       # Run tests
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081  # Custom port
```

### Basic Auth Module

```bash
cd basic-auto
mvn spring-boot:run            # Runs on port 8080
```

**Test with curl:**
```bash
curl -u username:password http://localhost:8080/private
```

### In-Memory Module

```bash
cd in-memory
mvn spring-boot:run            # No database required
```

## Database Setup

### PostgreSQL via Docker

The project includes a `docker-compose.yml` for PostgreSQL 15:

```bash
# Start database
docker-compose up -d

# View logs
docker logs jwt-postgres-db

# Stop database
docker-compose down

# Reset database (destroys data)
docker-compose down -v
```

**Connection Details:**
- **Host:** localhost
- **Port:** 5433 (mapped from container's 5432)
- **Database:** postgres
- **Schema:** jwt (auto-created by Hibernate)
- **Username:** yvz
- **Password:** ozm

**⚠️ Security Note:** These credentials are for local development only. Use environment variables or secret management in production.

### Schema Management

Hibernate DDL is set to `update` in `application.properties`:
```properties
spring.jpa.hibernate.ddl-auto=update
```

For production, use:
- `validate` - Verify schema matches entities
- Flyway/Liquibase for version-controlled migrations

## Architecture

### Layered Architecture

All modules follow Spring Boot's standard layered architecture:

```
┌─────────────────────────────────────┐
│   Controller Layer (@RestController) │  ← REST endpoints
├─────────────────────────────────────┤
│   Service Layer (@Service)          │  ← Business logic
├─────────────────────────────────────┤
│   Repository Layer (@Repository)    │  ← Data access (JPA)
├─────────────────────────────────────┤
│   Model Layer (@Entity)             │  ← Domain entities
└─────────────────────────────────────┘
```

**Additional Layers in JWT Module:**
- **Security Layer:** `JwtAuthFilter`, `SecurityConfig`
- **DTO Layer:** Request/Response objects decoupled from entities

### JWT Authentication Flow

```
┌─────────┐                    ┌──────────────────┐
│ Client  │                    │  Spring Boot App │
└────┬────┘                    └────────┬─────────┘
     │                                  │
     │ 1. POST /auth/generateToken     │
     │    {username, password}          │
     ├─────────────────────────────────>│
     │                                  │
     │                         ┌────────▼─────────┐
     │                         │ AuthenticationMgr │
     │                         │  validates creds  │
     │                         └────────┬─────────┘
     │                                  │
     │                         ┌────────▼─────────┐
     │                         │   JwtService     │
     │                         │ generates token  │
     │                         └────────┬─────────┘
     │                                  │
     │ 2. Returns JWT token             │
     │<─────────────────────────────────┤
     │                                  │
     │ 3. GET /auth/user                │
     │    Authorization: Bearer <token> │
     ├─────────────────────────────────>│
     │                                  │
     │                         ┌────────▼─────────┐
     │                         │  JwtAuthFilter   │
     │                         │ - Extract token  │
     │                         │ - Validate       │
     │                         │ - Load UserDetails│
     │                         │ - Set SecurityCtx│
     │                         └────────┬─────────┘
     │                                  │
     │                         ┌────────▼─────────┐
     │                         │  Controller      │
     │                         │  processes req   │
     │                         └────────┬─────────┘
     │                                  │
     │ 4. Returns protected resource    │
     │<─────────────────────────────────┤
     │                                  │
```

**Key Security Components:**

1. **`JwtAuthFilter` (OncePerRequestFilter)**
   - Intercepts every HTTP request
   - Extracts JWT from `Authorization: Bearer <token>` header
   - Validates token signature and expiration
   - Populates `SecurityContextHolder` with authenticated user

2. **`JwtService`**
   - Token generation with configurable expiration (default: 2 minutes)
   - HS256 signing algorithm with Base64-encoded secret
   - Claims extraction and validation

3. **`SecurityConfig`**
   - Defines public vs. protected endpoints
   - Configures STATELESS session management (no server-side sessions)
   - Registers custom filter before `UsernamePasswordAuthenticationFilter`
   - CSRF disabled (not needed for stateless APIs)

4. **`UserService` (implements UserDetailsService)**
   - Loads user from database by username
   - Integrates with Spring Security's authentication flow

### Endpoint Security Matrix

| Endpoint | Authentication | Authorization |
|----------|----------------|---------------|
| `/auth/welcome` | None (public) | N/A |
| `/auth/addNewUser` | None (public) | N/A |
| `/auth/generateToken` | None (public) | N/A |
| `/auth/user` | JWT required | Authenticated user |
| `/auth/admin` | JWT required | ROLE_ADMIN only |
| `/swagger-ui/**` | None (public) | N/A |

## API Usage / Testing

### Using Swagger UI (Recommended)

1. **Start the application**
   ```bash
   mvn spring-boot:run
   ```

2. **Open Swagger UI**
   ```
   http://localhost:8080/swagger-ui/index.html
   ```

3. **Register a new user**
   - Endpoint: `POST /auth/addNewUser`
   - Request body:
     ```json
     {
       "name": "John Doe",
       "username": "john",
       "password": "password123",
       "roles": ["ROLE_USER"]
     }
     ```

4. **Generate JWT token**
   - Endpoint: `POST /auth/generateToken`
   - Request body:
     ```json
     {
       "username": "john",
       "password": "password123"
     }
     ```
   - Copy the returned token

5. **Authorize in Swagger**
   - Click the **"Authorize"** button (top-right)
   - Enter: `Bearer <your-token-here>`
   - Click "Authorize"

6. **Test protected endpoints**
   - Try `GET /auth/user` (should succeed)
   - Try `GET /auth/admin` (fails unless user has ROLE_ADMIN)

### Using cURL

```bash
# 1. Register user
curl -X POST http://localhost:8080/auth/addNewUser \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane","username":"jane","password":"pass123","roles":["ROLE_USER"]}'

# 2. Get token
TOKEN=$(curl -X POST http://localhost:8080/auth/generateToken \
  -H "Content-Type: application/json" \
  -d '{"username":"jane","password":"pass123"}' | tr -d '"')

# 3. Access protected endpoint
curl http://localhost:8080/auth/user \
  -H "Authorization: Bearer $TOKEN"
```

### Token Expiration

Tokens expire after **2 minutes** by default (configured in `JwtService.java:60`). To modify:

```java
.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // 30 minutes
```

## CI (GitHub Actions)

This repository includes a GitHub Actions workflow that runs on every push and pull request.

### Required Files Check

The CI pipeline enforces the presence of documentation files to maintain code quality:

```yaml
- name: Check required files exist
  run: |
    test -f README.md || (echo "README.md is missing" && exit 1)
    test -f CLAUDE.md || (echo "CLAUDE.md is missing" && exit 1)
```

**Why these files matter:**
- **README.md** - Onboarding documentation for developers
- **CLAUDE.md** - AI-assisted development context (guides Claude Code CLI)

If either file is missing, the CI build will fail. This ensures that:
1. The project remains self-documenting
2. AI tools have proper context for code assistance
3. New contributors can quickly understand the architecture

### CI Workflow

The pipeline performs:
- Compilation check (`mvn compile`)
- Unit test execution (`mvn test`)
- Documentation validation (README.md, CLAUDE.md presence)

## Common Pitfalls

### 1. Database Connection Failed
**Problem:** `Connection to localhost:5433 refused`

**Solution:**
```bash
# Check if PostgreSQL container is running
docker ps | grep jwt-postgres-db

# If not running
docker-compose up -d

# Check logs
docker logs jwt-postgres-db
```

---

### 2. JWT Token Invalid or Expired
**Problem:** `401 Unauthorized` on protected endpoints

**Solution:**
- Tokens expire in 2 minutes by default
- Generate a new token via `/auth/generateToken`
- Ensure header format is exactly: `Authorization: Bearer <token>`
- No extra spaces before/after token

---

### 3. Port 5433 Already in Use
**Problem:** PostgreSQL fails to start

**Solution:**
```bash
# Find process using port 5433
lsof -i :5433

# Kill process or change port in docker-compose.yml
ports:
  - "5434:5432"  # Use 5434 instead

# Update application.properties accordingly
spring.datasource.url=jdbc:postgresql://localhost:5434/postgres
```

---

### 4. Schema "jwt" Does Not Exist
**Problem:** SQL errors on startup

**Solution:**
- With `spring.jpa.hibernate.ddl-auto=update`, Hibernate creates the schema automatically
- If using `validate`, manually create schema:
  ```sql
  CREATE SCHEMA IF NOT EXISTS jwt;
  ```

---

### 5. Lombok Not Working in IDE
**Problem:** Cannot find getters/setters/constructors

**Solution:**
- Install Lombok plugin for your IDE
- Enable annotation processing:
  - **IntelliJ:** Settings → Build, Execution, Deployment → Compiler → Annotation Processors → Enable
  - **Eclipse:** Install Lombok via `lombok.jar`

---

### 6. CSRF Token Missing (Basic Auth Module)
**Problem:** `403 Forbidden` on POST requests

**Solution:**
- The JWT module disables CSRF (stateless)
- Basic auth module may require CSRF tokens for POST/PUT/DELETE
- For testing, you can disable CSRF (NOT recommended for production):
  ```java
  .csrf(AbstractHttpConfigurer::disable)
  ```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**Maintained by:** [Your Name/Organization]
**Questions?** Open an issue on GitHub
**Contributions:** PRs welcome! Please read CONTRIBUTING.md first.
