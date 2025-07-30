package UMC.news.newsIntelligent.domain.feedback.code;

import UMC.news.newsIntelligent.global.apiPayload.code.success.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FeedbackSuccessCode implements BaseSuccessCode {
    FEEDBACK_CREATED(HttpStatus.CREATED, "FEEDBACK2001", "성공적으로 피드백이 생성했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
