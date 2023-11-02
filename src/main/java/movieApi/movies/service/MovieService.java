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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
/*
Currently, multi-threading is slower than normal processing, because database size for movies is only 11 objects.
Yet as more movies are added the multi-threading version will be the more optimal choice.
 */
@Service
public class MovieService {
    @Autowired
    private MovieRepository repository;
    @Autowired
    private RequestValidator validator;
    private ThreadPoolExecutor executor;

    /**
     * Initializes Threads Based on the current number of objects in repository
     */
    @PostConstruct
    public void initializeExecutor() {
        int initialThreadPoolSize = (int) calculateInitialThreadPoolSize();
        int maxThreadPoolSize = (int) calculateMaxThreadPoolSize();

        executor = new ThreadPoolExecutor(
                initialThreadPoolSize,
                maxThreadPoolSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    /**
     * Calculates the minimal amount of threads needed.
     * @return Number of initial threads.
     */
    private long calculateInitialThreadPoolSize() {
        long numObjectsInDatabase = repository.count();
        return Math.min(numObjectsInDatabase, Runtime.getRuntime().availableProcessors());
    }

    /**
     * Calculates the max Threads needed.
     * @return Max number of threads.
     */
    private long calculateMaxThreadPoolSize() {
        long numObjectsInDatabase = repository.count();
        return Math.max(numObjectsInDatabase, Runtime.getRuntime().availableProcessors() * 2L);
    }

    /**
     * Fetches all Movies in movie repository using threads.
     * @return A list of MovieDTOs
     * @throws InterruptedException If threading fails
     */
    public List<MovieDTO> findAllMoviesConcurrently() throws InterruptedException {
        List<Future<MovieDTO>> futures = new ArrayList<>();
        List<Movie> movies = repository.findAll();
        List<MovieDTO> response = new ArrayList<>();

        for (Movie movie : movies) {
            Future<MovieDTO> future = executor.submit(() -> Converter.MovieToDTO(movie));
            futures.add(future);
        }

        for (Future<MovieDTO> dtoFuture : futures) {
            try {
                MovieDTO movieDTO = dtoFuture.get();
                response.add(movieDTO);
            } catch (InterruptedException | ExecutionException e) {
                throw new InterruptedException("Error 404: internal server error");
            }
        }
        return response;
    }

    /**
     * Finds Specific movie with matching imdbId.
     * when movie is returned, object is stored in a cache
     * @param imdbId Unique Range Key for Movies
     * @return an Optional MovieDTO
     */
    @Cacheable(value = "moviesCache", key = "#imdbId")
    public Optional<MovieDTO> findMovieByImdbId(String imdbId) {
        return repository.findMovieByImdbId(imdbId)
                .map(Converter::MovieToDTO);
    }

    /**
     * Using the request param creates a new movie object then stores it into the Movie
     * Repo.
     * @param request
     * @return A new MovieDTO
     * @throws InvalidHTTPRequestException Will throw if the request format is Invalid
     */
    @CacheEvict(value = "moviesCache", allEntries = true)
    public MovieDTO uploadNewMovie(CreateMovieRequest request) throws InvalidHTTPRequestException {
        validator.validMovieRequest(request);

        String imdbId = CustomIdMaker.generateRandomNumberIdentifier();
        boolean isAvailable = false;
        // Find better way to determine weather or not the id is already in use
        while (!isAvailable) {
            if (!findMovieByImdbId(imdbId).get().title().isEmpty()) {
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

    /**
     * Stops all threads
     */
    @PreDestroy
    public void shutdownExecutorService() {
        executor.shutdown();
    }
}
