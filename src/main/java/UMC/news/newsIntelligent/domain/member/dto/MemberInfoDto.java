package UMC.news.newsIntelligent.domain.member.dto;

import java.time.LocalDateTime;

public record MemberInfoDto(
        String  email,
        Boolean subscribe_topic_alert,
        Boolean read_topic_alert,
        Boolean daily_report_alert,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Boolean is_deactivated
) {}