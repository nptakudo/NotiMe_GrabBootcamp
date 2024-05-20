package webscrape_engine

import (
	"bytes"
	"encoding/json"
	"io"
	"log/slog"
	"net/http"
	"notime/repository/models"
	"notime/utils/htmlutils"
	"os"
	"time"
)

type WebscrapeEngineImpl struct {
	Host string
	Port string
}

func (e *WebscrapeEngineImpl) ScrapeFromUrl(url string, timeout time.Duration) ([]*models.ScrapedArticle, error) {
	publisherName, err := htmlutils.GetPublisherNameFromSubdomainsAndDomain(url)
	if err != nil {
		slog.Error("[Webscrape] ScrapeFromUrl:", "error", err)
		return nil, err
	}

	// Define the command to be executed
	command := "python3 linkscrape.py " + url

	// Create the JSON payload
	payload := map[string]string{
		"command": command,
	}
	jsonData, err := json.Marshal(payload)
	if err != nil {
		slog.Error("[Webscrape] ScrapeFromUrl: Error marshalling payload:", "error", err)
		return nil, err
	}

	client := &http.Client{Timeout: timeout}
	resp, err := client.Post(e.Host+":"+e.Port+"/execute_command", "application/json", bytes.NewBuffer(jsonData))
	if err != nil {
		slog.Error("[Webscrape] ScrapeFromUrl: Error sending POST request:", "error", err)
		return nil, err
	}
	defer resp.Body.Close()

	// Open the JSON file
	file, err := os.Open("../pipeline/webscrape/webscrape/spiders/output.json")
	if err != nil {
		slog.Error("[Webscrape] ScrapeFromUrl: Error opening output.json:", "error", err)
		return nil, err
	}
	defer file.Close()

	// Read the file content
	content, err := io.ReadAll(file)
	if err != nil {
		slog.Error("[Webscrape] ScrapeFromUrl: Error reading output.json:", "error", err)
		return nil, err
	}

	// Decode the response
	var articles []*models.ScrapedArticle
	err = json.Unmarshal(content, &articles)
	if err != nil {
		slog.Error("[Webscrape] ScrapeFromUrl: Error unmarshalling output.json:", "error", err)
		return nil, err
	}
	for _, article := range articles {
		article.PublisherName = publisherName
		article.SetTime(time.Now().UTC())
	}
	return articles, nil
}
