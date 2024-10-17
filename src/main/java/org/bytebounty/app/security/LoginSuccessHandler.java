package org.bytebounty.app.security;

import java.io.IOException;

import org.bytebounty.app.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("myAuthenticationSuccessHandler")
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    AccountService accountService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

                String userName = authentication.getName();
                accountService.updateLastLoginByEmail(userName);
                response.sendRedirect("/");
                
    }
    
}
