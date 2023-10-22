package movieApi.movies.converter;

import movieApi.movies.dto.response.MovieDTO;
import movieApi.movies.dto.response.PrivateUserDTO;
import movieApi.movies.dto.response.ReviewDTO;
import movieApi.movies.dto.response.PublicUserDTO;
import movieApi.movies.entity.Movie;
import movieApi.movies.entity.Review;
import movieApi.movies.entity.User;

import java.util.stream.Collectors;

public class Converter {

    private Converter() {
    }

    /**
     * Converts a database Movie object into a DTO for client side use.
     * @param movie Raw Database entity.
     * @return DTO of Movie object
     */
    public static MovieDTO MovieToDTO(Movie movie) {
        return new MovieDTO(
                movie.getImdbId(),
                movie.getTitle(),
                movie.getReleaseDate(),
                movie.getTrailerLink(),
                movie.getPoster(),
                movie.getGenres(),
                movie.getBackdrops(),
                movie.getReviewIds().stream()
                .map(Converter::reviewToDTO).collect(Collectors.toList()));
    }

    /**
     * Converts a database Review object into a DTO for client side use.
     * @param review Raw Database entity.
     * @return DTO of review object
     */
    public static ReviewDTO reviewToDTO(Review review) {
        return new ReviewDTO(review.getBody());
    }

    /**
     * Converts a database User object into a DTO for client side use.
     * Will convert into a public version, for public information available to all users.
     * @param user Raw Database entity.
     * @return Public DTO of user object
     */
    public static PublicUserDTO userToPublicDTO(User user) {
        return new PublicUserDTO(
                user.getImdbId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getUserReviews()
        );
    }

    /**
     * Converts a database User object into a DTO for client side use.
     * Will convert into a private version, for the owner of the user only. (only for owners eyes).
     * @param user Raw Database entity.
     * @return private DTO of user object
     */
    public static PrivateUserDTO userToPrivateDTO(User user) {
        return new PrivateUserDTO(
                user.getImdbId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getUserReviews()
        );
    }
}
