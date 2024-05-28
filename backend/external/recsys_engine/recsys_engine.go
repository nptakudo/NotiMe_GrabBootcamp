package recsys_engine

import (
	"bytes"
	"encoding/json"
	"io/ioutil"
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
	resp, err := client.Post(r.Host+"/suggest", "application/json", bytes.NewBuffer(jsonData))
	if err != nil {
		slog.Error("[Recsys] GetRelatedArticleUrlsFromUrl: Error sending POST request:", "error", err)
		return nil, err
	}
	defer resp.Body.Close()

	// Read the response body
	bodyBytes, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		slog.Error("[Recsys] GetRelatedArticleUrlsFromUrl: Error reading response body:", "error", err)
		return nil, err
	}

	// Log the response body
	slog.Info("[Recsys] GetRelatedArticleUrlsFromUrl: Response body:", "body", string(bodyBytes))

	// Use the body content
	var relatedArticleUrls []string
	err = json.Unmarshal(bodyBytes, &relatedArticleUrls)
	if err != nil {
		slog.Error("[Recsys] GetRelatedArticleUrlsFromUrl: Error decoding response:", "error", err)
		return nil, err
	}

	return relatedArticleUrls, nil
}
