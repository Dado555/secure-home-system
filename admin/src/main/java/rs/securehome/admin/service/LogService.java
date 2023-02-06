package rs.securehome.admin.service;


import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rs.securehome.admin.exception.InvalidRegularExpressionException;
import rs.securehome.admin.model.events.*;
import rs.securehome.admin.model.valueobjects.Log;
import rs.securehome.admin.repository.AlarmRepository;
import rs.securehome.admin.repository.CustomLogAlarmRepository;
import rs.securehome.admin.repository.mongo.LogRepository;
import rs.securehome.common.enums.LogType;
import rs.securehome.common.valueobjects.ApplicationLog;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Service
@Slf4j
public class LogService {

    private final LogRepository logRepository;
    private final KieSession kieSession;
    private final AlarmRepository alarmRepository;
    private final List<String> maliciousIps;
    private final CustomLogAlarmRepository customLogAlarmRepository;

    @Autowired
    public LogService(LogRepository logRepository, KieSession kieSession,
                      AlarmRepository alarmRepository, List<String> maliciousIps,
                      CustomLogAlarmRepository customLogAlarmRepository) {
        this.logRepository = logRepository;
        this.kieSession = kieSession;
        this.alarmRepository = alarmRepository;
        this.maliciousIps = maliciousIps;
        this.customLogAlarmRepository = customLogAlarmRepository;
    }


    public Log info(String ipAddress, String message, String component, String username, List<String> methodParams) {
        return this.saveLog(ipAddress, message, component, LogType.INFO, username, methodParams);

    }

    public Log error(String ipAddress, String message, String component, String username, List<String> methodParams) {
        // Pojava loga čiji tip je ERROR
        // CEP - alarm
        LogError le = new LogError(ipAddress, message, component, username);
        kieSession.insert(le);
        kieSession.fireAllRules();

        alarmRepository.save(new Alarm(AlarmType.LOG_ERROR, le, LocalDateTime.now()));

        return this.saveLog(ipAddress, message, component, LogType.ERROR, username, methodParams);

    }

    public Log debug(String ipAddress, String message, String component, String username, List<String> methodParams) {
        return this.saveLog(ipAddress, message, component, LogType.DEBUG, username, methodParams);

    }

    public Log warn(String ipAddress, String message, String component, String username, List<String> methodParams) {
        return this.saveLog(ipAddress, message, component, LogType.WARN, username, methodParams);
    }

    private Log saveLog(String ipAddress, String message, String component, LogType type, String username, List<String> methodParams) {
        var log = new Log();
        log.setLogType(type).setCurrentTimestamp().setIpAddress(ipAddress).setMessage(message).setComponent(component);
        log.setMethodParams(methodParams);
        log.setUsername(username);
        //System.out.println(log);

        // Detekcija suviše učestalih zahtjeva
        ManyRequests mr = new ManyRequests(ipAddress, message, component, username, false);
        kieSession.insert(mr);
        kieSession.fireAllRules();

        // ako vrati alarm da upise u mongo..
        if(mr.getIsTooMuch())
            alarmRepository.save(new Alarm(AlarmType.MANY_REQUESTS, mr, LocalDateTime.now()));

        this.ipMalicious(ipAddress);

        var logRules = customLogAlarmRepository.findAll();
        for(var logRule : logRules) {
            if(logRule.getType().equals(log.getLogType()) && log.getMessage().toUpperCase().contains(logRule.getSubstring().toUpperCase())) {
                LogError le = new LogError(ipAddress, message, component, username);
                alarmRepository.save(new Alarm(AlarmType.CUSTOM_LOG_RULE, le, LocalDateTime.now()));
            }
        }

        return this.logRepository.save(log);
    }


    public Log writeLog(ApplicationLog applicationLog) {
        var log = (Log) applicationLog;
        return this.logRepository.save(log);
    }

    public Page<Log> readAllLogs(String query, LogType[] logTypes, String timestampFrom, String timestampTo, Pageable pageable) {
        try {
            if(query == null)
                query = "";
            else if (query.strip().equals("*") || query.strip().equals("null"))
                query = "";

            if(timestampFrom == null)
                timestampFrom = "1999-08-02T06:35:00Z";
            else if(timestampFrom.strip().equals("null"))
                timestampFrom = "1999-08-02T06:35:00Z";


            if(timestampTo == null)
                timestampTo = "2200-10-10T22:22:22Z";
            else if(timestampTo.strip().equals("null"))
                timestampTo = "2200-10-10T22:22:22Z";

            Pattern.compile(query);
            return   this.logRepository.findAllWithRegex(query, logTypes, timestampFrom, timestampTo, pageable);
        } catch (PatternSyntaxException exception) {
            throw new InvalidRegularExpressionException("Query is not a valid regular expression.");
        }
    }

    // Pojava loga u kom se nalazi IP adresa sa spiska malicioznih IP adresa
    public void ipMalicious(String ipAddress) {
        List<String> result = new ArrayList<>();
        if(!maliciousIps.isEmpty()) {
            result = maliciousIps;
        }
        IpMalicious ips = new IpMalicious(ipAddress, result, false);
        kieSession.insert(ips);
        kieSession.fireAllRules();

        if(ips.getIsMalicious())
            alarmRepository.save(new Alarm(AlarmType.IP_MALICIOUS, ips, LocalDateTime.now()));
    }
}
