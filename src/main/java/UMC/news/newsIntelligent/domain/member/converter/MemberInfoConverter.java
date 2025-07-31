package UMC.news.newsIntelligent.domain.member.converter;

import UMC.news.newsIntelligent.domain.member.dto.MemberInfoDto;
import UMC.news.newsIntelligent.domain.member.entity.Member;

import java.util.List;
import java.util.stream.Collectors;

public class MemberInfoConverter {
    private MemberInfoConverter() {}   // 인스턴스화 방지

    public static MemberInfoDto toDto(Member member) {
        return new MemberInfoDto(
                member.getEmail(),
                member.getSubscribeTopicAlert(),
                member.getReadTopicAlert(),
                member.getDailyReportAlert(),
                member.getCreatedAt(),
                member.getUpdatedAt(),
                member.getIsDeactivated()
        );
    }

    /* entity -> dto  */
    public static List<MemberInfoDto> toDtoList(List<Member> members) {
        return members.stream()
                .map(MemberInfoConverter::toDto)
                .collect(Collectors.toList());
    }
}
