package UMC.news.newsIntelligent.domain.news.repository;

import UMC.news.newsIntelligent.domain.news.entity.News;
import UMC.news.newsIntelligent.domain.news.repository.projection.OldestPerTopicProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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

    //토픽은 1개 (is_new true, is_third false)를 만족하는 뉴스가 단 하나라도 있다면 선택
    @Query(value = """
        SELECT n.topic_id
        FROM news n
        WHERE n.is_new = 1
          AND n.is_third = 0
        GROUP BY n.topic_id
        ORDER BY COUNT(*) DESC, MAX(n.id) DESC
        LIMIT 1
    """, nativeQuery = true)
    Long pickTopTopicIdByQualifiedNews();

    /**
     * 토픽은 1개 (is_new true, is_third false)를 만족하는 뉴스가 단 하나라도 있다면 선택
     * 해당 토픽의 기사 3개를 반환하는데, 그중 하나는 (is_new true, is_third false)이 조건을 만족함
     * 나머지 2개는 조건 무관하게 최신순으로 리턴 기준 완화하려했으나,
     * sql구문이 매우 복잡해지고 잘못된 값을 불러서.. (is_new true, is_third false)를 만족하는 뉴스만큼 리턴함
     */
    @Query(value = """
        SELECT n.*
        FROM news n
        WHERE n.topic_id = :topicId
          AND n.is_new = 1
          AND n.is_third = 0
        ORDER BY n.publish_date DESC, n.id DESC
        LIMIT 3
    """, nativeQuery = true)
    List<News> findTop3QualifiedNewsByTopicId(@Param("topicId") Long topicId);

    @Query("""
    SELECT n.topic.id AS topicId, 
            n.press AS press,
            n.title AS title,
            n.newsLink AS newsLink 
    FROM News n
    WHERE n.topic.id IN :topicIds
      AND n.publishDate = (
          SELECT MIN(n2.publishDate)
          FROM News n2
          WHERE n2.topic.id = n.topic.id
      )
      AND n.id = (
          SELECT MAX(n3.id)
          FROM News n3
          WHERE n3.topic.id = n.topic.id
            AND n3.publishDate = n.publishDate
      )
    """)
    List<OldestPerTopicProjection> findOldestPerTopic(java.util.Collection<Long> topicIds);

    // is_new = true AND is_third = false 인 기사 중
    // 아직 어떤 run에서도 LatestCorrectionItem 에 기록되지 않은 것만
    @Query("""
        SELECT n
        FROM News n
        WHERE n.isNew = true AND n.isThird = false
          AND NOT EXISTS (
              SELECT 1 FROM LatestCorrectionItem i
              WHERE i.news.id = n.id
          )
    """)
    List<News> findAllQualifiedNotRecorded();

    // 같은 토픽에서 자격 뉴스들을 제외하고 최신순으로 기사 수집
    @Query("""
        SELECT n
        FROM News n
        WHERE n.topic.id = :topicId
          AND (:excludeIds IS NULL OR n.id NOT IN :excludeIds)
        ORDER BY n.publishDate DESC, n.id DESC
    """)
    List<News> findFillersByTopicExclude(
            @Param("topicId") Long topicId,
            @Param("excludeIds") java.util.Collection<Long> excludeIds,
            org.springframework.data.domain.Pageable pageable
    );

    // 특정 토픽 내에서 가장 오래된 publishDate, id가 가장 큰 기사
    Optional<News> findFirstByTopicIdOrderByPublishDateAscIdDesc(Long topicId);
}
//    @Query(value = """
//    SELECT n.*
//    FROM news n
//    JOIN (
//        SELECT topic_id
//        FROM news
//        WHERE is_new = 1
//          AND is_third = 0
//        GROUP BY topic_id
//        HAVING COUNT(*) >= 3
//        ORDER BY COUNT(*) DESC, MAX(id) DESC   -- 동률이면 id 큰 토픽 우선
//        LIMIT 1
//    ) t ON t.topic_id = n.topic_id
//    WHERE n.is_new = 1
//      AND n.is_third = 0
//    """, nativeQuery = true)
//    List<News> findArticlesOfTopTopicByCount();

