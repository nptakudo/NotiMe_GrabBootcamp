package repository

import "notime/domain"

type BookmarkListRepositoryImpl struct{}

func (r *BookmarkListRepositoryImpl) GetById(id uint32) (*domain.BookmarkList, error) {
	return nil, nil
}

func (r *BookmarkListRepositoryImpl) GetOwnByUser(userId uint32) ([]*domain.BookmarkList, error) {
	return nil, nil
}

func (r *BookmarkListRepositoryImpl) GetSharedWithUser(userId uint32) ([]*domain.BookmarkList, error) {
	return nil, nil
}

func (r *BookmarkListRepositoryImpl) IsInBookmarkList(articleId uint32, bookmarkListId uint32) (bool, error) {
	return false, nil
}

func (r *BookmarkListRepositoryImpl) AddToBookmarkList(articleId uint32, bookmarkListId uint32) error {
	return nil
}

func (r *BookmarkListRepositoryImpl) RemoveFromBookmarkList(articleId uint32, bookmarkListId uint32) error {
	return nil
}

func (r *BookmarkListRepositoryImpl) Create(bookmarkListName string, userId uint32) (*domain.BookmarkList, error) {
	return nil, nil
}

func (r *BookmarkListRepositoryImpl) Delete(bookmarkListId uint32) (*domain.BookmarkList, error) {
	return nil, nil
}
