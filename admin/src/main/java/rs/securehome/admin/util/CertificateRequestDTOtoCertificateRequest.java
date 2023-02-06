package rs.securehome.admin.util;

import rs.securehome.admin.builder.CertificateRequestBuilder;
import rs.securehome.admin.dto.CertificateRequestDTO;
import rs.securehome.admin.model.CertificateRequest;

import java.util.HashSet;

public class CertificateRequestDTOtoCertificateRequest {

    private CertificateRequestDTOtoCertificateRequest() {
        throw new IllegalStateException("CertificateRequestDTOtoCertificateRequest cannot be instantiated.");
    }

    public static CertificateRequest convert(CertificateRequestDTO dto) {
        return CertificateRequestBuilder
                .createBuilder()
                    .setEmail(dto.getEmail())
                    .setName(dto.getName())
                    .setSurname(dto.getSurname())
                    .setOrganization(dto.getOrganization())
                    .setOrganizationUnit(dto.getOrganizationUnit())
                    .setCountryCode(dto.getCountryCode())
                    .setValidityStart(dto.getValidityStart())
                    .setValidityEnd(dto.getValidityEnd())
                    .setAuthorityKeyIdentifier(dto.getAuthorityKeyIdentifier())
                    .setSubjectKeyIdentifier(dto.getSubjectKeyIdentifier())
                    .setSubjectAlternativeNames(new HashSet<>(dto.getSubjectAlternativeNames()))
                    .setKeyUsages(new HashSet<>(dto.getKeyUsages()))
                    .setExtendedKeyUsages(new HashSet<>(dto.getExtendedKeyUsages()))
                    .setCommonName(dto.getCommonName())
                    .setProvince(dto.getProvince())
                    .setCity(dto.getCity())
                .build();
    }
}
