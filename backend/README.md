# Windsurf Project Backend

A modern, secure, and scalable backend system built with Spring Boot and JWT authentication.

## Project Structure

```
backend/
├── auth/                    # Authentication service
├── common/                  # Common utilities and shared components
│   ├── common-core/         # Core utilities and base classes
│   ├── common-redis/        # Redis integration and utilities
│   └── common-security/     # Security configurations and JWT utilities
└── pom.xml                  # Parent POM file
```

## Features

- JWT-based authentication
- Role-based access control
- Redis token blacklist
- Secure password hashing
- Modular architecture
- RESTful API design

## Technical Stack

- **Framework**: Spring Boot
- **Security**: Spring Security, JWT
- **Database**: MySQL
- **Cache**: Redis
- **Build Tool**: Maven
- **Java Version**: 11+

## API Endpoints

### Authentication API (`/api/v1/auth`)

- `POST /register` - Register new user
- `POST /login` - User login
- `POST /refresh` - Refresh JWT token
- `POST /logout` - User logout

## Getting Started

### Prerequisites

- JDK 11 or higher
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### Configuration

1. Configure database in `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/windsurf
    username: your_username
    password: your_password
```

2. Configure Redis in `application.yml`:
```yaml
spring:
  redis:
    host: localhost
    port: 6379
```

3. Configure JWT settings in `application.yml`:
```yaml
security:
  jwt:
    secret: your_jwt_secret_key
    expiration: 86400  # 24 hours in seconds
```

### Building and Running

1. Build the project:
```bash
mvn clean install
```

2. Run the application:
```bash
mvn spring-boot:run
```

## Security Features

- Password validation with minimum requirements:
  - At least 8 characters
  - Contains numbers
  - Contains uppercase and lowercase letters
  - Contains special characters
- Token blacklisting for logout
- Automatic token refresh
- Protected endpoints with role-based access

## Development Guidelines

1. **Code Style**
   - Follow Java code conventions
   - Use lombok annotations for boilerplate reduction
   - Document public APIs

2. **Security**
   - Never expose sensitive information in responses
   - Always validate user input
   - Use proper exception handling

3. **Testing**
   - Write unit tests for services
   - Include integration tests for APIs
   - Test security configurations

## License

[MIT License](LICENSE)
