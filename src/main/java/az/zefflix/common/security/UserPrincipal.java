package az.zefflix.common.security;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * JWT token-ləri ilə işləyərkən istifadəçi məlumatlarını daşıyan immutable value object.
 *
 * <p>{@link UserDetails} implement edir ki, Spring Security
 * {@link org.springframework.security.core.context.SecurityContext}-ə
 * birbaşa yerləşdirilə bilsin və {@code SecurityUtils.getCurrentUser()} düzgün işləsin.
 *
 * <p>İstifadə nümunəsi:
 * <pre>
 *   UserPrincipal principal = UserPrincipal.of(
 *       UUID.fromString("..."),
 *       "user@zefflix.az",
 *       "username",
 *       List.of("ROLE_USER")
 *   );
 *   String token = jwtUtil.generateAccessToken(principal);
 * </pre>
 */
public final class UserPrincipal implements UserDetails {

    private final UUID id;
    private final String email;
    private final String username;
    private final List<String> roles;

    private UserPrincipal(UUID id, String email, String username, List<String> roles) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.roles = List.copyOf(roles);
    }

    /**
     * Factory method.
     */
    public static UserPrincipal of(UUID id, String email, String username, List<String> roles) {
        return new UserPrincipal(id, email, username, roles);
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    // ── UserDetails ───────────────────────────────────────────────────────────

    /**
     * Spring Security üçün username = email.
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Rolları {@link GrantedAuthority} kolleksiyasına çevirir.
     * "ROLE_USER" → {@code SimpleGrantedAuthority("ROLE_USER")}
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    /**
     * Şifrə JWT axınında istifadə olunmur — boş string qaytarılır.
     */
    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Original String rol siyahısını qaytarır (JWT claim-ləri üçün).
     */
    public List<String> getRoles() {
        return roles;
    }

}