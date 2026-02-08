# Skill Development & Training Management System — Build Plan

This document defines the plan to **complete** the project to full compliance with the official **Project Description**. The system implements a microservices-based solution for training institutes and corporate learning: course catalogs, trainee profiles, assignments, assessments, certifications. It ensures transparent operations, learning standards, efficient course delivery, and accurate record-keeping.

**Scope:** Implement only what is given in the project description — nothing less.

**Constraints:** Do not redesign the architecture. Do not remove existing logic. Complete missing spec-required features with minimal, clean, production-style changes.

---

## Project Description (Summary)

- **Technology Stack:** Microservices; Eureka Server (registry); Spring Cloud Gateway (API Gateway); Java, Spring Boot; Feign Client; Thymeleaf (frontend); MySQL (database); Security Service (Spring Security, JWT); Global Exception Handling; Validation Service.
- **Microservices:** Course, Trainee, Trainer, Assessment, Certification, Validation, Security. Each has defined responsibilities (register/manage entities, integrate with Validation/other services, access controlled via Security).
- **Frontend (Thymeleaf):** Home; Admin (Dashboard, Register Course, Manage Courses, Assign Trainer); Trainer (Dashboard, Create Assessment, Evaluate Assessment, View Results, Feedback Management); Trainee (Dashboard, View Courses, Enroll, Assessment, Results, Certificate, Feedback); Profile (all roles — update details, change password).
- **Security:** Centralized auth at API Gateway → Security Service (JWT validation). Roles: Admin, Trainer, Trainee. Access controlled per role.
- **Deployment:** Docker containers — Eureka first, then Gateway + Security, then MySQL and business services, then Frontend; shared network; validation per deployment document.

---

## Spec compliance summary (vs original description)

| Area | Status | Details |
|------|--------|--------|
| Overview, technology stack, all 7 microservices | ✅ Done | Eureka, Gateway, Security, Validation, Course, Trainee, Trainer, Assessment, Certification as described. MySQL primary for deployment; H2 dev-only. |
| Frontend UI pages (Home, Admin, Trainer, Trainee, Profile) | ✅ Done | All pages per description; session-based traineeId/trainerId; feedback; certificate PDF; Profile update/change password forms. |
| Profile Page (all roles) | ✅ Done | Update details and change password forms wired to PUT /auth/profile and PUT /auth/password. |
| Inter-service communication, Security, Deployment | ✅ Done | As per description. |

**Full audit (what’s right, what’s wrong, how to correct):** **PROJECT_STATUS.md**.

---

## Objective

Bring the project to full compliance with the official project description, focusing on:

- **Centralized security enforcement** (Gateway JWT validation)
- **Proper user–entity mapping** (traineeId / trainerId from auth)
- **Profile & password management** (Security Service APIs)
- **Feedback backend** (persistence and APIs for existing UI)
- **Certificate PDF download** (generate and return PDF)
- **Global exception handling** (standardized error responses)
- **Docker-based deployment** (containerize full system)

---

## Phase 1 — API Gateway: JWT Enforcement (CRITICAL) ✅ DONE

**Goal:** Ensure all `/api/**` routes are protected by JWT validation via the Security Service.

### Tasks

1. Implement a **GlobalFilter** in Spring Cloud Gateway. ✅
2. **Skip** authentication for:
   - `/api/auth/login` ✅
   - `/api/auth/register` (if present) ✅
3. For all other `/api/**` requests:
   - Read `Authorization: Bearer <token>` ✅
   - Call `POST /auth/validate` on Security Service ✅
   - If invalid or missing → return **401 Unauthorized** ✅
   - If valid → forward request ✅

### Constraints

- Use **WebClient** (Gateway is reactive). ✅
- Do not block threads. ✅
- Do not change existing route definitions. ✅

### Exit criteria

- Unauthenticated requests to `/api/courses`, `/api/trainees`, etc. return 401. ✅
- Requests with valid `Authorization: Bearer <token>` are forwarded and succeed. ✅
- Login and (if present) register remain publicly accessible. ✅

### Files changed (implemented)

