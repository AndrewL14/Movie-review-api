package movieApi.movies.dto.request;

public record UpdateUserRequest(
        String imdbId,
        String firstName,
        String lastName,
        String username,
        String password,
        String email
) {
}
