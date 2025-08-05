package com.raven.training.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    private static final String USERNAME = "testuser";
    private static final String PRIVATE_KEY = "testPrivateKey123456789012345678901234567890";
    private static final String ISSUER = "test-issuer";
    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtils, "privateKey", PRIVATE_KEY);
        ReflectionTestUtils.setField(jwtUtils, "userGenerator", ISSUER);
        
        when(authentication.getPrincipal()).thenReturn(USERNAME);
        Collection<GrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority(ROLE_USER),
            new SimpleGrantedAuthority(ROLE_ADMIN)
        );
        when(authentication.getAuthorities()).thenAnswer(i -> authorities);
    }

    @Test
    @DisplayName("You should create a valid JWT token")
    void shouldCreateValidJwtToken() {
        String token = jwtUtils.createToken(authentication);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(PRIVATE_KEY))
                .withIssuer(ISSUER)
                .build()
                .verify(token);
                
        assertEquals(USERNAME, decodedJWT.getSubject());
        assertEquals(ISSUER, decodedJWT.getIssuer());
        assertTrue(decodedJWT.getExpiresAt().after(new Date()));
        assertNotNull(decodedJWT.getClaim("authorities"));
    }

    @Test
    @DisplayName("Should validate a valid JWT token")
    void shouldValidateValidToken() {
        String token = jwtUtils.createToken(authentication);

        DecodedJWT decodedJWT = jwtUtils.validationToken(token);

        assertNotNull(decodedJWT);
        assertEquals(USERNAME, decodedJWT.getSubject());
    }

    @Test
    @DisplayName("Should extract the username from the token")
    void shouldExtractUsernameFromToken() {
        String token = jwtUtils.createToken(authentication);
        DecodedJWT decodedJWT = jwtUtils.validationToken(token);

        String username = jwtUtils.extractUsername(decodedJWT);

        assertEquals(USERNAME, username);
    }

    @Test
    @DisplayName("Should extract a specific claim from the token")
    void shouldExtractSpecificClaim() {
        String token = jwtUtils.createToken(authentication);
        DecodedJWT decodedJWT = jwtUtils.validationToken(token);
        String claimName = "authorities";

        Claim claim = jwtUtils.getSpecificClaim(decodedJWT, claimName);

        assertNotNull(claim);
        assertFalse(claim.isNull());
        assertTrue(claim.asString().contains(ROLE_USER));
        assertTrue(claim.asString().contains(ROLE_ADMIN));
    }

    @Test
    @DisplayName("Should extract all the claims from the token.")
    void shouldExtractAllClaims() {
        String token = jwtUtils.createToken(authentication);
        DecodedJWT decodedJWT = jwtUtils.validationToken(token);

        Map<String, Claim> claims = jwtUtils.extractAllClaims(decodedJWT);

        assertNotNull(claims);
        assertFalse(claims.isEmpty());
        assertTrue(claims.containsKey("sub"));
        assertTrue(claims.containsKey("iss"));
        assertTrue(claims.containsKey("authorities"));
        assertTrue(claims.containsKey("iat"));
        assertTrue(claims.containsKey("exp"));
        assertTrue(claims.containsKey("jti"));
        assertTrue(claims.containsKey("nbf"));
    }
}
