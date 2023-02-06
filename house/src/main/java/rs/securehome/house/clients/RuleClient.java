package rs.securehome.house.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import rs.securehome.common.dto.DeviceMessageAuthenticatedDTO;
import rs.securehome.house.config.CustomFeignConfig;

import javax.validation.Valid;

@FeignClient(value = "ruleClient", url = "https://127.0.0.1:8081/api/rules", configuration = CustomFeignConfig.class)
public interface RuleClient {

    @PostMapping(value = "/alarm-should-be-fired")
    boolean alarmShouldBeFired(@RequestBody @Valid DeviceMessageAuthenticatedDTO deviceMessageAuthenticatedDTO);
}
