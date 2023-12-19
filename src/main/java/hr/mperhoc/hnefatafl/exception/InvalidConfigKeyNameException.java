package hr.mperhoc.hnefatafl.exception;

public class InvalidConfigKeyNameException extends RuntimeException {
    public InvalidConfigKeyNameException() {
    }

    public InvalidConfigKeyNameException(String message) {
        super(message);
    }

    public InvalidConfigKeyNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidConfigKeyNameException(Throwable cause) {
        super(cause);
    }

    public InvalidConfigKeyNameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
