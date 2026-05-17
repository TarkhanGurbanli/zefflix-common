package az.zefflix.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Mövcud resursula konflikt (409).
 * Məsələn: eyni email ilə ikinci qeydiyyat.
 */
public class ConflictException extends BaseException {

    private static final String ERROR_CODE = "CONFLICT";

    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT, ERROR_CODE);
    }

    public ConflictException(String resource, String field, Object value) {
        super(
            String.format("%s artıq mövcuddur: %s = '%s'", resource, field, value),
            HttpStatus.CONFLICT,
            ERROR_CODE
        );
    }
}
