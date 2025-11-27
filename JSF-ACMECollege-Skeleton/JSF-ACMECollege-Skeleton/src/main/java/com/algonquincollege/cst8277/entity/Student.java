/********************************************************************************************************
 * File:  Student.java Course Materials CST 8277
 *
 * @author Mike Norman
 * @author Teddy Yap
 * 
 */
package com.algonquincollege.cst8277.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.algonquincollege.cst8277.entity.Student;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.faces.view.ViewScoped;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;

/**
 * The persistent class for the student database table.
 */
@SuppressWarnings("unused")

//Hint - @Entity marks this class as an entity which needs to be mapped by JPA.
//Hint - @Entity does not need a name if the name of the class is sufficient.
//Hint - @Entity name does not matter as long as it is consistent across the code.
@Entity(name = "Student")
//Hint - @Table defines a specific table on DB which is mapped to this entity.
@Table(name = "student")
//Hint - @NamedQuery attached to this class which uses JPQL/HQL.  SQL cannot be used with NamedQuery.
//Hint - @NamedQuery uses the name which is defined in @Entity for JPQL, if no name is defined use class name.
//Hint - @NamedNativeQuery can optionally be used if there is a need for SQL query.
@NamedQuery(name = Student.ALL_STUDENTS_QUERY_NAME, query = "SELECT s FROM Student s LEFT JOIN FETCH s.courseRegistrations LEFT JOIN FETCH s.studentClubs")
@NamedQuery(name = Student.QUERY_STUDENT_BY_ID, query = "SELECT s FROM Student s LEFT JOIN FETCH s.courseRegistrations LEFT JOIN FETCH s.studentClubs WHERE s.id = :param1")
//Hint - No need for AttributeOverride as student id column is called id as well.
//Hint - @AttributeOverride can override column details from the mapped super class.
public class Student extends PojoBase implements Serializable {
	/** explicit set serialVersionUID */
	private static final long serialVersionUID = 1L;

	public static final String ALL_STUDENTS_QUERY_NAME = "Student.findAll";
    public static final String QUERY_STUDENT_BY_ID = "Student.findAllByID";

	// JPA requires the default constructor be present
    public Student() {
    	super();
    }
    
	// Hint - @Basic(optional = false) is used when the object cannot be null.
	// Hint - @Basic or none can be used if the object can be null.
	// Hint - @Basic is for checking the state of object at the scope of our code.
    @Basic(optional = false)
	// Hint - @Column is used to define the details of the column which this object will map to.
	// Hint - @Column is for mapping and creation (if needed) of an object to DB.
	// Hint - @Column can also be used to define a specific name for the column if it is different than our object name.
	@Column(name = "first_name", nullable = false, length = 50)
	protected String firstName;
    
    @Basic(optional = false)
	@Column(name = "last_name", nullable = false, length = 50)
	protected String lastName;

    @Basic(optional = true)
	@Column(name = "email", nullable = true, length = 100)
    protected String email;

    @Basic(optional = true)
	@Column(name = "phone", nullable = true, length = 10)
    protected String phone;

    @Basic(optional = true)
	@Column(name = "program", nullable = true, length = 45)
    protected String program;
    
	// Hint - @OneToMany is used to define 1:M relationship between this entity and another.
	// Hint - @OneToMany option cascade can be added to define if changes to this entity should cascade to objects.
	// Hint - @OneToMany option cascade will be ignored if not added, meaning no cascade effect.
	// Hint - @OneToMany option fetch should be lazy to prevent eagerly initializing all the data.
    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "student")
	// Hint - java.util.Set is used as a collection, however List could have been used as well.
	// Hint - java.util.Set will be unique and also possibly can provide better get performance with HashCode.
	protected Set<CourseRegistration> courseRegistrations = new HashSet<>();
    
	// Hint - @ManyToMany is used to define M:N relationship between this entity and another.
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    // Hint - @JoinTable is used to specify the mapping of associations.  It is applied to the owning side of an association.
    // Hint - @JoinTable is typically used in the mapping of many-to-many and unidirectional one-to-many associations.  It may also be used to map bidirectional many-to-one/one-to-many associations, unidirectional many-to-one relationships, and one-to-one associations (both bidirectional and unidirectional).
    // Hint - When a join table is used in mapping a relationship with an embeddable class on the owning side of the relationship, the containing entity rather than the embeddable class is considered the owner of the relationship.
    // Hint - If the JoinTable annotation is missing, the default values of the annotation elements apply.  The name of the join table is assumed to be the table names of the associated primary tables concatenated together (owning side first) using an underscore.
    @JoinTable(name="club_membership",
    joinColumns=@JoinColumn(name="student_id", referencedColumnName="id"),
    inverseJoinColumns=@JoinColumn(name="club_id", referencedColumnName="club_id"))
	// Hint - @JsonIgnore is used to mark a field or method within a Java class that should be ignored during JSON serialization and deserialization processes.
    @JsonIgnore
    protected Set<StudentClub> studentClubs = new HashSet<StudentClub>();
    
	// Hint - @Transient is used to annotate a property or field of an entity class, mapped superclass, or embeddable class which is not persistent.
    @Transient
	protected boolean editable = false;
    
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	// Hint - @JsonIgnore is used to mark a field or method within a Java class that should be ignored during JSON serialization and deserialization processes.
    // Simplify JSON body, skip prescriptions
    @JsonIgnore
    public Set<CourseRegistration> getCourseRegistrations() {
		return courseRegistrations;
	}

	public void setCourseRegistrations(Set<CourseRegistration> courseRegistrations) {
		this.courseRegistrations = courseRegistrations;
	}

    public Set<StudentClub> getStudentClubs() {
		return studentClubs;
	}

	public void setStudentClubs(Set<StudentClub> studentClubs) {
		this.studentClubs = studentClubs;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	//Inherited hashCode/equals is sufficient for this entity class

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Student[id = ").append(id).append(", firstName = ").append(firstName).append(", lastName = ")
				.append(lastName).append(", email = ").append(email).append(", phone = ").append(phone).append(", program = ").append(program)
				.append(", created = ").append(created).append(", updated = ").append(updated).append(", version = ").append(version).append("]");
		return builder.toString();
	}
	
}
