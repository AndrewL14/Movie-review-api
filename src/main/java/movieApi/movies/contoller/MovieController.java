package movieApi.movies.contoller;

import movieApi.movies.dto.CreateMovieRequest;
import movieApi.movies.dto.MovieDTO;
import movieApi.movies.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/movies")
public class MovieController {
    @Autowired
    private MovieService service;

    @GetMapping
    public ResponseEntity<List<MovieDTO>> allMovies() {
        return new ResponseEntity<List<MovieDTO>>(service.findAllMovies(), HttpStatus.OK);
    }

    @GetMapping("/{imdbId}")
    public ResponseEntity<MovieDTO> getSingleMovie(@PathVariable String imdbId) {
        return new ResponseEntity<MovieDTO>(service.findMovieByImdbId(imdbId), HttpStatus.OK);
    }

    @PutMapping(value = "/upload")
    public ResponseEntity<MovieDTO> uploadNewMovie(@Validated @RequestBody CreateMovieRequest request) {
        return new ResponseEntity<>(service.uploadNewMovie(request) , HttpStatus.CREATED);
    }
}
