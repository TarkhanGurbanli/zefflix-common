package az.zefflix.common.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("Exception siniflerinin testi")
class ExceptionTest {

    @Test
    @DisplayName("ResourceNotFoundException - resurs, sahe, deyer ile")
    void resourceNotFound_withResourceFieldValue() {
        ResourceNotFoundException ex =
                new ResourceNotFoundException("Film", "id", 42L);
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ex.getMessage()).contains("Film").contains("id").contains("42");
        assertThat(ex.getErrorCode()).isEqualTo("RESOURCE_NOT_FOUND");
    }

    @Test
    @DisplayName("ConflictException - resurs, sahe, deyer ile")
    void conflictException_withResourceFieldValue() {
        ConflictException ex =
                new ConflictException("User", "email", "admin@zefflix.az");
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex.getMessage()).contains("admin@zefflix.az");
    }

    @Test
    @DisplayName("BadRequestException - sahe ve sebeb ile")
    void badRequest_withFieldAndReason() {
        BadRequestException ex = new BadRequestException("email", "etibarsizdir");
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ex.getMessage()).contains("email").contains("etibarsizdir");
    }

    @Test
    @DisplayName("UnauthorizedException - default mesaj")
    void unauthorized_defaultMessage() {
        UnauthorizedException ex = new UnauthorizedException();
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(ex.getMessage()).isNotBlank();
    }

    @Test
    @DisplayName("ForbiddenException - default mesaj")
    void forbidden_defaultMessage() {
        ForbiddenException ex = new ForbiddenException();
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(ex.getMessage()).isNotBlank();
    }

}
