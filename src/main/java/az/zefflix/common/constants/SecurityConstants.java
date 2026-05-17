package az.zefflix.common.constants;

/**
 * Güvənlik ilə bağlı sabitlər.
 */
@SuppressWarnings("HideUtilityClassConstructor")
public final class SecurityConstants {

    // ── HTTP Headers ──────────────────────────────────────────────────────────
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String USER_ID_HEADER = "X-User-Id";

    // ── Public endpoints (auth tələb etmir) ──────────────────────────────────
    public static final String[] PUBLIC_ENDPOINTS = {
        "/api/v1/auth/**",
        "/api/v1/contents",
        "/api/v1/contents/**",
        "/api/v1/persons/**",
        "/api/v1/search/**",
        "/actuator/health",
        "/actuator/info",
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html"
    };

    // ── Rollər ────────────────────────────────────────────────────────────────
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_MODERATOR = "ROLE_MODERATOR";
    public static final String ROLE_USER = "ROLE_USER";

    private SecurityConstants() {
        throw new UnsupportedOperationException("Bu sinif instansiya yaradıla bilməz.");
    }

}
