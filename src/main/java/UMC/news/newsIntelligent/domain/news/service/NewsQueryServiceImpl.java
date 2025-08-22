package UMC.news.newsIntelligent.domain.news.service;

import UMC.news.newsIntelligent.domain.member.repository.MemberTopicRepository;
import UMC.news.newsIntelligent.domain.news.converter.NewsConverter;
import UMC.news.newsIntelligent.domain.news.dto.NewsResponseDTO;
//import UMC.news.newsIntelligent.domain.news.entity.News;
import UMC.news.newsIntelligent.domain.news.entity.News;
import UMC.news.newsIntelligent.domain.news.entity.PressLogo;
import UMC.news.newsIntelligent.domain.news.repository.NewsRepository;
import UMC.news.newsIntelligent.domain.news.repository.PressLogoRepository;
import UMC.news.newsIntelligent.domain.news.repository.latestCorrection.LatestCorrectionItemRepository;
import UMC.news.newsIntelligent.domain.news.repository.latestCorrection.LatestCorrectionRepository;
import UMC.news.newsIntelligent.domain.topic.entity.Topic;
import UMC.news.newsIntelligent.domain.topic.repository.TopicRepository;
import UMC.news.newsIntelligent.global.apiPayload.code.error.ErrorCode;
import UMC.news.newsIntelligent.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsQueryServiceImpl implements NewsQueryService{

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 20;
    private static final String DEFAULT_LOGO = null; // 기본 로고

    private final NewsRepository newsRepository;
    private final PressLogoRepository pressLogoRepository;
    private final LatestCorrectionRepository latestCorrectionRepository;
    private final LatestCorrectionItemRepository lcItemRepository;
    private final MemberTopicRepository memberTopicRepository;

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

        List<NewsResponseDTO.NewsListResDTO> content =
                NewsConverter.toListResDTOs(pageItems, press -> logoFor(press, logoMap));

        Long newLastId = content.isEmpty() ? null : content.get(content.size() - 1).id();

        return new NewsResponseDTO.NewsResDTO(content, totalCount, newLastId, pageSize, hasNext);
    }

    @Override
    @Transactional(readOnly = true)
    public NewsResponseDTO.TopicQualifiedListResDTO getLatestTopicNews(Long memberId) {
        // 최신수정보도 중 "토픽 기사 수 ≥ 3"인 것 4건
        var top3Page = org.springframework.data.domain.PageRequest.of(0, 4);
        var lcs = latestCorrectionRepository.findRecentWhoseTopicHasAtLeastNews(3, top3Page);
        if (lcs.isEmpty()) {
            throw new CustomException(ErrorCode.LATEST_NEWS_NOT_FOUND);
        }

        List<NewsResponseDTO.TopicQualifiedItemResDTO> items = new java.util.ArrayList<>();

        for (var lc : lcs) {
            var topic   = lc.getTopic();
            Long topicId = topic.getId();

            // 자격 뉴스(이번 run에서 잡힌 것들) 최신순으로 최대 3개
            var three = org.springframework.data.domain.PageRequest.of(0, 3);
            List<News> qualified = lcItemRepository
                    .findQualifiedNewsByLatestCorrectionId(lc.getId(), three);

            // 부족하면 같은 토픽의 다른 최신 뉴스로 채우기 (자격뉴스 제외)
            List<Long> excludeIds = qualified.stream().map(News::getId).toList();
            if (qualified.size() < 3) {
                int need = 3 - qualified.size();
                List<News> fillers = newsRepository
                        .findFillersByTopicExclude(
                                topicId,
                                excludeIds.isEmpty() ? null : excludeIds,
                                org.springframework.data.domain.PageRequest.of(0, need)
                        );
                if (!fillers.isEmpty()) {
                    qualified = new java.util.ArrayList<>(qualified);
                    qualified.addAll(fillers);
                }
            }

            // 최대 3개만 사용하여 DTO 변환
            List<News> top3 = (qualified.size() > 3) ? qualified.subList(0, 3) : qualified;
            List<NewsResponseDTO.NewsRelatedArticleDto> related =
                    NewsConverter.toRelatedDtos(top3);

            // 출처 기사 (가장 오래된 기사 + id DESC)
            var sourceOpt = newsRepository.findFirstByTopicIdOrderByPublishDateAscIdAsc(topicId);
            NewsResponseDTO.ImageSource imageSource = sourceOpt
                    .map(n -> NewsResponseDTO.ImageSource.builder()
                            .press(n.getPress())
                            .title(n.getTitle())
                            .build())
                    .orElse(null);

            boolean isSub = false;
            if (memberId != null) {
                isSub = memberTopicRepository
                        .existsByMemberIdAndTopicIdAndIsSubscribeTrue(memberId, topicId);
            }

            // 단건 아이템 DTO
            var item = NewsConverter.toTopicQualifiedItem(topic, imageSource, isSub, related);
            items.add(item);
        }

        // 3건 묶어서 반환
        return NewsResponseDTO.TopicQualifiedListResDTO.builder()
                .items(items)
                .build();
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