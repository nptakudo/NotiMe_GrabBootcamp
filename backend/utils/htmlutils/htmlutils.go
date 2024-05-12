package htmlutils

import (
	"bytes"
	"errors"
	md "github.com/JohannesKaufmann/html-to-markdown"
	"github.com/PuerkitoBio/goquery"
	"github.com/microcosm-cc/bluemonday"
	"log/slog"
	"net/http"
	"strconv"
)

var (
	ErrCannotFetch = errors.New("cannot fetch from url")
	ErrCannotParse = errors.New("cannot parse html")
)

func GetLargestImageUrlFromArticle(url string) (string, error) {
	html, err := GetSanitizedHtml(url)
	if err != nil {
		return "", err
	}

	// Select <article> from the HTML document
	doc, err := goquery.NewDocumentFromReader(html)
	if err != nil {
		slog.Error("[HTMLUtils] GetLargestImageUrlFromArticle:", "error", err)
		return "", ErrCannotParse
	}
	article := doc.Find("article").First()
	if article == nil {
		slog.Error("[HTMLUtils] GetLargestImageUrlFromArticle: element <article> not found")
		return "", ErrCannotParse
	}

	// Select the first image in the article
	imageUrl := ""
	imageWidth := 0
	article.Find("img").Each(func(i int, s *goquery.Selection) {
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

func ScrapeAndConvertArticleToMarkdown(url string) (string, error) {
	html, err := GetSanitizedHtml(url)
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

func GetSanitizedHtml(url string) (*bytes.Buffer, error) {
	// Request the HTML page.
	req, err := http.NewRequest("GET", url, nil)
	if err != nil {
		slog.Error("[HTMLUtils] GetSanitizedHtml:", "error", err)
		return nil, ErrCannotFetch
	}
	req.Header.Set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36")
	res, err := http.DefaultClient.Do(req)
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
