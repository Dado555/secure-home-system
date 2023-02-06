package rs.securehome.house.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.securehome.house.annotation.HasPrivilege;
import rs.securehome.house.model.Alarm;
import rs.securehome.house.service.AlarmService;
import rs.securehome.house.util.HttpRequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/api/alarms")
@Slf4j
public class AlarmController {

    private final AlarmService alarmService;
    private final HttpRequestUtil httpRequestUtil;

    public AlarmController(AlarmService alarmService, HttpRequestUtil httpRequestUtil) {
        this.alarmService = alarmService;
        this.httpRequestUtil = httpRequestUtil;
    }

    @HasPrivilege(privilege = "READ_ALARMS")
    @GetMapping
    public List<Alarm> getAllForUser(HttpServletRequest request) {
        return this.alarmService.getAlarmsForUser(httpRequestUtil.getJwtFromRequestHeader(request), httpRequestUtil.getCookieValue(request.getCookies(), "jwt-security"));
    }
}
