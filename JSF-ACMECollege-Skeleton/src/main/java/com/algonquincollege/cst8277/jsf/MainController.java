/********************************************************************************************************
 * File:  MainController.java
 * Course Materials CST 8277
 * 
 * @author Teddy Yap
 *
 */
package com.algonquincollege.cst8277.jsf;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.algonquincollege.cst8277.utility.MyConstants;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("mainController")
@SessionScoped
public class MainController implements Serializable, MyConstants {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;

    public static final String NONE = "None";
    public static final String STUDENT_MANAGEMENT = "Student Management";
    public static final String COURSE_MANAGEMENT = "Course Management";
    public static final String PROFESSOR_MANAGEMENT = "Professor Management";
    public static final String STUDENT_CLUB_MANAGEMENT = "Student Club Management";
    public static final String COURSE_REGISTRATION = "Course Registration";
    public static final String ASSIGN_PROFESSOR = "Assign Professor";
    public static final String ASSIGN_GRADE = "Assign Grade";
    public static final String CLUB_MEMBERSHIP_REGISTRATION = "Club Membership Registration";
    
    @Inject
    protected StudentController studentController;

    //TODO Add your own controllers here
    
    protected String optionChosen = "None";
    protected List<String> options = Stream.of(NONE, STUDENT_MANAGEMENT, COURSE_MANAGEMENT, PROFESSOR_MANAGEMENT, STUDENT_CLUB_MANAGEMENT, COURSE_REGISTRATION, ASSIGN_PROFESSOR, ASSIGN_GRADE, CLUB_MEMBERSHIP_REGISTRATION).collect(Collectors.toList());
    
    public MainController() {
    	super();
    }
    
    public String getOptionChosen() {
    	return optionChosen;
    }
    
    public void setOptionChosen(String option) {
    	optionChosen = option;
    }
    
    public List<String> getOptions() {
    	return options;
    }
    
    public String submitForm() {
    	return null; //current page
    }
    
    public void loadData() {
    	switch (optionChosen) {
    		case STUDENT_MANAGEMENT: studentController.loadStudents();
    		break;
    		//TODO Add your own cases here
    	}
    }
    
}
