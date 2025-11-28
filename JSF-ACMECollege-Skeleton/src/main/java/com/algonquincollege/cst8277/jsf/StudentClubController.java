/********************************************************************************************************
 * File:  StudentClubController.java
 * Course Materials CST 8277
 * 
 * @author (original) Mike Norman
 * @author Teddy Yap
 *
 */
package com.algonquincollege.cst8277.jsf;

import java.io.Serializable;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algonquincollege.cst8277.utility.MyConstants;
import com.algonquincollege.cst8277.entity.StudentClub;
import com.algonquincollege.cst8277.rest.resource.MyObjectMapperProvider;

@Named("studentClubController")
@SessionScoped
public class StudentClubController implements Serializable, MyConstants {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LogManager.getLogger();

    @Inject
    protected FacesContext facesContext;
    @Inject
    protected ExternalContext externalContext;
    @Inject
    protected ServletContext sc;
    @Inject
    protected LoginBean loginBean;

    protected List<StudentClub> listOfClubs;
    protected boolean adding;
    protected ResourceBundle bundle;
    
    static URI uri;
    static HttpAuthenticationFeature auth;
    protected Client client;
    protected WebTarget webTarget;

    public StudentClubController() {
    	super();
    }
    
    @PostConstruct
    public void initialize() {
        uri = UriBuilder
                .fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION)
                .scheme(HTTP_SCHEMA)
                .host(HOST)
                .port(PORT)
                .build();
        
        auth = HttpAuthenticationFeature.basic(loginBean.getUsername(), loginBean.getPassword());
        
        client = ClientBuilder.newClient(
                new ClientConfig().register(MyObjectMapperProvider.class).register(new LoggingFeature()));
        
        webTarget = client.target(uri);
        
        loadClubs();
    }

    public List<StudentClub> getClubs() {
        return listOfClubs;
    }
    
    public void setClubs(List<StudentClub> listOfClubs) {
        this.listOfClubs = listOfClubs;
    }
    
    public void loadClubs() {
    	Response response = webTarget
                .register(auth)
                .path(STUDENT_CLUB_RESOURCE_NAME)
                .request()
                .get();
        listOfClubs = response.readEntity(new GenericType<List<StudentClub>>(){});
    }

    public boolean isAdding() {
        return adding;
    }
    
    public void setAdding(boolean adding) {
        this.adding = adding;
    }
    
    /**
     * Toggles the "add-club" mode which determines whether the addClub form is rendered
     */
    public void toggleAdding() {
        setAdding(!isAdding());
    }

    public String editClub(StudentClub club) {
        club.setEditable(true);
        return null; //current page
    }

    public String updateClub(StudentClub club) {
        Response response = webTarget
        		.register(auth)
                .path(STUDENT_CLUB_RESOURCE_NAME + "/" + club.getId())
                .request()
                .put(Entity.json(club));
        StudentClub updatedClub = response.readEntity(StudentClub.class);
        updatedClub.setEditable(false);
        int idx = listOfClubs.indexOf(club);
        listOfClubs.remove(idx);
        listOfClubs.add(idx, updatedClub);
        return null; //current page
    }

    public String cancelUpdate(StudentClub club) {
        club.setEditable(false);
        return null; //current page
    }

    public String deleteClub(int clubId) {
        Response response = webTarget
        		.register(auth)
                .path(STUDENT_CLUB_RESOURCE_NAME + "/" + clubId)
                .request()
                .get();
        StudentClub clubToBeDeleted = response.readEntity(StudentClub.class);
        if (clubToBeDeleted != null) {
        	response = webTarget     	
                    .register(auth)
                    .path(STUDENT_CLUB_RESOURCE_NAME + "/" + clubToBeDeleted.getId())
                    .request()
                    .delete();
        	StudentClub deletedClub = response.readEntity(StudentClub.class);
            listOfClubs.remove(deletedClub);
        }
        return null; //current page
    }

    public String addNewClub(StudentClub theNewClub) {
        // Log data before sending to REST API
        LOG.debug("Attempting to add new StudentClub - Name: {}, Desc: {}, Academic: {}", 
                theNewClub != null ? theNewClub.getName() : "null",
                theNewClub != null ? theNewClub.getDesc() : "null",
                theNewClub != null ? theNewClub.getAcademic() : "null");
        
        // Serialize manually to include club-type
        String jsonPayload;
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper()
                .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
                .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            
            jsonPayload = mapper.writeValueAsString(theNewClub);
            LOG.debug("JSON Payload: {}", jsonPayload);
        } catch (Exception e) {
            LOG.error("Failed to serialize club", e);
            return null;
        }
        
        Response response = webTarget
                .register(auth)
                .path(STUDENT_CLUB_RESOURCE_NAME)
                .request()
                .post(Entity.entity(jsonPayload, jakarta.ws.rs.core.MediaType.APPLICATION_JSON));
        
        int statusCode = response.getStatus();
        LOG.debug("Response status code: {}", statusCode);
        
        FacesContext facesContext = FacesContext.getCurrentInstance();
        
        if (statusCode == 200) {
            StudentClub newClub = response.readEntity(StudentClub.class);
            listOfClubs.add(newClub);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Club added successfully!", null));
        } else if (statusCode == 409) {
            response.readEntity(String.class);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Duplicate club name", null));
        } else if (statusCode == 400) {
            String errorResponse = response.readEntity(String.class);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error: " + errorResponse, null));
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Status " + statusCode, null));
        }
        return null; //current page
    }

    public String refreshClubForm() {
        Iterator<FacesMessage> facesMessageIterator = facesContext.getMessages();
        while (facesMessageIterator.hasNext()) {
            facesMessageIterator.remove();
        }
        loadClubs();
        return MAIN_PAGE_REDIRECT;
    }
	
}

