package rs.securehome.admin.exception;

public class CRLEntryExistsException extends RuntimeException {

    public CRLEntryExistsException(String message) {
        super(message);
    }
}
