package repository

import (
	"context"
	"notime/domain"
	"notime/external/sql/store"
	"notime/utils/htmlutils"
)

type UtilitiesRepository struct {
	q *store.Queries
}

func (r *UtilitiesRepository) completeDmArticleFromDb(ctx context.Context, dbArticle *store.Post) (*domain.ArticleMetadata, error) {
	dbPublisher, err := r.q.GetPublisherById(ctx, dbArticle.SourceID)
	if err != nil {
		return nil, err
	}

	imgSrc, err := htmlutils.GetLargestImageUrlFromArticle(dbArticle.Url)
	if err != nil {
		imgSrc = ""
	}

	dmArticle, err := convertDbArticleToDm(dbArticle, &dbPublisher, imgSrc)
	if err != nil {
		return nil, err
	}

	return dmArticle, nil
}

func (r *UtilitiesRepository) completeDmBookmarkListFromDb(ctx context.Context, dbBookmarkList *store.ReadingList) (*domain.BookmarkList, error) {
	dbArticles, err := r.q.GetArticlesInBookmarkList(ctx, dbBookmarkList.ID)
	if err != nil {
		return nil, err
	}

	var articles []*domain.ArticleMetadata
	for _, dbArticle := range dbArticles {
		article, err := r.completeDmArticleFromDb(ctx, &dbArticle)
		if err != nil {
			return nil, err
		}
		articles = append(articles, article)
	}

	dmBookmarkList, err := convertDbBookmarkListToDm(dbBookmarkList, articles)
	if err != nil {
		return nil, err
	}
	return dmBookmarkList, nil
}
