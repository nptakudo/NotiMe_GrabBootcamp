DROP TABLE IF EXISTS list_sharing;
DROP TABLE IF EXISTS list_post;
DROP TABLE IF EXISTS reading_list;
DROP TABLE IF EXISTS subscription;
DROP TABLE IF EXISTS post;
DROP TABLE IF EXISTS source;
DROP TABLE IF EXISTS "user";

CREATE TABLE "user"
(
    id         SERIAL PRIMARY KEY,
    username   VARCHAR(255) UNIQUE NOT NULL,
    "password" VARCHAR(255)        NOT NULL
);

CREATE TABLE source
(
    id     SERIAL PRIMARY KEY,
    "name" VARCHAR(255)        NOT NULL,
    "url"  VARCHAR(255) UNIQUE NOT NULL,
    avatar VARCHAR(255) -- consider how to store the image
);

CREATE TABLE post
(
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(255)        NOT NULL,
    publish_date TIMESTAMP           NOT NULL,
    "url"        VARCHAR(255) UNIQUE NOT NULL,
    raw_text     TEXT                NOT NULL,
    source_id    INTEGER REFERENCES source (id) ON DELETE CASCADE -- delete post when source is deleted
    -- consider about the image of post to show on the top
);

CREATE TABLE subscription
(
    user_id   INTEGER REFERENCES "user" (id) ON DELETE CASCADE,
    source_id INTEGER REFERENCES source (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, source_id)
);

CREATE TABLE reading_list
(
    id        SERIAL PRIMARY KEY,
    list_name VARCHAR(255) NOT NULL,
    "owner"   INTEGER REFERENCES "user" (id) ON DELETE CASCADE,
    is_saved  BOOLEAN      NOT NULL -- stands for saved post list, the other list will be create with name
);

CREATE TABLE list_post
(
    list_id INTEGER REFERENCES reading_list (id) ON DELETE CASCADE,
    post_id BIGINT REFERENCES post (id) ON DELETE CASCADE,
    PRIMARY KEY (list_id, post_id)
);

CREATE TABLE list_sharing
(
    list_id INTEGER REFERENCES reading_list (id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES "user" (id) ON DELETE CASCADE,
    PRIMARY KEY (list_id, user_id)
);