/********************************************************************************************************
 * File:  ClubMembershipController.java
 * Course Materials CST 8277
 * 
 * @author (original) Mike Norman
 * @author Teddy Yap
 *
 */
package com.algonquincollege.cst8277.jsf;

import java.io.Serializable;
import java.io.StringReader;
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
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import com.algonquincollege.cst8277.utility.MyConstants;
import com.algonquincollege.cst8277.entity.Student;
import com.algonquincollege.cst8277.entity.StudentClub;
import com.algonquincollege.cst8277.rest.resource.MyObjectMapperProvider;

@Named("clubMembershipController")
@SessionScoped
public class ClubMembershipController implements Serializable, MyConstants {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;

    @Inject
    protected FacesContext facesContext;
    @Inject
    protected ExternalContext externalContext;
    @Inject
    protected ServletContext sc;
    @Inject
    protected LoginBean loginBean;

    protected List<Student> students;
    protected List<StudentClub> clubs;
    protected int selectedStudentId;
    protected int selectedClubId;
    protected ResourceBundle bundle;
    
    static URI uri;
    static HttpAuthenticationFeature auth;
    protected Client client;
    protected WebTarget webTarget;

    public ClubMembershipController() {
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
        
        loadStudents();
        loadClubs();
    }

    public List<Student> getStudents() {
        return students;
    }
    
    public void setStudents(List<Student> students) {
        this.students = students;
    }
    
    public void loadStudents() {
        try {
            Response response = webTarget
                    .register(auth)
                    .path(STUDENT_RESOURCE_NAME)
                    .request()
                    .get();
            if (response.getStatus() == 200) {
                students = response.readEntity(new GenericType<List<Student>>(){});
            } else {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to load students. Status: " + response.getStatus(), null));
            }
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error loading students: " + e.getMessage(), null));
        }
    }

    public List<StudentClub> getClubs() {
        return clubs;
    }
    
    public void setClubs(List<StudentClub> clubs) {
        this.clubs = clubs;
    }
    
    public void loadClubs() {
        try {
            Response response = webTarget
                    .register(auth)
                    .path(STUDENT_CLUB_RESOURCE_NAME)
                    .request()
                    .get();
            if (response.getStatus() == 200) {
                clubs = response.readEntity(new GenericType<List<StudentClub>>(){});
            } else {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to load clubs. Status: " + response.getStatus(), null));
            }
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error loading clubs: " + e.getMessage(), null));
        }
    }

    public int getSelectedStudentId() {
        return selectedStudentId;
    }
    
    public void setSelectedStudentId(int selectedStudentId) {
        this.selectedStudentId = selectedStudentId;
    }

    public int getSelectedClubId() {
        return selectedClubId;
    }
    
    public void setSelectedClubId(int selectedClubId) {
        this.selectedClubId = selectedClubId;
    }

    public String joinClub() {
        if (selectedStudentId <= 0 || selectedClubId <= 0) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select both student and club", null));
            return null;
        }
        com.algonquincollege.cst8277.jsf.ClubMembershipRequest request = new com.algonquincollege.cst8277.jsf.ClubMembershipRequest();
        request.setStudentId(selectedStudentId);
        request.setClubId(selectedClubId);
        Response response = webTarget
                .register(auth)
                .path(CLUB_MEMBERSHIP_RESOURCE_NAME)
                .request()
                .post(Entity.json(request));
        
        if (response.getStatus() == 404) {
            try {
                String errorJson = response.readEntity(String.class);
                JsonReader reader = Json.createReader(new StringReader(errorJson));
                JsonObject errorObj = reader.readObject();
                String reasonPhrase = errorObj.getString("reason-phrase");
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, reasonPhrase, null));
            } catch (Exception e) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Student or club not found", null));
            }
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Student successfully joined club", null));
        }
        return null; //current page
    }

    public String leaveClub() {
        if (selectedStudentId <= 0 || selectedClubId <= 0) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select both student and club", null));
            return null;
        }
        Response response = webTarget
                .register(auth)
                .path(CLUB_MEMBERSHIP_RESOURCE_NAME + "/student/" + selectedStudentId + "/club/" + selectedClubId)
                .request()
                .delete();
        
        if (response.getStatus() == 404) {
            try {
                String errorJson = response.readEntity(String.class);
                JsonReader reader = Json.createReader(new StringReader(errorJson));
                JsonObject errorObj = reader.readObject();
                String reasonPhrase = errorObj.getString("reason-phrase");
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, reasonPhrase, null));
            } catch (Exception e) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Membership not found", null));
            }
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Student successfully left club", null));
        }
        return null; //current page
    }

    public String refreshMembershipForm() {
        Iterator<FacesMessage> facesMessageIterator = facesContext.getMessages();
        while (facesMessageIterator.hasNext()) {
            facesMessageIterator.remove();
        }
        loadStudents();
        loadClubs();
        return MAIN_PAGE_REDIRECT;
    }
	
}

