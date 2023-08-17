# Movie-review-api

## description

This Project was made to showcase my understanding of web API, SpringBoot, and MongoDB.
This Project is an anonymous movie review API that can be easily integrated into most front-end designs.

### DISCLAIMER

Since this project is used just to showcase my knowledge the .env file will show up in my Github repo,
in actual practice, the .env file would be hidden and not be added to the repo.

Currently, the user and authentication have not been fully implemented yet.

### Program-startup

1. Clone the project onto your local desktop.
2. Compile the project then go to the `MovieApplication.java` class
3. Run the Application.
4. To interact with the program you will need a terminal, an Application like Postman, or any Browser.
5. The application should be running on http://localhost:8080/
6. Look at `Request navigation` below for all commands then copy and paste them into your selected option.

#### Note:
This program's responses or in JSON format to best Read I recommend Postman or terminal

### Request navigation

#### terminal

these commands should work on the terminal, CMD, and Git Bash

`Get all movies`
curl -X GET http://localhost:8080/api/v1/movies

`Get a movie based on it's imbdId`
cur -X GET http://localhost:8080/api/v1/movies -d "imbdId=imdbId you want to use"

List of available imbdIds:
1. tt3915174
2. tt1630029
3. tt8760708
4. tt11116912
5. tt6443346
6. tt0499549
7. tt3447590
8. tt9114286
9. tt10298840
10. tt8093700
11. tt8429231

`To create a new Review`
cur -X POST http://localhost:8080/api/v1/reviews -d "reviewBody=what ever you want to put in here&imdbId=imdbId you want to use&userImdbId=tt7581"

`Upload a new Movie`
Note: currently no authentication to what is put inside these fields as such duplication of the same can happen


`curl -X PUT -H "Content-Type: application/json" -d '{
"title": "Extraction 2",
"releaseDate": "2023-06-16",
"trailerLink": "https://www.youtube.com/watch?v=Y274jZs5s7s",
"poster": "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/7gKI9hpEMcZUQpNgKrkDzJpbnNS.jpg",
"genres": [
"Action thriller"
],
"backDrop": [
"https://www.themoviedb.org/t/p/original/3oKSoTLgJFsBUXe0amkKoSlKYNo.jpg",
"https://www.themoviedb.org/t/p/original/Av21MNshnpnp1zq5jt9OdQ1ZSWT.jpg",
"https://www.themoviedb.org/t/p/original/xXp7TbCOKe4lB65ngkt3CuhsiAa.jpg",
"https://www.themoviedb.org/t/p/original/bQLukBjKh91ZY9KITFXXqXaniEK.jpg"
]
}' http://localhost:8080/api/v1/movies/upload`

#### Postman And Browser

`Get all movies` GET request
localhost:8080/api/v1/movies

`Get a movie based on its imbdId` GET request
localhost:8080/api/v1/movies/the imdbId you want to use

Or you can submit a JSON body:
`{
    "imdbId": "the imdbId you want"
}`

`To create a new Review` POST request
localhost:8080/api/v1/reviews?reviewBody=your review&imdbId=imdbId you want to use&userImdbId=tt7581

OR

localhost:8080/api/v1/reviews

JSON body:
`{
    "reviewBody": "The Movie was great.",
    "imdbId": "tt3915174",
    "userImdbId": "tt7581"
}`

`Upload a new Movie` PUT request

Note: currently no authentication to what is put inside these fields as such duplication of the same can happen
I recommend doing this one in Postman since the request for the browser is well very long.


`localhost:8080/api/v1/movies/upload?title=Extraction 2
&releaseDate=2023-06-16&trailerLink=https://www.youtube.com/watch?v=Y274jZs5s7s
&poster=https://www.themoviedb.org/t/p/w600_and_h900_bestv2/7gKI9hpEMcZUQpNgKrkDzJpbnNS.jpg
&genres=action thriller&genres=action
&backDrop=https://www.themoviedb.org/t/p/original/3oKSoTLgJFsBUXe0amkKoSlKYNo.jpg
&backDrop=https://www.themoviedb.org/t/p/original/Av21MNshnpnp1zq5jt9OdQ1ZSWT.jpg
&backDrop=https://www.themoviedb.org/t/p/original/xXp7TbCOKe4lB65ngkt3CuhsiAa.jpg
&backDrop=https://www.themoviedb.org/t/p/original/bQLukBjKh91ZY9KITFXXqXaniEK.jpg`

OR

localhost:8080/api/v1/movies/upload

JSON body:
`{
    "title": "Extraction 2",
    "releaseDate": "2023-06-16",
    "trailerLink": "https://www.youtube.com/watch?v=Y274jZs5s7s",
    "poster": "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/7gKI9hpEMcZUQpNgKrkDzJpbnNS.jpg",
    "genres": [
    "Action thriller"
    ],
    "backDrop": [
    "https://www.themoviedb.org/t/p/original/3oKSoTLgJFsBUXe0amkKoSlKYNo.jpg",
    "https://www.themoviedb.org/t/p/original/Av21MNshnpnp1zq5jt9OdQ1ZSWT.jpg",
    "https://www.themoviedb.org/t/p/original/xXp7TbCOKe4lB65ngkt3CuhsiAa.jpg",
    "https://www.themoviedb.org/t/p/original/bQLukBjKh91ZY9KITFXXqXaniEK.jpg"
    ]
}`
