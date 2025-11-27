/********************************************************************************************************
 * File:  FacesConfigAndServletContextListener.java
 * Course Materials CST 8277
 * 
 * @author (original) Mike Norman
 * @author Teddy Yap
 *
 */
package com.algonquincollege.cst8277;

import static com.algonquincollege.cst8277.utility.MyConstants.ADMIN_ROLE;
import static com.algonquincollege.cst8277.utility.MyConstants.USER_ROLE;
import static jakarta.faces.application.ProjectStage.PROJECT_STAGE_PARAM_NAME;
import static jakarta.faces.application.ViewHandler.FACELETS_REFRESH_PERIOD_PARAM_NAME;
import static jakarta.faces.validator.BeanValidator.ENABLE_VALIDATE_WHOLE_BEAN_PARAM_NAME;

import jakarta.faces.annotation.FacesConfig;
import jakarta.faces.annotation.FacesConfig.Version;
import jakarta.faces.application.ProjectStage;
import jakarta.faces.webapp.FacesServlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletRegistration.Dynamic;
import jakarta.servlet.annotation.WebListener;

/**
 * Programatically replace web.xml setup with this {@link ServletContextListener} <br/>
 * NB:  Some web.xml elements cannot be programatically replaced, for example &lt;welcome-file-list&gt;
 *
 * <pre>
 * {@code
   <web-app
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns="http://xmlns.jcp.org/xml/ns/javaee"
    xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
    version="4.0">

    <context-param>
      <param-name>jakarta.faces.PROJECT_STAGE</param-name>
      <param-value>Development</param-value>
    </context-param>
    <context-param>
      <param-name>jakarta.faces.FACELETS_REFRESH_PERIOD</param-name>
      <param-value>0</param-value>
    </context-param>
    <context-param>
      <param-name>jakarta.faces.validator.ENABLE_VALIDATE_WHOLE_BEAN</param-name>
      <param-value>true</param-value>
    </context-param>

    <servlet>
      <servlet-name>Faces Servlet</servlet-name>
      <servlet-class>jakarta.faces.webapp.FacesServlet</servlet-class>
      <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet-mapping>
      <servlet-name>Faces Servlet</servlet-name>
      <url-pattern>*.xhtml</url-pattern>
    </servlet-mapping>
 </web-app>
   }
   </pre>
 *
 * @author mwnorman
 */
@FacesConfig(
    // JSF 2.3 is configured 'out-of-the-box' to be backwards-compatible with 2.2
    // so if you want 2.3 features, you must *explicitly* configure it with the
    // @FacesConfig annotation and Version.JSF_2_3 constant
    version = Version.JSF_2_3
)
@WebListener
public class FacesConfigAndServletContextListener implements ServletContextListener {
   @Override
   public void contextInitialized(ServletContextEvent sce) {
      ServletContextListener.super.contextInitialized(sce);
      ServletContext sc = sce.getServletContext();
      sc.declareRoles(USER_ROLE, ADMIN_ROLE);

      //Registering the JSF Servlet
      Dynamic facesServlet = sc.addServlet("Faces Servlet", FacesServlet.class.getName());
      //Specifying the Servlet Mapping
      facesServlet.addMapping("*.xhtml");
      //Setting Priority, 0 or higher for eager, if negative then it's lazy
      facesServlet.setLoadOnStartup(1);
      //Context parameters
      sc.setInitParameter(FACELETS_REFRESH_PERIOD_PARAM_NAME, "0");
      sc.setInitParameter(PROJECT_STAGE_PARAM_NAME, ProjectStage.Development.name());
      sc.setInitParameter(ENABLE_VALIDATE_WHOLE_BEAN_PARAM_NAME, Boolean.TRUE.toString());
   }
}