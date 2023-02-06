package rs.securehome.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import rs.securehome.admin.annotation.Log;
import rs.securehome.admin.dto.CertificateRequestDTO;
import rs.securehome.admin.model.CertificateRequest;
import rs.securehome.admin.service.CertificateRequestService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;

@RestController
@RequestMapping(value = "/api/requests")
public class CertificateRequestController {

    private final CertificateRequestService certificateRequestService;

    public CertificateRequestController(CertificateRequestService certificateRequestService) {
        this.certificateRequestService = certificateRequestService;
    }

    @Log(message = "Approving a certificate request with a given id.")
    @PostMapping(value = "/approve/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void approveCertificateRequest(HttpServletRequest request, @PathVariable int id) throws NoSuchAlgorithmException, NoSuchProviderException {
        certificateRequestService.createCertificateEntry(id);
    }


    @Log(message = "Creating a new certificate request.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CertificateRequest createNewRequest(HttpServletRequest request, @RequestBody @Valid CertificateRequestDTO requestDTO) {
        return certificateRequestService.createNewRequest(requestDTO);
    }

    @Log(message = "Fetching all certificate requests.")
    @GetMapping(value = "/getAll")
    public List<CertificateRequest> getAllRequests(HttpServletRequest request) {
        return certificateRequestService.getAllCertificateRequests();
    }

    @Log(message = "Editing a certificate request with a given id.")
    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editRequest(HttpServletRequest request, @RequestBody @Valid CertificateRequestDTO requestDTO, @PathVariable Integer id) {
        certificateRequestService.editRequest(requestDTO, id);
    }
}
