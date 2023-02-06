package rs.securehome.admin.model.enums;

public enum CertificateRevocationReason {
    UNSPECIFIED(0),
    KEY_COMPROMISE(1),
    CA_COMPROMISE(2),
    AFFILIATION_CHANGED(3),
    SUPERSEDED(4),
    CESSATION_OF_OPERATION(5),
    CERTIFICATE_HOLD(6),
    PRIVILEGE_WITHDRAWN(9),
    AA_COMPROMISE(10);


    private final int value;

    CertificateRevocationReason(int value){
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static CertificateRevocationReason getReasonFromValue(int value) {
        for(var reason: CertificateRevocationReason.values()) {
            if(reason.getValue() == value)
                return reason;
        }
        throw new IllegalArgumentException(String.format("Value %d not recognized.", value));
    }

}
