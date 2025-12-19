/********************************************************************************************************
 * File:  SecurityUser.java Course Materials CST 8277
 *
 * This is an academic graduation project for CST8277 course.
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author Mahmoud Ibrahim
 * 
 */
package com.algonquincollege.cst8277.entity;

import java.io.Serializable;
import java.security.Principal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import com.algonquincollege.cst8277.rest.serializer.SecurityRoleSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

//TODO SU01 - Make this into JPA entity and add all the necessary annotations inside the class.
@Entity
@Table(name = "security_user")
@Access(AccessType.FIELD)
@NamedQuery(name = SecurityUser.SECURITY_USER_BY_NAME, query = "SELECT u FROM SecurityUser u WHERE u.username = :param1")
@NamedQuery(name = SecurityUser.SECURITY_USER_BY_STUDENT_ID, query = "SELECT u FROM SecurityUser u WHERE u.student.id = :param1")
public class SecurityUser implements Serializable, Principal {
  /** Explicit set serialVersionUID */
  private static final long serialVersionUID = 1L;
  
  public static final String SECURITY_USER_BY_NAME = "SecurityUser.userByName";
  public static final String SECURITY_USER_BY_STUDENT_ID = "SecurityUser.userByStudentId";

  //TODO SU02 - Add annotations.
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id", nullable = false)
  protected int id;
  
  //TODO SU03 - Add annotations.
  @Basic(optional = false)
  @Column(name = "username", nullable = false, length = 100, unique = true)
  protected String username;
  
  //TODO SU04 - Add annotations.
  @Basic(optional = false)
  @Column(name = "password_hash", nullable = false, length = 256)
  protected String pwHash;
  
  //TODO SU05 - Add annotations.
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "student_id", referencedColumnName = "id")
  protected Student student;
  
  //TODO SU06 - Add annotations.
  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
  @JoinTable(
      name = "user_has_role",
      joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id")
  )
  protected Set<SecurityRole> roles = new HashSet<SecurityRole>();

  public SecurityUser() {
      super();
  }

  public int getId() {
      return id;
  }
  
  public void setId(int id) {
      this.id = id;
  }

  public String getUsername() {
      return username;
  }
  
  public void setUsername(String username) {
      this.username = username;
  }

  public String getPwHash() {
      return pwHash;
  }
  
  public void setPwHash(String pwHash) {
      this.pwHash = pwHash;
  }

  // TODO SU07 - Setup to use custom JSON serializer called SecurityRoleSerializer
  @JsonSerialize(using = SecurityRoleSerializer.class)
  public Set<SecurityRole> getRoles() {
      return roles;
  }
  
  public void setRoles(Set<SecurityRole> roles) {
      this.roles = roles;
  }

  public Student getStudent() {
      return student;
  }
  
  public void setStudent(Student student) {
      this.student = student;
  }

  // Principal
  @Override
  public String getName() {
      return getUsername();
  }

  @Override
  public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      // Only include member variables that really contribute to an object's identity
      // i.e. if variables like version/updated/name/etc. change throughout an object's lifecycle,
      // they shouldn't be part of the hashCode calculation
      return prime * result + Objects.hash(getId());
  }

  @Override
  public boolean equals(Object obj) {
      if (this == obj) {
          return true;
      }
      if (obj == null) {
          return false;
      }
      if (obj instanceof SecurityUser otherSecurityUser) {
          // See comment (above) in hashCode():  Compare using only member variables that are
          // truly part of an object's identity
          return Objects.equals(this.getId(), otherSecurityUser.getId());
      }
      return false;
  }

  @Override
  public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("SecurityUser [id = ").append(id).append(", username = ").append(username).append("]");
      return builder.toString();
  }
  
}
