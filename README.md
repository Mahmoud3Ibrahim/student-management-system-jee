# ACME College Management System

A comprehensive college management system built with Jakarta EE, featuring a RESTful backend and JSF frontend for managing students, courses, professors, and student clubs.

## Project Structure

The project consists of two main components:

- **REST-ACMECollege-Skeleton**: Backend REST API built with JAX-RS, JPA/Hibernate, and EJB
- **JSF-ACMECollege-Skeleton**: Frontend web application built with Jakarta Server Faces

Both applications deploy to Payara Server and share a common MySQL database.

## Key Features

### Student Management
- Full CRUD operations for student records
- Track student information including name, email, phone, and program
- Secure user authentication and role-based access control

### Course Registration
- Register students for courses with semester and year tracking
- Assign professors to course registrations
- Grade assignment with standardized letter grades
- Dynamic dropdowns for all selection fields to prevent data entry errors

### Professor Management
- Maintain professor records with department affiliations
- View professor assignments across multiple course registrations

### Student Clubs
- Support for both academic and non-academic clubs
- Complete club lifecycle management (create, update, delete)
- Student membership tracking

## Recent Improvements

### Enhanced Validation and Error Handling

We've improved the robustness of the system by adding comprehensive validation at multiple layers:

- **API Layer**: All REST endpoints now validate incoming data before processing. Invalid requests return proper HTTP 400 status codes with clear error messages instead of cryptic 500 errors.
- **Service Layer**: Business logic validates entity relationships and ensures data integrity before persistence operations.
- **Database Layer**: Proper foreign key handling ensures referential integrity across all tables.

### Dynamic Data Loading

Previously, several UI components used hardcoded values that could become outdated. We've replaced these with dynamic data loading:

- **Semesters**: Now loaded from the database via a new REST endpoint (`GET /courseregistration/semester`)
- **Letter Grades**: Integrated with existing endpoint (`GET /courseregistration/lettergrade`)
- **Year Selection**: Dynamically generated based on current date

This approach eliminates the need for code changes when adding new semesters or adjusting grading scales.

### Composite Key Handling

Course registrations use a composite primary key (student ID + course ID). We've refined the handling to ensure:

- Proper attachment of managed JPA entities before persistence
- Correct JSON serialization/deserialization of composite keys
- Validation that both student and course exist before creating a registration

### Polymorphic Type Support

Student clubs use single-table inheritance to distinguish between academic and non-academic clubs. The system now correctly:

- Serializes the discriminator field (`club-type`) in JSON payloads
- Deserializes incoming requests to the appropriate subclass
- Validates required fields for both club types

### Better Error Feedback

The JSF frontend now handles API errors gracefully:

- Checks HTTP status codes before attempting to parse responses
- Displays user-friendly error messages in the UI
- Prevents application crashes from unexpected response formats

## Technical Stack

- **Application Server**: Payara 6.2024.4
- **Java Version**: Java 17+
- **Build Tool**: Maven 3.6+
- **Database**: MySQL 8.0+
- **Frameworks**:
  - Jakarta EE 10
  - JAX-RS (Jersey) for REST APIs
  - JPA 3.1 (Hibernate) for persistence
  - JSF 4.0 for web UI
  - Jackson for JSON processing

## Setup and Installation

### Prerequisites

1. Install Java JDK 17 or higher
2. Install Apache Maven
3. Install Payara Server 6.2024.4
4. Install MySQL Server 8.0+

### Database Configuration

1. Create a MySQL database named `acme_college`
2. Update JDBC connection settings in Payara:
   - Pool Name: `acme_college_pool`
   - JNDI Name: `java:app/datasources/ACMECollegeDS`
   - Connection URL: `jdbc:mysql://localhost:3306/acme_college`

The application will automatically create tables and load initial data on first deployment.

### Building the Projects

```bash
# Build REST backend
cd REST-ACMECollege-Skeleton
mvn clean install

# Build JSF frontend
cd ../JSF-ACMECollege-Skeleton
mvn clean install
```

### Deployment

1. Start Payara Server
2. Deploy the REST application first:
   ```bash
   asadmin deploy REST-ACMECollege-Skeleton/target/REST-ACMECollege-Skeleton.war
   ```
