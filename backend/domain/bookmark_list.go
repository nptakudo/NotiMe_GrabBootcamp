package domain

type BookmarkList struct {
	Id          uint32             `json:"id"`
	Name        string             `json:"name"`
	IsSavedList bool               `json:"is_saved_list"`
	Articles    []*ArticleMetadata `json:"articles"`
	OwnerId     uint32             `json:"owner_id"`
}

type BookmarkListRepository interface {
	GetById(id uint32) (*BookmarkList, error)
	GetOwnByUser(userId uint32) ([]*BookmarkList, error)
	GetSharedWithUser(userId uint32) ([]*BookmarkList, error)
	IsInBookmarkList(articleId uint32, bookmarkListId uint32) (bool, error)
	AddToBookmarkList(articleId uint32, bookmarkListId uint32) error
	RemoveFromBookmarkList(articleId uint32, bookmarkListId uint32) error
	Create(bookmarkListName string, userId uint32) (*BookmarkList, error)
	Delete(bookmarkListId uint32) (*BookmarkList, error)
}
