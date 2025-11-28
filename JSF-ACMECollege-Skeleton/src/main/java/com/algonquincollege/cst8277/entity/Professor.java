/********************************************************************************************************
 * File:  Professor.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * 
 */
package com.algonquincollege.cst8277.entity;

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
//TODO P01 - Add the missing annotations.
@Entity
@Table(name = "professor")
@Access(AccessType.FIELD)
@AttributeOverride(name = "id", column = @Column(name = "professor_id"))
@NamedQuery(name = Professor.ALL_PROFESSORS_QUERY, query = "SELECT p FROM Professor p LEFT JOIN FETCH p.courseRegistrations")
//TODO P02 - Do we need a mapped super class?  If so, which one?
public class Professor extends PojoBase {
	
	public static final String ALL_PROFESSORS_QUERY = "Professor.findAll";

	// TODO P03 - Add annotations.
	@Basic(optional = false)
	@Column(name = "first_name", nullable = false, length = 50)
	protected String firstName;

	// TODO P04 - Add annotations.
	@Basic(optional = false)
	@Column(name = "last_name", nullable = false, length = 50)
	protected String lastName;

	// TODO P05 - Add annotations.
	@Basic(optional = true)
	@Column(name = "degree", nullable = true, length = 45)
	protected String degree;

	// TODO P06 - Add annotations for 1:M relation.  What should be the cascade and fetch types?
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "professor")
	// TODO P07 - Add other missing annotations.
	@JsonIgnore
	protected Set<CourseRegistration> courseRegistrations = new HashSet<>();
	
	// TODO P08 - Add annotations.
	@Transient
	protected boolean editable = false;

	public Professor() {
		super();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	// TODO P09 - Is an annotation needed here?
	@JsonIgnore
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
		builder.append("Professor[id = ").append(id).append(", firstName = ").append(firstName).append(", lastName = ")
				.append(lastName).append(", degree = ").append(degree)
				.append(", created = ").append(created).append(", updated = ").append(updated).append(", version = ").append(version).append("]");
		return builder.toString();
	}
	
}
