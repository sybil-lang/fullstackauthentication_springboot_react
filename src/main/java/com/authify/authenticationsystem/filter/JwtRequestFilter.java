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


    private static final List<String> PUBLIC_URLS= List.of("/login","/register","/send-reset-otp","/reset-password","/logout");
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



        String path=request.getServletPath();
        if(PUBLIC_URLS.contains(path)){
            filterChain.doFilter(request,response);
            return;
        }

        String jwt = null;
        String email = null;


        //1. Get Authorization Header
        // Example:
        // Authorization: Bearer eyJhbGciOiJIUzI1NiJ9....
        final String authorizationHeader = request.getHeader("Authorization");



        /**
         * Check whether Authorization header exists
         * and starts with "Bearer "
         */
        if (authorizationHeader != null &&  authorizationHeader.startsWith("Bearer ")) {

            // Extract token after "Bearer "
            jwt = authorizationHeader.substring(7);

            // Extract email/username from token
            email = jwtUtil.extractEmail(jwt);
        }

        //2. if not found in header check in cookies
        if(jwt==null){
            Cookie[] cookies=request.getCookies();
            if(cookies!=null){
                for (Cookie cookie : cookies) {
                    if("jwt".equals(cookie.getName())){
                        jwt = cookie.getValue();
                        break;
                    }
                }
            }
        }

        /**
         * If email exists and user is not already authenticated
         */
        if (email != null &&  SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user from database
            UserDetails userDetails = appUserDetailService.loadUserByUsername(email);

            /**
             * Validate token
             * Checks:
             * 1. Username matches
             * 2. Token not expired
             */
            if (jwtUtil.validateToken(jwt, userDetails)) {

                /**
                 * Create Authentication Token
                 *
                 * Parameters:
                 * 1. UserDetails object
                 * 2. Credentials (null because password already validated)
                 * 3. Authorities/Roles
                 */
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                /**
                 * Add request details
                 * مثل IP Address / Session ID etc.
                 */
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                /**
                 * Set authentication in Security Context
                 *
                 * After this step Spring Security understands
                 * that the user is authenticated.
                 */
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        /**
         * Continue filter chain
         * Pass request to next filter
         */
        filterChain.doFilter(request, response);
    }
}