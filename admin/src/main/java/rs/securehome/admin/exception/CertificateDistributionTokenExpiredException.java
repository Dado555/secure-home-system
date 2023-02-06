package rs.securehome.admin.exception;

public class CertificateDistributionTokenExpiredException extends RuntimeException {

    public CertificateDistributionTokenExpiredException(String message) {
        super(message);
    }
}
