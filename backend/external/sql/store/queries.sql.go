// Code generated by sqlc. DO NOT EDIT.
// versions:
//   sqlc v1.20.0
// source: queries.sql

package store

import (
	"context"
	"database/sql"
	"time"
)

const addArticleToBookmarkList = `-- name: AddArticleToBookmarkList :exec
INSERT INTO list_post (list_id, post_id)
VALUES ($1, $2)
`

type AddArticleToBookmarkListParams struct {
	ListID    int32 `json:"list_id"`
	ArticleID int64 `json:"article_id"`
}

func (q *Queries) AddArticleToBookmarkList(ctx context.Context, arg AddArticleToBookmarkListParams) error {
	_, err := q.db.Exec(ctx, addArticleToBookmarkList, arg.ListID, arg.ArticleID)
	return err
}

const createArticle = `-- name: CreateArticle :one
INSERT INTO post (title, publish_date, url, source_id)
VALUES ($1, $2, $3, $4)
RETURNING post.id, post.title, post.publish_date, post.url, post.source_id
`

type CreateArticleParams struct {
	Title       string    `json:"title"`
	PublishDate time.Time `json:"publish_date"`
	Url         string    `json:"url"`
	PublisherID int32     `json:"publisher_id"`
}

func (q *Queries) CreateArticle(ctx context.Context, arg CreateArticleParams) (Post, error) {
	row := q.db.QueryRow(ctx, createArticle,
		arg.Title,
		arg.PublishDate,
		arg.Url,
		arg.PublisherID,
	)
	var i Post
	err := row.Scan(
		&i.ID,
		&i.Title,
		&i.PublishDate,
		&i.Url,
		&i.SourceID,
	)
	return i, err
}

const createBookmarkList = `-- name: CreateBookmarkList :one
INSERT INTO reading_list (list_name, owner, is_saved)
VALUES ($1, $2, $3)
RETURNING id, list_name, owner, is_saved
`

type CreateBookmarkListParams struct {
	Name    string `json:"name"`
	OwnerID int32  `json:"owner_id"`
	IsSaved bool   `json:"is_saved"`
}

func (q *Queries) CreateBookmarkList(ctx context.Context, arg CreateBookmarkListParams) (ReadingList, error) {
	row := q.db.QueryRow(ctx, createBookmarkList, arg.Name, arg.OwnerID, arg.IsSaved)
	var i ReadingList
	err := row.Scan(
		&i.ID,
		&i.ListName,
		&i.Owner,
		&i.IsSaved,
	)
	return i, err
}

const createPublisher = `-- name: CreatePublisher :one
INSERT INTO source (name, url, avatar)
VALUES ($1, $2, $3)
RETURNING source.id, source.name, source.url, source.avatar
`

type CreatePublisherParams struct {
	Name      string         `json:"name"`
	Url       string         `json:"url"`
	AvatarUrl sql.NullString `json:"avatar_url"`
}

func (q *Queries) CreatePublisher(ctx context.Context, arg CreatePublisherParams) (Source, error) {
	row := q.db.QueryRow(ctx, createPublisher, arg.Name, arg.Url, arg.AvatarUrl)
	var i Source
	err := row.Scan(
		&i.ID,
		&i.Name,
		&i.Url,
		&i.Avatar,
	)
	return i, err
}

const deleteBookmarkList = `-- name: DeleteBookmarkList :one
DELETE
FROM reading_list
WHERE id = $1
RETURNING id, list_name, owner, is_saved
`

func (q *Queries) DeleteBookmarkList(ctx context.Context, id int32) (ReadingList, error) {
	row := q.db.QueryRow(ctx, deleteBookmarkList, id)
	var i ReadingList
	err := row.Scan(
		&i.ID,
		&i.ListName,
		&i.Owner,
		&i.IsSaved,
	)
	return i, err
}

const getAllArticles = `-- name: GetAllArticles :many
SELECT id, title, publish_date, url, source_id
FROM post
ORDER BY publish_date DESC
`

