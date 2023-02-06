package rs.securehome.admin;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.securehome.admin.builder.CertificateRequestBuilder;
import rs.securehome.admin.model.*;
import rs.securehome.admin.model.enums.ExtendedKeyUsage;
import rs.securehome.admin.model.enums.KeyUsage;
import rs.securehome.admin.repository.*;
import rs.securehome.admin.util.CRLUtil;
import rs.securehome.admin.util.KeyStoreUtil;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class DbInitializer implements ApplicationRunner {

    private final AuthorityRepository authorityRepository;
    private final CertificateRequestRepository certificateRequestRepository;
    private final UserRepository userRepository;
    private final PrivilegeRepository privilegeRepository;
    private final HouseholdRepository householdRepository;
    private final RealEstateRepository realEstateRepository;
    private final DeviceRepository deviceRepository;

    private final KeyStoreUtil keyStoreUtil;
    private final PasswordEncoder passwordEncoder;
    private final CRLUtil crlUtil;



    @Value("${keystore.password}")
    private String keyStorePassword;

    @Value("${keystore.path}")
    private String keyStorePath;

    private final SimpleDateFormat iso8601Formatter;


    @Autowired
    public DbInitializer(AuthorityRepository authorityRepository,
                         CertificateRequestRepository certificateRequestRepository,
                         UserRepository userRepository,
                         PrivilegeRepository privilegeRepository,
                         HouseholdRepository householdRepository,
                         RealEstateRepository realEstateRepository,
                         DeviceRepository deviceRepository,
                         KeyStoreUtil keyStoreUtil,
                         PasswordEncoder passwordEncoder,
                         CRLUtil crlUtil) {
        this.privilegeRepository = privilegeRepository;
        this.authorityRepository = authorityRepository;
        this.certificateRequestRepository = certificateRequestRepository;
        this.userRepository = userRepository;
        this.householdRepository = householdRepository;
        this.realEstateRepository = realEstateRepository;
        this.deviceRepository = deviceRepository;
        this.keyStoreUtil = keyStoreUtil;
        this.passwordEncoder = passwordEncoder;
        this.crlUtil = crlUtil;
        this.iso8601Formatter = new SimpleDateFormat("yyyy-MM-dd");

    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {

        var readCertificates = new Privilege("READ_CERTIFICATES");
        var readCertificate = new Privilege("READ_CERTIFICATE");
        var revokeCertificate = new Privilege("REVOKE_CERTIFICATE");
        var approveRequest = new Privilege("APPROVE_REQUEST");
        var readRequests = new Privilege("READ_REQUESTS");
        var editRequest = new Privilege("EDIT_REQUEST");
        var logout = new Privilege("LOGOUT");
        var readUsers = new Privilege("READ_USERS");
        var upgradeTenantRole = new Privilege("UPGRADE_TENANT_ROLE");
        var readHouseholds = new Privilege("READ_HOUSEHOLDS");
        var readRealEstatesForHousehold = new Privilege("READ_REAL_ESTATES_FOR_HOUSEHOLD");
        var deleteRealEstate = new Privilege("DELETE_REAL_ESTATE");
        var createTenant = new Privilege("CREATE_TENANT");
        var updateTenant = new Privilege("UPDATE_TENANT");
        var updateTenantRole = new Privilege("UPDATE_TENANT_ROLE");
        var changeRealEstateVisibility = new Privilege("CHANGE_REAL_ESTATE_VISIBILITY");
        var readUsersForHousehold = new Privilege("READ_USERS_FOR_HOUSEHOLD");
        var readHouseholdsForUser = new Privilege("READ_HOUSEHOLDS_FOR_USER");
        var deleteUser = new Privilege("DELETE_USER");
        var readVisibleRealEstate = new Privilege("READ_VISIBLE_REAL_ESTATE");
        var updateRealEstate = new Privilege("UPDATE_REAL_ESTATE");
        var createRealEstate = new Privilege("CREATE_REAL_ESTATE");
        var changeDefaultPassword = new Privilege("CHANGE_DEFAULT_PASSWORD");
        var readSingleRealEstate = new Privilege("READ_SINGLE_REAL_ESTATE");
        var readDevices = new Privilege("READ_DEVICES");
        var updateDevices = new Privilege("UPDATE_DEVICES");
        var createDevices = new Privilege("CREATE_DEVICES");
        var deleteDevices = new Privilege("DELETE_DEVICES");
        var readLogs = new Privilege("READ_LOGS");
        var checkDeviceOwnership = new Privilege("CHECK_DEVICE_OWNERSHIP");
        var viewAlarmsAdmin = new Privilege("VIEW_ALARMS_ADMIN");
        var createRule = new Privilege("CREATE_RULE");
        var readAlarms = new Privilege("READ_ALARMS");
        var checkPrivilege = new Privilege("CHECK_PRIVILEGE");
        var readDevicesForHousehold = new Privilege("READ_DEVICES_FOR_HOUSEHOLD");
        var readRules = new Privilege("READ_RULES");
        var createNewCustomLogRule = new Privilege("CREATE_NEW_CUSTOM_LOG_RULE");
        var deleteCustomLogRule = new Privilege("DELETE_CUSTOM_LOG_RULE");
        var readCustomLogRule = new Privilege("READ_CUSTOM_LOG_RULES");


        this.privilegeRepository.saveAll(Arrays.asList(readCertificates, readCertificate, revokeCertificate, approveRequest,
                readRequests, editRequest, logout, readUsers, upgradeTenantRole, readHouseholds, readRealEstatesForHousehold,
                deleteRealEstate, createTenant, updateTenant, updateTenantRole, changeRealEstateVisibility, readUsersForHousehold,
                readHouseholdsForUser, deleteUser, readVisibleRealEstate, updateRealEstate, createRealEstate, changeDefaultPassword,
                readDevices, updateDevices, createDevices, deleteDevices, readSingleRealEstate, checkDeviceOwnership, readLogs, createRule,
                readAlarms, checkPrivilege, readDevicesForHousehold, readRules, viewAlarmsAdmin, createNewCustomLogRule, deleteCustomLogRule, readCustomLogRule));


        Authority adminAuthority = new Authority("ADMIN");
        adminAuthority
                .addPrivilege(approveRequest)
                .addPrivilege(readCertificates)
                .addPrivilege(readCertificate)
                .addPrivilege(revokeCertificate)
                .addPrivilege(readRequests)
                .addPrivilege(editRequest)
                .addPrivilege(logout)
                .addPrivilege(readUsers)
                .addPrivilege(updateTenantRole)
                .addPrivilege(readHouseholds)
                .addPrivilege(readRealEstatesForHousehold)
                .addPrivilege(deleteRealEstate)
                .addPrivilege(readUsersForHousehold)
                .addPrivilege(deleteUser)
                .addPrivilege(changeDefaultPassword)
                .addPrivilege(readDevices)
                .addPrivilege(updateDevices)
                .addPrivilege(createDevices)
                .addPrivilege(deleteDevices)
                .addPrivilege(readSingleRealEstate)
                .addPrivilege(readLogs)
                .addPrivilege(viewAlarmsAdmin)
                .addPrivilege(createRule)
                .addPrivilege(checkPrivilege)
                .addPrivilege(readRules)
                .addPrivilege(createNewCustomLogRule)
                .addPrivilege(readCustomLogRule)
                .addPrivilege(deleteCustomLogRule);


        Authority houseHeadAuthority = new Authority("HOUSE_HEAD");
        houseHeadAuthority
                .addPrivilege(logout)
                .addPrivilege(createTenant)
                .addPrivilege(updateTenant)
                .addPrivilege(upgradeTenantRole)
                .addPrivilege(changeRealEstateVisibility)
                .addPrivilege(readUsersForHousehold)
                .addPrivilege(readHouseholdsForUser)
                .addPrivilege(readVisibleRealEstate)
                .addPrivilege(createRealEstate)
                .addPrivilege(updateRealEstate)
                .addPrivilege(changeDefaultPassword)
                .addPrivilege(readDevices)
                .addPrivilege(readSingleRealEstate)
                .addPrivilege(checkDeviceOwnership)
                .addPrivilege(readAlarms)
                .addPrivilege(checkPrivilege)
                .addPrivilege(readDevicesForHousehold);


        Authority houseTenantAuthority = new Authority("HOUSE_TENANT");
        houseTenantAuthority
                .addPrivilege(readUsersForHousehold)
                .addPrivilege(readHouseholdsForUser)
                .addPrivilege(logout)
                .addPrivilege(readVisibleRealEstate)
                .addPrivilege(changeDefaultPassword)
                .addPrivilege(readDevices)
                .addPrivilege(readSingleRealEstate)
                .addPrivilege(checkDeviceOwnership)
                .addPrivilege(checkPrivilege)
                .addPrivilege(readAlarms);

        this.authorityRepository.saveAll(Arrays.asList(adminAuthority, houseHeadAuthority, houseTenantAuthority));

        User admin = new User("email@email.com", passwordEncoder.encode("Password1!"), "Igor", "Kapisoda", adminAuthority);
        User headOfHousehold1 = new User("head1@email.com", passwordEncoder.encode("Password1!"), "Milan", "Vukotic", houseHeadAuthority);
        User headOfHousehold2 = new User("head2@email.com", passwordEncoder.encode("Password1!"), "Milos", "Stankovic", houseHeadAuthority);
        User tenant1OfHousehold1 = new User("tenant11@email.com", passwordEncoder.encode("Password1!"), "Nikola", "Vukotic", houseTenantAuthority);

        admin.setDefaultPasswordChanged(true);
        headOfHousehold1.setDefaultPasswordChanged(true);
        headOfHousehold2.setDefaultPasswordChanged(true);
        tenant1OfHousehold1.setDefaultPasswordChanged(true);

        userRepository.saveAll(Arrays.asList(admin, headOfHousehold1, headOfHousehold2, tenant1OfHousehold1));

        Household household1 = new Household();
        household1.addTenant(headOfHousehold1);
        household1.addTenant(tenant1OfHousehold1);
        Household household2 = new Household();
        household2.addTenant(headOfHousehold2);
        householdRepository.save(household1);
        householdRepository.save(household2);

        RealEstate re1 = new RealEstate("Kuca");
        re1.setDeviceReadingPeriod(5);
        RealEstate re2 = new RealEstate("Vikendica");
        re2.setDeviceReadingPeriod(10);
        RealEstate re3 = new RealEstate("Stan");
        re3.setDeviceReadingPeriod(5);
        household1.addObject(re1);
        household1.addObject(re2);
        household2.addObject(re3);
        re1.getCanBeSeenBy().add(headOfHousehold1);
        re1.getCanBeSeenBy().add(tenant1OfHousehold1);
        re2.getCanBeSeenBy().add(headOfHousehold1);
        re3.getCanBeSeenBy().add(headOfHousehold2);

        realEstateRepository.saveAll(Arrays.asList(re1, re2, re3));

        householdRepository.save(household1);
        householdRepository.save(household2);

        Device d1 = new Device("Thermometer - ground floor", ".*C", re1);
        Device d2 = new Device("Thermometer - wine cellar", "*", re1);
        Device d3 = new Device("Thermometer - living room", "*", re2);
        Device d4 = new Device("Smoke detector", ".*detected.*", re1);
        Device d5 = new Device("Entrance Door sensor", ".*opened.*", re1);
        Device d6 = new Device("Spotlight motion sensor", "*", re1);
        deviceRepository.saveAll(Arrays.asList(d1, d2, d3, d4, d5, d6));
        re1.addDevice(d1);
        re1.addDevice(d2);
        re1.addDevice(d4);
        re1.addDevice(d5);
        re1.addDevice(d6);
        re2.addDevice(d3);
        realEstateRepository.save(re1);
        realEstateRepository.save(re2);
        deviceRepository.saveAll(Arrays.asList(d1, d2, d3, d4, d5, d6));

        var extendedKeyUsages = createExtendedKeyUsages(ExtendedKeyUsage.IP_SECURITY_END_SYSTEM, ExtendedKeyUsage.TLS_WEB_CLIENT_AUTHENTICATION, ExtendedKeyUsage.IP_SECURITY_TUNNEL_TERMINATION);
        var keyUsages = createKeyUsages(KeyUsage.DIGITAL_SIGNATURE, KeyUsage.DATA_ENCIPHERMENT, KeyUsage.CRL_SIGN);
        var alternativeNames = createAlternativeNames("sajt.com", "www.sajtic.com", "192.0.2.146", "mrki@email.com");


        Date startDate = this.iso8601Formatter.parse("2022-03-31");
        Date endDate = this.iso8601Formatter.parse("2024-12-31");

        CertificateRequest request1 = CertificateRequestBuilder
                .createBuilder()
                .setEmail("email1@email.com")
                .setName("Joko")
                .setSurname("Joksimovic")
                .setOrganization("FTN-UNS")
                .setOrganizationUnit("Katedra za energetiku")
                .setCountryCode("RS")
                .setValidityStart(startDate)
                .setValidityEnd(endDate)
                .setSubjectKeyIdentifier(true)
                .setAuthorityKeyIdentifier(true)
                .setSubjectAlternativeNames(alternativeNames)
                .setKeyUsages(keyUsages)
                .setExtendedKeyUsages(extendedKeyUsages)
                .setCommonName("Joko Joksimovic")
                .setProvince("Vojvodina")
                .setCity("Novi Sad")
                .build();
        certificateRequestRepository.save(request1);

        KeyPair keyPairRoot = generateKeyPair();
        X500Name rootName = makeRootCAName();
        initializeKeyStore(admin, rootName, keyPairRoot);
        crlUtil.createCRL();

    }

    private Set<KeyUsage> createKeyUsages(KeyUsage... keyUsages) {
        return new HashSet<>(Arrays.asList(keyUsages));
    }

    private Set<String> createAlternativeNames(String... names) {
        return new HashSet<>(Arrays.asList(names));
    }

    private Set<ExtendedKeyUsage> createExtendedKeyUsages(ExtendedKeyUsage... extendedKeyUsages) {
        return new HashSet<>(Arrays.asList(extendedKeyUsages));
    }


    private KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        keyGen.initialize(2048, random);
        return keyGen.generateKeyPair();
    }

    private void initializeKeyStore(User admin, X500Name rootName, KeyPair keyPairRoot) throws CertificateException, NoSuchAlgorithmException, ParseException, NoSuchProviderException, OperatorCreationException, IOException {
        Security.addProvider(new BouncyCastleProvider());


        File file = new File(keyStorePath);
        if (file.exists()) {
            return;
        }

        keyStoreUtil.loadKeyStore(null, keyStorePassword.toCharArray());

        KeyPair keyPairAdmin = generateKeyPair();

        Certificate rootCA = makeRootCA(keyPairRoot, rootName);
        keyStoreUtil.writeCertificateChain("root_ca", keyPairRoot.getPrivate(), "password".toCharArray(), new Certificate[]{rootCA});

        Certificate intermediateCA = makeIntermediateCA(keyPairRoot.getPrivate(), keyPairRoot.getPublic(), keyPairAdmin.getPublic(), admin, rootName);
        keyStoreUtil.writeCertificateChain("intermediate_ca", keyPairAdmin.getPrivate(), "password".toCharArray(), new Certificate[]{rootCA, intermediateCA});

        keyStoreUtil.saveKeyStore(keyStorePath, keyStorePassword.toCharArray());

    }

    private Certificate makeRootCA(KeyPair keyPair, X500Name rootName) throws NoSuchAlgorithmException, OperatorCreationException, ParseException, CertIOException, CertificateException {
        Date startDate = this.iso8601Formatter.parse("2021-12-31");
        Date endDate = this.iso8601Formatter.parse("2025-12-31");
        return generateCertificate(keyPair.getPrivate(), keyPair.getPublic(), keyPair.getPublic(), rootName, rootName, startDate, endDate, "1");
    }

    private Certificate makeIntermediateCA(PrivateKey rootKey, PublicKey issuerPublicKey, PublicKey subjectKey, User admin, X500Name issuerName) throws NoSuchAlgorithmException, OperatorCreationException, ParseException, CertIOException, CertificateException {
        Date startDate = this.iso8601Formatter.parse("2021-12-31");
        Date endDate = this.iso8601Formatter.parse("2025-12-31");

        X500NameBuilder nameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        nameBuilder.addRDN(BCStyle.CN, admin.getName() + " " + admin.getSurname());
        nameBuilder.addRDN(BCStyle.SURNAME, admin.getSurname());
        nameBuilder.addRDN(BCStyle.GIVENNAME, admin.getName());
        nameBuilder.addRDN(BCStyle.O, "SHS");
        nameBuilder.addRDN(BCStyle.OU, "Secure Home System Administration");
        nameBuilder.addRDN(BCStyle.C, "RS");
        nameBuilder.addRDN(BCStyle.ST, "Vojvodina");
        nameBuilder.addRDN(BCStyle.L, "Novi Sad");
        nameBuilder.addRDN(BCStyle.E, admin.getEmail());
        nameBuilder.addRDN(BCStyle.UID, "2");
        X500Name subjectName = nameBuilder.build();

        return generateCertificate(rootKey, issuerPublicKey, subjectKey, issuerName, subjectName, startDate, endDate, "2");
    }

    private X500Name makeRootCAName() {
        X500NameBuilder nameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        nameBuilder.addRDN(BCStyle.CN, "Korenko Korenkovic");
        nameBuilder.addRDN(BCStyle.SURNAME, "Korenkovic");
        nameBuilder.addRDN(BCStyle.GIVENNAME, "Korenko");
        nameBuilder.addRDN(BCStyle.O, "SHS");
        nameBuilder.addRDN(BCStyle.OU, "Secure Home System Administration");
        nameBuilder.addRDN(BCStyle.C, "RS");
        nameBuilder.addRDN(BCStyle.ST, "Vojvodina");
        nameBuilder.addRDN(BCStyle.L, "Novi Sad");
        nameBuilder.addRDN(BCStyle.E, "korenko.korenkovic@uns.ac.rs");
        nameBuilder.addRDN(BCStyle.UID, "1");

        return nameBuilder.build();
    }

    private Certificate generateCertificate(PrivateKey issuerKey, PublicKey issuerPublicKey, PublicKey subjectKey, X500Name issuerName, X500Name subjectName, Date startDate, Date endDate, String uid) throws CertIOException, NoSuchAlgorithmException, OperatorCreationException, CertificateException {
        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
        builder = builder.setProvider("BC");

        ContentSigner contentSigner = builder.build(issuerKey);

        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerName,
                new BigInteger(uid),
                startDate,
                endDate,
                subjectName,
                subjectKey);

        JcaX509ExtensionUtils rootCertExtUtils = new JcaX509ExtensionUtils();
        certGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
        certGen.addExtension(Extension.subjectKeyIdentifier, false, rootCertExtUtils.createSubjectKeyIdentifier(subjectKey));
        certGen.addExtension(Extension.authorityKeyIdentifier, false, rootCertExtUtils.createAuthorityKeyIdentifier(issuerPublicKey));

        X509CertificateHolder certHolder = certGen.build(contentSigner);

        JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
        certConverter = certConverter.setProvider("BC");

        return certConverter.getCertificate(certHolder);
    }
}
