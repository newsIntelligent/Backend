package UMC.news.newsIntelligent.domain.member.service;

import UMC.news.newsIntelligent.domain.mail.entity.OtpCode;
import UMC.news.newsIntelligent.domain.mail.repository.OtpCodeRepository;
import UMC.news.newsIntelligent.domain.mail.service.MailService;
import UMC.news.newsIntelligent.domain.member.Member;
import UMC.news.newsIntelligent.domain.member.repository.MemberRepository;
import UMC.news.newsIntelligent.domain.member.dto.TokenResponseDto;
import UMC.news.newsIntelligent.global.apiPayload.code.error.GeneralErrorCode;
import UMC.news.newsIntelligent.global.apiPayload.exception.CustomException;
import UMC.news.newsIntelligent.global.config.security.jwt.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static UMC.news.newsIntelligent.domain.mail.entity.OtpCode.Type.LOGIN;
import static UMC.news.newsIntelligent.domain.mail.entity.OtpCode.Type.SIGNUP;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepo;
    private final OtpCodeRepository otpRepo;
    private final MailService mail;
    private final JwtTokenProvider jwt;

    /* ------- 메일 발송 (공통) ------- */
    public void sendCode(String email, OtpCode.Type type) {

        /* 1) 회원가입 메일: 이미 가입된 주소면 차단 */
        if (type == SIGNUP && memberRepo.existsByEmail(email)) {
            // 추후 커스텀 에러 추가 예정
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }

        /* 2) 로그인 메일: 가입되지 않은 주소(또는 탈퇴 계정)면 차단 */
        if (type == LOGIN) {
            Member m = memberRepo.findByEmail(email)
                    // 추후 커스텀 에러 추가 예정
                    .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));
            if (Boolean.TRUE.equals(m.getIsDeactivated())) {
                // 추후 커스텀 에러 추가 예정
                throw new IllegalStateException("탈퇴한 계정입니다.");
            }
        }

        /* 3) OTP·토큰 생성 및 저장 */
        String code  = mail.generateCode();
        String token = mail.generateToken();

        otpRepo.save(OtpCode.builder()
                .email(email).type(type)
                .code(code).token(token)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build());

        /* 4) 메일 발송 */
        mail.sendOtpMail(email, code, token, type);
    }

    /* ------- 숫자 코드 검증 ------- */
    public Member signupByCode(String email, String code) {
        OtpCode otp = getOtp(email, SIGNUP);
        otp.validateUsable();
        if (!otp.getCode().equals(code)) throw new IllegalArgumentException("코드 불일치"); // 추후 커스텀 에러 추가 예정
        return completeSignup(otp);
    }

    public TokenResponseDto loginByCode(String email, String code) {
        OtpCode otp = getOtp(email, LOGIN);
        otp.validateUsable();

        // 코드 일치 여부 확인
        if (!otp.getCode().equals(code)) {
            throw new CustomException(GeneralErrorCode.OTP_WRONG);
        }
        return completeLogin(otp);
    }

    /* ------- 매직링크 검증 ------- */
    public Member signupByToken(String token) {
        OtpCode otp = otpRepo.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("토큰 없음")); // 추후 커스텀 에러 추가 예정
        otp.validateUsable();
        return completeSignup(otp);
    }

    public TokenResponseDto loginByToken(String token) {
        OtpCode otp = otpRepo.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("토큰 없음")); // 추후 커스텀 에러 추가 예정
        otp.validateUsable();
        return completeLogin(otp);
    }

    /* ------- 공통 후처리 ------- */
    private Member completeSignup(OtpCode otp) {
        Member m = memberRepo.save(Member.newMember(otp.getEmail()));
        otp.markVerified();  otpRepo.save(otp);
        return m;
    }

    private TokenResponseDto completeLogin(OtpCode otp) {
        Member m = memberRepo.findByEmail(otp.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("회원 없음")); // 추후 커스텀 에러 추가 예정
        m.updateLastLogin();  memberRepo.save(m);
        otp.markVerified();   otpRepo.save(otp);

        String access = jwt.generateAccessToken(m.getId(), m.getEmail(), "ROLE_USER");
        return new TokenResponseDto(access);
    }

    private OtpCode getOtp(String email, OtpCode.Type type) {
        return otpRepo.findById(new OtpCode.PK(email, type))
                .orElseThrow(() -> new IllegalStateException("인증 데이터 없음")); // 추후 커스텀 에러 추가 예정
    }
}
