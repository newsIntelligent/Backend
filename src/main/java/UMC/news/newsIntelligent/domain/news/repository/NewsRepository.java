package UMC.news.newsIntelligent.domain.news.repository;

import UMC.news.newsIntelligent.domain.news.News;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {

    @Query("""
        SELECT n FROM News n
        WHERE n.topic.id = :topicId
          AND (:lastId IS NULL OR n.id < :lastId)
        ORDER BY n.id DESC
    """)
    List<News> findByTopicIdWithPaging(
            @Param("topicId") Long topicId,
            @Param("lastId") Long lastId,
            Pageable pageable
    );

    int countByTopicId(Long topicId);
}
