package movieApi.movies.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record CreateMovieRequest(
        String title,
        String releaseDate,
        String trailerLink,
        String poster,
        List<String> genres,
        List<String> backDrop
) {
}
