package UMC.news.newsIntelligent.domain.news.repository.latestCorrection;

import UMC.news.newsIntelligent.domain.news.entity.News;
import UMC.news.newsIntelligent.domain.news.entity.latestCorrection.LatestCorrectionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LatestCorrectionItemRepository extends JpaRepository<LatestCorrectionItem, Long> {
    boolean existsByNews_Id(Long newsId);

    boolean existsByLatestCorrection_IdAndNews_Id(Long latestCorrectionId, Long newsId);

    // 특정 최신수정보도의 자격 뉴스들을 최신순으로 수집
    @Query("""
        SELECT i.news
        FROM LatestCorrectionItem i
        WHERE i.latestCorrection.id = :lcId
        ORDER BY i.news.publishDate DESC, i.news.id DESC
    """)
    List<News> findQualifiedNewsByLatestCorrectionId(
            @Param("lcId") Long lcId,
            org.springframework.data.domain.Pageable pageable
    );
}