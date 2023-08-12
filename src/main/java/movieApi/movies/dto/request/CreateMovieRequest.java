package movieApi.movies.dto;

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
