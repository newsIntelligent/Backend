package UMC.news.newsIntelligent.global.apiPayload.code.success;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode implements BaseSuccessCode{
    OK(HttpStatus.OK, "COMMON200", "성공적으로 처리했습니다."),
    CREATED(HttpStatus.CREATED, "COMMON201", "성공적으로 생성했습니다."),
    NO_CONTENT_204(HttpStatus.NO_CONTENT, "COMMON204", "성공했지만 콘텐츠는 없습니다."),

    // 인증
    EMAIL_SENT          (HttpStatus.OK, "AUTH200", "인증 메일을 발송했습니다."),
    SIGNUP_SUCCESS      (HttpStatus.OK, "AUTH201", "회원가입이 완료되었습니다."),
    LOGIN_SUCCESS       (HttpStatus.OK, "AUTH202", "로그인에 성공했습니다."),
    LOGOUT_SUCCESS   (HttpStatus.OK, "AUTH203", "로그아웃이 완료되었습니다."),
    WITHDRAW_SUCCESS (HttpStatus.OK, "AUTH204", "회원 탈퇴가 완료되었습니다."),
    OTP_RIGHT (HttpStatus.OK, "AUTH205", "코드 검증에 성공했습니다."),

    // 회원
    GET_MEMBER_INFO (HttpStatus.OK, "MEMBER200", "회원 정보 조회가 완료되었습니다."),
    NICKNAME_CHANGED (HttpStatus.OK, "MEMBER201", "닉네임이 변경되었습니다."),
    NICKNAME_VALID (HttpStatus.OK, "MEMBER202", "사용 가능한 닉네임입니다."),


    // 토픽
    GET_TOPIC(HttpStatus.OK, "TOPIC200", "토픽 상세 페이지 조회가 완료되었습니다."),
    GET_NEWS(HttpStatus.OK, "NEWS200", "토픽 출처 기사 목록 조회가 완료되었습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
