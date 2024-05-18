package recsys_engine

type RecsysEngineImpl struct {
	Host string
	Port string
}

func (r *RecsysEngineImpl) GetRelatedArticleUrlsFromUrl(url string) ([]string, error) {
	// TODO
	return nil, nil
}
