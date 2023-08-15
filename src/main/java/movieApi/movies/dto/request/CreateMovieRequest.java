package movieApi.movies.dto.request;

import java.util.List;

public record CreateMovieRequest(
        String title,
        String releaseDate,
        String trailerLink,
        String poster,
        List<String> genres,
        List<String> backDrop
) {
}
