package rs.securehome.admin.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.securehome.common.dto.PrivilegeCheckRequestDTO;
import rs.securehome.common.dto.PrivilegeCheckResponseDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/privileges")
@Slf4j
public class PrivilegeController {


    @PostMapping(value = "/check")
    public PrivilegeCheckResponseDTO checkPrivilege(HttpServletRequest request, @RequestBody PrivilegeCheckRequestDTO dto) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var authorities = authentication.getAuthorities();
        var hasPrivilege = authorities
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList())
                .contains(dto.getPrivilege());
        return new PrivilegeCheckResponseDTO(hasPrivilege);

    }
}
