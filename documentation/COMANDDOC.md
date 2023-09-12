# Design Document

## NOTE:
these commands can run both on your local browser and postman.
I recommend using postman because the JSON response is automatically
organized.

the base link:
http://localhost:8080/api/v1/users

### USER controller commands

#### Get all Public users
works both in the browser and postman If used in Postman set the Request to
send a GET request.

http://localhost:8080/api/v1/users/
#### Get Public User based on imdbID
http://localhost:8080/api/v1/users/public?imdbId=tt7581

PostMan:
Set Request to GET
```
{
    "imdbId": "tt7581"
}
```

#### get Public User based on username
http://localhost:8080/api/v1/users/public?username=beastBoy

PostMan:
Set Request to GET

```
{
    "username": "bestBoy"
}
```

#### get Public User based on email
http://localhost:8080/api/v1/users/public?email=example@gmail.com

PostMan:
Set Request to GET

```
{
    "email": "example@gmail.com"
}
```

#### get Private User based on imdbID
http://localhost:8080/api/v1/users/private?imdbId=tt7581

PostMan:
Set Request to GET
```
{
    "imdbId": "tt7581"
}
```

#### create new user
NOTE!! this program authenticates request so first name, last name, and 
email must be valid format.
first and last names should not contain any special characters or numbers
password should contain 1 upper and lowercase letter, 1 number, 1 special character, and
should be a length of 8 or longer
email:example@gmail.com

this program also checks if the user already exist based on email and username, it will throw 
a invalidHTTPRequestException if passed A already existing user.

REPLACE BRACKETS WITH VALID VALUES!!

http://localhost:8080/api/v1/users/create?firstName={firstname}&lastName={lastname}&username{username}&password={password}&email{email}

POSTMAN: 
Set request to POST

```
{
    "firstName": "{firstname}"
    "lastName": "{lastname}"
    "username": "{username}"
    "password": "{password}"
    "email": "{email}"
}
```

To be properly configured
#### update existing user

To be properly configured
#### delete user
