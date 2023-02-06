package rs.securehome.house.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import rs.securehome.common.exception.UserNotOwningRealEstateException;
import rs.securehome.house.exception.UnauthorizedException;
import rs.securehome.house.util.ErrorObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

@ControllerAdvice
public class ErrorHandler {

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(UserNotOwningRealEstateException.class)
    @ResponseBody
    ErrorObject handleUserNotOwningRealEstateException(HttpServletRequest request, UserNotOwningRealEstateException ex) {
        return new ErrorObject(request, ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    ErrorObject handleUnauthorizedException(HttpServletRequest request, UnauthorizedException ex) {
        return new ErrorObject(request, ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(GeneralSecurityException.class)
    @ResponseBody
    ErrorObject handleGeneralSecurityException(HttpServletRequest request, GeneralSecurityException ex) {
        return new ErrorObject(request, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FeignException.class)
    @ResponseBody
    ErrorObject handleCertificateDistributionExceptions(HttpServletRequest request, HttpServletResponse response, FeignException ex) {
        var errorObjectStream = (ex.responseBody().get());
        String result = StandardCharsets.UTF_8.decode(errorObjectStream).toString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            var error = mapper.readValue(result, ErrorObject.class);
            response.setStatus(error.statusCode);
            return error;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            var error = new ErrorObject(request, "Unknown error occurred.", HttpStatus.BAD_REQUEST);
            response.setStatus(error.statusCode);
            return error;
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    @ResponseBody
    ErrorObject handleMethodBindException(HttpServletRequest request, BindException ex) {
        StringBuilder errorStringBuilder = new StringBuilder();
        errorStringBuilder.append("Fields that failed validation: ");
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errorStringBuilder
                        .append(e.getField())
                        .append(" (")
                        .append(e.getDefaultMessage())
                        .append("); "));
        ex.getBindingResult().getGlobalErrors()
                .forEach(e -> errorStringBuilder
                        .append(e.getObjectName())
                        .append(" (")
                        .append(e.getDefaultMessage())
                        .append("); "));

        return new ErrorObject(request, errorStringBuilder.toString(), HttpStatus.BAD_REQUEST);
    }

}
