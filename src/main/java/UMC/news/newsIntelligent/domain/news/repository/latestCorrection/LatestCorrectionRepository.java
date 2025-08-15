package UMC.news.newsIntelligent.domain.news.repository.latestCorrection;

import UMC.news.newsIntelligent.domain.news.entity.latestCorrection.LatestCorrection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LatestCorrectionRepository extends JpaRepository<LatestCorrection, Long> {
    Optional<LatestCorrection> findByRunKeyAndTopic_Id(String runKey, Long topicId);

    @EntityGraph(attributePaths = {"topic", "items", "items.news"})
    List<LatestCorrection> findByRunKey(String runKey);

    // 토픽의 전체 뉴스수가 3개 이상인 최신수정보도만 최신순으로
    @Query("""
        SELECT lc
        FROM LatestCorrection lc
        WHERE (
          SELECT COUNT(n)
          FROM News n
          WHERE n.topic.id = lc.topic.id
        ) >= :minNews
        ORDER BY lc.id DESC
    """)
    List<LatestCorrection> findRecentWhoseTopicHasAtLeastNews(
            @Param("minNews") long minNews,
            Pageable pageable
    );
}