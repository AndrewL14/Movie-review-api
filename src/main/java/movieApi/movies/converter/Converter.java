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

    public static ReviewDTO reviewToDTO(Review review) {
        return new ReviewDTO(review.getBody());
    }

    public static PublicUserDTO userToPublicDTO(User user) {
        return new PublicUserDTO(
                user.getImdbId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getUserReviews()
        );
    }

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
