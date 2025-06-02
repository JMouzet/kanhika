# API:
## Auth
- <span style="color: green;">POST /api/auth/register <- Create an account, return Bearer token</span>
- <span style="color: green;">POST /api/auth/login <- Log into an account, return Bearer token</span>

## Users
- GET /api/users/get/{username} <- Get info on a user, return id, username, exp, flame, *role*, *joined date* ... if not disabled
- GET /api/users/me <- Return user info using the Bearer token
- PATCH /api/users/me <- Edit profile
- PATCH /api/users/me/password <- Change password, old and new passwords required
- DELETE /api/users/me <- Disable (delete) its own account (beware of unique email field !)
- POST /api/users/follow/{username} <- Follow a user
- DELETE /api/users/follow/{username} <- Unfollow a user
- POST /api/users/block/{username} <- Block a user
- DELETE /api/users/block/{username} <- Unblock a user

- (?) DELETE /api/users/{username} <- ADMIN ONLY: Disable someone else account

## Kanjis
- GET /api/kanjis/{kanji} <- Get info on a kanji
- GET /api/kanjis/grade/{level} <- Get all kanjis with the matching grade level
- GET /api/kanjis/jlpt/{level} <- Get all kanjis with the matching jlpt level
- GET /api/kanjis/search/{input} <- Return a list of kanji matching the search input. Can be a kanji, a reading or a meaning
- GET /api/kanjis/{kanji}/comments <- Get comments on a kanji page
- POST /api/kanjis/{kanji}/comments <- Send a comment on a kanji page

## Comments
- PATCH /api/comments/{id} <- Edit a comment, check the Bearer token
- DELETE /api/comments/{id} <- Remove a comment, check the Bearer token unless ADMIN (?)
- POST /api/comments/{id}/vote/up <- Send an upvote (like), replace if a vote already exists
- POST /api/comments/{id}/vote/down <- Send a downvote (dislike), replace if a vote already exists
- DELETE /api/comments/{id}/vote <- Remove a vote

## Conversations
- GET /api/conversations/ <- Get the user's conversations sorted by latest with a preview of the latest message for each
- GET /api/conversations/users/{username} <- Get the conversation with the specified user, set all messages as read
- GET /api/conversations/unread <- Get the number of unread messages
- POST /api/messages <- Send a message
- PATCH /api/messages/{id} <- Edit a message, check the Bearer token
- DELETE /api/messages/{id} <- Delete a message, check the Bearer token (no ADMIN actions possible here)

## Quizzes
- GET /api/quizzes <- Get a potiental uncleared quiz
- POST /api/quizzes <- Generate a quiz, return the quiz id and the first question
- GET /api/quizzes/{id}/questions <- Get the next question
- POST /api/quizzes/{id}/questions/{id} <- Send the answer, return the result and change the score statistics
