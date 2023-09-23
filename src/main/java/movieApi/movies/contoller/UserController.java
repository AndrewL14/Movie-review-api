package movieApi.movies.contoller;

import movieApi.movies.dto.request.CreateUserRequest;
import movieApi.movies.dto.request.UpdateUserRequest;
import movieApi.movies.dto.response.PrivateUserDTO;
import movieApi.movies.dto.response.PublicUserDTO;
import movieApi.movies.exception.InvalidHTTPRequestException;
import movieApi.movies.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    UserService service;

    /*
    TODO in separate classes
    1. Login
    1A. check if first field given is username or email
        1A1. get user based on username/check if username and password match up
        1B1. get user based on email/ check if email and password match up
     2. Get user information in the form of DTO
     3. Get all user information if the user request their information.
     4. update information if user wishes
     5. update review when user post new review
     */


    @GetMapping("/")
    public ResponseEntity<List<PublicUserDTO>> getAllPublicInformationUsers() throws InterruptedException {
        return  new ResponseEntity<List<PublicUserDTO>>(
                service.getAllUsersConcurrently(), HttpStatus.OK)
                ;
    }

    @GetMapping("/public/{imdbId}")
    public ResponseEntity<Optional<PublicUserDTO>> getPublicUserInformationByImdbId(@PathVariable String imdbId) {
        return new ResponseEntity<Optional<PublicUserDTO>>(
                service.getPublicUserByImdbId(imdbId), HttpStatus.OK
                );
    }

    @GetMapping("/public/{username}")
    public ResponseEntity<PublicUserDTO> getPublicUserInformationByUsername(@PathVariable String username) {
        return new ResponseEntity<PublicUserDTO>(
                service.getPublicUserByUsername(username), HttpStatus.OK
                );
    }

    @GetMapping("/public/{email}")
    public ResponseEntity<PublicUserDTO> getPublicUserInformationByEmail(@PathVariable String email) {
        return new ResponseEntity<PublicUserDTO>(
                service.getPublicUserByEmail(email), HttpStatus.OK
        );
    }

    @GetMapping("/private/{imdbId}")
    public ResponseEntity<Optional<PrivateUserDTO>> getPrivateUserInformationByImdbId(@PathVariable String imdbID) {
        return new ResponseEntity<Optional<PrivateUserDTO>>(
                service.getPrivateUserByImdbId(imdbID), HttpStatus.OK
        );
    }

    @PostMapping("/create")
    public ResponseEntity<PrivateUserDTO> createNewUser(@Validated @RequestBody CreateUserRequest user) throws InvalidHTTPRequestException {
            return new ResponseEntity<PrivateUserDTO>(
                    service.createNewUser(user), HttpStatus.CREATED
            );
    }

    @PutMapping("/update")
    public ResponseEntity<PrivateUserDTO> updateExistingUserWithRequest(@Validated @RequestBody UpdateUserRequest request) throws InvalidHTTPRequestException {
        return new ResponseEntity<PrivateUserDTO>(
                service.updateExistingUser(request), HttpStatus.OK
        );
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@PathVariable String imdbId) throws InvalidHTTPRequestException {
        service.deleteUserFromDB(imdbId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
