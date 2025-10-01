# 🎓 Maritime LMS Backend System

Hệ thống Learning Management System (LMS) dành cho ngành Hàng hải, được xây dựng với Spring Boot và PostgreSQL. Hệ thống cung cấp đầy đủ các tính năng quản lý khóa học, bài tập, người dùng và báo cáo thống kê.

## 🛠️ Tech Stack

### **Backend Framework**
- **Java 21** - Programming language với Virtual Threads
- **Spring Boot 3.5.6** - Main application framework
- **Spring Security 6.x** - Authentication & Authorization
- **Spring Data JPA** - Data access layer với Hibernate 6.6.29
- **Spring Validation** - Request validation

### **Database & Migration**
- **PostgreSQL 16-alpine** - Primary database
- **Flyway 10.x** - Database versioning và migration (V1→V6)
- **HikariCP** - Connection pooling (default)

### **Security & Authentication**  
- **JWT (jsonwebtoken 0.12.3)** - Stateless authentication
- **BCrypt** - Password hashing
- **Role-based Access Control** - ADMIN/TEACHER/STUDENT

### **Documentation & Development**
- **SpringDoc OpenAPI 2.6.0** - API documentation (Swagger UI)
- **Docker Compose** - Development environment
- **pgAdmin 4** - Database administration
- **Maven 3.x** - Build and dependency management
- **Lombok** - Code generation

## 🚀 Quick Start

### **System Requirements**
- **Java 21** (JDK 21 với Virtual Threads support)
- **Maven 3.6+** 
- **Docker & Docker Compose**
- **Git** (for version control)

### **Development Setup**
```powershell
# 1. Clone repository
git clone https://github.com/linhlinh0222/backend-lms-postgres.git
cd backend-lms-postgres

# 2. Start database services (PostgreSQL + pgAdmin)
docker compose up -d

# 3. Wait for database to be ready (check health)
docker compose ps

# 4. Build và run application
mvn clean package
mvn spring-boot:run

# Alternative: Run with specific profile
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
```

### **First Time Setup**
```powershell
# Check if migration ran successfully  
curl http://localhost:8088/api/v1/health

# Access Swagger UI for testing
# Browser: http://localhost:8088/swagger-ui
```

## 🔗 Service Endpoints

### **Application URLs**
- **API Base URL**: http://localhost:8088/api/v1
- **Swagger UI**: http://localhost:8088/swagger-ui/index.html
- **Health Check**: http://localhost:8088/api/v1/health
- **pgAdmin**: http://localhost:8081
  - **Email**: `admin@devmail.net`
  - **Password**: `S3cure!Passw0rd`

## 📚 Complete API Documentation

### **🔐 Authentication & User Management**
```
POST   /api/v1/auth/register     - User registration
POST   /api/v1/auth/login        - User login (JWT)
POST   /api/v1/auth/logout       - User logout  
POST   /api/v1/auth/refresh      - Refresh JWT token
GET    /api/v1/auth/profile      - Get user profile
PUT    /api/v1/auth/profile      - Update user profile

GET    /api/v1/users             - List all users (ADMIN)
GET    /api/v1/users/{id}        - Get user details
PUT    /api/v1/users/{id}        - Update user
DELETE /api/v1/users/{id}        - Delete user (ADMIN)
PUT    /api/v1/users/{id}/role   - Change user role (ADMIN)
```

### **📚 Course Management**
```
GET    /api/v1/courses           - List courses (with pagination)
GET    /api/v1/courses/{id}      - Get course details  
POST   /api/v1/courses           - Create course (TEACHER/ADMIN)
PUT    /api/v1/courses/{id}      - Update course (TEACHER/ADMIN)
DELETE /api/v1/courses/{id}      - Delete course (ADMIN)
POST   /api/v1/courses/{id}/enroll - Enroll in course (STUDENT)
```

### **📖 Section & Lesson Management**
```
GET    /api/v1/sections          - List sections
POST   /api/v1/sections          - Create section (TEACHER/ADMIN)
PUT    /api/v1/sections/{id}     - Update section
DELETE /api/v1/sections/{id}     - Delete section

GET    /api/v1/lessons           - List lessons
POST   /api/v1/lessons           - Create lesson (TEACHER/ADMIN)
PUT    /api/v1/lessons/{id}      - Update lesson
DELETE /api/v1/lessons/{id}      - Delete lesson
```

