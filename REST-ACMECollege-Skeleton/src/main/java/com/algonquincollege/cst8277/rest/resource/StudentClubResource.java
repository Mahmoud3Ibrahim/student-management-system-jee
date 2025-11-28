/********************************************************************************************************
 * File:  StudentClubResource.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * 
 */
package com.algonquincollege.cst8277.rest.resource;

import static com.algonquincollege.cst8277.utility.MyConstants.ADMIN_ROLE;
import static com.algonquincollege.cst8277.utility.MyConstants.STUDENT_CLUB_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static com.algonquincollege.cst8277.utility.MyConstants.RESOURCE_PATH_ID_PATH;
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
import com.algonquincollege.cst8277.entity.StudentClub;
import com.algonquincollege.cst8277.rest.resource.HttpErrorResponse;

@Path(STUDENT_CLUB_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StudentClubResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;

    @GET
    // Only a user with the SecurityRole 'ADMIN_ROLE' can get the list of all student clubs.
    @RolesAllowed({ADMIN_ROLE})
    public Response getStudentClubs() {
        LOG.debug("retrieving all student clubs ...");
        List<StudentClub> studentClubs = service.getAllStudentClubs();
        Response response = Response.ok(studentClubs).build();
        return response;
    }

    @GET
    // A user with either the role 'ADMIN_ROLE' or 'USER_ROLE' can get a specific student club.
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getStudentClubById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("try to retrieve specific student club " + id);
        Response response = null;
        StudentClub studentClub = service.getStudentClubById(id);
        response = Response.status(studentClub == null ? Status.NOT_FOUND : Status.OK).entity(studentClub).build();
        return response;
    }

    @POST
    // Only a user with the SecurityRole 'ADMIN_ROLE' can add a new student club (Academic or NonAcademic).
    @RolesAllowed({ADMIN_ROLE})
    public Response addStudentClub(StudentClub newStudentClub) {
        LOG.debug("Received request to add new StudentClub");
        
        // Log received data
        if (newStudentClub == null) {
            LOG.warn("Received null StudentClub object");
        } else {
            LOG.debug("StudentClub received - Name: '{}', Desc: '{}', Academic: {}, Class: {}", 
                    newStudentClub.getName(),
                    newStudentClub.getDesc(),
                    newStudentClub.getAcademic(),
                    newStudentClub.getClass().getSimpleName());
            LOG.debug("StudentClub toString: {}", newStudentClub.toString());
        }
        
        if (newStudentClub == null ||
                newStudentClub.getName() == null || newStudentClub.getName().trim().isEmpty() ||
                newStudentClub.getDesc() == null || newStudentClub.getDesc().trim().isEmpty()) {
            LOG.warn("Validation failed - Name: '{}', Desc: '{}'", 
                    newStudentClub != null ? newStudentClub.getName() : "null",
                    newStudentClub != null ? newStudentClub.getDesc() : "null");
            return Response.status(Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new HttpErrorResponse(400, "Name and description cannot be null or empty"))
                    .build();
        }
        try {
            LOG.debug("Attempting to persist StudentClub");
            StudentClub newStudentClubWithIdTimestamps = service.persistStudentClub(newStudentClub);
            LOG.debug("Successfully persisted StudentClub with ID: {}", newStudentClubWithIdTimestamps.getId());
            return Response.ok(newStudentClubWithIdTimestamps)
                .type(MediaType.APPLICATION_JSON)
                .build();
        } catch (IllegalArgumentException e) {
            LOG.warn("IllegalArgumentException while persisting StudentClub: {}", e.getMessage());
            return Response.status(Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new HttpErrorResponse(400, e.getMessage()))
                    .build();
        } catch (RuntimeException e) {
            LOG.warn("RuntimeException while persisting StudentClub: {}", e.getMessage(), e);
            return Response.status(Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(new HttpErrorResponse(409, e.getMessage()))
                .build();
        } catch (Exception e) {
            LOG.error("Unexpected exception while persisting StudentClub: {}", e.getMessage(), e);
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(new HttpErrorResponse(500, "Internal server error: " + e.getMessage()))
                .build();
        }
    }

    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response updateStudentClubById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, StudentClub studentClubWithUpdates) {
        if (studentClubWithUpdates == null ||
                studentClubWithUpdates.getName() == null || studentClubWithUpdates.getName().trim().isEmpty() ||
                studentClubWithUpdates.getDesc() == null || studentClubWithUpdates.getDesc().trim().isEmpty()) {
            return Response.status(Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new HttpErrorResponse(400, "Name and description cannot be null or empty"))
                    .build();
        }
        try {
            StudentClub updatedStudentClub = service.updateStudentClubById(id, studentClubWithUpdates);
            if (updatedStudentClub == null) {
                return Response.status(Status.NOT_FOUND)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(new HttpErrorResponse(404, "Student club not found"))
                        .build();
            }
            return Response.ok(updatedStudentClub)
                .type(MediaType.APPLICATION_JSON)
                .build();
        } catch (Exception e) {
            LOG.error("Unexpected exception while updating StudentClub: {}", e.getMessage(), e);
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(new HttpErrorResponse(500, "Internal server error: " + e.getMessage()))
                .build();
        }
    }
    
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteStudentClubById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        Response response = null;
        StudentClub studentClubDeleted = service.deleteStudentClubById(id);
        response = Response.ok(studentClubDeleted).build();
        return response;
    }
    
}