3. Deploy the JSF application:
   ```bash
   asadmin deploy JSF-ACMECollege-Skeleton/target/JSF-ACMECollege-Skeleton.war
   ```

The JSF application will be available at `http://localhost:8080/JSF-ACMECollege-Skeleton`

### Default Credentials

- **Admin User**: 
  - Username: `admin`
  - Password: `admin`

## API Documentation

### Base URL
```
http://localhost:8080/REST-ACMECollege-Skeleton/api/v1
```

### Authentication
All endpoints require HTTP Basic Authentication with admin credentials.

### Main Endpoints

#### Students
- `GET /student` - List all students
- `GET /student/{id}` - Get student by ID
- `POST /student` - Create new student
- `PUT /student/{id}` - Update student
- `DELETE /student/{id}` - Delete student

#### Courses
- `GET /course` - List all courses
- `GET /course/{id}` - Get course by ID
- `POST /course` - Create new course
- `PUT /course/{id}` - Update course
- `DELETE /course/{id}` - Delete course

#### Course Registrations
- `GET /courseregistration` - List all registrations
- `GET /courseregistration/student/{studentId}/course/{courseId}` - Get specific registration
- `POST /courseregistration` - Create new registration
- `PUT /courseregistration/student/{studentId}/course/{courseId}` - Assign professor
- `PUT /courseregistration/student/{studentId}/course/{courseId}/lettergrade` - Assign grade
- `GET /courseregistration/semester` - Get list of semesters
- `GET /courseregistration/lettergrade` - Get list of letter grades

#### Professors
- `GET /professor` - List all professors
- `GET /professor/{id}` - Get professor by ID
- `POST /professor` - Create new professor
- `PUT /professor/{id}` - Update professor
- `DELETE /professor/{id}` - Delete professor

#### Student Clubs
- `GET /studentclub` - List all clubs
- `GET /studentclub/{id}` - Get club by ID
- `POST /studentclub` - Create new club
- `PUT /studentclub/{id}` - Update club
- `DELETE /studentclub/{id}` - Delete club

## Development Notes

### Running Tests

The REST project includes a comprehensive test suite:

```bash
cd REST-ACMECollege-Skeleton
mvn test
```

Make sure the REST application is deployed and running before executing tests.

### Database Schema Management

The application uses JPA schema generation. On deployment, it will:
1. Drop existing tables
2. Create new tables based on entity definitions
3. Load initial data from `data.sql`

To disable this behavior (e.g., in production), modify `persistence.xml`:
```xml
<property name="jakarta.persistence.schema-generation.database.action" value="none"/>
```

### Adding New Semesters

Simply insert new records into the `semester` table. The UI will automatically pick them up on next page load:

```sql
INSERT INTO semester (name) VALUES ('FALL2025');
```

### Logging

Both applications use SLF4J with Logback. Logs are written to the Payara server log directory.

To adjust log levels, modify the logging configuration in Payara admin console.

## Known Limitations

- The system currently supports only admin-level users. Student and professor roles are defined in the database but not fully implemented in the UI.
- Course registration updates are limited to professor and grade assignment. Changing semester or year requires deleting and recreating the registration.
- Student club membership management is not yet implemented in the JSF UI (API endpoints exist).

## Troubleshooting

### Common Issues

**Problem**: "MessageBodyReader not found" errors

**Solution**: Ensure all REST endpoints explicitly set the content type to `application/json` for both success and error responses.

---

**Problem**: Duplicate data after redeployment

**Solution**: Both WAR files have schema generation enabled. To prevent duplicates, set `schema-generation.database.action` to `none` in one of the `persistence.xml` files.

---

**Problem**: Professor assignment returns 500 error

**Solution**: Verify that the professor ID in the request payload exists in the database. The API now validates this and returns 404 for missing professors.

---

**Problem**: Student club creation fails with "description cannot be null"

**Solution**: Ensure the `desc` field is populated in the JSON payload. This field is required and cannot be null or empty.

## Contributing

When making changes to the codebase:

1. Maintain consistent validation across all three layers (REST, Service, JSF)
2. Always return proper HTTP status codes (400 for validation errors, 404 for not found, 500 for server errors)
3. Include descriptive error messages in the response body
4. Update tests to reflect any changes to API contracts
5. Keep JSON field names consistent with database column names where possible

## License

This project is developed as part of the CST8277 course at Algonquin College.

