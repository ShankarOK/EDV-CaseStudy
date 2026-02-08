# How to Run the Skill Development System

**Overview (per project description):** Microservices-based solution for training institutes and corporate learning: course catalogs, trainee profiles, assignments, assessments, certifications. **Stack:** Eureka (registry), Spring Cloud Gateway, Security Service (JWT), Course / Trainee / Trainer / Assessment / Certification / Validation services, Thymeleaf frontend. **Database:** MySQL is the primary database for deployment; H2 is used for local development only (default profile). See **README.md** → *Database Configuration Strategy*. **Security:** Centralized auth; Gateway validates JWT via Security Service before routing; roles Admin, Trainer, Trainee.

**Spec alignment:** For a clear audit of what is **done**, **partial**, or **not done** versus the original project description—and **how to correct or implement** each item—see **PROJECT_STATUS.md**. Summary: **README.md** → *Project description compliance*.

---

## Option A: Run with Docker (Phase 7 — recommended for full stack)

**Prerequisites:** Docker Engine and Docker Compose (or `docker compose` plugin) installed.

From the project root:

```powershell
docker compose up -d
```

Or, to build images first then start:

```powershell
docker compose build
docker compose up -d
```

- **Frontend:** http://localhost:9090  
- **API Gateway:** http://localhost:8080  
- **Eureka dashboard:** http://localhost:8761  

MySQL runs in a container (port 3306); all business services use profile `mysql` and connect to the `mysql` service. Data persists in the `mysql_data` volume. To stop: `docker compose down`. To stop and remove the database volume: `docker compose down -v`.

**Default seed data:** On first run (empty DB), Trainer service creates 2 trainers, Course service creates 1 course (assigned to trainer 1), and Trainee service creates 1 trainee. You can log in as **admin/admin123** and immediately register courses (trainers list is populated), assign trainers (courses and trainers exist), or as **trainee/trainee123** to see courses and enroll.

---

## Option B: Run locally (separate terminals)

Run the services in this order. Use **separate terminals** (or run some in the background). All commands assume you are in the project root: `SkillDevelopment`.

**Important (Phase 1 — JWT at Gateway):** All API requests through the Gateway (except `/api/auth/login` and `/api/auth/register`) **require** a valid JWT in the `Authorization: Bearer <token>` header. Unauthenticated requests to `/api/courses`, `/api/trainees`, etc. will receive **401 Unauthorized**. The frontend sends the token automatically after login; for direct API calls (e.g. Postman, curl), obtain a token via `POST /api/auth/login` first and add the header to subsequent requests.

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
**UI:** http://localhost:9090 — Thymeleaf frontend; login and use Admin / Trainer / Trainee dashboards. **Phase 3:** Session traineeId/trainerId (no hardcoded IDs). **Phase 4:** Trainee/ Trainer feedback pages submit and list feedback via `/api/feedback`. **Phase 5:** Certificate page has “Download PDF” per certificate (proxied via frontend with auth).

---

## Quick tests (all via Gateway on port 8080)

Use a browser, Postman, or `curl`. **All API calls except login (and register) require a JWT.** Get a token first (step 1), then pass it as `Authorization: Bearer <token>` for steps 2–14. Examples below use **PowerShell** and `Invoke-RestMethod`.

### 1. Login (get JWT) — required for all other API calls

```powershell
# PowerShell
$body = '{"username":"admin","password":"admin123"}'
$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -Body $body -ContentType "application/json"
$token = $loginResponse.token
$headers = @{ Authorization = "Bearer $token" }
```

You get JSON with `token`, `username`, and `role`. Use `$headers` in steps 2–14 for authenticated requests.

**Other users:**  
- `trainer` / `trainer123` (role: TRAINER)  
- `trainee` / `trainee123` (role: TRAINEE)

### 1b. Get current user / entityId (Phase 2 — for frontend session)

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/me" -Method Get -Headers $headers
```

Returns `username`, `role`, `entityId` (traineeId or trainerId for use in enrollments, certificates, etc.), and optional `displayName`, `email`. Use this after login so the frontend can store `entityId` in session.

**Change password (Phase 2):**
```powershell
$body = '{"currentPassword":"admin123","newPassword":"newpass456"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/password" -Method Put -Body $body -ContentType "application/json" -Headers $headers
```
Returns 204 on success. Then use the new password for login.

**Update profile (Phase 2):**
```powershell
$body = '{"displayName":"Admin User","email":"admin@example.com"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/profile" -Method Put -Body $body -ContentType "application/json" -Headers $headers
```
Returns the same shape as `/auth/me` including the updated displayName and email.

### 2. Validation (requires JWT)

```powershell
$body = '{"durationHours":40,"startDate":"2025-03-01","endDate":"2025-04-01","trainerId":1,"category":"Java"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/validate/course" -Method Post -Body $body -ContentType "application/json" -Headers $headers
```

Expected: `valid: true` and empty `errors` (or specific errors if you send invalid data). Without a valid token you get **401**.

### 3. Create a trainer

```powershell
$body = '{"name":"John Doe","specialization":"Java","experienceYears":5,"available":true,"email":"john@example.com","contact":"1234567890"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/trainers" -Method Post -Body $body -ContentType "application/json" -Headers $headers
```

### 4. List trainers

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/trainers" -Method Get -Headers $headers
```

