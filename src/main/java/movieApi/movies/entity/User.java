package movieApi.movies.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Document(collection = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    private ObjectId id;
    private String imdbId;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    @DocumentReference
    private List<Review> userReviews;

    public User(String imdbId , String firstName , String lastName ,
                String username , String password , String email , List<Review> userReviews) {
        this.imdbId = imdbId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.userReviews = userReviews;
    }
}
