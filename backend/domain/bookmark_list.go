package domain

import "context"

type BookmarkList struct {
	Id          int32              `json:"id"`
	Name        string             `json:"name"`
	IsSavedList bool               `json:"is_saved_list"`
	Articles    []*ArticleMetadata `json:"articles"`
	OwnerId     int32              `json:"owner_id"`
}

type BookmarkListRepository interface {
	GetById(ctx context.Context, id int32) (*BookmarkList, error)
	GetOwnByUser(ctx context.Context, userId int32) ([]*BookmarkList, error)
	GetSharedWithUser(ctx context.Context, userId int32) ([]*BookmarkList, error)
	IsInBookmarkList(ctx context.Context, articleId int64, bookmarkListId int32) (bool, error)
	IsInAnyBookmarkList(ctx context.Context, articleId int64, userId int32) (bool, error)
	AddToBookmarkList(ctx context.Context, articleId int64, bookmarkListId int32) error
	RemoveFromBookmarkList(ctx context.Context, articleId int64, bookmarkListId int32) error
	Create(ctx context.Context, bookmarkListName string, userId int32, isSaved bool) (*BookmarkList, error)
	Delete(ctx context.Context, bookmarkListId int32) (*BookmarkList, error)
}
