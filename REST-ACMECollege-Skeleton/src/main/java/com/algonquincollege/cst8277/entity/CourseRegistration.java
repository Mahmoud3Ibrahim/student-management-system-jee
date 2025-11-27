/********************************************************************************************************
 * File:  CourseRegistration.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * 
 */
package com.algonquincollege.cst8277.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("unused")
/**
 * The persistent class for the course_registration database table.
 */
@Entity
@Table(name = "course_registration")
@Access(AccessType.FIELD)
@NamedQuery(name = CourseRegistration.ALL_COURSE_REGISTRATIONS_QUERY_NAME, query = "SELECT cr FROM CourseRegistration cr")
@NamedQuery(name = CourseRegistration.QUERY_SPECIFIC_COURSE_REGISTRATION, query = "SELECT cr FROM CourseRegistration cr WHERE cr.student.id = :param1 AND cr.course.id = :param2")
@JsonIgnoreProperties({"student", "course", "professor"})
public class CourseRegistration extends PojoBaseCompositeKey<CourseRegistrationPK> implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String ALL_COURSE_REGISTRATIONS_QUERY_NAME = "CourseRegistration.findAll";
    public static final String QUERY_SPECIFIC_COURSE_REGISTRATION = "CourseRegistration.findSpecificCourseRegistration";

    // Hint - What annotation is used for a composite primary key type?
	@EmbeddedId
	protected CourseRegistrationPK id;

	// @MapsId is used to map a part of composite key to an entity.
	@MapsId("studentId")
    @ManyToOne(cascade = {}, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
	@JsonIgnore
	protected Student student;

	//TODO CR01 - Add missing annotations.  Similar to student, this field is a part of the composite key of this entity.  What should be the cascade and fetch types?  Reference to a course is not optional.
	@MapsId("courseId")
	@ManyToOne(cascade = {}, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "course_id", referencedColumnName = "course_id", nullable = false)
	@JsonIgnore
	protected Course course;

	//TODO CR02 - Add missing annotations.  What should be the cascade and fetch types?
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
	@JoinColumn(name = "professor_id", referencedColumnName = "professor_id", nullable = true)
	@JsonIgnore
	protected Professor professor;

	//TODO CR03 - Add missing annotations.
	@Basic(optional = false)
	@Column(name = "year", nullable = false)
	protected int year;

	//TODO CR03 - Add missing annotations.
	@Basic(optional = false)
	@Column(name = "semester", nullable = false, length = 6)
	protected String semester;

	//TODO CR03 - Add missing annotations.
	@Basic(optional = true)
	@Column(name = "letter_grade", nullable = true, length = 3)
	protected String letterGrade;

	public CourseRegistration() {
		id = new CourseRegistrationPK();
	}

	@Override
	public CourseRegistrationPK getId() {
		return id;
	}

	@Override
	public void setId(CourseRegistrationPK id) {
		this.id = id;
	}

	@JsonIgnore
	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		id.setStudentId(student.getId());
		this.student = student;
	}

	@JsonIgnore
	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		id.setCourseId(course.getId());
		this.course = course;
	}

	@JsonIgnore
	public Professor getProfessor() {
		return professor;
	}

	public void setProfessor(Professor professor) {
		this.professor = professor;
	}

	public int getYear() {
		return year;
	}
	
	public void setYear(int year) {
		this.year = year;
	}

	public String getSemester() {
		return semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}

	public String getLetterGrade() {
		return letterGrade;
	}

	public void setLetterGrade(String letterGrade) {
		this.letterGrade = letterGrade;
	}

	//Inherited hashCode/equals is sufficient for this entity class

}