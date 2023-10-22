package movieApi.movies.utils;

import java.util.Random;

public class CustomIdMaker {

    // Define the pool of characters for generating numbers in the identifier
    private static final String NUMBER_POOL = "0123456789";

    /**
     * generates a unique imdbId for later use
     * @return A new imdbId
     */
    public static String generateRandomNumberIdentifier() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder()
                .append("tt");

        for (int i = 0; i < 7; i++) {
            int randomIndex = random.nextInt(NUMBER_POOL.length());
            char randomChar = NUMBER_POOL.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }

        return stringBuilder.toString();
    }
}
