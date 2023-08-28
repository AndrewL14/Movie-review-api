package movieApi.movies.repository;

import movieApi.movies.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {
    Optional<User> findUserByImdbId(String imdbId);
    Optional<User> findByUsername(String username);
}
