package UMC.news.newsIntelligent.global.apiPayload.code.success;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GeneralSuccessCode implements BaseSuccessCode{
    OK(HttpStatus.OK, "COMMON200", "성공적으로 처리했습니다."),
    CREATED(HttpStatus.CREATED, "COMMON201", "성공적으로 생성했습니다."),
    NO_CONTENT_204(HttpStatus.NO_CONTENT, "COMMON204", "성공했지만 콘텐츠는 없습니다."),

    /* --- 회원/인증 관련 --- */
    EMAIL_SENT          (HttpStatus.OK, "MEMBER200", "인증 메일을 발송했습니다."),
    SIGNUP_SUCCESS      (HttpStatus.OK, "MEMBER201", "회원가입이 완료되었습니다."),
    LOGIN_SUCCESS       (HttpStatus.OK, "MEMBER202", "로그인에 성공했습니다.");
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
