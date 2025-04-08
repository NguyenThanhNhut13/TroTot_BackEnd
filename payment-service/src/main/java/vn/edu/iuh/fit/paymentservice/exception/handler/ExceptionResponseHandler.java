package vn.edu.iuh.fit.paymentservice.exception.handler;
import vn.edu.iuh.fit.paymentservice.exception.code.ErrorCode;
import vn.edu.iuh.fit.paymentservice.exception.custom.CustomException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import vn.edu.iuh.fit.paymentservice.model.BaseResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionResponseHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { CustomException.class })
    protected ResponseEntity<BaseResponse<?>> handleCustomException(CustomException ex, WebRequest request) {
        BaseResponse<?> response = new BaseResponse<>(false, ex.getMessage(), ex.getDetails());
        return new ResponseEntity<>(response, ex.getHttpStatusCode());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse<?>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, Object> details = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));

        BaseResponse<?> response = new BaseResponse<>(
                false,
                ErrorCode.INVALID_PARAMETER.getCode() + ": " + ErrorCode.INVALID_PARAMETER.getMessage(),
                details
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
