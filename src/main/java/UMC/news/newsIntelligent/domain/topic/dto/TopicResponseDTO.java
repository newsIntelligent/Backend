package UMC.news.newsIntelligent.domain.topic.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class TopicResponseDTO {

    @Builder
    public record TopicPreviewResDTO(
            Long id,
            String topicName,
            String aiSummary,
            LocalDateTime summaryTime,
            String imageUrl
    ) {}

    @Builder
    public record TopicPreviewListResDTO(
            Long cursor,
            Boolean hasNext,
            List<TopicPreviewResDTO> topics
    ) {}

    @Builder
    public record TopicDetailsResDTO(
            Long id,
            String topicName,
            String aiSummary,
            String imageUrl,
//            boolean idSub,
            LocalDateTime summaryTime
//            LocalDateTime createdAt,
//            LocalDateTime updatedAt
    ) {}
}
