package com.raven.training.config.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.raven.training.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that validates JWT (JSON Web Token) tokens present in the Authorization header.
 * This filter extends {@link OncePerRequestFilter} to ensure it is executed only once
 * per request. It is responsible for extracting, validating, and setting the
 * authentication context for requests that contain a valid JWT.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
@AllArgsConstructor
public class JwtTokenValidator extends OncePerRequestFilter {

    private JwtUtils jwtUtils;

    /**
     * Performs the token validation and sets the security context.
     *
     * This method:
     * <ul>
     * <li>Extracts the JWT from the "Authorization" header.</li>
     * <li>Validates the token using {@link JwtUtils}.</li>
     * <li>Extracts the username from the decoded token.</li>
     * <li>Creates an {@link Authentication} object and sets it in the
     * {@link SecurityContextHolder}, allowing the request to proceed
     * as authenticated within the Spring Security framework.</li>
     * </ul>
     * If no valid JWT is found, the filter chain continues without setting the
     * security context, and subsequent filters or security configurations
     * will handle the unauthenticated request.
     *
     * @param request The servlet request.
     * @param response The servlet response.
     * @param filterChain The filter chain.
     * @throws ServletException If a servlet-related error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {

        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7);

            DecodedJWT decodedJWT = jwtUtils.validationToken(jwtToken);
            String username = jwtUtils.extractUsername(decodedJWT);

            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, null);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        }

        filterChain.doFilter(request, response);
    }

}
