package UMC.news.newsIntelligent.domain.news.dto;

import lombok.Builder;

import java.util.List;

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
            String publishDate,
            String press,
            String imageUrl,
            String imageSource
    ) {}

    public record NewsRelatedArticleDto (
            Long id,
            String press,
            String title,
            String newsSummary,
            String newLink,
            String publishDate
    ) {}

    // “조건을 만족하는 토픽 묶음 중 최신 1개” 반환용 DTO
    public record TopicQualifiedItemResDTO(
            Long id,                     // topic_id
            String title,
            String newsSummary,
            String imageUrl,
            String publish_date,
            List<NewsRelatedArticleDto> relatedArticles
    ) {}
}
