package vn.edu.iuh.fit.addressservice.util;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Base response chuẩn cho tất cả API")
public class BaseResponse<T> {
    @Schema(description = "Trạng thái thành công hay thất bại", example = "true")
    private boolean success;

    @Schema(description = "Thông báo đi kèm", example = "Success")
    private String message;

    @Schema(description = "Dữ liệu trả về (có thể null hoặc object/list)", nullable = true)
    private T data;

    public static <T> BaseResponse<T> ok(T data) {
        return new BaseResponse<>(true, "Success", data);
    }

    public static <T> BaseResponse<T> error(String message) {
        return new BaseResponse<>(false, message, null);
    }
}
