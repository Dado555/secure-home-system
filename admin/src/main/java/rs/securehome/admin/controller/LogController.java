package rs.securehome.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.securehome.admin.model.valueobjects.Log;
import rs.securehome.admin.service.LogService;
import rs.securehome.common.enums.LogType;

@RestController
@RequestMapping(value = "/api/logs")
@Slf4j
public class LogController {

    private final LogService logService;

    @Autowired
    public LogController(LogService logService) {
        this.logService = logService;
    }


    @GetMapping
    public Page<Log> readLogs(@RequestParam(name = "query") @Nullable String query,
                              @RequestParam(name = "logTypes") @Nullable LogType[] logTypes,
                              @RequestParam(name = "timestampFrom") @Nullable String timestampFrom,
                              @RequestParam(name = "timestampTo") @Nullable String timestampTo,
                              @PageableDefault Pageable pageable) {
        return this.logService.readAllLogs(query, logTypes, timestampFrom, timestampTo, pageable);
    }
}
