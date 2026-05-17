package az.zefflix.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * Bütün Zefflix API endpoint-lərinin standart cavab formatı.
 *
 * <p>Uğurlu cavab nümunəsi:
 * <pre>
 * {
 *   "success": true,
 *   "message": "Məzmun tapıldı.",
 *   "data": { ... },
 *   "timestamp": "2024-01-15T10:30:00"
 * }
 * </pre>
 *
 * <p>Xəta cavabı nümunəsi:
 * <pre>
 * {
 *   "success": false,
 *   "errorCode": "RESOURCE_NOT_FOUND",
 *   "message": "Film tapılmadı: id = '99'",
 *   "timestamp": "2024-01-15T10:30:00"
 * }
 * </pre>
 *
 * @param <T> cavab data tipi
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final String errorCode;
    private final T data;

    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();

    // ── Static factory methods ────────────────────────────────────────────────

    /**
     * Data ilə uğurlu cavab.
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .data(data)
            .build();
    }

    /**
     * Data və xüsusi mesaj ilə uğurlu cavab.
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .message(message)
            .data(data)
            .build();
    }

    /**
     * Sadəcə mesaj ilə uğurlu cavab (data yoxdur).
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
            .success(true)
            .message(message)
            .build();
    }

    /**
     * Xəta cavabı.
     *
     * @param errorCode  maşın tərəfindən oxunan xəta kodu
     * @param message    istifadəçiyə göstəriləcək mesaj
     * @param data       əlavə xəta detalları (məsələn, field validation xətaları)
     */
    public static <T> ApiResponse<T> error(String errorCode, String message, T data) {
        return ApiResponse.<T>builder()
            .success(false)
            .errorCode(errorCode)
            .message(message)
            .data(data)
            .build();
    }

    /**
     * Data-sız xəta cavabı.
     */
    public static <T> ApiResponse<T> error(String errorCode, String message) {
        return error(errorCode, message, null);
    }
}
