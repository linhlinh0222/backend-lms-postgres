-- Migration V2: Add Users table and update courses table

-- Drop existing table if exists to recreate with UUID
DROP TABLE IF EXISTS course CASCADE;

-- Create Users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT' CHECK (role IN ('ADMIN', 'TEACHER', 'STUDENT')),
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create Courses table with UUID and relationships
CREATE TABLE courses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(64) UNIQUE NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'PENDING', 'APPROVED', 'REJECTED')),
    teacher_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create junction table for course enrollments
CREATE TABLE course_enrollments (
    course_id UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    student_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (course_id, student_id)
);

-- Create indexes for performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_courses_code ON courses(code);
CREATE INDEX idx_courses_teacher_id ON courses(teacher_id);
CREATE INDEX idx_courses_status ON courses(status);
CREATE INDEX idx_course_enrollments_course_id ON course_enrollments(course_id);
CREATE INDEX idx_course_enrollments_student_id ON course_enrollments(student_id);

-- Insert default admin user (password: admin123)
INSERT INTO users (username, email, password, full_name, role, enabled) VALUES
('admin', 'admin@lms.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'Administrator', 'ADMIN', true);

-- Insert sample teacher (password: admin123)
INSERT INTO users (username, email, password, full_name, role, enabled) VALUES
('teacher1', 'teacher1@lms.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'Nguyễn Văn Giáo', 'TEACHER', true);

-- Insert sample student (password: admin123)
INSERT INTO users (username, email, password, full_name, role, enabled) VALUES
('student1', 'student1@lms.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'Trần Thị Học', 'STUDENT', true);

-- Insert sample courses
INSERT INTO courses (code, title, description, status, teacher_id) 
SELECT 'JAVA101', 'Lập trình Java cơ bản', 'Khóa học Java cho người mới bắt đầu', 'APPROVED', u.id 
FROM users u WHERE u.username = 'teacher1';

INSERT INTO courses (code, title, description, status, teacher_id) 
SELECT 'SPRING201', 'Spring Framework', 'Khóa học Spring Boot nâng cao', 'APPROVED', u.id 
FROM users u WHERE u.username = 'teacher1';

-- Enroll student in courses
INSERT INTO course_enrollments (course_id, student_id) 
SELECT c.id, u.id 
FROM courses c, users u 
WHERE c.code = 'JAVA101' AND u.username = 'student1';

INSERT INTO course_enrollments (course_id, student_id) 
SELECT c.id, u.id 
FROM courses c, users u 
WHERE c.code = 'SPRING201' AND u.username = 'student1';