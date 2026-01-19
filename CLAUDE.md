# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot educational project demonstrating **three different authentication strategies**:

1. **`jwt-token` (root module)** - Full JWT-based authentication with PostgreSQL
2. **`basic-auto`** - Basic authentication with auto-configuration and PostgreSQL
3. **`in-memory`** - In-memory authentication (no database)

Each module is **independent** and can be run separately. They are NOT submodules but separate Spring Boot applications within the same repository.

## Build & Run Commands

### JWT Token Module (Main)
```bash
# Build
mvn clean install

# Run
mvn spring-boot:run

# Run tests
mvn test
```

### Basic Auto Module
```bash
cd basic-auto
mvn spring-boot:run
```

### In-Memory Module
```bash
cd in-memory
mvn spring-boot:run
```

## Database Setup

The main JWT module uses PostgreSQL running in Docker:

```bash
# Start PostgreSQL
docker-compose up -d

# Stop PostgreSQL
docker-compose down

# View logs
docker logs jwt-postgres-db
```

**Database Connection Details:**
- Host: localhost
- Port: 5433 (mapped from container's 5432)
- Database: postgres
- Schema: jwt (auto-created by Hibernate)
- Username: yvz
- Password: ozm

## Architecture

### JWT Module Architecture (Root)

This follows a **layered architecture** with JWT authentication:

**Security Flow:**
1. `JwtAuthFilter` (security/JwtAuthFilter.java) - Intercepts every HTTP request
2. Extracts JWT from `Authorization: Bearer <token>` header
3. `JwtService` (service/JwtService.java) - Validates token and extracts username
4. `UserService` loads UserDetails from database
5. Sets authentication in `SecurityContextHolder`

**Layer Structure:**
- **Controller Layer** (`controller/`) - REST endpoints (`@RestController`)
  - `UserController` - Handles `/auth/**` endpoints (login, register, token generation)
- **Service Layer** (`service/`) - Business logic
  - `JwtService` - Token generation and validation (uses JJWT library)
  - `UserService` - User management and UserDetailsService implementation
- **Repository Layer** (`repository/`) - Data access
  - `UserRepository` extends `JpaRepository<User, Long>`
- **Security Layer** (`security/`) - Authentication/Authorization
  - `SecurityConfig` - Main security configuration, defines filter chain
  - `JwtAuthFilter` - Custom filter extending `OncePerRequestFilter`
- **Model Layer** (`model/`) - Entities
  - `User` implements `UserDetails` (Spring Security interface)
  - `UserRole` enum for authorities
- **DTO Layer** (`dto/`) - Data transfer objects
  - `CreateUserRequest` - User registration
  - `AuthRequest` - Login credentials

**Key Security Configuration:**
- CSRF disabled (stateless JWT)
- Session management: STATELESS
- Public endpoints: `/auth/welcome`, `/auth/addNewUser`, `/auth/generateToken`, `/auth/login`, `/auth/register`, `/swagger-ui/**`
- Protected endpoints: `/auth/user` (authenticated), `/auth/admin` (ROLE_ADMIN)
- JWT expiration: 2 minutes (configured in JwtService:60)

### Configuration Files

**application.properties:**
- Located in `src/main/resources/application.properties`
- Contains database connection settings
- JWT secret key stored as `jwt.key` (Base64 encoded)
- Hibernate DDL set to `update` (auto-creates/updates schema)

**SwaggerConfig:**
- Swagger UI available at: `http://localhost:8080/swagger-ui/index.html`
- All endpoints documented with OpenAPI
- JWT authentication supported in Swagger (use "Authorize" button)

## Important Implementation Details

### JWT Token Flow
1. **Register:** POST `/auth/addNewUser` with `CreateUserRequest`
2. **Login:** POST `/auth/generateToken` with username/password
3. **Use Token:** Include in header as `Authorization: Bearer <token>`
4. Token validated on every request via `JwtAuthFilter`

### User Model
- User entity implements Spring Security's `UserDetails`
- Authorities stored in separate table via `@ElementCollection`
- UserRole enum: ROLE_USER, ROLE_ADMIN (stored as strings)
- Four boolean flags: accountNonExpired, accountNonLocked, credentialsNonExpired, isEnabled

### Password Encoding
- Uses `BCryptPasswordEncoder` configured in `PasswordEncoderConfig`
- Passwords hashed before storage

## Testing

The main application uses port **8080** by default.

**Manual Testing with Swagger:**
1. Start application and database
2. Navigate to `http://localhost:8080/swagger-ui/index.html`
3. Register a new user via `/auth/addNewUser`
4. Generate token via `/auth/generateToken`
5. Click "Authorize" button, enter `Bearer <your-token>`
6. Test protected endpoints

## Module Differences

- **`jwt-token`**: Full JWT implementation with PostgreSQL, Swagger, token-based stateless auth
- **`basic-auto`**: Uses Spring Security's basic auth with PostgreSQL (simpler, session-based)
- **`in-memory`**: No database, users configured in memory (for quick testing)

Each module has its own `pom.xml`, `application.properties`, and complete Spring Boot application structure.

## Common Pitfalls

- Port 5433 must be available for PostgreSQL (not 5432, to avoid conflicts)
- JWT secret key must be Base64 encoded
- Token expires in 2 minutes (change in JwtService:60 if needed)
- Don't commit `application.properties` with real credentials in production
- Schema `jwt` must exist or set `spring.jpa.hibernate.ddl-auto=create` for first run
