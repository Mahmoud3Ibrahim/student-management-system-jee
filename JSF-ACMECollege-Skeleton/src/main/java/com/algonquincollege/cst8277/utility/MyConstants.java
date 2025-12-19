/********************************************************************************************************
 * File:  MyConstants.java
 * Course Materials CST 8277
 * 
 * This is an academic graduation project for CST8277 course.
 *
 * @author Mike Norman
 * @author Teddy Yap
 * @author Mahmoud Ibrahim
 * 
 */
package com.algonquincollege.cst8277.utility;

import static jakarta.faces.application.ProjectStage.Development;
import static jakarta.faces.application.ViewHandler.DEFAULT_FACELETS_SUFFIX;
/**
 * <p>
 * This class holds various constants used by this App's artifacts (Resources, Servlets, etc.)
 * <p>
 * The key idea here is that often an annotation contains String-based parameters that <b><u>must be an exact match</u></b> <br/>
 * to a string used elsewhere. Use of this type of 'Contants' Interface class prevents errors such as:
<blockquote><pre>
{@literal @}GET
{@literal @}Path("{<b><u>emailID</u></b>}/project")  //accidently capitalized <b><u>ID</u></b>, instead of camel-case <b><u>Id</u></b>
public List<Project> getProjects({@literal @}PathParam("<b><u>emailId</u></b>") String emailId) ...  // path parameter does not match Annotation
</pre></blockquote>
 *
 * @author mwnorman
 */
public interface MyConstants {
    
    // constants on interfaces are 'public static final' by default, but I leave it this way in case I move them to a class
    public static final String ADMIN_ROLE = "ADMIN_ROLE";
    public static final String USER_ROLE = "USER_ROLE";
    public static final String SLASH = "/";
    public static final String ALL_FACELETS_SUFFIX = "*" + DEFAULT_FACELETS_SUFFIX;
    public static final String DEVELOPMENT_STAGE = Development.name();
    public static final String LOGIN_XHTML = "login" + DEFAULT_FACELETS_SUFFIX;
    public static final String LOGIN_PAGE_REDIRECT = SLASH + LOGIN_XHTML;
    public static final String MAIN_XHTML = "main" + DEFAULT_FACELETS_SUFFIX;
    public static final String MAIN_PAGE_REDIRECT = SLASH + MAIN_XHTML;
    public static final String LOGIN_FAILED_MSG = "Login failed";
    public static final String BUNDLE_BASENAME = "Bundle";
    public static final String STUDENT_MISSING_REFRESH_BUNDLE_MSG = "refresh";
    public static final String STUDENT_OUTOFDATE_REFRESH_BUNDLE_MSG = "outOfDate";

    // REST API constants
    public static final String HTTP_SCHEMA = "http";
    public static final String HOST = "localhost";
    public static final int PORT = 8080;
    public static final String APPLICATION_API_VERSION = "/api/v1";
    public static final String REST_APPLICATION_PATH = SLASH + "api" + SLASH + "v1";

    // Resource constants
    public static final String APPLICATION_CONTEXT_ROOT = SLASH + "REST-ACMECollege-Skeleton";
    public static final String CREDENTIAL_RESOURCE_NAME = "credential";
    public static final String STUDENT_RESOURCE_NAME =  "student";
    public static final String PROGRAM_RESOURCE_PATH = SLASH + "program";
    
    //TODO Add your own resource constants here
    public static final String COURSE_RESOURCE_NAME = "course";
    public static final String PROFESSOR_RESOURCE_NAME = "professor";
    public static final String STUDENT_CLUB_RESOURCE_NAME = "studentclub";
    public static final String COURSE_REGISTRATION_RESOURCE_NAME = "courseregistration";
    public static final String LETTER_GRADE_RESOURCE_PATH = SLASH + "lettergrade";
    public static final String STUDENT_ID_ELEMENT = "studentId";
    public static final String COURSE_ID_ELEMENT = "courseId";
    public static final String STUDENT_COURSE_REGISTRATION_RESOURCE_PATH = SLASH + "student" + SLASH + "{" + STUDENT_ID_ELEMENT + "}" + SLASH + "course" + SLASH + "{" + COURSE_ID_ELEMENT + "}";
    public static final String CLUB_ID_ELEMENT = "clubId";
    public static final String CLUB_MEMBERSHIP_RESOURCE_NAME = "clubmembership";
    public static final String STUDENT_CLUB_MEMBERSHIP_PATH = SLASH + "student" + SLASH + "{" + STUDENT_ID_ELEMENT + "}" + SLASH + "club" + SLASH + "{" + CLUB_ID_ELEMENT + "}";

    
    
    public static final String ACCESS_REQUIRES_AUTHENTICATION =
            "Access requires authentication";
    public static final String ACCESS_TO_THE_SPECIFIED_RESOURCE_HAS_BEEN_FORBIDDEN =
            "Access to the specified resource has been forbidden";
    // Eclipse MicroProfile Config - externalise configuration:  default in META-INF/microprofile-config.properties
    public static final String DEFAULT_ADMIN_USER_PROPNAME = "default-admin-user";
    public static final String DEFAULT_ADMIN_USER = "admin";
    public static final String DEFAULT_ADMIN_USER_PASSWORD_PROPNAME = "default-admin-user-password";
    public static final String DEFAULT_ADMIN_USER_PASSWORD = "admin";
    public static final String DEFAULT_USER = "cst8277";
    public static final String DEFAULT_USER_PASSWORD = "8277";
    public static final String DEFAULT_USER_PREFIX = "user";

    // The nickname of this hash algorithm is 'PBandJ' (Peanut-Butter-And-Jam, like the sandwich!)
    // I would like to use the constants from org.glassfish.soteria.identitystores.hash.Pbkdf2PasswordHashImpl
    // but they are not visible, so type in them all over again :-( Hope there are no typos!
    public static final String PROPERTY_ALGORITHM  = "Pbkdf2PasswordHash.Algorithm";
    public static final String DEFAULT_PROPERTY_ALGORITHM  = "PBKDF2WithHmacSHA256";
    public static final String PROPERTY_ITERATIONS = "Pbkdf2PasswordHash.Iterations";
    public static final String DEFAULT_PROPERTY_ITERATIONS = "2048";
    public static final String PROPERTY_SALT_SIZE = "Pbkdf2PasswordHash.SaltSizeBytes";
    public static final String DEFAULT_SALT_SIZE = "32";
    public static final String PROPERTY_KEY_SIZE = "Pbkdf2PasswordHash.KeySizeBytes";
    public static final String DEFAULT_KEY_SIZE = "32";

    // JPA constants
    public static final String PU_NAME = "acmecollege-PU";
    public static final String PARAM1 = "param1";

}