package rs.securehome.house.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import rs.securehome.common.dto.PrivilegeCheckRequestDTO;
import rs.securehome.common.dto.PrivilegeCheckResponseDTO;
import rs.securehome.house.config.CustomFeignConfig;

@FeignClient(value = "privilegeClient", url = "https://127.0.0.1:8081/api/privileges", configuration = CustomFeignConfig.class)
public interface PrivilegeClient {

    @PostMapping(value = "/check")
    PrivilegeCheckResponseDTO checkPrivilege(@RequestHeader("Authorization") String bearerToken, @CookieValue("jwt-security") String jwtSecurity, @RequestBody PrivilegeCheckRequestDTO dto);
}
