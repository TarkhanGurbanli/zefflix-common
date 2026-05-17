package az.zefflix.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Kimliksiz giriş cəhdi (401).
 */
public class UnauthorizedException extends BaseException {

    private static final String ERROR_CODE = "UNAUTHORIZED";

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, ERROR_CODE);
    }

    public UnauthorizedException() {
        super("Giriş üçün autentifikasiya tələb olunur.", HttpStatus.UNAUTHORIZED, ERROR_CODE);
    }
}
