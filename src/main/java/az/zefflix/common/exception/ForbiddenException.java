package az.zefflix.common.exception;

import org.springframework.http.HttpStatus;

/**
 * İcazə yoxdur (403).
 */
public class ForbiddenException extends BaseException {

    private static final String ERROR_CODE = "FORBIDDEN";

    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN, ERROR_CODE);
    }

    public ForbiddenException() {
        super("Bu əməliyyat üçün icazəniz yoxdur.", HttpStatus.FORBIDDEN, ERROR_CODE);
    }
}
