package UMC.news.newsIntelligent.domain.feedback.controller;

import UMC.news.newsIntelligent.domain.feedback.code.FeedbackSuccessCode;
import UMC.news.newsIntelligent.domain.feedback.dto.FeedbackRequestDTO;
import UMC.news.newsIntelligent.domain.feedback.service.command.FeedbackCommandService;
import UMC.news.newsIntelligent.global.apiPayload.CustomResponse;
import UMC.news.newsIntelligent.global.apiPayload.exception.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "피드백 컨트롤러", description = "피드백 관련 API")
public class FeedbackController {

    private final FeedbackCommandService feedbackCommandService;

    @Operation(summary = "피드백 작성", description = "사용자가 불편함을 느끼는 피드백을 받는 API")
    @PostMapping("/feedbacks")
    public CustomResponse<?> submitFeedback(@RequestBody FeedbackRequestDTO.FeedbackRequest request) {
        try {
            feedbackCommandService.submitFeedback(request);
            return CustomResponse.onSuccess(FeedbackSuccessCode.FEEDBACK_CREATED);

        } catch (CustomException e) {
            return CustomResponse.onFailure(e.getCode());
        }
    }
}
