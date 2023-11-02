package movieApi.movies.contoller;

import movieApi.movies.dto.request.CreateMovieRequest;
import movieApi.movies.dto.response.MovieDTO;
import movieApi.movies.exception.InvalidHTTPRequestException;
import movieApi.movies.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/movies")
public class MovieController {
    @Autowired
    private MovieService service;

    @GetMapping
    public ResponseEntity<List<MovieDTO>> allMovies() throws InterruptedException {
        return new ResponseEntity<List<MovieDTO>>(service.findAllMoviesConcurrently(), HttpStatus.OK);
    }

    @GetMapping("/{imdbId}")
    public ResponseEntity<Optional<MovieDTO>> getSingleMovie(@PathVariable String imdbId) {
        return new ResponseEntity<Optional<MovieDTO>>(service.findMovieByImdbId(imdbId), HttpStatus.OK);
    }

    @PutMapping("/upload")
    public ResponseEntity<MovieDTO> uploadNewMovie(@Validated @RequestBody CreateMovieRequest request) throws InvalidHTTPRequestException {
        return new ResponseEntity<>(service.uploadNewMovie(request) , HttpStatus.CREATED);
    }
}
