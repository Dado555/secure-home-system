package rs.securehome.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.operator.OperatorCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rs.securehome.admin.annotation.Log;
import rs.securehome.admin.dto.CertificateResponseDTO;
import rs.securehome.admin.dto.CertificateRevocationDTO;
import rs.securehome.admin.dto.CertificateValidResponseDTO;
import rs.securehome.admin.dto.PrivateKeyDownloadDTO;
import rs.securehome.admin.service.CertificateService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/api/certificates")
public class CertificateController {

    private final CertificateService certificateService;

    @Autowired
    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @Log(message = "Fetching all certificates.")
    @GetMapping
    public List<CertificateResponseDTO> getAllCertificates(HttpServletRequest request) {
        return certificateService.getAllCertificates();
    }

    @Log(message = "Fetching certificate for a given alias.")
    @GetMapping("/{alias}")
    public CertificateResponseDTO getCertificate(HttpServletRequest request, @PathVariable String alias) throws CertificateEncodingException, IOException {
        return certificateService.getCertificate(alias);
    }


    @Log(message = "Checking validity of a certificate with a given alias.")
    @GetMapping(value = "/valid/{alias}")
    public CertificateValidResponseDTO isCertificateValid(HttpServletRequest request, @PathVariable("alias") String alias) {
        return certificateService.isCertificateValid(alias);
    }

    @Log(message = "Checking validity of a given .cer file.")
    @PostMapping(value = "/valid-cer")
    public CertificateValidResponseDTO isCertificateValid(HttpServletRequest request, @RequestParam("cerFile") MultipartFile cerFile) throws CertificateException, IOException {
        return this.certificateService.isCertificateValid(cerFile);
    }

    @Log(message = "Downloading a certificate.")
    @GetMapping(value = "/download-certificate")
    public ResponseEntity<InputStreamResource> downloadCertificate(HttpServletRequest request, @RequestParam("alias") String alias, @RequestParam("token") UUID token) throws CertificateEncodingException {
        return new ResponseEntity<>(new InputStreamResource(new ByteArrayInputStream(certificateService.getCertificateForDownload(alias, token))), HttpStatus.OK);
    }

    @Log(message = "Downloading a private key.")
    @PostMapping(value = "/download-key")
    public ResponseEntity<InputStreamResource> downloadPrivateKey(HttpServletRequest request, @RequestBody @Valid PrivateKeyDownloadDTO body) throws IOException, OperatorCreationException {
        return new ResponseEntity<>(new InputStreamResource(new ByteArrayInputStream(certificateService.getPrivateKeyForDownload(body.getAlias(), body.getPassword(), body.getToken()))), HttpStatus.OK);
    }

    @Log(message = "Revoking a certificate.")
    @PostMapping(value = "/revoke")
    public void revokeCertificate(HttpServletRequest request, @RequestBody @Valid CertificateRevocationDTO revocationDTO) throws IOException, OperatorCreationException {
        this.certificateService.revokeCertificate(revocationDTO.getCertificateAlias(), revocationDTO.getReason());
    }


}
