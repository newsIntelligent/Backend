package UMC.news.newsIntelligent.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class MemberTopicResponseDTO {

    @Builder
    public record MemberTopicPreviewResDTO(
            Long id,
            String topicName,
            String aiSummary,
            LocalDateTime summaryTime,
            String imageUrl
    ) {}

    @Builder
    public record MemberTopicPreviewListResDTO(
            Long cursor,
            Boolean hasNext,
            List<MemberTopicResponseDTO.MemberTopicPreviewResDTO> topics
    ) {}
}