// behavior: sorted by publish_date desc
func (q *Queries) GetAllArticles(ctx context.Context) ([]Post, error) {
	rows, err := q.db.Query(ctx, getAllArticles)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	items := []Post{}
	for rows.Next() {
		var i Post
		if err := rows.Scan(
			&i.ID,
			&i.Title,
			&i.PublishDate,
			&i.Url,
			&i.SourceID,
		); err != nil {
			return nil, err
		}
		items = append(items, i)
	}
	if err := rows.Err(); err != nil {
		return nil, err
	}
	return items, nil
}

const getArticleById = `-- name: GetArticleById :one


SELECT id, title, publish_date, url, source_id
FROM post
WHERE id = $1
`

// -- DROP SCHEMA public CASCADE;
// -- CREATE SCHEMA public;
// -- GRANT ALL ON SCHEMA public TO postgres;
// -- GRANT ALL ON SCHEMA public TO public;
//
// CREATE TABLE "user" (
//
//	id SERIAL PRIMARY KEY,
//	username VARCHAR(255) UNIQUE NOT NULL,
//	"password" VARCHAR(255) NOT NULL
//
// );
//
// CREATE TABLE source (
//
//	id SERIAL PRIMARY KEY,
//	"name" VARCHAR(255) NOT NULL,
//	"url" VARCHAR(255) NOT NULL,
//	avatar VARCHAR(255) NOT NULL -- consider how to store the image
//
// );
//
// CREATE TABLE post (
//
//	                  id BIGSERIAL PRIMARY KEY,
//	                  title VARCHAR(255) NOT NULL,
//	                  publish_date BIGINT,
//	                  "url" VARCHAR(255) NOT NULL,
//	                  source_id INTEGER REFERENCES source (id) ON DELETE CASCADE -- delete post when source is deleted
//	-- consider about the image of post to show on the top
//
// );
//
// CREATE TABLE subscription (
//
//	user_id INTEGER REFERENCES "user" (id) ON DELETE CASCADE,
//	source_id INTEGER REFERENCES source (id) ON DELETE CASCADE,
//	PRIMARY KEY (user_id, source_id)
//
// );
//
// CREATE TABLE reading_list (
//
//	id SERIAL PRIMARY KEY,
//	list_name VARCHAR(255) NOT NULL,
//	"owner" INTEGER REFERENCES "user" (id) ON DELETE CASCADE,
//	is_saved BOOLEAN NOT NULL -- stands for saved post list, the other list will be create with name
//
// );
//
// CREATE TABLE list_post (
//
//	list_id INTEGER REFERENCES reading_list (id) ON DELETE CASCADE,
//	post_id BIGINT REFERENCES post(id) ON DELETE CASCADE,
//	PRIMARY KEY (list_id, post_id)
//
// );
//
// CREATE TABLE list_sharing (
//
//	list_id INTEGER REFERENCES reading_list (id) ON DELETE CASCADE,
//	user_id INTEGER REFERENCES "user" (id) ON DELETE CASCADE,
//	PRIMARY KEY (list_id, user_id)
//
// );
// -----------------------------------------------
// ARTICLE REPOSITORY
// -----------------------------------------------
func (q *Queries) GetArticleById(ctx context.Context, id int64) (Post, error) {
	row := q.db.QueryRow(ctx, getArticleById, id)
	var i Post
	err := row.Scan(
		&i.ID,
		&i.Title,
		&i.PublishDate,
		&i.Url,
		&i.SourceID,
	)
	return i, err
}

const getArticlesByPublisherId = `-- name: GetArticlesByPublisherId :many
SELECT id, title, publish_date, url, source_id
FROM post
WHERE source_id = $2
ORDER BY publish_date DESC
LIMIT $3 OFFSET $1
`

