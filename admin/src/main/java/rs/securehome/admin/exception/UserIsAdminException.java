package rs.securehome.admin.exception;

public class UserIsAdminException extends RuntimeException {

    public UserIsAdminException(String message) {
        super(message);
    }
}
