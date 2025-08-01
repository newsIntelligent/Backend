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
    LOGIN_SUCCESS       (HttpStatus.OK, "MEMBER202", "로그인에 성공했습니다."),
    LOGOUT_SUCCESS   (HttpStatus.OK, "MEMBER203", "로그아웃이 완료되었습니다."),
    WITHDRAW_SUCCESS (HttpStatus.OK, "MEMBER204", "회원 탈퇴가 완료되었습니다."),

    // 토픽
    GET_TOPIC(HttpStatus.OK, "TOPIC200", "토픽 상세 페이지 조회가 완료되었습니다."),
    GET_NEWS(HttpStatus.OK, "NEWS200", "토픽 출처 기사 목록 조회가 완료되었습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