type GetArticlesByPublisherIdParams struct {
	Offset      int32 `json:"offset"`
	PublisherID int32 `json:"publisher_id"`
	Count       int32 `json:"count"`
}

// params: publisherId: number, limit: number, offset: number
// behavior: sorted by publish_date desc
func (q *Queries) GetArticlesByPublisherId(ctx context.Context, arg GetArticlesByPublisherIdParams) ([]Post, error) {
	rows, err := q.db.Query(ctx, getArticlesByPublisherId, arg.Offset, arg.PublisherID, arg.Count)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	items := []Post{}
	for rows.Next() {
		var i Post
		if err := rows.Scan(
			&i.ID,
			&i.Title,
			&i.PublishDate,
			&i.Url,
			&i.SourceID,
		); err != nil {
			return nil, err
		}
		items = append(items, i)
	}
	if err := rows.Err(); err != nil {
		return nil, err
	}
	return items, nil
}

const getArticlesInBookmarkList = `-- name: GetArticlesInBookmarkList :many
SELECT post.id, post.title, post.publish_date, post.url, post.source_id
FROM post
         JOIN list_post ON post.id = list_post.post_id
WHERE list_post.list_id = $1
`

func (q *Queries) GetArticlesInBookmarkList(ctx context.Context, listID int32) ([]Post, error) {
	rows, err := q.db.Query(ctx, getArticlesInBookmarkList, listID)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	items := []Post{}
	for rows.Next() {
		var i Post
		if err := rows.Scan(
			&i.ID,
			&i.Title,
			&i.PublishDate,
			&i.Url,
			&i.SourceID,
		); err != nil {
			return nil, err
		}
		items = append(items, i)
	}
	if err := rows.Err(); err != nil {
		return nil, err
	}
	return items, nil
}

const getBookmarkListById = `-- name: GetBookmarkListById :one

SELECT id, list_name, owner, is_saved
FROM reading_list
WHERE id = $1
`

// -----------------------------------------------
// BOOKMARK LIST REPOSITORY
// -----------------------------------------------
func (q *Queries) GetBookmarkListById(ctx context.Context, id int32) (ReadingList, error) {
	row := q.db.QueryRow(ctx, getBookmarkListById, id)
	var i ReadingList
	err := row.Scan(
		&i.ID,
		&i.ListName,
		&i.Owner,
		&i.IsSaved,
	)
	return i, err
}

const getBookmarkListsOwnByUserId = `-- name: GetBookmarkListsOwnByUserId :many
SELECT id, list_name, owner, is_saved
FROM reading_list
WHERE owner = $1
`

func (q *Queries) GetBookmarkListsOwnByUserId(ctx context.Context, ownerID int32) ([]ReadingList, error) {
	rows, err := q.db.Query(ctx, getBookmarkListsOwnByUserId, ownerID)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	items := []ReadingList{}
	for rows.Next() {
		var i ReadingList
		if err := rows.Scan(
			&i.ID,
			&i.ListName,
			&i.Owner,
			&i.IsSaved,
		); err != nil {
			return nil, err
		}
		items = append(items, i)
	}
	if err := rows.Err(); err != nil {
		return nil, err
	}
	return items, nil
}

const getBookmarkListsSharedWithUserId = `-- name: GetBookmarkListsSharedWithUserId :many
SELECT DISTINCT reading_list.id, reading_list.list_name, reading_list.owner, reading_list.is_saved
FROM reading_list
         JOIN list_sharing ON reading_list.id = list_sharing.list_id
WHERE list_sharing.user_id = $1
`

func (q *Queries) GetBookmarkListsSharedWithUserId(ctx context.Context, userID int32) ([]ReadingList, error) {
	rows, err := q.db.Query(ctx, getBookmarkListsSharedWithUserId, userID)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	items := []ReadingList{}
	for rows.Next() {
		var i ReadingList
		if err := rows.Scan(
			&i.ID,
			&i.ListName,
			&i.Owner,
			&i.IsSaved,
		); err != nil {
			return nil, err
		}
		items = append(items, i)
	}
	if err := rows.Err(); err != nil {
		return nil, err
	}
	return items, nil
}

