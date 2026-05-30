# resQ

resQ is a full-stack animal rescue platform designed to help citizens report injured or suffering stray animals and connect them with rescue volunteers.

The platform allows:

* Citizens to report animals with images and location details
* Volunteers to manage rescue operations
* Admins to oversee rescue workflows and assignments

---

# Tech Stack

## Backend

* Java
* Spring Boot
* Spring Security
* JWT Authentication
* MySQL
* Hibernate / JPA

## Frontend (In Progress)

* React
* Vite
* Tailwind CSS
* Axios

---

# Features

## Authentication & Authorization

* User Registration
* User Login
* JWT Authentication
* Role-Based Access Control

### Roles

* CITIZEN
* VOLUNTEER
* ADMIN

---

## Report Management

* Create Rescue Reports
* Upload Animal Images
* View Reports
* Update Reports
* Delete Reports
* My Reports API
* Ownership-Based Security

---

## Rescue Workflow

* Volunteer Assignment
* Rescue Status Tracking

### Current Status Flow

* PENDING
* ASSIGNED
* ON_THE_WAY
* RESCUING
* RESCUED
* FAILED

---

# API Overview

## Auth APIs

```http
POST /api/auth/register
POST /api/auth/login
```

## Citizen APIs

```http
POST /api/reports
GET /api/reports/my
PUT /api/reports/{id}
DELETE /api/reports/{id}
```

## Admin APIs

```http
PUT /api/reports/{reportId}/assign/{volunteerId}
GET /api/admin/test
```

---

# Running the Backend

## Clone Repository

```bash
git clone [<repository-url>](https://github.com/K-Kathiresan/resQ-A-Voice-for-the-Voiceless)
```

## Navigate to Project

```bash
cd resQ
```

## Run Spring Boot Application

```bash
mvn spring-boot:run
```

---

# Future Plans

* React Frontend
* Live Rescue Tracking
* AI-Based First Aid Guidance
* Google Maps Integration
* Cloud Image Storage
* Real-Time Notifications

---

# Author

Kathiresan K
