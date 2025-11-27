/********************************************************************************************************
 * File:  ProfessorResource.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * 
 */
package com.algonquincollege.cst8277.rest.resource;

import static com.algonquincollege.cst8277.utility.MyConstants.ADMIN_ROLE;
import static com.algonquincollege.cst8277.utility.MyConstants.PROFESSOR_RESOURCE_NAME;
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
import com.algonquincollege.cst8277.entity.Professor;

@Path(PROFESSOR_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProfessorResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;

    @GET
    // Only a user with the SecurityRole 'ADMIN_ROLE' can get the list of all professors.
    @RolesAllowed({ADMIN_ROLE})
    public Response getProfessors() {
        LOG.debug("retrieving all professors ...");
        List<Professor> professors = service.getAllProfessors();
        Response response = Response.ok(professors).build();
        return response;
    }

    @GET
    // A user with either the role 'ADMIN_ROLE' or 'USER_ROLE' can get a specific professor.
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getProfessorById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("try to retrieve specific professor " + id);
        Response response = null;
        Professor professor = service.getProfessorById(id);
        response = Response.status(professor == null ? Status.NOT_FOUND : Status.OK).entity(professor).build();
        return response;
    }

    @POST
    // Only a user with the SecurityRole 'ADMIN_ROLE' can add a new professor.
    @RolesAllowed({ADMIN_ROLE})
    public Response addProfessor(Professor newProfessor) {
        Response response = null;
        Professor newProfessorWithIdTimestamps = service.persistProfessor(newProfessor);
        response = Response.ok(newProfessorWithIdTimestamps).build();
        return response;
    }

    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response updateProfessorById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, Professor professorWithUpdates) {
        Response response = null;
        Professor updatedProfessor = service.updateProfessorById(id, professorWithUpdates);
        response = Response.ok(updatedProfessor).build();
        return response;
    }
    
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteProfessorById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        Response response = null;
        Professor professorDeleted = service.deleteProfessorById(id);
        response = Response.ok(professorDeleted).build();
        return response;
    }
    
}

