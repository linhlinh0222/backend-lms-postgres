# backend-lms-postgres

Spring Boot 3.5.6 (Java 21) + PostgreSQL bằng Docker Compose.

## Chạy
```powershell
docker compose up -d
mvn clean package
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
Swagger UI: http://localhost:8088/swagger-ui
PgAdmin: http://localhost:8081 (admin@local / admin)
