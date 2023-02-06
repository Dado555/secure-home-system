package rs.securehome.admin.exception;

public class PasswordFailedPolicyCheckException extends RuntimeException {
    public PasswordFailedPolicyCheckException(String message) {
        super(message);
    }
}