const getPublisherById = `-- name: GetPublisherById :one

SELECT id, name, url, avatar
FROM source
WHERE id = $1
`

// -----------------------------------------------
// PUBLISHER REPOSITORY
// -----------------------------------------------
func (q *Queries) GetPublisherById(ctx context.Context, id int32) (Source, error) {
	row := q.db.QueryRow(ctx, getPublisherById, id)
	var i Source
	err := row.Scan(
		&i.ID,
		&i.Name,
		&i.Url,
		&i.Avatar,
	)
	return i, err
}

const getSubscribedPublishersByUserId = `-- name: GetSubscribedPublishersByUserId :many

SELECT DISTINCT source.id, source.name, source.url, source.avatar
FROM source
         JOIN subscription ON source.id = subscription.source_id
WHERE subscription.user_id = $1
`

// -----------------------------------------------
// SUBSCRIBE LIST REPOSITORY
// -----------------------------------------------
func (q *Queries) GetSubscribedPublishersByUserId(ctx context.Context, userID int32) ([]Source, error) {
	rows, err := q.db.Query(ctx, getSubscribedPublishersByUserId, userID)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	items := []Source{}
	for rows.Next() {
		var i Source
		if err := rows.Scan(
			&i.ID,
			&i.Name,
			&i.Url,
			&i.Avatar,
		); err != nil {
			return nil, err
		}
		items = append(items, i)
	}
	if err := rows.Err(); err != nil {
		return nil, err
	}
	return items, nil
}

const isArticleInAnyBookmarkList = `-- name: IsArticleInAnyBookmarkList :one
SELECT list_post.list_id, list_post.post_id
FROM list_post
WHERE list_post.post_id = $1 AND EXISTS (
    SELECT 1
    FROM reading_list
    WHERE reading_list.id = list_post.list_id
      AND reading_list.owner = $2
)
   OR EXISTS (
    SELECT 1
    FROM list_sharing
    WHERE list_sharing.list_id = list_post.list_id
      AND list_sharing.user_id = $2
)
`

type IsArticleInAnyBookmarkListParams struct {
	ArticleID int64 `json:"article_id"`
	UserID    int32 `json:"user_id"`
}

// params: articleId: number, userId: number
// behavior: check if the article is in any bookmark list that the user owns, or in any shared list with the user
func (q *Queries) IsArticleInAnyBookmarkList(ctx context.Context, arg IsArticleInAnyBookmarkListParams) (ListPost, error) {
	row := q.db.QueryRow(ctx, isArticleInAnyBookmarkList, arg.ArticleID, arg.UserID)
	var i ListPost
	err := row.Scan(&i.ListID, &i.PostID)
	return i, err
}

const isArticleInBookmarkList = `-- name: IsArticleInBookmarkList :one
SELECT list_id, post_id
FROM list_post
WHERE list_id = $1
  AND post_id = $2
`

type IsArticleInBookmarkListParams struct {
	ListID    int32 `json:"list_id"`
	ArticleID int64 `json:"article_id"`
}

func (q *Queries) IsArticleInBookmarkList(ctx context.Context, arg IsArticleInBookmarkListParams) (ListPost, error) {
	row := q.db.QueryRow(ctx, isArticleInBookmarkList, arg.ListID, arg.ArticleID)
	var i ListPost
	err := row.Scan(&i.ListID, &i.PostID)
	return i, err
}

const isPublisherSubscribedByUserId = `-- name: IsPublisherSubscribedByUserId :one
SELECT user_id, source_id
FROM subscription
WHERE user_id = $1
  AND source_id = $2
`

type IsPublisherSubscribedByUserIdParams struct {
	UserID      int32 `json:"user_id"`
	PublisherID int32 `json:"publisher_id"`
}

