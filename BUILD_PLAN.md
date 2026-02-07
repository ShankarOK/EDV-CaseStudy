# Skill Development & Training Management System — Build Plan

This document defines the order and scope for building each microservice so that dependencies are ready when needed and each step is testable.

---

## Principles

- **Build in dependency order**: Registry → Gateway → Security → Validation → domain services → frontend.
- **One service at a time**: Finish and verify each before moving to the next.
- **Incremental integration**: Each new service registers with Eureka and (where applicable) uses Gateway and Security.
- **Database per service**: Each business service gets its own schema/DB configuration (can share one MySQL instance with different DBs/schemas).

---

## Phase 1: Infrastructure (Discovery, Routing, Security)

Services in this phase have no business domain; they enable the rest of the system.

### 1.1 Eureka Server

**Purpose:** Service registry so all other services can register and be discovered.

**Deliverables:**
- Add `spring-cloud-starter-netflix-eureka-server` (with Spring Cloud BOM compatible with Boot 3.5.x).
- Main class: `@EnableEurekaServer`.
- Config: `server.port=8761`, `eureka.client.register-with-eureka=false`, `eureka.client.fetch-registry=false`.
- Run and confirm dashboard at `http://localhost:8761`.

**Exit criteria:** Eureka starts; dashboard is reachable; no clients yet.

---

### 1.2 API Gateway

**Purpose:** Single entry point; route requests to services by name via Eureka; later integrate with Security for token validation.

**Deliverables:**
- Add Spring Cloud Gateway + Eureka client dependencies.
- Config: `server.port=8080`, Eureka `defaultZone=http://localhost:8761/eureka/`.
- Route definitions (path → service-id) for each future service (e.g. `/api/courses/**` → `course-service`). Routes can be placeholders that 404 until services exist.
- Run and confirm Gateway starts and registers with Eureka.

**Exit criteria:** Gateway registered in Eureka; routes configured; downstream 404s are acceptable until services exist.

---

### 1.3 Security Service

**Purpose:** Central auth: login, JWT issue/validate, user and role management (Admin, Trainer, Trainee).

**Deliverables:**
- Add `spring-boot-starter-web`, `spring-boot-starter-security`, JWT library (e.g. jjwt), Eureka client.
- Config: port (e.g. `8081`), Eureka registration, JWT secret and expiry.
- User entity/repository (in-memory or MySQL): username, password (encoded), role.
- Endpoints: `POST /auth/login` (returns JWT), optional `POST /auth/register` (for trainee self-registration if in scope), token validation endpoint or filter for Gateway.
- Role-based authority (Admin, Trainer, Trainee).
- Run and confirm registration with Eureka; login returns a valid JWT.

**Exit criteria:** Security Service registered; login returns JWT; roles present in token; ready for Gateway to validate tokens.

**Optional (can be Phase 1.4):** Gateway filter that validates JWT via Security Service (or shared secret) and forwards user/roles to downstream services. If deferred, add a simple “health” or “public” route so Gateway is still usable.

---

## Phase 2: Validation & Core Domain Services

These services hold core data and call Validation for rules.

### 2.1 Validation Service

**Purpose:** Centralized business and data validation used by Course, Trainee, Trainer, Assessment, Certification.

**Deliverables:**
- Add `spring-boot-starter-web`, Eureka client.
- Config: port, Eureka registration.
- Validation endpoints (or single generic endpoint) for:
  - Course: duration, dates, trainer assignment.
  - Trainee: unique email/contact, enrollment eligibility.
  - Trainer: expertise vs course category, availability.
  - Assessment: passing criteria, score ranges.
  - Certification: completion and eligibility.
- Implementation can start with simple rule logic (e.g. duration > 0, dates valid); no DB required for rules initially.
- Run and confirm registered in Eureka; Gateway route returns 200 for a sample validation call.

**Exit criteria:** Validation Service registered; at least one validation endpoint callable via Gateway; ready for other services to call via Feign.

---

### 2.2 Trainer Service

**Purpose:** Trainer profiles, specialization, availability; used by Course for assignment.

