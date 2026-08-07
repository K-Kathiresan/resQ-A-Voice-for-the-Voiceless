# resQ – A Voice for the Voiceless

A full-stack animal rescue management platform built using **Java, Spring Boot, MySQL, HTML, CSS, and JavaScript**. The platform enables citizens to report injured animals, volunteers to manage rescue operations, and administrators to oversee rescue workflows through secure role-based access control.

---

## Project Overview

resQ is designed to bridge the gap between citizens who encounter injured or abandoned animals and volunteers capable of providing timely assistance. The application streamlines the reporting, assignment, tracking, and completion of rescue operations through a centralized platform.

The backend follows a layered Spring Boot architecture with JWT-based authentication, RESTful APIs, Bean Validation, Global Exception Handling, and MySQL persistence. The frontend provides dedicated dashboards for Citizens, Volunteers, and Administrators.

---

## Key Features

### Authentication & Security
- JWT Authentication
- Spring Security
- BCrypt Password Encryption
- Role-Based Access Control (Citizen, Volunteer, Admin)
- Protected REST APIs

### Citizen Module
- Register and login securely
- Create rescue reports
- Upload animal images
- Capture GPS location
- Track rescue status
- View previously submitted reports

### Volunteer Module
- View assigned rescue requests
- Update rescue status
- Add rescue notes
- Navigate using Google Maps
- Manage rescue workflow

### Administrator Module
- Manage rescue reports
- Assign volunteers
- Search and filter reports
- Monitor rescue analytics
- Track overall rescue activity

### Backend Engineering
- Layered Architecture
- DTO Pattern
- Repository Pattern
- Bean Validation
- Global Exception Handling
- Multipart File Upload
- Static Resource Configuration
- RESTful API Design

---

# Application Screenshots


## Login
<img width="1919" height="1028" alt="login" src="https://github.com/user-attachments/assets/746ec79b-7ad1-419e-b775-a7fe60a5f2ea" />



---

## Create Animal Rescue Report

<img width="1919" height="1035" alt="create_report" src="https://github.com/user-attachments/assets/59bf6c01-9ee7-4f41-a785-77f399207c8f" />

---

## Citizen Dashboard

<img width="1909" height="1029" alt="citizen_dashboard" src="https://github.com/user-attachments/assets/4114d5c9-52ef-4da2-bd75-1f9ad998be55" />


---

## Volunteer Dashboard
<img width="1919" height="983" alt="volunteer_dashboard" src="https://github.com/user-attachments/assets/1acd5438-e601-416a-bedb-7b973ebd3ffa" />



---

## Administrator Dashboard
<img width="1919" height="1044" alt="admin_dashboard" src="https://github.com/user-attachments/assets/d5873843-c070-4bc4-9d52-506e897814ec" />



---

# Technology Stack

| Layer | Technologies |
|--------|--------------|
| Backend | Java, Spring Boot, Spring Security, Spring Data JPA, Hibernate |
| Frontend | HTML5, CSS3, JavaScript |
| Database | MySQL |
| Authentication | JWT, BCrypt |
| Build Tool | Maven |
| Tools | Git, GitHub, VS Code, Postman, XAMPP |

---

# System Architecture

```text
Client
   │
Spring Security (JWT)
   │
REST Controllers
   │
Service Layer
   │
Repository Layer
   │
MySQL Database
```

<!-- INSERT ARCHITECTURE DIAGRAM HERE -->

---

# Project Structure

```text
src
├── controller
├── service
├── repository
├── entity
├── dto
├── security
├── config
├── exception
├── validation
└── util
```

---

# Rescue Workflow

```text
Citizen
  │
PENDING
  │
ASSIGNED
  │
ON_THE_WAY
  │
RESCUING
 ├── RESCUED
 └── FAILED
```

---

# REST API Overview

## Authentication

POST /api/auth/register

POST /api/auth/login

## Citizen

POST /api/reports

GET /api/reports/my

PUT /api/reports/{id}

DELETE /api/reports/{id}

## Volunteer

GET /api/volunteer/reports

PUT /api/volunteer/reports/{id}/status

## Administrator

GET /api/admin/reports

PUT /api/admin/reports/{reportId}/assign/{volunteerId}

---

# Getting Started

```bash
git clone https://github.com/K-Kathiresan/resQ-A-Voice-for-the-Voiceless.git
cd resQ-A-Voice-for-the-Voiceless
mvn spring-boot:run
```

Update your database credentials in `application.properties` before running the application.

---

# Engineering Practices

- Object-Oriented Programming
- Layered Architecture
- Repository Pattern
- DTO Pattern
- Bean Validation
- Global Exception Handling
- RESTful API Design
- Git Version Control

---

# Roadmap

- ✅ JWT Authentication
- ✅ Role-Based Authorization
- ✅ Citizen, Volunteer & Administrator Modules
- ✅ GPS Location Capture
- ✅ Google Maps Navigation
- ✅ Dashboard Analytics
- ⏳ Unit Testing (JUnit)
- ⏳ Docker
- ⏳ CI/CD
- ⏳ Cloud Deployment
- ⏳ AI-powered Injury Detection

---

# Author

**Kathiresan K**

- GitHub: https://github.com/K-Kathiresan
- LinkedIn: https://linkedin.com/in/kathiresan-k-05047a2b2
