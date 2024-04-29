package domain

const (
	Owner     = iota
	ReadOnly  = iota
	ReadWrite = iota
)

type BookmarkList struct {
	Id        uint32     `json:"id"`
	Name      string     `json:"name"`
	Articles  []*Article `json:"articles"`
	User      *User      `json:"user"`
	Privilege int        `json:"privilege"` // 0: owner, 1: read-only, 2: read-write
}

type BookmarkListRepository interface {
	GetById(id uint32, userId uint32) (*BookmarkList, error)
	GetByUser(userId uint32) ([]*BookmarkList, error)
	IsBookmarked(articleId uint32, userId uint32) (bool, error)
	IsInBookmarkList(articleId uint32, bookmarkListId uint32) (bool, error)
	AddToBookmarkList(articleId uint32, bookmarkListId uint32) error
	RemoveFromBookmarkList(articleId uint32, bookmarkListId uint32) error
	ChangePrivilege(bookmarkListId uint32, privilege int) error
	Create(bookmarkListName string, userId uint32) (*BookmarkList, error)
	Delete(bookmarkListId uint32) (*BookmarkList, error)
}
