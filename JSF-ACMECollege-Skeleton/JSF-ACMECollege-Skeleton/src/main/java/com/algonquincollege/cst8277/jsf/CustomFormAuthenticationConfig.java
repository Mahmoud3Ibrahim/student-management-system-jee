/********************************************************************************************************
 * File:  CustomFormAuthenticationConfig.java
 * Course Materials CST 8277
 * 
 * @author Mike Norman
 * @author Teddy Yap
 * 
 */
package com.algonquincollege.cst8277.jsf;

import static com.algonquincollege.cst8277.utility.MyConstants.LOGIN_PAGE_REDIRECT;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.authentication.mechanism.http.CustomFormAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.LoginToContinue;

@CustomFormAuthenticationMechanismDefinition(
    loginToContinue = @LoginToContinue(
        loginPage = LOGIN_PAGE_REDIRECT,
        useForwardToLogin = false
    )
)
@ApplicationScoped
public class CustomFormAuthenticationConfig {
}