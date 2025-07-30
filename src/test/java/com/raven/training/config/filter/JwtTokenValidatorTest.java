package com.raven.training.config.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.raven.training.util.JwtUtils; // Asegúrate de importar tu JwtUtils
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for JwtTokenValidator")
class JwtTokenValidatorTest {

    @InjectMocks
    private JwtTokenValidator jwtTokenValidator;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private DecodedJWT decodedJWT;

    private final String VALID_TOKEN = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJ0ZXN0LWlzc3VlciIsInN1YiI6InRlc3R1c2VyIiwiYXV0aG9yaXRpZXMiOiJST0xFX1VTRVIscmVhZDphbGwiLCJpYXQiOjE2NzgwODk4NDYsImV4cCI6MTY3ODA5MTY0Niwiand0aWQiOiJiMDc5YzI2MS02OGVjLTQyY2YtYjIzZC01YTc1NjA0MjQzMmEiLCJuYmYiOjE2NzgwODk4NDZ9.someValidSignature";
    private final String TOKEN_WITHOUT_BEARER = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJ0ZXN0LWlzc3VlciIsInN1YiI6InRlc3R1c2VyIiwiYXV0aG9yaXRpZXMiOiJST0xFX1VTRVIscmVhZDphbGwiLCJpYXQiOjE2NzgwODk4NDYsImV4cCI6MTY3ODA5MTY0Niwiand0aWQiOiJiMDc5YzI2MS02OGVjLTQyY2YtYjIzZC01YTc1NjA0MjQzMmEiLCJuYmYiOjE2NzgwODk4NDZ9.someValidSignature";
    private final String INVALID_TOKEN_SIGNATURE = "Bearer invalid.token.signature";
    private final String USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should set authentication in SecurityContextHolder for a valid JWT token")
    void doFilterInternal_WithValidJwtToken_ShouldSetAuthentication() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(VALID_TOKEN);
        when(jwtUtils.validationToken(anyString())).thenReturn(decodedJWT);
        when(jwtUtils.extractUsername(decodedJWT)).thenReturn(USERNAME);

        jwtTokenValidator.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertTrue(authentication instanceof UsernamePasswordAuthenticationToken, "Authentication should be set");
        assertEquals(USERNAME, authentication.getPrincipal(), "Principal should be the extracted username");
        assertNull(authentication.getCredentials(), "Credentials should be null");
        assertTrue(authentication.getAuthorities().isEmpty(), "Authorities should be empty (as per your code)");

        verify(request, times(1)).getHeader(HttpHeaders.AUTHORIZATION);
        verify(jwtUtils, times(1)).validationToken(anyString());
        verify(jwtUtils, times(1)).extractUsername(decodedJWT);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not set authentication when no JWT token is present in header")
    void doFilterInternal_NoJwtToken_ShouldNotSetAuthentication() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        jwtTokenValidator.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication should not be set");

        verify(request, times(1)).getHeader(HttpHeaders.AUTHORIZATION);
        verify(jwtUtils, never()).validationToken(anyString());
        verify(jwtUtils, never()).extractUsername(any(DecodedJWT.class));
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not set authentication when JWT token does not start with 'Bearer '")
    void doFilterInternal_TokenWithoutBearerPrefix_ShouldNotSetAuthentication() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(TOKEN_WITHOUT_BEARER);

        jwtTokenValidator.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication should not be set");

        verify(request, times(1)).getHeader(HttpHeaders.AUTHORIZATION);
        verify(jwtUtils, never()).validationToken(anyString());
        verify(jwtUtils, never()).extractUsername(any(DecodedJWT.class));
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
