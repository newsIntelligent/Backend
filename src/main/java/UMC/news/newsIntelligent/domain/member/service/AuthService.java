package UMC.news.newsIntelligent.domain.member.service;

import UMC.news.newsIntelligent.domain.mail.entity.OtpCode;
import UMC.news.newsIntelligent.domain.mail.repository.OtpCodeRepository;
import UMC.news.newsIntelligent.domain.mail.service.MailService;
import UMC.news.newsIntelligent.domain.member.dto.MemberResponseDto;
import UMC.news.newsIntelligent.domain.member.entity.Member;
import UMC.news.newsIntelligent.domain.member.entity.RevokedToken;
import UMC.news.newsIntelligent.domain.member.repository.MemberRepository;
import UMC.news.newsIntelligent.domain.member.dto.TokenResponseDto;
import UMC.news.newsIntelligent.domain.member.repository.RevokedTokenRepository;
import UMC.news.newsIntelligent.global.apiPayload.code.error.ErrorCode;
import UMC.news.newsIntelligent.global.apiPayload.exception.CustomException;
import UMC.news.newsIntelligent.global.config.security.jwt.JwtTokenProvider;
import UMC.news.newsIntelligent.global.utils.AuthUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static UMC.news.newsIntelligent.domain.mail.entity.OtpCode.Type.LOGIN;
import static UMC.news.newsIntelligent.domain.mail.entity.OtpCode.Type.SIGNUP;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final OtpCodeRepository otpCodeRepository;
    private final MailService mailService;
    private final RevokedTokenRepository revokedTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthUtils authUtils;

    /* 메일 발송 (공통) */
    public void sendCode(String email, OtpCode.Type type) {

        // 1) 회원가입 메일: 이미 가입된 주소면 차단
        if (type == SIGNUP && memberRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.MEMBER_ALREADY_EXIST);
        }

        // 2) 로그인 메일: 가입되지 않은 주소(또는 탈퇴 계정)면 차단
        if (type == LOGIN) {
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));;
            if (Boolean.TRUE.equals(member.getIsDeactivated())) {
                throw new CustomException(ErrorCode.MEMBER_ALREADY_DEACTIVATED);
            }
        }

        // 3) OTP·토큰 생성 및 저장
        String code  = mailService.generateCode();
        String token = mailService.generateToken();

        otpCodeRepository.deleteByEmailAndType(email, type); // 기존 발급 삭제
        otpCodeRepository.save(OtpCode.builder()
                .email(email)
                .type(type)
                .code(code)
                .token(token)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build());

        // 4) 메일 발송
        mailService.sendOtpMail(email, code, token, type);
    }

    /* 회원가입 코드 검증 */
    public TokenResponseDto signupByCode(String email, String code) {
        OtpCode otp = authUtils.getOtpCode(email, SIGNUP);

        if (!otp.getCode().equals(code))
            throw new CustomException(ErrorCode.OTP_WRONG);

        Member member = authUtils.saveAndReturnMember(email);

        otp.markVerified();
        otpCodeRepository.delete(otp);

        return authUtils.generateToken(member);
    }

    /* 로그인 코드 검증 */
    public TokenResponseDto loginByCode(String email, String code) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (member.isDeactivated())
            throw new CustomException(ErrorCode.MEMBER_ALREADY_DEACTIVATED);

        OtpCode otpCode = authUtils.getOtpCode(email, LOGIN);

        if (!otpCode.getCode().equals(code))
            throw new CustomException(ErrorCode.OTP_WRONG);

        otpCodeRepository.delete(otpCode);

        return authUtils.generateToken(member);
    }

    /* 회원가입 매직링크 검증 */
    public TokenResponseDto signupByToken(String token) {
        OtpCode otp = otpCodeRepository.findByTokenAndType(token, SIGNUP)
                .orElseThrow(() ->  new CustomException(ErrorCode.BAD_REQUEST_400));
        otp.validateUsable();

        Member member = authUtils.saveAndReturnMember(otp.getEmail());

        otp.markVerified();
        otpCodeRepository.delete(otp);

        return authUtils.generateToken(member);
    }

    /* 로그인 매직링크 검증 */
    public TokenResponseDto loginByToken(String token) {
        OtpCode otp = otpCodeRepository.findByTokenAndType(token, LOGIN)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST_400));
        otp.validateUsable();

        Member member = memberRepository.findByEmail(otp.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        if (member.isDeactivated()) throw new CustomException(ErrorCode.MEMBER_ALREADY_DEACTIVATED);

        otpCodeRepository.delete(otp);
        return authUtils.generateToken(member);
    }

    /* 로그아웃 */
    public void logout(String accessToken) {
        String jwtId = jwtTokenProvider.getJwtId(accessToken);
        LocalDateTime exp = jwtTokenProvider.getExpiration(accessToken)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        revokedTokenRepository.save(new RevokedToken(jwtId, exp));
    }

    /* 회원 탈퇴 */
    public void withdraw(Member member, String accessToken) {
        // 탈퇴 여부 확인
        if (member.isDeactivated()) {
            throw new CustomException(ErrorCode.MEMBER_ALREADY_DEACTIVATED);
        }

        member.deactivate();    // isDeactivated = true로 설정
        memberRepository.save(member);
        logout(accessToken);
    }

}
