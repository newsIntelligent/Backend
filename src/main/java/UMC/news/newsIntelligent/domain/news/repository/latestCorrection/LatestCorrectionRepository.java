package UMC.news.newsIntelligent.domain.news.repository.latestCorrection;

import UMC.news.newsIntelligent.domain.news.entity.latestCorrection.LatestCorrection;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LatestCorrectionRepository extends JpaRepository<LatestCorrection, Long> {
    Optional<LatestCorrection> findByRunKeyAndTopic_Id(String runKey, Long topicId);

    @EntityGraph(attributePaths = {"topic", "items", "items.news"})
    List<LatestCorrection> findByRunKey(String runKey);
}