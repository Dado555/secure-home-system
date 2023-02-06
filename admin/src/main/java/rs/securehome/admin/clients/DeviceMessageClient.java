package rs.securehome.admin.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rs.securehome.admin.config.CustomFeignConfig;
import rs.securehome.common.dto.DeviceMessageFrequencyResponseDTO;

@FeignClient(value = "deviceMessageClient", url = "https://127.0.0.2:8082/api/messages", configuration = CustomFeignConfig.class)
public interface DeviceMessageClient {

    @GetMapping(value = "/frequency")
    DeviceMessageFrequencyResponseDTO countFrequency(@RequestParam("seconds") Integer seconds, @RequestParam("messagePart") String messagePart);
}
