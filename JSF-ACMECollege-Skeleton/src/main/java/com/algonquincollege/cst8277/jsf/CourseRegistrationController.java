/********************************************************************************************************
 * File:  CourseRegistrationController.java
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
import com.algonquincollege.cst8277.entity.CourseRegistration;
import com.algonquincollege.cst8277.entity.Student;
import com.algonquincollege.cst8277.entity.Course;
import com.algonquincollege.cst8277.entity.Professor;
import com.algonquincollege.cst8277.rest.resource.MyObjectMapperProvider;

@Named("courseRegistrationController")
@SessionScoped
public class CourseRegistrationController implements Serializable, MyConstants {
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

    protected List<CourseRegistration> listOfRegistrations;
    protected List<Student> students;
    protected List<Course> courses;
    protected List<Professor> professors;
    protected List<String> letterGrades;
    protected boolean adding;
    protected ResourceBundle bundle;
    
    // Fields for Assign Professor/Grade forms
    protected int selectedStudentIdForAssign;
    protected int selectedCourseIdForAssign;
    protected int selectedProfessorIdForAssign;
    protected int selectedStudentIdForGrade;
    protected int selectedCourseIdForGrade;
    protected String selectedLetterGrade;
    
    static URI uri;
    static HttpAuthenticationFeature auth;
    protected Client client;
    protected WebTarget webTarget;

    public CourseRegistrationController() {
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
        
        loadRegistrations();
        loadStudents();
        loadCourses();
        loadProfessors();
        loadLetterGrades();
    }

    public List<CourseRegistration> getRegistrations() {
        return listOfRegistrations;
    }
    
    public void setRegistrations(List<CourseRegistration> listOfRegistrations) {
        this.listOfRegistrations = listOfRegistrations;
    }
    
    public void loadRegistrations() {
    	Response response = webTarget
                .register(auth)
                .path(COURSE_REGISTRATION_RESOURCE_NAME)
                .request()
                .get();
        listOfRegistrations = response.readEntity(new GenericType<List<CourseRegistration>>(){});
    }

    public List<Student> getStudents() {
        return students;
    }
    
    public void setStudents(List<Student> students) {
        this.students = students;
    }
    
    public void loadStudents() {
    	Response response = webTarget
                .register(auth)
                .path(STUDENT_RESOURCE_NAME)
                .request()
                .get();
        students = response.readEntity(new GenericType<List<Student>>(){});
    }

    public List<Course> getCourses() {
        return courses;
    }
    
    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
    
    public void loadCourses() {
    	Response response = webTarget
                .register(auth)
                .path(COURSE_RESOURCE_NAME)
                .request()
                .get();
        courses = response.readEntity(new GenericType<List<Course>>(){});
    }

    public List<Professor> getProfessors() {
        return professors;
    }
    
    public void setProfessors(List<Professor> professors) {
        this.professors = professors;
    }
    
    public void loadProfessors() {
    	Response response = webTarget
                .register(auth)
                .path(PROFESSOR_RESOURCE_NAME)
                .request()
                .get();
        professors = response.readEntity(new GenericType<List<Professor>>(){});
    }

    public List<String> getLetterGrades() {
        return letterGrades;
    }
    
    public void setLetterGrades(List<String> letterGrades) {
        this.letterGrades = letterGrades;
    }
    
    public void loadLetterGrades() {
    	Response response = webTarget
                .register(auth)
                .path(COURSE_REGISTRATION_RESOURCE_NAME + LETTER_GRADE_RESOURCE_PATH)
                .request()
                .get();
        letterGrades = response.readEntity(new GenericType<List<String>>(){});
    }

    public boolean isAdding() {
        return adding;
    }
    
    public void setAdding(boolean adding) {
        this.adding = adding;
    }
    
    /**
     * Toggles the "add-registration" mode which determines whether the addRegistration form is rendered
     */
    public void toggleAdding() {
        setAdding(!isAdding());
    }

    public String editRegistration(CourseRegistration registration) {
        // no inline editing for CourseRegistration (not part of model)
        return null; //current page
    }

    public String updateRegistration(CourseRegistration registration) {
        // Note: REST endpoint for updating CourseRegistration (year/semester) is not implemented
        // This method is kept for UI consistency but does not perform actual update
        // no inline editing for CourseRegistration (not part of model)
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Update functionality not available", null));
        return null; //current page
    }

    public String cancelUpdate(CourseRegistration registration) {
        // no inline editing for CourseRegistration (not part of model)
        return null; //current page
    }

    public String deleteRegistration(int studentId, int courseId) {
        Response response = webTarget
        		.register(auth)
                .path(COURSE_REGISTRATION_RESOURCE_NAME + "/student/" + studentId + "/course/" + courseId)
                .request()
                .delete();
        if (response.getStatus() == 200) {
            // Remove from list by finding matching registration
            listOfRegistrations.removeIf(reg -> 
                reg.getId().getStudentId() == studentId && reg.getId().getCourseId() == courseId);
        }
        return null; //current page
    }

    public String addNewRegistration(CourseRegistration theNewRegistration) {
        Response response = webTarget
                .register(auth)
                .path(COURSE_REGISTRATION_RESOURCE_NAME)
                .request()
                .post(Entity.json(theNewRegistration));
        
        if (response.getStatus() == 404) {
            try {
                String errorJson = response.readEntity(String.class);
                JsonReader reader = Json.createReader(new StringReader(errorJson));
                JsonObject errorObj = reader.readObject();
                String reasonPhrase = errorObj.getString("reason-phrase");
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, reasonPhrase, null));
            } catch (Exception e) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Student or course not found", null));
            }
        } else {
            CourseRegistration newRegistration = response.readEntity(CourseRegistration.class);
            listOfRegistrations.add(newRegistration);
        }
        return null; //current page
    }

    public String assignProfessor(int studentId, int courseId, int professorId) {
        Professor professor = new Professor();
        professor.setId(professorId);
        Response response = webTarget
                .register(auth)
                .path(COURSE_REGISTRATION_RESOURCE_NAME + "/student/" + studentId + "/course/" + courseId)
                .request()
                .put(Entity.json(professor));
        CourseRegistration updatedRegistration = response.readEntity(CourseRegistration.class);
        if (updatedRegistration != null) {
            int idx = listOfRegistrations.indexOf(updatedRegistration);
            if (idx >= 0) {
                listOfRegistrations.remove(idx);
                listOfRegistrations.add(idx, updatedRegistration);
            }
        }
        return null; //current page
    }

    public String assignGrade(int studentId, int courseId, String letterGrade) {
        Response response = webTarget
                .register(auth)
                .path(COURSE_REGISTRATION_RESOURCE_NAME + "/student/" + studentId + "/course/" + courseId + LETTER_GRADE_RESOURCE_PATH)
                .request()
                .put(Entity.json(letterGrade));
        CourseRegistration updatedRegistration = response.readEntity(CourseRegistration.class);
        if (updatedRegistration != null) {
            int idx = listOfRegistrations.indexOf(updatedRegistration);
            if (idx >= 0) {
                listOfRegistrations.remove(idx);
                listOfRegistrations.add(idx, updatedRegistration);
            }
        }
        return null; //current page
    }

    public String refreshRegistrationForm() {
        Iterator<FacesMessage> facesMessageIterator = facesContext.getMessages();
        while (facesMessageIterator.hasNext()) {
            facesMessageIterator.remove();
        }
        loadRegistrations();
        return MAIN_PAGE_REDIRECT;
    }

    // Getters and setters for Assign Professor/Grade forms
    public int getSelectedStudentIdForAssign() {
        return selectedStudentIdForAssign;
    }

    public void setSelectedStudentIdForAssign(int selectedStudentIdForAssign) {
        this.selectedStudentIdForAssign = selectedStudentIdForAssign;
    }

    public int getSelectedCourseIdForAssign() {
        return selectedCourseIdForAssign;
    }

    public void setSelectedCourseIdForAssign(int selectedCourseIdForAssign) {
        this.selectedCourseIdForAssign = selectedCourseIdForAssign;
    }

    public int getSelectedProfessorIdForAssign() {
        return selectedProfessorIdForAssign;
    }

    public void setSelectedProfessorIdForAssign(int selectedProfessorIdForAssign) {
        this.selectedProfessorIdForAssign = selectedProfessorIdForAssign;
    }

    public int getSelectedStudentIdForGrade() {
        return selectedStudentIdForGrade;
    }

    public void setSelectedStudentIdForGrade(int selectedStudentIdForGrade) {
        this.selectedStudentIdForGrade = selectedStudentIdForGrade;
    }

    public int getSelectedCourseIdForGrade() {
        return selectedCourseIdForGrade;
    }

    public void setSelectedCourseIdForGrade(int selectedCourseIdForGrade) {
        this.selectedCourseIdForGrade = selectedCourseIdForGrade;
    }

    public String getSelectedLetterGrade() {
        return selectedLetterGrade;
    }

    public void setSelectedLetterGrade(String selectedLetterGrade) {
        this.selectedLetterGrade = selectedLetterGrade;
    }
	
}

