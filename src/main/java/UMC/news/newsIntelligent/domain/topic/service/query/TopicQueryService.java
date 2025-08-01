package UMC.news.newsIntelligent.domain.topic.service.query;

import UMC.news.newsIntelligent.domain.topic.dto.TopicResponseDTO;
import UMC.news.newsIntelligent.domain.topic.entity.Topic;

public interface TopicQueryService {
    TopicResponseDTO.TopicPreviewListResDTO searchTopics(String keyword, Long cursor, int size);

    TopicResponseDTO.TopicDetailsResDTO getTopicById(Long topicId);
}
