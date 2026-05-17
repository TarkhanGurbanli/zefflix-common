package az.zefflix.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * JWT access/refresh token yaratma, doğrulama və parse etmə.
 *
 * <p>Tələb olunan application properties:
 * <pre>
 * zefflix:
 *   jwt:
 *     secret: "min-256-bit-secret-key-here-change-in-production"
 *     access-token-expiration: 900000      # 15 dəq (ms)
 *     refresh-token-expiration: 604800000  # 7 gün (ms)
 * </pre>
 */
@Slf4j
@Component
public class JwtUtil {

    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_ROLES = "roles";
    private static final String CLAIM_TOKEN_TYPE = "type";
    private static final String ACCESS_TOKEN = "access";
    private static final String REFRESH_TOKEN = "refresh";

    @Value("${zefflix.jwt.secret}")
    private String secret;

    @Value("${zefflix.jwt.access-token-expiration:900000}")
    private long accessTokenExpiration;

    @Value("${zefflix.jwt.refresh-token-expiration:604800000}")
    private long refreshTokenExpiration;

    // ── Token generation ─────────────────────────────────────────────────────

    /**
     * İstifadəçi üçün access token yaradır.
     * Token içinə userId, email, username və rol siyahısı daxil edilir.
     */
    public String generateAccessToken(UserPrincipal principal) {
        List<String> roles = principal.getAuthorities().stream()
            .map(Object::toString)
            .toList();

        return Jwts.builder()
            .subject(principal.getId().toString())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
            .claims(Map.of(
                CLAIM_USER_ID, principal.getId().toString(),
                CLAIM_EMAIL, principal.getEmail(),
                CLAIM_USERNAME, principal.getUsername(),
                CLAIM_ROLES, roles,
                CLAIM_TOKEN_TYPE, ACCESS_TOKEN
            ))
            .signWith(getSigningKey())
            .compact();
    }

    /**
     * Refresh token yaradır. Yalnız userId saxlanır.
     */
    public String generateRefreshToken(UUID userId) {
        return Jwts.builder()
            .subject(userId.toString())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
            .claims(Map.of(
                CLAIM_USER_ID, userId.toString(),
                CLAIM_TOKEN_TYPE, REFRESH_TOKEN
            ))
            .signWith(getSigningKey())
            .compact();
    }

    // ── Token parsing ─────────────────────────────────────────────────────────

    /**
     * Token-dən bütün claim-ləri çıxarır.
     *
     * @throws JwtException token etibarsız və ya müddəti bitibsə
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(extractAllClaims(token).get(CLAIM_USER_ID, String.class));
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).get(CLAIM_EMAIL, String.class);
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).get(CLAIM_USERNAME, String.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractAllClaims(token).get(CLAIM_ROLES, List.class);
    }

    public boolean isAccessToken(String token) {
        return ACCESS_TOKEN.equals(extractAllClaims(token).get(CLAIM_TOKEN_TYPE, String.class));
    }

    public boolean isRefreshToken(String token) {
        return REFRESH_TOKEN.equals(extractAllClaims(token).get(CLAIM_TOKEN_TYPE, String.class));
    }

    // ── Validation ────────────────────────────────────────────────────────────

    /**
     * Token-in etibarlı (imzalanmış, vaxtı keçməmiş) olduğunu yoxlayır.
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (ExpiredJwtException e) {
            log.debug("Token müddəti bitib: {}", e.getMessage());
            return false;
        } catch (JwtException e) {
            log.warn("Etibarsız token: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractAllClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Token-dən UserPrincipal yenidən qurur. JwtAuthFilter istifadə edir.
     */
    public UserPrincipal buildPrincipal(String token) {
        Claims claims = extractAllClaims(token);
        return UserPrincipal.of(
            UUID.fromString(claims.get(CLAIM_USER_ID, String.class)),
            claims.get(CLAIM_EMAIL, String.class),
            claims.get(CLAIM_USERNAME, String.class),
            extractRoles(token)
        );
    }

    // ── Private ───────────────────────────────────────────────────────────────

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
