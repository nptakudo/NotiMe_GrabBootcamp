package models

import (
	"log/slog"
	"strings"
	"time"
)

type ScrapedArticle struct {
	Url           string `json:"url"`
	Title         string `json:"title"`
	Date          string `json:"date"`
	Content       string `json:"content"`
	PublisherName string `json:"publisher_name"`
}

func (s *ScrapedArticle) GetTime() (time.Time, error) {
	t := time.Time{}
	var err error = nil

	slog.Info("Date", "date", s.Date)
	if strings.Contains(s.Date, "|") {
		slog.Info("Date contains |")
		t, err = time.Parse("2 Jan 2006", strings.Split(s.Date, " | ")[0])
	} else {
		t, err = time.Parse("January 2, 2006", s.Date)
	}
	if err != nil {
		return time.Time{}, err
	}
	t = t.UTC()
	return t, nil
}

func (s *ScrapedArticle) SetTime(t time.Time) {
	s.Date = t.Format("January 2, 2006")
}
