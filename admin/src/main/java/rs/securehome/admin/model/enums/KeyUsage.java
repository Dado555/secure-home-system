package rs.securehome.admin.model.enums;

public enum KeyUsage {
    CERTIFICATE_SIGNING(4),
    CRL_SIGN(2),
    DATA_ENCIPHERMENT(16),
    DECIPHER_ONLY(32768),
    DIGITAL_SIGNATURE(128),
    ENCIPHER_ONLY(1),
    KEY_AGREEMENT(8),
    KEY_ENCIPHERMENT(32),
    NON_REPUDIATION(64);

    private final int value;

    KeyUsage(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
