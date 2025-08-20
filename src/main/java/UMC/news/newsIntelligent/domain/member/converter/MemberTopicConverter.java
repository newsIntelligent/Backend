package UMC.news.newsIntelligent.domain.member.converter;

import UMC.news.newsIntelligent.domain.member.dto.MemberTopicResponseDTO;
import UMC.news.newsIntelligent.domain.member.entity.MemberTopic;
import UMC.news.newsIntelligent.domain.topic.dto.TopicResponseDTO;
import UMC.news.newsIntelligent.domain.topic.entity.Topic;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Map;

public class MemberTopicConverter {

    public static MemberTopicResponseDTO.MemberTopicPreviewResDTO toPreviewResDTO(
            MemberTopic mt, TopicResponseDTO.ImageSource imageSource
    ) {
        Topic t = mt.getTopic();
        return MemberTopicResponseDTO.MemberTopicPreviewResDTO.builder()
                .id(t.getId())
                .topicName(t.getTopicName())
                .aiSummary(t.getAiSummary())
                .summaryTime(t.getSummaryTime())
                .imageUrl(t.getImageUrl())
                .imageSource(imageSource)
                .isSub(Boolean.TRUE.equals(mt.getIsSubscribe()))
                .build();
    }

    public static MemberTopicResponseDTO.MemberTopicPreviewResDTO toPreviewResDTO(
            Topic t, TopicResponseDTO.ImageSource imageSource, boolean isSub
    ) {
        return MemberTopicResponseDTO.MemberTopicPreviewResDTO.builder()
                .id(t.getId())
                .topicName(t.getTopicName())
                .aiSummary(t.getAiSummary())
                .summaryTime(t.getSummaryTime())
                .imageUrl(t.getImageUrl())
                .imageSource(imageSource)
                .isSub(isSub)
                .build();
    }

    private static List<MemberTopicResponseDTO.MemberTopicPreviewResDTO> mapFromMemberTopic(
            List<MemberTopic> list, Map<Long, TopicResponseDTO.ImageSource> srcMap
    ) {
        return list.stream()
                .map(mt -> {
                    Long tid = mt.getTopic().getId();
                    return toPreviewResDTO(mt, srcMap.get(tid));
                })
                .toList();
    }

    private static List<MemberTopicResponseDTO.MemberTopicPreviewResDTO> mapFromTopic(
            List<Topic> list,
            Map<Long, TopicResponseDTO.ImageSource> srcMap,
            Map<Long, Boolean> subMap
    ) {
        return list.stream()
                .map(t -> toPreviewResDTO(
                        t,
                        srcMap.get(t.getId()),
                        subMap != null && subMap.getOrDefault(t.getId(), false)
                ))
                .toList();
    }

    public static MemberTopicResponseDTO.MemberTopicPreviewListResDTO toPreviewList(
            Slice<MemberTopic> slice,
            Map<Long, TopicResponseDTO.ImageSource> srcMap
    ) {
        var topics = mapFromMemberTopic(slice.getContent(), srcMap);

        Long nextCursor = (slice.hasNext() && !topics.isEmpty())
                ? topics.get(topics.size() - 1).id()
                : null;

        return MemberTopicResponseDTO.MemberTopicPreviewListResDTO.builder()
                .cursor(nextCursor)
                .hasNext(slice.hasNext())
                .topics(topics)
                .build();
    }

    public static MemberTopicResponseDTO.MemberTopicPreviewListResDTO toPreviewList(
            Slice<Topic> slice,
            Map<Long, TopicResponseDTO.ImageSource> srcMap,
            Map<Long, Boolean> subMap
    ) {
        var topics = mapFromTopic(slice.getContent(), srcMap, subMap);

        Long nextCursor = (slice.hasNext() && !topics.isEmpty())
                ? topics.get(topics.size() - 1).id()
                : null;

        return MemberTopicResponseDTO.MemberTopicPreviewListResDTO.builder()
                .cursor(nextCursor)
                .hasNext(slice.hasNext())
                .topics(topics)
                .build();
    }
}