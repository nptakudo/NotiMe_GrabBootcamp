package geminiutils

import (
	"context"
	"errors"
	"fmt"
	"github.com/google/generative-ai-go/genai"
	"google.golang.org/api/option"
	"log/slog"
	"notime/bootstrap"
)

const (
	characterLowerThreshold = 100
	characterUpperThreshold = 200
	summarizePrompt         = "In one short paragraph less than 200 characters, concisely summarize the whole article. Include important numbers, quotes. The article is: %s"
)

var (
	ErrCannotGenerateSummary = errors.New("cannot generate summary")
)

func GenerateArticleSummary(env *bootstrap.Env, content string) (string, error) {
	ctx := context.Background()
	client, err := genai.NewClient(ctx, option.WithAPIKey(env.LlmApiKey))
	if err != nil {
		slog.Error("[GeminiUtils] GenerateArticleSummary:", "error", err)
		return "", ErrCannotGenerateSummary
	}
	defer client.Close()

	model := client.GenerativeModel("gemini-pro")
	prompt := genai.Text(fmt.Sprintf(summarizePrompt, content))
	resp, err := model.GenerateContent(ctx, prompt)
	if err != nil {
		slog.Error("[GeminiUtils] GenerateArticleSummary:", "error", err)
		return "", ErrCannotGenerateSummary
	}

	summary := ""
	for _, candidate := range resp.Candidates {
		// Parts[0] is for text output, other parts are for multi-model output (which we don't need)
		text := fmt.Sprint(candidate.Content.Parts[0])

		// Only consider text with length >= lowerThreshold. This is to avoid non-summary text (e.g. "As a LLM, I cannot bla bla.")
		// Prefer text with length between lowerThreshold and upperThreshold
		if summary == "" && len(text) >= characterLowerThreshold {
			summary = text
		} else if len(summary) > characterUpperThreshold && len(text) >= characterLowerThreshold && len(text) <= characterUpperThreshold {
			summary = text
		}
		if len(summary) >= characterLowerThreshold && len(summary) <= characterUpperThreshold {
			break
		}
	}
	return summary, nil
}
