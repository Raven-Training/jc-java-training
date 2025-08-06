![Descripción alternativa](src/main/resources/static/book.png)

# Java Training Documentation - Raven.inc

## Description
**This is a library management system developed with Spring Boot. It allows you to manage books by performing CRUD operations (Create, Read, Update, Delete) and advanced searches, filtering books by ISBN, title, author, and genre.**

**It includes Swagger integration to facilitate interactive documentation and testing of the endpoints.**


## Feature

1. **Authentication with Spring Security**: The API implements an authentication mechanism and scheme using Spring Security. This identifies which user is performing information searches.

2. **Authorization with JSON Web Token**: he API implements a security method that validates and controls access to the REST API.

3. **Testing**: Includes unit and integration tests.

4. **Layered Architecture**: This project implements a clearly defined layered architecture, following the principles of separation of concerns and high cohesion. Each layer has a specific function and communicates with the others in a controlled manner.

5. **Database Storage**: A database schema is implemented, preferably Postgres, to store all necessary information. This includes data related to books and users.

6. **OpenLibrary Integration**: Search for books by ISBN using the OpenLibrary API.

7. **Pagination**: Book listings with pagination and sorting.

8. **Swagger Documentation**: This project uses Swagger to automatically generate REST API documentation. Swagger allows you to explore and test endpoints interactively from an intuitive web interface.

You can access the documentation at:
```properties
http://localhost:8081/swagger-ui/index.html
```

## Configuration
## Installation Instructions

### Prerequisites

- Java JDK 21
- Maven
- Postgres
- Git

### Clone the repository
- git clone https://github.com/Raven-Training/jc-java-training.git

### Database Configuration
- Create a Postgres database named `book`

You must configure the corresponding Postgres properties in the application.properties file:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/book
```

## Project Packages
The project is organized into the following packages:

- `com.raven.training.config`:Configures the security of a Spring Boot application with JWT (JSON Web Token). Its main features are: Implements stateless authentication with JWT tokens, Defines public access points for Swagger as well as for registration and login endpoints (/api/v1/auth/**), Protects all other endpoints with authentication, Configures the JWT token validator, Uses BCrypt for password encryption.
- `com.raven.training.exception`: Captures custom exceptions.
- `com.raven.training.mapper`: Contains mappers using MapStruct for efficient conversion between objects of different application layers.
- `com.raven.training.persistence`: Contains entities, models, and repositories.
- `com.raven.training.presentation`: Contains controllers that act as the entry point to the application and DTOs for transferring information.
- `com.raven.training.service`: Contains the application's business logic, structured into two main components: Interfaces and Implementations.
- `com.raven.training.utils`: Contains tools like JwtUtils which encapsulates all logic related to JWT token management, including token generation, validation, and information extraction. It also includes IdentifierDeserializer, a custom Jackson deserializer that handles identifiers that may come as a simple string or an array in a JSON. This is useful for handling external API responses where identifiers may vary in their structure.

## Controllers

- The application implements a modular route structure with `AuthUserController`, `BookController`, `UserController` that delegate business logic to the lower layers. Each controller specializes in a specific resource or application functionality.

### Routes:

**URL:**
```properties
http://localhost:8081
```
**AuthUserController:**

- **POST**
- `/api/v1/auth/register`: User registration.
- `/api/v1/auth/login`: Authentication and token generation.

**BookController:**

- **GET**
- `/api/v1/books/findAll`: Paginated list of books (10 per page).
- `/api/v1/books/findAll?author=author`: Search by author.
- `/api/v1/books/findAll?title=title`: Search by title.
- `/api/v1/books/findAll?gender=gender`: Search by genre.
- `/api/v1/books/findById/{id}`: Details of a specific book by its ID.
- `/api/v1/books/isbn/{isbn}`: Details of a specific book by its ISBN.


- **POST**
- `/api/v1/books/create`: Creates a new book.

- **PUT**
- `/api/v1/books/update/{id}`: Updates a book record.

- **DELETE**
- `/api/v1/books/delete/{id}`: Deletes a book record.

**UserController:**

- **GET**
- `/api/v1/users/findAll`: Paginated list of users (10 per page).
- `/api/v1/users/logged`: Retrieves the currently logged-in user.
- `/api/v1/users/findById/{id}`: Details of a specific user by their ID.


- **POST**
- `/api/v1/users/{userId}/books/{bookId}`: Adds a new book to the user's book collection.

- **PUT**
- `/api/v1/users/update/{id}`: Updates a user record.

- **DELETE**
- `/api/v1/users/delete/{id}`: Deletes a user record.
- `/api/v1/users//{userId}/books/{bookId}`: Removes a book from a user's book collection.

## Exception Handling
The project includes an exception handling system that ensures an appropriate response to different types of errors. Exception handling is done through the `GlobalExceptionHandler`, `BookAlreadyInCollectionException`, `BookNotFoundException`, `BookNotInCollectionException`, `EmailAlreadyExistsException`, `UsernameAlreadyExistsException`, `UserNotFoundException` classes.

## Steps to Download a File
1. On GitHub.com, navigate to the main page of the repository.
2. Navigate to the file and click to enter it.
2. At the top of the file, click Raw.
3. You will be taken to the file's content; right-click and save it.

## Summary
This document provides an overview of the project's structure, key components, and configurations.
