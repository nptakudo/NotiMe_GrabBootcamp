package webscrape

import (
	"bytes"
	"encoding/json"
	"errors"
	"github.com/bobesa/go-domain-util/domainutil"
	"io"
	"log/slog"
	"net/http"
	"notime/domain"
	"notime/repository/models"
	"os"
	"strings"
	"time"
)

func ScrapeFromUrl(url string, host string, port string, timeout time.Duration) ([]*models.ScrapedArticle, error) {
	// Define the command to be executed
	command := "python3 linkscrape.py " + url

	// Create the JSON payload
	payload := map[string]string{
		"command": command,
	}
	jsonData, err := json.Marshal(payload)
	if err != nil {
		slog.Error("[Webscrape] ScrapeFromUrl: Error marshalling payload: %v", err)
		return nil, err
	}

	client := &http.Client{Timeout: timeout}
	resp, err := client.Post(host+":"+port+"/execute_command", "application/json", bytes.NewBuffer(jsonData))
	if err != nil {
		slog.Error("[Webscrape] ScrapeFromUrl: Error sending POST request: %v", err)
		return nil, err
	}
	defer resp.Body.Close()

	// Open the JSON file
	file, err := os.Open("../pipeline/webscrape/webscrape/spiders/output.json")
	if err != nil {
		slog.Error("[Webscrape] ScrapeFromUrl: Error opening output.json: %v", err)
		return nil, err
	}
	defer file.Close()

	// Read the file content
	content, err := io.ReadAll(file)
	if err != nil {
		slog.Error("[Webscrape] ScrapeFromUrl: Error reading output.json: %v", err)
		return nil, err
	}

	// Decode the response
	var articles []*models.ScrapedArticle
	//err = json.NewDecoder(resp.Body).Decode(&articles)
	//if err != nil {
	//	slog.Error("[Webscrape] ScrapeFromUrl: %v", err)
	//	return nil, err
	//}

	err = json.Unmarshal(content, &articles)
	if err != nil {
		slog.Error("[Webscrape] ScrapeFromUrl: Error unmarshalling output.json: %v", err)
		return nil, err
	}

	publisherName, err := getPublisherNameFromSubdomainsAndDomain(url)
	if err != nil {
		slog.Error("[Webscrape] ScrapeFromUrl: %v", err)
		return nil, err
	}
	for _, article := range articles {
		article.PublisherName = publisherName
		article.Date = time.Now()
	}
	return articles, nil
}

func CheckAndCompilePublisher(url string, timeout time.Duration) (*domain.Publisher, error) {
	client := &http.Client{Timeout: timeout}
	resp, err := client.Get(url)
	if err != nil {
		slog.Error("[Webscrape] CheckAndCompilePublisher: URL does not respond: %v", err)
		return nil, err
	}
	defer resp.Body.Close()

	if resp.StatusCode == http.StatusOK {
		slog.Info("[Webscrape] CheckAndCompilePublisher: URL is reachable")
	} else {
		slog.Info("[Webscrape] CheckAndCompilePublisher: URL is not reachable. Status code: %v", resp.StatusCode)
		return nil, errors.New("URL is not reachable")
	}

	publisherName, err := getPublisherNameFromSubdomainsAndDomain(url)
	if err != nil {
		slog.Error("[Webscrape] CheckAndCompilePublisher: Error parsing URL: %v", err)
		return nil, err
	}
	return &domain.Publisher{
		Id:         -1,
		Name:       publisherName,
		Url:        url,
		AvatarPath: "",
	}, nil
}

func getPublisherNameFromSubdomainsAndDomain(urlStr string) (string, error) {
	domain := domainutil.Domain(urlStr)
	domainSuffix := domainutil.DomainSuffix(urlStr)
	subdomain := domainutil.Subdomain(urlStr)
	if domain == "" {
		return "", errors.New("invalid URL")
	}

	// Remove the suffix from the domain
	if domainSuffix != "" {
		domain = domain[:len(domain)-len(domainSuffix)-1]
	}
	// If there are many subdomains, only keep the lowest subdomain
	if strings.Contains(subdomain, ".") {
		subdomain = strings.Split(subdomain, ".")[0]
	}

	publisherName := domain
	if subdomain != "" && subdomain != "www" {
		publisherName = subdomain + "@" + domain
	}
	return publisherName, nil
}
