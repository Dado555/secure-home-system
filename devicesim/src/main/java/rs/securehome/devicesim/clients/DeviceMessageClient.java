package rs.securehome.devicesim.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import rs.securehome.common.dto.DeviceMessageDTO;
import rs.securehome.devicesim.config.CustomFeignConfig;

@FeignClient(value = "devicemessageclient", url = "https://127.0.0.2:8082/api/messages", configuration = CustomFeignConfig.class)
public interface DeviceMessageClient {

    @PostMapping
    void sendMessage(@RequestBody DeviceMessageDTO deviceMessageDTO);
}
