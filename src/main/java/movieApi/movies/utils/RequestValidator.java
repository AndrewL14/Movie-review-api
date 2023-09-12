package movieApi.movies.utils;

import movieApi.movies.dto.request.CreateMovieRequest;
import movieApi.movies.dto.request.CreateUserRequest;
import movieApi.movies.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;

public class RequestValidator {
    @Autowired
    private MovieRepository repository;

    public static Pair<String, Boolean> MovieAlreadyExist(CreateMovieRequest request) {
        // check format
        // check if all values are present
        // check if movie doesn't exist in database
        //      checks movie name and release date
        return null;
    }

    public static Pair<String, Boolean> UserAlreadyExist(CreateUserRequest request) {
        return null;
    }


    private boolean validMovieRequest(CreateMovieRequest request) {
        return false;
    }

    private boolean validUserRequest(CreateUserRequest request) {
        return false;
    }
}
