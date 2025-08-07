package UMC.news.newsIntelligent.domain.member.converter;

import UMC.news.newsIntelligent.domain.member.dto.MemberInfoDto;
import UMC.news.newsIntelligent.domain.member.entity.Member;

public class MemberInfoConverter {

    public static MemberInfoDto.MemberInfoResponse toDto(Member member) {
        return MemberInfoDto.MemberInfoResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .notificationEmail(member.getNotificationEmail())
                .nickname(member.getNickname())
                .subscribe_topic_alert(member.getSubscribeTopicAlert())
                .read_topic_alert(member.getReadTopicAlert())
                .daily_report_alert(member.getDailyReportAlert())
                .is_deactivated(member.isDeactivated())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();

    }
}
