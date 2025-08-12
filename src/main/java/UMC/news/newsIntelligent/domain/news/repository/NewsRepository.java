package UMC.news.newsIntelligent.domain.news.repository;

import UMC.news.newsIntelligent.domain.news.entity.News;
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

//    @Query(value = """
//        SELECT *
//        FROM news n
//        WHERE n.is_new = 1
//          AND n.is_third = 0
//          AND n.topic_id IN (
//            SELECT topic_id
//            FROM news
//            WHERE is_new = 1 AND is_third = 0
//            GROUP BY topic_id
//            HAVING COUNT(*) >= 3
//          )
//        ORDER BY n.topic_id, n.publish_date
//        """, nativeQuery = true)
//    List<News> findLatestNews();

    @Query(value = """
    SELECT n.*
    FROM news n
    JOIN (
        SELECT topic_id
        FROM news
        WHERE is_new = 1
          AND is_third = 0
        GROUP BY topic_id
        HAVING COUNT(*) >= 3
        ORDER BY COUNT(*) DESC, MAX(id) DESC   -- 동률이면 id 큰 토픽 우선
        LIMIT 1
    ) t ON t.topic_id = n.topic_id
    WHERE n.is_new = 1
      AND n.is_third = 0
    """, nativeQuery = true)
    List<News> findArticlesOfTopTopicByCount();
}