### 5. Create a course (after at least one trainer exists)

Use the trainer `id` from step 3 (e.g. 1). Use `$headers` from step 1 (JWT):

```powershell
$body = '{"title":"Spring Boot Basics","category":"Java","durationHours":40,"description":"Intro to Spring Boot","startDate":"2025-03-01","endDate":"2025-04-01","trainerId":1}'
Invoke-RestMethod -Uri "http://localhost:8080/api/courses" -Method Post -Body $body -ContentType "application/json" -Headers $headers
```

### 6. List courses

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/courses" -Method Get -Headers $headers
```

### 7. Create a trainee

```powershell
$body = '{"name":"Jane Smith","email":"jane@example.com","contact":"9876543210","qualification":"B.Tech","skillPreferences":"Java"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/trainees" -Method Post -Body $body -ContentType "application/json" -Headers $headers
```

### 8. Enroll trainee in course

Use trainee id and course id from above (e.g. trainee 1, course 1):

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/trainees/1/enroll?courseId=1" -Method Post -Headers $headers
```

### 9. List trainee enrollments

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/trainees/1/enrollments" -Method Get -Headers $headers
```

### 10. Create an assessment (after a course exists)

```powershell
$body = '{"title":"Java Quiz","courseId":1,"passingScore":60,"maxScore":100,"dueDate":"2025-12-31","createdByTrainerId":1,"status":"PUBLISHED"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/assessments" -Method Post -Body $body -ContentType "application/json" -Headers $headers
```

### 11. Add a question to the assessment

```powershell
$body = '{"questionText":"What is Java?","optionA":"A language","optionB":"A coffee","optionC":"Both","optionD":"Neither","correctOption":"C","marksPerQuestion":10}'
Invoke-RestMethod -Uri "http://localhost:8080/api/assessments/1/questions" -Method Post -Body $body -ContentType "application/json" -Headers $headers
```

### 12. Trainee submits assessment (answers: questionId -> selected option, e.g. "A")

```powershell
$body = '{"traineeId":1,"answers":{"1":"C"}}'
Invoke-RestMethod -Uri "http://localhost:8080/api/assessments/1/submit" -Method Post -Body $body -ContentType "application/json" -Headers $headers
```

### 13. Trainer evaluates submission (if score >= passingScore, certificate is issued automatically)

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/assessments/submissions/1/evaluate?trainerId=1&courseName=Spring%20Boot%20Basics" -Method Post -Headers $headers
```

### 14. List certificates for a trainee

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/certificates/trainee/1" -Method Get -Headers $headers
```

---

## If something fails

- **4xx/5xx responses (Phase 6):** Business and Security services return a consistent JSON body on error: `{ "timestamp", "status", "error" }` (400 Bad Request, 404 Not Found, 500 Internal Server Error).
- **401 Unauthorized:** All API calls (except login/register) require a valid JWT. Run step 1 to get a token and use `-Headers $headers` (with `Authorization: Bearer $token`) in PowerShell, or set the header in Postman.
- **Eureka:** Must be running first. Wait until http://localhost:8761 loads, then start Gateway and the rest.
- **Gateway:** If it does not list in Eureka, check that `eureka.client.service-url.defaultZone=http://localhost:8761/eureka/` is in `api-gateway/src/main/resources/application.properties`.
- **Port in use:** Change the port in that service’s `application.properties` (e.g. `server.port=8086`) and ensure no other app uses the same port.
- **Course/Trainee creation fails:** Ensure Validation Service is up (they call it via Feign). Ensure Trainer Service is up when creating courses (course references `trainerId`).
- **Assessment/Certificate:** Assessment Service calls Validation and Certification. Ensure both Validation and Certification services are up when creating assessments or evaluating submissions (certificate is issued on pass).
- **Database:** By default, Trainer/Course/Trainee use **H2** (file in `./data/`). No MySQL needed. For MySQL, use profile `mysql` and configure `application-mysql.properties` (create DBs `trainer_db`, `course_db`, `trainee_db` if needed).
- **Docker:** If a service fails to start, run `docker compose logs <service-name>` (e.g. `docker compose logs api-gateway`). Ensure Eureka is healthy before Gateway/Security; business services wait for MySQL and Eureka. First run may take a few minutes while images build.

---

## Stopping

**Local run:** Stop each service with **Ctrl+C** in its terminal. Stopping Eureka last is fine; clients will just lose discovery until you start Eureka again.

**Docker:** Run `docker compose down`. Use `docker compose down -v` to also remove the MySQL data volume.
