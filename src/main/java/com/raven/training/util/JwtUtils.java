package com.raven.training.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Utility class for JSON Web Token (JWT) management.
 * This component provides methods for creating, validating, and extracting
 * information from JWTs, essential for handling authentication and
 * authorization in the application.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
@Component
public class JwtUtils {

    @Value("${security.jwt.user.generator}")
    private String userGenerator;

    @Value("${security.jwt.key.private}")
    private String privateKey;

    /**
     * Creates a new JWT based on the provided authentication object.
     * The token includes the username, authorities, and various
     * claims such as expiration time and issuer.
     *
     * @param authentication The {@link Authentication} object containing user details.
     * @return The generated JWT as a signed string.
     */
    public String createToken(Authentication authentication){
        Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

        String username = authentication.getPrincipal().toString();
        String authorities = authentication.getAuthorities()
                .stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.joining(","));

        return JWT.create()
                .withIssuer(this.userGenerator)
                .withSubject(username)
                .withClaim("authorities", authorities)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1800000))
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date(System.currentTimeMillis()))
                .sign(algorithm);
    }

    /**
     * Validates a given JWT string.
     * It verifies the token's signature, expiration, and issuer.
     *
     * @param token The JWT string to be validated.
     * @return A {@link DecodedJWT} object if the token is valid.
     * @throws JWTVerificationException if the token is invalid for any reason
     * (e.g., signature mismatch, expired token, incorrect issuer).
     */
    public DecodedJWT validationToken(String token) throws JWTVerificationException {
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(this.userGenerator)
                    .build();

            return verifier.verify(token);
        } catch (JWTVerificationException exception) {
            throw exception;
        }
    }

    /**
     * Extracts the username from a decoded JWT.
     *
     * @param decodedJWT The decoded JWT object.
     * @return The username as a string.
     */
    public String extractUsername(DecodedJWT decodedJWT){
        return decodedJWT.getSubject().toString();
    }

    /**
     * Retrieves a specific claim from a decoded JWT.
     *
     * @param decodedJWT The decoded JWT object.
     * @param claimName The name of the claim to retrieve.
     * @return The {@link Claim} object corresponding to the claim name.
     */
    public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName){
        return decodedJWT.getClaim(claimName);
    }

    /**
     * Extracts all claims from a decoded JWT.
     *
     * @param decodedJWT The decoded JWT object.
     * @return A {@link Map} containing all claims, with the claim name as the key.
     */
    public Map<String, Claim> extractAllClaims(DecodedJWT decodedJWT){
        return decodedJWT.getClaims();
    }
}
