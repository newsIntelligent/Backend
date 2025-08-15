package UMC.news.newsIntelligent.domain.news.converter;

import UMC.news.newsIntelligent.domain.news.dto.NewsResponseDTO;
import UMC.news.newsIntelligent.domain.news.entity.News;
import UMC.news.newsIntelligent.domain.topic.dto.TopicResponseDTO;
import UMC.news.newsIntelligent.domain.topic.entity.Topic;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class NewsConverter {
    private NewsConverter() {}

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

    // 리스트 매핑
    public static List<NewsResponseDTO.NewsListResDTO> toListResDTOs(
            List<News> items,
            Function<String, String> logoResolver
    ) {
        return items.stream()
                .map(n -> toListResDTO(n, logoResolver.apply(n.getPress())))
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

    public static NewsResponseDTO.TopicQualifiedItemResDTO toTopicQualifiedItem(
            Topic topic,
            NewsResponseDTO.ImageSource imageSource,
            List<NewsResponseDTO.NewsRelatedArticleDto> related
    ) {
        return NewsResponseDTO.TopicQualifiedItemResDTO.builder()
                .id(topic.getId())
                .topicName(topic.getTopicName())
                .aiSummary(topic.getAiSummary())
                .imageUrl(topic.getImageUrl())
                .summaryTime(topic.getSummaryTime())
                .imageSource(imageSource)
                .relatedArticles(related)
                .build();
    }
}