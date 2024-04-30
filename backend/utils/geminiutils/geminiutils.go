package geminiutils

import (
	"context"
	"errors"
	"fmt"
	"github.com/google/generative-ai-go/genai"
	"google.golang.org/api/option"
	"log"
	"notime/bootstrap"
)

const (
	summarizePrompt         = "In 1 paragraph, summarize the whole article. Include important numbers, quotes. The article is: %s"
	characterLowerThreshold = 100
	characterUpperThreshold = 300
)

var (
	ErrCannotGenerateSummary = errors.New("cannot generate summary")
)

func GenerateArticleSummary(env *bootstrap.Env, content string) (string, error) {
	ctx := context.Background()
	// Access your API key as an environment variable (see "Set up your API key" above)
	client, err := genai.NewClient(ctx, option.WithAPIKey(env.LlmApiKey))
	if err != nil {
		log.Fatal(err)
	}
	defer client.Close()

	model := client.GenerativeModel("gemini-pro")
	resp, err := model.GenerateContent(ctx, genai.Text(fmt.Sprintf(summarizePrompt, content)))
	if err != nil {
		log.Fatal(err)
	}

	candidates := resp.Candidates
	if len(candidates) == 0 {
		return "", ErrCannotGenerateSummary
	}

	summary := ""
	for _, candidate := range candidates {
		text := fmt.Sprint(candidate.Content.Parts[0])
		if summary == "" && len(text) >= characterLowerThreshold {
			summary = text
		} else if len(summary) > characterUpperThreshold && len(text) >= characterLowerThreshold && len(text) <= characterUpperThreshold {
			summary = text
		}
	}
	return summary, nil
}
