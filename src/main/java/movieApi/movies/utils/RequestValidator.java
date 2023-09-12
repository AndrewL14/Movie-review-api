package movieApi.movies.utils;

import movieApi.movies.dto.request.CreateMovieRequest;
import movieApi.movies.dto.request.CreateUserRequest;
import movieApi.movies.entity.Movie;
import movieApi.movies.entity.User;
import movieApi.movies.exception.InvalidHTTPRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestValidator {
    @Autowired
    private MongoTemplate template;
    private final Set<String> GENRES = new HashSet<>();
    {
        GENRES.add("ACTION");
        GENRES.add("ADVENTURE");
        GENRES.add("ANIMATION");
        GENRES.add("COMEDY");
        GENRES.add("CRIME");
        GENRES.add("DRAMA");
        GENRES.add("FANTASY");
        GENRES.add("HORROR");
        GENRES.add("MYSTERY");
        GENRES.add("ROMANCE");
        GENRES.add("SCIENCE FICTION");
        GENRES.add("THRILLER");
    }

    public void validMovieRequest(CreateMovieRequest request) throws InvalidHTTPRequestException {
        if (request == null) throw new InvalidHTTPRequestException("request is null");
        String title = request.title();
        String releaseDate = request.releaseDate();
        List<String> genres = request.genres();
        List<String> backDrops = request.backDrop();
        if (request.title().isEmpty() || request.releaseDate().isEmpty() || request.trailerLink().isEmpty()
               || request.poster().isEmpty() || genres.isEmpty() || backDrops.isEmpty()) {
            throw new InvalidHTTPRequestException("request contains null values");
        }

        validGenreFormat(genres);
        validBackDropLink(backDrops);

        Movie possibleMovie;
        Query titleQuery = new Query();
        titleQuery.addCriteria(Criteria.where("title").is(title));

        possibleMovie = template.findOne(titleQuery, Movie.class);

        if (possibleMovie != null && possibleMovie.getReleaseDate().equals(releaseDate)) {
            throw new InvalidHTTPRequestException("Movie already exist");
        }
    }

    public void validUserRequest(CreateUserRequest request) throws InvalidHTTPRequestException {
        if (request == null) throw new InvalidHTTPRequestException("request is null");

        String firstName = request.firstName();
        String lastName = request.lastName();
        String username = request.username();
        String password = request.password();
        String email = request.email();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty()
                || password.isEmpty() || email.isEmpty())
            throw new InvalidHTTPRequestException("request contains null data.");

        if (!isValidName(firstName)) throw new InvalidHTTPRequestException("first name format invalid.");
        if(!isValidName(lastName)) throw new InvalidHTTPRequestException("last name format invalid");
        if(!isValidEmail(email)) throw new InvalidHTTPRequestException("email format invalid");

        doesUserExist(username, email);
        isValidPassword(password);
    }

    private void validGenreFormat(List<String> genres) throws InvalidHTTPRequestException {
        for (String genre : genres) {
            if (!GENRES.contains(genre.toUpperCase())) {
                throw new InvalidHTTPRequestException("Invalid genre");
            }
        }
    }

    private void validBackDropLink(List<String> backdrops) throws InvalidHTTPRequestException {
        try {
            for (String link : backdrops) {
                new URL(link).toURI();
            }
        } catch (Exception e) {
            throw new InvalidHTTPRequestException("Invalid url");
        }
    }

    private boolean isValidName(String name) {
        return name.matches("^[a-zA-Z]*$");
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    private void isValidPassword(String password) throws InvalidHTTPRequestException {
        String lengthRegex = ".{8,}";
        String uppercaseRegex = ".*[A-Z].*";
        String lowercaseRegex = ".*[a-z].*";
        String digitRegex = ".*\\d.*";
        String specialCharRegex = ".*[!@#$%^&*()\\-_+=<>?].*";

        String combinedRegex = lengthRegex + uppercaseRegex + lowercaseRegex
                + digitRegex + specialCharRegex;

        Pattern pattern = Pattern.compile(combinedRegex);
        Matcher matcher = pattern.matcher(password);

        if (!matcher.matches()) throw new InvalidHTTPRequestException("Invalid Password format");
    }


    private void doesUserExist(String username, String email) throws InvalidHTTPRequestException {
        Query usernameQuery = new Query();
        Query emailQuery = new Query();
        User possibleUserByUsername;
        User possibleUserByEmail;

        usernameQuery.addCriteria(Criteria.where("username").is(username));
        emailQuery.addCriteria(Criteria.where("email").is(email));

        possibleUserByUsername = template.findOne(usernameQuery, User.class);
        possibleUserByEmail = template.findOne(emailQuery, User.class);

        if (possibleUserByUsername != null) throw new InvalidHTTPRequestException("Username already exist");
        if (possibleUserByEmail != null) throw new InvalidHTTPRequestException("Email already exist");
    }
}
