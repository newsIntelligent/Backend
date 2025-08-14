package UMC.news.newsIntelligent.domain.member.dto;

import UMC.news.newsIntelligent.domain.topic.dto.TopicResponseDTO;
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
            String imageUrl,
            TopicResponseDTO.ImageSource imageSource
    ) {}

    @Builder
    public record MemberTopicPreviewListResDTO(
            Long cursor,
            Boolean hasNext,
            List<MemberTopicResponseDTO.MemberTopicPreviewResDTO> topics
    ) {}
}
