package movieApi.movies.converterTest;

import movieApi.movies.converter.Converter;
import movieApi.movies.dto.response.MovieDTO;
import movieApi.movies.dto.response.PrivateUserDTO;
import movieApi.movies.dto.response.PublicUserDTO;
import movieApi.movies.dto.response.ReviewDTO;
import movieApi.movies.entity.Movie;
import movieApi.movies.entity.Review;
import movieApi.movies.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.ArrayList;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.junit.jupiter.api.Assertions.*;

public class ConverterTest {
    private static final Movie MOVIE_TO_USE = Movie.builder()
            .imdbId("tt87394")
            .title("generic title")
            .releaseDate("2222-12-12")
            .trailerLink("https://trailerjosh")
            .poster("https://Aposter")
            .genres(new ArrayList<>())
            .reviewIds(new ArrayList<>())
            .build();
    private static final Review REVIEW_TO_USE = new Review("generic review");
    private static final User USER_TO_USE = User.builder()
            .imdbId("tt893453")
            .firstName("Jhon")
            .lastName("Doe")
            .username("JhonDoe69")
            .password("I Am Him")
            .email("genericEmail@yahoo.com")
            .userReviews(new ArrayList<>())
            .build();

    @InjectMocks
    Converter converter;

    @BeforeEach
    public void setup() {
        initMocks(this);
    }

    @Test
    public void createMovieDTO_validMovie_MovieDTO() {
        // GIVEN

        // WHEN
        MovieDTO response = Converter.MovieToDTO(MOVIE_TO_USE);

        // THEN
        assertNotNull(response);
        assertEquals("tt87394", response.imdbId(), "expected tt87394 but got: " + response.imdbId());
        assertEquals("generic title", response.title(), "expected generic title but got: " + response.title());
        assertEquals("2222-12-12", response.releaseDate(), "expected 2222-12-12 but got: " + response.releaseDate());
        assertEquals("https://trailerjosh", response.trailerLink(), "expected https://trailerjosh but got: " + response.trailerLink());
        assertNotNull(response.genres());
        assertNotNull(response.reviewIds());
    }

    @Test
    public void createReviewDTO_validReview_ReviewDTO() {
        // GIVEN

        // WHEN
        ReviewDTO response = Converter.reviewToDTO(REVIEW_TO_USE);

        // THEN
        assertNotNull(response);
        assertEquals("generic review", response.body(), "expected generic review but got: " + response.body());
    }

    @Test
    public void CreatePublicUserDTO_ValidUser_publicUserDTO() {
        // GIVEN

        // WHEN
        PublicUserDTO response = Converter.userToPublicDTO(USER_TO_USE);

        // THEN
        assertNotNull(response);
        assertEquals("tt893453", response.imdbId(), "expected tt893453 but got: " + response.imdbId());
        assertEquals("Jhon", response.firstName(), "expected first name to be Jhon but got: " + response.firstName());
        assertEquals("Doe", response.lastName(), "expected last name to be Doe but got: " + response.lastName());
        assertEquals("JhonDoe69", response.username(), "expected username to be JhonDoe69 but got: " + response.username());
        assertNotNull(response.userReviews());
    }

    @Test
    public void createPrivateUserDTO_validUser_privateUserDTO() {
        // GIVEN

        // WHEN
        PrivateUserDTO response = Converter.userToPrivateDTO(USER_TO_USE);

        // WHEN
        assertNotNull(response);
        assertEquals("tt893453", response.imdbId(), "expected tt893453 but got: " + response.imdbId());
        assertEquals("Jhon", response.firstName(), "expected first name to be Jhon but got: " + response.firstName());
        assertEquals("Doe", response.lastName(), "expected last name to be Doe but got: " + response.lastName());
        assertEquals("JhonDoe69", response.username(), "expected username to be JhonDoe69 but got: " + response.username());
        assertEquals("I Am Him", response.password(), "expected password to be I Am Him but got: " + response.password());
        assertEquals("genericEmail@yahoo.com", response.email(), "expected email to be genericEmail@yahoo.com but got: " + response.email());
        assertNotNull(response.userReviews());
    }
}
