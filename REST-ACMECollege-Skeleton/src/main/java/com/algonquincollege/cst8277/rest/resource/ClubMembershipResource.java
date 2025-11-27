/********************************************************************************************************
 * File:  ClubMembershipResource.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * 
 */
package com.algonquincollege.cst8277.rest.resource;

import static com.algonquincollege.cst8277.utility.MyConstants.ADMIN_ROLE;
import static com.algonquincollege.cst8277.utility.MyConstants.CLUB_MEMBERSHIP_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.CLUB_ID_ELEMENT;
import static com.algonquincollege.cst8277.utility.MyConstants.STUDENT_ID_ELEMENT;
import static com.algonquincollege.cst8277.utility.MyConstants.STUDENT_CLUB_MEMBERSHIP_PATH;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import com.algonquincollege.cst8277.ejb.ACMECollegeService;
import com.algonquincollege.cst8277.entity.StudentClub;

@Path(CLUB_MEMBERSHIP_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClubMembershipResource {

    @EJB
    protected ACMECollegeService service;

    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addMembership(ClubMembershipRequest request) {
        if (request == null || request.getStudentId() <= 0 || request.getClubId() <= 0) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(new HttpErrorResponse(400, "studentId and clubId are required"))
                    .build();
        }
        StudentClub club = service.addStudentToClub(request.getStudentId(), request.getClubId());
        if (club == null) {
            return Response.status(Status.NOT_FOUND)
                    .entity(new HttpErrorResponse(404, "Student or club not found"))
                    .build();
        }
        return Response.ok(request).build();
    }

    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(STUDENT_CLUB_MEMBERSHIP_PATH)
    public Response removeMembership(
            @PathParam(STUDENT_ID_ELEMENT) int studentId,
            @PathParam(CLUB_ID_ELEMENT) int clubId) {
        boolean removed = service.removeStudentFromClub(studentId, clubId);
        if (!removed) {
            return Response.status(Status.NOT_FOUND)
                    .entity(new HttpErrorResponse(404, "Membership not found"))
                    .build();
        }
        return Response.ok().build();
    }
}

