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

---

## 5. Voting Endpoints

Voting allows users to upvote or downvote bugs and comments. Each user can cast **one vote per item**. Voting on the same item again with the **same type** removes the vote (toggle off). Voting with the **opposite type** switches the vote.

### Vote on a Bug
- **Endpoint:** `POST /bugs/{bugId}/votes`
- **Description:** Casts or toggles a vote on a bug report.
- **Path Parameter:** `bugId` (ID of the bug)
- **Request Body:** `VoteRequest`
  - `userId` (Long) — ID of the voting user
  - `voteType` (String) — `"UPVOTE"` or `"DOWNVOTE"`
- **Response Body:** `VoteResponse`
  - `id` (Long)
  - `targetType` (String) — `"BUG"`
  - `targetId` (Long) — Same as `bugId`
  - `userId` (Long)
  - `voteType` (String) — `"UPVOTE"` or `"DOWNVOTE"`
- **Success Status:** `200 OK`
- **Edge Cases:**
  - If `userId` matches `bug.authorId` → **`403 Forbidden`** ("Cannot vote on your own bug")
  - If the user already voted with the **same** `voteType` → **remove** the existing vote and return `204 No Content`
  - If the user already voted with the **opposite** `voteType` → **switch** the vote to the new type

### Vote on a Comment
- **Endpoint:** `POST /comments/{commentId}/votes`
- **Description:** Casts or toggles a vote on a comment.
- **Path Parameter:** `commentId` (ID of the comment)
- **Request Body:** `VoteRequest`
  - `userId` (Long)
  - `voteType` (String) — `"UPVOTE"` or `"DOWNVOTE"`
- **Response Body:** `VoteResponse`
  - `id` (Long)
  - `targetType` (String) — `"COMMENT"`
  - `targetId` (Long) — Same as `commentId`
  - `userId` (Long)
  - `voteType` (String) — `"UPVOTE"` or `"DOWNVOTE"`
- **Success Status:** `200 OK`
- **Edge Cases:**
  - If `userId` matches `comment.authorId` → **`403 Forbidden`** ("Cannot vote on your own comment")
  - Toggle/switch behavior same as bug voting above

### Remove a Vote
- **Endpoint:** `DELETE /votes/{voteId}`
- **Description:** Deletes an existing vote by its ID.
- **Request Body:** None
- **Response Body:** None
- **Success Status:** `204 No Content`
- **Edge Cases:**
  - If the vote does not exist → `404 Not Found`
  - Only the user who cast the vote (or a moderator) should be allowed to delete it → `403 Forbidden` otherwise

---

## 6. User Score Endpoint

### Get User Score
- **Endpoint:** `GET /users/{id}/score`
- **Description:** Returns the aggregate score of a user. The score is typically the sum of all upvotes minus downvotes received across the user's bugs and comments.
- **Path Parameter:** `id` (User ID)
- **Request Body:** None
- **Response Body:** `UserScoreResponse`
  - `userId` (Long)
  - `score` (Double)
- **Success Status:** `200 OK`
- **Edge Cases:**
  - If user does not exist → `404 Not Found`

---

## 7. Bug Details Page — Backend Notes & Edge Cases

This section documents behavioral requirements for the Bug Details page that the backend must enforce.

### Enhanced Response Fields

The `BugResponse` and `CommentResponse` DTOs should be extended with the following fields to support the details page:

- **`BugResponse` additions:**
  - `authorScore` (Double) — The bug author's aggregate user score
  - `voteScore` (Integer) — Total upvotes minus downvotes on this bug
  - `userVote` (String, nullable) — The requesting user's existing vote type (`"UPVOTE"`, `"DOWNVOTE"`, or `null`). Determined via optional `?userId=` query parameter on `GET /bugs/{id}`

- **`CommentResponse` additions:**
  - `authorScore` (Double) — The comment author's aggregate user score
  - `voteScore` (Integer) — Total upvotes minus downvotes on this comment
  - `userVote` (String, nullable) — The requesting user's existing vote type. Determined via optional `?userId=` query parameter on `GET /bugs/{bugId}/comments`

### Login Response Enhancement

- `POST /auth/login` should return the user's details in the response body:
  - `id` (Long)
  - `username` (String)
  - `role` (String / UserRole) — `"USER"` or `"MODERATOR"`

### Status Transition Rules

| Current Status | Event                    | New Status    |
|----------------|--------------------------|---------------|
| `OPEN`         | First comment is posted  | `IN_PROGRESS` |
| `IN_PROGRESS`  | Bug author marks solved  | `SOLVED`      |
| `SOLVED`       | —                        | (terminal)    |

- When **creating a comment** on a bug with status `OPEN`, the backend **must automatically** change the bug's status to `IN_PROGRESS`.
- When a bug status is `SOLVED`, the backend **must reject** new comment creation with `400 Bad Request` ("Bug is solved — no new comments allowed").
- Only the **bug author** should be able to set status to `SOLVED` via `PUT /bugs/{id}`. Other users or moderators setting `status: "SOLVED"` should receive `403 Forbidden`.

### Permission Rules Summary

| Action              | Allowed By                        |
|---------------------|-----------------------------------|
| Edit/Delete Bug     | Bug author, Moderator             |
| Edit/Delete Comment | Comment author, Moderator         |
| Vote on Bug         | Any user **except** the bug author |
| Vote on Comment     | Any user **except** the comment author |
| Mark Bug Solved     | Bug author **only**               |
| Add Comment         | Any user (if bug is not `SOLVED`) |
