package rs.securehome.common.exception;

public class UserNotOwningRealEstateException extends RuntimeException {
    public UserNotOwningRealEstateException(String message) {
        super(message);
    }
}
