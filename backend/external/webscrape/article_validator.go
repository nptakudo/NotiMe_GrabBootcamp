package webscrape

import (
	"errors"
	"github.com/PuerkitoBio/goquery"
	"log/slog"
	"notime/utils/htmlutils"
)

func ValidateUrlAsArticle(url string, pElementThreshold int) (bool, error) {
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
	pElements := doc.Find("p")
	return pElements.Length() >= pElementThreshold, nil
}
