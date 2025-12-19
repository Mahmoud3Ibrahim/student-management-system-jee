# ACME College Management System

**Academic Graduation Project for CST8277 Course**

**Author:** Mahmoud Ibrahim

---

## Overview

This project is a comprehensive college management system built using **Jakarta EE 10**.  
It contains two main parts:

- **REST-ACMECollege-Skeleton** – Backend REST API  
- **JSF-ACMECollege-Skeleton** – Frontend web application (JSF pages)

The REST API communicates with **MySQL**,  
and the JSF app communicates with the REST API using HTTP.

---

## Project Description

This is an academic graduation project developed for the CST8277 course. The system provides a complete solution for managing college operations including student enrollment, course registration, professor management, and student club activities.

### What I Implemented

1. **REST API Backend (REST-ACMECollege-Skeleton)**
   - Complete RESTful API implementation using JAX-RS
   - Entity classes with JPA annotations for database persistence
   - Service layer (EJB) for business logic
   - Security implementation using Jakarta Security
   - Error handling and exception mapping
   - JSON serialization/deserialization configuration

2. **JSF Frontend (JSF-ACMECollege-Skeleton)**
   - User interface implementation using JSF 4.0
   - Controllers for managing all entities (Students, Courses, Professors, Student Clubs, Course Registrations)
   - Form validation and error handling
   - Integration with REST API using Jersey client
   - Authentication and authorization

3. **Key Features Implemented**
   - Student management (CRUD operations)
   - Course management
   - Professor management with course assignments
   - Course registration with composite keys
   - Student club management (Academic and Non-Academic clubs)
   - Club membership management
   - Security and authentication
   - Form validation and error handling
   - Logging and debugging capabilities

---

## Features

### Students
- Create, update, delete students  
- View student information  
- Data loaded from REST API

### Courses & Registrations
- Register students in courses  
- Set semester, year, professor, and grade  
- Uses composite key (student + course)

### Professors
- Add and manage professor records  
- Show their course assignments

### Student Clubs
- Academic + Non-academic clubs  
- Create, update, delete clubs  
- Track membership

### Security
- Login using Jakarta Security (Basic Authentication)

---

## Technology Stack

- Java 17+  
- Jakarta EE 10  
- JAX-RS (REST)  
- JPA / Hibernate  
- JSF 4.0  
- Payara 6.2024.4  
- MySQL 8  
- Maven

---

## Building the Project

```bash
# REST backend
cd REST-ACMECollege-Skeleton
mvn clean install

# JSF frontend
cd ../JSF-ACMECollege-Skeleton
mvn clean install
```

---

## Deploying

1. Start Payara Server  
2. Deploy REST:

```bash
asadmin deploy REST-ACMECollege-Skeleton/target/REST-ACMECollege-Skeleton.war
```

3. Deploy JSF:

```bash
asadmin deploy JSF-ACMECollege-Skeleton/target/JSF-ACMECollege-Skeleton.war
```

### Default Login
- **Username:** admin  
- **Password:** admin

---

## REST API Base URL

```
http://localhost:8080/REST-ACMECollege-Skeleton/api/v1
```

Main endpoints:
- `/student`
- `/course`
- `/professor`
- `/courseregistration`
- `/studentclub`

All endpoints require **Basic Authentication**.

---

## Running Tests (REST Project)

```bash
cd REST-ACMECollege-Skeleton
mvn test
```

---

## Notes

- JSF must call REST for all data  
- Deploy REST **before** JSF  
- Database tables are created automatically by JPA
