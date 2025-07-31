package UMC.news.newsIntelligent.domain.member.service;

import UMC.news.newsIntelligent.domain.member.dto.MemberTopicResponseDTO;

public interface MemberTopicQueryService {

    MemberTopicResponseDTO.MemberTopicPreviewListResDTO searchReadTopics(String keyword, Long cursor, int size, Long memberId);
    MemberTopicResponseDTO.MemberTopicPreviewListResDTO getReadTopics(Long cursor, int size, Long memberId);
}