### **📝 Assignment Management**
```
GET    /api/v1/assignments       - List assignments
GET    /api/v1/assignments/{id}  - Get assignment details
POST   /api/v1/assignments       - Create assignment (TEACHER/ADMIN)
PUT    /api/v1/assignments/{id}  - Update assignment
DELETE /api/v1/assignments/{id}  - Delete assignment

POST   /api/v1/assignments/{id}/submit    - Submit assignment (STUDENT)
GET    /api/v1/assignments/{id}/submissions - List submissions (TEACHER/ADMIN)
PUT    /api/v1/assignments/submissions/{id}/grade - Grade submission (TEACHER/ADMIN)
```

### **👨‍💼 Admin Dashboard & Analytics**
```
GET    /api/v1/admin/stats       - System statistics
GET    /api/v1/admin/users       - User analytics
GET    /api/v1/admin/courses     - Course analytics  
GET    /api/v1/admin/assignments - Assignment analytics
```

### **📁 File Upload Management**
```
POST   /api/v1/files/upload      - Upload file
GET    /api/v1/files/{filename}  - Download file
DELETE /api/v1/files/{filename}  - Delete file (ADMIN)
```

## 🗄️ Database Schema

### **PostgreSQL Configuration**
- **Version**: PostgreSQL 16-alpine
- **Connection**: localhost:5432/lms
- **Credentials**: `lms` / `lms`
- **Encoding**: UTF-8
- **Timezone**: UTC

### **Migration History (Flyway)**
```sql
V1__init.sql                          -- Initial schema (courses)
V2__add_users_and_update_courses.sql  -- User management & course updates  
V3__create_course_content_structure.sql -- Sections & lessons
V4__create_assignment_submissions_table.sql -- Assignment system
V5__add_instructions_to_assignments.sql -- Assignment enhancements
V6__add_description_to_lessons.sql   -- Lesson descriptions
```

### **Database Entity Relations**
```
Users (1) ←→ (N) Courses (enrollment)
Courses (1) → (N) Sections → (N) Lessons
Courses (1) → (N) Assignments → (N) AssignmentSubmissions
Users (1) → (N) AssignmentSubmissions
```

## 📁 Project Architecture

```
backend-lms-postgres/
├── docker-compose.yml           # PostgreSQL + pgAdmin setup
├── pom.xml                     # Maven dependencies
├── README.md                   # Project documentation
└── src/main/
    ├── java/com/example/lms/
    │   ├── BackendLmsPostgresApplication.java
    │   ├── config/                     # Security & App configuration
    │   │   ├── JwtAuthenticationFilter.java
    │   │   ├── OpenApiConfig.java
    │   │   ├── PasswordConfig.java 
    │   │   └── SecurityConfig.java
    │   ├── controller/                 # REST API Controllers (8)
    │   │   ├── AdminController.java    # Admin dashboard & analytics
    │   │   ├── AssignmentController.java # Assignment CRUD & submissions
    │   │   ├── AuthController.java     # Authentication & JWT
    │   │   ├── CourseController.java   # Course management
    │   │   ├── FileUploadController.java # File operations
    │   │   ├── HealthController.java   # Health checks
    │   │   ├── LessonController.java   # Lesson management
    │   │   ├── SectionController.java  # Section management
    │   │   └── UserController.java     # User CRUD operations
    │   ├── dto/                        # Data Transfer Objects
    │   │   ├── ApiResponse.java        # Standardized API responses
    │   │   └── ErrorResponse.java      # Error handling DTOs
    │   ├── entity/                     # JPA Entities (7)
    │   │   ├── Assignment.java         # Assignment model
    │   │   ├── AssignmentSubmission.java # Submission model  
    │   │   ├── Course.java             # Course model
    │   │   ├── Lesson.java             # Lesson model
    │   │   ├── Section.java            # Section model
    │   │   ├── Submission.java         # Legacy submission
    │   │   └── User.java               # User model with roles
    │   ├── repository/                 # JPA Repositories (7)  
    │   │   ├── AssignmentRepository.java
    │   │   ├── AssignmentSubmissionRepository.java
    │   │   ├── CourseRepository.java
    │   │   ├── LessonRepository.java
    │   │   ├── SectionRepository.java
    │   │   ├── SubmissionRepository.java
    │   │   └── UserRepository.java
    │   └── service/                    # Business Logic Services (8)
    │       ├── AdminService.java       # Analytics & reporting
    │       ├── AssignmentService.java  # Assignment business logic
    │       ├── AuthService.java        # Authentication logic
    │       ├── CourseService.java      # Course business logic
    │       ├── FileUploadService.java  # File handling logic
    │       ├── LessonService.java      # Lesson business logic
    │       ├── SectionService.java     # Section business logic
    │       └── UserService.java        # User management logic
    └── resources/
        ├── application.yml             # Main configuration
        ├── application-dev.yml         # Development settings
        ├── application-prod.yml        # Production settings
        └── db/migration/              # Flyway migrations (V1-V6)
```

