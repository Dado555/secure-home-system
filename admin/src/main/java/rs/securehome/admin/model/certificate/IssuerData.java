package rs.securehome.admin.model.certificate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.asn1.x500.X500Name;

import java.security.PrivateKey;
import java.security.cert.Certificate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IssuerData {
    private X500Name x500name;
    private PrivateKey privateKey;
    private Certificate certificate;
}
