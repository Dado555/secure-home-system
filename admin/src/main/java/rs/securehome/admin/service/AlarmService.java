package rs.securehome.admin.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rs.securehome.admin.dto.CustomLogAlarmDTO;
import rs.securehome.admin.exception.NotFoundException;
import rs.securehome.admin.model.events.Alarm;
import rs.securehome.admin.model.events.AlarmType;
import rs.securehome.admin.model.events.CustomLogAlarm;
import rs.securehome.admin.repository.AlarmRepository;
import rs.securehome.admin.repository.CustomLogAlarmRepository;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class AlarmService {

    private final AlarmRepository alarmRepository;

    private final CustomLogAlarmRepository customLogAlarmRepository;

    @Autowired
    public AlarmService(AlarmRepository alarmRepository,
                        CustomLogAlarmRepository customLogAlarmRepository) {
        this.alarmRepository = alarmRepository;
        this.customLogAlarmRepository = customLogAlarmRepository;
    }

    public Alarm findById(Integer id) {
        return alarmRepository.findById(id);
    }

    public List<Alarm> findAllByType(AlarmType type) {
        return alarmRepository.findAllByType(type);
    }

    public Page<Alarm> findAllByIdNotNull(Pageable pageable) {
        return alarmRepository.findAllByIdNotNull(pageable);
    }

    public Page<Alarm> findAllByTypeAndDate(AlarmType[] type, String timeFrom, String timeTo, Pageable pageable) {
        LocalDateTime timestampFrom  = LocalDateTime.of(1970, 1, 1, 0, 0);
        LocalDateTime timestampTo = LocalDateTime.of(3000, 1, 1, 0, 0);

        if(!timeFrom.equals("")) {
            ZonedDateTime zdt = ZonedDateTime.parse(timeFrom);
            timestampFrom = zdt.toLocalDateTime();
        }
        if(!timeTo.equals("")) {
            ZonedDateTime zdt = ZonedDateTime.parse(timeTo);
            timestampTo = zdt.toLocalDateTime();
        }

        List<AlarmType> alarmTypes = Arrays.asList(type);
        return alarmRepository.findAllByTypeInAndTimestampBetween(alarmTypes, timestampFrom,
                timestampTo, pageable);
    }

    public CustomLogAlarm createCustomLogALarm(CustomLogAlarmDTO customLogAlarmDTO) {
        return customLogAlarmRepository.save(new CustomLogAlarm(customLogAlarmDTO.getType(), customLogAlarmDTO.getSubstring()));
    }

    public List<CustomLogAlarm> getAllCustomLogAlarms() {
        return customLogAlarmRepository.findAll();
    }

    public void deleteCustomLogAlarm(int id) {
        var alarmToDelete = customLogAlarmRepository.findById(id).orElseThrow(() -> new NotFoundException("Custom log alarm with ID " + id + " does not exist"));
        customLogAlarmRepository.delete(alarmToDelete);
    }
}
