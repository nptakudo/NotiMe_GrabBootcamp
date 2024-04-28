package domain

const (
	Owner     = iota
	ReadOnly  = iota
	ReadWrite = iota
)

type BookmarkList struct {
	ID        uint32     `json:"id"`
	Articles  []*Article `json:"articles"`
	User      *User      `json:"user"`
	Privilege int        `json:"privilege"` // 0: owner, 1: read-only, 2: read-write
}

type ArticleListRepository interface {
	GetByID(id uint32) (*BookmarkList, error)
	GetByUser(userId uint32) ([]*BookmarkList, error)
	IsBookmarked(article *Article, userId uint32) (bool, error)
	AddToBookmarkList(article *Article, articleList *BookmarkList) error
	RemoveFromBookmarkList(article *Article, articleList *BookmarkList) error
	ChangePrivilege(articleList *BookmarkList, privilege int) error
	Delete(articleList *BookmarkList) error
}
