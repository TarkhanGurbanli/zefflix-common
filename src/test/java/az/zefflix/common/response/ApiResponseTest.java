package az.zefflix.common.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ApiResponse testi")
class ApiResponseTest {

    @Test
    @DisplayName("Ugurlu cavab - data ile")
    void successWithData_shouldSetCorrectFields() {
        ApiResponse<String> response = ApiResponse.success("test-data");
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo("test-data");
        assertThat(response.getErrorCode()).isNull();
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Ugurlu cavab - mesaj ve data ile")
    void successWithMessageAndData_shouldSetAllFields() {
        ApiResponse<Integer> response = ApiResponse.success("Tapildi", 42);
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Tapildi");
        assertThat(response.getData()).isEqualTo(42);
    }

    @Test
    @DisplayName("Xeta cavabi - butun saheler")
    void error_shouldSetAllErrorFields() {
        ApiResponse<Void> response = ApiResponse.error("NOT_FOUND", "Tapilmadi", null);
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getErrorCode()).isEqualTo("NOT_FOUND");
        assertThat(response.getMessage()).isEqualTo("Tapilmadi");
        assertThat(response.getData()).isNull();
    }

    @Test
    @DisplayName("Xeta cavabi - data-siz")
    void errorWithoutData_shouldHaveNullData() {
        ApiResponse<Void> response = ApiResponse.error("ERROR", "Xeta");
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getData()).isNull();
    }

}