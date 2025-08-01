package UMC.news.newsIntelligent.global.apiPayload.code.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum GeneralErrorCode implements BaseErrorCode{

    BAD_REQUEST_400(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다"),

    UNAUTHORIZED_401(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다"),

    FORBIDDEN_403(HttpStatus.FORBIDDEN, "COMMON403", "접근이 금지되었습니다"),

    NOT_FOUND_404(HttpStatus.NOT_FOUND, "COMMON404", "요청한 자원을 찾을 수 없습니다"),

    INTERNAL_SERVER_ERROR_500(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 내부 오류가 발생했습니다"),

    // 유효성 검사
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALID400_0", "잘못된 파라미터 입니다."),
    // 커서 에러
    CURSOR_INVALID(HttpStatus.BAD_REQUEST, "CURSOR400", "커서가 유효하지 않습니다."),
    // 알림 에러
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION400", "해당 알림을 찾을 수 없습니다."),

    /* --- 회원/인증 관련 에러 ---*/
    OTP_WRONG      ( HttpStatus.BAD_REQUEST, "AUTH401", "인증번호가 일치하지 않습니다."),
    OTP_EXPIRED    ( HttpStatus.BAD_REQUEST, "AUTH402",  "인증번호가 만료되었습니다."),
    ALREADY_DEACTIVATED ( HttpStatus.BAD_REQUEST, "MEMBER403", "이미 탈퇴한 계정입니다."),
    MEMBER_NOT_FOUND (HttpStatus.BAD_REQUEST, "MEMBER404", "존재하지 않는 회원입니다."),

    // 토픽 에러
    TOPIC_NOT_FOUND(HttpStatus.NOT_FOUND, "TOPIC404_1", "존재하지 않는 토픽입니다.")
    ;

    // 필요한 필드값 선언
    private final HttpStatus status;
    private final String code;
    private final String message;
}
