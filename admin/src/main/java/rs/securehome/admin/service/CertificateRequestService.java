package rs.securehome.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rs.securehome.admin.builder.CertificateRequestBuilder;
import rs.securehome.admin.dto.CertificateRequestDTO;
import rs.securehome.admin.email.EmailService;
import rs.securehome.admin.exception.NotFoundException;
import rs.securehome.admin.model.CertificateDistributionToken;
import rs.securehome.admin.model.CertificateRequest;
import rs.securehome.admin.model.User;
import rs.securehome.admin.repository.AuthorityRepository;
import rs.securehome.admin.repository.CertificateDistributionTokenRepository;
import rs.securehome.admin.repository.CertificateRequestRepository;
import rs.securehome.admin.repository.UserRepository;
import rs.securehome.admin.util.CertificateRequestDTOtoCertificateRequest;
import rs.securehome.admin.util.CertificateUtil;
import rs.securehome.admin.util.KeyStoreUtil;
import rs.securehome.admin.util.PasswordGenerator;

import java.security.*;
import java.security.cert.Certificate;
import java.util.List;

@Service
public class CertificateRequestService {

    private final CertificateRequestRepository certificateRequestRepository;
    private final CertificateDistributionTokenRepository certificateDistributionTokenRepository;
    private final KeyStoreUtil keyStoreUtil;
    private final CertificateUtil certificateUtil;
    private final EmailService emailService;

    private final HouseholdService householdService;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Value("${keystore.path}")
    private String keyStorePath;

    @Value("${keystore.password}")
    private String keyStorePassword;

    @Value("${certificate.download.path}")
    private String certificateDownloadPath;

    @Autowired
    public CertificateRequestService(CertificateRequestRepository certificateRequestRepository,
                                     CertificateDistributionTokenRepository certificateDistributionTokenRepository, KeyStoreUtil keyStoreUtil,
                                     CertificateUtil certificateUtil,
                                     EmailService emailService, HouseholdService householdService,
                                     AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder,
                                     UserRepository userRepository) {
        this.certificateRequestRepository = certificateRequestRepository;
        this.certificateDistributionTokenRepository = certificateDistributionTokenRepository;
        this.keyStoreUtil = keyStoreUtil;
        this.certificateUtil = certificateUtil;
        this.emailService = emailService;
        this.householdService = householdService;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public void createCertificateEntry(Integer id) throws NoSuchAlgorithmException, NoSuchProviderException {
        var request = certificateRequestRepository.findById(id).orElseThrow(() -> new NotFoundException("Certificate request with id " + id + " does not exist."));
        keyStoreUtil.loadKeyStore(keyStorePath, keyStorePassword.toCharArray());
        var rootData = keyStoreUtil.readIssuerFromStore("root_ca", keyStorePassword.toCharArray());
        var issuerData = keyStoreUtil.readIssuerFromStore("intermediate_ca", keyStorePassword.toCharArray());

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        keyGen.initialize(2048, random);

        KeyPair subjectKeyPair = keyGen.generateKeyPair();

        var subjectCertificate = certificateUtil.generateCertificate(request, issuerData, subjectKeyPair.getPublic());

        var generatedPassword = PasswordGenerator.generatePassword(8 + random.nextInt(4));
        keyStoreUtil.writeCertificateChain(request.getEmail(), subjectKeyPair.getPrivate(), generatedPassword, new Certificate[]{rootData.getCertificate(), issuerData.getCertificate(), subjectCertificate});
        keyStoreUtil.saveKeyStore(keyStorePath, keyStorePassword.toCharArray());

        StringBuilder sb = new StringBuilder();
        for(char c : generatedPassword) {
            sb.append(c);
        }

        var token = new CertificateDistributionToken(request.getEmail());
        this.certificateDistributionTokenRepository.save(token);

        emailService.sendSimpleMessage(request.getEmail(),
                "Your secure home credentials",
                "Dear " + request.getName() + ",\nYour password for acquiring your key pair and logging into your application is: " + sb
        + " and you can use it by visiting this address: " +this.certificateDownloadPath + token.getValue().toString());

        // destroying password
        for(int i = 0; i < generatedPassword.length; i++) {
            generatedPassword[i] += random.nextInt(100);
        }

        var newHouseHead = new User(request.getEmail(), passwordEncoder.encode(sb.toString()), request.getName(), request.getSurname(), authorityRepository.findByName("HOUSE_HEAD"));
        var savedUser = userRepository.save(newHouseHead);

        householdService.makeNewHousehold(savedUser.getId());

        certificateRequestRepository.deleteById(id);
    }

    public CertificateRequest createNewRequest(CertificateRequestDTO requestDTO) {
        CertificateRequest request = CertificateRequestDTOtoCertificateRequest.convert(requestDTO);
        return certificateRequestRepository.save(request);
    }

    public void editRequest(CertificateRequestDTO requestDTO, Integer id) {
        var oldRequest = certificateRequestRepository.findById(id).orElseThrow(() -> new NotFoundException("Certificate request with that id does not exist."));
        CertificateRequest newRequest = CertificateRequestDTOtoCertificateRequest.convert(requestDTO);//

        oldRequest = CertificateRequestBuilder.createBuilder(oldRequest)
                .setAuthorityKeyIdentifier(newRequest.getAuthorityKeyIdentifier())
                .setSubjectKeyIdentifier(newRequest.getSubjectKeyIdentifier())
                .setSubjectAlternativeNames(newRequest.getSubjectAlternativeNames())
                .setKeyUsages(newRequest.getKeyUsages())
                .setExtendedKeyUsages(newRequest.getExtendedKeyUsages())
                .setCommonName(newRequest.getCommonName())
                .setEmail(newRequest.getEmail())
                .setName(newRequest.getName())
                .setSurname(newRequest.getSurname())
                .setOrganization(newRequest.getOrganization())
                .setOrganizationUnit(newRequest.getOrganizationUnit())
                .setCity(newRequest.getCity())
                .setProvince(newRequest.getProvince())
                .setCountryCode(newRequest.getCountryCode())
                .setValidityStart(newRequest.getValidityStart())
                .setValidityEnd(newRequest.getValidityEnd())
                .build();
        certificateRequestRepository.save(oldRequest);
    }

    public List<CertificateRequest> getAllCertificateRequests() {
        return certificateRequestRepository.findAll();
    }
}
