package az.zefflix.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JWT token-lərin yaradılması, doğrulanması və parse edilməsi üçün utility sinifi.
 */
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_ROLES = "roles";
    private static final String CLAIM_TOKEN_TYPE = "tokenType";
    private static final String TYPE_ACCESS = "ACCESS";
    private static final String TYPE_REFRESH = "REFRESH";

    // Package-private fields allow ReflectionTestUtils injection in unit tests.
    String secret;
    long accessTokenExpiration;
    long refreshTokenExpiration;

    /** No-arg constructor for test usage with ReflectionTestUtils. */
    public JwtUtil() {
    }

    /** Production constructor — initialised from JwtProperties. */
    public JwtUtil(JwtProperties properties) {
        if (properties.getSecret() == null || properties.getSecret().isBlank()) {
            throw new IllegalArgumentException(
                    "zefflix.security.jwt.secret konfigurasiya edilmeyib.");
        }
        this.secret = properties.getSecret();
        this.accessTokenExpiration = properties.getAccessTokenExpiryMs();
        this.refreshTokenExpiration = properties.getRefreshTokenExpiryMs();
    }

    public String generateAccessToken(UserPrincipal principal) {
        return buildToken(
                principal.getId().toString(),
                principal.getEmail(),
                principal.getUsername(),
                principal.getRoles(),
                TYPE_ACCESS,
                accessTokenExpiration
        );
    }

    public String generateRefreshToken(UUID userId) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(userId.toString())
                .claim(CLAIM_TOKEN_TYPE, TYPE_REFRESH)
                .claim(CLAIM_USER_ID, userId.toString())
                .issuedAt(new Date(now))
                .expiration(new Date(now + refreshTokenExpiration))
                .signWith(signingKey())
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            parseClaims(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        return TYPE_ACCESS.equals(parseClaims(token).get(CLAIM_TOKEN_TYPE, String.class));
    }

    public boolean isRefreshToken(String token) {
        return TYPE_REFRESH.equals(parseClaims(token).get(CLAIM_TOKEN_TYPE, String.class));
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(parseClaims(token).get(CLAIM_USER_ID, String.class));
    }

    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Object raw = parseClaims(token).get(CLAIM_ROLES);
        if (raw instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return List.of();
    }

    public UserPrincipal buildPrincipal(String token) {
        Claims claims = parseClaims(token);
        UUID id = UUID.fromString(claims.get(CLAIM_USER_ID, String.class));
        String email = claims.getSubject();
        String username = claims.get(CLAIM_USERNAME, String.class);
        return UserPrincipal.of(id, email, username, extractRoles(token));
    }

    public Date extractExpiration(String token) {
        return parseClaims(token).getExpiration();
    }

    private String buildToken(String userId, String email, String username,
                              List<String> roles, String tokenType, long expiryMs) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(email)
                .claim(CLAIM_USER_ID, userId)
                .claim(CLAIM_USERNAME, username)
                .claim(CLAIM_ROLES, roles)
                .claim(CLAIM_TOKEN_TYPE, tokenType)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiryMs))
                .signWith(signingKey())
                .compact();
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}