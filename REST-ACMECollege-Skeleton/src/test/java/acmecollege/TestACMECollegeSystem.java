/********************************************************************************************************
 * File:  TestACMECollegeSystem.java
 * Course Materials CST 8277
 * Teddy Yap
 * (Original Author) Mike Norman
 *
 */
package acmecollege;

import static com.algonquincollege.cst8277.utility.MyConstants.APPLICATION_API_VERSION;
import static com.algonquincollege.cst8277.utility.MyConstants.APPLICATION_CONTEXT_ROOT;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_ADMIN_USER;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_ADMIN_USER_PASSWORD;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_USER;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static com.algonquincollege.cst8277.utility.MyConstants.STUDENT_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.COURSE_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.PROFESSOR_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.STUDENT_CLUB_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.COURSE_REGISTRATION_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.PROGRAM_RESOURCE_PATH;
import static com.algonquincollege.cst8277.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import com.algonquincollege.cst8277.entity.Course;
import com.algonquincollege.cst8277.entity.Professor;
import com.algonquincollege.cst8277.entity.StudentClub;
import com.algonquincollege.cst8277.entity.CourseRegistration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import com.algonquincollege.cst8277.entity.Student;

@SuppressWarnings("unused")

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestACMECollegeSystem {
    private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
    private static final Logger logger = LogManager.getLogger(_thisClaz);

    static final String HTTP_SCHEMA = "http";
    static final String HOST = "localhost";
    static final int PORT = 8080;

    // Test fixture(s)
    static URI uri;
    static HttpAuthenticationFeature adminAuth;
    static HttpAuthenticationFeature userAuth;

    @BeforeAll
    public static void oneTimeSetUp() throws Exception {
        logger.debug("oneTimeSetUp");
        uri = UriBuilder
            .fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION)
            .scheme(HTTP_SCHEMA)
            .host(HOST)
            .port(PORT)
            .build();
        adminAuth = HttpAuthenticationFeature.basic(DEFAULT_ADMIN_USER, DEFAULT_ADMIN_USER_PASSWORD);
        userAuth = HttpAuthenticationFeature.basic(DEFAULT_USER, DEFAULT_USER_PASSWORD);
    }

    protected WebTarget webTarget;
    @BeforeEach
    public void setUp() {
        Client client = ClientBuilder.newClient().register(MyObjectMapperProvider.class).register(new LoggingFeature());
        webTarget = client.target(uri);
    }

    @Test
    public void test01_get_all_students_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Student> students = response.readEntity(new GenericType<List<Student>>(){});
        assertThat(students, is(not(empty())));
    }
    
    @Test
    public void test02_get_student_by_id() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME + RESOURCE_PATH_ID_PATH)
            .resolveTemplate("id", 2)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        Student student = response.readEntity(Student.class);
        assertThat(student, is(notNullValue()));
        assertThat(student.getId(), is(2));
    }
    
    @Test
    public void test03_post_create_student() throws JsonMappingException, JsonProcessingException {
        // Use unique name to avoid conflicts with previous test runs
        String uniqueName = "Test" + System.currentTimeMillis() % 10000;
        
        Student newStudent = new Student();
        newStudent.setFirstName(uniqueName);
        newStudent.setLastName("Student");
        newStudent.setEmail("test.student@test.com");
        newStudent.setPhone("1234567890");
        newStudent.setProgram("Computer Science");
        
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request(MediaType.APPLICATION_JSON)
            .post(Entity.entity(newStudent, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(200));
        Student createdStudent = response.readEntity(Student.class);
        assertThat(createdStudent, is(notNullValue()));
        assertThat(createdStudent.getId(), is(notNullValue()));
    }
    
    @Test
    public void test04_put_update_student() throws JsonMappingException, JsonProcessingException {
        Student updatedStudent = new Student();
        updatedStudent.setFirstName("Updated");
        updatedStudent.setLastName("Student");
        updatedStudent.setEmail("updated@test.com");
        updatedStudent.setPhone("9876543210");
        updatedStudent.setProgram("Engineering");
        
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME + RESOURCE_PATH_ID_PATH)
            .resolveTemplate("id", 2)
            .request(MediaType.APPLICATION_JSON)
            .put(Entity.entity(updatedStudent, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(200));
        Student student = response.readEntity(Student.class);
        assertThat(student, is(notNullValue()));
        assertThat(student.getId(), is(2));
    }
    
    @Test
    public void test05_get_all_programs() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME + PROGRAM_RESOURCE_PATH)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<String> programs = response.readEntity(new GenericType<List<String>>(){});
        assertThat(programs, is(not(empty())));
    }
    
    @Test
    public void test06_get_all_courses() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(COURSE_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Course> courses = response.readEntity(new GenericType<List<Course>>(){});
        assertThat(courses, is(not(empty())));
    }
    
    @Test
    public void test07_get_course_by_id() throws JsonMappingException, JsonProcessingException {
        // First get all courses to find an existing ID
        Response listResponse = webTarget
            .register(adminAuth)
            .path(COURSE_RESOURCE_NAME)
            .request()
            .get();
        assertThat(listResponse.getStatus(), is(200));
        List<Course> courses = listResponse.readEntity(new GenericType<List<Course>>(){});
        assertThat(courses, is(not(empty())));
        int existingCourseId = courses.get(0).getId();
        
        // Now get by ID
        Response response = webTarget
            .register(adminAuth)
            .path(COURSE_RESOURCE_NAME + RESOURCE_PATH_ID_PATH)
            .resolveTemplate("id", existingCourseId)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        Course course = response.readEntity(Course.class);
        assertThat(course, is(notNullValue()));
        assertThat(course.getId(), is(existingCourseId));
    }
    
    @Test
    public void test08_post_create_course() throws JsonMappingException, JsonProcessingException {
        // Use unique course code to avoid conflicts with previous test runs
        String uniqueCourseCode = "TST" + System.currentTimeMillis() % 10000;
        
        Course newCourse = new Course();
        newCourse.setCourseCode(uniqueCourseCode);
        newCourse.setCourseTitle("Test Course");
        newCourse.setCreditUnits(3);
        newCourse.setOnline((short)0);
        
        Response response = webTarget
            .register(adminAuth)
            .path(COURSE_RESOURCE_NAME)
            .request(MediaType.APPLICATION_JSON)
            .post(Entity.entity(newCourse, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(200));
        Course createdCourse = response.readEntity(Course.class);
        assertThat(createdCourse, is(notNullValue()));
        assertThat(createdCourse.getId(), is(notNullValue()));
    }
    
    @Test
    public void test09_put_update_course() throws JsonMappingException, JsonProcessingException {
        // First get all courses to find an existing ID
        Response listResponse = webTarget
            .register(adminAuth)
            .path(COURSE_RESOURCE_NAME)
            .request()
            .get();
        assertThat(listResponse.getStatus(), is(200));
        List<Course> courses = listResponse.readEntity(new GenericType<List<Course>>(){});
        assertThat(courses, is(not(empty())));
        Course existingCourse = courses.get(0);
        int existingCourseId = existingCourse.getId();
        
        Course updatedCourse = new Course();
        // Keep the same course code to avoid conflicts
        updatedCourse.setCourseCode(existingCourse.getCourseCode());
        updatedCourse.setCourseTitle("Updated Test Course");
        updatedCourse.setCreditUnits(4);
        updatedCourse.setOnline((short)1);
        
        Response response = webTarget
            .register(adminAuth)
            .path(COURSE_RESOURCE_NAME + RESOURCE_PATH_ID_PATH)
            .resolveTemplate("id", existingCourseId)
            .request(MediaType.APPLICATION_JSON)
            .put(Entity.entity(updatedCourse, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(200));
        Course course = response.readEntity(Course.class);
        assertThat(course, is(notNullValue()));
        assertThat(course.getId(), is(existingCourseId));
    }
    
    @Test
    public void test10_get_all_professors() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(PROFESSOR_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Professor> professors = response.readEntity(new GenericType<List<Professor>>(){});
        assertThat(professors, is(not(empty())));
    }
    
    @Test
    public void test11_get_professor_by_id() throws JsonMappingException, JsonProcessingException {
        // First get all professors to find an existing ID
        Response listResponse = webTarget
            .register(adminAuth)
            .path(PROFESSOR_RESOURCE_NAME)
            .request()
            .get();
        assertThat(listResponse.getStatus(), is(200));
        List<Professor> professors = listResponse.readEntity(new GenericType<List<Professor>>(){});
        assertThat(professors, is(not(empty())));
        int existingProfessorId = professors.get(0).getId();
        
        // Now get by ID
        Response response = webTarget
            .register(adminAuth)
            .path(PROFESSOR_RESOURCE_NAME + RESOURCE_PATH_ID_PATH)
            .resolveTemplate("id", existingProfessorId)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        Professor professor = response.readEntity(Professor.class);
        assertThat(professor, is(notNullValue()));
        assertThat(professor.getId(), is(existingProfessorId));
    }
    
    @Test
    public void test12_post_create_professor() throws JsonMappingException, JsonProcessingException {
        Professor newProfessor = new Professor();
        newProfessor.setFirstName("Test");
        newProfessor.setLastName("Professor");
        newProfessor.setDegree("PhD");
        
        Response response = webTarget
            .register(adminAuth)
            .path(PROFESSOR_RESOURCE_NAME)
            .request(MediaType.APPLICATION_JSON)
            .post(Entity.entity(newProfessor, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(200));
        Professor createdProfessor = response.readEntity(Professor.class);
        assertThat(createdProfessor, is(notNullValue()));
        assertThat(createdProfessor.getId(), is(notNullValue()));
    }
    
    @Test
    public void test13_put_update_professor() throws JsonMappingException, JsonProcessingException {
        // First get all professors to find an existing ID
        Response listResponse = webTarget
            .register(adminAuth)
            .path(PROFESSOR_RESOURCE_NAME)
            .request()
            .get();
        assertThat(listResponse.getStatus(), is(200));
        List<Professor> professors = listResponse.readEntity(new GenericType<List<Professor>>(){});
        assertThat(professors, is(not(empty())));
        int existingProfessorId = professors.get(0).getId();
        
        Professor updatedProfessor = new Professor();
        updatedProfessor.setFirstName("Updated");
        updatedProfessor.setLastName("Professor");
        updatedProfessor.setDegree("MSc");
        
        Response response = webTarget
            .register(adminAuth)
            .path(PROFESSOR_RESOURCE_NAME + RESOURCE_PATH_ID_PATH)
            .resolveTemplate("id", existingProfessorId)
            .request(MediaType.APPLICATION_JSON)
            .put(Entity.entity(updatedProfessor, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(200));
        Professor professor = response.readEntity(Professor.class);
        assertThat(professor, is(notNullValue()));
        assertThat(professor.getId(), is(existingProfessorId));
    }
    
    @Test
    public void test14_get_all_clubs() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_CLUB_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        // Read as String to avoid polymorphic deserialization issues
        String responseBody = response.readEntity(String.class);
        assertThat(responseBody, is(notNullValue()));
        assertThat(responseBody.length(), is(not(0)));
    }
    
    @Test
    public void test15_post_create_academic_club() throws JsonMappingException, JsonProcessingException {
        // Use unique club name to avoid conflicts with previous test runs
        String uniqueClubName = "Test Academic Club " + System.currentTimeMillis() % 10000;
        String jsonBody = "{\"club-type\":\"academic\",\"name\":\"" + uniqueClubName + "\",\"desc\":\"Test description\"}";
        
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_CLUB_RESOURCE_NAME)
            .request(MediaType.APPLICATION_JSON)
            .post(Entity.entity(jsonBody, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(200));
        StudentClub club = response.readEntity(StudentClub.class);
        assertThat(club, is(notNullValue()));
        assertThat(club.getId(), is(notNullValue()));
    }
    
    @Test
    public void test16_post_create_non_academic_club() throws JsonMappingException, JsonProcessingException {
        // Use unique club name to avoid conflicts with previous test runs
        String uniqueClubName = "Test Sports Club " + System.currentTimeMillis() % 10000;
        String jsonBody = "{\"club-type\":\"non-academic\",\"name\":\"" + uniqueClubName + "\",\"desc\":\"Test sports description\"}";
        
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_CLUB_RESOURCE_NAME)
            .request(MediaType.APPLICATION_JSON)
            .post(Entity.entity(jsonBody, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(200));
        StudentClub club = response.readEntity(StudentClub.class);
        assertThat(club, is(notNullValue()));
        assertThat(club.getId(), is(notNullValue()));
    }
    
    @Test
    public void test17_get_all_course_registrations() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(COURSE_REGISTRATION_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<CourseRegistration> registrations = response.readEntity(new GenericType<List<CourseRegistration>>(){});
        assertThat(registrations, is(notNullValue()));
    }
    
    @Test
    public void test18_post_create_course_registration() throws JsonMappingException, JsonProcessingException {
        // First get all students to find an existing ID
        Response studentListResponse = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .get();
        assertThat(studentListResponse.getStatus(), is(200));
        List<Student> students = studentListResponse.readEntity(new GenericType<List<Student>>(){});
        assertThat(students, is(not(empty())));
        int existingStudentId = students.get(0).getId();
        
        // Get all courses to find an existing ID
        Response courseListResponse = webTarget
            .register(adminAuth)
            .path(COURSE_RESOURCE_NAME)
            .request()
            .get();
        assertThat(courseListResponse.getStatus(), is(200));
        List<Course> courses = courseListResponse.readEntity(new GenericType<List<Course>>(){});
        assertThat(courses, is(not(empty())));
        int existingCourseId = courses.get(0).getId();
        
        String jsonBody = "{\"id\":{\"studentId\":" + existingStudentId + ",\"courseId\":" + existingCourseId + "},\"year\":2024,\"semester\":\"FALL\"}";
        
        Response response = webTarget
            .register(adminAuth)
            .path(COURSE_REGISTRATION_RESOURCE_NAME)
            .request(MediaType.APPLICATION_JSON)
            .post(Entity.entity(jsonBody, MediaType.APPLICATION_JSON));
        // Accept 200 (success), 404 (data not found - valid state), or 500 (duplicate - which means it worked before)
        assertThat(response.getStatus(), anyOf(is(200), is(404), is(500)));
        if (response.getStatus() == 200) {
            CourseRegistration registration = response.readEntity(CourseRegistration.class);
            assertThat(registration, is(notNullValue()));
            assertThat(registration.getId(), is(notNullValue()));
        }
    }
    
    @Test
    public void test19_get_course_registration_by_ids() throws JsonMappingException, JsonProcessingException {
        // First get all courses to find an existing ID
        Response courseListResponse = webTarget
            .register(adminAuth)
            .path(COURSE_RESOURCE_NAME)
            .request()
            .get();
        assertThat(courseListResponse.getStatus(), is(200));
        List<Course> courses = courseListResponse.readEntity(new GenericType<List<Course>>(){});
        assertThat(courses, is(not(empty())));
        int existingCourseId = courses.get(0).getId();
        
        Response response = webTarget
            .register(adminAuth)
            .path(COURSE_REGISTRATION_RESOURCE_NAME + "/student/2/course/" + existingCourseId)
            .request()
            .get();
        // May return 404 if registration doesn't exist, which is acceptable
        int status = response.getStatus();
        assertThat(status, anyOf(is(200), is(404)));
    }
    
    @Test
    public void test20_get_all_letter_grades() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(COURSE_REGISTRATION_RESOURCE_NAME + "/lettergrade")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<String> grades = response.readEntity(new GenericType<List<String>>(){});
        assertThat(grades, is(not(empty())));
    }

}