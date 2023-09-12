package movieApi.movies.exception;

import javax.naming.directory.InvalidAttributesException;
import java.io.Serial;

public class InvalidHTTPRequestException extends InvalidAttributesException {

    @Serial
    private static final long serialVersionUID = 8210052284450171757L;

    public InvalidHTTPRequestException(String explanation) {
        super(explanation);
    }

    public InvalidHTTPRequestException() {
    }
}
