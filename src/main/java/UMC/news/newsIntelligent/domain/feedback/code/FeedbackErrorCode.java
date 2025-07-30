package UMC.news.newsIntelligent.domain.feedback.code;

import UMC.news.newsIntelligent.global.apiPayload.code.error.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FeedbackErrorCode implements BaseErrorCode {
    EMPTY_CONTENT(HttpStatus.BAD_REQUEST, "4001", "내용이 비어 있습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
