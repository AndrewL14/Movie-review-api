package movieApi.movies.service;

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
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public ReviewDTO createReview(String reviewBody, String imdbId, String userImdbId) {
        if (!imdbId.startsWith("tt") || !userImdbId.startsWith("tt")) {
            throw new IllegalArgumentException("user or movie imdbId invalid format");
        }


        Review review = reviewRepository.insert(new Review(reviewBody));
        try {
            mongoTemplate.update(Movie.class)
                    .matching(Criteria.where("imdbId").is(imdbId))
                    .apply(new Update().push("reviewIds").value(review))
                    .first();
        } catch (Exception e) {
            throw new MovieNotFoundException("Invalid Movie imdbId: " + imdbId);
        }

        try {
            mongoTemplate.update(User.class)
                    .matching(Criteria.where("imdbId").is(userImdbId))
                    .apply(new Update().push("userReviews").value(review))
                    .first();
        } catch (Exception e) {
            throw new UserNotFoundException("Invalid User imdbId: " + userImdbId);
        }

        return Converter.reviewToDTO(review);
    }
}
