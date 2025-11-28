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
        try {
            StudentClub newStudentClubWithIdTimestamps = service.persistStudentClub(newStudentClub);
            return Response.ok(newStudentClubWithIdTimestamps)
                .type(MediaType.APPLICATION_JSON)
                .build();
        } catch (RuntimeException e) {
            return Response.status(Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(new HttpErrorResponse(409, e.getMessage()))
                .build();
        }
    }

    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response updateStudentClubById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, StudentClub studentClubWithUpdates) {
        Response response = null;
        StudentClub updatedStudentClub = service.updateStudentClubById(id, studentClubWithUpdates);
        response = Response.ok(updatedStudentClub)
            .type(MediaType.APPLICATION_JSON)
            .build();
        return response;
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

