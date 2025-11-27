/********************************************************************************************************
 * File:  NewStudentView.java
 * Course Materials CST 8277
 * 
 * @author Mike Norman
 * @author Teddy Yap
 * 
 */
package com.algonquincollege.cst8277.jsf;

import java.io.Serializable;

import com.algonquincollege.cst8277.entity.Student;

import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("newStudent")
@ViewScoped
public class NewStudentView implements Serializable {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;

    protected String firstName;
    protected String lastName;
    protected String email;
    protected String phone;
    protected String program;
    
    @Inject
    @ManagedProperty("#{studentController}")
    protected StudentController studentController;

    public NewStudentView() {
    }
    
    /**
     * @return  firstName
     */
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * @param firstName  firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return  lastName
     */
    public String getLastName() {
        return lastName;
    }
    
    /**
     * @param LastName  LastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    /**
     * @return  email
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * @param email  email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return  phone
     */
    public String getPhone() {
        return phone;
    }
    
    /**
     * @param phone  phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return  program
     */
    public String getProgram() {
        return program;
    }
    
    /**
     * @param program  program to set
     */
    public void setProgram(String program) {
        this.program = program;
    }

    public void addStudent() {
        Student theNewStudent = new Student();
        theNewStudent.setFirstName(getFirstName());
        theNewStudent.setLastName(getLastName());
        theNewStudent.setEmail(getEmail());
        theNewStudent.setPhone(getPhone());
        theNewStudent.setProgram(getProgram());
        studentController.addNewStudent(theNewStudent);
        
        //clean up
		studentController.toggleAdding();
        setFirstName(null);
        setLastName(null);
        setEmail(null);
        setPhone(null);
        setProgram(null);
    }
    
}