- `api-gateway/pom.xml` — added `spring-boot-starter-webflux` for WebClient.
- `api-gateway/src/main/java/com/skilldev/gateway/filter/JwtAuthGlobalFilter.java` — GlobalFilter with `Ordered.HIGHEST_PRECEDENCE`; skips non-`/api/` and `/api/auth/login`, `/api/auth/register`; validates token via client; returns 401 otherwise.
- `api-gateway/src/main/java/com/skilldev/gateway/filter/JwtValidationClient.java` — reactive client that calls `POST {security-service-url}/auth/validate` with `Authorization` header.
- `api-gateway/src/main/resources/application.properties` — added `app.security-service-url=http://localhost:8081`.

---

## Phase 2 — Security Service: User–Entity Mapping + Profile APIs ✅ DONE

**Goal:** Eliminate hardcoded traineeId/trainerId in frontend; support profile and password management.

### Tasks

**1. Add `GET /auth/me`** ✅

- Extract username and role from JWT (from `Authorization` header or dedicated filter).
- Return JSON: `{ "username", "role", "entityId", "displayName", "email" }`.
- `entityId` from application properties mapping (`app.user-entities.trainee`, `.trainer`, `.admin`).

**2. Add Change Password API** ✅

- Endpoint: `PUT /auth/password`; body `{ "currentPassword", "newPassword" }`.
- Validate current password, encode and store new password via `UserDetailsManager.updateUser()`.
- Return 204 on success; 401 if token invalid or current password wrong; 400 if new password blank.

**3. (Optional) Profile Update** ✅

- `PUT /auth/profile` with body `{ "displayName", "email" }`; in-memory `ProfileStore`; returned in `GET /auth/me`.

### Exit criteria

- `GET /api/auth/me` with valid JWT returns username, role, entityId (and optional displayName, email). ✅
- `PUT /api/auth/password` with valid credentials updates password; invalid current password returns 401. ✅
- Frontend can use entityId for trainee/trainer flows (Phase 3 will consume this). ✅

### Files changed (implemented)

- `security-service/src/main/resources/application.properties` — added `app.user-entities.trainee=1`, `.trainer=1`, `.admin=0`.
- `security-service/.../dto/MeResponse.java` — record (username, role, entityId, displayName, email).
- `security-service/.../dto/PasswordRequest.java` — record (currentPassword, newPassword).
- `security-service/.../dto/ProfileRequest.java` — record (displayName, email).
- `security-service/.../config/UserEntityMapping.java` — component reading properties, `getEntityId(username)`.
- `security-service/.../service/ProfileStore.java` — in-memory map for displayName/email per username.
- `security-service/.../config/SecurityConfig.java` — bean return type `UserDetailsManager` (was `UserDetailsService`) for password update.
- `security-service/.../controller/AuthController.java` — `GET /auth/me`, `PUT /auth/password`, `PUT /auth/profile`; `usernameFromToken()` helper.

---

## Phase 3 — Frontend: Remove Hardcoded IDs ✅ DONE

**Goal:** Use authenticated identity instead of constants (traineeId=1, trainerId=1).

### Tasks

1. **After login:** call `GET /api/auth/me` (with Bearer token). ✅
2. **Store entityId in session:** `traineeId` for TRAINEE, `trainerId` for TRAINER. ✅
3. **Replace all hardcoded IDs** in enrollment, assessment submit, results, certificates, assessment create, evaluate. ✅

### Constraints

- Do not expose entityId in URLs unnecessarily. ✅
- Prefer session-based access. ✅

### Exit criteria

- Trainee flows use session traineeId; trainer flows use session trainerId. ✅
- No hardcoded `1` for trainee or trainer identity in forms or API paths. ✅

### Files changed (implemented)

- `frontend-app/.../web/AuthInterceptor.java` — added `SESSION_TRAINEE_ID`, `SESSION_TRAINER_ID`.
- `frontend-app/.../client/GatewayApiService.java` — added `MeResponse` record (username, role, entityId, displayName, email).
- `frontend-app/.../controller/AuthController.java` — after login, call `gatewayApi.get(session, "/auth/me", MeResponse.class)` and set `SESSION_TRAINEE_ID` or `SESSION_TRAINER_ID` by role.
- `frontend-app/.../controller/TraineeController.java` — all trainee endpoints require session traineeId; redirect to login if null; enroll POST takes only `courseId` (traineeId from session); dashboard/results/certificates use session traineeId in API paths; submit assessment uses session traineeId.
- `frontend-app/.../controller/TrainerController.java` — all trainer endpoints require session trainerId; redirect to login if null; create assessment uses session trainerId for `createdByTrainerId`; evaluate uses session trainerId (removed from form).
- `frontend-app/.../templates/trainee/enroll.html` — removed Trainee ID field.
- `frontend-app/.../templates/trainee/take-assessment.html` — removed hidden `traineeId` input.
- `frontend-app/.../templates/trainer/submissions.html` — removed hidden `trainerId` input.

