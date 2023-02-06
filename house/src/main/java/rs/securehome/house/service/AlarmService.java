package rs.securehome.house.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rs.securehome.house.clients.DeviceClient;
import rs.securehome.house.model.Alarm;
import rs.securehome.house.repository.AlarmRepository;

import java.util.List;

@Service
public class AlarmService {

    private final AlarmRepository alarmRepository;

    private final DeviceClient deviceClient;

    @Autowired
    public AlarmService(AlarmRepository alarmRepository, DeviceClient deviceClient) {
        this.alarmRepository = alarmRepository;
        this.deviceClient = deviceClient;
    }

    public List<Alarm> getAlarmsForUser(String token, String cookie) {
        var response = this.deviceClient.getAllDevicesForHousehold(token, cookie);
        return this.alarmRepository.getAlarmsByDevicesIds(response.getDeviceIds(), Sort.by(Sort.Direction.DESC, "timestamp"));
    }

}
