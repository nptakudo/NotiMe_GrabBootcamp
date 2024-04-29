package domain

type BookmarkList struct {
	Id          uint32             `json:"id"`
	Name        string             `json:"name"`
	IsSavedList bool               `json:"is_saved_list"`
	Articles    []*ArticleMetadata `json:"articles"`
	User        *User              `json:"user"`
	IsOwner     bool               `json:"is_owner"`
}

type BookmarkListRepository interface {
	GetById(id uint32, userId uint32) (*BookmarkList, error)
	GetByUser(userId uint32) ([]*BookmarkList, error)
	IsBookmarked(articleId uint32, userId uint32) (bool, error)
	IsInBookmarkList(articleId uint32, bookmarkListId uint32) (bool, error)
	AddToBookmarkList(articleId uint32, bookmarkListId uint32) error
	RemoveFromBookmarkList(articleId uint32, bookmarkListId uint32) error
	Create(bookmarkListName string, userId uint32) (*BookmarkList, error)
	Delete(bookmarkListId uint32) (*BookmarkList, error)
}
