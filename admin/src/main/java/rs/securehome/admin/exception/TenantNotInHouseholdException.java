package rs.securehome.admin.exception;

public class TenantNotInHouseholdException extends RuntimeException{
    public TenantNotInHouseholdException(String message) {
        super(message);
    }
}
