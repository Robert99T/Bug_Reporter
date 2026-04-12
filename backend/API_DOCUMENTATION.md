# REST API Documentation

This document describes the REST API endpoints available in the Bug Reporter backend, including the HTTP methods, endpoint paths, descriptions, and the Data Transfer Objects (DTOs) involved in requests and responses. It is designed to be easily readable by both developers and automated AI tools. 

*(Note: Fields marked as "Optional" are not strictly strictly required by validation rules, or may return `null` in responses. Unmarked fields are mandatory.)*

---

## 1. Authentication Endpoints

### User Login
- **Endpoint:** `POST /auth/login`
- **Description:** Authenticates a user and establishes a session.
- **Request Body:** `LoginRequest`
  - `username` (String)
  - `password` (String)
- **Response Body:** `LoginResponse`
  - `token` (String)
- **Success Status:** `200 OK`

---

## 2. User Endpoints

### Register User
- **Endpoint:** `POST /users/register`
- **Description:** Registers a new user.
- **Request Body:** `UserRegistrationDTO`
  - `username` (String)
  - `email` (String)
  - `password` (String)
  - `phoneNumber` (String) - *Optional*
- **Response Body:** `UserResponseDTO`
  - `id` (Long)
  - `username` (String)
  - `email` (String)
  - `phoneNumber` (String) - *Optional*
  - `score` (Double)
  - `role` (String / UserRole)
- **Success Status:** `201 Created`

### Get All Users
- **Endpoint:** `GET /users`
- **Description:** Retrieves a list of all registered users.
- **Request Body:** None
- **Response Body:** `List<UserResponseDTO>` (See `UserResponseDTO` above)
- **Success Status:** `200 OK`

### Get User by ID
- **Endpoint:** `GET /users/{id}`
- **Description:** Retrieves details of a specific user by their ID.
- **Request Body:** None
- **Response Body:** `UserResponseDTO` (See `UserResponseDTO` above)
- **Success Status:** `200 OK`

### Update User
- **Endpoint:** `PUT /users/{id}`
- **Description:** Updates the details of a specific user.
- **Request Body:** `UserRequestDTO`
  - `username` (String)
  - `email` (String)
  - `password` (String)
  - `phoneNumber` (String) - *Optional*
- **Response Body:** `UserResponseDTO` (See `UserResponseDTO` above)
- **Success Status:** `200 OK`

### Delete User
- **Endpoint:** `DELETE /users/{id}`
- **Description:** Deletes a specific user by their ID.
- **Request Body:** None
- **Response Body:** `UserResponseDTO` (Contains details of the deleted user)
- **Success Status:** `200 OK`

---

## 3. Bug Endpoints

### Create a Bug
- **Endpoint:** `POST /bugs`
- **Description:** Creates a new bug report.
- **Request Body:** `CreateBugRequest`
  - `title` (String)
  - `text` (String)
  - `status` (String)
  - `pictureUrl` (String) - *Optional*
  - `authorId` (Long) - *Optional*
  - `tags` (Set<String>) - *Optional*
- **Response Body:** `BugResponse`
  - `id` (Long)
  - `title` (String)
  - `text` (String)
  - `creationDate` (LocalDateTime)
  - `status` (String)
  - `authorId` (Long)
  - `authorUsername` (String)
  - `comments` (List<CommentResponse>) - *Optional*
  - `tags` (Set<String>) - *Optional*
  - `pictureUrl` (String) - *Optional*
- **Success Status:** `201 Created`

### Get All Bugs
- **Endpoint:** `GET /bugs`
- **Description:** Retrieves a list of all bug reports.
- **Request Body:** None
- **Response Body:** `List<BugResponse>` (See `BugResponse` above)
- **Success Status:** `200 OK`

### Get Bug by ID
- **Endpoint:** `GET /bugs/{id}`
- **Description:** Retrieves details of a specific bug report by its ID.
- **Request Body:** None
- **Response Body:** `BugResponse` (See `BugResponse` above)
- **Success Status:** `200 OK`

### Update a Bug
- **Endpoint:** `PUT /bugs/{id}`
- **Description:** Updates an existing bug report.
- **Request Body:** `UpdateBugRequest`
  - `title` (String) - *Optional*
  - `text` (String) - *Optional*
  - `pictureUrl` (String) - *Optional*
  - `status` (String) - *Optional*
- **Response Body:** `BugResponse` (See `BugResponse` above)
- **Success Status:** `200 OK`

### Delete a Bug
- **Endpoint:** `DELETE /bugs/{id}`
- **Description:** Deletes a specific bug report.
- **Request Body:** None
- **Response Body:** None
- **Success Status:** `204 No Content`

---

## 4. Comment Endpoints

### Create a Comment
- **Endpoint:** `POST /bugs/{bugId}/comments`
- **Description:** Adds a new comment to a specific bug report.
- **Path Parameter:** `bugId` (ID of the bug to comment on)
- **Request Body:** `CreateCommentRequest`
  - `text` (String)
  - `authorId` (Long)
  - `pictureUrl` (String) - *Optional*
- **Response Body:** `CommentResponse`
  - `id` (Long)
  - `text` (String)
  - `creationDate` (LocalDateTime)
  - `authorId` (Long)
  - `authorUsername` (String)
  - `bugId` (Long)
  - `pictureUrl` (String) - *Optional*
- **Success Status:** `201 Created`

### Get Comments by Bug ID
- **Endpoint:** `GET /bugs/{bugId}/comments`
- **Description:** Retrieves all comments associated with a specific bug.
- **Path Parameter:** `bugId` (ID of the bug)
- **Request Body:** None
- **Response Body:** `List<CommentResponse>` (See `CommentResponse` above)
- **Success Status:** `200 OK`

### Get Comment by ID
- **Endpoint:** `GET /comments/{id}`
- **Description:** Retrieves details of a specific comment by its ID.
- **Request Body:** None
- **Response Body:** `CommentResponse` (See `CommentResponse` above)
- **Success Status:** `200 OK`

### Update a Comment
- **Endpoint:** `PUT /comments/{id}`
- **Description:** Updates an existing comment.
- **Request Body:** `UpdateCommentRequest`
  - `text` (String) - *Optional*
  - `pictureUrl` (String) - *Optional*
- **Response Body:** `CommentResponse` (See `CommentResponse` above)
- **Success Status:** `200 OK`

### Delete a Comment
- **Endpoint:** `DELETE /comments/{id}`
- **Description:** Deletes a specific comment by its ID.
- **Request Body:** None
- **Response Body:** None
- **Success Status:** `204 No Content`
