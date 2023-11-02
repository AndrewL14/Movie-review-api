package movieApi.movies.service;

import com.mongodb.client.result.UpdateResult;
import movieApi.movies.converter.Converter;
import movieApi.movies.dto.response.ReviewDTO;
import movieApi.movies.entity.Movie;
import movieApi.movies.entity.Review;
import movieApi.movies.entity.User;
import movieApi.movies.exception.MovieNotFoundException;
import movieApi.movies.exception.UserNotFoundException;
import movieApi.movies.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Creates a new Review objects, saves it to database, returns DTO.
     * If movie is not found throws MovieNotFoundException, If User is not found throws
     * UserNotFoundException.
     * @param reviewBody A string containing the review text from the user.
     * @param imdbId A unique database identifier for finding the corresponding movie object.
     * @param userImdbId A unique database identifier for finding the corresponding user object.
     * @return A Review DTO object
     */
    public ReviewDTO createReview(String reviewBody, String imdbId, String userImdbId) {
        if (!imdbId.startsWith("tt") || !userImdbId.startsWith("tt")) {
            throw new IllegalArgumentException("user or movie imdbId invalid format");
        }


        Review review = reviewRepository.insert(new Review(reviewBody));
        try {
            mongoTemplate.updateFirst(
                    Query.query(Criteria.where("imdbId").is(imdbId)),
                    new Update().push("reviewIds").value(review),
                    Movie.class
            );
        } catch (Exception e) {
            throw new MovieNotFoundException("Invalid Movie imdbId: " + imdbId);
        }

        try {
            mongoTemplate.updateFirst(
                    Query.query(Criteria.where("imdbId").is(userImdbId)),
                    new Update().push("userReviews").value(review),
                    User.class
            );
        } catch (Exception e) {
            throw new UserNotFoundException("Invalid User imdbId: " + userImdbId);
        }

        return Converter.reviewToDTO(review);
    }
}
