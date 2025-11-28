/********************************************************************************************************
 * File:  ACMECollegeService.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * 
 */
package com.algonquincollege.cst8277.ejb;

import static com.algonquincollege.cst8277.entity.Course.ALL_COURSES_QUERY;
import static com.algonquincollege.cst8277.entity.CourseRegistration.ALL_COURSE_REGISTRATIONS_QUERY_NAME;
import static com.algonquincollege.cst8277.entity.CourseRegistration.QUERY_SPECIFIC_COURSE_REGISTRATION;
import static com.algonquincollege.cst8277.entity.Professor.ALL_PROFESSORS_QUERY;
import static com.algonquincollege.cst8277.entity.SecurityRole.SECURITY_ROLE_BY_NAME;
import static com.algonquincollege.cst8277.entity.Student.ALL_STUDENTS_QUERY_NAME;
import static com.algonquincollege.cst8277.entity.StudentClub.ALL_STUDENT_CLUBS_QUERY;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_KEY_SIZE;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_PROPERTY_ALGORITHM;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_PROPERTY_ITERATIONS;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_SALT_SIZE;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_USER_PREFIX;
import static com.algonquincollege.cst8277.utility.MyConstants.PARAM1;
import static com.algonquincollege.cst8277.utility.MyConstants.PARAM2;
import static com.algonquincollege.cst8277.utility.MyConstants.PROPERTY_ALGORITHM;
import static com.algonquincollege.cst8277.utility.MyConstants.PROPERTY_ITERATIONS;
import static com.algonquincollege.cst8277.utility.MyConstants.PROPERTY_KEY_SIZE;
import static com.algonquincollege.cst8277.utility.MyConstants.PROPERTY_SALT_SIZE;
import static com.algonquincollege.cst8277.utility.MyConstants.PU_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.USER_ROLE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algonquincollege.cst8277.entity.Course;
import com.algonquincollege.cst8277.entity.CourseRegistration;
import com.algonquincollege.cst8277.entity.Professor;
import com.algonquincollege.cst8277.entity.SecurityRole;
import com.algonquincollege.cst8277.entity.SecurityUser;
import com.algonquincollege.cst8277.entity.Student;
import com.algonquincollege.cst8277.entity.StudentClub;

@SuppressWarnings("unused")

/**
 * Stateless Singleton EJB Bean - ACMECollegeService
 */
