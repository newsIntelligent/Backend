package UMC.news.newsIntelligent.domain.news.service;

import UMC.news.newsIntelligent.domain.news.entity.News;
import UMC.news.newsIntelligent.domain.news.entity.latestCorrection.LatestCorrection;
import UMC.news.newsIntelligent.domain.news.entity.latestCorrection.LatestCorrectionItem;
import UMC.news.newsIntelligent.domain.news.repository.NewsRepository;
import UMC.news.newsIntelligent.domain.news.repository.latestCorrection.LatestCorrectionItemRepository;
import UMC.news.newsIntelligent.domain.news.repository.latestCorrection.LatestCorrectionRepository;
import UMC.news.newsIntelligent.domain.topic.entity.Topic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LatestCorrectionService {

    private final NewsRepository newsRepository;
    private final LatestCorrectionRepository lcRepository;
    private final LatestCorrectionItemRepository itemRepository;

    @Transactional
    public List<LatestCorrection> record(String runKey) {
        // 이번 사이클에서 조건을 만족하지만 아직 기록되지 않은 뉴스 수집
        List<News> candidates = newsRepository.findAllQualifiedNotRecorded();
        if (candidates.isEmpty()) return List.of();

        // 토픽 기준으로 그룹핑
        Map<Topic, List<News>> byTopic = candidates.stream()
                .collect(Collectors.groupingBy(News::getTopic, LinkedHashMap::new, Collectors.toList()));

        List<LatestCorrection> out = new ArrayList<>();

        for (var entry : byTopic.entrySet()) {
            Topic topic = entry.getKey();
            List<News> newsList = entry.getValue();

            LatestCorrection lc = lcRepository.findByRunKeyAndTopic_Id(runKey, topic.getId())
                    .orElseGet(() -> lcRepository.save(LatestCorrection.of(runKey, topic)));

            // 각 뉴스에 대해 이미 기록되었으면 스킵, 아니면 추가
            for (News n : newsList) {
                if (itemRepository.existsByLatestCorrection_IdAndNews_Id(lc.getId(), n.getId())
                        || itemRepository.existsByNews_Id(n.getId())) {
                    continue;
                }
                LatestCorrectionItem item = LatestCorrectionItem.of(lc, n);
                itemRepository.save(item);
                lc.addItem(item);
            }
            out.add(lc);
        }
        return out;
    }
}