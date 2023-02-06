package rs.securehome.admin.service;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX500NameUtil;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.openssl.PKCS8Generator;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8EncryptorBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.io.pem.PemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rs.securehome.admin.dto.CertificateResponseDTO;
import rs.securehome.admin.dto.CertificateValidResponseDTO;
import rs.securehome.admin.exception.*;
import rs.securehome.admin.model.enums.CertificateRevocationReason;
import rs.securehome.admin.repository.CertificateDistributionTokenRepository;
import rs.securehome.admin.util.CRLUtil;
import rs.securehome.admin.util.KeyStoreUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class CertificateService {

    private final CertificateDistributionTokenRepository certificateDistributionTokenRepository;

    private final KeyStoreUtil keyStoreUtil;

    @Value("${keystore.path}")
    private String keyStorePath;

    @Value("${keystore.password}")
    private String keyStorePassword;

    private final CRLUtil crlUtil;

    @Autowired
    public CertificateService(CertificateDistributionTokenRepository certificateDistributionTokenRepository, KeyStoreUtil keyStoreUtil, CRLUtil crlUtil) {
        this.certificateDistributionTokenRepository = certificateDistributionTokenRepository;
        this.keyStoreUtil = keyStoreUtil;
        this.crlUtil = crlUtil;
    }

    public List<CertificateResponseDTO> getAllCertificates() {
        keyStoreUtil.loadKeyStore(keyStorePath, keyStorePassword.toCharArray());

        ArrayList<CertificateResponseDTO> ret = new ArrayList<>();
        var keystore = keyStoreUtil.getKeyStore();
        Enumeration<String> enumeration;
        try {
            enumeration = keystore.aliases();
            while (enumeration.hasMoreElements()) {
                String alias = enumeration.nextElement();
                var certificates = keystore.getCertificateChain(alias);
                var certificate = certificates[certificates.length - 1];
                ret.add(this.createCertificateResponseDTO(alias, certificate));

            }
        } catch (KeyStoreException | CertificateEncodingException | IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public CertificateResponseDTO getCertificate(String alias) throws CertificateEncodingException, IOException {
        keyStoreUtil.loadKeyStore(keyStorePath, keyStorePassword.toCharArray());
        var certificate = keyStoreUtil.getCertificate(alias);
        return this.createCertificateResponseDTO(alias, certificate);
    }

    public void revokeCertificate(String alias, CertificateRevocationReason reason) throws IOException, OperatorCreationException {
        keyStoreUtil.loadKeyStore(keyStorePath, keyStorePassword.toCharArray());
        X509Certificate certificate = (X509Certificate) keyStoreUtil.getCertificate(alias);
        BigInteger certificateSerialNumber = certificate.getSerialNumber();
        if (crlUtil.isCertificateRevoked(certificateSerialNumber))
            throw new CRLEntryExistsException(String.format("Certificate with alias %s and id %d has already been revoked.", alias, certificateSerialNumber));

        if (certificateSerialNumber.equals(BigInteger.valueOf(1)) || certificateSerialNumber.equals(BigInteger.valueOf(2))) {
            throw new ForbiddenCertificateIdException(String.format("Certificate alias %s and id %d cannot be revoked.", alias, certificateSerialNumber));
        }
        crlUtil.addEntry(certificateSerialNumber, reason.getValue());
    }

    public CertificateValidResponseDTO isCertificateValid(MultipartFile cerFile) throws IOException, CertificateException {
        X509CertificateHolder holder = new X509CertificateHolder(cerFile.getBytes());
        X509Certificate certificate = new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(holder);
        return this.isCertificateValid(certificate);
    }

    public CertificateValidResponseDTO isCertificateValid(String alias) {
        keyStoreUtil.loadKeyStore(keyStorePath, keyStorePassword.toCharArray());
        var certificate = (X509Certificate) keyStoreUtil.getCertificate(alias);
        return this.isCertificateValid(certificate);
    }

    public CertificateValidResponseDTO isCertificateValid(X509Certificate x509Certificate) {
        try {
            x509Certificate.checkValidity();
            this.validateCertificateUpTheChain(x509Certificate);
            var certificateSerialNumber = x509Certificate.getSerialNumber();
            boolean valid = !crlUtil.isCertificateRevoked(certificateSerialNumber);
            if (valid) {
                return new CertificateValidResponseDTO(true, "");
            } else {
                String message = "Revoked because " + crlUtil.getRevocationReason(certificateSerialNumber);
                return new CertificateValidResponseDTO(false, message);
            }
        } catch (CertificateException | NoSuchAlgorithmException | SignatureException | InvalidKeyException | NoSuchProviderException e) {
            e.printStackTrace();
            return new CertificateValidResponseDTO(false, e.getMessage());
        }
    }

    private void validateCertificateUpTheChain(X509Certificate x509Certificate) throws CertificateException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, NoSuchProviderException {
        var issuerX500Name = JcaX500NameUtil.getIssuer(x509Certificate);
        keyStoreUtil.loadKeyStore(keyStorePath, keyStorePassword.toCharArray());
        var issuerChain = keyStoreUtil.getChainForX500Name(issuerX500Name);
        int issuerIndex;
        for (int i = 0; i < issuerChain.length; i++) {
            var cert = (X509Certificate) issuerChain[i];
            cert.checkValidity();
            if (i == 0)
                issuerIndex = 0;
            else
                issuerIndex = i - 1;

            cert.verify(issuerChain[issuerIndex].getPublicKey());
        }
        x509Certificate.verify(issuerChain[issuerChain.length - 1].getPublicKey());
    }

    private CertificateResponseDTO createCertificateResponseDTO(String alias, Certificate certificate) throws IOException, CertificateEncodingException {
        StringWriter stringWriter = new StringWriter();
        JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter);
        pemWriter.writeObject(certificate);
        pemWriter.flush();
        pemWriter.close();

        String aki = "";
        X509CertificateHolder h = new X509CertificateHolder(certificate.getEncoded());
        if (h.getExtension(Extension.authorityKeyIdentifier) != null) {
            aki = h.getExtension(Extension.authorityKeyIdentifier).getExtnValue().toString().substring(9);
        }

        return new CertificateResponseDTO(alias, stringWriter.toString().replace("\r\n", ""), aki);
    }

    public byte[] getCertificateForDownload(String alias, UUID tokenValue) throws CertificateEncodingException {
        keyStoreUtil.loadKeyStore(keyStorePath, keyStorePassword.toCharArray());
        var certificate = keyStoreUtil.getCertificate(alias);

        this.checkToken(alias, tokenValue, true);
        return certificate.getEncoded();
    }

    public byte[] getPrivateKeyForDownload(String alias, String password, UUID tokenValue) throws OperatorCreationException, IOException {

        keyStoreUtil.loadKeyStore(keyStorePath, keyStorePassword.toCharArray());
        var privateKey = keyStoreUtil.getPrivateKey(alias, password.toCharArray());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PemWriter pemWriter = new PemWriter(new OutputStreamWriter(outputStream));
        pemWriter.writeObject(new JcaPKCS8Generator(privateKey, new JceOpenSSLPKCS8EncryptorBuilder(
                PKCS8Generator.AES_256_CBC).setPasssword(password.toCharArray()).build()));
        pemWriter.close();

        this.checkToken(alias, tokenValue, false);

        return outputStream.toByteArray();
    }

    private void checkToken(String alias, UUID tokenValue, boolean checkForCertificate) {
        var token = this.certificateDistributionTokenRepository
                .getCertificateDistributionTokenByValue(tokenValue)
                .orElseThrow(() -> new NotFoundException(String.format("Token with value: %s not found.", tokenValue.toString())));

        if (!token.getAlias().equals(alias))
            throw new CertificateDistributionTokenMisuseException(String.format("Token with value: %s not allowed for alias: %s.", tokenValue.toString(), alias));
        if (token.isExpired())
            throw new CertificateDistributionTokenExpiredException(String.format("Token with value: %s has expired.", tokenValue.toString()));

        if (checkForCertificate) {
            if (Boolean.TRUE.equals(token.getUsedForCertificate()))
                throw new CertificateDistributionTokenMisuseException(String.format("Token with value: %s has already been used for certificate download.", tokenValue.toString()));
            token.setUsedForCertificate(true);
        } else {
            if (Boolean.TRUE.equals(token.getUsedForKey()))
                throw new CertificateDistributionTokenMisuseException(String.format("Token with value: %s has already been used for private key download.", tokenValue.toString()));
            token.setUsedForKey(true);
        }
        this.certificateDistributionTokenRepository.save(token);
    }

}
