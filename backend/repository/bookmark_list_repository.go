package repository

import (
	"context"
	"errors"
	"github.com/jackc/pgconn"
	"github.com/jackc/pgx/v4"
	"log/slog"
	"notime/bootstrap"
	"notime/domain"
	"notime/external/sql/store"
	"slices"
)

type BookmarkListRepositoryImpl struct {
	q *store.Queries
	UtilitiesRepository
}

func NewBookmarkListRepository(env *bootstrap.Env, q *store.Queries) domain.BookmarkListRepository {
	return &BookmarkListRepositoryImpl{
		q:                   q,
		UtilitiesRepository: UtilitiesRepository{q: q, env: env},
	}
}

func (r *BookmarkListRepositoryImpl) GetById(ctx context.Context, id int32) (*domain.BookmarkList, error) {
	dbBookmarkList, err := r.q.GetBookmarkListById(ctx, id)
	if err != nil {
		slog.Error("[BookmarkList Repository] GetById query:", "error", err)
		return nil, err
	}
	dmBookmarkList, err := r.completeDmBookmarkListFromDb(ctx, &dbBookmarkList)
	if err != nil {
		slog.Error("[BookmarkList Repository] GetById convert:", "error", err)
		return nil, err
	}
	return dmBookmarkList, nil
}

func (r *BookmarkListRepositoryImpl) GetOwnByUser(ctx context.Context, userId int32) ([]*domain.BookmarkList, error) {
	dbBookmarkLists, err := r.q.GetBookmarkListsOwnByUserId(ctx, userId)
	if err != nil {
		slog.Error("[BookmarkList Repository] GetOwnByUser query:", "error", err)
		return nil, err
	}
	dmBookmarkLists := make([]*domain.BookmarkList, 0)
	for _, dbBookmarkList := range dbBookmarkLists {
		dmBookmarkList, err := r.completeDmBookmarkListFromDb(ctx, &dbBookmarkList)
		if err != nil {
			slog.Error("[BookmarkList Repository] GetOwnByUser convert:", "error", err)
			return nil, err
		}
		dmBookmarkLists = append(dmBookmarkLists, dmBookmarkList)
	}
	return dmBookmarkLists, nil
}

func (r *BookmarkListRepositoryImpl) GetSharedWithUser(ctx context.Context, userId int32) ([]*domain.BookmarkList, error) {
	dbBookmarkLists, err := r.q.GetBookmarkListsSharedWithUserId(ctx, userId)
	if err != nil {
		slog.Error("[BookmarkList Repository] GetSharedWithUser query:", "error", err)
		return nil, err
	}
	dmBookmarkLists := make([]*domain.BookmarkList, 0)
	for _, dbBookmarkList := range dbBookmarkLists {
		dmBookmarkList, err := r.completeDmBookmarkListFromDb(ctx, &dbBookmarkList)
		if err != nil {
			slog.Error("[BookmarkList Repository] GetSharedWithUser convert:", "error", err)
			return nil, err
		}
		dmBookmarkLists = append(dmBookmarkLists, dmBookmarkList)
	}
	return dmBookmarkLists, nil
}

func (r *BookmarkListRepositoryImpl) IsInBookmarkList(ctx context.Context, articleId int64, bookmarkListId int32) (bool, error) {
	_, err := r.q.IsArticleInBookmarkList(ctx, store.IsArticleInBookmarkListParams{
		ListID:    bookmarkListId,
		ArticleID: articleId,
	})
	if err != nil {
		slog.Error("[BookmarkList Repository] IsInBookmarkList query:", "error", err)
		return false, err
	}
	return true, nil
}

func (r *BookmarkListRepositoryImpl) IsInAnyBookmarkList(ctx context.Context, articleId int64, userId int32) (bool, error) {
	_, err := r.q.IsArticleInAnyBookmarkList(ctx, store.IsArticleInAnyBookmarkListParams{
		ArticleID: articleId,
		UserID:    userId,
	})
	if err != nil {
		// If the error indicates that article is not in any bookmark list, return false
		var dbErr *pgconn.PgError
		if errors.As(err, &dbErr) {
			if slices.Contains(DbErrCodeNotFound, dbErr.Code) {
				return false, nil
			}
		}
		if errors.Is(err, pgx.ErrNoRows) {
			return false, nil
		}
		// Otherwise, log error
		slog.Error("[BookmarkList Repository] IsInAnyBookmarkList query:", "error", err)
		return false, err
	}
	return true, nil
}

func (r *BookmarkListRepositoryImpl) AddToBookmarkList(ctx context.Context, articleId int64, bookmarkListId int32) error {
	err := r.q.AddArticleToBookmarkList(ctx, store.AddArticleToBookmarkListParams{
		ListID:    bookmarkListId,
		ArticleID: articleId,
	})
	if err != nil {
		slog.Error("[BookmarkList Repository] AddToBookmarkList query:", "error", err)
		return err
	}
	return nil
}

func (r *BookmarkListRepositoryImpl) RemoveFromBookmarkList(ctx context.Context, articleId int64, bookmarkListId int32) error {
	err := r.q.RemoveArticleFromBookmarkList(ctx, store.RemoveArticleFromBookmarkListParams{
		ListID:    bookmarkListId,
		ArticleID: articleId,
	})
	if err != nil {
		slog.Error("[BookmarkList Repository] RemoveFromBookmarkList query:", "error", err)
		return err
	}
	return nil
}

func (r *BookmarkListRepositoryImpl) Create(ctx context.Context, bookmarkListName string, userId int32, isSaved bool) (*domain.BookmarkList, error) {
	dbBookmarkList, err := r.q.CreateBookmarkList(ctx, store.CreateBookmarkListParams{
		Name:    bookmarkListName,
		OwnerID: userId,
		IsSaved: isSaved,
	})
	if err != nil {
		slog.Error("[BookmarkList Repository] Create query:", "error", err)
		return nil, err
	}
	dmBookmarkList, err := r.completeDmBookmarkListFromDb(ctx, &dbBookmarkList)
	if err != nil {
		slog.Error("[BookmarkList Repository] Create convert:", "error", err)
		return nil, err
	}
	return dmBookmarkList, nil
}

func (r *BookmarkListRepositoryImpl) Delete(ctx context.Context, bookmarkListId int32) (*domain.BookmarkList, error) {
	dbBookmarkList, err := r.q.DeleteBookmarkList(ctx, bookmarkListId)
	if err != nil {
		slog.Error("[BookmarkList Repository] Delete query:", "error", err)
		return nil, err
	}
	dmBookmarkList, err := r.completeDmBookmarkListFromDb(ctx, &dbBookmarkList)
	if err != nil {
		slog.Error("[BookmarkList Repository] Delete convert:", "error", err)
		return nil, err
	}
	return dmBookmarkList, nil
}
