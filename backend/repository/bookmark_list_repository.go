package repository

import (
	"context"
	"github.com/jackc/pgx/v5/pgtype"
	"log/slog"
	"notime/domain"
	"notime/external/sql/store"
)

type BookmarkListRepositoryImpl struct {
	q *store.Queries
	UtilitiesRepository
}

func NewBookmarkListRepository(q *store.Queries) domain.BookmarkListRepository {
	return &BookmarkListRepositoryImpl{q: q}
}

func (r *BookmarkListRepositoryImpl) GetById(ctx context.Context, id int32) (*domain.BookmarkList, error) {
	dbBookmarkList, err := r.q.GetBookmarkListById(ctx, id)
	if err != nil {
		slog.Error("[BookmarkList Repository] GetById query:", err)
		return nil, err
	}
	dmBookmarkList, err := r.completeDmBookmarkListFromDb(ctx, &dbBookmarkList)
	if err != nil {
		slog.Error("[BookmarkList Repository] GetById convert:", err)
		return nil, err
	}
	return dmBookmarkList, nil
}

func (r *BookmarkListRepositoryImpl) GetOwnByUser(ctx context.Context, userId int32) ([]*domain.BookmarkList, error) {
	dbBookmarkLists, err := r.q.GetBookmarkListsOwnByUserId(ctx, pgtype.Int4{Int32: userId})
	if err != nil {
		slog.Error("[BookmarkList Repository] GetOwnByUser query:", err)
		return nil, err
	}
	dmBookmarkLists := make([]*domain.BookmarkList, 0)
	for _, dbBookmarkList := range dbBookmarkLists {
		dmBookmarkList, err := r.completeDmBookmarkListFromDb(ctx, &dbBookmarkList)
		if err != nil {
			slog.Error("[BookmarkList Repository] GetOwnByUser convert:", err)
			return nil, err
		}
		dmBookmarkLists = append(dmBookmarkLists, dmBookmarkList)
	}
	return dmBookmarkLists, nil
}

func (r *BookmarkListRepositoryImpl) GetSharedWithUser(ctx context.Context, userId int32) ([]*domain.BookmarkList, error) {
	dbBookmarkLists, err := r.q.GetBookmarkListsSharedWithUserId(ctx, userId)
	if err != nil {
		slog.Error("[BookmarkList Repository] GetSharedWithUser query:", err)
		return nil, err
	}
	dmBookmarkLists := make([]*domain.BookmarkList, 0)
	for _, dbBookmarkList := range dbBookmarkLists {
		dmBookmarkList, err := r.completeDmBookmarkListFromDb(ctx, &dbBookmarkList)
		if err != nil {
			slog.Error("[BookmarkList Repository] GetSharedWithUser convert:", err)
			return nil, err
		}
		dmBookmarkLists = append(dmBookmarkLists, dmBookmarkList)
	}
	return dmBookmarkLists, nil
}

func (r *BookmarkListRepositoryImpl) IsInBookmarkList(ctx context.Context, articleId int64, bookmarkListId int32) (bool, error) {
	_, err := r.q.IsArticleInBookmarkList(ctx, store.IsArticleInBookmarkListParams{
		ListID: bookmarkListId,
		PostID: articleId,
	})
	if err != nil {
		slog.Error("[BookmarkList Repository] IsInBookmarkList query:", err)
		return false, err
	}
	return true, nil
}

func (r *BookmarkListRepositoryImpl) AddToBookmarkList(ctx context.Context, articleId int64, bookmarkListId int32) error {
	err := r.q.AddArticleToBookmarkList(ctx, store.AddArticleToBookmarkListParams{
		ListID: bookmarkListId,
		PostID: articleId,
	})
	if err != nil {
		slog.Error("[BookmarkList Repository] AddToBookmarkList query:", err)
		return err
	}
	return nil
}

func (r *BookmarkListRepositoryImpl) RemoveFromBookmarkList(ctx context.Context, articleId int64, bookmarkListId int32) error {
	err := r.q.RemoveArticleFromBookmarkList(ctx, store.RemoveArticleFromBookmarkListParams{
		ListID: bookmarkListId,
		PostID: articleId,
	})
	if err != nil {
		slog.Error("[BookmarkList Repository] RemoveFromBookmarkList query:", err)
		return err
	}
	return nil
}

func (r *BookmarkListRepositoryImpl) Create(ctx context.Context, bookmarkListName string, userId int32, isSaved bool) (*domain.BookmarkList, error) {
	dbBookmarkList, err := r.q.CreateBookmarkList(ctx, store.CreateBookmarkListParams{
		ListName: bookmarkListName,
		Owner:    pgtype.Int4{Int32: userId},
		IsSaved:  isSaved,
	})
	if err != nil {
		slog.Error("[BookmarkList Repository] Create query:", err)
		return nil, err
	}
	dmBookmarkList, err := r.completeDmBookmarkListFromDb(ctx, &dbBookmarkList)
	if err != nil {
		slog.Error("[BookmarkList Repository] Create convert:", err)
		return nil, err
	}
	return dmBookmarkList, nil
}

func (r *BookmarkListRepositoryImpl) Delete(ctx context.Context, bookmarkListId int32) (*domain.BookmarkList, error) {
	dbBookmarkList, err := r.q.DeleteBookmarkList(ctx, bookmarkListId)
	if err != nil {
		slog.Error("[BookmarkList Repository] Delete query:", err)
		return nil, err
	}
	dmBookmarkList, err := r.completeDmBookmarkListFromDb(ctx, &dbBookmarkList)
	if err != nil {
		slog.Error("[BookmarkList Repository] Delete convert:", err)
		return nil, err
	}
	return dmBookmarkList, nil
}
