package movieApi.movies.dto.request;

public record CreateUserRequest(
        String firstName,
        String lastName,
        String username,
        String password,
        String email
) {
}
