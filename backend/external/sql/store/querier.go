// Code generated by sqlc. DO NOT EDIT.
// versions:
//   sqlc v1.20.0

package store

import (
	"context"

	"github.com/jackc/pgx/v5/pgtype"
)

type Querier interface {
	AddArticleToBookmarkList(ctx context.Context, arg AddArticleToBookmarkListParams) (ListPost, error)
	CreateBookmarkList(ctx context.Context, arg CreateBookmarkListParams) (ReadingList, error)
	DeleteBookmarkList(ctx context.Context, id int32) (ReadingList, error)
	// -- DROP SCHEMA public CASCADE;
	// -- CREATE SCHEMA public;
	// -- GRANT ALL ON SCHEMA public TO postgres;
	// -- GRANT ALL ON SCHEMA public TO public;
	//
	// CREATE TABLE "user" (
	//                         id SERIAL PRIMARY KEY,
	//                         username VARCHAR(255) UNIQUE NOT NULL,
	//                         "password" VARCHAR(255) NOT NULL
	// );
	//
	// CREATE TABLE source (
	//                         id SERIAL PRIMARY KEY,
	//                         "name" VARCHAR(255) NOT NULL,
	//                         "url" VARCHAR(255) NOT NULL,
	//                         avatar VARCHAR(255) NOT NULL -- consider how to store the image
	// );
	//
	// CREATE TABLE post (
	//                       id BIGSERIAL PRIMARY KEY,
	//                       title VARCHAR(255) NOT NULL,
	//                       publish_date BIGINT,
	//                       "url" VARCHAR(255) NOT NULL,
	//                       source_id INTEGER REFERENCES source (id) ON DELETE CASCADE -- delete post when source is deleted
	//     -- consider about the image of post to show on the top
	// );
	//
	// CREATE TABLE subscription (
	//                               user_id INTEGER REFERENCES "user" (id) ON DELETE CASCADE,
	//                               source_id INTEGER REFERENCES source (id) ON DELETE CASCADE,
	//                               PRIMARY KEY (user_id, source_id)
	// );
	//
	// CREATE TABLE reading_list (
	//                               id SERIAL PRIMARY KEY,
	//                               list_name VARCHAR(255) NOT NULL,
	//                               "owner" INTEGER REFERENCES "user" (id) ON DELETE CASCADE,
	//                               is_saved BOOLEAN NOT NULL -- stands for saved post list, the other list will be create with name
	// );
	//
	// CREATE TABLE list_post (
	//                            list_id INTEGER REFERENCES reading_list (id) ON DELETE CASCADE,
	//                            post_id BIGINT REFERENCES post(id) ON DELETE CASCADE,
	//                            PRIMARY KEY (list_id, post_id)
	// );
	//
	// CREATE TABLE list_sharing (
	//                               list_id INTEGER REFERENCES reading_list (id) ON DELETE CASCADE,
	//                               user_id INTEGER REFERENCES "user" (id) ON DELETE CASCADE,
	//                               PRIMARY KEY (list_id, user_id)
	// );
	//-----------------------------------------------
	// ARTICLE REPOSITORY
	//-----------------------------------------------
	GetArticleById(ctx context.Context, id int64) (Post, error)
	GetArticlesByPublisherId(ctx context.Context, sourceID pgtype.Int4) ([]Post, error)
	//-----------------------------------------------
	// BOOKMARK LIST REPOSITORY
	//-----------------------------------------------
	GetBookmarkListById(ctx context.Context, id int32) ([]ReadingList, error)
	GetBookmarkListsOwnByUserId(ctx context.Context, owner pgtype.Int4) ([]ReadingList, error)
	GetBookmarkListsSharedWithUserId(ctx context.Context, userID int32) ([]GetBookmarkListsSharedWithUserIdRow, error)
	//-----------------------------------------------
	// PUBLISHER REPOSITORY
	//-----------------------------------------------
	GetPublisherById(ctx context.Context, id int32) (Source, error)
	//-----------------------------------------------
	// SUBSCRIBE LIST REPOSITORY
	//-----------------------------------------------
	GetSubscribedPublishersByUserId(ctx context.Context, userID int32) ([]GetSubscribedPublishersByUserIdRow, error)
	IsArticleInBookmarkList(ctx context.Context, arg IsArticleInBookmarkListParams) (ListPost, error)
	IsPublisherSubscribedByUserId(ctx context.Context, arg IsPublisherSubscribedByUserIdParams) (Subscription, error)
	RemoveArticleFromBookmarkList(ctx context.Context, arg RemoveArticleFromBookmarkListParams) (ListPost, error)
	SearchArticlesByName(ctx context.Context, dollar_1 pgtype.Text) ([]Post, error)
	SearchPublishersByName(ctx context.Context, dollar_1 pgtype.Text) ([]Source, error)
	SubscribePublisher(ctx context.Context, arg SubscribePublisherParams) (Subscription, error)
	UnsubscribePublisher(ctx context.Context, arg UnsubscribePublisherParams) (Subscription, error)
}

var _ Querier = (*Queries)(nil)
