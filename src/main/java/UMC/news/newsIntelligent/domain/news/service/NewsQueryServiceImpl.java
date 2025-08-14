package UMC.news.newsIntelligent.domain.news.service;

import UMC.news.newsIntelligent.domain.news.converter.NewsConverter;
import UMC.news.newsIntelligent.domain.news.dto.NewsResponseDTO;
//import UMC.news.newsIntelligent.domain.news.entity.News;
import UMC.news.newsIntelligent.domain.news.entity.News;
import UMC.news.newsIntelligent.domain.news.entity.PressLogo;
import UMC.news.newsIntelligent.domain.news.repository.NewsRepository;
import UMC.news.newsIntelligent.domain.news.repository.PressLogoRepository;
import UMC.news.newsIntelligent.domain.topic.entity.Topic;
import UMC.news.newsIntelligent.domain.topic.repository.TopicRepository;
import UMC.news.newsIntelligent.global.apiPayload.code.error.ErrorCode;
import UMC.news.newsIntelligent.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsQueryServiceImpl implements NewsQueryService{

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 20;
    private static final String DEFAULT_LOGO = null; // 기본 로고

    private final TopicRepository topicRepository;
    private final NewsRepository newsRepository;
    private final PressLogoRepository pressLogoRepository;

    // 연관 기사 목록
    @Override
    public NewsResponseDTO.NewsResDTO getRelatedNews(Long topicId, Long lastId, int size) {
        int pageSize = normalizeSize(size);

        Pageable pageable = PageRequest.of(0, pageSize + 1);
        List<News> fetched = newsRepository.findByTopicIdWithPaging(topicId, lastId, pageable);
        boolean hasNext = fetched.size() > pageSize;
        List<News> pageItems = hasNext ? fetched.subList(0, pageSize) : fetched;

        int totalCount = newsRepository.countByTopicId(topicId);

        Map<String, String> logoMap = loadPressLogos(pageItems);

        List<NewsResponseDTO.NewsListResDTO> content = pageItems.stream()
                .map(n -> NewsConverter.toListResDTO(n, logoFor(n.getPress(), logoMap)))
                .toList();

        Long newLastId = content.isEmpty() ? null : content.get(content.size() - 1).id();

        return new NewsResponseDTO.NewsResDTO(content, totalCount, newLastId, pageSize, hasNext);
    }

    // 최신 수정 보도 (조건 만족 + 같은 topic_id로 1개 이상인 토픽 1개)
    @Override
    public NewsResponseDTO.TopicQualifiedItemResDTO getLatestTopicNews() throws CustomException {
        Long qualifiedTopic = newsRepository.pickTopTopicIdByQualifiedNews();
        if (qualifiedTopic == null) {
            throw new CustomException(ErrorCode.TOPIC_NOT_FOUND);
        }

        Topic topic = topicRepository.findById(qualifiedTopic)
                .orElseThrow(() -> new CustomException(ErrorCode.TOPIC_NOT_FOUND));

        List<News> relatedNewsEntity = newsRepository.findTop3QualifiedNewsByTopicId(qualifiedTopic);
        if (relatedNewsEntity.isEmpty()) {
            throw new CustomException(ErrorCode.LATEST_NEWS_NOT_FOUND);
        }
        List<NewsResponseDTO.NewsRelatedArticleDto> relatedNews =
                NewsConverter.toRelatedDtos(relatedNewsEntity);

        return new NewsResponseDTO.TopicQualifiedItemResDTO(
                topic.getId(),
                topic.getTopicName(),
                topic.getAiSummary(),
                topic.getImageUrl(),
                topic.getSummaryTime(),
                relatedNews
        );
    }

    private static int normalizeSize(int size) {
        return (size < 1) ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);
    }

    private Map<String, String> loadPressLogos(List<News> newsList) {
        Set<String> presses = newsList.stream()
                .map(News::getPress)
                .map(NewsQueryServiceImpl::normPress)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (presses.isEmpty()) return Collections.emptyMap();

        return pressLogoRepository.findByPressIn(presses).stream()
                .collect(Collectors.toMap(
                        pl -> normPress(pl.getPress()),
                        PressLogo::getLogoImage,
                        (a, b) -> a
                ));
    }

    private String logoFor(String press, Map<String, String> logoMap) {
        String key = normPress(press);
        return (key == null) ? DEFAULT_LOGO : logoMap.getOrDefault(key, DEFAULT_LOGO);
    }

    // 한글 정규화
    private static String normPress(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;
        return Normalizer.normalize(t, Normalizer.Form.NFC);
    }
}