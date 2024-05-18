package webscrape

import (
	"errors"
	"github.com/PuerkitoBio/goquery"
	"log/slog"
	"notime/utils/htmlutils"
)

func ValidateUrlAsArticle(url string, pCharCount int, pElementThreshold int) (bool, error) {
	html, err := htmlutils.GetSanitizedHtml(url)
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