@Singleton
public class ACMECollegeService implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LogManager.getLogger();
    
    private static final String READ_ALL_PROGRAMS = "SELECT name FROM program";
    private static final String READ_ALL_LETTER_GRADES = "SELECT grade FROM letter_grade";
    //TODO ACMECS01 - Add your query constants here.
    
    @PersistenceContext(name = PU_NAME)
    protected EntityManager em;
    
    @Inject
    protected Pbkdf2PasswordHash pbAndjPasswordHash;

    public List<Student> getAllStudents() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Student> cq = cb.createQuery(Student.class);
        cq.select(cq.from(Student.class));
        return em.createQuery(cq).getResultList();
    }

    public Student getStudentById(int id) {
        return em.find(Student.class, id);
    }

    @Transactional
    public Student persistStudent(Student newStudent) {
        em.persist(newStudent);
        return newStudent;
    }

    @Transactional
    public void buildUserForNewStudent(Student newStudent) {
        String username = DEFAULT_USER_PREFIX + "_" + newStudent.getFirstName() + "." + newStudent.getLastName();
        
        // Check if username already exists
        TypedQuery<SecurityUser> usernameQuery = em.createNamedQuery(SecurityUser.SECURITY_USER_BY_NAME, SecurityUser.class);
        usernameQuery.setParameter(PARAM1, username);
        List<SecurityUser> existingUsers = usernameQuery.getResultList();
        if (!existingUsers.isEmpty()) {
            throw new RuntimeException("Student with name '" + newStudent.getFirstName() + " " + newStudent.getLastName() + "' already exists");
        }
        
        SecurityUser userForNewStudent = new SecurityUser();
        userForNewStudent.setUsername(username);
        Map<String, String> pbAndjProperties = new HashMap<>();
        pbAndjProperties.put(PROPERTY_ALGORITHM, DEFAULT_PROPERTY_ALGORITHM);
        pbAndjProperties.put(PROPERTY_ITERATIONS, DEFAULT_PROPERTY_ITERATIONS);
        pbAndjProperties.put(PROPERTY_SALT_SIZE, DEFAULT_SALT_SIZE);
        pbAndjProperties.put(PROPERTY_KEY_SIZE, DEFAULT_KEY_SIZE);
        pbAndjPasswordHash.initialize(pbAndjProperties);
        String pwHash = pbAndjPasswordHash.generate(DEFAULT_USER_PASSWORD.toCharArray());
        userForNewStudent.setPwHash(pwHash);
        userForNewStudent.setStudent(newStudent);
        /* TODO ACMEMS01 - Use NamedQuery on SecurityRole to find USER_ROLE */
        TypedQuery<SecurityRole> query = em.createNamedQuery(SECURITY_ROLE_BY_NAME, SecurityRole.class);
        query.setParameter(PARAM1, USER_ROLE);
        SecurityRole userRole = query.getSingleResult();
        userForNewStudent.getRoles().add(userRole);
        userRole.getUsers().add(userForNewStudent);
        em.persist(userForNewStudent);
    }

    /**
     * To update a student
     * 
     * @param id - id of entity to update
     * @param studentWithUpdates - entity with updated information
     * @return Entity with updated information
     */
    @Transactional
    public Student updateStudentById(int id, Student studentWithUpdates) {
    	Student studentToBeUpdated = getStudentById(id);
        if (studentToBeUpdated != null) {
            em.refresh(studentToBeUpdated);
            // Copy fields from studentWithUpdates to studentToBeUpdated
            studentToBeUpdated.setFirstName(studentWithUpdates.getFirstName());
            studentToBeUpdated.setLastName(studentWithUpdates.getLastName());
            studentToBeUpdated.setEmail(studentWithUpdates.getEmail());
            studentToBeUpdated.setPhone(studentWithUpdates.getPhone());
            studentToBeUpdated.setProgram(studentWithUpdates.getProgram());
            em.merge(studentToBeUpdated);
            em.flush();
        }
        return studentToBeUpdated;
    }

    /**
     * To delete a student by id
     * 
     * @param id - student id to delete
     */
    @Transactional
    public Student deleteStudentById(int id) {
        Student student = getStudentById(id);
        if (student != null) {
            em.refresh(student);
            /* TODO ACMEMS02 - Use NamedQuery on SecurityRole to find this related Physician
               so that when we remove it, the relationship from SECURITY_USER table
               is not dangling*/
            TypedQuery<SecurityUser> findUser = em.<SecurityUser>createNamedQuery("SecurityUser.userByStudentId", SecurityUser.class).setParameter(PARAM1, id);
            try {
                SecurityUser sUser = findUser.getSingleResult();
                em.remove(sUser);
            } catch (jakarta.persistence.NoResultException e) {
                // No SecurityUser linked to this student, continue with deletion
            }
            em.remove(student);
        }
        return student;
    }
    
	@SuppressWarnings("unchecked")
    public List<String> getAllPrograms() {
		List<String> programs = new ArrayList<>();
		try {
			programs = (List<String>) em.createNativeQuery(READ_ALL_PROGRAMS).getResultList();
		}
		catch (Exception e) {
		}
		return programs;
    }

	//TODO ACMECS02 - Add the rest of your CRUD methods here.
	
	public List<Course> getAllCourses() {
		TypedQuery<Course> query = em.createNamedQuery(ALL_COURSES_QUERY, Course.class);
		return query.getResultList();
	}

	public Course getCourseById(int id) {
		return em.find(Course.class, id);
	}

	@Transactional
	public Course persistCourse(Course newCourse) {
		em.persist(newCourse);
		return newCourse;
	}

	@Transactional
	public Course updateCourseById(int id, Course courseWithUpdates) {
		Course courseToBeUpdated = getCourseById(id);
		if (courseToBeUpdated != null) {
			em.refresh(courseToBeUpdated);
			// Copy fields from courseWithUpdates to courseToBeUpdated
			courseToBeUpdated.setCourseCode(courseWithUpdates.getCourseCode());
			courseToBeUpdated.setCourseTitle(courseWithUpdates.getCourseTitle());
			courseToBeUpdated.setCreditUnits(courseWithUpdates.getCreditUnits());
			courseToBeUpdated.setOnline(courseWithUpdates.getOnline());
			em.merge(courseToBeUpdated);
			em.flush();
		}
		return courseToBeUpdated;
	}

	@Transactional
	public Course deleteCourseById(int id) {
		Course course = getCourseById(id);
		if (course != null) {
			em.refresh(course);
			em.remove(course);
		}
		return course;
	}

	public List<Professor> getAllProfessors() {
		TypedQuery<Professor> query = em.createNamedQuery(ALL_PROFESSORS_QUERY, Professor.class);
		return query.getResultList();
	}

	public Professor getProfessorById(int id) {
		return em.find(Professor.class, id);
	}

	@Transactional
	public Professor persistProfessor(Professor newProfessor) {
		em.persist(newProfessor);
		return newProfessor;
	}

	@Transactional
	public Professor updateProfessorById(int id, Professor professorWithUpdates) {
		Professor professorToBeUpdated = getProfessorById(id);
		if (professorToBeUpdated != null) {
			em.refresh(professorToBeUpdated);
			// Copy fields from professorWithUpdates to professorToBeUpdated
			professorToBeUpdated.setFirstName(professorWithUpdates.getFirstName());
			professorToBeUpdated.setLastName(professorWithUpdates.getLastName());
			professorToBeUpdated.setDegree(professorWithUpdates.getDegree());
			em.merge(professorToBeUpdated);
			em.flush();
		}
		return professorToBeUpdated;
	}

	@Transactional
	public Professor deleteProfessorById(int id) {
		Professor professor = getProfessorById(id);
		if (professor != null) {
			em.refresh(professor);
			em.remove(professor);
		}
		return professor;
	}

	public List<StudentClub> getAllStudentClubs() {
		TypedQuery<StudentClub> query = em.createNamedQuery(ALL_STUDENT_CLUBS_QUERY, StudentClub.class);
		return query.getResultList();
	}

	public StudentClub getStudentClubById(int id) {
		return em.find(StudentClub.class, id);
	}

	@Transactional
	public StudentClub persistStudentClub(StudentClub newStudentClub) {
		// Check if club name already exists
		TypedQuery<StudentClub> nameQuery = em.createNamedQuery(StudentClub.STUDENT_CLUB_BY_NAME, StudentClub.class);
		nameQuery.setParameter(PARAM1, newStudentClub.getName());
		List<StudentClub> existingClubs = nameQuery.getResultList();
		if (!existingClubs.isEmpty()) {
			throw new RuntimeException("Student club with name '" + newStudentClub.getName() + "' already exists");
		}
		
		em.persist(newStudentClub);
		return newStudentClub;
	}

	@Transactional
	public StudentClub updateStudentClubById(int id, StudentClub studentClubWithUpdates) {
		StudentClub studentClubToBeUpdated = getStudentClubById(id);
		if (studentClubToBeUpdated != null) {
			em.refresh(studentClubToBeUpdated);
			em.merge(studentClubWithUpdates);
			em.flush();
		}
		return studentClubWithUpdates;
	}

	@Transactional
	public StudentClub deleteStudentClubById(int id) {
		StudentClub studentClub = getStudentClubById(id);
		if (studentClub != null) {
			em.refresh(studentClub);
			em.remove(studentClub);
		}
		return studentClub;
	}

	@Transactional
	public StudentClub addStudentToClub(int studentId, int clubId) {
		Student student = getStudentById(studentId);
		StudentClub club = getStudentClubById(clubId);
		if (student == null || club == null) {
			return null;
		}
		student.getStudentClubs().add(club);
		club.getStudentMembers().add(student);
		em.merge(student);
		em.merge(club);
		em.flush();
		return club;
	}

	@Transactional
	public boolean removeStudentFromClub(int studentId, int clubId) {
		Student student = getStudentById(studentId);
		StudentClub club = getStudentClubById(clubId);
		if (student == null || club == null) {
			return false;
		}
		boolean removed = student.getStudentClubs().remove(club);
		club.getStudentMembers().remove(student);
		if (removed) {
			em.merge(student);
			em.merge(club);
			em.flush();
		}
		return removed;
	}

	public List<CourseRegistration> getAllCourseRegistrations() {
		TypedQuery<CourseRegistration> query = em.createNamedQuery(ALL_COURSE_REGISTRATIONS_QUERY_NAME, CourseRegistration.class);
		return query.getResultList();
	}

	public CourseRegistration getCourseRegistrationById(int studentId, int courseId) {
		TypedQuery<CourseRegistration> query = em.createNamedQuery(QUERY_SPECIFIC_COURSE_REGISTRATION, CourseRegistration.class);
		query.setParameter(PARAM1, studentId);
		query.setParameter(PARAM2, courseId);
		try {
			return query.getSingleResult();
		} catch (jakarta.persistence.NoResultException e) {
			return null;
		}
	}

	@Transactional
	public CourseRegistration persistCourseRegistration(CourseRegistration newCourseRegistration) {
		int studentId = 0;
		int courseId = 0;
		
		// Get IDs from the entity objects (from JSON: student.id, course.id)
		if (newCourseRegistration.getStudent() != null) {
			studentId = newCourseRegistration.getStudent().getId();
		}
		if (newCourseRegistration.getCourse() != null) {
			courseId = newCourseRegistration.getCourse().getId();
		}
		
		if (studentId == 0 || courseId == 0) {
			return null;
		}
		
		// Fetch actual managed entities from DB
		Student student = em.find(Student.class, studentId);
		Course course = em.find(Course.class, courseId);
		
		if (student == null || course == null) {
			return null;
		}
		
		// Set the actual entities (this also updates the composite key IDs)
		newCourseRegistration.setStudent(student);
		newCourseRegistration.setCourse(course);
		
		em.persist(newCourseRegistration);
		em.flush();
		return newCourseRegistration;
	}

	@Transactional
	public CourseRegistration assignProfessorToCourseRegistration(int studentId, int courseId, Professor professor) {
		CourseRegistration courseRegistration = getCourseRegistrationById(studentId, courseId);
		if (courseRegistration != null) {
			courseRegistration.setProfessor(professor);
			em.merge(courseRegistration);
			em.flush();
		}
		return courseRegistration;
	}

	@Transactional
	public CourseRegistration assignGradeToCourseRegistration(int studentId, int courseId, String letterGrade) {
		CourseRegistration courseRegistration = getCourseRegistrationById(studentId, courseId);
		if (courseRegistration != null) {
			courseRegistration.setLetterGrade(letterGrade);
			em.merge(courseRegistration);
			em.flush();
		}
		return courseRegistration;
	}

	@Transactional
	public CourseRegistration deleteCourseRegistrationById(int studentId, int courseId) {
		CourseRegistration courseRegistration = getCourseRegistrationById(studentId, courseId);
		if (courseRegistration != null) {
			em.refresh(courseRegistration);
			em.remove(courseRegistration);
		}
		return courseRegistration;
	}

	@SuppressWarnings("unchecked")
	public List<String> getAllLetterGrades() {
		List<String> letterGrades = new ArrayList<>();
		try {
			letterGrades = (List<String>) em.createNativeQuery(READ_ALL_LETTER_GRADES).getResultList();
		} catch (Exception e) {
		}
		return letterGrades;
	}
	
}