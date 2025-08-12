package UMC.news.newsIntelligent.domain.news.service;

import UMC.news.newsIntelligent.domain.news.entity.News;
import UMC.news.newsIntelligent.domain.news.dto.NewsResponseDTO;
import UMC.news.newsIntelligent.domain.news.repository.NewsRepository;
import UMC.news.newsIntelligent.global.apiPayload.code.error.ErrorCode;
import UMC.news.newsIntelligent.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsQueryServiceImpl implements NewsQueryService{

    private final NewsRepository newsRepository;

    @Override
    public NewsResponseDTO.NewsResDTO getRelatedNews(Long topicId, Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);

        List<News> newsList = newsRepository.findByTopicIdWithPaging(topicId, lastId, pageable);
        int totalCount = newsRepository.countByTopicId(topicId);

        List<NewsResponseDTO.NewsListResDTO> content = newsList.stream()
                .map(n -> new NewsResponseDTO.NewsListResDTO(
                        n.getId(),
                        n.getTitle(),
                        n.getNewsSummary(),
                        n.getNewsLink(),
                        n.getPublishDate(),
                        n.getPress()
                ))
                .toList();

        Long newLastId = content.isEmpty() ? null : content.get(content.size() - 1).id();
        boolean hasNext = newsList.size() == size;

        return new NewsResponseDTO.NewsResDTO(content, totalCount, newLastId, size, hasNext);
    }


    // 최신 수정 보도 (조건 만족 + 같은 topic_id로 3개 이상 중 '기사 수가 가장많은' 토픽 1개)
    @Override
    public NewsResponseDTO.TopicQualifiedItemResDTO getLatestTopicNews() throws CustomException {
        List<News> latestNews = newsRepository.findArticlesOfTopTopicByCount();

        if(latestNews.isEmpty()){
            throw new CustomException(ErrorCode.LATEST_NEWS_NOT_FOUND);
        }

        // 최신순 정렬
        latestNews.sort(
                Comparator.comparing(News::getPublishDate)
                        .thenComparing(News::getId)
                        .reversed()
        );

        Long topicId = latestNews.get(0).getTopic().getId();

        // 대표 기사: 가장 최신(동일시간일 시 id가 큰 것으로)
        News main = latestNews.stream()
                .max(Comparator.comparing(News::getId))
                .orElse(latestNews.get(0));

        List<NewsResponseDTO.NewsRelatedArticleDto> related = latestNews.stream()
                .map(n -> new NewsResponseDTO.NewsRelatedArticleDto(
                        n.getId(),
                        n.getPress(),
                        n.getTitle(),
                        n.getNewsSummary(),
                        n.getNewsLink(),
                        n.getPublishDate()
                ))
                .toList();

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
