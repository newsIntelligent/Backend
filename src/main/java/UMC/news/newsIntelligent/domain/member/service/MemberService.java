package UMC.news.newsIntelligent.domain.member.service;

import UMC.news.newsIntelligent.domain.member.entity.Member;
import UMC.news.newsIntelligent.domain.member.entity.RevokedToken;
import UMC.news.newsIntelligent.domain.member.repository.MemberRepository;
import UMC.news.newsIntelligent.domain.member.repository.RevokedTokenRepository;
import UMC.news.newsIntelligent.global.apiPayload.code.error.GeneralErrorCode;
import UMC.news.newsIntelligent.global.apiPayload.exception.CustomException;
import UMC.news.newsIntelligent.global.config.security.jwt.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RevokedTokenRepository revokedTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /* --- 로그아웃 --- */
    public void logout(String accessToken) {
        String jwtId = jwtTokenProvider.getJwtId(accessToken);
        LocalDateTime exp = jwtTokenProvider.getExpiration(accessToken)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        revokedTokenRepository.save(new RevokedToken(jwtId, exp));
    }

    /* --- 회원 탈퇴 --- */
    public void withdraw(Member member, String accessToken) {
        // 탈퇴 여부 확인
        if (member.isDeactivated()) {
            throw new CustomException(GeneralErrorCode.ALREADY_DEACTIVATED);
        }

        member.deactivate();    // isDeactivated = true로 설정
        memberRepository.save(member);
        logout(accessToken);
    }
}
