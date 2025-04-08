package vn.edu.iuh.fit.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.paymentservice.dto.TransactionHistoryDTO;
import vn.edu.iuh.fit.paymentservice.dto.BaseResponse;
import vn.edu.iuh.fit.paymentservice.service.TransactionHistoryService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${spring.application.api-prefix}/transaction-history")
@RequiredArgsConstructor
@Tag(name = "Transaction History Controller", description = "Quản lý lịch sử giao dịch người dùng")
public class TransactionHistoryController {

    private final TransactionHistoryService service;

    @Operation(
            summary = "Lấy tất cả lịch sử giao dịch",
            description = "Trả về toàn bộ danh sách giao dịch (nạp tiền, thanh toán...) trong hệ thống.",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Lấy danh sách thành công",
                    content = @Content(mediaType = "application/json")
            )
    )
    @GetMapping
    public BaseResponse<List<TransactionHistoryDTO>> getAll() {
        return new BaseResponse<>(true, "Lấy danh sách thành công", service.getAll());
    }

    @Operation(
            summary = "Lấy giao dịch theo ID",
            description = "Tìm kiếm một giao dịch cụ thể theo ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tìm thấy giao dịch"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy giao dịch")
            }
    )
    @GetMapping("/{id}")
    public BaseResponse<Optional<TransactionHistoryDTO>> getById(
            @Parameter(description = "ID của giao dịch") @PathVariable Long id) {
        return new BaseResponse<>(true, "Truy vấn thành công", service.getById(id));
    }

    @Operation(
            summary = "Lấy các giao dịch của một người dùng",
            description = "Lọc danh sách giao dịch theo userId.",
            responses = @ApiResponse(responseCode = "200", description = "Truy vấn thành công")
    )
    @GetMapping("/user/{userId}")
    public BaseResponse<List<TransactionHistoryDTO>> getByUserId(
            @Parameter(description = "ID của người dùng") @PathVariable Long userId) {
        return new BaseResponse<>(true, "Truy vấn thành công", service.getByUserId(userId));
    }

    @Operation(
            summary = "Lấy các giao dịch theo khoảng thời gian",
            description = "Lọc danh sách giao dịch theo khoảng thời gian từ ngày - đến ngày (yyyy-MM-dd).",
            responses = @ApiResponse(responseCode = "200", description = "Truy vấn thành công")
    )
    @GetMapping("/date-range")
    public BaseResponse<List<TransactionHistoryDTO>> getByDateRange(
            @Parameter(description = "Từ ngày (yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Đến ngày (yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return new BaseResponse<>(true, "Truy vấn theo thời gian thành công", service.getByDateRange(from, to));
    }

//    @Operation(
//            summary = "Tạo mới giao dịch",
//            description = "Tạo một bản ghi giao dịch mới vào lịch sử, ví dụ nạp tiền hoặc thanh toán.",
//            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
//                    required = true,
//                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionHistoryDTO.class))
//            ),
//            responses = @ApiResponse(responseCode = "200", description = "Tạo mới thành công")
//    )
//    @PostMapping
//    public BaseResponse<TransactionHistoryDTO> create(
//            @RequestBody TransactionHistoryDTO dto) {
//        return new BaseResponse<>(true, "Tạo mới giao dịch thành công", service.save(dto));
//    }
}
