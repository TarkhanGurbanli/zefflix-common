package az.zefflix.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT konfiqurasiya xüsusiyyətləri.
 *
 * <p>application.yml-də aşağıdakı kimi konfiqurasiya edilməlidir:
 * <pre>
 * zefflix:
 *   security:
 *     jwt:
 *       secret: &lt;base64-encoded-256-bit-secret&gt;
 *       access-token-expiry-ms: 900000      # 15 dəqiqə
 *       refresh-token-expiry-ms: 604800000  # 7 gün
 * </pre>
 */
@ConfigurationProperties(prefix = "zefflix.security.jwt")
public class JwtProperties {

    /**
     * Base64-encoded HMAC-SHA256 secret key (minimum 256 bit / 32 byte).
     */
    private String secret;

    /**
     * Access token-in etibarlılıq müddəti (millisaniyə). Default: 15 dəqiqə.
     */
    private long accessTokenExpiryMs = 900_000L;

    /**
     * Refresh token-in etibarlılıq müddəti (millisaniyə). Default: 7 gün.
     */
    private long refreshTokenExpiryMs = 604_800_000L;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getAccessTokenExpiryMs() {
        return accessTokenExpiryMs;
    }

    public void setAccessTokenExpiryMs(long accessTokenExpiryMs) {
        this.accessTokenExpiryMs = accessTokenExpiryMs;
    }

    public long getRefreshTokenExpiryMs() {
        return refreshTokenExpiryMs;
    }

    public void setRefreshTokenExpiryMs(long refreshTokenExpiryMs) {
        this.refreshTokenExpiryMs = refreshTokenExpiryMs;
    }

}
