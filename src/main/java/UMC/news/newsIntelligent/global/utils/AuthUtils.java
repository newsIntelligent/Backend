package UMC.news.newsIntelligent.global.utils;

import UMC.news.newsIntelligent.domain.mail.entity.OtpCode;
import UMC.news.newsIntelligent.domain.mail.repository.OtpCodeRepository;
import UMC.news.newsIntelligent.domain.member.dto.TokenResponseDto;
import UMC.news.newsIntelligent.domain.member.entity.Member;
import UMC.news.newsIntelligent.domain.member.repository.MemberRepository;
import UMC.news.newsIntelligent.domain.member.service.NicknameService;
import UMC.news.newsIntelligent.global.apiPayload.code.error.ErrorCode;
import UMC.news.newsIntelligent.global.apiPayload.exception.CustomException;
import UMC.news.newsIntelligent.global.config.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class AuthUtils {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final OtpCodeRepository otpCodeRepository;
    private final NicknameService nicknameService;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    /* 만료시각 포매팅 */
    public String toKstIso(Date exp) {
        return exp.toInstant().atZone(KST).format(FORMATTER);
    }

    /* OTP 조회 */
    public OtpCode getOtpCode(String email, OtpCode.Type type) {
        OtpCode otp = otpCodeRepository.findByEmailAndType(email, OtpCode.Type.SIGNUP)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST_400));
        otp.validateUsable();

        return otp;
    }

    /* 회원 아니면 새로 생성 */
    public Member saveAndReturnMember(String email) {
        return memberRepository.findByEmail(email).orElseGet(() -> {
            String nickname = nicknameService.proposeFromEmail(email);
            return memberRepository.save(
                    Member.builder()
                            .email(email)
                            .notificationEmail(email)
                            .nickname(nickname)
                            .subscribeTopicAlert(true)
                            .readTopicAlert(true)
                            .dailyReportAlert(true)
                            .isDeactivated(false)
                            .build()
            );
        });
    }

    /* 토큰 발급 */
    public TokenResponseDto generateToken(Member member) {
        String token = jwtTokenProvider.generateAccessToken(member.getId(), member.getEmail(), "ROLE_USER");

        String expIsoKst = toKstIso(jwtTokenProvider.getExpiration(token));

        member.updateLastLogin();
        memberRepository.save(member);

        return TokenResponseDto.builder()
                .accessToken(token)
                .expiresAt(expIsoKst)
                .build();
    }

}
