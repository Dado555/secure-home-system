package rs.securehome.common.valueobjects;


import lombok.Getter;
import rs.securehome.common.enums.LogType;

import java.time.LocalDateTime;

@Getter
public abstract class ApplicationLog {

    protected String message;

    protected LocalDateTime timestamp;

    protected String ipAddress;

    protected LogType logType;

    protected String component;

    protected ApplicationLog() {
    }

    protected ApplicationLog(String message, LocalDateTime timestamp, String ipAddress, LogType logType, String component) {
        this.message = message;
        this.timestamp = timestamp;
        this.ipAddress = ipAddress;
        this.logType = logType;
        this.component = component;
    }



    public ApplicationLog setMessage(String message) {
        this.message = message;
        return this;
    }

    public ApplicationLog setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public ApplicationLog setCurrentTimestamp() {
        this.timestamp = LocalDateTime.now();
        return this;
    }

    public ApplicationLog setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public ApplicationLog setLogType(LogType logType) {
        this.logType = logType;
        return this;
    }

    public ApplicationLog setComponent(String component) {
        this.component = component;
        return this;
    }
}
