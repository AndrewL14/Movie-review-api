package movieApi.movies.service;

import movieApi.movies.converter.Converter;
import movieApi.movies.dto.CreateMovieRequest;
import movieApi.movies.dto.MovieDTO;
import movieApi.movies.entity.Movie;
import movieApi.movies.exception.MovieNotFoundException;
import movieApi.movies.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class MovieService {
    @Autowired
    private MovieRepository repository;

    public List<MovieDTO> findAllMovies() {
        return repository.findAll().stream()
                .map(Converter::MovieToDTO)
                .collect(Collectors.toList());
    }
    public MovieDTO findMovieByImdbId(String imdbId) {
        Movie movie = repository.findMovieByImdbId(imdbId)
                .orElseThrow(MovieNotFoundException::new);
        return Converter.MovieToDTO(movie);
    }

    public MovieDTO uploadNewMovie(CreateMovieRequest request) {
        Movie movie = repository.insert(new Movie(
                String.format("tt%s", generateRandomNumberIdentifier()),
                request.title(),
                request.releaseDate(),
                request.trailerLink(),
                request.poster(),
                request.genres(),
                request.backDrop(),
                new ArrayList<>()
        ));

        return Converter.MovieToDTO(movie);
    }

    // Define the pool of characters for generating numbers in the identifier
    private static final String NUMBER_POOL = "0123456789";

    // Method to generate a random number identifier
    private static String generateRandomNumberIdentifier() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(7);

        for (int i = 0; i < 7; i++) {
            int randomIndex = random.nextInt(NUMBER_POOL.length());
            char randomChar = NUMBER_POOL.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }

        return stringBuilder.toString();
    }
}
