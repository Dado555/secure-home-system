package rs.securehome.admin.exception;

public class BlacklistedJWTException extends RuntimeException {

    public BlacklistedJWTException(String message) {
        super(message);
    }
}
