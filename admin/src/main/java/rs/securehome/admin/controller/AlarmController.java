package rs.securehome.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import rs.securehome.admin.annotation.Log;
import rs.securehome.admin.dto.CustomLogAlarmDTO;
import rs.securehome.admin.model.events.Alarm;
import rs.securehome.admin.model.events.AlarmType;
import rs.securehome.admin.service.AlarmService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/alarms")
@Slf4j
public class AlarmController {
    private final AlarmService alarmService;

    @Autowired
    public AlarmController(AlarmService alarmService) {
        this.alarmService = alarmService;
    }

    @Log(message = "Fetching all alarms.")
    @GetMapping
    public Page<Alarm> getAll(HttpServletRequest request, @PageableDefault Pageable pageable) {
        return alarmService.findAllByIdNotNull(pageable);
    }

    @Log(message = "Fetching alarm for a given id.")
    @GetMapping("/{id}")
    public Alarm getById(HttpServletRequest request, @PathVariable Integer id) {
        return alarmService.findById(id);
    }

    @Log(message = "Fetching filtered alarms for type and date.")
    @GetMapping("/byType")
    public Page<Alarm> getByTypeAndDate(HttpServletRequest request,
                                        @RequestParam(name = "alarmTypes") @Nullable AlarmType[] alarmTypes,
                                        @RequestParam(name = "timestampFrom") String timestampFrom,
                                        @RequestParam(name = "timestampTo") String timestampTo,
                                        @PageableDefault Pageable pageable) {
        return alarmService.findAllByTypeAndDate(alarmTypes, timestampFrom, timestampTo, pageable);
    }

    @Log(message = "Creating new custom log alarm.")
    @PostMapping("/log-alarm")
    public CustomLogAlarmDTO createCustomLogAlarm(HttpServletRequest request, @RequestBody @Valid CustomLogAlarmDTO customLogAlarmDTO) {
        var alarm = alarmService.createCustomLogALarm(customLogAlarmDTO);
        return new CustomLogAlarmDTO(alarm.getId(), alarm.getType(), alarm.getSubstring());
    }

    @Log(message = "Fetching all custom log alarms.")
    @GetMapping("/log-alarm")
    public List<CustomLogAlarmDTO> getAllCustomAlarms(HttpServletRequest request) {
        return alarmService.getAllCustomLogAlarms().stream().map((a) -> new CustomLogAlarmDTO(a.getId(), a.getType(), a.getSubstring())).collect(Collectors.toList());
    }

    @Log(message = "Deleting custom log alarms.")
    @DeleteMapping("/log-alarm/{id}")
    public void deleteCustomLogAlarm(HttpServletRequest request, @PathVariable Integer id) {
        alarmService.deleteCustomLogAlarm(id);
    }
}
