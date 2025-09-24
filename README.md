# 🎓 Backend LMS PostgreSQL

Hệ thống Learning Management System (LMS) Backend sử dụng Spring Boot và PostgreSQL.

## 🛠️ Công nghệ sử dụng

- **Java 21** - Programming language
- **Spring Boot 3.5.6** - Application framework  
- **PostgreSQL 16.10** - Database
- **Flyway** - Database migration
- **Docker Compose** - Development environment
- **Swagger/OpenAPI** - API documentation
- **Maven** - Build tool

## 🚀 Cách chạy dự án

### Yêu cầu hệ thống
- Java 21
- Maven 3.6+
- Docker & Docker Compose

### Chạy ứng dụng
```powershell
# 1. Khởi động database
docker compose up -d

# 2. Build và chạy ứng dụng
mvn clean package
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
```

## 🔗 Endpoints

- **API Base URL**: http://localhost:8088/api/v1
- **Swagger UI**: http://localhost:8088/swagger-ui
- **Health Check**: http://localhost:8088/api/v1/health
- **PgAdmin**: http://localhost:8081 (admin@local / admin)

## 📚 API Documentation

### Course Management
- `GET /api/v1/courses` - Danh sách khóa học
- `GET /api/v1/courses/{code}` - Chi tiết khóa học
- `PUT /api/v1/courses/{code}` - Tạo/cập nhật khóa học
- `DELETE /api/v1/courses/{code}` - Xóa khóa học

## 🗄️ Database

- **Database**: PostgreSQL 16.10
- **Connection**: localhost:5432/lms
- **Credentials**: lms/lms
- **Migration**: Flyway

## 📁 Cấu trúc dự án

```
src/main/java/com/example/lms/
├── BackendLmsPostgresApplication.java
├── controller/
│   ├── CourseController.java
│   └── HealthController.java
├── entity/
│   └── Course.java
└── repository/
    └── CourseRepository.java
```

## 🐳 Docker Services

```yaml
services:
  db: postgres:16-alpine (port 5432)
  pgadmin: dpage/pgadmin4:8 (port 8081)
```

## 🔧 Environment Profiles

- **dev**: Development (port 8088)
- **prod**: Production (port 8080)

## 📝 License

MIT License
