package models

import "time"

type ScrapedArticle struct {
	Url           string    `json:"url"`
	Title         string    `json:"title"`
	Date          time.Time `json:"date"`
	Content       string    `json:"content"`
	PublisherName string    `json:"publisher_name"`
}
