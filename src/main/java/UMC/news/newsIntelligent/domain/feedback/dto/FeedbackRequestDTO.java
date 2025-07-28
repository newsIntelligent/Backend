package UMC.news.newsIntelligent.domain.feedback.dto;

import lombok.Builder;

public class FeedbackRequestDTO {

    @Builder
    public record FeedbackRequest(
       String content
    ) {}
}
