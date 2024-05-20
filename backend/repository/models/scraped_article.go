package models

import "time"

type ScrapedArticle struct {
	Url           string `json:"url"`
	Title         string `json:"title"`
	Date          string `json:"date"`
	Content       string `json:"content"`
	PublisherName string `json:"publisher_name"`
}

func (s *ScrapedArticle) GetTime() (time.Time, error) {
	t, err := time.Parse("January 2, 2006", s.Date)
	if err != nil {
		return time.Time{}, err
	}
	return t, nil
}

func (s *ScrapedArticle) SetTime(t time.Time) {
	s.Date = t.Format("January 2, 2006")
}
