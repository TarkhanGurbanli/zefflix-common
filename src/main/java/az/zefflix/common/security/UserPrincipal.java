package az.zefflix.common.security;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * JWT token-dən çıxarılan autentifika olunmuş istifadəçi.
 * Spring Security {@link org.springframework.security.core.context.SecurityContext}-ə yerləşdirilir.
 *
 * <p>Hər servis bu principal-dan mövcud istifadəçi məlumatlarını alır:
 * <pre>
 *   UserPrincipal user = SecurityUtils.getCurrentUser();
 *   UUID userId = user.getId();
 * </pre>
 */
@Getter
@Builder
public class UserPrincipal implements UserDetails {

    private final UUID id;
    private final String email;
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * Rol siyahısından UserPrincipal yaradır.
     */
    public static UserPrincipal of(UUID id, String email, String username, List<String> roles) {
        List<SimpleGrantedAuthority> authorities = roles.stream()
            .map(role -> new SimpleGrantedAuthority(
                role.startsWith("ROLE_") ? role : "ROLE_" + role))
            .toList();

        return UserPrincipal.builder()
            .id(id)
            .email(email)
            .username(username)
            .authorities(authorities)
            .build();
    }

    @Override
    public String getPassword() {
        return null; // Şifrə heç vaxt token-də saxlanmır
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
}
