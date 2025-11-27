/********************************************************************************************************
 * File:  LoginBean.java
 * Course Materials CST 8277
 * 
 * @author Mike Norman
 * @author Teddy Yap
 * 
 */
package com.algonquincollege.cst8277.jsf;

import java.io.IOException;
import java.io.Serializable;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.algonquincollege.cst8277.utility.MyConstants;

@Named("loginBean")
//@RequestScoped
@SessionScoped
public class LoginBean implements Serializable, MyConstants {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    @Inject
    private SecurityContext securityContext;
    @Inject
    private ExternalContext externalContext;
    @Inject
    private FacesContext facesContext;

    public LoginBean() {
    	super();
        System.out.println("LoginBean constructor done");
    }

    /*
    @Inject
    public LoginBean(SecurityContext securityContext, ExternalContext externalContext, FacesContext facesContext) {
        super();
        this.securityContext = securityContext;
        this.externalContext = externalContext;
        this.facesContext = facesContext;
        System.out.println("constructor done");
    }
    */

    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public void validateUsernamePassword() throws IOException {
        switch (continueAuthentication()) {
            case SEND_CONTINUE:
                facesContext.responseComplete();
                break;
            case SEND_FAILURE:
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, LOGIN_FAILED_MSG, null));
                break;
            case SUCCESS:
                externalContext.redirect(externalContext.getRequestContextPath() + MAIN_PAGE_REDIRECT);
                break;
            case NOT_DONE:
                break;
        }
    }
    
    public String logout() {
        externalContext.invalidateSession();
        setUsername(null);
        setPassword(null);
        return LOGIN_PAGE_REDIRECT + "?faces-redirect=true";
    }

    private AuthenticationStatus continueAuthentication() {
        return securityContext.authenticate(
            (HttpServletRequest) externalContext.getRequest(),
            (HttpServletResponse) externalContext.getResponse(),
            AuthenticationParameters.withParams().credential(
                new UsernamePasswordCredential(getUsername(), getPassword()))
        );
    }
    
}