func (q *Queries) IsPublisherSubscribedByUserId(ctx context.Context, arg IsPublisherSubscribedByUserIdParams) (Subscription, error) {
	row := q.db.QueryRow(ctx, isPublisherSubscribedByUserId, arg.UserID, arg.PublisherID)
	var i Subscription
	err := row.Scan(&i.UserID, &i.SourceID)
	return i, err
}

const removeArticleFromBookmarkList = `-- name: RemoveArticleFromBookmarkList :exec
DELETE
FROM list_post
WHERE list_id = $1
  AND post_id = $2
`

type RemoveArticleFromBookmarkListParams struct {
	ListID    int32 `json:"list_id"`
	ArticleID int64 `json:"article_id"`
}

func (q *Queries) RemoveArticleFromBookmarkList(ctx context.Context, arg RemoveArticleFromBookmarkListParams) error {
	_, err := q.db.Exec(ctx, removeArticleFromBookmarkList, arg.ListID, arg.ArticleID)
	return err
}

const searchArticlesByName = `-- name: SearchArticlesByName :many
SELECT id, title, publish_date, url, source_id
FROM post
WHERE title ILIKE '%' || $2 || '%'
ORDER BY publish_date DESC
LIMIT $3 OFFSET $1
`

type SearchArticlesByNameParams struct {
	Offset int32          `json:"offset"`
	Query  sql.NullString `json:"query"`
	Count  int32          `json:"count"`
}

// params: query: string, limit: number, offset: number
// behavior: sorted by publish_date desc
func (q *Queries) SearchArticlesByName(ctx context.Context, arg SearchArticlesByNameParams) ([]Post, error) {
	rows, err := q.db.Query(ctx, searchArticlesByName, arg.Offset, arg.Query, arg.Count)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	items := []Post{}
	for rows.Next() {
		var i Post
		if err := rows.Scan(
			&i.ID,
			&i.Title,
			&i.PublishDate,
			&i.Url,
			&i.SourceID,
		); err != nil {
			return nil, err
		}
		items = append(items, i)
	}
	if err := rows.Err(); err != nil {
		return nil, err
	}
	return items, nil
}

const searchPublishersByName = `-- name: SearchPublishersByName :many
SELECT id, name, url, avatar
FROM source
WHERE name ILIKE '%' || $1 || '%'
`

func (q *Queries) SearchPublishersByName(ctx context.Context, query sql.NullString) ([]Source, error) {
	rows, err := q.db.Query(ctx, searchPublishersByName, query)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	items := []Source{}
	for rows.Next() {
		var i Source
		if err := rows.Scan(
			&i.ID,
			&i.Name,
			&i.Url,
			&i.Avatar,
		); err != nil {
			return nil, err
		}
		items = append(items, i)
	}
	if err := rows.Err(); err != nil {
		return nil, err
	}
	return items, nil
}

const subscribePublisher = `-- name: SubscribePublisher :exec
INSERT INTO subscription (user_id, source_id)
VALUES ($1, $2)
`

type SubscribePublisherParams struct {
	UserID      int32 `json:"user_id"`
	PublisherID int32 `json:"publisher_id"`
}

func (q *Queries) SubscribePublisher(ctx context.Context, arg SubscribePublisherParams) error {
	_, err := q.db.Exec(ctx, subscribePublisher, arg.UserID, arg.PublisherID)
	return err
}

const unsubscribePublisher = `-- name: UnsubscribePublisher :exec
DELETE
FROM subscription
WHERE user_id = $1
  AND source_id = $2
`

type UnsubscribePublisherParams struct {
	UserID      int32 `json:"user_id"`
	PublisherID int32 `json:"publisher_id"`
}

func (q *Queries) UnsubscribePublisher(ctx context.Context, arg UnsubscribePublisherParams) error {
	_, err := q.db.Exec(ctx, unsubscribePublisher, arg.UserID, arg.PublisherID)
	return err
}
