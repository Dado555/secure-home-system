package rs.securehome.house.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import rs.securehome.common.dto.*;
import rs.securehome.house.clients.UserClient;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/users")
@Slf4j
public class UserController {

    private final UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @PutMapping(value = "/{id}/change-default-password")
    public String changeDefaultPassword(@RequestHeader("Authorization") String bearerToken, @Nullable @CookieValue("jwt-security") String jwtSecurity, @PathVariable("id") Integer id, @Valid @RequestBody ChangeDefaultPasswordRequestDTO changeDefaultPasswordRequest, HttpServletResponse response) {
        var responseEntity = this.userClient.changeDefaultPassword(bearerToken, jwtSecurity, id, changeDefaultPasswordRequest);
        var headers = responseEntity.getHeaders();
        var cookieHeaderValues = headers.get("Set-Cookie");
        response.addHeader("Set-Cookie", cookieHeaderValues.get(0));
        return responseEntity.getBody();

    }

    @PostMapping("/authenticate")
    public AuthenticationResponseDTO authenticate(@RequestBody AuthenticationRequestDTO requestBody, HttpServletResponse response) {
        var responseEntity = userClient.authenticate(requestBody);
        var headers = responseEntity.getHeaders();
        var cookieHeaderValues = headers.get("Set-Cookie");
        response.addHeader("Set-Cookie", cookieHeaderValues.get(0));
        return responseEntity.getBody();
    }

    @GetMapping(value = "/logout")
    public String logout(@RequestHeader("Authorization") String bearerToken, @Nullable @CookieValue("jwt-security") String jwtSecurity, HttpServletResponse response) {
        var responseEntity = this.userClient.logout(bearerToken, jwtSecurity);
        var headers = responseEntity.getHeaders();
        var cookieHeaderValues = headers.get("Set-Cookie");
        response.addHeader("Set-Cookie", cookieHeaderValues.get(0));
        return responseEntity.getBody();
    }

    @GetMapping
    public Page<UserResponseDTO> getAllUsers(Pageable pageable, @RequestHeader("Authorization") String bearerToken, @Nullable @CookieValue("jwt-security") String jwtSecurity) {
        return userClient.getAllUsers(bearerToken, jwtSecurity, pageable);
    }

    @GetMapping("/household/{id}")
    public Page<UserResponseDTO> getAllUsersForHousehold(Pageable pageable, @RequestHeader("Authorization") String bearerToken, @Nullable @CookieValue("jwt-security") String jwtSecurity, @PathVariable Integer id) {
        return userClient.getAllUsersForHousehold(bearerToken, jwtSecurity, pageable, id);
    }

    @GetMapping("/household-id/{id}")
    public List<UserResponseDTO> getAllUsersForHouseholdId(@RequestHeader("Authorization") String bearerToken, @Nullable @CookieValue("jwt-security") String jwtSecurity, @PathVariable Integer id) {
        return userClient.getAllUsersForHouseholdId(bearerToken, jwtSecurity, id);
    }

    @PutMapping("/tenant/role/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void upgradeTenantRole(@PathVariable Integer id, @RequestHeader("Authorization") String bearerToken, @Nullable @CookieValue("jwt-security") String jwtSecurity) {
        userClient.upgradeTenantRole(bearerToken, jwtSecurity, id);
    }

    @PutMapping("/tenant/role")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTenantRole(@RequestBody UpdateTenantRoleDTO body, @RequestHeader("Authorization") String bearerToken, @Nullable @CookieValue("jwt-security") String jwtSecurity) {
        userClient.updateTenantRole(bearerToken, jwtSecurity, body);
    }

    @PostMapping("/tenant")
    @ResponseStatus(HttpStatus.CREATED)
    public void createTenant(@RequestBody CreateTenantDTO body, @RequestHeader("Authorization") String bearerToken, @Nullable @CookieValue("jwt-security") String jwtSecurity) {
        userClient.createTenant(bearerToken, jwtSecurity, body);
    }

    @PutMapping("/tenant/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createTenant(@RequestBody UpdateTenantDTO body, @RequestHeader("Authorization") String bearerToken, @Nullable @CookieValue("jwt-security") String jwtSecurity, @PathVariable Integer id) {
        userClient.updateTenant(bearerToken, jwtSecurity, body, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTenant(@RequestHeader("Authorization") String bearerToken, @Nullable @CookieValue("jwt-security") String jwtSecurity, @PathVariable Integer id) {
        userClient.deleteTenant(bearerToken, jwtSecurity, id);
    }
}