## 🐳 Docker Services

```yaml
services:
  db:                              # PostgreSQL Database
    image: postgres:16-alpine
    container_name: lms-postgres
    ports: ["5432:5432"]
    environment:
      POSTGRES_USER: lms
      POSTGRES_PASSWORD: lms  
      POSTGRES_DB: lms
    volumes: [pgdata:/var/lib/postgresql/data]
    healthcheck: pg_isready every 5s

  pgadmin:                         # Database Administration
    image: dpage/pgadmin4
    ports: ["8081:80"]
    environment:
      PGADMIN_DEFAULT_EMAIL: admin@devmail.net
      PGADMIN_DEFAULT_PASSWORD: S3cure!Passw0rd
    depends_on: [db]
```

## 🎯 Key Features Implemented

### **✅ Authentication & Authorization**
- JWT-based stateless authentication
- Role-based access control (ADMIN/TEACHER/STUDENT)
- Password encryption with BCrypt
- Token refresh mechanism
- Session management

### **✅ Course Management System**
- Complete CRUD operations for courses
- Course enrollment system
- Section and lesson organization
- Course status management (DRAFT/PUBLISHED/ARCHIVED)

### **✅ Assignment & Submission System**
- Assignment creation with due dates
- File upload for assignment submissions
- Grading system with feedback
- Assignment status tracking

### **✅ User Management**
- User registration and profile management
- Role assignment and permission control
- User analytics and reporting

### **✅ Admin Dashboard**
- System statistics and analytics
- User activity monitoring
- Course performance metrics
- Assignment completion tracking

### **✅ API Documentation**
- Complete Swagger/OpenAPI documentation
- Interactive API testing interface
- Request/response examples
- Authentication integration in Swagger UI

## 🔧 Environment Configurations

### **Development Profile** (`application-dev.yml`)
- **Server Port**: 8088
- **Database**: localhost:5432/lms
- **JWT Expiration**: 24 hours
- **Logging Level**: DEBUG for com.example.lms
- **Open-in-view**: true (for lazy loading)

### **Production Profile** (`application-prod.yml`)  
- **Server Port**: 8080
- **Database**: Production PostgreSQL
- **JWT Expiration**: Configurable via environment
- **Logging Level**: INFO
- **Security**: Enhanced configurations

## ⚠️ Known Issues & Improvements Needed

### **🔒 Security Concerns**
- JWT secret is hard-coded (should use environment variable)
- Missing rate limiting for API endpoints
- CORS configuration needs refinement
- No API versioning strategy

### **🗄️ Database & Performance**
- Missing database indexing strategy
- No connection pooling configuration
- Lazy loading issues partially resolved
- Need backup/recovery procedures

### **🧪 Testing & Quality**
- No unit tests implemented
- Missing integration tests  
- No error handling strategy
- Need code coverage reports

### **🚀 DevOps & Monitoring**
- No CI/CD pipeline
- Missing environment variable management
- No health check for application in Docker
- No monitoring and logging aggregation

## 📋 Development Roadmap

### **Phase 1: Testing & Quality**
- [ ] Implement unit tests for all services
- [ ] Add integration tests
- [ ] Setup code coverage reporting
- [ ] Implement error handling strategy

### **Phase 2: Security Enhancement**
- [ ] Move JWT secret to environment variables
- [ ] Implement rate limiting
- [ ] Add API versioning
- [ ] Enhance CORS configuration

### **Phase 3: Performance & Monitoring**
- [ ] Database indexing optimization
- [ ] Connection pooling configuration
- [ ] Application monitoring setup
- [ ] Performance profiling

### **Phase 4: DevOps & Deployment**
- [ ] CI/CD pipeline setup
- [ ] Environment configuration management
- [ ] Docker health checks for application
- [ ] Production deployment strategy

## 📝 License

MIT License

---

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📞 Support

For support and questions:
- **GitHub Issues**: [Create an issue](https://github.com/linhlinh0222/backend-lms-postgres/issues)
- **Documentation**: Check Swagger UI at http://localhost:8088/swagger-ui
