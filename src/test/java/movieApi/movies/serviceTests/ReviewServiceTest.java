package movieApi.movies.serviceTests;

import com.mongodb.client.result.UpdateResult;
import movieApi.movies.dto.response.ReviewDTO;
import movieApi.movies.entity.Movie;
import movieApi.movies.entity.Review;
import movieApi.movies.entity.User;
import movieApi.movies.repository.ReviewRepository;
import movieApi.movies.service.ReviewService;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
public class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateReview() {
        // GIVEN
        Movie movie = Movie.builder().imdbId("tt123").build();
        User user = User.builder().imdbId("tt456").build();

        // WHEN
        when(reviewRepository.insert(any(Review.class))).thenReturn(new Review("Sample review"));
        when(mongoTemplate.updateFirst(any(), any(), eq(Movie.class)))
                .thenReturn(UpdateResult.acknowledged(1, 1L, null));
        when(mongoTemplate.updateFirst(any(), any(), eq(User.class)))
                .thenReturn(UpdateResult.acknowledged(1, 1L, null));
        ReviewDTO response = reviewService.createReview("Sample review text", "tt123", "tt456");

        // THEN
        verify(mongoTemplate, times(1)).updateFirst(
                any(), any(), eq(Movie.class));
        verify(mongoTemplate, times(1)).updateFirst(
                any(), any(), eq(User.class));
        assertNotNull(response, "expected response to NOT be null");
    }

}