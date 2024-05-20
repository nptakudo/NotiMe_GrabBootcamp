package recsys_engine

import (
	"bytes"
	"encoding/json"
	"log/slog"
	"net/http"
	"time"
)

type RecsysEngineImpl struct {
	Host string
	Port string
}

func (r *RecsysEngineImpl) GetRelatedArticleUrlsFromUrl(url string, timeout time.Duration) ([]string, error) {
	// Create the JSON payload
	payload := map[string]string{
		"url": url,
	}
	jsonData, err := json.Marshal(payload)
	if err != nil {
		slog.Error("[Recsys] GetRelatedArticleUrlsFromUrl: Error marshalling payload:", "error", err)
		return nil, err
	}

	client := &http.Client{Timeout: timeout}
	resp, err := client.Post(r.Host+":"+r.Port+"/suggest_on_url", "application/json", bytes.NewBuffer(jsonData))
	if err != nil {
		slog.Error("[Recsys] GetRelatedArticleUrlsFromUrl: Error sending POST request:", "error", err)
		return nil, err
	}
	defer resp.Body.Close()

	// Read the response as a list of strings
	var relatedArticleUrls []string
	err = json.NewDecoder(resp.Body).Decode(&relatedArticleUrls)
	if err != nil {
		slog.Error("[Recsys] GetRelatedArticleUrlsFromUrl: Error decoding response:", "error", err)
		return nil, err
	}

	return relatedArticleUrls, nil
}
