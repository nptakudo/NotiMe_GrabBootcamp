package repository

import (
	"errors"
	"notime/domain"
	"notime/external/sql/store"
)

var (
	ErrInstanceIsNil       = errors.New("instance is nil")
	ErrPublisherIdMismatch = errors.New("publisher id mismatch")
)

func convertDbArticleToDm(dbArticle *store.Post, dbPublisher *store.Source, imageUrl string) (*domain.ArticleMetadata, error) {
	if dbPublisher == nil || dbArticle == nil {
		return nil, ErrInstanceIsNil
	}
	if dbPublisher.ID != dbArticle.SourceID {
		return nil, ErrPublisherIdMismatch
	}

	dmPublisher, err := convertDbPublisherToDm(dbPublisher)
	if err != nil {
		return nil, err
	}
	return &domain.ArticleMetadata{
		Id:         dbArticle.ID,
		Title:      dbArticle.Title,
		Publisher:  dmPublisher,
		Url:        dbArticle.Url,
		Date:       dbArticle.PublishDate,
		ImageUrl:   imageUrl,
		RawContent: dbArticle.RawText,
	}, nil
}

func convertDbBookmarkListToDm(dbBookmarkListMetadata *store.ReadingList, dmArticles []*domain.ArticleMetadata) (*domain.BookmarkList, error) {
	if dbBookmarkListMetadata == nil {
		return nil, ErrInstanceIsNil
	}

	return &domain.BookmarkList{
		Id:       dbBookmarkListMetadata.ID,
		Name:     dbBookmarkListMetadata.ListName,
		IsSaved:  dbBookmarkListMetadata.IsSaved,
		Articles: dmArticles,
		OwnerId:  dbBookmarkListMetadata.Owner,
	}, nil
}

func convertDbPublisherToDm(dbPublisher *store.Source) (*domain.Publisher, error) {
	if dbPublisher == nil {
		return nil, ErrInstanceIsNil
	}

	avatarUrl := ""
	if dbPublisher.Avatar.Valid {
		avatarUrl = dbPublisher.Avatar.String
	}
	return &domain.Publisher{
		Id:        dbPublisher.ID,
		Name:      dbPublisher.Name,
		Url:       dbPublisher.Url,
		AvatarUrl: avatarUrl,
	}, nil
}

func convertDbUserToDm(dbUser *store.User) (*domain.User, error) {
	if dbUser == nil {
		return nil, ErrInstanceIsNil
	}

	return &domain.User{
		Id:       dbUser.ID,
		Username: dbUser.Username,
		Password: dbUser.Password,
	}, nil
}
