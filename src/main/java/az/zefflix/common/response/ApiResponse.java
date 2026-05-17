package az.zefflix.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import lombok.Getter;

/**
 * Bütün Zefflix REST endpointlərinin standart cavab wrapper-ı.
 *
 * <p>Uğurlu cavab nümunəsi:
 * <pre>
 * {
 *   "success": true,
 *   "data": { ... },
 *   "timestamp": "2024-01-01T12:00:00Z"
 * }
 * </pre>
 *
 * <p>Xəta cavabı nümunəsi:
 * <pre>
 * {
 *   "success": false,
 *   "errorCode": "RESOURCE_NOT_FOUND",
 *   "message": "Content tapilmadi: id = '42'",
 *   "timestamp": "2024-01-01T12:00:00Z"
 * }
 * </pre>
 *
 * @param <T> data payload tipi
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final String errorCode;
    private final String message;
    private final Instant timestamp;

    private ApiResponse(boolean success, T data, String errorCode, String message) {
        this.success = success;
        this.data = data;
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = Instant.now();
    }

    /**
     * Uğurlu cavab — data ilə.
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null);
    }

    /**
     * Uğurlu cavab — datasız (məs. DELETE əməliyyatları).
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, null, null, null);
    }

    /**
     * Uğurlu cavab — mesaj və data ilə.
     * Parametr sırası: (message, data) — test kontraktına uyğundur.
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, data, null, message);
    }

    /**
     * Xəta cavabı — errorCode, mesaj və əlavə detallar ilə.
     *
     * @param errorCode maşın tərəfindən oxunan xəta kodu (ASCII, UPPER_SNAKE_CASE)
     * @param message   insan tərəfindən oxunan xəta mesajı
     * @param details   əlavə detallar (field validation xətaları və s.)
     */
    public static <T> ApiResponse<T> error(String errorCode, String message, T details) {
        return new ApiResponse<>(false, details, errorCode, message);
    }

    /**
     * Xəta cavabı — data olmadan (ən çox istifadə olunan overload).
     *
     * @param errorCode maşın tərəfindən oxunan xəta kodu
     * @param message   insan tərəfindən oxunan xəta mesajı
     */
    public static <T> ApiResponse<T> error(String errorCode, String message) {
        return new ApiResponse<>(false, null, errorCode, message);
    }

}
