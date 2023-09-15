package movieApi.movies.service;

import movieApi.movies.converter.Converter;
import movieApi.movies.dto.request.CreateMovieRequest;
import movieApi.movies.dto.response.MovieDTO;
import movieApi.movies.entity.Movie;
import movieApi.movies.exception.InvalidHTTPRequestException;
import movieApi.movies.repository.MovieRepository;
import movieApi.movies.utils.CustomIdMaker;
import movieApi.movies.utils.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieService {
    @Autowired
    private MovieRepository repository;
    private RequestValidator validator;

    public List<MovieDTO> findAllMovies() {
        return repository.findAll().stream()
                .map(Converter::MovieToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "moviesCache", key = "#imdbId")
    public Optional<MovieDTO> findMovieByImdbId(String imdbId) {
        return repository.findMovieByImdbId(imdbId)
                .map(Converter::MovieToDTO);
    }

    @CacheEvict(value = "moviesCache", allEntries = true)
    public MovieDTO uploadNewMovie(CreateMovieRequest request) throws InvalidHTTPRequestException {
        validator.validMovieRequest(request);

        String imdbId = CustomIdMaker.generateRandomNumberIdentifier();
        boolean isAvailable = false;
        // Find better way to determine weather or not the id is already in use
        while (!isAvailable) {
            if (findMovieByImdbId(imdbId).isEmpty()) {
                isAvailable = true;
            } else {
                imdbId = CustomIdMaker.generateRandomNumberIdentifier();
            }
        }

        Movie movie = repository.insert(new Movie(
                imdbId,
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
}
