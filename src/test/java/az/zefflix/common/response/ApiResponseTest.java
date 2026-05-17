package az.zefflix.common.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ApiResponse testi")
class ApiResponseTest {

    @Test
    @DisplayName("Uğurlu cavab - data ilə")
    void successWithData_shouldSetCorrectFields() {
        ApiResponse<String> response = ApiResponse.success("test-data");

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo("test-data");
        assertThat(response.getErrorCode()).isNull();
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Uğurlu cavab - mesaj və data ilə")
    void successWithMessageAndData_shouldSetAllFields() {
        ApiResponse<Integer> response = ApiResponse.success("Tapıldı", 42);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Tapıldı");
        assertThat(response.getData()).isEqualTo(42);
    }

    @Test
    @DisplayName("Xəta cavabı - bütün sahələr")
    void error_shouldSetAllErrorFields() {
        ApiResponse<Void> response = ApiResponse.error("NOT_FOUND", "Tapılmadı", null);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getErrorCode()).isEqualTo("NOT_FOUND");
        assertThat(response.getMessage()).isEqualTo("Tapılmadı");
        assertThat(response.getData()).isNull();
    }

    @Test
    @DisplayName("Xəta cavabı - data-sız")
    void errorWithoutData_shouldHaveNullData() {
        ApiResponse<Void> response = ApiResponse.error("ERROR", "Xəta");

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getData()).isNull();
    }
}
