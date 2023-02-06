package rs.securehome.admin.model.valueobjects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import rs.securehome.common.enums.LogType;
import rs.securehome.common.util.AnsiCodes;
import rs.securehome.common.valueobjects.ApplicationLog;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "logs")
public class Log extends ApplicationLog {

    @Id
    private String id;

    private List<String> methodParams;

    private String username;


    public Log() {
        this.methodParams = new ArrayList<>();
    }

    public Log(String message, LocalDateTime timestamp, String ipAddress, LogType logType, String component) {
        super(message, timestamp, ipAddress, logType, component);
        this.methodParams = new ArrayList<>();
    }

    public void addMethodParam(String param) {
        this.methodParams.add(param);
    }

    public List<String> getMethodParams() {
        return methodParams;
    }

    public void setMethodParams(List<String> methodParams) {
        this.methodParams = methodParams;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        String typeColor;
        switch (logType) {
            case INFO:
                typeColor = AnsiCodes.GREEN;
                break;
            case ERROR:
                typeColor = AnsiCodes.RED;
                break;
            case DEBUG:
                typeColor = AnsiCodes.BLUE;
                break;
            case WARN:
                typeColor = AnsiCodes.YELLOW;
                break;
            default:
                typeColor = AnsiCodes.WHITE;
        }

        String type = typeColor + logType + AnsiCodes.RESET;
        String coloredComponent = AnsiCodes.CYAN + this.component + AnsiCodes.RESET;
        return String.format("%s  %s %s %s %s  :  %s", timestamp.toString().substring(0, timestamp.toString().length() - 6), type, ipAddress, username, coloredComponent, message);
    }


}
