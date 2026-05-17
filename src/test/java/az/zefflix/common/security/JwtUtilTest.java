package az.zefflix.common.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("JwtUtil testi")
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserPrincipal testUser;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret",
                "test-secret-key-minimum-256-bits-long-for-hmac-sha256-algorithm");
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpiration", 900_000L);
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenExpiration", 604_800_000L);

        testUser = UserPrincipal.of(
                UUID.randomUUID(),
                "test@zefflix.az",
                "testuser",
                List.of("ROLE_USER")
        );
    }

    @Test
    @DisplayName("Access token yaradilir ve dogrulanir")
    void generateAccessToken_shouldBeValid() {
        String token = jwtUtil.generateAccessToken(testUser);
        assertThat(token).isNotBlank();
        assertThat(jwtUtil.isTokenValid(token)).isTrue();
        assertThat(jwtUtil.isAccessToken(token)).isTrue();
    }

    @Test
    @DisplayName("Token-den userId duzgun cixarilir")
    void extractUserId_shouldMatchOriginal() {
        String token = jwtUtil.generateAccessToken(testUser);
        UUID extractedId = jwtUtil.extractUserId(token);
        assertThat(extractedId).isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("Token-den email duzgun cixarilir")
    void extractEmail_shouldMatchOriginal() {
        String token = jwtUtil.generateAccessToken(testUser);
        assertThat(jwtUtil.extractEmail(token)).isEqualTo("test@zefflix.az");
    }

    @Test
    @DisplayName("Token-den roller duzgun cixarilir")
    void extractRoles_shouldContainUserRole() {
        String token = jwtUtil.generateAccessToken(testUser);
        List<String> roles = jwtUtil.extractRoles(token);
        assertThat(roles).containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("Refresh token yaradilir")
    void generateRefreshToken_shouldBeValidRefreshToken() {
        String token = jwtUtil.generateRefreshToken(testUser.getId());
        assertThat(token).isNotBlank();
        assertThat(jwtUtil.isTokenValid(token)).isTrue();
        assertThat(jwtUtil.isRefreshToken(token)).isTrue();
    }

    @Test
    @DisplayName("Token-den UserPrincipal yeniden qurulur")
    void buildPrincipal_shouldRestoreOriginalData() {
        String token = jwtUtil.generateAccessToken(testUser);
        UserPrincipal rebuilt = jwtUtil.buildPrincipal(token);
        assertThat(rebuilt.getId()).isEqualTo(testUser.getId());
        assertThat(rebuilt.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(rebuilt.getUsername()).isEqualTo(testUser.getUsername());
    }

    @Test
    @DisplayName("Muddeti bitmis token etibarsiz sayilir")
    void expiredToken_shouldBeInvalid() {
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpiration", -1L);
        String expiredToken = jwtUtil.generateAccessToken(testUser);
        assertThat(jwtUtil.isTokenValid(expiredToken)).isFalse();
        assertThat(jwtUtil.isTokenExpired(expiredToken)).isTrue();
    }

}