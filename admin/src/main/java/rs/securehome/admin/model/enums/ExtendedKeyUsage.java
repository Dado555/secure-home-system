package rs.securehome.admin.model.enums;

import org.bouncycastle.asn1.x509.KeyPurposeId;

public enum ExtendedKeyUsage {
//    ADOBE_PDF_SIGNING(),
//    DOCUMENT_SIGNING(),
//    TSL_SIGNING(),
//    ENCRYPTED_FILE_SYSTEM(),
    IP_SECURITY_END_SYSTEM(KeyPurposeId.id_kp_ipsecEndSystem),
    OCSP_SIGNING(KeyPurposeId.id_kp_OCSPSigning),
    TLS_WEB_CLIENT_AUTHENTICATION(KeyPurposeId.id_kp_clientAuth),
    ANY_EXTENDED_KEY_USAGE(KeyPurposeId.anyExtendedKeyUsage),
    IP_SECURITY_TUNNEL_TERMINATION(KeyPurposeId.id_kp_ipsecTunnel),
    SMARTCARD_LOGON(KeyPurposeId.id_kp_smartcardlogon),
    TLS_WEB_SERVER_AUTHENTICATION(KeyPurposeId.id_kp_serverAuth),
    CODE_SIGNING(KeyPurposeId.id_kp_codeSigning),
    EMAIL_PROTECTION(KeyPurposeId.id_kp_emailProtection),
    IP_SECURITY_USER(KeyPurposeId.id_kp_ipsecUser),
    TIME_STAMPING(KeyPurposeId.id_kp_timeStamping);

    private final KeyPurposeId value;

    ExtendedKeyUsage(KeyPurposeId value) {
        this.value = value;
    }

    public KeyPurposeId getValue() {
        return value;
    }
}
