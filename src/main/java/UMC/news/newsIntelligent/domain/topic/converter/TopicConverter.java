package UMC.news.newsIntelligent.domain.topic.converter;

import UMC.news.newsIntelligent.domain.topic.dto.TopicResponseDTO;
import UMC.news.newsIntelligent.domain.topic.entity.Topic;

import java.util.List;
import java.util.Map;

public class TopicConverter {

    // 단건 매핑
    public static TopicResponseDTO.TopicPreviewResDTO toPreviewResDTO(
            Topic topic,
            TopicResponseDTO.ImageSource imageSource
    ) {
        return TopicResponseDTO.TopicPreviewResDTO.builder()
                .id(topic.getId())
                .topicName(topic.getTopicName())
                .aiSummary(topic.getAiSummary())
                .summaryTime(topic.getSummaryTime())
                .imageUrl(topic.getImageUrl())
                .imageSource(imageSource)
                .build();
    }

    // 리스트 매핑
    public static List<TopicResponseDTO.TopicPreviewResDTO> toPreviewResDTOList(
            List<Topic> topics,
            Map<Long, TopicResponseDTO.ImageSource> imageSourceMap
    ) {
        return topics.stream()
                .map(t -> toPreviewResDTO(t, imageSourceMap.get(t.getId())))
                .toList();
    }
}