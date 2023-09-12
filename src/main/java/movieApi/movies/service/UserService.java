package movieApi.movies.service;

import com.mongodb.client.result.UpdateResult;
import movieApi.movies.converter.Converter;
import movieApi.movies.dto.request.CreateUserRequest;
import movieApi.movies.dto.request.UpdateUserRequest;
import movieApi.movies.dto.response.PrivateUserDTO;
import movieApi.movies.dto.response.PublicUserDTO;
import movieApi.movies.entity.User;
import movieApi.movies.exception.InvalidHTTPRequestException;
import movieApi.movies.exception.UserNotFoundException;
import movieApi.movies.repository.UserRepository;
import movieApi.movies.utils.CustomIdMaker;
import movieApi.movies.utils.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;
    @Autowired
    private MongoTemplate template;
    private RequestValidator validator;

    public List<PublicUserDTO> getAllUsersFromDB() {
        return repo.findAll().stream()
                .map(Converter::userToPublicDTO)
                .collect(Collectors.toList());
    }

    public Optional<PublicUserDTO> getPublicUserByImdbId(String imdbId) {
        return repo.findUserByImdbId(imdbId)
                .map(Converter::userToPublicDTO);
    }

    public PublicUserDTO getPublicUserByUsername(String username) {
        User user = template.findOne(Query.query(
                        Criteria.where("username").is(username)) ,
                User.class);
        assert user != null;
        return Converter.userToPublicDTO(user);
    }

    public PublicUserDTO getPublicUserByEmail(String email) {
        User user = template.findOne(Query.query(
                        Criteria.where("email").is(email)) ,
                User.class);
        assert user != null;
        return Converter.userToPublicDTO(user);
    }

    public Optional<PrivateUserDTO> getPrivateUserByImdbId(String imdbId) {
        return repo.findUserByImdbId(imdbId)
                .map(Converter::userToPrivateDTO);
    }

    public PrivateUserDTO createNewUser(CreateUserRequest user) throws InvalidHTTPRequestException {
        validator.validUserRequest(user);

        String imdbId = CustomIdMaker.generateRandomNumberIdentifier();
        boolean isAvailable = false;
        // Find better way to determine weather or not the id is already in use
        while (!isAvailable) {
            if (getPublicUserByImdbId(imdbId).isEmpty()) {
                isAvailable = true;
            } else {
                imdbId = CustomIdMaker.generateRandomNumberIdentifier();
            }
        }

        User createdUser = repo.insert(new User(imdbId ,
                user.firstName() ,
                user.lastName() ,
                user.username() ,
                user.password() ,
                user.email() ,
                new ArrayList<>()));

        return Converter.userToPrivateDTO(createdUser);
    }

    public PrivateUserDTO updateExistingUser(UpdateUserRequest request) throws InvalidHTTPRequestException {
        if (request == null) throw new InvalidHTTPRequestException("Request is null");

        String imdbId = request.imdbId(),
                firstName = request.firstName(),
                lastName = request.lastName(),
                username = request.username(),
                password = request.password(),
                email = request.email();


        validator.isValidName(firstName);
        validator.isValidName(lastName);
        validator.isValidPassword(password);
        validator.isValidEmail(email);
        validator.doesUserExist(username , email);

        Update update = new Update();

        if (!firstName.isEmpty()) update.set("firstName" , firstName);
        if (!lastName.isEmpty()) update.set("lastName" , lastName);
        if (!username.isEmpty()) update.set("username" , username);
        if (!password.isEmpty()) update.set("password" , password);
        if (!email.isEmpty()) update.set("email" , email);

        Query query = new Query(Criteria.where("imdbId").is(imdbId));

        UpdateResult result = template.updateFirst(query , update , User.class);

        if (result.getModifiedCount() > 0) {
            return Converter.userToPrivateDTO(repo.findUserByImdbId(imdbId)
                    .orElseThrow(UserNotFoundException::new));
        } else {
            throw new RuntimeException("internal server error at service.");
        }
    }

    public void deleteUserFromDB(String imdbId) throws InvalidHTTPRequestException {
        if (imdbId.isEmpty()) throw new InvalidHTTPRequestException("request null");

        User user = repo.findUserByImdbId(imdbId).orElseThrow(UserNotFoundException::new);

        repo.delete(user);
    }
}