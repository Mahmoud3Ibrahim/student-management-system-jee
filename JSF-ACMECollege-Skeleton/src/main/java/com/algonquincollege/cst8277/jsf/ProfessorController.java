/********************************************************************************************************
 * File:  ProfessorController.java
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
import com.algonquincollege.cst8277.entity.Professor;
import com.algonquincollege.cst8277.rest.resource.MyObjectMapperProvider;

@Named("professorController")
@SessionScoped
public class ProfessorController implements Serializable, MyConstants {
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

    protected List<Professor> listOfProfessors;
    protected boolean adding;
    protected ResourceBundle bundle;
    
    static URI uri;
    static HttpAuthenticationFeature auth;
    protected Client client;
    protected WebTarget webTarget;

    public ProfessorController() {
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
        
        loadProfessors();
    }

    public List<Professor> getProfessors() {
        return listOfProfessors;
    }
    
    public void setProfessors(List<Professor> listOfProfessors) {
        this.listOfProfessors = listOfProfessors;
    }
    
    public void loadProfessors() {
    	Response response = webTarget
                .register(auth)
                .path(PROFESSOR_RESOURCE_NAME)
                .request()
                .get();
        listOfProfessors = response.readEntity(new GenericType<List<Professor>>(){});
    }

    public boolean isAdding() {
        return adding;
    }
    
    public void setAdding(boolean adding) {
        this.adding = adding;
    }
    
    /**
     * Toggles the "add-professor" mode which determines whether the addProfessor form is rendered
     */
    public void toggleAdding() {
        setAdding(!isAdding());
    }

    public String editProfessor(Professor professor) {
        professor.setEditable(true);
        return null; //current page
    }

    public String updateProfessor(Professor professor) {
        Response response = webTarget
        		.register(auth)
                .path(PROFESSOR_RESOURCE_NAME + "/" + professor.getId())
                .request()
                .put(Entity.json(professor));
        Professor updatedProfessor = response.readEntity(Professor.class);
        updatedProfessor.setEditable(false);
        int idx = listOfProfessors.indexOf(professor);
        listOfProfessors.remove(idx);
        listOfProfessors.add(idx, updatedProfessor);
        return null; //current page
    }

    public String cancelUpdate(Professor professor) {
        professor.setEditable(false);
        return null; //current page
    }

    public String deleteProfessor(int professorId) {
        Response response = webTarget
        		.register(auth)
                .path(PROFESSOR_RESOURCE_NAME + "/" + professorId)
                .request()
                .get();
        Professor professorToBeDeleted = response.readEntity(Professor.class);
        if (professorToBeDeleted != null) {
        	response = webTarget     	
                    .register(auth)
                    .path(PROFESSOR_RESOURCE_NAME + "/" + professorToBeDeleted.getId())
                    .request()
                    .delete();
        	Professor deletedProfessor = response.readEntity(Professor.class);
            listOfProfessors.remove(deletedProfessor);
        }
        return null; //current page
    }

    public String addNewProfessor(Professor theNewProfessor) {
        Response response = webTarget
                .register(auth)
                .path(PROFESSOR_RESOURCE_NAME)
                .request()
                .post(Entity.json(theNewProfessor));
        
        if (response.getStatus() == 409) {
            try {
                String errorJson = response.readEntity(String.class);
                JsonReader reader = Json.createReader(new StringReader(errorJson));
                JsonObject errorObj = reader.readObject();
                String reasonPhrase = errorObj.getString("reason-phrase");
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, reasonPhrase, null));
            } catch (Exception e) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Duplicate entry", null));
            }
        } else {
            Professor newProfessor = response.readEntity(Professor.class);
            listOfProfessors.add(newProfessor);
        }
        return null; //current page
    }

    public String refreshProfessorForm() {
        Iterator<FacesMessage> facesMessageIterator = facesContext.getMessages();
        while (facesMessageIterator.hasNext()) {
            facesMessageIterator.remove();
        }
        loadProfessors();
        return MAIN_PAGE_REDIRECT;
    }
	
}

