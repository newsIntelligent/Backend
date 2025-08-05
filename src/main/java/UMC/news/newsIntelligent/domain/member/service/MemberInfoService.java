package UMC.news.newsIntelligent.domain.member.service;

import UMC.news.newsIntelligent.domain.member.converter.MemberInfoConverter;
import UMC.news.newsIntelligent.domain.member.dto.MemberInfoDto;
import UMC.news.newsIntelligent.domain.member.entity.Member;
import UMC.news.newsIntelligent.domain.member.repository.MemberRepository;
import UMC.news.newsIntelligent.global.apiPayload.code.error.ErrorCode;
import UMC.news.newsIntelligent.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberInfoService {

    private final MemberRepository memberRepository;

    /* 회원 정보 조회 */
    public MemberInfoDto.MemberInfoResponse getInfo(Long memberId) {
        Member member = findActive(memberId);
        return MemberInfoConverter.toDto(member);
    }

    /* 닉네임 유효성 검사 */
    public MemberInfoDto.NicknameAvailabilityResponse checkNicknameAvailability(String nickname) {
        String nick = nickname.trim();
        boolean available = !memberRepository.existsByNickname(nick);
        return new MemberInfoDto.NicknameAvailabilityResponse(nick, available);
    }

    /* 닉네임 변경 */
    @Transactional
    public MemberInfoDto.MemberInfoResponse updateNickname(Long memberId, String nickname) {
        String nick = nickname.trim();
        Member member = findActive(memberId);

        if (memberRepository.existsByNickname(nick))
            throw new CustomException(ErrorCode.NICKNAME_DUPLICATED);

        try {
            member.changeNickname(nick);
            return MemberInfoConverter.toDto(member);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.BAD_REQUEST_400);
        }
    }

    private Member findActive(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        if (member.isDeactivated()) throw new CustomException(ErrorCode.MEMBER_ALREADY_DEACTIVATED);
        return member;
    }
}
