package UMC.news.newsIntelligent.domain.news.dto;

import UMC.news.newsIntelligent.domain.topic.dto.TopicResponseDTO;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class NewsResponseDTO {


    @Builder
    public record NewsResDTO(
            List<NewsListResDTO> content,
            int totalCount,
            Long lastId,
            int size,
            boolean hasNext
    ) {}

    @Builder
    public record NewsListResDTO(
            Long id,
            String title,
            String newsSummary,
            String newsLink,
            LocalDateTime publishDate,
            String press,
            String pressLogoUrl
    ) {}

    @Builder
    public record NewsRelatedArticleDto (
            Long id,
            String press,
            String title,
            String newsSummary,
            String newLink,
            LocalDateTime publishDate
    ) {}

    // “조건을 만족하는 토픽 묶음 중 최신 1개” 반환용 DTO
    @Builder
    public record TopicQualifiedItemResDTO(
            Long id,
            String topicName,
            String aiSummary,
            String imageUrl,
            LocalDateTime summaryTime,
            ImageSource imageSource,
            boolean isSub,
            List<NewsRelatedArticleDto> relatedArticles
    ) {}

    @Builder
    public record TopicQualifiedListResDTO(
            List<TopicQualifiedItemResDTO> items
    ) {}

    @Builder
    public record ImageSource(
            String press,
            String title
    ) {}
}
