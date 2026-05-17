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
    public static UserPrincipal getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
            || !(auth.getPrincipal() instanceof UserPrincipal)) {
            throw new UnauthorizedException();
        }
        return (UserPrincipal) auth.getPrincipal();
    }

    /**
     * Cari istifadəçinin ID-sini qaytarır.
     *
     * @throws UnauthorizedException autentifika yoxdursa
     */
    public static UUID getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Cari istifadəçinin ID-sini Optional ilə qaytarır.
     * Anonimlik mümkün olan endpointlər üçün.
     */
    public static Optional<UUID> getCurrentUserIdOptional() {
        try {
            return Optional.of(getCurrentUserId());
        } catch (UnauthorizedException e) {
            return Optional.empty();
        }
    }

    /**
     * Cari istifadəçinin müəyyən rola malik olduğunu yoxlayır.
     */
    public static boolean hasRole(String role) {
        try {
            String normalizedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            return getCurrentUser().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(normalizedRole));
        } catch (UnauthorizedException e) {
            return false;
        }
    }

    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    public static boolean isModerator() {
        return hasRole("MODERATOR");
    }
}
