package az.zefflix.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Yanlış sorğu parametrləri üçün (400).
 */
public class BadRequestException extends BaseException {

    private static final String ERROR_CODE = "BAD_REQUEST";

    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST, ERROR_CODE);
    }

    public BadRequestException(String field, String reason) {
        super(String.format("Sahə '%s': %s", field, reason), HttpStatus.BAD_REQUEST, ERROR_CODE);
    }
}
