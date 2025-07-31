package UMC.news.newsIntelligent.domain.topic.service.query;

import UMC.news.newsIntelligent.domain.topic.dto.TopicResponseDTO;

public interface TopicQueryService {
    TopicResponseDTO.TopicPreviewListResDTO searchTopics(String keyword, Long cursor, int size);
}
