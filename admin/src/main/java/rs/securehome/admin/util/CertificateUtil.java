package rs.securehome.admin.util;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Component;
import rs.securehome.admin.model.CertificateRequest;
import rs.securehome.admin.model.certificate.IssuerData;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class CertificateUtil {
    public CertificateUtil() {
        Security.addProvider(new BouncyCastleProvider());
    }

    public X509Certificate generateCertificate(CertificateRequest request, IssuerData issuerData, PublicKey subjectKey) {
        try {
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            builder = builder.setProvider("BC");
            ContentSigner contentSigner = builder.build(issuerData.getPrivateKey());

            int id = request.getId() + 2;
            X500Name subjectName = createX500Name(request);
            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerData.getX500name(),
                    new BigInteger(Integer.toString(id)),
                    request.getValidityStart(),
                    request.getValidityEnd(),
                    subjectName,
                    subjectKey);

            JcaX509ExtensionUtils rootCertExtUtils = new JcaX509ExtensionUtils();
            certGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));

            if(Boolean.TRUE.equals(request.getSubjectKeyIdentifier())) {
                certGen.addExtension(Extension.subjectKeyIdentifier, false, rootCertExtUtils.createSubjectKeyIdentifier(subjectKey));
            }

            if(Boolean.TRUE.equals(request.getAuthorityKeyIdentifier())) {
                certGen.addExtension(Extension.authorityKeyIdentifier, false, rootCertExtUtils.createAuthorityKeyIdentifier(issuerData.getCertificate().getPublicKey()));
            }

            if(!request.getKeyUsages().isEmpty()) {
                int keyUsage = 0;
                for(var usage : request.getKeyUsages()) {
                    keyUsage = keyUsage | usage.getValue();
                }
                KeyUsage keyUsages = new KeyUsage(keyUsage);
                certGen.addExtension(Extension.keyUsage, true, keyUsages);
            }

            if(!request.getExtendedKeyUsages().isEmpty()) {
                List<KeyPurposeId> extendedUsages = new ArrayList<>();
                for(var extendedUsage : request.getExtendedKeyUsages()) {
                    extendedUsages.add(extendedUsage.getValue());
                }
                ExtendedKeyUsage extKeyUsage = new ExtendedKeyUsage(extendedUsages.toArray(new KeyPurposeId[0]));
                certGen.addExtension(Extension.extendedKeyUsage, false, extKeyUsage);
            }

            if(!request.getSubjectAlternativeNames().isEmpty()) {
                List<GeneralName> altNames = request.getSubjectAlternativeNames().stream().map(this::createGeneralName).collect(Collectors.toList());
                GeneralNames subjectAltNames = GeneralNames.getInstance(new DERSequence(altNames.toArray(new GeneralName[] {})));
                certGen.addExtension(Extension.subjectAlternativeName, false, subjectAltNames);
            }

            X509CertificateHolder certHolder = certGen.build(contentSigner);
            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider("BC");

            return certConverter.getCertificate(certHolder);
        } catch (IllegalArgumentException | IllegalStateException | OperatorCreationException | CertificateException | CertIOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private X500Name createX500Name(CertificateRequest request) {
        X500NameBuilder nameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        nameBuilder.addRDN(BCStyle.CN, request.getCommonName());
        nameBuilder.addRDN(BCStyle.SURNAME, request.getSurname());
        nameBuilder.addRDN(BCStyle.GIVENNAME, request.getName());
        nameBuilder.addRDN(BCStyle.O, request.getOrganization());
        nameBuilder.addRDN(BCStyle.OU, request.getOrganizationUnit());
        nameBuilder.addRDN(BCStyle.C, request.getCountryCode());
        nameBuilder.addRDN(BCStyle.ST, request.getProvince());
        nameBuilder.addRDN(BCStyle.L, request.getCity());
        nameBuilder.addRDN(BCStyle.E, request.getEmail());
        nameBuilder.addRDN(BCStyle.UID, UUID.randomUUID().toString());

        return nameBuilder.build();
    }

    private GeneralName createGeneralName(String alternativeName) {
        var ipv4Template = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";
        var emailTemplate = "^(.+)@(\\S+)$";
        if( Pattern.matches(ipv4Template, alternativeName)) {
            return new GeneralName(GeneralName.iPAddress, alternativeName);
        } else if (Pattern.matches(emailTemplate, alternativeName)) {
            return new GeneralName(GeneralName.rfc822Name, alternativeName);
        } else {
            return new GeneralName(GeneralName.dNSName, alternativeName);
        }
    }
}
