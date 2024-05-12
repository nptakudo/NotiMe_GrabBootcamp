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
WHERE id = $1;

-- name: GetArticlesByPublisherId :many
-- params: publisherId: number, limit: number, offset: number
-- behavior: sorted by publish_date desc
SELECT *
FROM post
WHERE source_id = $1
ORDER BY publish_date DESC
LIMIT $2 OFFSET $3;

-- name: SearchArticlesByName :many
-- params: name: string, limit: number, offset: number
-- behavior: sorted by publish_date desc
SELECT *
FROM post
WHERE title ILIKE '%' || @query || '%'
ORDER BY publish_date DESC
LIMIT $1 OFFSET $2;

-- name: CreateArticle :one
INSERT INTO post (title, publish_date, url, source_id)
VALUES ($1, $2, $3, $4)
RETURNING post.*;


-------------------------------------------------
-- BOOKMARK LIST REPOSITORY
-------------------------------------------------

-- name: GetBookmarkListById :one
SELECT *
FROM reading_list
WHERE id = $1;

-- name: GetBookmarkListsOwnByUserId :many
SELECT *
FROM reading_list
WHERE owner = $1;

-- name: GetBookmarkListsSharedWithUserId :many
SELECT DISTINCT reading_list.*
FROM reading_list
         JOIN list_sharing ON reading_list.id = list_sharing.list_id
WHERE list_sharing.user_id = $1;

-- name: GetArticlesInBookmarkList :many
SELECT post.*
FROM post
         JOIN list_post ON post.id = list_post.post_id
WHERE list_post.list_id = $1;

-- name: IsArticleInBookmarkList :one
SELECT *
FROM list_post
WHERE list_id = $1
  AND post_id = $2;

-- name: AddArticleToBookmarkList :exec
INSERT INTO list_post (list_id, post_id)
VALUES ($1, $2);

-- name: RemoveArticleFromBookmarkList :exec
DELETE
FROM list_post
WHERE list_id = $1
  AND post_id = $2;

-- name: CreateBookmarkList :one
INSERT INTO reading_list (list_name, owner, is_saved)
VALUES ($1, $2, $3)
RETURNING *;

-- name: DeleteBookmarkList :one
DELETE
FROM reading_list
WHERE id = $1
RETURNING *;


-------------------------------------------------
-- PUBLISHER REPOSITORY
-------------------------------------------------

-- name: GetPublisherById :one
SELECT *
FROM source
WHERE id = $1;

-- name: SearchPublishersByName :many
SELECT *
FROM source
WHERE name ILIKE '%' || $1 || '%';

-- name: CreatePublisher :one
INSERT INTO source (name, url, avatar)
VALUES ($1, $2, $3)
RETURNING source.*;


-------------------------------------------------
-- SUBSCRIBE LIST REPOSITORY
-------------------------------------------------

-- name: GetSubscribedPublishersByUserId :many
SELECT DISTINCT source.*
FROM source
         JOIN subscription ON source.id = subscription.source_id
WHERE subscription.user_id = $1;

-- name: IsPublisherSubscribedByUserId :one
SELECT *
FROM subscription
WHERE user_id = $1
  AND source_id = $2;

-- name: SubscribePublisher :exec
INSERT INTO subscription (user_id, source_id)
VALUES ($1, $2);

-- name: UnsubscribePublisher :exec
DELETE
FROM subscription
WHERE user_id = $1
  AND source_id = $2;