package rs.securehome.house.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import rs.securehome.common.dto.HouseholdResponseDTO;
import rs.securehome.house.config.CustomFeignConfig;

@FeignClient(value = "householdClient", url = "https://127.0.0.1:8081/api/households", configuration = CustomFeignConfig.class)
public interface HouseholdClient {

    @GetMapping
    Page<HouseholdResponseDTO> getAllHouseholds(@RequestHeader("Authorization") String bearerToken, @CookieValue("jwt-security") String jwtSecurity, @SpringQueryMap Pageable pageable);

    @GetMapping("/user/{id}")
    HouseholdResponseDTO getHouseholdForUser(@RequestHeader("Authorization") String bearerToken, @CookieValue("jwt-security") String jwtSecurity, @PathVariable Integer id);
}
