package movieApi.movies.exception;

public class UserNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 5276571551464689700L;

    public UserNotFoundException() {
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message , Throwable cause) {
        super(message , cause);
    }

    public UserNotFoundException(Throwable cause) {
        super(cause);
    }
}
