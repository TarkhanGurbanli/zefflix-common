package az.zefflix.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Resurs tapılmadıqda atılan exception (404).
 *
 * <p>İstifadə nümunəsi:
 * <pre>
 *   throw new ResourceNotFoundException("Content", "id", contentId);
 * </pre>
 */
public class ResourceNotFoundException extends BaseException {

    private static final String ERROR_CODE = "RESOURCE_NOT_FOUND";

    public ResourceNotFoundException(String resource, String field, Object value) {
        super(
            String.format("%s tapılmadı: %s = '%s'", resource, field, value),
            HttpStatus.NOT_FOUND,
            ERROR_CODE
        );
    }

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, ERROR_CODE);
    }
}
