package UMC.news.newsIntelligent.domain.news.service;

import UMC.news.newsIntelligent.domain.news.dto.NewsResponseDTO;
import UMC.news.newsIntelligent.global.apiPayload.exception.CustomException;

public interface NewsQueryService {

    NewsResponseDTO.NewsResDTO getRelatedNews(Long topicId, Long lastId, int size);

    NewsResponseDTO.TopicQualifiedListResDTO getLatestTopicNews(Long memberId);
}