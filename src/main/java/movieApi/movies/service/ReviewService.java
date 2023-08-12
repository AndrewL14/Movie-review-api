package movieApi.movies.service;

import movieApi.movies.converter.Converter;
import movieApi.movies.dto.response.ReviewDTO;
import movieApi.movies.entity.Movie;
import movieApi.movies.entity.Review;
import movieApi.movies.entity.User;
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
        Review review = reviewRepository.insert(new Review(reviewBody));

        mongoTemplate.update(Movie.class)
                .matching(Criteria.where("imdbId").is(imdbId))
                .apply(new Update().push("reviewIds").value(review))
                .first();

        mongoTemplate.update(User.class)
                .matching(Criteria.where("imdbId").is(userImdbId))
                .apply(new Update().push("userReviews").value(review))
                .first();

        return Converter.reviewToDTO(review);
    }
}
