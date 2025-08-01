package UMC.news.newsIntelligent.domain.feedback.controller;

import UMC.news.newsIntelligent.domain.feedback.code.FeedbackSuccessCode;
import UMC.news.newsIntelligent.domain.feedback.dto.FeedbackRequestDTO;
import UMC.news.newsIntelligent.domain.feedback.service.command.FeedbackCommandService;
import UMC.news.newsIntelligent.domain.member.entity.Member;
import UMC.news.newsIntelligent.global.apiPayload.CustomResponse;
import UMC.news.newsIntelligent.global.apiPayload.exception.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "피드백 API", description = "피드백 작성")
public class FeedbackController {

    private final FeedbackCommandService feedbackCommandService;

    /* --- 피드백 작성 --- */
    @Operation(summary = "피드백 작성", description = "사용자가 불편함을 느끼는 피드백을 받는 API입니다.")
    @PostMapping("/feedbacks")
    public CustomResponse<?> submitFeedback(
            @RequestBody FeedbackRequestDTO.FeedbackRequest request,
            @AuthenticationPrincipal Member member
    ) {
        try {
            feedbackCommandService.submitFeedback(request, member.getId());
            return CustomResponse.onSuccess(FeedbackSuccessCode.FEEDBACK_CREATED);

        } catch (CustomException e) {
            return CustomResponse.onFailure(e.getCode());
        }
    }
}
