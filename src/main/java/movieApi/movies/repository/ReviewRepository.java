package movieApi.movies.repository;

import movieApi.movies.entity.Review;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository(value = "reviews")
public interface ReviewRepository extends MongoRepository<Review, ObjectId> {
}
