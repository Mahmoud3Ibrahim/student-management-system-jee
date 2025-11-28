/********************************************************************************************************
 * File:  NewCourseView.java
 * Course Materials CST 8277
 * 
 * @author Mike Norman
 * @author Teddy Yap
 * 
 */
package com.algonquincollege.cst8277.jsf;

import java.io.Serializable;

import com.algonquincollege.cst8277.entity.Course;

import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("newCourse")
@ViewScoped
public class NewCourseView implements Serializable {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;

    protected String courseCode;
    protected String courseTitle;
    protected Integer creditUnits;
    protected Short online;
    
    @Inject
    @ManagedProperty("#{courseController}")
    protected CourseController courseController;

    public NewCourseView() {
    }
    
    /**
     * @return  courseCode
     */
    public String getCourseCode() {
        return courseCode;
    }
    
    /**
     * @param courseCode  courseCode to set
     */
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    /**
     * @return  courseTitle
     */
    public String getCourseTitle() {
        return courseTitle;
    }
    
    /**
     * @param courseTitle  courseTitle to set
     */
    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    /**
     * @return  creditUnits
     */
    public Integer getCreditUnits() {
        return creditUnits;
    }
    
    /**
     * @param creditUnits  creditUnits to set
     */
    public void setCreditUnits(Integer creditUnits) {
        this.creditUnits = creditUnits;
    }

    /**
     * @return  online
     */
    public Short getOnline() {
        return online;
    }
    
    /**
     * @param online  online to set
     */
    public void setOnline(Short online) {
        this.online = online;
    }

    public void addCourse() {
        Course theNewCourse = new Course();
        theNewCourse.setCourseCode(getCourseCode());
        theNewCourse.setCourseTitle(getCourseTitle());
        theNewCourse.setCreditUnits(getCreditUnits());
        theNewCourse.setOnline(getOnline());
        courseController.addNewCourse(theNewCourse);
        
        //clean up
		courseController.toggleAdding();
        setCourseCode(null);
        setCourseTitle(null);
        setCreditUnits(null);
        setOnline(null);
    }
    
}

