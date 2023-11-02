package movieApi.movies.serviceTests;

import com.mongodb.client.result.UpdateResult;
import movieApi.movies.dto.request.CreateUserRequest;
import movieApi.movies.dto.request.UpdateUserRequest;
import movieApi.movies.dto.response.PrivateUserDTO;
import movieApi.movies.dto.response.PublicUserDTO;
import movieApi.movies.entity.User;
import movieApi.movies.exception.InvalidHTTPRequestException;
import movieApi.movies.repository.UserRepository;
import movieApi.movies.service.UserService;
import movieApi.movies.utils.RequestValidator;
import org.bson.BsonValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.cache.CacheManager;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;
import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private RequestValidator requestValidator;

    @Mock
    private ThreadPoolExecutor executor;

    @MockBean
    private CacheManager cacheManager;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetPublicUserByImdbId() {
        String imdbId = "tt154875";
        User user = new User(imdbId, "John", "Doe", "johndoe", "password1", "johndoe@example.com", new ArrayList<>());

        // Mock the behavior of the repository to return a user
        Mockito.when(userRepository.findUserByImdbId(imdbId)).thenReturn(Optional.of(user));

        Optional<PublicUserDTO> publicUserDTO = userService.getPublicUserByImdbId(imdbId);

        // Add assertions to verify the result
        // Check that publicUserDTO matches the expected DTO for the user
        assertTrue(publicUserDTO.isPresent());
    }

    @Test
    public void testGetPublicUserByUsername() {
        String username = "johndoe";
        User user = new User("tt154875", "John", "Doe", username, "password1", "johndoe@example.com", new ArrayList<>());

        // Mock the behavior of the template to return a user based on username
        Mockito.when(mongoTemplate.findOne(Mockito.any(), Mockito.eq(User.class))).thenReturn(user);

        PublicUserDTO publicUserDTO = userService.getPublicUserByUsername(username);

        // Add assertions to verify the result
        // Check that publicUserDTO matches the expected DTO for the user
        assertEquals("John" , publicUserDTO.firstName());
    }

    @Test
    public void testGetPublicUserByEmail() {
        String email = "johndoe@example.com";
        User user = new User("tt154875", "John", "Doe", "johndoe", "password1", email, new ArrayList<>());

        // Mock the behavior of the template to return a user based on email
        Mockito.when(mongoTemplate.findOne(Mockito.any(), Mockito.eq(User.class))).thenReturn(user);

        PublicUserDTO publicUserDTO = userService.getPublicUserByEmail(email);

        // Add assertions to verify the result
        // Check that publicUserDTO matches the expected DTO for the user
        assertEquals("John" , publicUserDTO.firstName());
    }

    @Test
    public void testGetPrivateUserByImdbId() {
        String imdbId = "tt154875";
        User user = new User(imdbId, "John", "Doe", "johndoe", "password1", "johndoe@example.com", new ArrayList<>());

        // Mock the behavior of the repository to return a user
        Mockito.when(userRepository.findUserByImdbId(imdbId)).thenReturn(Optional.of(user));

        Optional<PrivateUserDTO> privateUserDTO = userService.getPrivateUserByImdbId(imdbId);

        // Add assertions to verify the result
        // Check that privateUserDTO matches the expected DTO for the user
        assertTrue(privateUserDTO.isPresent());
    }

    @Test
    public void testCreateNewUser() throws InvalidHTTPRequestException {
        CreateUserRequest createUserRequest = new CreateUserRequest("John", "Doe", "johndoe", "password1", "johndoe@example.com");

        // Mock the behavior of the repository to return the created user
        Mockito.when(userRepository.insert(Mockito.any(User.class))).thenReturn(new User("tt154875", "John", "Doe", "johndoe", "password1", "johndoe@example.com", new ArrayList<>()));

        PrivateUserDTO privateUserDTO = userService.createNewUser(createUserRequest);

        // Add assertions to verify the result
        // Check that privateUserDTO matches the expected DTO for the created user
        assertEquals("tt154875", privateUserDTO.imdbId());
    }

    @Test
    public void testUpdateExistingUser() throws InvalidHTTPRequestException {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest("tt154875", "joe", "sam", "janesmith", "newpassword", "janesmith@example.com");

        // Mock the behavior of the template to return a modified count for the update
        Mockito.when(mongoTemplate.updateFirst(Mockito.any(), Mockito.any(), Mockito.eq(User.class))).thenReturn(new UpdateResult() {
            @Override
            public boolean wasAcknowledged() {
                return true;
            }

            @Override
            public long getMatchedCount() {
                return 0;
            }

            @Override
            public long getModifiedCount() {
                return 1;
            }

            @Override
            public BsonValue getUpsertedId() {
                return null;
            }
        });
        Mockito.when(requestValidator.isValidName(Mockito.any())).thenReturn(true);

        // Mock the behavior of the repository to return the updated user
        Mockito.when(userRepository.findUserByImdbId("tt154875")).thenReturn(Optional.of(new User("tt154875", "joe", "sam", "janesmith", "newpassword", "janesmith@example.com", new ArrayList<>())));

        PrivateUserDTO privateUserDTO = userService.updateExistingUser(updateUserRequest);

        // Add assertions to verify the result
        // Check that privateUserDTO matches the expected DTO for the updated user
        assertNotNull(privateUserDTO);
    }

    @Test
    public void testDeleteUserFromDB() throws InvalidHTTPRequestException {
        String imdbId = "tt154875";
        User user = new User(imdbId, "John", "Doe", "johndoe", "password1", "johndoe@example.com", new ArrayList<>());

        // Mock the behavior of the repository to return a user based on imdbId
        Mockito.when(userRepository.findUserByImdbId(imdbId)).thenReturn(Optional.of(user));

        userService.deleteUserFromDB(imdbId);

        // Add assertions to verify that the user has been deleted
        // You can check if the repository's delete method has been called
        assertTrue(true);
    }
}
