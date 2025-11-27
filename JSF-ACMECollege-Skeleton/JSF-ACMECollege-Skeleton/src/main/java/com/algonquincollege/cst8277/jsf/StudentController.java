/********************************************************************************************************
 * File:  StudentController.java
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

import com.algonquincollege.cst8277.utility.MyConstants;
import com.algonquincollege.cst8277.entity.Student;
import com.algonquincollege.cst8277.rest.resource.MyObjectMapperProvider;

@Named("studentController")
@SessionScoped
public class StudentController implements Serializable, MyConstants {
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

    protected List<Student> listOfStudents;
    protected List<String> listOfPrograms;
    protected boolean adding;
    protected ResourceBundle bundle;
    
    static URI uri;
    static HttpAuthenticationFeature auth;
    protected Client client;
    protected WebTarget webTarget;

    public StudentController() {
    	super();
        //bundle = ResourceBundle.getBundle(BUNDLE_BASENAME, facesContext.getViewRoot().getLocale());
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
    }

    public List<Student> getStudents() {
        return listOfStudents;
    }
    
    public void setStudents(List<Student> listOfStudents) {
        this.listOfStudents = listOfStudents;
    }
    
    public void loadStudents() {
    	Response response = webTarget
                .register(auth)
                .path(STUDENT_RESOURCE_NAME)
                .request()
                .get();
        listOfStudents = response.readEntity(new GenericType<List<Student>>(){});
    }

    public boolean isAdding() {
        return adding;
    }
    
    public void setAdding(boolean adding) {
        this.adding = adding;
    }
    
    /**
     * Toggles the "add-student" mode which determines whether the addStudent form is rendered
     */
    public void toggleAdding() {
        setAdding(!isAdding());
    }

    public String editStudent(Student student) {
        student.setEditable(true);
        return null; //current page
    }

    public String updateStudent(Student student) {
        Response response = webTarget
        		.register(auth)
                .path(STUDENT_RESOURCE_NAME + "/" + student.getId())
                .request()
                .put(Entity.json(student));
        Student updatedStudent = response.readEntity(Student.class);
        updatedStudent.setEditable(false);
        int idx = listOfStudents.indexOf(student);
        listOfStudents.remove(idx);
        listOfStudents.add(idx, updatedStudent);
        return null; //current page
    }

    public String cancelUpdate(Student student) {
        student.setEditable(false);
        return null; //current page
    }

    public String deleteStudent(int studentId) {
        Response response = webTarget
        		.register(auth)
                .path(STUDENT_RESOURCE_NAME + "/" + studentId)
                .request()
                .get();
        Student studentToBeDeleted = response.readEntity(Student.class);
        if (studentToBeDeleted != null) {
        	response = webTarget     	
                    .register(auth)
                    .path(STUDENT_RESOURCE_NAME + "/" + studentToBeDeleted.getId())
                    .request()
                    .delete();
        	Student deletedStudent = response.readEntity(Student.class);
            listOfStudents.remove(deletedStudent);
        }
        return null; //current page
    }

    public String addNewStudent(Student theNewStudent) {
        Response response = webTarget
                .register(auth)
                .path(STUDENT_RESOURCE_NAME)
                .request()
                .post(Entity.json(theNewStudent));
        Student newStudent = response.readEntity(Student.class);
        listOfStudents.add(newStudent);
        return null; //current page
    }

    public String refreshStudentForm() {
        Iterator<FacesMessage> facesMessageIterator = facesContext.getMessages();
        while (facesMessageIterator.hasNext()) {
            facesMessageIterator.remove();
        }
        loadStudents();
        return MAIN_PAGE_REDIRECT;
    }
    
	public List<String> getPrograms() {
    	Response response = webTarget
                .register(auth)
                .path(STUDENT_RESOURCE_NAME + PROGRAM_RESOURCE_PATH)
                .request()
                .get();
        listOfPrograms = response.readEntity(new GenericType<List<String>>(){});
        return listOfPrograms;
	}
	
}