**Deliverables:**
- Add `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, Eureka client, MySQL driver, Feign (optional here; Course will call Trainer).
- Config: port (e.g. `8082`), Eureka, datasource (e.g. `trainer_db` or schema).
- Entity: Trainer (id, name, specialization, experience, availability, etc.).
- Repository, service, REST controller: CRUD for trainers.
- Secure endpoints (Admin, Trainer) via Gateway + JWT (Security); can use a simple “role” header or shared validation in Gateway.
- Run and confirm registered; CRUD works via Gateway with valid JWT.

**Exit criteria:** Trainer CRUD works through Gateway; Eureka shows `trainer-service`; DB persists data.

---

### 2.3 Course Service

**Purpose:** Course catalog; trainer assignment; integrates with Trainer and Validation.

**Deliverables:**
- Add `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, Eureka client, MySQL driver, OpenFeign.
- Config: port (e.g. `8083`), Eureka, datasource (e.g. `course_db`), Feign client for Trainer Service and Validation Service.
- Entity: Course (id, title, category, duration, description, start/end dates, trainerId or similar).
- Feign clients: get trainer by id; call Validation for course rules (duration, dates, trainer).
- Repository, service, REST controller: CRUD; on create/update call Validation (and optionally Trainer) via Feign.
- Access: Admin, Trainer only (enforced via Gateway/Security).
- Run and confirm CRUD and validation integration via Gateway.

**Exit criteria:** Course CRUD works; Validation and Trainer calls work via Feign; service registered.

---

### 2.4 Trainee Service

**Purpose:** Trainee profiles, enrollment; integrates with Validation (and later Assessment/Certification).

**Deliverables:**
- Add `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, Eureka client, MySQL driver, OpenFeign.
- Config: port (e.g. `8084`), Eureka, datasource (e.g. `trainee_db`), Feign client for Validation Service.
- Entity: Trainee (id, name, contact, email, qualification, skill preferences, etc.).
- Repository, service, REST controller: CRUD; on create/update call Validation (e.g. unique email/contact).
- Endpoints for “enroll in course” (can store enrollment in Trainee DB or a separate table; Course enrollment might involve Course Service later).
- Access: Admin, Trainee (self), or as defined by spec.
- Run and confirm registration and Validation integration via Gateway.

**Exit criteria:** Trainee CRUD and validation work; enrollment structure in place; ready for Assessment/Certification integration.

---

## Phase 3: Assessment & Certification

These depend on Course, Trainee, Trainer, and Validation.

### 3.1 Assessment Service

**Purpose:** Assessments, question bank, scoring, trainee results; triggers certification on success.

**Deliverables:**
- Add `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, Eureka client, MySQL driver, OpenFeign.
- Config: port (e.g. `8085`), Eureka, datasource (e.g. `assessment_db`), Feign to Validation, Certification, optionally Security.
- Entities: Assessment, Question, TraineeResult/Submission, etc.
- Validation: scoring rules, completion criteria via Validation Service.
- Endpoints: create/schedule assessment (Trainer/Admin), submit answers (Trainee), evaluate (Trainer), get results; on passing, call Certification Service to trigger certificate.
- Access: role-based via Gateway/Security.
- Run and confirm integration with Validation and Certification (e.g. “issue certificate” call).

**Exit criteria:** Assessments CRUD and results; validation and certification trigger work; registered in Eureka.

---

### 3.2 Certification Service

**Purpose:** Issue and store certificates; verify completion/eligibility via Assessment and Validation.

