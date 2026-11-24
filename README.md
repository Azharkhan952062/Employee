# Employee Management System (EMS) - HR Cloud Application

[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot 3.2.5](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Ready-blue.svg)](https://www.postgresql.org/)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3-green.svg)](https://www.thymeleaf.org/)
[![Docker](https://img.shields.io/badge/Docker-Multi--Stage-2496ED.svg)](https://www.docker.com/)
[![Render](https://img.shields.io/badge/Render-Deployable-46E3B7.svg)](https://render.com/)

A complete, production-grade, full-stack **Employee Management System** built with **Java 17**, **Spring Boot 3**, **Spring Data JPA**, **Hibernate**, **Thymeleaf**, **HTML5/CSS3/JavaScript**, and **PostgreSQL**.

Designed specifically as an interview showcase piece demonstrating senior backend architecture, enterprise database design, robust exception handling, Bean Validation, dual Web/REST APIs, and production deployment automation.

---

## 📑 Table of Contents
- [Project Overview](#-project-overview)
- [Key Features](#-key-features)
- [Technology Stack](#-technology-stack)
- [Application Architecture](#-application-architecture)
- [Folder Structure](#-folder-structure)
- [UI & Dashboard Showcase](#-ui--dashboard-showcase)
- [Database Configuration](#-database-configuration)
- [Environment Variables](#-environment-variables)
- [Local Setup & Running](#-local-setup--running)
- [Maven Commands](#-maven-commands)
- [REST API Reference](#-rest-api-reference)
- [Automated Testing](#-automated-testing)
- [Deploying to Render](#-deploying-to-render)
- [Future Improvements](#-future-improvements)

---

## 🌟 Project Overview

The Employee Management System is a centralized HR management platform that streamlines workforce operations. It empowers HR administrators and engineering managers to onboard employees, manage departmental allocations, track payroll metrics, filter and search talent, and analyze workforce distributions via live database aggregations.

### Why this is portfolio & interview ready:
1. **Clean Layered Architecture**: Strictly decouples Web Controllers, Service Layer, Repositories, DTOs, and JPA Entities.
2. **Dual-Channel Interface**: Full Thymeleaf-rendered server-side MVC frontend alongside a comprehensive JSON REST API for third-party integrations.
3. **Database-Driven Metrics**: Zero mock data. Dashboard statistics (headcount, active/inactive ratios, average salary, monthly payroll, department percentages) are dynamically computed via SQL/JPA aggregate queries.
4. **Cloud-Native Deployment**: Production-ready with multi-stage Docker build, container memory optimizations, automatic Render PostgreSQL URL parsing, and Render Blueprint (`render.yaml`).
5. **Comprehensive Test Suite**: Unit and integration tests covering CRUD operations, edge cases, validation rules, and error handling with 100% test pass rate.

---

## ⚡ Key Features

- **Full Employee CRUD Operations**:
  - Add employees with strict validation (unique email, positive salary, past/present joining date, regex phone verification).
  - View detailed employee profiles with tenure, departmental tags, and contact links.
  - Edit employee records with duplicate email protection across existing records.
  - Soft or hard operations: toggle active/inactive status in one click, or delete with an interactive confirmation modal.
- **Modern HR Dashboard**:
  - High-level KPI summary cards: Total Headcount, Active Staff, Inactive Staff, Average Annual Salary, and Monthly Payroll.
  - Interactive **Chart.js** analytics:
    - Department Headcount Distribution (Doughnut Chart)
    - Average Salary by Department (Bar Chart)
  - Detailed departmental breakdown table showing headcount, mean salary, total budget, and percentage share.
  - Recent hires feed with direct profile access.
- **Advanced Search, Filtering & Pagination**:
  - Case-insensitive search across first name, last name, work email, and designation.
  - Department filtering (Engineering, HR, Marketing, Finance, Sales, Operations, Product, Legal).
  - Status filtering (Active / Inactive).
  - Multi-column sorting (Joining Date, Name, Salary, Department) in ascending or descending order.
  - Server-side pagination with active page indicators.
- **Enterprise UI / UX**:
  - Responsive sidebar navigation with mobile drawer support.
  - Clean typography and professional slate/indigo color palette.
  - Toast alerts with auto-dismiss animations for success/error notifications.
  - Delete confirmation modal preventing accidental data loss.
- **Centralized Exception Handling**:
  - Custom exceptions (`ResourceNotFoundException`, `DuplicateResourceException`).
  - `@ControllerAdvice` (`GlobalExceptionHandler`) delivering formatted JSON for API callers and styled flash messages/views for web users.

---

## 🛠️ Technology Stack

| Layer | Technologies |
|---|---|
| **Backend Framework** | Java 17, Spring Boot 3.2.5, Spring MVC, Spring Data JPA, Hibernate ORM |
| **Validation** | Jakarta Bean Validation (Hibernate Validator) |
| **Database** | PostgreSQL (Production), H2 in PostgreSQL compatibility mode (Local Dev) |
| **Frontend** | Thymeleaf 3, HTML5, Modern CSS3 (CSS Custom Properties, Flexbox, Grid), JavaScript (Vanilla ES6) |
| **Data Visualization** | Chart.js 4.4.1 |
| **Testing** | JUnit 5, Mockito 5, AssertJ, Spring MockMvc |
| **Build & Tooling** | Apache Maven 3.9+, Maven Wrapper (`mvnw`, `mvnw.cmd`) |
| **Containerization & Cloud** | Docker (Multi-stage build), Render Web Service, Render Managed PostgreSQL |

---

## 🏗️ Application Architecture

The application adheres to clean architecture principles and separation of concerns:

```
[ Web Browser (Thymeleaf) ]     [ REST Clients / Mobile ]
            \                                /
             \                              /
        [ EmployeeController ]     [ EmployeeRestController ]
             (Spring MVC)                (REST API)
                  \                         /
                   \                       /
               [ EmployeeService (Interface) ]
                           |
               [ EmployeeServiceImpl ]
                  (Business Logic)
                     /         \
   [ DTO Mappings / Validation ] [ EmployeeSpecification (Criteria API) ]
                    \           /
               [ EmployeeRepository ]
                (Spring Data JPA)
                        |
               [ Hibernate ORM ]
                        |
        +---------------+---------------+
        |                               |
[ Render PostgreSQL (Prod) ]    [ H2 In-Memory (Dev) ]
```

---

## 📁 Folder Structure

```
employee-management-system/
├── .mvn/wrapper/                  # Maven Wrapper configuration
├── src/
│   ├── main/
│   │   ├── java/com/ems/employeemanagement/
│   │   │   ├── EmployeeManagementApplication.java  # Spring Boot Main Entry
│   │   │   ├── config/
│   │   │   │   ├── DatabaseConfig.java             # Dynamic Render PostgreSQL parser
│   │   │   │   └── DataInitializer.java            # Seed data for production demo
│   │   │   ├── controller/
│   │   │   │   ├── EmployeeController.java         # Thymeleaf UI Controller
│   │   │   │   └── EmployeeRestController.java     # REST API Controller
│   │   │   ├── dto/
│   │   │   │   ├── ApiResponse.java                # Standard API response wrapper
│   │   │   │   ├── DashboardStatsDto.java          # HR KPI and metrics DTO
│   │   │   │   ├── DepartmentStatDto.java          # Department breakdown DTO
│   │   │   │   ├── EmployeeRequestDto.java         # Form input & validation DTO
│   │   │   │   └── EmployeeResponseDto.java        # Entity presentation DTO
│   │   │   ├── entity/
│   │   │   │   ├── Department.java                 # Department Enum
│   │   │   │   ├── Employee.java                   # JPA Entity with JPA indexes
│   │   │   │   └── EmployeeStatus.java             # Active / Inactive Status Enum
│   │   │   ├── exception/
│   │   │   │   ├── DuplicateResourceException.java # 409 Conflict exception
│   │   │   │   ├── GlobalExceptionHandler.java     # @ControllerAdvice
│   │   │   │   └── ResourceNotFoundException.java  # 404 Not Found exception
│   │   │   ├── repository/
│   │   │   │   ├── EmployeeRepository.java         # Spring Data JPA + JPQL Aggregates
│   │   │   │   └── EmployeeSpecification.java      # Criteria API dynamic filter
│   │   │   └── service/
│   │   │       ├── EmployeeService.java            # Business Service contract
│   │   │       └── impl/EmployeeServiceImpl.java   # Transactional Implementation
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── css/styles.css                  # Modern HR dashboard stylesheet
│   │       │   └── js/main.js                      # Modal, toast & navigation logic
│   │       ├── templates/
│   │       │   ├── dashboard.html                  # HR Analytics Overview
│   │       │   ├── error.html                      # User-friendly error page
│   │       │   ├── employees/
│   │       │   │   ├── form.html                   # Add/Edit employee form
│   │       │   │   ├── index.html                  # Directory table with filters
│   │       │   │   └── view.html                   # Detailed profile view
│   │       │   └── fragments/layout.html           # Reusable sidebar, topbar, modals
│   │       ├── application.properties              # Local dev configuration (H2)
│   │       └── application-prod.properties         # Production configuration (PostgreSQL)
│   └── test/
│       ├── java/com/ems/employeemanagement/
│       │   ├── controller/
│       │   │   └── EmployeeRestControllerTest.java # MockMvc REST API & validation tests
│       │   └── service/
│       │       └── EmployeeServiceTest.java        # Service layer unit tests
│       └── resources/
│           └── mockito-extensions/
│               └── org.mockito.plugins.MockMaker   # Subclass mock maker configuration
├── .dockerignore
├── .gitignore
├── Dockerfile                     # Multi-stage production build
├── pom.xml                        # Maven dependencies & plugins
├── render.yaml                    # Render Infrastructure as Code Blueprint
├── system.properties              # Java runtime specification
└── README.md
```

---

## 📊 UI & Dashboard Showcase

```
+------------------------------------------------------------------------------------+
|  [HR] WorkForce Cloud                                            [+ New Employee]  |
+------------------------------------------------------------------------------------+
| [Dashboard]     TOTAL STAFF   ACTIVE STAFF   INACTIVE   AVG SALARY   MONTHLY BURN  |
| [Employees]         16            15            1        $123,000      $153,750    |
| [REST API]     +--------------------------+  +-----------------------------------+ |
|                | Headcount by Department  |  | Salary Distribution by Department | |
|                |      (Chart.js)          |  |           (Chart.js)              | |
|                +--------------------------+  +-----------------------------------+ |
|                                                                                    |
|                +-----------------------------------------------------------------+ |
|                | Department Breakdown & Budget Allocations                       | |
|                +-----------------------------------------------------------------+ |
|                | Department      | Staff | Avg Salary | Total Budget | % Share   | |
|                | Engineering     |   4   | $114,750   | $459,000     |  25.0%    | |
|                | HR              |   2   | $107,000   | $214,000     |  12.5%    | |
|                | Marketing       |   2   | $117,500   | $235,000     |  12.5%    | |
|                +-----------------------------------------------------------------+ |
+------------------------------------------------------------------------------------+
```

---

## 🗄️ Database Configuration

### Zero-Config Local Development
By default, the application runs on an in-memory **H2 database** configured in **PostgreSQL compatibility mode**. You do not need to install or configure PostgreSQL to test locally:
- JDBC URL: `jdbc:h2:mem:emsdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL`
- H2 Console: `http://localhost:8080/h2-console`

### Production PostgreSQL (Render)
When deployed to Render, `DatabaseConfig.java` automatically detects the environment variable `DATABASE_URL` (injected in the format `postgres://user:password@host:port/database`) and configures a production HikariCP connection pool pointing to PostgreSQL with zero manual code changes required.

---

## ⚙️ Environment Variables

| Variable | Required in Prod | Default (Local) | Purpose |
|---|---|---|---|
| `PORT` | Yes (Render sets this) | `8080` | Web server listening port |
| `DATABASE_URL` | Yes (Render PostgreSQL) | `""` (Uses H2 fallback) | PostgreSQL connection string |
| `SPRING_PROFILES_ACTIVE` | Recommended | `default` | Set to `prod` for production caching and logging |
| `SPRING_DATASOURCE_URL` | Optional | `jdbc:h2:mem:emsdb;...` | Custom JDBC URL if not using Render `DATABASE_URL` |
| `SPRING_DATASOURCE_USERNAME` | Optional | `sa` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Optional | `""` | Database password |

---

## 🚀 Local Setup & Running

### Prerequisites
- **Java Development Kit (JDK) 17** or higher
- Git

### Steps
1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/employee-management-system.git
   cd employee-management-system
   ```

2. **Run tests to verify build**:
   ```bash
   # Linux / macOS
   ./mvnw test

   # Windows
   .\mvnw.cmd test
   ```

3. **Start the application**:
   ```bash
   # Linux / macOS
   ./mvnw spring-boot:run

   # Windows
   .\mvnw.cmd spring-boot:run
   ```

4. **Access the application**:
   - Web Dashboard: [http://localhost:8080/dashboard](http://localhost:8080/dashboard)
   - Employee Directory: [http://localhost:8080/employees](http://localhost:8080/employees)
   - REST API Stats: [http://localhost:8080/api/employees/dashboard-stats](http://localhost:8080/api/employees/dashboard-stats)
   - H2 Console: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

---

## 📦 Maven Commands

| Command | Action |
|---|---|
| `.\mvnw.cmd clean test` | Runs the full test suite (Service + Controller) |
| `.\mvnw.cmd clean package` | Compiles and packages the runnable JAR in `target/` |
| `.\mvnw.cmd spring-boot:run` | Runs the application in development mode with live reload |
| `java -jar target/employee-management-system-1.0.0.jar` | Runs the compiled executable JAR |

---

## 📡 REST API Reference

All REST endpoints return a unified JSON response structure:
```json
{
  "success": true,
  "message": "Operation status description",
  "data": { ... }
}
```

### Endpoints

| Method | Endpoint | Description | Status Codes |
|---|---|---|---|
| `GET` | `/api/employees` | Paginated and filtered employee list (`?search=&department=&status=&page=0&size=10&sortBy=id&sortDir=asc`) | `200 OK` |
| `GET` | `/api/employees/all` | List all employee records | `200 OK` |
| `GET` | `/api/employees/{id}` | Get employee by ID | `200 OK`, `404 NOT FOUND` |
| `POST` | `/api/employees` | Create a new employee | `201 CREATED`, `400 BAD REQUEST`, `409 CONFLICT` |
| `PUT` | `/api/employees/{id}` | Update existing employee | `200 OK`, `400 BAD REQUEST`, `404 NOT FOUND`, `409 CONFLICT` |
| `PATCH` | `/api/employees/{id}/toggle-status` | Toggle employee active/inactive status | `200 OK`, `404 NOT FOUND` |
| `DELETE` | `/api/employees/{id}` | Permanently delete employee | `200 OK`, `404 NOT FOUND` |
| `GET` | `/api/employees/search?keyword={query}` | Search employees by keyword (name or email) | `200 OK` |
| `GET` | `/api/employees/department/{department}` | List employees by department (e.g. `ENGINEERING`) | `200 OK` |
| `GET` | `/api/employees/dashboard-stats` | HR KPI metrics and department statistics | `200 OK` |

### Sample JSON: Create Employee (`POST /api/employees`)
```json
{
  "firstName": "Sarah",
  "lastName": "Connor",
  "email": "sarah.connor@cyberdyne.com",
  "phoneNumber": "+1 415-555-0199",
  "department": "ENGINEERING",
  "designation": "Staff Security Engineer",
  "salary": 145000.00,
  "joiningDate": "2023-04-15",
  "status": "ACTIVE"
}
```

### Sample JSON: Validation Error Response (`400 Bad Request`)
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "email": "Please provide a valid email address",
    "salary": "Salary must be greater than zero",
    "joiningDate": "Joining date cannot be in the future"
  }
}
```

---

## 🧪 Automated Testing

The project includes unit and integration tests using JUnit 5, Mockito, and Spring MockMvc:
- **Service Layer Tests** (`EmployeeServiceTest`):
  - Employee creation with duplicate email verification
  - Finding employee by ID & handling not found scenarios
  - Employee updating with duplicate email safeguards across different records
  - Employee deletion and not-found checks
  - Status toggle logic (ACTIVE <-> INACTIVE)
  - Real database aggregate calculations for dashboard metrics
  - Department retrieval and search queries
- **Controller Layer Tests** (`EmployeeRestControllerTest`):
  - Paginated retrieval (`GET /api/employees`)
  - Finding single employee (`GET /api/employees/{id}`)
  - 404 response on missing employee
  - Successful creation (`POST /api/employees`) returning HTTP 201
  - Validation failure checks (`POST /api/employees` with invalid fields) returning HTTP 400
  - Duplicate email conflict handling returning HTTP 409
  - Successful update (`PUT /api/employees/{id}`)
  - Successful deletion (`DELETE /api/employees/{id}`)
  - Search by keyword (`GET /api/employees/search`)
  - Department filtering (`GET /api/employees/department/{department}`)

Run tests anytime with:
```powershell
.\mvnw.cmd test
```

---

## ☁️ Deploying to Render

This repository is pre-configured for deployment as a **Render Web Service** with a managed **Render PostgreSQL** database.

### Method 1: Automatic Blueprint Deployment (Recommended)
1. Fork or push this repository to GitHub.
2. Sign in to your [Render Dashboard](https://dashboard.render.com).
3. Click **New +** -> **Blueprint**.
4. Connect your GitHub repository.
5. Render detects `render.yaml` and will automatically configure:
   - Web Service: `employee-management-system` (Docker environment)
   - PostgreSQL Database: `ems-postgres`
   - Injected `DATABASE_URL` linking the web service to the database.
6. Click **Apply**. Render will build the Docker container and deploy the app live!

### Method 2: Manual Web Service Deployment
1. **Create PostgreSQL Database on Render**:
   - Go to Render Dashboard -> **New +** -> **PostgreSQL**.
   - Name: `ems-postgres`
   - Database: `emsdb`
   - User: `emsuser`
   - Region: Select your preferred region (e.g., Oregon or Frankfurt).
   - Plan: Free.
   - Click **Create Database** and copy the **Internal Database URL** (e.g. `postgres://emsuser:pass@dpg-xxx:5432/emsdb`).

2. **Create Web Service on Render**:
   - Go to Render Dashboard -> **New +** -> **Web Service**.
   - Connect your GitHub repository.
   - Language / Runtime: **Docker**.
   - Dockerfile Path: `Dockerfile`
   - Branch: `main`.

3. **Set Environment Variables**:
   Under the **Environment** tab, add:
   | Key | Value |
   |---|---|
   | `SPRING_PROFILES_ACTIVE` | `prod` |
   | `DATABASE_URL` | *(Paste Internal Database URL from step 1)* |
   | `USE_RENDER_DATABASE_URL` | `true` |

4. **Deploy**:
   Click **Create Web Service**. Once the build finishes, open your Render `.onrender.com` URL to view the live dashboard!

---

## 🔮 Future Improvements

1. **Authentication & Authorization**: Integrate Spring Security with JWT or OAuth2 / Keycloak for role-based access control (HR Admin vs Employee).
2. **Export & Reporting**: Add one-click export of employee directories and payroll reports to CSV and PDF formats.
3. **Audit Logging**: Implement Hibernate Envers or Spring Data JPA Auditing to maintain a historical log of all employee profile changes.
4. **Document Attachments**: Support uploading resumes, contracts, and certifications with AWS S3 or Google Cloud Storage.

---

## 👤 Author & License

Developed as a showcase Java Full-Stack engineering project. Free to use for portfolio demonstrations, educational reference, and interviews.
