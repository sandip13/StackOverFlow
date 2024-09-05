package com.stackoverflow.beta.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Use a secret key from properties for better security
    @Value("${jwt.secret}")
    private String secretKey;

    private final long JWT_EXPIRATION = 1000 * 60 * 60; // 1 hour

    // Method to get the signing key
    private Key getSigningKey() {
        return new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }

    /**
     * Generate a JWT token for a given username.
     * @param username the username to be included in the token
     * @return the generated JWT token
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION)) // Set expiration
                .signWith(SignatureAlgorithm.HS256, getSigningKey()) // Use a key for signing
                .compact();
    }

    /**
     * Extract claims from a JWT token.
     * @param token the JWT token
     * @return the claims extracted from the token
     */
    public Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            System.out.println("JWT Token has expired.");
            throw e; // Handle expired token scenario
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT Token.");
            throw e; // Handle malformed token scenario
        } catch (SignatureException e) {
            System.out.println("JWT signature does not match.");
            throw e; // Handle invalid signature scenario
        }
    }

    /**
     * Extract the username from a JWT token.
     * @param token the JWT token
     * @return the username
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Check if a token has expired.
     * @param token the JWT token
     * @return true if the token has expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    /**
     * Validate a token against the UserDetails object.
     * @param token the JWT token
     * @param userDetails the UserDetails object to validate against
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}