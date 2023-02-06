package rs.securehome.admin.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rs.securehome.admin.annotation.Log;
import rs.securehome.admin.dto.FrequencyRuleDTO;
import rs.securehome.admin.dto.TemperatureRuleDTO;
import rs.securehome.admin.model.rules.FrequencyRule;
import rs.securehome.admin.model.rules.TemperatureRule;
import rs.securehome.admin.service.RuleService;
import rs.securehome.common.dto.DeviceMessageAuthenticatedDTO;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/rules")
@Slf4j
public class RuleController {

    private final RuleService ruleService;

    @Autowired
    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @PostMapping(value = "/alarm-should-be-fired")
    public boolean alarmShouldBeFired(HttpServletRequest request, @RequestBody @Valid DeviceMessageAuthenticatedDTO deviceMessageAuthenticatedDTO) {
        return this.ruleService.alarmShouldBeFired(deviceMessageAuthenticatedDTO);
    }

    @Log(message = "Creating a temperature rule.")
    @PostMapping(value = "/temperature-rules")
    public TemperatureRule createTemperatureRule(HttpServletRequest request, @Valid @RequestBody TemperatureRuleDTO temperatureRuleDTO) {
        return this.ruleService.createTemperatureRule(temperatureRuleDTO);
    }

    @Log(message = "Fethcing all temperature rules.")
    @GetMapping(value = "/temperature-rules")
    public List<TemperatureRule> getAllTemperatureRules(HttpServletRequest request) {
        return this.ruleService.getAllTemperatureRules();
    }

    @Log(message = "Creating a frequency rule.")
    @PostMapping(value = "/frequency-rules")
    public FrequencyRule createFrequencyRule(HttpServletRequest request, @Valid @RequestBody FrequencyRuleDTO frequencyRuleDTO) {
        return this.ruleService.createFrequencyRule(frequencyRuleDTO);
    }

    @Log(message = "Fetching all frequency rules.")
    @GetMapping(value = "/frequency-rules")
    public List<FrequencyRule> getAllFrequencyRules(HttpServletRequest request) {
        return this.ruleService.getAllFrequencyRules();
    }
}