---

## Phase 4 — Feedback Backend ✅ DONE

**Goal:** Implement feedback persistence matching existing UI (Feedback Management for Trainer, Feedback for Trainee).

### Tasks

1. **Create Feedback entity** with id, traineeId, trainerId, courseId, rating (1–5), comment, createdAt. ✅
2. **Create REST APIs:** `POST /feedback`, `GET /feedback/trainee/{id}`, `GET /feedback/trainer/{id}`. ✅
3. **Validate input:** rating 1–5, non-empty comment in FeedbackService. ✅
4. **Integrate with Thymeleaf:** Trainee submit form and list; Trainer list. ✅

### Constraints

- Added to **Assessment Service**; new Gateway route `/api/feedback/**` → assessment-service. ✅

### Exit criteria

- Feedback can be submitted and retrieved by trainee and trainer. ✅

### Files changed (implemented)

- `assessment-service`: `entity/Feedback.java`, `repository/FeedbackRepository.java`, `service/FeedbackService.java`, `controller/FeedbackController.java`.
- `api-gateway/application.properties`: route 7 = `/api/feedback/**` → assessment-service (route 8 = frontend-app).
- `frontend-app`: `TraineeController` (GET/POST feedback, load list; submit form with rating, comment, optional trainerId/courseId), `TrainerController` (load feedback list); `trainee/feedback.html` (submit form + table), `trainer/feedback.html` (table).

---

## Phase 5 — Certification PDF Download ✅ DONE

**Goal:** Allow trainees to download certificates as PDF.

### Tasks

1. **Add endpoint:** `GET /certificates/{id}/download`
2. **Generate PDF dynamically** using OpenPDF or iText:
   - Include: certificate code, trainee ID, course name, issue date, validity.
3. **Return** `Content-Type: application/pdf` (and optionally `Content-Disposition: attachment`).

### Constraints

- Do not store PDFs on disk; generate in-memory.

### Exit criteria

- Request to `GET /api/certificates/{id}/download` with valid id returns PDF.
- Frontend Certificate page has a “Download” link that uses this endpoint.

### Files to change

- **Done:** `certification-service`: OpenPDF, `CertificateService.generatePdf()`, `GET /certificates/{id}/download`; `frontend-app`: `getForDownload()`, trainee proxy endpoint, Download PDF link in certificates.html.

---

## Phase 6 — Global Exception Handling ✅ DONE

**Goal:** Standardize error responses across all services.

### Tasks

1. Add **`@RestControllerAdvice`** in each service (Eureka and Gateway optional). ✅
2. **Handle:** `IllegalArgumentException` → 400, `NoSuchElementException` → 404, `Exception` → 500. ✅
3. **Response format:** `{ "timestamp", "status", "error" }` (ISO-8601 timestamp). ✅

### Constraints

- Do not change existing success response shapes or status codes for valid cases. ✅

### Exit criteria

- Invalid or not-found requests return consistent JSON error body with status 400/404/500. ✅

### Files changed (implemented)

- **security-service:** `exception/ApiError.java` (record: timestamp, status, error), `exception/GlobalExceptionHandler.java` (@RestControllerAdvice, 3 @ExceptionHandler).
- **validation-service:** same (exception/ApiError, GlobalExceptionHandler).
- **trainer-service:** same.
- **course-service:** same.
- **trainee-service:** same.
- **assessment-service:** same.
- **certification-service:** same.
- Eureka and API Gateway: not modified (optional per plan).

---

## Phase 7 — Docker Deployment ✅ DONE

**Goal:** Containerize the full system per deployment document.

### Tasks

1. **Add Dockerfile** for: ✅
   - Eureka Server, API Gateway, Security Service, Validation Service
   - Trainer Service, Course Service, Trainee Service, Assessment Service, Certification Service
   - Frontend App

