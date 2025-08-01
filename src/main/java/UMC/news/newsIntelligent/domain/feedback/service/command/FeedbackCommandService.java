package UMC.news.newsIntelligent.domain.feedback.service.command;

import UMC.news.newsIntelligent.domain.feedback.dto.FeedbackRequestDTO;

public interface FeedbackCommandService {
    void submitFeedback(FeedbackRequestDTO.FeedbackRequest request, Long memberId);
}
