package rs.securehome.admin.util;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX500NameUtil;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.stereotype.Component;
import rs.securehome.admin.exception.NotFoundException;
import rs.securehome.admin.model.certificate.IssuerData;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

@Component
@Getter
@Setter
@Slf4j
public class KeyStoreUtil {

    private KeyStore keyStore;

    public KeyStoreUtil() {
        try {
            keyStore = KeyStore.getInstance("JKS", "SUN");
        } catch (KeyStoreException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    public void loadKeyStore(String fileName, char[] password) {
        try {
            if (fileName != null) {
                keyStore.load(new FileInputStream(fileName), password);
            } else {
                keyStore.load(null, password);
            }
        } catch (NoSuchAlgorithmException | CertificateException | IOException e) {
            e.printStackTrace();
        }
    }

    public void saveKeyStore(String fileName, char[] password) {
        try {
            keyStore.store(new FileOutputStream(fileName), password);
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            e.printStackTrace();
        }
    }

    public Certificate[] getCertificateChain(String alias) throws KeyStoreException {
        Certificate[] chain = keyStore.getCertificateChain(alias);
        this.throwIfCertChainIsNull(chain, alias);
        return chain;
    }

    public void writeCertificateChain(String alias, PrivateKey privateKey, char[] password, Certificate[] certificateChain) {
        try {
            keyStore.setKeyEntry(alias, privateKey, password, certificateChain);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }

    public PrivateKey getPrivateKey(String alias, char[] password) {
        try {
            return (PrivateKey) keyStore.getKey(alias, password);
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new NotFoundException(String.format("Alias %s not found.", alias));
        }

    }

    public X500Name getX500Name(String alias) {
        try {
            Certificate[] certChain = keyStore.getCertificateChain(alias);
            this.throwIfCertChainIsNull(certChain, alias);
            Certificate cert = certChain[certChain.length - 1];
            JcaX509CertificateHolder holder = new JcaX509CertificateHolder((X509Certificate) cert);
            return holder.getSubject();

        } catch (KeyStoreException | CertificateEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public IssuerData readIssuerFromStore(String alias, char[] keyPass) {
        try {
            Certificate[] certChain = keyStore.getCertificateChain(alias);
            this.throwIfCertChainIsNull(certChain, alias);
            Certificate cert = certChain[certChain.length - 1];

            PrivateKey privKey = (PrivateKey) keyStore.getKey(alias, keyPass);

            X500Name issuerName = new JcaX509CertificateHolder((X509Certificate) cert).getSubject();
            return new IssuerData(issuerName, privKey, cert);
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException |
                UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Certificate getCertificate(String alias) {
        try {
            Certificate[] certChain = keyStore.getCertificateChain(alias);
            this.throwIfCertChainIsNull(certChain, alias);
            return certChain[certChain.length - 1];
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }


    public Certificate[] getChainForX500Name(X500Name x500Name) {
        try {
            Enumeration<String> aliases = keyStore.aliases();
            while(aliases.hasMoreElements()){
                var alias = aliases.nextElement();
                var cert = (X509Certificate) this.getCertificate(alias);
                var subjectX500 = JcaX500NameUtil.getSubject(cert);
                if(subjectX500.toString().equals(x500Name.toString()))
                    return this.getCertificateChain(alias);
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
            throw new NotFoundException(String.format("Certificate for: %s not found.", x500Name));
        }
        throw new NotFoundException(String.format("Certificate for: %s not found.", x500Name));
    }

    private void throwIfCertChainIsNull(Certificate[] certificates, String alias) {
        if (certificates == null)
            throw new NotFoundException(String.format("Alias: %s not found.", alias));
    }
}
