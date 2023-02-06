package rs.securehome.house.util;

import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

public class ErrorObject {
    public String path;
    public String message;
    public Integer statusCode;
    public String statusReason;

    public ErrorObject() {}

    public ErrorObject(HttpServletRequest request, String message, HttpStatus statusCode) {
        this.path = request.getRequestURI();
        this.message = message;
        this.statusCode = statusCode.value();
        this.statusReason = statusCode.getReasonPhrase();
    }

}
