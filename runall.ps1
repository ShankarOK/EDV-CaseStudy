$BASE="C:\Users\shank\Downloads\SkillDevelopment"

Write-Host "Starting Skill Development Microservices..." -ForegroundColor Cyan

# 1. Eureka Server (must start first)
Start-Process powershell "-NoExit -Command cd '$BASE\eureka-server'; mvn spring-boot:run"
Start-Sleep -Seconds 12

# 2. API Gateway
Start-Process powershell "-NoExit -Command cd '$BASE\api-gateway'; mvn spring-boot:run"

# 3. Security Service
Start-Process powershell "-NoExit -Command cd '$BASE\security-service'; mvn spring-boot:run"

# 4. Validation Service
Start-Process powershell "-NoExit -Command cd '$BASE\validation-service'; mvn spring-boot:run"

# 5. Trainer Service
Start-Process powershell "-NoExit -Command cd '$BASE\trainer-service'; mvn spring-boot:run"

# 6. Course Service
Start-Process powershell "-NoExit -Command cd '$BASE\course-service'; mvn spring-boot:run"

# 7. Trainee Service
Start-Process powershell "-NoExit -Command cd '$BASE\trainee-service'; mvn spring-boot:run"

# 8. Assessment Service
Start-Process powershell "-NoExit -Command cd '$BASE\assessment-service'; mvn spring-boot:run"

# 9. Certification Service
Start-Process powershell "-NoExit -Command cd '$BASE\certification-service'; mvn spring-boot:run"

# 10. Frontend App
Start-Process powershell "-NoExit -Command cd '$BASE\frontend-app'; mvn spring-boot:run"

Write-Host "All services launched." -ForegroundColor Green
