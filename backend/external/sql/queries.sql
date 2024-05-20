-- -- DROP SCHEMA public CASCADE;
-- -- CREATE SCHEMA public;
-- -- GRANT ALL ON SCHEMA public TO postgres;
-- -- GRANT ALL ON SCHEMA public TO public;
--
-- CREATE TABLE "user" (
--                         id SERIAL PRIMARY KEY,
--                         username VARCHAR(255) UNIQUE NOT NULL,
--                         "password" VARCHAR(255) NOT NULL
-- );
--
-- CREATE TABLE source (
--                         id SERIAL PRIMARY KEY,
--                         "name" VARCHAR(255) NOT NULL,
--                         "url" VARCHAR(255) NOT NULL,
--                         avatar VARCHAR(255) NOT NULL -- consider how to store the image
-- );
--
-- CREATE TABLE post (
--                       id BIGSERIAL PRIMARY KEY,
--                       title VARCHAR(255) NOT NULL,
--                       publish_date BIGINT,
--                       "url" VARCHAR(255) NOT NULL,
--                       source_id INTEGER REFERENCES source (id) ON DELETE CASCADE -- delete post when source is deleted
--     -- consider about the image of post to show on the top
-- );
--
-- CREATE TABLE subscription (
--                               user_id INTEGER REFERENCES "user" (id) ON DELETE CASCADE,
--                               source_id INTEGER REFERENCES source (id) ON DELETE CASCADE,
--                               PRIMARY KEY (user_id, source_id)
-- );
--
-- CREATE TABLE reading_list (
--                               id SERIAL PRIMARY KEY,
--                               list_name VARCHAR(255) NOT NULL,
--                               "owner" INTEGER REFERENCES "user" (id) ON DELETE CASCADE,
--                               is_saved BOOLEAN NOT NULL -- stands for saved post list, the other list will be create with name
-- );
--
-- CREATE TABLE list_post (
--                            list_id INTEGER REFERENCES reading_list (id) ON DELETE CASCADE,
--                            post_id BIGINT REFERENCES post(id) ON DELETE CASCADE,
--                            PRIMARY KEY (list_id, post_id)
-- );
--
-- CREATE TABLE list_sharing (
--                               list_id INTEGER REFERENCES reading_list (id) ON DELETE CASCADE,
--                               user_id INTEGER REFERENCES "user" (id) ON DELETE CASCADE,
--                               PRIMARY KEY (list_id, user_id)
-- );

-------------------------------------------------
-- ARTICLE REPOSITORY
-------------------------------------------------

-- name: GetArticleById :one
SELECT *
FROM post
WHERE id = @id;

-- name: GetArticleByUrl :one
SELECT *
FROM post
WHERE url = @url;

-- name: GetArticlesByPublisherId :many
-- params: publisherId: number, limit: number, offset: number
-- behavior: sorted by publish_date desc
SELECT *
FROM post
WHERE source_id = @publisher_id
ORDER BY publish_date DESC
LIMIT @count OFFSET $1;

-- name: GetArticlesFromSubscribedPublishers :many
-- params: userId: number, limit: number, offset: number
-- behavior: sorted by publish_date desc
SELECT post.*
FROM post
         JOIN subscription ON post.source_id = subscription.source_id
WHERE subscription.user_id = @user_id
ORDER BY publish_date DESC
LIMIT @count OFFSET $1;

-- name: GetAllArticles :many
-- behavior: sorted by publish_date desc
SELECT *
FROM post
ORDER BY publish_date DESC;

-- name: SearchArticlesByName :many
-- params: query: string, limit: number, offset: number
-- behavior: sorted by publish_date desc
SELECT *
FROM post
WHERE title ILIKE '%' || @query || '%'
ORDER BY publish_date DESC
LIMIT @count OFFSET $1;

-- name: CreateArticle :one
INSERT INTO post (title, publish_date, url, source_id)
VALUES (@title, @publish_date, @url, @publisher_id)
RETURNING post.*;


-------------------------------------------------
-- BOOKMARK LIST REPOSITORY
-------------------------------------------------

-- name: GetBookmarkListById :one
SELECT *
FROM reading_list
WHERE id = @id;

-- name: GetBookmarkListsOwnByUserId :many
SELECT *
FROM reading_list
WHERE owner = @owner_id;

-- name: GetBookmarkListsSharedWithUserId :many
SELECT DISTINCT reading_list.*
FROM reading_list
         JOIN list_sharing ON reading_list.id = list_sharing.list_id
WHERE list_sharing.user_id = @user_id;

-- name: GetArticlesInBookmarkList :many
SELECT post.*
FROM post
         JOIN list_post ON post.id = list_post.post_id
WHERE list_post.list_id = @list_id;

-- name: IsArticleInBookmarkList :one
SELECT *
FROM list_post
WHERE list_id = @list_id
  AND post_id = @article_id;

-- name: IsArticleInAnyBookmarkList :one
-- params: articleId: number, userId: number
-- behavior: check if the article is in any bookmark list that the user owns, or in any shared list with the user
SELECT list_post.*
FROM list_post
WHERE list_post.post_id = @article_id AND EXISTS (SELECT 1
                                                  FROM reading_list
                                                  WHERE reading_list.id = list_post.list_id
                                                    AND reading_list.owner = @user_id)
   OR EXISTS (SELECT 1
              FROM list_sharing
              WHERE list_sharing.list_id = list_post.list_id
                AND list_sharing.user_id = @user_id);

-- name: AddArticleToBookmarkList :exec
INSERT INTO list_post (list_id, post_id)
VALUES (@list_id, @article_id);

-- name: RemoveArticleFromBookmarkList :exec
DELETE
FROM list_post
WHERE list_id = @list_id
  AND post_id = @article_id;

-- name: CreateBookmarkList :one
INSERT INTO reading_list (list_name, owner, is_saved)
VALUES (@name, @owner_id, @is_saved)
RETURNING *;

-- name: DeleteBookmarkList :one
DELETE
FROM reading_list
WHERE id = @id
RETURNING *;


-------------------------------------------------
-- PUBLISHER REPOSITORY
-------------------------------------------------

-- name: GetPublisherById :one
SELECT *
FROM source
WHERE id = @id;

-- name: SearchPublishersByUrl :many
SELECT *
FROM source
WHERE url ILIKE '%' || @query || '%';

-- name: SearchPublishersByName :many
SELECT *
FROM source
WHERE name ILIKE '%' || @query || '%';

-- name: CreatePublisher :one
INSERT INTO source (name, url, avatar)
VALUES (@name, @url, @avatar_url)
RETURNING source.*;


-------------------------------------------------
-- SUBSCRIBE LIST REPOSITORY
-------------------------------------------------

-- name: GetSubscribedPublishersByUserId :many
SELECT DISTINCT source.*
FROM source
         JOIN subscription ON source.id = subscription.source_id
WHERE subscription.user_id = @user_id;

-- name: IsPublisherSubscribedByUserId :one
SELECT *
FROM subscription
WHERE user_id = @user_id
  AND source_id = @publisher_id;

-- name: SubscribePublisher :exec
INSERT INTO subscription (user_id, source_id)
VALUES (@user_id, @publisher_id);

-- name: UnsubscribePublisher :exec
DELETE
FROM subscription
WHERE user_id = @user_id
  AND source_id = @publisher_id;

-------------------------------------------------
-- USER REPOSITORY
-------------------------------------------------

-- name: GetUserByUsername :one
SELECT * FROM "user"
WHERE username = @username;