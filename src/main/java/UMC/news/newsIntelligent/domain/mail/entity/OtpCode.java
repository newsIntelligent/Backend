package UMC.news.newsIntelligent.domain.mail.entity;

import UMC.news.newsIntelligent.global.apiPayload.code.error.GeneralErrorCode;
import UMC.news.newsIntelligent.global.apiPayload.exception.CustomException;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@IdClass(OtpCode.PK.class)
public class OtpCode {

    @Id
    private String email;
    @Id @Enumerated(EnumType.STRING)
    private Type type;      // SIGNUP, LOGIN
    private String code;    // 6자리 인증 코드
    private String token;    // 매직링크용 토큰
    private LocalDateTime expiresAt;

    /** 완료 플래그:
     * otp code나 매직링크 둘 중 하나로 검증되면 true
     * → 재사용 방지 */
    @Column(nullable = false) @Builder.Default
    private Boolean verified = false;

    public enum Type { SIGNUP, LOGIN; }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class PK implements Serializable {
        private String email;
        private Type type;
    }

    public void validateUsable() {
        if (Boolean.TRUE.equals(verified))
            throw new CustomException(GeneralErrorCode.OTP_WRONG);   // 불일치
        if (expiresAt.isBefore(LocalDateTime.now()))
            throw new CustomException(GeneralErrorCode.OTP_EXPIRED);   // 만료
    }

    public void markVerified() { this.verified = true; }

}
