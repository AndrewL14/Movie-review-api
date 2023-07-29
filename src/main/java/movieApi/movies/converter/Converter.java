package movieApi.movies.converter;

import movieApi.movies.dto.MovieDTO;
import movieApi.movies.dto.ReviewDTO;
import movieApi.movies.entity.Movie;
import movieApi.movies.entity.Review;

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
                .map(Converter::reviewDTO).collect(Collectors.toList()));
    }

    public static ReviewDTO reviewDTO(Review review) {
        return new ReviewDTO(review.getBody());
    }
}
