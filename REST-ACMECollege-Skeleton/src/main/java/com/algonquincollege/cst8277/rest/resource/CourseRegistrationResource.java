/********************************************************************************************************
 * File:  CourseRegistrationResource.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * 
 */
package com.algonquincollege.cst8277.rest.resource;

import static com.algonquincollege.cst8277.utility.MyConstants.ADMIN_ROLE;
import static com.algonquincollege.cst8277.utility.MyConstants.COURSE_REGISTRATION_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.COURSE_ID_ELEMENT;
import static com.algonquincollege.cst8277.utility.MyConstants.STUDENT_ID_ELEMENT;
import static com.algonquincollege.cst8277.utility.MyConstants.STUDENT_COURSE_REGISTRATION_RESOURCE_PATH;
import static com.algonquincollege.cst8277.utility.MyConstants.LETTER_GRADE_RESOURCE_PATH;
import static com.algonquincollege.cst8277.utility.MyConstants.USER_ROLE;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algonquincollege.cst8277.ejb.ACMECollegeService;
import com.algonquincollege.cst8277.entity.CourseRegistration;
import com.algonquincollege.cst8277.entity.Professor;
import com.algonquincollege.cst8277.rest.resource.HttpErrorResponse;

@Path(COURSE_REGISTRATION_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseRegistrationResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;

    @GET
    // Only a user with the SecurityRole 'ADMIN_ROLE' can get the list of all course registrations.
    @RolesAllowed({ADMIN_ROLE})
    public Response getCourseRegistrations() {
        LOG.debug("retrieving all course registrations ...");
        List<CourseRegistration> courseRegistrations = service.getAllCourseRegistrations();
        Response response = Response.ok(courseRegistrations).build();
        return response;
    }

    @GET
    // A user with either the role 'ADMIN_ROLE' or 'USER_ROLE' can get a specific course registration.
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(STUDENT_COURSE_REGISTRATION_RESOURCE_PATH)
    public Response getCourseRegistrationById(
            @PathParam(STUDENT_ID_ELEMENT) int studentId,
            @PathParam(COURSE_ID_ELEMENT) int courseId) {
        LOG.debug("try to retrieve specific course registration for student " + studentId + " and course " + courseId);
        Response response = null;
        CourseRegistration courseRegistration = service.getCourseRegistrationById(studentId, courseId);
        response = Response.status(courseRegistration == null ? Status.NOT_FOUND : Status.OK).entity(courseRegistration).build();
        return response;
    }

    @POST
    // Only a user with the SecurityRole 'ADMIN_ROLE' can add a new course registration.
    @RolesAllowed({ADMIN_ROLE})
    public Response addCourseRegistration(CourseRegistration newCourseRegistration) {
        LOG.debug("Adding new course registration...");
        
        // Validate that we have a valid composite key
        if (newCourseRegistration == null ||
                newCourseRegistration.getId() == null ||
                newCourseRegistration.getId().getStudentId() <= 0 ||
                newCourseRegistration.getId().getCourseId() <= 0) {
            LOG.error("Invalid composite key in course registration request");
            return Response.status(Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new HttpErrorResponse(400, "studentId and courseId are required in composite key"))
                    .build();
        }
        
        try {
            CourseRegistration newCourseRegistrationWithIdTimestamps = service.persistCourseRegistration(newCourseRegistration);
            
            if (newCourseRegistrationWithIdTimestamps == null) {
                LOG.error("Student or course not found");
                return Response.status(Status.NOT_FOUND)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(new HttpErrorResponse(404, "Student or course not found"))
                        .build();
            }
            
            LOG.debug("Course registration added successfully");
            return Response.ok(newCourseRegistrationWithIdTimestamps).build();
            
        } catch (jakarta.persistence.PersistenceException e) {
            // Duplicate key or other database constraint violation
            LOG.error("Database constraint violation: {}", e.getMessage());
            return Response.status(Status.CONFLICT)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new HttpErrorResponse(409, "Course registration already exists for this student and course"))
                    .build();
                    
        } catch (Exception e) {
            // Any other unexpected error
            LOG.error("Unexpected error adding course registration: {}", e.getMessage(), e);
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new HttpErrorResponse(500, "Internal server error: " + e.getMessage()))
                    .build();
        }
    }

    @PUT
    // Assign professor to course registration
    @RolesAllowed({ADMIN_ROLE})
    @Path(STUDENT_COURSE_REGISTRATION_RESOURCE_PATH)
    public Response assignProfessorToCourseRegistration(
            @PathParam(STUDENT_ID_ELEMENT) int studentId,
            @PathParam(COURSE_ID_ELEMENT) int courseId,
            Professor professor) {
        if (professor == null || professor.getId() <= 0) {
            return Response.status(Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new HttpErrorResponse(400, "Professor ID is required and must be greater than 0"))
                    .build();
        }
        CourseRegistration updatedCourseRegistration = service.assignProfessorToCourseRegistration(studentId, courseId, professor);
        if (updatedCourseRegistration == null) {
            return Response.status(Status.NOT_FOUND)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new HttpErrorResponse(404, "Course registration or professor not found"))
                    .build();
        }
        return Response.ok(updatedCourseRegistration)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PUT
    // Assign grade to course registration
    @RolesAllowed({ADMIN_ROLE})
    @Path(STUDENT_COURSE_REGISTRATION_RESOURCE_PATH + LETTER_GRADE_RESOURCE_PATH)
    public Response assignGradeToCourseRegistration(
            @PathParam(STUDENT_ID_ELEMENT) int studentId,
            @PathParam(COURSE_ID_ELEMENT) int courseId,
            String letterGrade) {
        Response response = null;
        CourseRegistration updatedCourseRegistration = service.assignGradeToCourseRegistration(studentId, courseId, letterGrade);
        response = Response.ok(updatedCourseRegistration).build();
        return response;
    }
    
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(STUDENT_COURSE_REGISTRATION_RESOURCE_PATH)
    public Response deleteCourseRegistrationById(
            @PathParam(STUDENT_ID_ELEMENT) int studentId,
            @PathParam(COURSE_ID_ELEMENT) int courseId) {
        Response response = null;
        CourseRegistration courseRegistrationDeleted = service.deleteCourseRegistrationById(studentId, courseId);
        response = Response.ok(courseRegistrationDeleted).build();
        return response;
    }

    @GET
    @RolesAllowed({ADMIN_ROLE})
    @Path(LETTER_GRADE_RESOURCE_PATH)
    public Response getLetterGrades() {
        Response response = null;
        List<String> letterGrades = service.getAllLetterGrades();
        response = Response.ok(letterGrades)
                .type(MediaType.APPLICATION_JSON)
                .build();
        return response;
    }

    @GET
    @RolesAllowed({ADMIN_ROLE})
    @Path("/semester")
    public Response getSemesters() {
        Response response = null;
        List<String> semesters = service.getAllSemesters();
        response = Response.ok(semesters)
                .type(MediaType.APPLICATION_JSON)
                .build();
        return response;
    }
    
}