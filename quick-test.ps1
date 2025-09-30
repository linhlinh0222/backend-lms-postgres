# LMS Maritime API Quick Test
$BASE_URL = "http://localhost:8088"

Write-Host "=== Testing LMS Maritime API ===" -ForegroundColor Green

# 1. Register Admin
Write-Host "`n1. Register Admin User..." -ForegroundColor Yellow
$registerData = @{
    username = "admin"
    email = "admin@maritime.com"
    password = "admin123"
    fullName = "Admin User"
    role = "ADMIN"
} | ConvertTo-Json

try {
    $registerResult = Invoke-RestMethod -Uri "$BASE_URL/api/v1/auth/register" -Method POST -Body $registerData -ContentType "application/json"
    Write-Host "✅ Register Success: $($registerResult.message)" -ForegroundColor Green
} catch {
    Write-Host "⚠️ Register: $($_.Exception.Message)" -ForegroundColor Yellow
}

# 2. Login and get token
Write-Host "`n2. Login to get JWT token..." -ForegroundColor Yellow
$loginData = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResult = Invoke-RestMethod -Uri "$BASE_URL/api/v1/auth/login" -Method POST -Body $loginData -ContentType "application/json"
    $token = $loginResult.data.token
    Write-Host "✅ Login Success! Token: $($token.Substring(0,50))..." -ForegroundColor Green
    
    # 3. Get Profile
    Write-Host "`n3. Get User Profile..." -ForegroundColor Yellow
    $headers = @{ Authorization = "Bearer $token" }
    $profile = Invoke-RestMethod -Uri "$BASE_URL/api/v1/auth/profile" -Method GET -Headers $headers
    Write-Host "✅ Profile: $($profile.data.fullName) - Role: $($profile.data.role)" -ForegroundColor Green
    
    # 4. Create Course
    Write-Host "`n4. Create Course..." -ForegroundColor Yellow
    $courseData = @{
        title = "Maritime Safety Course"
        description = "Complete course on maritime safety procedures"
        category = "SAFETY"
        level = "BEGINNER"
        duration = 40
    } | ConvertTo-Json
    
    $course = Invoke-RestMethod -Uri "$BASE_URL/api/v1/courses" -Method POST -Body $courseData -ContentType "application/json" -Headers $headers
    Write-Host "✅ Course Created: $($course.data.title) - ID: $($course.data.id)" -ForegroundColor Green
    
    # 5. Get All Courses
    Write-Host "`n5. Get All Courses..." -ForegroundColor Yellow
    $courses = Invoke-RestMethod -Uri "$BASE_URL/api/v1/courses?page=0&size=10" -Method GET -Headers $headers
    Write-Host "✅ Found $($courses.data.content.Count) courses" -ForegroundColor Green
    
    # 6. Get System Stats
    Write-Host "`n6. Get System Stats..." -ForegroundColor Yellow
    $stats = Invoke-RestMethod -Uri "$BASE_URL/api/v1/admin/stats" -Method GET -Headers $headers
    Write-Host "✅ Stats - Users: $($stats.data.totalUsers), Courses: $($stats.data.totalCourses)" -ForegroundColor Green
    
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🎉 API Testing Complete!" -ForegroundColor Green
Write-Host "👉 Open Swagger UI: http://localhost:8088/swagger-ui/index.html" -ForegroundColor Cyan