/********************************************************************************************************
 * File:  Course.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * 
 */
package com.algonquincollege.cst8277.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("unused")

/**
 * The persistent class for the course database table.
 */
//TODO C01 - Add the missing annotations.
//TODO C02 - Do we need a mapped super class?  If so, which one?
public class Course implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String ALL_COURSES_QUERY = "Course.findAll";

	// TODO C03 - Add missing annotations.
	protected String courseCode;

	// TODO C04 - Add missing annotations.
	protected String courseTitle;

	// TODO C05 - Add missing annotations.
	protected Integer creditUnits;

	// TODO C06 - Add missing annotations.
	protected Short online;
	
	// TODO C07 - Add annotations for 1:M relation.  What should be the cascade and fetch types?
	// TODO C08 - Add other missing annotations.
	protected Set<CourseRegistration> courseRegistrations = new HashSet<>();
	
	// TODO C09 - Add missing annotations.
	protected boolean editable = false;

	public Course() {
		super();
	}

	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

	public String getCourseTitle() {
		return courseTitle;
	}

	public void setCourseTitle(String courseTitle) {
		this.courseTitle = courseTitle;
	}

	public Integer getCreditUnits() {
		return creditUnits;
	}

	public void setCreditUnits(Integer creditUnits) {
		this.creditUnits = creditUnits;
	}

	public Short getOnline() {
		return online;
	}

	public void setOnline(Short online) {
		this.online = online;
	}

	public Set<CourseRegistration> getCourseRegistrations() {
		return courseRegistrations;
	}

	public void setCourseRegistrations(Set<CourseRegistration> courseRegistrations) {
		this.courseRegistrations = courseRegistrations;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	//Inherited hashCode/equals is sufficient for this Entity class

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Course[id = ").append(id).append(", courseCode = ").append(courseCode).append(", courseTitle = ")
				.append(courseTitle).append(", creditUnits = ").append(creditUnits).append(", online = ").append(online)
				.append(", created = ").append(created).append(", updated = ").append(updated).append(", version = ").append(version).append("]");
		return builder.toString();
	}
	
}
