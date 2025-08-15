package UMC.news.newsIntelligent.domain.news.repository;

import UMC.news.newsIntelligent.domain.news.entity.LatestCorrection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LatestCorrectionRepository extends JpaRepository<LatestCorrection, Long> {
    Optional<LatestCorrection> findByTopicId(Long topicId);

    @Query("select lc from LatestCorrection lc order by lc.createdAt desc")
    List<LatestCorrection> findRecent(Pageable pageable);
}