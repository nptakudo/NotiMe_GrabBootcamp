package htmlutils

import (
	"bytes"
	"errors"
	"fmt"
	md "github.com/JohannesKaufmann/html-to-markdown"
	"github.com/PuerkitoBio/goquery"
	"github.com/bobesa/go-domain-util/domainutil"
	"github.com/microcosm-cc/bluemonday"
	"log/slog"
	"net/http"
	"net/url"
	"notime/domain"
	"strconv"
	"strings"
	"time"
)

var (
	ErrCannotFetch = errors.New("cannot fetch from url")
	ErrCannotParse = errors.New("cannot parse html")
)

func GetLargestImageUrlFromArticle(url string, timeout time.Duration) (string, error) {
	html, err := GetSanitizedHtml(url, timeout)
	if err != nil {
		return "", err
	}

	// Select <article> from the HTML document
	doc, err := goquery.NewDocumentFromReader(html)
	if err != nil {
		slog.Error("[HTMLUtils] GetLargestImageUrlFromArticle:", "error", err)
		return "", ErrCannotParse
	}

	// Select the biggest image in the article, with a minimum width of 300px
	imageUrl := ""
	imageWidth := 0
	doc.Find("img").Each(func(i int, s *goquery.Selection) {
		url, urlExists := s.Attr("src")
		if !urlExists {
			return
		}

		widthStr, widthExists := s.Attr("width")
		if !widthExists {
			widthStr, widthExists = s.Attr("data-width")
		}
		var width int
		if !widthExists {
			width = 500
		} else {
			width, err = strconv.Atoi(widthStr)
			if err != nil {
				width = 0
			}
		}

		if (imageUrl == "" || width > imageWidth) && (width > 300) {
			imageUrl = url
		}
	})
	if imageUrl == "" {
		slog.Error("[HTMLUtils] GetLargestImageUrlFromArticle: image not found")
		return "", ErrCannotParse
	}
	return imageUrl, nil
}

func ScrapeAndConvertArticleToMarkdown(url string, timeout time.Duration) (string, error) {
	html, err := GetSanitizedHtml(url, timeout)
	if err != nil {
		return "", err
	}

	// Select <article> from the HTML document
	doc, err := goquery.NewDocumentFromReader(html)
	if err != nil {
		slog.Error("[HTMLUtils] ScrapeAndConvertArticleToMarkdown:", "error", err)
		return "", ErrCannotParse
	}
	article := doc.Find("article").First()
	if article == nil {
		slog.Error("[HTMLUtils] ScrapeAndConvertArticleToMarkdown: element <article> not found")
		return "", ErrCannotParse
	}

	// Convert HTML to Markdown
	figureRule := md.Rule{
		Filter: []string{"img", "figure"},
		Replacement: func(content string, selection *goquery.Selection, opt *md.Options) *string {
			return nil
		},
	}
	converter := md.NewConverter("", true, nil)
	converter.AddRules(figureRule)
	markdown := converter.Convert(article)
	return markdown, nil
}

func GetSanitizedHtml(url string, timeout time.Duration) (*bytes.Buffer, error) {
	// Request the HTML page.
	req, err := http.NewRequest("GET", url, nil)
	if err != nil {
		slog.Error("[HTMLUtils] GetSanitizedHtml:", "error", err)
		return nil, ErrCannotFetch
	}
	req.Header.Set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36")

	client := &http.Client{Timeout: timeout}
	res, err := client.Do(req)
	if err != nil {
		slog.Error("[HTMLUtils] GetSanitizedHtml:", "error", err)
		return nil, ErrCannotFetch
	}
	defer res.Body.Close()

	if res.StatusCode != 200 {
		slog.Error("[HTMLUtils] GetSanitizedHtml: status code error:", "status code", res.StatusCode, "status", res.Status)
		return nil, ErrCannotFetch
	}

	// Sanitize HTML
	sanitizer := bluemonday.UGCPolicy()
	sanitizer.AllowStandardURLs()
	html := sanitizer.SanitizeReader(res.Body)

	return html, nil
}

func ValidateUrlAsArticle(url string, pCharCount int, pElementThreshold int, timeout time.Duration) (bool, error) {
	html, err := GetSanitizedHtml(url, timeout)
	if err != nil {
		return false, err
	}

	// Select <article> from the HTML document
	doc, err := goquery.NewDocumentFromReader(html)
	if err != nil {
		slog.Error("[HTMLUtils] ScrapeAndConvertArticleToMarkdown:", "error", err)
		return false, errors.New("cannot parse HTML")
	}

	pCount := 0
	doc.Find("p").Each(func(i int, s *goquery.Selection) {
		// Only count <p> elements with more than 300 characters
		if len(s.Text()) >= pCharCount {
			pCount++
		}
	})
	return pCount >= pElementThreshold, nil
}

func CheckAndCompilePublisher(url string, timeout time.Duration) (*domain.Publisher, error) {
	client := &http.Client{Timeout: timeout}
	resp, err := client.Get(url)
	if err != nil {
		slog.Error("[Webscrape] CheckAndCompilePublisher: URL does not respond:", "error", err)
		return nil, err
	}
	defer resp.Body.Close()

	if resp.StatusCode == http.StatusOK {
		slog.Info("[Webscrape] CheckAndCompilePublisher: URL is reachable", "url", url)
	} else {
		slog.Info("[Webscrape] CheckAndCompilePublisher: URL is not reachable", "url", url, "status code", resp.StatusCode)
		return nil, errors.New("URL is not reachable")
	}

	publisherName, err := GetPublisherNameFromSubdomainsAndDomain(url)
	if err != nil {
		slog.Error("[Webscrape] CheckAndCompilePublisher: Error parsing URL:", "error", err)
		return nil, err
	}
	return &domain.Publisher{
		Id:        -1,
		Name:      publisherName,
		Url:       url,
		AvatarUrl: "",
	}, nil
}

func GetPublisherNameFromSubdomainsAndDomain(urlStr string) (string, error) {
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

func GetFullURL(strippedURL string) (string, error) {
	// Prepend https:// and try parsing the URL
	fullURL := fmt.Sprintf("https://%s", strippedURL)
	parsedURL, err := url.Parse(fullURL)
	if err != nil {
		// If parsing with https fails, try http://
		fullURL = fmt.Sprintf("http://%s", strippedURL)
		parsedURL, err = url.Parse(fullURL)
		if err != nil {
			return "", err
		}
	}
	// Check if the scheme is set (http or https)
	if parsedURL.Scheme == "" {
		return "", fmt.Errorf("invalid URL format: %s", strippedURL)
	}
	return parsedURL.String(), nil
}
