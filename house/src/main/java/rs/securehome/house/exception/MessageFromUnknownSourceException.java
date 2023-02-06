package rs.securehome.house.exception;

public class MessageFromUnknownSourceException extends RuntimeException {
    public MessageFromUnknownSourceException(String message) {
        super(message);
    }
}
