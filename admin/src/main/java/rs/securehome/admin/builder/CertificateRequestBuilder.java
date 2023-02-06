package rs.securehome.admin.builder;

import rs.securehome.admin.model.CertificateRequest;
import rs.securehome.admin.model.enums.ExtendedKeyUsage;
import rs.securehome.admin.model.enums.KeyUsage;

import java.util.Date;
import java.util.Set;

public class CertificateRequestBuilder {

    private final CertificateRequest instance;

    public CertificateRequestBuilder() {
        this.instance = new CertificateRequest();
    }

    public CertificateRequestBuilder(CertificateRequest instance){
        this.instance = instance;
    }

    public static CertificateRequestBuilder createBuilder() {
        return new CertificateRequestBuilder();
    }

    public static CertificateRequestBuilder createBuilder(CertificateRequest instance) {
        return new CertificateRequestBuilder(instance);
    }

    public CertificateRequestBuilder setEmail(String email){
        this.instance.setEmail(email);
        return this;
    }

    public CertificateRequestBuilder setName(String name){
        this.instance.setName(name);
        return this;
    }
    public CertificateRequestBuilder setSurname(String surname){
        this.instance.setSurname(surname);
        return this;
    }
    public CertificateRequestBuilder setOrganization(String organization){
        this.instance.setOrganization(organization);
        return this;
    }
    public CertificateRequestBuilder setOrganizationUnit(String organizationUnit){
        this.instance.setOrganizationUnit(organizationUnit);
        return this;
    }
    public CertificateRequestBuilder setCountryCode(String countryCode){
        this.instance.setCountryCode(countryCode);
        return this;
    }

    public CertificateRequestBuilder setCommonName(String commonName){
        this.instance.setCommonName(commonName);
        return this;
    }
    public CertificateRequestBuilder setProvince(String province){
        this.instance.setProvince(province);
        return this;
    }
    public CertificateRequestBuilder setCity(String city){
        this.instance.setCity(city);
        return this;
    }

    public CertificateRequestBuilder setValidityStart(Date validityStart){
        this.instance.setValidityStart(validityStart);
        return this;
    }
    public CertificateRequestBuilder setValidityEnd(Date validityEnd){
        this.instance.setValidityEnd(validityEnd);
        return this;
    }

    public CertificateRequestBuilder setAuthorityKeyIdentifier(Boolean authorityKeyIdentifier){
        this.instance.setAuthorityKeyIdentifier(authorityKeyIdentifier);
        return this;
    }

    public CertificateRequestBuilder setSubjectKeyIdentifier(Boolean subjectKeyIdentifier){
        this.instance.setSubjectKeyIdentifier(subjectKeyIdentifier);
        return this;
    }

    public CertificateRequestBuilder setSubjectAlternativeNames(Set<String> subjectAlternativeNames){
        this.instance.setSubjectAlternativeNames(subjectAlternativeNames);
        return this;
    }

    public CertificateRequestBuilder setKeyUsages(Set<KeyUsage> keyUsages){
        this.instance.setKeyUsages(keyUsages);
        return this;
    }


    public CertificateRequestBuilder setExtendedKeyUsages(Set<ExtendedKeyUsage> extendedKeyUsages){
        this.instance.setExtendedKeyUsages(extendedKeyUsages);
        return this;
    }



    public CertificateRequest build(){
        return this.instance;
    }

}
