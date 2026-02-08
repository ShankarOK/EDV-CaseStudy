# Skill Development & Training Management System

Microservices-based solution for training institutes and corporate learning: course catalogs, trainee profiles, assignments, assessments, and certifications. This repo is aligned with the **original project description** (Overview, Technology Stack, Microservices, Frontend UI, Inter-service communication, Security, Deployment).

---

## Project description compliance

**Done (per description):**

- **Technology stack:** Microservices, Eureka, Spring Cloud Gateway, Java/Spring Boot, Feign, Thymeleaf, MySQL (primary for deployment), Security Service (JWT), Global Exception Handling, Validation Service.
- **All 7 microservices:** Course, Trainee, Trainer, Assessment, Certification, Validation, Security — with stated responsibilities and integrations.
- **Frontend UI:** Home; Admin (Dashboard, Register Course, Manage Courses, Assign Trainer); Trainer (Dashboard, Create/Evaluate Assessment, View Results, Feedback); Trainee (Dashboard, View Courses, Enroll, Assessment, Results, Certificate, Feedback); Profile (all roles: update details, change password).
- **Inter-service communication:** Course→Validation, Trainee→Validation, Assessment→Certification, Gateway→Security as described.
- **Security:** Centralized auth; JWT at Gateway; roles Admin, Trainer, Trainee; access controlled via Gateway + frontend.
- **Deployment:** Docker; Eureka first, then Gateway + Security, then MySQL and business services, then frontend; MySQL via env.

**Not required by description (optional improvements):**

- Course Service: verify trainer exists on create.
- Trainee Service: enrollment eligibility via Validation.
- Validation Service: optional `/validate/feedback` (feedback rules currently in Assessment Service).
- Backend per-endpoint role checks (enforcement is at Gateway + frontend).

**Full audit (what’s right, what’s wrong, how to correct):** **PROJECT_STATUS.md**.

---

## Database Configuration Strategy

As specified in the project description, **MySQL is the primary database for deployment.** For faster local development and testing, **H2 is used in the default profile.** All services use identical JPA entities and schema definitions, ensuring full compatibility between H2 and MySQL. Docker-based deployment always runs with MySQL using environment-based configuration.

- **Local development (default):** No profile or `spring.profiles.active` unset → `application.properties` → H2 file-based databases per service. No MySQL required.
- **Deployment / Docker:** Profile `mysql` is always activated; `application-mysql.properties` is used; datasource URL, username, and password are supplied only via environment variables (no hardcoded credentials). Tables are created in MySQL on first run via `spring.jpa.hibernate.ddl-auto=update`.
- **Schema consistency:** The same JPA entities and `ddl-auto=update` are used in both H2 and MySQL profiles, so the schema is identical in local H2 and Dockerized MySQL.
- **Local run with MySQL:** Set environment variables `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD` and start the service with `spring.profiles.active=mysql`.

---

## Database Ownership & Integrity

Each microservice owns its database schema. Cross-service relationships (e.g. trainee–course, assessment–certification) are enforced at the application and validation layer, not via database foreign keys. There are no cross-database joins, no shared schemas, and no foreign keys across services.

---

## How to Run

- **Docker (recommended):** From project root run `docker compose up -d`. Frontend: http://localhost:9090 | Gateway: http://localhost:8080 | Eureka: http://localhost:8761  
- **Local:** See **HOW_TO_RUN.md** for service order and commands.

On first run with an empty database, **seed data** is created automatically: 2 trainers, 1 course, and 1 trainee so you can create courses, assign trainers, and enroll without manual setup.

---

## Project layout

- **BUILD_PLAN.md** — Phases, execution order, remaining work from description.  
- **HOW_TO_RUN.md** — Run order (Docker + local), quick tests, troubleshooting.  
- **PROJECT_STATUS.md** — **Spec audit:** for each part of the description, what’s right, what’s wrong, how to correct or implement.  

**Services:** `eureka-server`, `api-gateway`, `security-service`, `validation-service`, `trainer-service`, `course-service`, `trainee-service`, `assessment-service`, `certification-service`, `frontend-app`.
