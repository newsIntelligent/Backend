package UMC.news.newsIntelligent.domain.news.service;

import UMC.news.newsIntelligent.domain.news.converter.NewsConverter;
import UMC.news.newsIntelligent.domain.news.entity.News;
import UMC.news.newsIntelligent.domain.news.dto.NewsResponseDTO;
import UMC.news.newsIntelligent.domain.news.entity.PressLogo;
import UMC.news.newsIntelligent.domain.news.repository.NewsRepository;
import UMC.news.newsIntelligent.domain.news.repository.PressLogoRepository;
import UMC.news.newsIntelligent.global.apiPayload.code.error.ErrorCode;
import UMC.news.newsIntelligent.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NewsQueryServiceImpl implements NewsQueryService{

    private final NewsRepository newsRepository;
    private final PressLogoRepository pressLogoRepository;

    @Override
    public NewsResponseDTO.NewsResDTO getRelatedNews(Long topicId, Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);

        List<News> newsList = newsRepository.findByTopicIdWithPaging(topicId, lastId, pageable);
        int totalCount = newsRepository.countByTopicId(topicId);

        // 이번 페이지 언론사만 수집
        var presses = newsList.stream()
                .map(News::getPress)
                .filter(p -> p != null && !p.isBlank())
                .collect(java.util.stream.Collectors.toSet());

        Map<String, String> logoMap = java.util.Collections.emptyMap();
        if (!presses.isEmpty()) {
            logoMap = pressLogoRepository.findByPressIn(presses).stream()
                    .collect(java.util.stream.Collectors.toMap(PressLogo::getPress, PressLogo::getLogoImage));
        }

        // 기본 로고
        String DEFAULT_LOGO = null;

        List<NewsResponseDTO.NewsListResDTO> content = NewsConverter.toListResDTOs(newsList, logoMap, DEFAULT_LOGO);

        Long newLastId = content.isEmpty() ? null : content.get(content.size() - 1).id();
        boolean hasNext = newsList.size() == size;

        return new NewsResponseDTO.NewsResDTO(content, totalCount, newLastId, size, hasNext);
    }

    // 최신 수정 보도 (조건 만족 + 같은 topic_id로 3개 이상 중 '기사 수가 가장많은' 토픽 1개)
    @Override
    public NewsResponseDTO.TopicQualifiedItemResDTO getLatestTopicNews() throws CustomException {
        List<News> latestNews = newsRepository.findArticlesOfTopTopicByCount();

        if (latestNews.isEmpty()) {
            throw new CustomException(ErrorCode.LATEST_NEWS_NOT_FOUND);
        }

        // 최신순 정렬
        latestNews.sort(
                Comparator.comparing(News::getPublishDate)
                        .thenComparing(News::getId)
                        .reversed()
        );

        Long topicId = latestNews.get(0).getTopic().getId();

        // 대표 기사: 정렬 결과의 첫 번째(= 가장 최신 기사)
        News main = latestNews.get(0);

        List<NewsResponseDTO.NewsRelatedArticleDto> related = NewsConverter.toRelatedDtos(latestNews);

        return new NewsResponseDTO.TopicQualifiedItemResDTO(
                topicId,
                main.getTitle(),
                main.getNewsSummary(),
                main.getPublishDate(),
                related
        );

//        // topic_id 단위로 그룹핑
//        Map<Long, List<News>> byTopic = latestNews.stream()
//                .collect(Collectors.groupingBy(n -> n.getTopic().getId(), LinkedHashMap::new, Collectors.toList()));
//
//        // 개수가 가장 많은 토픽 그룹 선택 (동률이면 첫 그룹)
//        Map.Entry<Long, List<News>> mostCountGroup = byTopic.entrySet().stream()
//                .max(Comparator.comparingInt(e -> e.getValue().size()))
//                .orElseThrow(() -> new CustomException(GeneralErrorCode.LATEST_NEWS_NOT_FOUND));
//
//        Long topicId = mostCountGroup.getKey();
//        List<News> items = mostCountGroup.getValue();
//
//        // 대표 기사: id가 가장 큰 기사
//        News main = items.stream()
//                .max(Comparator.comparing(News::getId))
//                .orElse(items.get(0));

    }
}
