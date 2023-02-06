package rs.securehome.admin.exception;

public class CRLSignatureInvalidException extends RuntimeException {

    public CRLSignatureInvalidException(String message) {
        super(message);
    }
}
