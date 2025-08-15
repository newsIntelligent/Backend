package UMC.news.newsIntelligent.domain.news.repository.latestCorrection;

import UMC.news.newsIntelligent.domain.news.entity.latestCorrection.LatestCorrectionItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LatestCorrectionItemRepository extends JpaRepository<LatestCorrectionItem, Long> {
    boolean existsByNews_Id(Long newsId);

    boolean existsByLatestCorrection_IdAndNews_Id(Long latestCorrectionId, Long newsId);

}