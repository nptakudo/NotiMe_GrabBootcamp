// Code generated by sqlc. DO NOT EDIT.
// versions:
//   sqlc v1.20.0

package store

import (
	"time"
)

type ListPost struct {
	ListID int32 `json:"list_id"`
	PostID int64 `json:"post_id"`
}

type ListSharing struct {
	ListID int32 `json:"list_id"`
	UserID int32 `json:"user_id"`
}

type Post struct {
	ID          int64     `json:"id"`
	Title       string    `json:"title"`
	PublishDate time.Time `json:"publish_date"`
	Url         string    `json:"url"`
	SourceID    int32     `json:"source_id"`
}

type ReadingList struct {
	ID       int32  `json:"id"`
	ListName string `json:"list_name"`
	Owner    int32  `json:"owner"`
	IsSaved  bool   `json:"is_saved"`
}

type Source struct {
	ID     int32  `json:"id"`
	Name   string `json:"name"`
	Url    string `json:"url"`
	Avatar string `json:"avatar"`
}

type Subscription struct {
	UserID   int32 `json:"user_id"`
	SourceID int32 `json:"source_id"`
}

type User struct {
	ID       int32  `json:"id"`
	Username string `json:"username"`
	Password string `json:"password"`
}
