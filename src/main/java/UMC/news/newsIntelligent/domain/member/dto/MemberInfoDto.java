package UMC.news.newsIntelligent.domain.member.dto;

import UMC.news.newsIntelligent.domain.member.entity.Member;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MemberInfoDto(
        String email,
        Boolean subscribe_topic_alert,
        Boolean read_topic_alert,
        Boolean daily_report_alert,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Boolean is_deactivated
) {
    public static MemberInfoDto from(Member member) {
        return MemberInfoDto.builder()
                .email(member.getEmail())
                .subscribe_topic_alert(member.getSubscribeTopicAlert())
                .read_topic_alert(member.getReadTopicAlert())
                .daily_report_alert(member.getDailyReportAlert())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .is_deactivated(member.getIsDeactivated())
                .build();
    }
}