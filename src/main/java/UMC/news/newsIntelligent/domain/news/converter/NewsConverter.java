package UMC.news.newsIntelligent.domain.news.converter;

import UMC.news.newsIntelligent.domain.news.dto.NewsResponseDTO;
import UMC.news.newsIntelligent.domain.news.entity.News;

import java.util.List;
import java.util.Map;

public final class NewsConverter {
    private NewsConverter() {}

    /** 단건 변환 */
    public static NewsResponseDTO.NewsListResDTO toListResDTO(News n, String pressLogoUrl) {
        return new NewsResponseDTO.NewsListResDTO(
                n.getId(),
                n.getTitle(),
                n.getNewsSummary(),
                n.getNewsLink(),
                n.getPublishDate(),
                n.getPress(),
                pressLogoUrl
        );
    }

    /** 배치 변환 (press -> logoUrl 맵을 주입 받아 사용) */
    public static List<NewsResponseDTO.NewsListResDTO> toListResDTOs(
            List<News> newsList,
            Map<String, String> pressLogoMap,
            String defaultLogoUrl
    ) {
        return newsList.stream()
                .map(n -> toListResDTO(n, pressLogoMap.getOrDefault(n.getPress(), defaultLogoUrl)))
                .toList();
    }

    // 연관 기사 DTO - 매핑
    public static NewsResponseDTO.NewsRelatedArticleDto toRelatedDto(News n) {
        return new NewsResponseDTO.NewsRelatedArticleDto(
                n.getId(),
                n.getPress(),
                n.getTitle(),
                n.getNewsSummary(),
                n.getNewsLink(),
                n.getPublishDate()
        );
    }

    public static List<NewsResponseDTO.NewsRelatedArticleDto> toRelatedDtos(List<News> news) {
        return news.stream().map(NewsConverter::toRelatedDto).toList();
    }
}