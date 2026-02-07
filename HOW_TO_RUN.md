# How to Run the Skill Development System (Phase 1 + Phase 2 + Phase 3)

Run the services in this order. Use **separate terminals** (or run some in the background). All commands assume you are in the project root: `SkillDevelopment`.

---

## 1. Start Eureka Server (must be first)

**Why first:** Other services register here. If Eureka is not running, clients will retry until it is up.

```powershell
cd eureka-server
mvn spring-boot:run
```

- **Port:** 8761  
- **Check:** Open http://localhost:8761 — you should see the Eureka dashboard. At first it will show only `EUREKA-SERVER` (itself).

Leave this terminal running.

---

## 2. Start API Gateway

**New terminal:**

```powershell
cd api-gateway
mvn spring-boot:run
```

- **Port:** 8080  
- **Check:** In the Eureka dashboard (http://localhost:8761) you should see **API-GATEWAY** listed.

Leave this running.

---

## 3. Start Security Service

**New terminal:**

```powershell
cd security-service
mvn spring-boot:run
```

- **Port:** 8081  
- **Check:** Eureka dashboard shows **SECURITY-SERVICE**.

Leave this running.

---

## 4. Start Validation Service

**New terminal:**

```powershell
cd validation-service
mvn spring-boot:run
```

- **Port:** 8082  
- **Check:** Eureka shows **VALIDATION-SERVICE**.

Leave this running.

---

## 5. Start Trainer Service

**New terminal:**

```powershell
cd trainer-service
mvn spring-boot:run
```

- **Port:** 8083  
- **Check:** Eureka shows **TRAINER-SERVICE**.

Leave this running.

---

## 6. Start Course Service

**New terminal:**

```powershell
cd course-service
mvn spring-boot:run
```

- **Port:** 8084  
- **Check:** Eureka shows **COURSE-SERVICE**.

Leave this running.

---

## 7. Start Trainee Service

**New terminal:**

```powershell
cd trainee-service
mvn spring-boot:run
```

- **Port:** 8085  
- **Check:** Eureka shows **TRAINEE-SERVICE**.

Leave this running.

---

## 8. Start Assessment Service

**New terminal:**

```powershell
cd assessment-service
mvn spring-boot:run
```

- **Port:** 8086  
- **Check:** Eureka shows **ASSESSMENT-SERVICE**.

Leave this running.

---

## 9. Start Certification Service

**New terminal:**

```powershell
cd certification-service
mvn spring-boot:run
```

- **Port:** 8087  
- **Check:** Eureka shows **CERTIFICATION-SERVICE**.

Leave this running.

---

## 10. Start Frontend App (Thymeleaf UI)

**New terminal:**

```powershell
cd frontend-app
mvn spring-boot:run
```

- **Port:** 9090  
- **Check:** Open http://localhost:9090 — you should see the home page. Login with admin/admin123, trainer/trainer123, or trainee/trainee123 to access role-specific dashboards.

Leave this running.

---

## Summary: What to have running

| Order | Service          | Port | Command (from project root)     |
|-------|------------------|------|----------------------------------|
| 1     | Eureka Server    | 8761 | `cd eureka-server; mvn spring-boot:run` |
| 2     | API Gateway      | 8080 | `cd api-gateway; mvn spring-boot:run` |
| 3     | Security Service | 8081 | `cd security-service; mvn spring-boot:run` |
| 4     | Validation Service | 8082 | `cd validation-service; mvn spring-boot:run` |
| 5     | Trainer Service  | 8083 | `cd trainer-service; mvn spring-boot:run` |
| 6     | Course Service   | 8084 | `cd course-service; mvn spring-boot:run` |
| 7     | Trainee Service  | 8085 | `cd trainee-service; mvn spring-boot:run` |
| 8     | Assessment Service | 8086 | `cd assessment-service; mvn spring-boot:run` |
| 9     | Certification Service | 8087 | `cd certification-service; mvn spring-boot:run` |
| 10    | Frontend App          | 9090 | `cd frontend-app; mvn spring-boot:run` |

**Eureka dashboard:** http://localhost:8761 — 9 backend applications (frontend does not register with Eureka by default).  
**UI:** http://localhost:9090 — Thymeleaf frontend; login and use Admin / Trainer / Trainee dashboards.

---

## Quick tests (all via Gateway on port 8080)

Use a browser, Postman, or `curl`. Examples below use **PowerShell** and `curl` (or `Invoke-RestMethod`).

### 1. Login (get JWT)

```powershell
# PowerShell
$body = '{"username":"admin","password":"admin123"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -Body $body -ContentType "application/json"
```

You should get JSON with `token`, `username`, and `role`. Copy the `token` for the next steps (optional; see below).

**Other users:**  
- `trainer` / `trainer123` (role: TRAINER)  
- `trainee` / `trainee123` (role: TRAINEE)

### 2. Validation (no auth required for now)

```powershell
$body = '{"durationHours":40,"startDate":"2025-03-01","endDate":"2025-04-01","trainerId":1,"category":"Java"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/validate/course" -Method Post -Body $body -ContentType "application/json"
```

Expected: `valid: true` and empty `errors` (or specific errors if you send invalid data).

### 3. Create a trainer

```powershell
$body = '{"name":"John Doe","specialization":"Java","experienceYears":5,"available":true,"email":"john@example.com","contact":"1234567890"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/trainers" -Method Post -Body $body -ContentType "application/json"
```

### 4. List trainers

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/trainers" -Method Get
```

### 5. Create a course (after at least one trainer exists)

Use the trainer `id` from step 3 (e.g. 1):

```powershell
$body = '{"title":"Spring Boot Basics","category":"Java","durationHours":40,"description":"Intro to Spring Boot","startDate":"2025-03-01","endDate":"2025-04-01","trainerId":1}'
Invoke-RestMethod -Uri "http://localhost:8080/api/courses" -Method Post -Body $body -ContentType "application/json"
```

### 6. List courses

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/courses" -Method Get
```

### 7. Create a trainee

```powershell
$body = '{"name":"Jane Smith","email":"jane@example.com","contact":"9876543210","qualification":"B.Tech","skillPreferences":"Java"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/trainees" -Method Post -Body $body -ContentType "application/json"
```

### 8. Enroll trainee in course

Use trainee id and course id from above (e.g. trainee 1, course 1):

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/trainees/1/enroll?courseId=1" -Method Post
```

### 9. List trainee enrollments

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/trainees/1/enrollments" -Method Get
```

### 10. Create an assessment (after a course exists)

```powershell
$body = '{"title":"Java Quiz","courseId":1,"passingScore":60,"maxScore":100,"dueDate":"2025-12-31","createdByTrainerId":1,"status":"PUBLISHED"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/assessments" -Method Post -Body $body -ContentType "application/json"
```

### 11. Add a question to the assessment

```powershell
$body = '{"questionText":"What is Java?","optionA":"A language","optionB":"A coffee","optionC":"Both","optionD":"Neither","correctOption":"C","marksPerQuestion":10}'
Invoke-RestMethod -Uri "http://localhost:8080/api/assessments/1/questions" -Method Post -Body $body -ContentType "application/json"
```

### 12. Trainee submits assessment (answers: questionId -> selected option, e.g. "A")

```powershell
$body = '{"traineeId":1,"answers":{"1":"C"}}'
Invoke-RestMethod -Uri "http://localhost:8080/api/assessments/1/submit" -Method Post -Body $body -ContentType "application/json"
```

### 13. Trainer evaluates submission (if score >= passingScore, certificate is issued automatically)

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/assessments/submissions/1/evaluate?trainerId=1&courseName=Spring%20Boot%20Basics" -Method Post
```

### 14. List certificates for a trainee

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/certificates/trainee/1" -Method Get
```

---

## If something fails

- **Eureka:** Must be running first. Wait until http://localhost:8761 loads, then start Gateway and the rest.
- **Gateway:** If it does not list in Eureka, check that `eureka.client.service-url.defaultZone=http://localhost:8761/eureka/` is in `api-gateway/src/main/resources/application.properties`.
- **Port in use:** Change the port in that service’s `application.properties` (e.g. `server.port=8086`) and ensure no other app uses the same port.
- **Course/Trainee creation fails:** Ensure Validation Service is up (they call it via Feign). Ensure Trainer Service is up when creating courses (course references `trainerId`).
- **Assessment/Certificate:** Assessment Service calls Validation and Certification. Ensure both Validation and Certification services are up when creating assessments or evaluating submissions (certificate is issued on pass).
- **Database:** By default, Trainer/Course/Trainee use **H2** (file in `./data/`). No MySQL needed. For MySQL, use profile `mysql` and configure `application-mysql.properties` (create DBs `trainer_db`, `course_db`, `trainee_db` if needed).

---

## Stopping

Stop each service with **Ctrl+C** in its terminal. Stopping Eureka last is fine; clients will just lose discovery until you start Eureka again.
