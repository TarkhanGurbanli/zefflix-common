package az.zefflix.common.security;

import az.zefflix.common.exception.UnauthorizedException;
import java.util.Optional;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Spring Security context-dən cari istifadəçi məlumatlarını almaq üçün yardımçı sinif.
 *
 * <p>Controller və ya servis qatında istifadə nümunəsi:
 * <pre>
 *   UUID userId = SecurityUtils.getCurrentUserId();
 *   UserPrincipal user = SecurityUtils.getCurrentUser();
 * </pre>
 */
@UtilityClass
public class SecurityUtils {

    /**
     * Cari autentifika olunmuş istifadəçini qaytarır.
     *
     * @throws UnauthorizedException autentifika yoxdursa
     */
    public UserPrincipal getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new UnauthorizedException();
        }
        return principal;
    }

    /**
     * Cari istifadəçinin ID-sini qaytarır.
     *
     * @throws UnauthorizedException autentifika yoxdursa
     */
    public UUID getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Cari istifadəçinin ID-sini Optional ilə qaytarır.
     * Anonimlik mümkün olan endpointlər üçün.
     */
    public Optional<UUID> getCurrentUserIdOptional() {
        try {
            return Optional.of(getCurrentUserId());
        } catch (UnauthorizedException e) {
            return Optional.empty();
        }
    }

    /**
     * Cari istifadəçinin müəyyən rola malik olduğunu yoxlayır.
     * Həm "ADMIN" həm də "ROLE_ADMIN" formatını qəbul edir.
     */
    public boolean hasRole(String role) {
        try {
            // FIX: getAuthorities() artıq UserPrincipal implements UserDetails sayəsində mövcuddur
            String normalizedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            return getCurrentUser().getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(normalizedRole));
        } catch (UnauthorizedException e) {
            return false;
        }
    }

    /**
     * Cari istifadəçinin ADMIN olduğunu yoxlayır.
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * Cari istifadəçinin MODERATOR olduğunu yoxlayır.
     */
    public boolean isModerator() {
        return hasRole("MODERATOR");
    }

}