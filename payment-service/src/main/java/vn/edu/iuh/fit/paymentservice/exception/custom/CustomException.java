package vn.edu.iuh.fit.paymentservice.exception.custom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatusCode;

import java.util.Map;
@Getter
@Setter
@AllArgsConstructor
public class CustomException extends RuntimeException{
    private HttpStatusCode httpStatusCode;
    private String errorCode;
    private String message;
    private Map<String, Object> details;

    public CustomException(HttpStatusCode status, String message) {
        super(message);
        this.httpStatusCode = status;
        this.message = message;
        this.errorCode = null;
        this.details = null;
    }

    public CustomException(HttpStatusCode status, String message, String errorCode) {
        super(message);
        this.httpStatusCode = status;
        this.message = message;
        this.errorCode = errorCode;
        this.details = null;
    }
}
