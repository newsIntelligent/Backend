package UMC.news.newsIntelligent.domain.news;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

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
