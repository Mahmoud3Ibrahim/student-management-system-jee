/********************************************************************************************************
 * File:  SecurityUser.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * 
 */
package com.algonquincollege.cst8277.entity;

import java.io.Serializable;
import java.security.Principal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.algonquincollege.cst8277.entity.SecurityUser;

//TODO SU01 - Make this into JPA entity and add all the necessary annotations inside the class.
public class SecurityUser implements Serializable, Principal {
  /** Explicit set serialVersionUID */
  private static final long serialVersionUID = 1L;
  
  public static final String SECURITY_USER_BY_NAME = "SecurityUser.userByName";
  public static final String SECURITY_USER_BY_STUDENT_ID = "SecurityUser.userByStudentId";

  //TODO SU02 - Add annotations.
  protected int id;
  
  //TODO SU03 - Add annotations.
  protected String username;
  
  //TODO SU04 - Add annotations.
  protected String pwHash;
  
  //TODO SU05 - Add annotations.
  protected Student student;
  
  //TODO SU06 - Add annotations.
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
