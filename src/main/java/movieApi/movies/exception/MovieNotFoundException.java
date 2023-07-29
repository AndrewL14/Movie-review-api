package movieApi.movies.exception;

import java.io.Serial;

public class MovieNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -3328147430759310553L;

    public MovieNotFoundException() {
    }

    public MovieNotFoundException(String message) {
        super(message);
    }

    public MovieNotFoundException(String message , Throwable cause) {
        super(message , cause);
    }

    public MovieNotFoundException(Throwable cause) {
        super(cause);
    }
}
