package movieApi.movies.dto.response;

import movieApi.movies.entity.Review;

import java.util.List;

public record PrivateUserDTO(
        String imdbId,
        String firstName,
        String lastName,
        String username,
        String password,
        String email,
        List<Review> userReviews
) {
}
