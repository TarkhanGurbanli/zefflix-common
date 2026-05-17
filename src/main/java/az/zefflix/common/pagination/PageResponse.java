package az.zefflix.common.pagination;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Sayfalı siyahı cavabları üçün standart wrapper.
 *
 * <p>Cavab nümunəsi:
 * <pre>
 * {
 *   "content": [...],
 *   "page": 0,
 *   "size": 20,
 *   "totalElements": 150,
 *   "totalPages": 8,
 *   "first": true,
 *   "last": false
 * }
 * </pre>
 *
 * @param <T> siyahı element tipi
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean first;
    private final boolean last;
    private final boolean empty;

    /**
     * Spring Data {@code Page} obyektindən PageResponse yaradır.
     */
    public static <T> PageResponse<T> from(org.springframework.data.domain.Page<T> springPage) {
        return PageResponse.<T>builder()
            .content(springPage.getContent())
            .page(springPage.getNumber())
            .size(springPage.getSize())
            .totalElements(springPage.getTotalElements())
            .totalPages(springPage.getTotalPages())
            .first(springPage.isFirst())
            .last(springPage.isLast())
            .empty(springPage.isEmpty())
            .build();
    }

    /**
     * Spring Data {@code Page}-i fərqli DTO tipi ilə PageResponse-a map edir.
     * Servis qatında entity → DTO çevrilməsindən sonra istifadə olunur.
     */
    public static <T> PageResponse<T> of(List<T> content,
        org.springframework.data.domain.Page<?> springPage) {
        return PageResponse.<T>builder()
            .content(content)
            .page(springPage.getNumber())
            .size(springPage.getSize())
            .totalElements(springPage.getTotalElements())
            .totalPages(springPage.getTotalPages())
            .first(springPage.isFirst())
            .last(springPage.isLast())
            .empty(content.isEmpty())
            .build();
    }

}