2. **Add `docker-compose.yml`:** ✅
   - Shared network `skilldev-net` for all containers.
   - **MySQL** container (image `mysql:8.0`, port 3306, volume `mysql_data`); each business service uses `createDatabaseIfNotExist=true` for its DB (`trainer_db`, `course_db`, `trainee_db`, `assessment_db`, `certification_db`).
   - **Startup order:** MySQL and Eureka start first; Eureka has healthcheck (wget to :8761); Gateway and Security depend on Eureka healthy; Validation depends on Eureka; Trainer/Course/Trainee/Assessment/Certification depend on MySQL + Eureka healthy; Frontend depends on Gateway.
   - **Environment-based config:** `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`, `APP_SECURITY_SERVICE_URL`, `APP_GATEWAY_URL`, `SPRING_PROFILES_ACTIVE=mysql`, `SPRING_DATASOURCE_URL` (host `mysql`), etc.

### Constraints

- Use **multi-stage builds** (Maven build stage, then JRE run stage). ✅
- No Kubernetes required. ✅

### Exit criteria

- `docker compose up -d` (or `docker-compose up -d`) brings up the full stack. ✅
- Frontend: http://localhost:9090 | Gateway: http://localhost:8080 | Eureka: http://localhost:8761. Login and key flows work. ✅

### Files added

- **Dockerfiles:** `eureka-server/Dockerfile`, `api-gateway/Dockerfile`, `security-service/Dockerfile`, `validation-service/Dockerfile`, `trainer-service/Dockerfile`, `course-service/Dockerfile`, `trainee-service/Dockerfile`, `assessment-service/Dockerfile`, `certification-service/Dockerfile`, `frontend-app/Dockerfile` (multi-stage: `maven:3.9-eclipse-temurin-17-alpine` → `eclipse-temurin:17-jre-alpine`; Eureka run stage adds `wget` for healthcheck).
- **Project root:** `docker-compose.yml` (services, healthchecks, env, network `skilldev-net`, volume `mysql_data`).

---

## Important Rules

- **Do NOT** break existing APIs.
- **Do NOT** rename services or endpoints.
- **Keep changes incremental** — one phase at a time, verify before moving on.
- **Prefer clarity over cleverness.**
- **Follow Spring Boot best practices.**

---

## Execution Order

| Order | Phase | Depends on | Status |
|-------|--------|------------|--------|
| 1 | Phase 1 — API Gateway JWT | Existing Gateway, Security | ✅ Done |
| 2 | Phase 2 — Security /auth/me + password | Existing Security | ✅ Done |
| 3 | Phase 3 — Frontend remove hardcoded IDs | Phase 1, Phase 2 | ✅ Done |
| 4 | Phase 4 — Feedback backend | Existing services, Gateway route if new service | ✅ Done |
| 5 | Phase 5 — Certificate PDF download | Existing Certification Service | ✅ Done |
| 6 | Phase 6 — Global exception handling | Any; can be done per service in parallel | ✅ Done |
| 7 | Phase 7 — Docker deployment | All services stable | ✅ Done |

---

## Reference: Initial System Build Order (Already Done)

The following order was used to build the system initially; use for local run order and for Docker dependencies:

| Order | Service | Port |
|-------|---------|------|
| 1 | Eureka Server | 8761 |
| 2 | API Gateway | 8080 |
| 3 | Security Service | 8081 |
| 4 | Validation Service | 8082 |
| 5 | Trainer Service | 8083 |
| 6 | Course Service | 8084 |
| 7 | Trainee Service | 8085 |
| 8 | Assessment Service | 8086 |
| 9 | Certification Service | 8087 |
| 10 | Frontend App | 9090 |

See **HOW_TO_RUN.md** for exact commands and quick tests.

---

## Remaining work from description

**All description-required items are complete.** Profile Page now has update-details and change-password forms (POST /profile/update and POST /profile/password → Gateway PUT /auth/profile and PUT /auth/password).

Optional (not required by description):

- Course Service: verify trainer exists on create (PROJECT_STATUS §2.2).
- Trainee Service: enrollment eligibility via Validation (PROJECT_STATUS §2.2).
- Validation Service: optional `POST /validate/feedback` (PROJECT_STATUS §2.1).
