package rs.securehome.house.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import rs.securehome.common.dto.HouseholdResponseDTO;
import rs.securehome.house.clients.HouseholdClient;

@RestController
@RequestMapping("api/households")
public class HouseholdController {

    private final HouseholdClient householdClient;

    public HouseholdController(HouseholdClient householdClient) {
        this.householdClient = householdClient;
    }

    @GetMapping
    public Page<HouseholdResponseDTO> getAllHouseholds(Pageable pageable, @RequestHeader("Authorization") String bearerToken, @Nullable @CookieValue("jwt-security") String jwtSecurity) {
        return householdClient.getAllHouseholds(bearerToken, jwtSecurity, pageable);
    }

    @GetMapping("/user/{id}")
    public HouseholdResponseDTO getHouseholdForUser(@RequestHeader("Authorization") String bearerToken, @Nullable @CookieValue("jwt-security") String jwtSecurity, @PathVariable Integer id) {
        return householdClient.getHouseholdForUser(bearerToken, jwtSecurity, id);
    }
}
