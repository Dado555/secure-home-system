package rs.securehome.admin.exception;

public class HouseholdNotEmptyException extends RuntimeException {
    public HouseholdNotEmptyException(String message) {
        super(message);
    }
}
