package UMC.news.newsIntelligent.domain.member.dto;

import UMC.news.newsIntelligent.domain.member.Member;

public record MemberResponseDto(Long id, String email) {
    public static MemberResponseDto from(Member member) {
        return new MemberResponseDto(member.getId(), member.getEmail());
    }
}
