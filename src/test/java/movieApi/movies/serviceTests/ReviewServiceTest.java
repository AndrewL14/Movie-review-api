package movieApi.movies.serviceTests;

import com.mongodb.client.result.UpdateResult;
import movieApi.movies.dto.response.ReviewDTO;
import movieApi.movies.entity.Movie;
import movieApi.movies.entity.Review;
import movieApi.movies.entity.User;
import movieApi.movies.repository.MovieRepository;
import movieApi.movies.repository.ReviewRepository;
import movieApi.movies.repository.UserRepository;
import movieApi.movies.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
/*
Currently, incomplete Unit test -> issue: unsuccessful mocking of mongoTemplate
leading to null pointer when calling mongoTemplate through unit test.
Note: when testing the review service manually through PostMan all works fine. Just
need to find a way to mock mongoTemplate correctly.
 */
public class ReviewServiceTest {

    private static final String BODY = "A cool review", MOVIE_IMDBID = "tt349485", USER_IMDBID = "tt847492" ;

    @InjectMocks
    private ReviewService reviewService;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MongoTemplate mongoTemplate;

    @BeforeEach
    public void setup() {
        initMocks(this);

        MockitoAnnotations.openMocks(this);

        // Stubbing findMovieByImdbId behavior
        when(movieRepository.findMovieByImdbId(anyString()))
                .thenReturn(Optional.of(createTestMovie()));

        // Stubbing findUserByImdbId behavior
        when(userRepository.findUserByImdbId(anyString()))
                .thenReturn(Optional.of(createTestUser()));

        // Stubbing save behavior for repository.insert
        when(movieRepository.insert(any(Movie.class))).thenAnswer(invocation -> {
            return invocation.<Movie>getArgument(0);
        });
        // Stubbing save behavior for repository.insert
        when(reviewRepository.insert(any(Review.class))).thenAnswer(invocation -> {
            return invocation.<Review>getArgument(0);
        });

        // Stubbing update behavior for mongoTemplate.update
//        when(mongoTemplate.update(Movie.class).matching(Criteria.where(MOVIE_IMDBID))
//                .apply(new Update().push(anyString()).value(Review.class)).first()
//        ).thenAnswer(invocationOnMock -> {
//            Movie movie = invocationOnMock.getArgument(0);
//            List<Review> reviewList = movie.getReviewIds();
//            reviewList.add(new Review(BODY));
//            movie.setReviewIds(reviewList);
//            return movie;
//        });



//        when(mongoTemplate.update(Movie.class).matching(Criteria.where(MOVIE_IMDBID)))
//                .thenAnswer(invocation -> {
//                    Movie movie = createTestMovie();
//                    List<Review> reviewList = movie.getReviewIds();
//                    reviewList.add(new Review(BODY));
//                    movie.setReviewIds(reviewList);
//                    return movie;
//                });

        when(mongoTemplate.update(User.class).matching(Criteria.where(USER_IMDBID)))
                .thenAnswer(invocation -> {
                    User user  = createTestUser();
                    List<Review> reviewList = user.getUserReviews();
                    reviewList.add(new Review(BODY));
                    user.setUserReviews(reviewList);
                    return user;
                });
    }


    @Test
    public void createReview_validRequest_ReviewDTO() {
        // GIVEN

        // WHEN
        ReviewDTO resposne = reviewService.createReview(BODY, MOVIE_IMDBID, USER_IMDBID);

        // THEN
        assertValidReview(resposne);
    }



    private Movie createTestMovie() {
        return Movie.builder()
                .imdbId("tt349485")
                .reviewIds(new ArrayList<>())
                .build();
    }

    private User createTestUser() {
        return User.builder()
                .imdbId("tt847492")
                .userReviews(new ArrayList<>())
                .build();
    }

    private void assertValidReview(ReviewDTO response) {

        // Check review entity for valid reviewDTO
        assertNotNull(response);
        assertNotNull(response.body());
        assertEquals("A cool review", response.body(), "expected 'A cool review' but got " + response.body());

        // Check movie entity for non-empty reviewIds
        Optional<Movie> movieOptional = movieRepository.findMovieByImdbId(MOVIE_IMDBID);
        assertTrue(movieOptional.isPresent(), "Movie not found");
        assertTrue(!movieOptional.get().getReviewIds().isEmpty(), "Movie reviewIds list should not be empty");

        // Check user entity for non-empty userReviews
        Optional<User> userOptional = userRepository.findUserByImdbId(USER_IMDBID);
        assertTrue(userOptional.isPresent(), "User not found");
        assertTrue(!userOptional.get().getUserReviews().isEmpty(), "User userReviews list should not be empty");
    }
}
