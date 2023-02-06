package rs.securehome.admin.model.events;

public enum AlarmType {
    LOG_ERROR,
    LOGIN_FAIL,
    IP_MALICIOUS,
    CUSTOM_LOG_RULE,
    MANY_REQUESTS;

    AlarmType() {}
}
