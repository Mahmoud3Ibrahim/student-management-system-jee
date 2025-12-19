/********************************************************************************************************
 * File:  NewCourseRegistrationView.java
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

import com.algonquincollege.cst8277.entity.CourseRegistration;
import com.algonquincollege.cst8277.entity.Student;
import com.algonquincollege.cst8277.entity.Course;

import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("newCourseRegistration")
@ViewScoped
public class NewCourseRegistrationView implements Serializable {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;

    protected int studentId;
    protected int courseId;
    protected int year;
    protected String semester;
    
    @Inject
    @ManagedProperty("#{courseRegistrationController}")
    protected CourseRegistrationController courseRegistrationController;

    public NewCourseRegistrationView() {
    }
    
    /**
     * @return  studentId
     */
    public int getStudentId() {
        return studentId;
    }
    
    /**
     * @param studentId  studentId to set
     */
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    /**
     * @return  courseId
     */
    public int getCourseId() {
        return courseId;
    }
    
    /**
     * @param courseId  courseId to set
     */
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    /**
     * @return  year
     */
    public int getYear() {
        return year;
    }
    
    /**
     * @param year  year to set
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * @return  semester
     */
    public String getSemester() {
        return semester;
    }
    
    /**
     * @param semester  semester to set
     */
    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void addRegistration() {
        CourseRegistration theNewRegistration = new CourseRegistration();
        Student student = new Student();
        student.setId(studentId);
        Course course = new Course();
        course.setId(courseId);
        theNewRegistration.setStudent(student);
        theNewRegistration.setCourse(course);
        theNewRegistration.setYear(getYear());
        theNewRegistration.setSemester(getSemester());
        courseRegistrationController.addNewRegistration(theNewRegistration);
        
        //clean up
		courseRegistrationController.toggleAdding();
        setStudentId(0);
        setCourseId(0);
        setYear(0);
        setSemester(null);
    }
    
}

