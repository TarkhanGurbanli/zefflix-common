package az.zefflix.common.exception;

import az.zefflix.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Bütün Zefflix mikroservislər üçün mərkəzləşdirilmiş exception handler.
 * {@link RestControllerAdvice} sayəsində hər servis bu sinifi
 * avtomatik @Bean kimi götürür.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ── Zefflix custom exceptions ────────────────────────────────────────────

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(
        ResourceNotFoundException ex, HttpServletRequest req) {
        log.warn("ResourceNotFound [{}] → {}", req.getRequestURI(), ex.getMessage());
        return buildResponse(ex.getStatus(), ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(
        BadRequestException ex, HttpServletRequest req) {
        log.warn("BadRequest [{}] → {}", req.getRequestURI(), ex.getMessage());
        return buildResponse(ex.getStatus(), ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict(
        ConflictException ex, HttpServletRequest req) {
        log.warn("Conflict [{}] → {}", req.getRequestURI(), ex.getMessage());
        return buildResponse(ex.getStatus(), ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(
        UnauthorizedException ex, HttpServletRequest req) {
        log.warn("Unauthorized [{}] → {}", req.getRequestURI(), ex.getMessage());
        return buildResponse(ex.getStatus(), ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<Void>> handleForbidden(
        ForbiddenException ex, HttpServletRequest req) {
        log.warn("Forbidden [{}] → {}", req.getRequestURI(), ex.getMessage());
        return buildResponse(ex.getStatus(), ex.getErrorCode(), ex.getMessage());
    }

    // ── Spring Security exceptions ────────────────────────────────────────────

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthentication(
        AuthenticationException ex, HttpServletRequest req) {
        log.warn("AuthenticationException [{}] → {}", req.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_FAILED", ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(
        BadCredentialsException ex, HttpServletRequest req) {
        log.warn("BadCredentials [{}]", req.getRequestURI());
        return buildResponse(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS",
            "Email və ya şifrə yanlışdır.");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(
        AccessDeniedException ex, HttpServletRequest req) {
        log.warn("AccessDenied [{}] → {}", req.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, "ACCESS_DENIED",
            "Bu əməliyyat üçün icazəniz yoxdur.");
    }

    // ── Validation exceptions ─────────────────────────────────────────────────

    /**
     * @Valid annotasiyası ilə gələn DTO validation xətaları.
     * Field adı → xəta mesajı şəklində qaytarılır.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(
        MethodArgumentNotValidException ex, HttpServletRequest req) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });

        log.warn("Validation failed [{}] → {} field error(s)", req.getRequestURI(), errors.size());
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("VALİDASİYA_XƏTİ", "Sahə doğrulaması uğursuz oldu.", errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolation(
        ConstraintViolationException ex, HttpServletRequest req) {

        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> cv : ex.getConstraintViolations()) {
            String path = cv.getPropertyPath().toString();
            String field = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
            errors.put(field, cv.getMessage());
        }

        log.warn("ConstraintViolation [{}]", req.getRequestURI());
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("CONSTRAINT_VIOLATION", "Məhdudiyyət pozulması.", errors));
    }

    // ── HTTP / request exceptions ─────────────────────────────────────────────

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotReadable(
        HttpMessageNotReadableException ex, HttpServletRequest req) {
        log.warn("NotReadable [{}] → {}", req.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "INVALID_JSON", "Sorğu gövdəsi oxuna bilmir.");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(
        MissingServletRequestParameterException ex, HttpServletRequest req) {
        log.warn("MissingParam [{}] → {}", req.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "MISSING_PARAMETER",
            String.format("Tələb olunan parametr yoxdur: '%s'", ex.getParameterName()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
        MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        log.warn("TypeMismatch [{}] → {}", req.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "TYPE_MISMATCH",
            String.format("Parametr '%s' üçün yanlış tip.", ex.getName()));
    }

    // ── Fallback ──────────────────────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(
        Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception [{}]", req.getRequestURI(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
            "Daxili server xətası baş verdi. Zəhmət olmasa sonra yenidən cəhd edin.");
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private <T> ResponseEntity<ApiResponse<T>> buildResponse(
        HttpStatus status, String code, String message) {
        return ResponseEntity.status(status)
            .body(ApiResponse.error(code, message, null));
    }
}
