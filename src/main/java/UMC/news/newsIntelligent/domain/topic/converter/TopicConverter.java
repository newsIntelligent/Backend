package UMC.news.newsIntelligent.domain.topic.converter;

import UMC.news.newsIntelligent.domain.topic.Topic;
import UMC.news.newsIntelligent.domain.topic.dto.TopicResponseDTO;

public class TopicConverter {

    public static TopicResponseDTO.TopicPreviewResDTO toPreviewResDTO(Topic topic) {
        return TopicResponseDTO.TopicPreviewResDTO.builder()
                .id(topic.getId())
                .topicName(topic.getTopicName())
                .aiSummary(topic.getAiSummary())
                .imageUrl(topic.getImageUrl())
                .summaryTime(topic.getSummaryTime())
                .build();
    }
}
