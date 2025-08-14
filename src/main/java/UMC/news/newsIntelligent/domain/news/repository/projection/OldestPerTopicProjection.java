package UMC.news.newsIntelligent.domain.news.repository.projection;

public interface OldestPerTopicProjection {
    Long getTopicId();
    String getPress();
    String getTitle();
}