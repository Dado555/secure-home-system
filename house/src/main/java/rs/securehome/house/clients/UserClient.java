package rs.securehome.house.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.securehome.common.dto.*;
import rs.securehome.house.config.CustomFeignConfig;

import javax.validation.Valid;
import java.util.List;

@FeignClient(value = "userClient", url = "https://127.0.0.1:8081/api/users", configuration = CustomFeignConfig.class)
public interface UserClient {

    @PutMapping(value = "/{id}/change-default-password")
    ResponseEntity<String> changeDefaultPassword(@RequestHeader("Authorization") String bearerToken, @CookieValue("jwt-security") String jwtSecurity, @PathVariable("id") Integer id, @Valid @RequestBody ChangeDefaultPasswordRequestDTO changeDefaultPasswordRequest);

    @GetMapping(value = "/logout")
    ResponseEntity<String> logout(@RequestHeader("Authorization") String bearerToken, @CookieValue("jwt-security") String jwtSecurity);

    @PostMapping(value = "/authenticate")
    ResponseEntity<AuthenticationResponseDTO> authenticate(@RequestBody AuthenticationRequestDTO authenticationRequestDTO);

    @GetMapping
    Page<UserResponseDTO> getAllUsers(@RequestHeader("Authorization") String bearerToken, @CookieValue("jwt-security") String jwtSecurity, @SpringQueryMap Pageable pageable);

    @GetMapping("/household/{id}")
    Page<UserResponseDTO> getAllUsersForHousehold(@RequestHeader("Authorization") String bearerToken, @CookieValue("jwt-security") String jwtSecurity, @SpringQueryMap Pageable pageable, @PathVariable Integer id);

    @GetMapping("/household-id/{id}")
    List<UserResponseDTO> getAllUsersForHouseholdId(@RequestHeader("Authorization") String bearerToken, @CookieValue("jwt-security") String jwtSecurity, @PathVariable Integer id);

    @PutMapping("/tenant/role/{id}")
    Page<UserResponseDTO> upgradeTenantRole(@RequestHeader("Authorization") String bearerToken, @CookieValue("jwt-security") String jwtSecurity, @PathVariable Integer id);

    @PutMapping("/tenant/role")
    Page<UserResponseDTO> updateTenantRole(@RequestHeader("Authorization") String bearerToken, @CookieValue("jwt-security") String jwtSecurity, @RequestBody UpdateTenantRoleDTO body);

    @PostMapping("/tenant")
    UserResponseDTO createTenant(@RequestHeader("Authorization") String bearerToken, @CookieValue("jwt-security") String jwtSecurity, @RequestBody CreateTenantDTO body);

    @PutMapping("/tenant/{id}")
    UserResponseDTO updateTenant(@RequestHeader("Authorization") String bearerToken, @CookieValue("jwt-security") String jwtSecurity, @RequestBody UpdateTenantDTO body, @PathVariable Integer id);

    @DeleteMapping("/{id}")
    UserResponseDTO deleteTenant(@RequestHeader("Authorization") String bearerToken, @CookieValue("jwt-security") String jwtSecurity, @PathVariable Integer id);
}
