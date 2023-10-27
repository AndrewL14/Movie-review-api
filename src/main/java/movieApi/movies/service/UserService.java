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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;
    @Autowired
    private MongoTemplate template;
    @Autowired
    private RequestValidator validator;

    private ThreadPoolExecutor executor;

    @PostConstruct
    public void initializeExecutor() {
        int initialThreadPoolSize = (int) calculateInitialThreadPoolSize();
        int maxThreadPoolSize = (int) calculateMaxThreadPoolSize();

        executor = new ThreadPoolExecutor(
                initialThreadPoolSize,
                maxThreadPoolSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    private long calculateInitialThreadPoolSize() {
        long numObjectsInDatabase = repo.count();
        return Math.min(numObjectsInDatabase, Runtime.getRuntime().availableProcessors());
    }

    private long calculateMaxThreadPoolSize() {
        long numObjectsInDatabase = repo.count();
        return Math.max(numObjectsInDatabase, Runtime.getRuntime().availableProcessors() * 2L);
    }

    public List<PublicUserDTO> getAllUsersConcurrently() throws InterruptedException {
        List<Future<PublicUserDTO>> futures = new ArrayList<>();
        List<User> users = repo.findAll();
        List<PublicUserDTO> response = new ArrayList<>();

        for (User user : users) {
            Future<PublicUserDTO> future = executor.submit(() -> Converter.userToPublicDTO(user));
            futures.add(future);
        }

        for (Future<PublicUserDTO> dtoFuture : futures) {
            try {
                PublicUserDTO dto = dtoFuture.get();
                response.add(dto);
            } catch (InterruptedException | ExecutionException e) {
                throw new InterruptedException("Error 404: internal server error");
            }
        }
        return response;
    }

    @Cacheable(value = "usersCache", key = "#imdbId")
    public Optional<PublicUserDTO> getPublicUserByImdbId(String imdbId) {
        return repo.findUserByImdbId(imdbId)
                .map(Converter::userToPublicDTO);
    }

    @Cacheable(value = "usersCache", key = "#username")
    public PublicUserDTO getPublicUserByUsername(String username) {
        User user = template.findOne(Query.query(
                        Criteria.where("username").is(username)) ,
                User.class);
        if (user == null) throw new UserNotFoundException(
                String.format("user was not found match username %s", username)
        );
        return Converter.userToPublicDTO(user);
    }

    @Cacheable(value = "usersCache", key = "#email")
    public PublicUserDTO getPublicUserByEmail(String email) {
        User user = template.findOne(Query.query(
                        Criteria.where("email").is(email)) ,
                User.class);
        assert user != null;
        return Converter.userToPublicDTO(user);
    }

    @Cacheable(value = "usersCache", key = "#imdbId")
    public Optional<PrivateUserDTO> getPrivateUserByImdbId(String imdbId) {
        return repo.findUserByImdbId(imdbId)
                .map(Converter::userToPrivateDTO);
    }

    @CacheEvict(value = "usersCache", allEntries = true)
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

    @CachePut(value = "usersCache", key = "#imdbId")
    public PrivateUserDTO updateExistingUser(UpdateUserRequest request) throws InvalidHTTPRequestException {
        if (request == null) throw new InvalidHTTPRequestException("Request is null");

        String imdbId = request.imdbId(),
                firstName = request.firstName(),
                lastName = request.lastName(),
                username = request.username(),
                password = request.password(),
                email = request.email();


        if (!validator.isValidName(firstName)) throw new InvalidHTTPRequestException("Invalid first name");
        if (!validator.isValidName(lastName)) throw new InvalidHTTPRequestException("Invalid first name");
        validator.isValidPassword(password);
        validator.isValidEmail(email);
        validator.doesUserExist(username , email);

        Update updateRequest = new Update();

        if (!firstName.isEmpty()) updateRequest.set("firstName" , firstName);
        if (!lastName.isEmpty()) updateRequest.set("lastName" , lastName);
        if (!username.isEmpty()) updateRequest.set("username" , username);
        if (!password.isEmpty()) updateRequest.set("password" , password);
        if (!email.isEmpty()) updateRequest.set("email" , email);

        Query query = new Query(Criteria.where("imdbId").is(imdbId));

        UpdateResult result = template.updateFirst(query , updateRequest , User.class);

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
    
    @PreDestroy
    public void shutdownExecutorService() {
        executor.shutdown();
    }
}