**Deliverables:**
- Add `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, Eureka client, MySQL driver, OpenFeign.
- Config: port (e.g. `8086`), Eureka, datasource (e.g. `certification_db`), Feign to Assessment, Validation.
- Entity: Certificate (certificateId, traineeId, courseName, issueDate, validity, etc.).
- Endpoints: generate certificate (called by Assessment or internal flow), get/download certificate by trainee/course, list certificates.
- Validation: eligibility via Validation Service; completion/passing from Assessment Service.
- Access: Trainee (own certificates), Admin; enforced via Gateway/Security.
- Run and confirm certificate generation and retrieval via Gateway.

**Exit criteria:** Certificates generated and stored; download/view works; integrated with Assessment and Validation.

---

## Phase 4: Frontend (Thymeleaf)

**Purpose:** UI for Admin, Trainer, Trainee; all calls go through Gateway with JWT.

### 4.1 Frontend App

**Deliverables:**
- Add `spring-boot-starter-web`, Thymeleaf, Eureka client (optional; can call Gateway by fixed URL), HTTP client (RestTemplate or WebClient) to call Gateway.
- Config: port (e.g. `9090`), Gateway base URL (e.g. `http://localhost:8080`), login URL to Security Service (via Gateway).
- Controllers and pages:
  - **Home:** Landing with role-based links (Admin / Trainer / Trainee).
  - **Auth:** Login form → POST to Gateway → Security → store JWT (session/cookie); logout.
  - **Admin:** Dashboard (aggregate from Course, Trainee, Trainer), Register/Manage Courses, Assign Trainer.
  - **Trainer:** Dashboard, Create/Evaluate Assessments, View Results, Feedback.
  - **Trainee:** Dashboard, View/Enroll Courses, Take Assessment, View Results, Certificates, Feedback.
  - **Profile:** Update details, change password (via Security).
- All server-side calls: add JWT (or session token) to requests to Gateway; Gateway validates and routes.
- Run and confirm: login as each role, navigate to key pages, and see data from backend services via Gateway.

**Exit criteria:** All spec’d pages exist; login and role-based access work; data loads from microservices through Gateway.

---

## Phase 5: Integration, Security Hardening & Deployment (Optional)

- **Gateway + Security:** Ensure every route (except login/public) requires valid JWT and correct role where needed.
- **Global exception handling:** Per-service or Gateway-level error handling and consistent error payloads.
- **Docker:** Dockerfile per service; `docker-compose` with Eureka, Gateway, Security, MySQL, all business services, frontend; start order as in deployment doc (Eureka first, then Gateway + Security, then rest).
- **Health checks:** Eureka health; optional Spring Boot Actuator health endpoints for each service.

---

## Build Order Summary

| Order | Service           | Phase   | Depends on                          |
|-------|-------------------|---------|-------------------------------------|
| 1     | Eureka Server     | 1       | —                                   |
| 2     | API Gateway       | 1       | Eureka                              |
| 3     | Security Service  | 1       | Eureka                              |
| 4     | Validation Service| 2       | Eureka, Gateway (for routing)       |
| 5     | Trainer Service   | 2       | Eureka, Gateway, Security, Validation |
| 6     | Course Service    | 2       | Eureka, Gateway, Security, Validation, Trainer |
| 7     | Trainee Service   | 2       | Eureka, Gateway, Security, Validation |
| 8     | Assessment Service| 3       | Eureka, Gateway, Security, Validation, Certification (optional at start) |
| 9     | Certification Service | 3   | Eureka, Gateway, Security, Validation, Assessment |
| 10    | Frontend App      | 4       | Eureka, Gateway, Security, all business services |

---

## Per-Service Checklist (Template)

For each service, use this list:

- [ ] Dependencies added in `pom.xml` (web, JPA, Eureka, Feign, MySQL, etc.).
- [ ] `application.properties`: port, `spring.application.name`, Eureka URL, DB URL (if applicable).
- [ ] Main class: `@SpringBootApplication`; for Eureka Server add `@EnableEurekaServer`.
- [ ] Entities, repositories, services, controllers (as per spec).
- [ ] Feign clients (if caller): interface + `@EnableFeignClients`, config.
- [ ] Security: ensure endpoints are called with JWT via Gateway; no duplicate auth in business services unless required.
- [ ] Manual test: start Eureka (and dependencies), start service, check Eureka dashboard, hit one endpoint via Gateway.
- [ ] Optional: unit/integration tests.

Start with **1.1 Eureka Server** and proceed in order. After each service, run the system up to that point and verify before moving on.
