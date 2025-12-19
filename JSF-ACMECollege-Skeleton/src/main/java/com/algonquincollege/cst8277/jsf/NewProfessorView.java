/********************************************************************************************************
 * File:  NewProfessorView.java
 * Course Materials CST 8277
 * 
 * This is an academic graduation project for CST8277 course.
 *
 * @author Mike Norman
 * @author Teddy Yap
 * @author Mahmoud Ibrahim
 * 
 */
package com.algonquincollege.cst8277.jsf;

import java.io.Serializable;

import com.algonquincollege.cst8277.entity.Professor;

import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("newProfessor")
@ViewScoped
public class NewProfessorView implements Serializable {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;

    protected String firstName;
    protected String lastName;
    protected String degree;
    
    @Inject
    @ManagedProperty("#{professorController}")
    protected ProfessorController professorController;

    public NewProfessorView() {
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
     * @param lastName  lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return  degree
     */
    public String getDegree() {
        return degree;
    }
    
    /**
     * @param degree  degree to set
     */
    public void setDegree(String degree) {
        this.degree = degree;
    }

    public void addProfessor() {
        Professor theNewProfessor = new Professor();
        theNewProfessor.setFirstName(getFirstName());
        theNewProfessor.setLastName(getLastName());
        theNewProfessor.setDegree(getDegree());
        professorController.addNewProfessor(theNewProfessor);
        
        //clean up
		professorController.toggleAdding();
        setFirstName(null);
        setLastName(null);
        setDegree(null);
    }
    
}

