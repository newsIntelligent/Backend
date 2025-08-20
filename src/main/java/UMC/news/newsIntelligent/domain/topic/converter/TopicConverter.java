package UMC.news.newsIntelligent.domain.topic.converter;

import UMC.news.newsIntelligent.domain.topic.dto.TopicResponseDTO;
import UMC.news.newsIntelligent.domain.topic.entity.Topic;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Map;

public class TopicConverter {

    // 단건 매핑
    public static TopicResponseDTO.TopicPreviewResDTO toPreviewResDTO(
            Topic topic,
            TopicResponseDTO.ImageSource imageSource,
            boolean isSub
    ) {
        return TopicResponseDTO.TopicPreviewResDTO.builder()
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
    public static List<TopicResponseDTO.TopicPreviewResDTO> toPreviewResDTOList(
            List<Topic> topics,
            Map<Long, TopicResponseDTO.ImageSource> imageSourceMap,
            Map<Long, Boolean> subscribedMap
    ) {
        return topics.stream()
                .map(t -> toPreviewResDTO(
                        t,
                        imageSourceMap.get(t.getId()),
                        (subscribedMap != null) && subscribedMap.getOrDefault(t.getId(), false)
                ))
                .toList();
    }

    public static TopicResponseDTO.TopicPreviewListResDTO mapSliceToPreviewListDTO(
            Slice<Topic> slice,
            Map<Long, TopicResponseDTO.ImageSource> srcMap,
            Map<Long, Boolean> subMap
    ) {
        List<TopicResponseDTO.TopicPreviewResDTO> topics =
                TopicConverter.toPreviewResDTOList(slice.getContent(), srcMap, subMap);

        Long nextCursor = (slice.hasNext() && !topics.isEmpty())
                ? topics.get(topics.size() - 1).id()
                : null;

        return TopicResponseDTO.TopicPreviewListResDTO.builder()
                .cursor(nextCursor)
                .hasNext(slice.hasNext())
                .topics(topics)
                .build();
    }
}