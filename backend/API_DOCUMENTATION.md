# Bug Reporter - REST API Documentation

This document provides a comprehensive overview of the REST API endpoints available in the Bug Reporter backend service. It is designed for both human developers and LLM consumption to facilitate client integration and system understanding.

## Table of Contents
- [Authentication Endpoints](#1-authentication-endpoints)
- [User Endpoints](#2-user-endpoints)
- [Bug Endpoints](#3-bug-endpoints)
- [Comment Endpoints](#4-comment-endpoints)
- [Vote Endpoints](#5-vote-endpoints)

---

## 1. Authentication Endpoints

### 1.1 Login
Authenticate a user and create a session.

- **Endpoint**: `POST /auth/login`
- **Access Control**: Public
- **Usage Summary**: Authenticates user credentials via Spring Security. On success, sets a session cookie (JSESSIONID) and returns the authenticated user's ID, username, and role.
- **DTOs Involved**: `LoginRequest`, `LoginResponse`

**Request Payload (`LoginRequest`)**
| Field | Type | Required | Description |
| :--- | :--- | :---: | :--- |
| `username` | String | Yes | The user's registration username |
| `password` | String | Yes | The user's plaintext password |

**Response Payload (`LoginResponse`)**
| Field | Type | Required | Description |
| :--- | :--- | :---: | :--- |
| `id` | Long | Yes | Unique identifier for the authenticated user |
| `username` | String | Yes | Display name of the user |
| `role` | String | Yes | Authority level (e.g., "USER", "ADMIN") |

---

## 2. User Endpoints

### 2.1 Register User
Create a new user account.

- **Endpoint**: `POST /users/register`
- **Access Control**: Public
- **Usage Summary**: Registers a new user with the specified credentials.
- **DTOs Involved**: `UserRegistrationDTO`, `UserResponseDTO`

**Request Payload (`UserRegistrationDTO`)**
| Field | Type | Required | Description |
| :--- | :--- | :---: | :--- |
| `username` | String | Yes | Username (3-20 characters) |
| `email` | String | Yes | Valid email address |
| `password` | String | Yes | Password (min 8 characters) |
| `phoneNumber`| String | No | Contact phone number |

**Response Payload (`UserResponseDTO`)** *(See Appendix A for definition)*

### 2.2 Get All Users
Retrieve a list of all registered users.

- **Endpoint**: `GET /users`
- **Access Control**: Authenticated User (Admin recommended depending on system policy)
- **Usage Summary**: Fetches the complete list of system users.
- **DTOs Involved**: `UserResponseDTO`

**Response Payload**: List of `UserResponseDTO`

### 2.3 Get User by ID
Retrieve details for a specific user.

- **Endpoint**: `GET /users/{id}`
- **Access Control**: Authenticated User
- **Usage Summary**: Fetches user profile information by their unique ID.
- **DTOs Involved**: `UserResponseDTO`

**Response Payload**: `UserResponseDTO`

### 2.4 Get User Score
Retrieve the current reputation/contribution score for a user.

- **Endpoint**: `GET /users/{id}/score`
- **Access Control**: Authenticated User
- **Usage Summary**: Fetches specifically the score for a given user, useful for rapid UI updates without pulling the full profile.
- **DTOs Involved**: `UserScoreResponse`

**Response Payload (`UserScoreResponse`)**
| Field | Type | Required | Description |
| :--- | :--- | :---: | :--- |
| `userId` | Long | Yes | ID of the user |
| `score` | Double | Yes | The calculated reputation score |

### 2.5 Update User
Modify an existing user's profile information.

- **Endpoint**: `PUT /users/{id}`
- **Access Control**: Resource Owner (or Admin)
- **Usage Summary**: Updates the given user's information.
- **DTOs Involved**: `UserRequestDTO`, `UserResponseDTO`

**Request Payload (`UserRequestDTO`)**
| Field | Type | Required | Description |
| :--- | :--- | :---: | :--- |
| `username` | String | Yes | Updated username (3-20 characters) |
| `email` | String | Yes | Updated valid email address |
| `password` | String | Yes | Updated password (min 8 characters) |
| `phoneNumber`| String | No | Updated contact phone number |

**Response Payload**: `UserResponseDTO`

### 2.6 Delete User
Remove a user from the system.

- **Endpoint**: `DELETE /users/{id}`
- **Access Control**: Resource Owner (or Admin)
- **Usage Summary**: Soft or hard deletes the specified user account. Returns the deleted user information.
- **DTOs Involved**: `UserResponseDTO`

**Response Payload**: `UserResponseDTO` (State prior to deletion)

---

## 3. Bug Endpoints

### 3.1 Create Bug
Submit a new bug report.

- **Endpoint**: `POST /bugs`
- **Access Control**: Authenticated User
- **Usage Summary**: Registers a new bug report in the system.
- **DTOs Involved**: `CreateBugRequest`, `BugResponse`

**Request Payload (`CreateBugRequest`)**
| Field | Type | Required | Description |
| :--- | :--- | :---: | :--- |
| `title` | String | Yes | Short summary of the bug |
| `text` | String | Yes | Detailed description |
| `pictureUrl` | String | No | Direct URL to an uploaded screenshot/image |
| `status` | String | Yes | Initial status (e.g., "OPEN") |
| `authorId` | Long | No | Explicit author assignment (may be inferred from session context) |
| `tags` | Set<String> | No | Keyword tags associated with the bug |

**Response Payload (`BugResponse`)** *(See Appendix B for definition)*

### 3.2 Get All Bugs
Retrieve a list of all bug reports.

- **Endpoint**: `GET /bugs`
- **Access Control**: Authenticated User
- **Usage Summary**: Fetches all bug reports.
- **DTOs Involved**: `BugResponse`

**Response Payload**: List of `BugResponse`

### 3.3 Get Bug by ID
Retrieve detailed information about a specific bug.

- **Endpoint**: `GET /bugs/{id}`
- **Access Control**: Authenticated User
- **Usage Summary**: Fetches the bug details. Includes an optional `userId` parameter to personalize the response (e.g., to indicate if the requesting user has voted on the bug).
- **Query Parameters**:
  - `userId` (Long, Optional): The ID of the currently viewing user to determine their `userVote` state.
- **DTOs Involved**: `BugResponse`

**Response Payload**: `BugResponse`

### 3.4 Update Bug
Modify an existing bug report.

- **Endpoint**: `PUT /bugs/{id}`
- **Access Control**: Resource Owner or Admin
- **Usage Summary**: Updates the fields of a specific bug.
- **DTOs Involved**: `UpdateBugRequest`, `BugResponse`

**Request Payload (`UpdateBugRequest`)**
| Field | Type | Required | Description |
| :--- | :--- | :---: | :--- |
| `title` | String | No | Updated summary |
| `text` | String | No | Updated detailed description |
| `pictureUrl` | String | No | Updated image URL |
| `status` | String | No | Updated workflow status |

**Response Payload**: `BugResponse`

### 3.5 Delete Bug
Remove a bug report from the system.

- **Endpoint**: `DELETE /bugs/{id}`
- **Access Control**: Resource Owner or Admin
- **Usage Summary**: Deletes the specified bug.
- **DTOs Involved**: None

**Response Payload**: Empty Body (204 No Content)

---

## 4. Comment Endpoints

### 4.1 Create Comment
Add a comment to a specific bug report.

- **Endpoint**: `POST /bugs/{bugId}/comments`
- **Access Control**: Authenticated User
- **Usage Summary**: Attaches a new comment to the given bug.
- **DTOs Involved**: `CreateCommentRequest`, `CommentResponse`

**Request Payload (`CreateCommentRequest`)**
| Field | Type | Required | Description |
| :--- | :--- | :---: | :--- |
| `text` | String | Yes | Content of the comment |
| `pictureUrl` | String | No | Direct URL to an uploaded supporting image |
| `authorId` | Long | Yes | ID of the user submitting the comment |

**Response Payload (`CommentResponse`)** *(See Appendix C for definition)*

### 4.2 Get Comments by Bug ID
Retrieve all comments associated with a specific bug.

- **Endpoint**: `GET /bugs/{bugId}/comments`
- **Access Control**: Authenticated User
- **Usage Summary**: Fetches comments for a bug. Includes an optional `userId` parameter to personalize the `userVote` field in the response.
- **Query Parameters**:
  - `userId` (Long, Optional): The ID of the currently viewing user to determine their `userVote` state.
- **DTOs Involved**: `CommentResponse`

**Response Payload**: List of `CommentResponse`

### 4.3 Get Comment by ID
Retrieve details of a specific comment.

- **Endpoint**: `GET /comments/{id}`
- **Access Control**: Authenticated User
- **Usage Summary**: Fetches details for a single comment.
- **DTOs Involved**: `CommentResponse`

**Response Payload**: `CommentResponse`

### 4.4 Update Comment
Modify an existing comment.

- **Endpoint**: `PUT /comments/{id}`
- **Access Control**: Resource Owner or Admin
- **Usage Summary**: Updates the text or attached resource of a specific comment.
- **DTOs Involved**: `UpdateCommentRequest`, `CommentResponse`

**Request Payload (`UpdateCommentRequest`)**
| Field | Type | Required | Description |
| :--- | :--- | :---: | :--- |
| `text` | String | No | Updated comment content |
| `pictureUrl` | String | No | Updated attached image URL |

**Response Payload**: `CommentResponse`

### 4.5 Delete Comment
Remove a comment from the system.

- **Endpoint**: `DELETE /comments/{id}`
- **Access Control**: Resource Owner or Admin
- **Usage Summary**: Deletes the specified comment.
- **DTOs Involved**: None

**Response Payload**: Empty Body (204 No Content)

---

## 5. Vote Endpoints

### 5.1 Vote on a Bug
Cast or modify a vote on a bug report.

- **Endpoint**: `POST /bugs/{bugId}/votes`
- **Access Control**: Authenticated User
- **Usage Summary**: Creates a new vote or changes an existing vote (e.g., from UPVOTE to DOWNVOTE) for a bug. Returns `204 No Content` if the operation resulted in the vote being removed (e.g. toggling off).
- **DTOs Involved**: `VoteRequest`, `VoteResponse`

**Request Payload (`VoteRequest`)**
| Field | Type | Required | Description |
| :--- | :--- | :---: | :--- |
| `userId` | Long | Yes | ID of the user casting the vote |
| `voteType` | String | Yes | Must be "UPVOTE" or "DOWNVOTE" |

**Response Payload (`VoteResponse`)** *(See Appendix D for definition)*

### 5.2 Vote on a Comment
Cast or modify a vote on a comment.

- **Endpoint**: `POST /comments/{commentId}/votes`
- **Access Control**: Authenticated User
- **Usage Summary**: Creates a new vote or changes an existing vote for a comment. Returns `204 No Content` if the vote is removed.
- **DTOs Involved**: `VoteRequest`, `VoteResponse`

**Request Payload (`VoteRequest`)**
*Identical to Bug Vote Payload*

**Response Payload (`VoteResponse`)** *(See Appendix D for definition)*

### 5.3 Remove Bug Vote
Explicitly delete a user's vote on a bug.

- **Endpoint**: `DELETE /bugs/{bugId}/votes/{voteId}`
- **Access Control**: Resource Owner (The voter)
- **Usage Summary**: Completely removes a previously cast vote record.
- **DTOs Involved**: None

**Response Payload**: Empty Body (204 No Content)

### 5.4 Remove Comment Vote
Explicitly delete a user's vote on a comment.

- **Endpoint**: `DELETE /comments/{commentId}/votes/{voteId}`
- **Access Control**: Resource Owner (The voter)
- **Usage Summary**: Completely removes a previously cast vote record.
- **DTOs Involved**: None

**Response Payload**: Empty Body (204 No Content)

---

## Appendix: Shared Response DTO Definitions

### A. `UserResponseDTO`
Returned when a user is requested or updated.
| Field | Type | Description |
| :--- | :--- | :--- |
| `id` | Long | Unique identifier |
| `username` | String | Display name |
| `email` | String | Email address |
| `phoneNumber`| String | Contact number |
| `score` | double | Aggregated user reputation score |
| `role` | UserRole | Enum representing authority (e.g., USER, ADMIN) |

### B. `BugResponse`
Returned when bug details are requested.
| Field | Type | Description |
| :--- | :--- | :--- |
| `id` | Long | Unique identifier |
| `title` | String | Short summary |
| `text` | String | Detailed description |
| `creationDate`| LocalDateTime | Timestamp of creation |
| `pictureUrl` | String | Attached image URL |
| `status` | String | Current workflow status |
| `authorId` | Long | Author's user ID |
| `authorUsername`| String | Author's display name |
| `authorScore` | Double | Cumulative score of the author |
| `voteScore` | Integer | Aggregate score (upvotes minus downvotes) |
| `userVote` | String | The requesting user's vote ("UPVOTE", "DOWNVOTE", or null) |
| `comments` | List<CommentResponse> | Attached comments |
| `tags` | Set<String> | Associated categorical tags |

### C. `CommentResponse`
Returned when comments are requested.
| Field | Type | Description |
| :--- | :--- | :--- |
| `id` | Long | Unique identifier |
| `text` | String | Content |
| `pictureUrl` | String | Attached image URL |
| `creationDate`| LocalDateTime | Timestamp of creation |
| `authorId` | Long | Author's user ID |
| `authorUsername`| String | Author's display name |
| `authorScore` | Double | Cumulative score of the author |
| `voteScore` | Integer | Aggregate score (upvotes minus downvotes) |
| `userVote` | String | The requesting user's vote ("UPVOTE", "DOWNVOTE", or null) |
| `bugId` | Long | The ID of the bug this comment belongs to |

### D. `VoteResponse`
Returned when a successful vote is cast.
| Field | Type | Description |
| :--- | :--- | :--- |
| `id` | Long | Unique identifier for the vote record |
| `targetType` | String | Specifies "BUG" or "COMMENT" |
| `targetId` | Long | ID of the target bug or comment |
| `userId` | Long | ID of the user who cast the vote |
| `voteType` | String | The type of vote cast ("UPVOTE" or "DOWNVOTE") |
