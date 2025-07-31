package UMC.news.newsIntelligent.domain.member.converter;

import UMC.news.newsIntelligent.domain.member.dto.MemberTopicResponseDTO;
import UMC.news.newsIntelligent.domain.topic.entity.Topic;

public class MemberTopicConverter {

    public static MemberTopicResponseDTO.MemberTopicPreviewResDTO toPreviewResDTO(Topic topic) {

        return MemberTopicResponseDTO.MemberTopicPreviewResDTO.builder()
                .id(topic.getId())
                .topicName(topic.getTopicName())
                .aiSummary(topic.getAiSummary())
                .imageUrl(topic.getImageUrl())
                .summaryTime(topic.getSummaryTime())
                .build();
    }

}
