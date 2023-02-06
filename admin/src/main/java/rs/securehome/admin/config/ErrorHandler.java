package rs.securehome.admin.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import rs.securehome.admin.exception.*;
import rs.securehome.admin.util.ErrorObject;

import javax.security.auth.login.AccountLockedException;
import javax.servlet.http.HttpServletRequest;
import java.time.format.DateTimeParseException;

@ControllerAdvice
@Slf4j
public class ErrorHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    ErrorObject handleNotFoundException(HttpServletRequest request, NotFoundException ex) {
        return new ErrorObject(request, ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({CertificateDistributionTokenExpiredException.class, CertificateDistributionTokenMisuseException.class})
    @ResponseBody
    ErrorObject handleCertificateDistributionExceptions(HttpServletRequest request, RuntimeException ex) {
        return new ErrorObject(request, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseBody
    ErrorObject handleUsernameNotFoundException(HttpServletRequest request, UsernameNotFoundException ex) {
        return new ErrorObject(request, ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseBody
    ErrorObject handleBadCredentialsException(HttpServletRequest request, BadCredentialsException ex) {
        return new ErrorObject(request, ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    ErrorObject handleAccessDeniedException(HttpServletRequest request, AccessDeniedException ex) {
        return new ErrorObject(request, ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccountLockedException.class)
    @ResponseBody
    ErrorObject handleAccountLockedException(HttpServletRequest request, AccountLockedException ex) {
        return new ErrorObject(request, ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(LockedException.class)
    @ResponseBody
    ErrorObject handleLockedException(HttpServletRequest request, LockedException ex) {
        return new ErrorObject(request, ex.getMessage(), HttpStatus.FORBIDDEN);
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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CRLEntryExistsException.class)
    @ResponseBody
    ErrorObject handleCRLEntryExistsException(HttpServletRequest request, CRLEntryExistsException ex) {
        return new ErrorObject(request, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    ErrorObject handleJSONParseException(HttpServletRequest request, HttpMessageNotReadableException ex) {
        return new ErrorObject(request, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PasswordFailedPolicyCheckException.class)
    @ResponseBody
    ErrorObject handlePasswordFailedPolicyCheckException(HttpServletRequest request, PasswordFailedPolicyCheckException ex) {
        return new ErrorObject(request, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PasswordException.class)
    @ResponseBody
    ErrorObject handlePasswordException(HttpServletRequest request, PasswordException ex) {
        return new ErrorObject(request, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidRegularExpressionException.class)
    @ResponseBody
    ErrorObject handleInvalidRegularExpressionException(HttpServletRequest request, InvalidRegularExpressionException ex) {
        return new ErrorObject(request, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserIsAdminException.class)
    @ResponseBody
    ErrorObject handleUserIsAdminException(HttpServletRequest request, UserIsAdminException ex) {
        return new ErrorObject(request, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HouseholdNotEmptyException.class)
    @ResponseBody
    ErrorObject handleHouseholdNotEmptyException(HttpServletRequest request, HouseholdNotEmptyException ex) {
        return new ErrorObject(request, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TenantNotInHouseholdException.class)
    @ResponseBody
    ErrorObject handleTenantNotInHouseholdException(HttpServletRequest request, TenantNotInHouseholdException ex) {
        return new ErrorObject(request, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DateTimeParseException.class)
    @ResponseBody
    ErrorObject handleDateTimeParseException(HttpServletRequest request, DateTimeParseException ex) {
        return new ErrorObject(request, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RuleException.class)
    @ResponseBody
    ErrorObject handleRuleException(HttpServletRequest request, RuleException ex) {
        return new ErrorObject(request, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CRLSignatureInvalidException.class)
    @ResponseBody
    ErrorObject handleCRLSignatureInvalid(HttpServletRequest request, CRLSignatureInvalidException ex) {
        return new ErrorObject(request, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    ErrorObject handleException(HttpServletRequest request, Exception ex) {
        ex.printStackTrace();
        return new ErrorObject(request, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
