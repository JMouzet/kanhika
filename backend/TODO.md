# API:
## Auth
- <span style="color: green;">POST /api/auth/register <= Create an account, return Bearer token</span>
- <span style="color: green;">POST /api/auth/login <= Log into an account, return Bearer token</span>

## Users
- <span style="color: green;">GET /api/users/get/{username} <= Get info on a user, return username, bio, exp, flame, role, joined date ... if not disabled</span>
- <span style="color: green;">GET /api/users/me <= Return self user info that shows in settings</span>
- <span style="color: green;">PATCH /api/users/me/bio <= Change biography</span>
- <span style="color: green;">PATCH /api/users/me/username <= Change username</span>
- <span style="color: green;">PATCH /api/users/me/email <= Change email</span>
- <span style="color: green;">PATCH /api/users/me/password <= Change password, old and new passwords required</span>
- <span style="color: green;">DELETE /api/users/me <= Disable (delete) its own account</span>
- <span style="color: green;">GET /api/users/followers <= Get accounts following you</span>
- <span style="color: green;">GET /api/users/following <= Get the accounts you follow</span>
- <span style="color: green;">POST /api/users/follow/{username} <= Follow a user</span>
- <span style="color: green;">DELETE /api/users/follow/{username} <= Unfollow a user</span>
- <span style="color: green;">GET /api/users/block <= Block the accounts you follow</span>
- <span style="color: green;">POST /api/users/block/{username} <= Block a user</span>
- <span style="color: green;">DELETE /api/users/block/{username} <= Unblock a user</span>
- <span style="color: green;">GET /api/users/search/{input} <= Return a list of users matching the search input</span>

### Admin:
- DELETE /api/admin/users/ban/{username} <= Ban an account

## Kanjis
- <span style="color: green;">GET /api/kanjis/{kanji} <= Get info on a kanji</span>
- <span style="color: green;">GET /api/kanjis/grade/{level} <= Get all kanjis with the matching grade level</span>
- <span style="color: green;">GET /api/kanjis/jlpt/{level} <= Get all kanjis with the matching jlpt level</span>
- <span style="color: green;">GET /api/kanjis/search/{input} <= Return a list of kanji matching the search input. Can be a kanji, a reading or a meaning</span>
- <span style="color: green;">GET /api/kanjis/{kanji}/comments <= Get comments on a kanji page</span>
- <span style="color: green;">POST /api/kanjis/{kanji}/comments <= Send a comment on a kanji page</span>

## Comments
- <span style="color: green;">PATCH /api/comments/{id} <= Edit a comment, check the Bearer token</span>
- <span style="color: green;">DELETE /api/comments/{id} <= Remove a comment, check the Bearer token</span>
- <span style="color: green;">POST /api/comments/{id}/vote/up <= Send an upvote (like), replace if a vote already exists</span>
- <span style="color: green;">POST /api/comments/{id}/vote/down <= Send a downvote (dislike), replace if a vote already exists</span>
- <span style="color: green;">DELETE /api/comments/{id}/vote <= Remove a vote</span>

### Admin:
- DELETE /api/admin/comments/{id} <= Remove any comment, check for ADMIN role

## Conversations
- GET /api/conversations/ <= Get the user's conversations sorted by latest with a preview of the latest message for each
- GET /api/conversations/users/{username} <= Get the conversation with the specified user, set all messages as read
- GET /api/conversations/unread <= Get the number of unread messages
- POST /api/messages <= Send a message
- PATCH /api/messages/{id} <= Edit a message, check the Bearer token
- DELETE /api/messages/{id} <= Delete a message, check the Bearer token (no ADMIN actions possible here)

## Quizzes
- GET /api/quizzes <= Get a potentially uncleared quiz
- POST /api/quizzes <= Generate a quiz, return the quiz id and the first question
- GET /api/quizzes/{id}/questions <= Get the next question
- POST /api/quizzes/{id}/questions/{id} <= Send the answer, return the result and change the score statistics


# Debug cleaning
- Remove exception message in the default exception handler
- Run project in prod before pushing to main