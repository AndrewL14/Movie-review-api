package movieApi.movies.dto.response;

import movieApi.movies.entity.Review;

import java.util.List;

public record PublicUserDTO(
        String imdbId,
        String firstName,
        String lastName,
        String username,
        List<Review> userReviews
) {
}
