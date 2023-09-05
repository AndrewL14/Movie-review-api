package movieApi.movies.utilsTest;

import movieApi.movies.utils.CustomIdMaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.junit.jupiter.api.Assertions.*;

public class CustomIdMakerTest {

    @InjectMocks
    CustomIdMaker customIdMaker;

    @BeforeEach
    public void setup() {
        initMocks(this);
    }

    @Test
    public void CreateCustomId_ValidID() {
        // GIVEN

        // WHEN
        String response = CustomIdMaker.generateRandomNumberIdentifier();

        // THEN
        assertNotNull(response);
        assertTrue(response.startsWith("tt"), "expected valid response starting with tt but got: " + response);
        assertEquals(9 , response.length() , "expected valid response with length 9 but got length " + response.length());
    }
}
