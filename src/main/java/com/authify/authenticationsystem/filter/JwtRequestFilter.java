package com.authify.authenticationsystem.filter;

import com.authify.authenticationsystem.service.AppUserDetailService;
import com.authify.authenticationsystem.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    // Service used to load user details from database
    private final AppUserDetailService appUserDetailService;

    // Utility class for JWT operations
    private final JwtUtil jwtUtil;


    private static final List<String> PUBLIC_URLS = List.of(
            "/api/v1.0/login",
            "/api/v1.0/register",
            "/api/v1.0/send-reset-otp",
            "/api/v1.0/reset-password",
            "/api/v1.0/logout"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        System.out.println("PATH = " + request.getRequestURI());
        return isPublicRequest(request);
    }

    /**
     * This method executes once for every incoming request.
     * It checks:
     * 1. Whether JWT token is present
     * 2. Whether token is valid
     * 3. If valid -> authenticate user
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        System.out.println("inside doFilterInternal");
        String jwt = null;
        String email = null;

        try {

            /**
             * =====================================================
             * 1. Check JWT Token in Authorization Header
             * =====================================================
             *
             * Example:
             * Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
             */
            final String authorizationHeader =
                    request.getHeader("Authorization");

            if (authorizationHeader != null &&
                    authorizationHeader.startsWith("Bearer ")) {

                // Extract JWT Token
                jwt = authorizationHeader.substring(7);

                // Extract Email from JWT
                email = jwtUtil.extractEmail(jwt);
            }

            /**
             * =====================================================
             * 2. If JWT not found in Header, check Cookies
             * =====================================================
             */
            if (jwt == null) {

                Cookie[] cookies = request.getCookies();

                if (cookies != null) {

                    for (Cookie cookie : cookies) {

                        // Find JWT Cookie
                        if ("jwt".equals(cookie.getName())) {

                            // Extract JWT from Cookie
                            jwt = cookie.getValue();

                            // Extract Email from JWT
                            email = jwtUtil.extractEmail(jwt);

                            break;
                        }
                    }
                }
            }

            /**
             * =====================================================
             * 3. Authenticate User
             * =====================================================
             */
            if (email != null &&
                    SecurityContextHolder.getContext()
                            .getAuthentication() == null) {

                // Load User from Database
                UserDetails userDetails =
                        appUserDetailService
                                .loadUserByUsername(email);

                /**
                 * Validate JWT Token
                 */
                if (jwtUtil.validateToken(jwt, userDetails)) {

                    /**
                     * Create Authentication Object
                     */
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    /**
                     * Add Request Details
                     */
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    /**
                     * Set Authentication in Security Context
                     */
                    SecurityContextHolder.getContext()
                            .setAuthentication(authToken);
                }
            }

        } catch (Exception ex) {

            /**
             * =====================================================
             * Invalid JWT Token
             * =====================================================
             */
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            response.setContentType("application/json");

            response.getWriter().write(
                    """
                    {
                        "error": "Unauthorized",
                        "message": "Invalid or expired JWT token"
                    }
                    """
            );

            return;
        }

        /**
         * =====================================================
         * Continue Filter Chain
         * =====================================================
         */
        filterChain.doFilter(request, response);
    }

    private boolean isPublicRequest(HttpServletRequest request) {

        String path = request.getRequestURI();

        return PUBLIC_URLS.stream()
                .anyMatch(publicUrl ->
                        path.equals(publicUrl)
                                || path.startsWith(publicUrl + "/"));
    }

}
