package com.authify.authenticationsystem.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * This class handles unauthorized access attempts.
 * Whenever a user tries to access a protected API  without valid authentication, Spring Security calls this class automatically.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * This method executes when authentication fails.
     *
     * Example Cases:
     * 1. Invalid JWT Token
     * 2. Expired JWT Token
     * 3. Missing JWT Token
     * 4. Unauthorized API access
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        /**
         * Set HTTP Status Code
         * 401 = Unauthorized
         */
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        /**
         * Set response type as JSON
         */
        response.setContentType("application/json");

        /**
         * Send custom error response
         */
        response.getWriter().write(
                """
                {
                    "error": "Unauthorized",
                    "message": "Invalid or missing JWT token"
                }
                """
        );
    }
}