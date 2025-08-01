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
}
