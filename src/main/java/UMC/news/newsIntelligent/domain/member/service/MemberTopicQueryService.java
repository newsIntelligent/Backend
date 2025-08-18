package UMC.news.newsIntelligent.domain.member.service;

import UMC.news.newsIntelligent.domain.member.dto.MemberTopicResponseDTO;
import UMC.news.newsIntelligent.domain.topic.entity.Topic;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Map;

public interface MemberTopicQueryService {

    MemberTopicResponseDTO.MemberTopicPreviewListResDTO searchReadTopics(String keyword, Long cursor, int size, Long memberId);
    MemberTopicResponseDTO.MemberTopicPreviewListResDTO getReadTopics(Long cursor, int size, Long memberId);
    MemberTopicResponseDTO.MemberTopicPreviewListResDTO getSubscriptionTopics(Long cursor, int size, Long memberId);
    Map<Long, Boolean> buildSubscribedMap(Long memberId, Slice<Topic> slice);
}
