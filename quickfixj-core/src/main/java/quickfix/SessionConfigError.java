package quickfix;

/**
 * This exception is thrown when a session configuration error is detected.
 *
 * @author nikita.smirnov
 */
public class SessionConfigError extends ConfigError{

    public SessionConfigError() {
        super();
    }

    public SessionConfigError(String sessionField, String message) {
        super(message);
        this.sessionField = sessionField;
    }

    public SessionConfigError(String sessionField, Throwable cause) {
        super(cause);
        this.sessionField = sessionField;
    }

    public SessionConfigError(String sessionField, String string, Throwable e) {
        super(string, e);
        this.sessionField = sessionField;
    }

    public String getSessionField(){
        return sessionField;
    }

    private String sessionField;
}
