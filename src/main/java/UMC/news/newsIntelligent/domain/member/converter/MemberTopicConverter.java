package UMC.news.newsIntelligent.domain.member.converter;

import UMC.news.newsIntelligent.domain.member.dto.MemberTopicResponseDTO;
import UMC.news.newsIntelligent.domain.topic.dto.TopicResponseDTO;
import UMC.news.newsIntelligent.domain.topic.entity.Topic;

import java.util.List;
import java.util.Map;

public class MemberTopicConverter {

    // 단건 매핑
    public static MemberTopicResponseDTO.MemberTopicPreviewResDTO toPreviewResDTO(
            Topic topic,
            TopicResponseDTO.ImageSource imageSource,
            boolean isSub
    ) {
        return MemberTopicResponseDTO.MemberTopicPreviewResDTO.builder()
                .id(topic.getId())
                .topicName(topic.getTopicName())
                .aiSummary(topic.getAiSummary())
                .summaryTime(topic.getSummaryTime())
                .imageUrl(topic.getImageUrl())
                .imageSource(imageSource)
                .isSub(isSub)
                .build();
    }

    // 리스트 매핑
    public static List<MemberTopicResponseDTO.MemberTopicPreviewResDTO> toPreviewResDTOList(
            List<Topic> topics,
            Map<Long, TopicResponseDTO.ImageSource> imageSourceMap,
            Map<Long, Boolean> subscribedMap
    ) {
        return topics.stream()
                .map(t -> toPreviewResDTO(
                        t,
                        imageSourceMap.get(t.getId()),
                        subscribedMap != null && subscribedMap.getOrDefault(t.getId(), false)
                ))
                .toList();
    }
}
