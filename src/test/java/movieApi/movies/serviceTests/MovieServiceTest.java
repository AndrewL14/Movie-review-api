package movieApi.movies.serviceTests;

import movieApi.movies.dto.request.CreateMovieRequest;
import movieApi.movies.dto.response.MovieDTO;
import movieApi.movies.entity.Movie;
import movieApi.movies.entity.Review;
import movieApi.movies.exception.InvalidHTTPRequestException;
import movieApi.movies.repository.MovieRepository;
import movieApi.movies.service.MovieService;
import movieApi.movies.utils.CustomIdMaker;
import movieApi.movies.utils.RequestValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
public class MovieServiceTest {

    private static final  String IMDB_ID = "tt" + CustomIdMaker.generateRandomNumberIdentifier(), TITLE = "BANDIT", RELEASE_DATE = "2022-10-19",
            TRAILER_LINK = "http://examplelink", POSTER = "EXAMPLE_POSTER";
    private static final List<String> GENRES = new ArrayList<String>(), BACKDROP = new ArrayList<>();
    private static final List<Review> REVIEW_IDS = new ArrayList<>();

    @InjectMocks
    private MovieService service;

    @Mock
    private MovieRepository repository;
    @Mock
    private RequestValidator validator;

    @BeforeEach
    public void setup() {
        // Initialize your mocks and stub repository behavior here
        MockitoAnnotations.openMocks(this);

        // Stubbing findMovieByImdbId behavior
        when(repository.findMovieByImdbId(anyString()))
                .thenReturn(Optional.of(createTestMovie()));

        // Stubbing findAll behavior
        when(repository.findAll())
                .thenReturn(Collections.singletonList(createTestMovie()));

        // Stubbing save behavior for repository.insert
        when(repository.insert(any(Movie.class))).thenAnswer(invocation -> {
            Movie movie = invocation.getArgument(0);
            return movie; // Returning the same movie for simplicity
        });

        service.initializeExecutor();
    }

    @Test
    public void findAllMovies_ListOfMovieDTO() throws InterruptedException {
        // GIVEN

        // WHEN
        List<MovieDTO> response = service.findAllMoviesConcurrently();

        // THEN
        assertNotNull(response);
        assertTrue(response.size() > 0, "List Is Empty!");
    }

    @Test
    public void findMovie_validImdbId_MovieDTO() {
        // GIVEN
        final String validImdbId = "tt10298840";

        // WHEN
        Optional<MovieDTO> response = service.findMovieByImdbId(validImdbId);

        // THEN
        assertFalse(response.isEmpty(), "expected MovieDTO but got nothing");
        assertMovieDTO(response.get());
    }

    @Test
    public void uploadNewMovie_validMovie_MovieDTO() throws InvalidHTTPRequestException {
        // GIVEN
        CreateMovieRequest request = CreateMovieRequest.builder()
                .title("BANDIT")
                .releaseDate("2022-10-19")
                .trailerLink("http://examplelink")
                .genres(List.of("Action"))
                .poster("EXAMPLE_POSTER")
                .backDrop(List.of("example.backdrop"))
                .build();

        // WHEN
        MovieDTO response = service.uploadNewMovie(request);

        // THEN
        assertMovieDTO(response);
    }

    private Movie createTestMovie() {
        GENRES.add("Action");
        BACKDROP.add("example.backdrop");
        return Movie.builder()
                .imdbId(IMDB_ID)
                .title(TITLE)
                .releaseDate(RELEASE_DATE)
                .trailerLink(TRAILER_LINK)
                .poster(POSTER)
                .genres(GENRES)
                .backdrops(BACKDROP)
                .reviewIds(REVIEW_IDS)
                .build();
    }


    private void assertMovieDTO(MovieDTO response) {
        assertNotNull(response.imdbId(), "expected imdbId but got nothing");
        assertEquals("BANDIT", response.title(), "expected title to be BANDIT but got " + response.title());
        assertEquals("2022-10-19", response.releaseDate(), "expected date to be 2022-10-19 but got " + response.releaseDate());
        assertEquals("http://examplelink", response.trailerLink(), "expected trailer to have valid link but got " + response.trailerLink());
        assertTrue(response.genres().size() > 0, "expected non empty list ");
        assertEquals("Action", response.genres().get(0), "expected list to have Action but got " + response.genres().get(0));
        assertEquals("EXAMPLE_POSTER", response.poster(), "expected EXAMPLE_POSTER but got " + response.poster());
        assertEquals("example.backdrop", response.backdrop().get(0), "expected example.backdrop but got "  + response.backdrop().get(0));
        assertNotNull(response.reviewIds());
    }
}
