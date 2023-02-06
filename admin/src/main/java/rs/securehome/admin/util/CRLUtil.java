package rs.securehome.admin.util;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.CertException;
import org.bouncycastle.cert.X509CRLEntryHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import rs.securehome.admin.exception.CRLSignatureInvalidException;
import rs.securehome.admin.model.enums.CertificateRevocationReason;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;


@Component
@Slf4j
public class CRLUtil {

    @Value("${crl.path}")
    private String crlPath;

    @Value("${crl.issuer.alias}")
    private String crlIssuerAlias;

    @Value("${crl.issuer.password}")
    private String crlIssuerPassword;

    @Value("${keystore.password}")
    private String keyStorePassword;

    @Value("${keystore.path}")
    private String keyStorePath;

    private final KeyStoreUtil keyStoreUtil;

    @Autowired
    public CRLUtil(KeyStoreUtil keyStoreUtil) {
        this.keyStoreUtil = keyStoreUtil;
    }


    public void createCRL() throws OperatorCreationException, IOException {
        File file = new File(crlPath);
        boolean result = file.createNewFile();
        if (!result) {
            log.info("Crl file at location: " + crlPath + " already exists.");
        } else {
            log.info("Created crl file at location: " + crlPath);
            var holder = this.buildCRL();
            this.writeCRLToFile(holder);
        }

    }

    public boolean isCertificateRevoked(BigInteger certificateSerialNumber) {
        X509CRLHolder holder = this.readCRLHolderFromFile();
        X509CRLEntryHolder entryHolder = holder.getRevokedCertificate(certificateSerialNumber);
        return entryHolder != null;
    }


    public void addCrlEntry(BigInteger certificateSerialNumber, CRLReason crlReason) throws OperatorCreationException, IOException {
       this.addEntry(certificateSerialNumber, crlReason.getValue().intValue());
    }

    public String getRevocationReason(BigInteger certificateSerialNumber) {
        X509CRLHolder holder = this.readCRLHolderFromFile();
        X509CRLEntryHolder entryHolder = holder.getRevokedCertificate(certificateSerialNumber);
        var extension = entryHolder.getExtension(Extension.reasonCode);
        if(extension == null)
            return CertificateRevocationReason.getReasonFromValue(0).toString();
        String extensionValue = extension.getExtnValue().toString();
        String stringCode = extension.getExtnValue().toString().charAt(extensionValue.length() - 1) + "";
        int code = Integer.parseInt(stringCode, 16);
        return CertificateRevocationReason.getReasonFromValue(code).toString();
    }

    public void addEntry(BigInteger certificateSerialNumber, int crlReason) throws OperatorCreationException, IOException {
        X509v2CRLBuilder builder = new X509v2CRLBuilder(this.readCRLHolderFromFile());
        builder.addCRLEntry(certificateSerialNumber, new Date(), crlReason);
        X509CRLHolder holder = this.buildCRL(builder);
        this.writeCRLToFile(holder);
    }


    private X509CRLHolder buildCRL(X509v2CRLBuilder builder) throws OperatorCreationException {
        ContentSigner contentSigner = this.createContentSigner();
        return builder.build(contentSigner);
    }

    private X509CRLHolder buildCRL() throws OperatorCreationException {
        keyStoreUtil.loadKeyStore(keyStorePath, keyStorePassword.toCharArray());
        ContentSigner contentSigner = this.createContentSigner();
        X500Name issuerName = keyStoreUtil.getX500Name(crlIssuerAlias);
        X509v2CRLBuilder crlBuilder = new X509v2CRLBuilder(issuerName, new Date());
        return crlBuilder.build(contentSigner);
    }

    private ContentSigner createContentSigner() throws OperatorCreationException {
        keyStoreUtil.loadKeyStore(keyStorePath, keyStorePassword.toCharArray());
        PrivateKey issuerKey = keyStoreUtil.getPrivateKey(crlIssuerAlias, crlIssuerPassword.toCharArray());
        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
        builder = builder.setProvider("BC");
        return builder.build(issuerKey);
    }

    private boolean isSignatureValid(X509CRLHolder crlHolder) {
        keyStoreUtil.loadKeyStore(keyStorePath, keyStorePassword.toCharArray());
        PublicKey issuerPublicKey = keyStoreUtil.getCertificate(crlIssuerAlias).getPublicKey();
        try {
            ContentVerifierProvider contentVerifierProvider = new JcaContentVerifierProviderBuilder().build(issuerPublicKey);
            return crlHolder.isSignatureValid(contentVerifierProvider);
        } catch (OperatorCreationException | CertException e) {
            return false;
        }
    }


    private X509CRLHolder readCRLHolderFromFile()  {
        try(FileInputStream fileInputStream = new FileInputStream(crlPath);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            PEMParser pemParser = new PEMParser(inputStreamReader)) {

            var pemObject = pemParser.readObject();
            X509CRLHolder crlHolder = (X509CRLHolder) pemObject;
            boolean signatureValid = this.isSignatureValid(crlHolder);
            if(!signatureValid)
                throw new CRLSignatureInvalidException("Signature verification failed for crl file.");


            return crlHolder;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void writeCRLToFile(X509CRLHolder holder) throws IOException {
        StringWriter stringWriter = new StringWriter();
        JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter);
        pemWriter.writeObject(holder);
        pemWriter.flush();
        pemWriter.close();
        try (FileOutputStream outputStream = new FileOutputStream(crlPath)) {
            outputStream.write(stringWriter.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
